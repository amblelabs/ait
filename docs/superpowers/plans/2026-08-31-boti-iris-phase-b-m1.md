# BOTI Phase B — Milestone 1 (Feasibility Gate) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Prove (or disprove) that we can render the shadow world through its own `WorldRenderer` nested inside the main world render, under Iris's pipeline swapped to the exterior dimension, without corrupting the outer render — and that the interior comes out shaded by the shaderpack.

**Architecture:** Under an active shaderpack, at a live-pipeline hook (`WorldRenderEvents.AFTER_TRANSLUCENT`), for ONE doorway: save the outer Iris pipeline + client/GL state, swap `client.world`/camera/projection to the shadow world and `preparePipeline(exteriorDim)`, call the shadow `WorldRenderer.render(...)` **unclipped**, then restore everything. Success is judged in-game.

**Tech Stack:** Java 21, Minecraft 1.20.1, Fabric, Iris (`modCompileOnly`, guarded by `hasIris()`+`isShaderPackInUse()`), blaze3d/OpenGL.

**Spec:** `docs/superpowers/specs/2026-08-31-boti-iris-phase-b-design.md`

## Global Constraints

- **Build/JDK:** compile with `nix develop --command ./gradlew compileJava` (JDK 21; system Java is 17). Never plain `./gradlew`.
- **No unit tests exist** for this GL/Iris code; verification is compile success + in-game observation. Do not fabricate JUnit tests.
- **Iris is `modCompileOnly` only** — never a runtime/required dependency. Every Iris reference is guarded by `DependencyChecker.hasIris()` and, for rendering, `IrisApi.getInstance().isShaderPackInUse()`. Non-Iris and Iris-without-pack must behave exactly as today (Phase A).
- **M1 is a feasibility gate.** It is deliberately crude: ONE doorway, unclipped, rough camera. Do NOT build clipping, camera parallax, perf gating, or config here — those are M2–M5.
- **Commits may need the user** — the SSH signing agent has been refusing non-interactively in this environment; if `git commit` fails on signing, leave the change staged and report it.
- **Render thread only.** Never leak GL/client state across the hook boundary — every save has a matching restore.

---

## File Structure

