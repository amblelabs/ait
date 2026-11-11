# Gbuffer Injection — Milestone 1: Doorway Clip

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax.

**Goal:** Turn the validated throwaway gbuffer-injection probe into a real injector that draws the TARDIS interior into Iris's gbuffer **clipped to the doorway aperture** (no splatter), shaded, no doubling — inside→out view.

**Architecture:** At `WorldRenderEvents.AFTER_ENTITIES` (pre-deferred, gbuffer bound), for the current TARDIS: draw the door aperture mask into the gbuffer stencil, then draw the interior's baked terrain VBOs (solid + cutout + cutout-mipped) with `setPhase(TERRAIN_*)` and a stencil test so only the doorway pixels are written. Reuse the cached portal view/projection and the door mask/transform from the door render path.

**Tech Stack:** Java 21, MC 1.20.1, Fabric, Iris (`modCompileOnly`, guarded), blaze3d/OpenGL stencil.

**Spec:** `docs/superpowers/specs/2026-08-31-boti-iris-phase-b-gbuffer-injection-design.md`

## Global Constraints

- Build: `nix develop --command bash -lc './gradlew compileJava -Dorg.gradle.java.home="$JAVA_HOME"'` (JDK 21; the machine's global gradle.properties forces JDK 25). Never commit a machine-specific `org.gradle.java.home`.
- No unit tests; verification = compile + in-game observation.
- Iris only via `modCompileOnly`, guarded by `hasIris()`+`isIrisShaderPackInUse()`. Non-Iris and Iris-no-pack unchanged (Phase A).
- Shader-path only: this injector runs solely when `isIrisShaderPackInUse()`; the Phase A `END` composite remains for all other cases.
- Commits may fail on SSH signing in this env — if so, stage and report; the user signs.
- Render thread only; every GL state change (stencil, phase, blend, depth/color mask) has a matching restore.

---

## File Structure

- `src/main/java/dev/amble/ait/client/boti/iris/GbufferInjectionProbe.java` → rename/rework into `GbufferPortalInjector.java` (the real, clipped injector). One responsibility: clipped interior injection for the shader path.
- `src/main/java/dev/amble/ait/client/boti/iris/IrisPhase.java` — extend with `setTerrainCutout()` / `setTerrainCutoutMipped()` (same pattern as `setTerrainSolid`).
- `src/main/java/dev/loqor/portal/client/WorldGeometryRenderer.java` — replace the throwaway `debugInjectTerrainIntoGbuffer()` with `injectClippedTerrain()` that draws solid+cutout+cutout-mipped layers (stencil already set up by the caller).
- `src/main/java/dev/amble/ait/client/boti/TardisDoorBOTI.java` — extract the door aperture mask geometry + transform into a reusable static helper the injector can call to write the stencil (avoid duplicating the mask math).
- `src/main/java/dev/amble/ait/client/AITModClient.java` — point the `AFTER_ENTITIES` registration at the reworked injector.

---

## Task 1: Verify gbuffer stencil availability + doorway stencil mask

**Files:**
- Modify: `src/main/java/dev/amble/ait/client/boti/iris/GbufferInjectionProbe.java` (→ add stencil masking; keep unclipped draw as fallback if stencil unavailable)
- Modify: `src/main/java/dev/amble/ait/client/boti/TardisDoorBOTI.java` (extract mask helper)

**Interfaces:**
- Produces: `TardisDoorBOTI.drawDoorApertureMask(ClientTardis, DoorBlockEntity, MatrixStack)` — draws the door portal quad into the currently-bound framebuffer's stencil (the same aperture Phase A masks with), leaving stencil=1 inside the doorway.

- [ ] **Step 1: Extract the aperture-mask draw from `TardisDoorBOTI` into a reusable static helper**

`TardisDoorBOTI.renderInteriorDoorBoti` already builds the aperture transform (portal position, `portalWidth`/`portalHeight`, scale) and calls `mask.render(...)`. Extract the transform + `mask.render` into `public static void drawDoorApertureMask(...)` that assumes the caller has set up stencil write state (`glStencilFunc(GL_ALWAYS,1,0xFF)`, `glStencilOp(GL_KEEP,GL_KEEP,GL_REPLACE)`, colorMask off, depthMask off). Have Phase A's path call the extracted helper too (no behavior change there).

- [ ] **Step 2: In the injector, set up gbuffer stencil, draw the mask, then clip the injection**

Wrap the existing injection: enable stencil, draw the aperture mask (stencil=1 in doorway), set `glStencilFunc(GL_EQUAL,1,0xFF)` + `glStencilMask(0x00)`, then draw the interior terrain, then fully restore stencil state (disable, reset func/op/mask). Log once whether the gbuffer actually has stencil bits: `glGetInteger(GL_STENCIL_BITS)` after binding — if 0, stencil clip is unavailable in Iris's gbuffer (record for Step 4).

- [ ] **Step 3: Compile** — `nix develop --command bash -lc './gradlew compileJava -Dorg.gradle.java.home="$JAVA_HOME"'`.

- [ ] **Step 4: In-game read** — inside a TARDIS under a shaderpack:
  - Does the interior now render **only inside the doorway** (splatter gone)? Is it still shaded? Main world still not doubled?
  - Log: `GL_STENCIL_BITS=…`. If it was 0 but clipping still worked, great; if 0 and clipping failed, we pivot Task 2 to a **depth-aperture** clip instead of stencil (note it in the report).

- [ ] **Step 5: Commit** — `feat(boti): clip gbuffer injection to the doorway via stencil` (stage + report if signing fails).

---

## Task 2: Multi-layer clipped injection (solid + cutout + cutout-mipped)

**Files:**
- Modify: `src/main/java/dev/amble/ait/client/boti/iris/IrisPhase.java` (add `setTerrainCutout()`, `setTerrainCutoutMipped()`)
- Modify: `src/main/java/dev/loqor/portal/client/WorldGeometryRenderer.java` (replace `debugInjectTerrainIntoGbuffer` with `injectClippedTerrain()` drawing the three opaque layers, each wrapped in its matching phase)
- Modify: `src/main/java/dev/amble/ait/client/boti/iris/GbufferInjectionProbe.java` → rename to `GbufferPortalInjector.java`; call `injectClippedTerrain()`
- Modify: `src/main/java/dev/amble/ait/client/AITModClient.java` (registration → `GbufferPortalInjector::run`)

**Interfaces:**
- Consumes: `TardisDoorBOTI.drawDoorApertureMask` (Task 1), `IrisPhase.setTerrainSolid/Cutout/CutoutMipped/reset`.
- Produces: `WorldGeometryRenderer.injectClippedTerrain()` — draws solid, cutout-mipped, cutout layers (each phase-wrapped) using cached portal matrices, into the currently-bound (already stencil-clipped) gbuffer.

- [ ] **Step 1: Add cutout phase helpers to `IrisPhase`**

```java
public static boolean setTerrainCutout() { return set(WorldRenderingPhase.TERRAIN_CUTOUT); }
public static boolean setTerrainCutoutMipped() { return set(WorldRenderingPhase.TERRAIN_CUTOUT_MIPPED); }
```
(Refactor the existing `setTerrainSolid`/`reset` to a private `set(WorldRenderingPhase)`/nullable-pipeline helper to avoid duplication.)

- [ ] **Step 2: Replace `debugInjectTerrainIntoGbuffer` with `injectClippedTerrain()`**

Draw each opaque layer with its phase (mirror `renderTerrain`'s layer loop, but per-layer phase, and NO afbo, NO blend changes beyond what layers set):
```java
public void injectClippedTerrain() {
    if (sectionBuffers.isEmpty()) return;
    List<Map<RenderLayer, VertexBuffer>> visible = new ArrayList<>();
    for (Map.Entry<ChunkSectionPos, Map<RenderLayer, VertexBuffer>> e : sectionBuffers.entrySet())
        if (isSectionVisible(e.getKey())) visible.add(e.getValue());
    if (visible.isEmpty()) return;
    RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
    drawPhased(RenderLayer.getSolid(), visible, IrisPhase::setTerrainSolid);
    drawPhased(RenderLayer.getCutoutMipped(), visible, IrisPhase::setTerrainCutoutMipped);
    drawPhased(RenderLayer.getCutout(), visible, IrisPhase::setTerrainCutout);
}
private void drawPhased(RenderLayer layer, List<Map<RenderLayer, VertexBuffer>> visible, BooleanSupplier phase) {
    boolean p = phase.getAsBoolean();
    try { drawLayer(layer, visible); } finally { if (p) dev.amble.ait.client.boti.iris.IrisPhase.reset(); }
}
```
(Import `java.util.function.BooleanSupplier`; `IrisPhase.setTerrainSolid` etc. are `BooleanSupplier`-compatible only if they take no args and return boolean — they do.)

- [ ] **Step 3: Rename the probe to `GbufferPortalInjector`, call `injectClippedTerrain()`, keep the stencil setup from Task 1 around the call; update the `AITModClient` registration.**

- [ ] **Step 4: Compile** — `nix develop --command bash -lc './gradlew compileJava -Dorg.gradle.java.home="$JAVA_HOME"'`.

- [ ] **Step 5: In-game read** — inside a TARDIS under a shaderpack: interior renders inside the doorway with solid + foliage/glass (cutout) present and shaded; no splatter; no doubling. Compare shading fullness vs Task 1 (solid-only).

- [ ] **Step 6: Commit** — `feat(boti): multi-layer clipped gbuffer injection (solid+cutout)` (stage + report if signing fails).

---

## After M1

If the doorway-clipped injection reads well in-game → plan M2 (shading fullness: `renderChunkLayer` mixin + translucent/shadow pass) and M3 (out→in + variants). If clipping is impossible in Iris's gbuffer (no stencil, depth-aperture also fails) → report and reconsider.

## Self-Review notes

- **Spec coverage:** M1 (doorway clip) → Tasks 1–2; stencil-availability unknown (spec risk #1) → Task 1 Step 2/4; multi-layer (part of shading fullness floor) → Task 2. M2–M4 explicitly deferred.
- **Types:** `drawDoorApertureMask`, `injectClippedTerrain`, `IrisPhase.setTerrainSolid/Cutout/CutoutMipped/reset`, `GbufferPortalInjector.run` used consistently.
- **No placeholders:** the depth-aperture fallback is a genuine conditional (gated on the `GL_STENCIL_BITS` reading), not deferred work.