- `build.gradle` — add a Maven repo + `modCompileOnly` for Iris (and Sodium, which Iris's referenced types depend on at compile time).
- `src/main/java/dev/amble/ait/client/boti/iris/IrisNestedRenderProbe.java` (new) — the whole M1 probe: guarded entry point, pipeline + state save/restore, the nested `WorldRenderer.render(...)` call. Isolated in its own `iris` subpackage so it can be deleted/rewritten wholesale for M2+ without touching Phase A code.
- `src/main/java/dev/amble/ait/client/AITModClient.java` — register the probe on `WorldRenderEvents.AFTER_TRANSLUCENT` under a `hasIris()` guard, in addition to (not replacing) the existing Phase A `END` registration.
- `src/main/java/dev/loqor/portal/client/PortalData.java` — (only if needed) expose the shadow `WorldRenderer`/`ClientWorld` to the probe; `renderer()` and `world()` record accessors already exist.

---

## Task 1: Add Iris (and Sodium) as compile-only dependencies

**Files:**
- Modify: `build.gradle`
- Modify: `src/main/java/dev/amble/ait/compat/DependencyChecker.java` (add a typed `isShaderPackInUse()` passthrough so the rest of the code has one guarded entry point)

**Interfaces:**
- Produces: `DependencyChecker.isIrisShaderPackInUse()` → `boolean` (false when Iris absent or no pack).

- [ ] **Step 1: Add the Modrinth maven repo and compile-only deps to `build.gradle`**

In the `repositories { }` block add:
```groovy
maven { url "https://api.modrinth.com/maven" } // Iris + Sodium (compile-only, for Phase B Iris integration)
```
In `dependencies { }` add (Iris references Sodium types in method signatures, so both are needed on the compile classpath):
```groovy
modCompileOnly "maven.modrinth:iris:1.7.0+1.20.1"
modCompileOnly "maven.modrinth:sodium:mc1.20.1-0.5.8"
```

- [ ] **Step 2: Resolve the exact versions**

Run: `nix develop --command ./gradlew compileJava`
If Gradle reports the version is not found, it lists nearby versions; pick a real Iris `*+1.20.1` and matching Sodium `mc1.20.1-*` and update the two lines. Iterate until dependencies resolve (a resolution failure looks like `Could not find maven.modrinth:iris:...`). It is fine if compile then fails only on the not-yet-written probe class.

- [ ] **Step 3: Add the guarded shaderpack check to `DependencyChecker`**

```java
public static boolean isIrisShaderPackInUse() {
    if (!HAS_IRIS) return false;
    try {
        return net.irisshaders.iris.api.v0.IrisApi.getInstance().isShaderPackInUse();
    } catch (Throwable t) {
        return false; // Iris internals moved / not initialised yet
    }
}
```

- [ ] **Step 4: Compile**

Run: `nix develop --command ./gradlew compileJava`
Expected: BUILD SUCCESSFUL, with `net.irisshaders.iris.api.v0.IrisApi` resolving.

- [ ] **Step 5: Commit** (if signing fails, stage and report)

```bash
git add build.gradle src/main/java/dev/amble/ait/compat/DependencyChecker.java
git commit -m "build(boti): add Iris/Sodium modCompileOnly + isShaderPackInUse guard (Phase B M1)"
```

---

## Task 2: The nested-render probe (save/restore + shadow render, unclipped)

**Files:**
- Create: `src/main/java/dev/amble/ait/client/boti/iris/IrisNestedRenderProbe.java`
- Modify: `src/main/java/dev/amble/ait/client/AITModClient.java` (register on `AFTER_TRANSLUCENT`)

**Interfaces:**
- Consumes: `DependencyChecker.isIrisShaderPackInUse()` (Task 1); `PortalDataManager.get(UUID)` → `PortalData`; `PortalData.renderer()` → `net.minecraft.client.render.WorldRenderer`, `PortalData.world()` → `ClientWorld`; `ClientTardisUtil.getCurrentTardis()` for a target UUID.
- Produces: `IrisNestedRenderProbe.run(WorldRenderContext)` — the `AFTER_TRANSLUCENT` callback.

- [ ] **Step 1: Create the probe skeleton with the guard and target selection**

```java
package dev.amble.ait.client.boti.iris;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import org.joml.Matrix4f;

import dev.amble.ait.AITMod;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.loqor.portal.client.PortalData;
import dev.loqor.portal.client.PortalDataManager;

import net.irisshaders.iris.Iris;
import net.irisshaders.iris.pipeline.PipelineManager;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;

/** THROWAWAY-GRADE Phase B M1 feasibility probe: render ONE shadow world nested under a swapped Iris pipeline,
 *  unclipped, and confirm the outer render survives + the interior is shaded. Rewritten wholesale for M2+. */
public final class IrisNestedRenderProbe {
    private static boolean loggedOnce = false;

    private IrisNestedRenderProbe() {}

    public static void run(WorldRenderContext ctx) {
        if (!DependencyChecker.isIrisShaderPackInUse()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        ClientTardis tardis = ClientTardisUtil.getCurrentTardis();
        if (tardis == null) return; // M1: only when the player is in/near a TARDIS we have data for

        PortalData data = PortalDataManager.get(tardis.getUuid());
        if (data == null || data.world() == null || data.renderer() == null) return;

        try {
            nestedRender(client, data, ctx);
        } catch (Throwable t) {
            if (!loggedOnce) {
                AITMod.LOGGER.error("Phase B M1: nested render threw (feasibility probe)", t);
                loggedOnce = true;
            }
        }
    }
    // nestedRender added in the next steps
}
```

- [ ] **Step 2: Add pipeline save/restore around the nested render**

Add this method. It captures the shared `PipelineManager` current pipeline before the nested render and re-prepares the main dimension's pipeline afterward, so the outer render regains a live pipeline (the make-or-break behaviour this milestone tests):

```java
    private static void nestedRender(MinecraftClient client, PortalData data, WorldRenderContext ctx) {
        PipelineManager pm = Iris.getPipelineManager();
        WorldRenderingPipeline outerPipeline = pm.getPipelineNullable();

        ClientWorld outerWorld = client.world;
        float tickDelta = ctx.tickDelta();

        try {
            // Swap the client to the shadow world so Iris.getCurrentDimension() resolves to the exterior dimension
            // and the nested renderLevel prepares that dimension's pipeline.
            client.world = data.world();

            WorldRenderer shadowRenderer = data.renderer();
            Camera shadowCamera = new Camera();
            // M1 rough camera: sit at the shadow world's spawn-ish origin looking north. Parallax/portal transform
            // is M3; M1 only asks "does it render shaded and not corrupt the outer render".
            shadowCamera.update(data.world(), client.player, false, false, tickDelta);

            GameRenderer gameRenderer = client.gameRenderer;
            LightmapTextureManager lightmap = gameRenderer.getLightmapTextureManager();
            Matrix4f projection = new Matrix4f(net.minecraft.client.render.RenderSystem.getProjectionMatrix());
            MatrixStack matrices = new MatrixStack();

            shadowRenderer.render(matrices, tickDelta, 0L, false, shadowCamera, gameRenderer, lightmap, projection);
        } finally {
            client.world = outerWorld;
            // Restore the outer pipeline reference so the rest of the outer frame has a live pipeline again.
            if (outerWorld != null)
                pm.preparePipeline(Iris.getCurrentDimension());
            if (!loggedOnce) {
                AITMod.LOGGER.info("Phase B M1: nested render completed; outerPipeline={} restoredTo={}",
                        outerPipeline, pm.getPipelineNullable());
                loggedOnce = true;
            }
        }
    }
```

Note for the implementer: `Camera.update(...)` and `WorldRenderer.render(...)` yarn signatures are for 1.20.1; if a parameter type/name differs in this mappings build, adjust to the actual yarn signature (the intent — a full nested world render with a valid camera — is what matters). If `client.player` is null-guarded away, pass the real player; the camera just needs a position in the shadow world.

- [ ] **Step 3: Register the probe on `AFTER_TRANSLUCENT` under the Iris guard**

In `AITModClient.onInitializeClient`, inside the existing `if (DependencyChecker.hasIris()) { ... }` block (which currently registers the Phase A variants on `END`), ADD (do not remove the `END` registrations):
```java
            // Phase B M1 feasibility probe (guarded again at call time by isShaderPackInUse()). Runs during the
            // world pass where Iris's pipeline is live. Additive to the Phase A END path.
            WorldRenderEvents.AFTER_TRANSLUCENT.register(dev.amble.ait.client.boti.iris.IrisNestedRenderProbe::run);
```

- [ ] **Step 4: Compile**

Run: `nix develop --command ./gradlew compileJava`
Expected: BUILD SUCCESSFUL. If a yarn signature mismatch appears (`Camera.update`, `WorldRenderer.render`), fix to the actual 1.20.1 signature and recompile.

- [ ] **Step 5: In-game feasibility read (THE GATE)**

Launch with Iris + a shaderpack (BSL/Complementary), stand in/near a TARDIS so `PortalData` exists. Observe and record:
- **Outer world integrity:** does the normal world still render correctly after the probe runs (no black screen, no frozen/duplicated frame, no crash)? Read the `Phase B M1: nested render completed; outerPipeline=… restoredTo=…` log — `restoredTo` should be non-null.
- **Interior visibility + shading:** does any shadow-world geometry appear on screen (it will be unclipped/fullscreen-ish and mis-positioned — that's expected for M1), and is it **shaded by the pack** (shadows, coloured lighting) rather than flat?
- **Crash/GL errors:** capture any exception from the `Phase B M1: nested render threw` log.

- [ ] **Step 6: Record the verdict in the ledger / PR notes**

- **Outer survives AND interior shaded** → M1 PASS: the approach is feasible; proceed to plan M2 (doorway clip).
- **Outer corrupted (black/frozen/crash) even with `restoredTo` non-null** → M1 investigation: the shared-pipeline restore is insufficient; try also restoring per-`WorldRenderer` state or an Iris version with IP-compat pipeline save/restore. If no restore path works from outside Iris, **M1 FAIL** → the approach is infeasible on this Iris version; Phase A remains the ceiling (report to the user, stop).
- **Interior renders but unshaded** → the nested render isn't going through Iris's pipeline; revisit dimension/pipeline swap.

- [ ] **Step 7: Commit** (if signing fails, stage and report)

```bash
git add src/main/java/dev/amble/ait/client/boti/iris/IrisNestedRenderProbe.java \
        src/main/java/dev/amble/ait/client/AITModClient.java
git commit -m "feat(boti): Phase B M1 nested-render feasibility probe (Iris pipeline swap)"
```

---

## After M1

- **PASS** → invoke writing-plans for M2–M5 (doorway clip, camera/parallax, perf/gating, coexistence/config), informed by what M1 revealed about pipeline save/restore.
- **FAIL** → stop; report to the user that full-correctness Iris shading is infeasible on this Iris version, and Phase A (shipped) remains the ceiling. The probe class and the `AFTER_TRANSLUCENT` registration are reverted.

## Self-Review notes

- **Spec coverage:** M1 in the spec (feasibility gate: nested render + pipeline save/restore, unclipped, one doorway) → Tasks 1–2. M2–M5 are explicitly out of scope for this plan (spec says each milestone is independently verifiable; they get their own plan post-M1).
- **Placeholder scan:** the rough camera and "adjust to actual yarn signature" steps are genuine feasibility-probe latitude, not deferred work — M1's deliverable is a verdict, not production code.
- **Type consistency:** `DependencyChecker.isIrisShaderPackInUse()`, `IrisNestedRenderProbe.run(WorldRenderContext)`, `PortalData.renderer()/world()` used consistently across tasks.
- **Non-Iris safety:** the probe's first line is `isIrisShaderPackInUse()`; the registration is inside `hasIris()`. Non-Iris builds never load Iris classes at runtime (the callback references them but is only registered under `hasIris()`; class-load happens lazily on first call, which only occurs when Iris is present).
