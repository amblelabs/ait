# BOTI under Iris — Phase A Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make the TARDIS BOTI portal effect render correctly when an Iris shaderpack is active (no smear, no skybox-colour bleed on items/text, interior visible), by making the four BOTI compositors framebuffer-agnostic instead of assuming `client.getFramebuffer()` is the on-screen target.

**Architecture:** Each BOTI variant copies the live scene into a stencil-enabled scratch FBO (`afbo`), draws the interior clipped to the doorway, then blits back. Today source+dest are hardcoded to `client.getFramebuffer()`, which under Iris is not the world target — so the copies are invisible and the `endWrite/beginWrite` juggling corrupts Iris's bound target. The fix: capture the *currently bound* draw FBO at entry, use it as copy source and composite destination, and restore it (plus GL state) at exit. Interior stays lit by our own lightmap (unshaded by the pack — correct for phase A).

**Tech Stack:** Java 21, Minecraft 1.20.1, Fabric, LWJGL/OpenGL (blaze3d `GlStateManager`/`RenderSystem`), Iris (soft dependency, detected via `DependencyChecker.hasIris()`; no compile coupling in phase A).

**Spec:** `docs/superpowers/specs/2026-08-31-boti-iris-rendering-design.md`

## Global Constraints

- **Build/JDK:** system Java is 17; the project needs JDK 21. Compile with `nix develop --command ./gradlew compileJava` from the repo root. Never assume plain `./gradlew` works.
- **No Iris compile coupling in phase A.** Do not add an Iris `modCompileOnly` dependency (that is phase B). Detect Iris only via `DependencyChecker.hasIris()`.
- **No unit tests exist for this code.** Verification is (a) `compileJava` success and (b) in-game visual observation + a temporary instrumentation log. Do not fabricate JUnit tests for GL framebuffer compositing.
- **Preserve vanilla (no-Iris) behaviour exactly.** In the vanilla pipeline the captured draw FBO *is* `client.getFramebuffer()`, so the refactor must be a no-op there.
- **Render thread only.** All this code runs on the render thread; no concurrency guards needed, but never leak GL state across the callback boundary.
- **Mac depth path:** the existing `copyDepth` Mac shader path (`COPY_DEPTH_PROGRAM`, reads `src.getDepthAttachment()`) must keep working; generalise it to a raw destination FBO id rather than dropping it.

---

## File Structure

- `src/main/java/dev/amble/ait/client/boti/BOTI.java` — add framebuffer-agnostic helpers (`currentDrawFbo`, raw-fbo blit overloads, `beginBotiComposite`/`endBotiComposite`, `BotiCompositeState`). Central home for the shared compositing primitives all four variants already call.
- `src/main/java/dev/amble/ait/client/boti/TardisDoorBOTI.java` — convert entry/mid/exit to the agnostic helpers.
- `src/main/java/dev/amble/ait/client/boti/TardisExteriorBOTI.java` — same conversion.
- `src/main/java/dev/amble/ait/client/boti/RiftBOTI.java` — same conversion.
- `src/main/java/dev/amble/ait/client/boti/PaintingBOTI.java` — same conversion.
- `src/main/java/dev/amble/ait/client/AITModClient.java` — (Task 6, conditional only) fallback hook wiring if `END` proves too late.
- `src/main/java/dev/amble/ait/mixin/client/boti/IrisTranslucentBotiMixin.java` — (Task 6, conditional only) fallback translucent-phase hook.

---

## Task 1: Instrument the live Iris draw target (decision gate)

Determine, in-game, which framebuffer is bound at the BOTI hook under Iris and whether it holds the world colour+depth. This gates whether the `WorldRenderEvents.END` hook is sufficient (Tasks 2–5) or the fallback mixin (Task 6) is needed.

**Files:**
- Modify: `src/main/java/dev/amble/ait/client/boti/BOTI.java` (add `currentDrawFbo`)
- Modify: `src/main/java/dev/amble/ait/client/boti/TardisDoorBOTI.java` (temporary log at entry, `:52` area)

**Interfaces:**
- Produces: `public static int BOTI.currentDrawFbo()` — returns the GL id of the currently bound draw framebuffer. Reused by all later tasks.

- [ ] **Step 1: Add `currentDrawFbo()` to `BOTI.java`**

Add imports `org.lwjgl.opengl.GL11` (already present) and `org.lwjgl.opengl.GL30`, then:

```java
/** The GL id of the framebuffer currently bound for drawing. Under Iris this is Iris's live world
 *  target, not client.getFramebuffer(); in the vanilla pipeline the two are the same. */
public static int currentDrawFbo() {
    return GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
}
```

- [ ] **Step 2: Add a temporary instrumentation log at the door BOTI entry**

In `TardisDoorBOTI.renderInteriorDoorBoti`, immediately before `client.getFramebuffer().endWrite();` (`:52`):

```java
AITMod.LOGGER.info("BOTI-DIAG hasIris={} boundDrawFbo={} mainFbo={} win={}x{}",
        dev.amble.ait.compat.DependencyChecker.hasIris(),
        BOTI.currentDrawFbo(),
        client.getFramebuffer().fbo,
        client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight());
```

- [ ] **Step 3: Compile**

Run: `nix develop --command ./gradlew compileJava`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 4: Run in-game and capture the log**

Launch the client with Iris installed. Enable a shaderpack (e.g. BSL or Complementary). Place/enter a TARDIS so the door BOTI renders. Read the log lines.
Record:
- Under Iris + shaderpack: is `boundDrawFbo` non-zero and **different** from `mainFbo`? (Expected: yes — that is Iris's world target.)
- Does the doorway currently show broken (skybox-colour) output? (Baseline confirmation.)

- [ ] **Step 5: Decision gate (write the finding into the plan/PR notes)**

- If `boundDrawFbo` is non-zero at the hook (world target live): proceed with Tasks 2–5 (END hook is sufficient). **Skip Task 6.**
- If `boundDrawFbo` is `0` (default FB) or the world target is already blitted/cleared by this point: Tasks 2–4 still apply, but the hook must move earlier — implement **Task 6** (translucent-phase mixin) and register the variants there under Iris.

- [ ] **Step 6: Commit**

```bash
git add src/main/java/dev/amble/ait/client/boti/BOTI.java \
        src/main/java/dev/amble/ait/client/boti/TardisDoorBOTI.java
git commit -m "diag(boti): instrument bound draw fbo at door BOTI hook"
```

---

## Task 2: Framebuffer-agnostic blit + state helpers in `BOTI.java`

Add the primitives the variants will use so their compositing no longer references `client.getFramebuffer()`.

**Files:**
- Modify: `src/main/java/dev/amble/ait/client/boti/BOTI.java`

**Interfaces:**
- Consumes: `BOTI.currentDrawFbo()` (Task 1).
- Produces:
  - `public static void copyColorFromFbo(int srcFbo, int w, int h, Framebuffer dest)`
  - `public static void copyColorToFbo(Framebuffer src, int destFbo, int w, int h)`
  - `public static void copyDepthToFbo(Framebuffer src, int destFbo, int w, int h)` — Mac shader path aware
  - `public static BotiCompositeState beginBotiComposite()`
  - `public static void endBotiComposite(BotiCompositeState state)`
  - `public static final class BotiCompositeState` (holds `int drawFbo; int[] viewport; boolean stencil, depthMask, cullR,cullG,cullB,cullA...`) — opaque holder; only `begin/endBotiComposite` touch its fields.

- [ ] **Step 1: Add the raw-fbo colour blits**

Add to `BOTI.java` (mirror existing `copyColor`, imports `GlStateManager`, `GlConst` already present):

```java
/** Blit a Framebuffer's colour into a raw destination FBO id (e.g. Iris's live world target). */
public static void copyColorToFbo(Framebuffer src, int destFbo, int w, int h) {
    GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, src.fbo);
    GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, destFbo);
    GlStateManager._glBlitFrameBuffer(0, 0, src.textureWidth, src.textureHeight,
            0, 0, w, h, GlConst.GL_COLOR_BUFFER_BIT, GlConst.GL_NEAREST);
}

/** Blit a raw source FBO id's colour into a Framebuffer (e.g. the live scene -> afbo backdrop). */
public static void copyColorFromFbo(int srcFbo, int w, int h, Framebuffer dest) {
    GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, srcFbo);
    GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, dest.fbo);
    GlStateManager._glBlitFrameBuffer(0, 0, w, h,
            0, 0, dest.textureWidth, dest.textureHeight, GlConst.GL_COLOR_BUFFER_BIT, GlConst.GL_NEAREST);
}
```

- [ ] **Step 2: Add `copyFramebufferFromFbo` (colour+depth) for the entry backdrop copy**

```java
/** Copy the live scene (raw source FBO id) colour+depth into afbo, replacing copyFramebuffer(main, afbo). */
public static void copyFramebufferFromFbo(int srcFbo, int w, int h, Framebuffer dest) {
    copyColorFromFbo(srcFbo, w, h, dest);
    GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, srcFbo);
    GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, dest.fbo);
    GlStateManager._glBlitFrameBuffer(0, 0, w, h,
            0, 0, dest.textureWidth, dest.textureHeight, GlConst.GL_DEPTH_BUFFER_BIT, GlConst.GL_NEAREST);
    GL11.glGetError();
}
```

- [ ] **Step 3: Add `copyDepthToFbo` (afbo depth -> live target), Mac-shader aware**

Generalise the existing Mac path (`copyDepth`) to a raw destination FBO id + explicit dimensions:

```java
/** Copy afbo's depth into a raw destination FBO id. Mirrors copyDepth's non-Mac blit and Mac shader path. */
public static void copyDepthToFbo(Framebuffer src, int destFbo, int w, int h) {
    if (!MinecraftClient.IS_SYSTEM_MAC || COPY_DEPTH_PROGRAM == null || src.getDepthAttachment() <= 0) {
        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, src.fbo);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, destFbo);
        GlStateManager._glBlitFrameBuffer(0, 0, src.textureWidth, src.textureHeight,
                0, 0, w, h, GlConst.GL_DEPTH_BUFFER_BIT, GlConst.GL_NEAREST);
        GL11.glGetError();
        return;
    }

    GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, destFbo);
    RenderSystem.viewport(0, 0, w, h);

    RenderSystem.colorMask(false, false, false, false);
    RenderSystem.depthMask(true);
    RenderSystem.enableDepthTest();
    RenderSystem.depthFunc(GL11.GL_ALWAYS);
    RenderSystem.disableCull();

    Matrix4f prevProjection = new Matrix4f(RenderSystem.getProjectionMatrix());
    VertexSorter prevSorter = RenderSystem.getVertexSorting();
    RenderSystem.setProjectionMatrix(IDENTITY_MATRIX, VertexSorter.BY_DISTANCE);
    MatrixStack modelView = RenderSystem.getModelViewStack();
    modelView.push();
    modelView.loadIdentity();
    RenderSystem.applyModelViewMatrix();

    RenderSystem.setShaderTexture(0, src.getDepthAttachment());
    RenderSystem.setShader(() -> COPY_DEPTH_PROGRAM);

    BufferBuilder builder = Tessellator.getInstance().getBuffer();
    builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
    builder.vertex(-1.0, -1.0, 0.0).texture(0.0f, 0.0f).next();
    builder.vertex(1.0, -1.0, 0.0).texture(1.0f, 0.0f).next();
    builder.vertex(1.0, 1.0, 0.0).texture(1.0f, 1.0f).next();
    builder.vertex(-1.0, 1.0, 0.0).texture(0.0f, 1.0f).next();
    BufferRenderer.drawWithGlobalProgram(builder.end());

    modelView.pop();
    RenderSystem.applyModelViewMatrix();
    RenderSystem.setProjectionMatrix(prevProjection, prevSorter);

    RenderSystem.depthFunc(GL11.GL_LEQUAL);
    RenderSystem.colorMask(true, true, true, true);
    RenderSystem.depthMask(true);
    RenderSystem.enableCull();
}
```

- [ ] **Step 4: Add `BotiCompositeState` + `beginBotiComposite`/`endBotiComposite`**

```java
/** Captured GL state for one BOTI composite, so the callback restores exactly what it found and never
 *  leaves Iris's next pass on the wrong target or with dirty stencil/depth state. */
public static final class BotiCompositeState {
    int drawFbo;
    final int[] viewport = new int[4];
    boolean stencilEnabled;
    boolean depthMask;
}

/** Capture the live draw target + the GL state the composite mutates. Call at the very start of a variant. */
public static BotiCompositeState beginBotiComposite() {
    BotiCompositeState s = new BotiCompositeState();
    s.drawFbo = currentDrawFbo();
    GL11.glGetIntegerv(GL11.GL_VIEWPORT, s.viewport);
    s.stencilEnabled = GL11.glIsEnabled(GL11.GL_STENCIL_TEST);
    s.depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
    return s;
}

/** Rebind the captured target + restore state. Call after the afbo colour has been blitted back. */
public static void endBotiComposite(BotiCompositeState s) {
    GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, s.drawFbo);
    RenderSystem.viewport(s.viewport[0], s.viewport[1], s.viewport[2], s.viewport[3]);
    GL11.glStencilMask(0xFF);
    GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 0xFF);
    GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
    if (!s.stencilEnabled) GL11.glDisable(GL11.GL_STENCIL_TEST);
    RenderSystem.depthMask(s.depthMask);
    RenderSystem.enableCull();
}
```

(Add imports as needed: `org.lwjgl.opengl.GL30` for Task 1; `GL11` already imported.)

- [ ] **Step 5: Compile**

Run: `nix develop --command ./gradlew compileJava`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 6: Commit**

```bash
git add src/main/java/dev/amble/ait/client/boti/BOTI.java
git commit -m "feat(boti): framebuffer-agnostic composite helpers"
```

---

## Task 3: Convert `TardisDoorBOTI` to the agnostic composite

**Files:**
- Modify: `src/main/java/dev/amble/ait/client/boti/TardisDoorBOTI.java` (`:52`, `:80`, `:113`, `:236`–`:250`)

**Interfaces:**
- Consumes: `BOTI.beginBotiComposite()`, `BOTI.copyFramebufferFromFbo`, `BOTI.copyDepthToFbo`, `BOTI.copyColorToFbo`, `BOTI.endBotiComposite`, `BotiCompositeState`, `BOTI.currentDrawFbo()`.

- [ ] **Step 1: Capture state at entry**

Replace (`:52`):
```java
client.getFramebuffer().endWrite();
```
with:
```java
BOTI.BotiCompositeState composite = BOTI.beginBotiComposite();
int winW = client.getWindow().getFramebufferWidth();
int winH = client.getWindow().getFramebufferHeight();
```

- [ ] **Step 2: Backdrop copy from the live target**

Replace (`:80`):
```java
BOTI.copyFramebuffer(client.getFramebuffer(), BOTI_HANDLER.afbo);
```
with:
```java
BOTI.copyFramebufferFromFbo(composite.drawFbo, winW, winH, BOTI_HANDLER.afbo);
```

- [ ] **Step 3: Mid depth copy to the live target**

Replace (`:113`):
```java
copyDepth(BOTI_HANDLER.afbo, client.getFramebuffer());
```
with:
```java
BOTI.copyDepthToFbo(BOTI_HANDLER.afbo, composite.drawFbo, winW, winH);
```

- [ ] **Step 4: Composite back + restore at exit**

Replace the tail block (`:236`–`:250`):
```java
        // **NEW APPROACH: Disable stencil BEFORE switching framebuffers**
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0x00);

        // Switch to main framebuffer and copy color
        client.getFramebuffer().beginWrite(false);  // false = don't check for errors
        BOTI.copyColor(BOTI_HANDLER.afbo, client.getFramebuffer());

        // Reset all stencil state on main framebuffer
        GL11.glStencilMask(0xFF);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);

        // Ensure depth mask is enabled for normal rendering
        RenderSystem.depthMask(true);
```
with:
```java
        // Disable stencil before blitting the composited interior back to the live target.
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0x00);

        // Blit afbo's colour into whatever framebuffer was live at entry (Iris's world target, or the
        // vanilla main FB), then rebind it and restore the GL state we captured.
        BOTI.copyColorToFbo(BOTI_HANDLER.afbo, composite.drawFbo, winW, winH);
        BOTI.endBotiComposite(composite);
```

- [ ] **Step 5: Compile**

Run: `nix develop --command ./gradlew compileJava`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 6: In-game verification (door variant)**

Launch client. Verify in three configs:
- Vanilla (no Iris): door BOTI unchanged from before.
- Iris, no shaderpack: interior renders in the doorway; items/text render normally; no smear.
- Iris + BSL/Complementary: interior renders in the doorway; items/text normal; no smear.

- [ ] **Step 7: Commit**

```bash
git add src/main/java/dev/amble/ait/client/boti/TardisDoorBOTI.java
git commit -m "fix(boti): door BOTI composites into the live draw target (Iris-safe)"
```

---

## Task 4: Convert `TardisExteriorBOTI`, `RiftBOTI`, `PaintingBOTI`

Identical transformation, three files. Each uses either `client` or `MinecraftClient.getInstance()` for the client handle — match the existing local in each file.

**Files:**
- Modify: `src/main/java/dev/amble/ait/client/boti/TardisExteriorBOTI.java` (`:53`, `:63`, `:95`, `:228`, `:230`, `:232`)
- Modify: `src/main/java/dev/amble/ait/client/boti/RiftBOTI.java` (`:29`, `:33`, `:49`, `:81`, `:83`, `:85`)
- Modify: `src/main/java/dev/amble/ait/client/boti/PaintingBOTI.java` (`:29`, `:33`, `:53`, `:70`, `:72`, `:74`)

**Interfaces:**
- Consumes: same `BOTI` helpers as Task 3.

- [ ] **Step 1: TardisExteriorBOTI — entry (`:53`)**

Replace:
```java
MinecraftClient.getInstance().getFramebuffer().endWrite();
```
with:
```java
BOTI.BotiCompositeState composite = BOTI.beginBotiComposite();
int winW = MinecraftClient.getInstance().getWindow().getFramebufferWidth();
int winH = MinecraftClient.getInstance().getWindow().getFramebufferHeight();
```

- [ ] **Step 2: TardisExteriorBOTI — backdrop (`:63`), mid depth (`:95`), exit (`:228`–`:232`)**

`:63` replace:
```java
BOTI.copyFramebuffer(MinecraftClient.getInstance().getFramebuffer(), BOTI_HANDLER.afbo);
```
with:
```java
BOTI.copyFramebufferFromFbo(composite.drawFbo, winW, winH, BOTI_HANDLER.afbo);
```
`:95` replace:
```java
copyDepth(BOTI_HANDLER.afbo, MinecraftClient.getInstance().getFramebuffer());
```
with:
```java
BOTI.copyDepthToFbo(BOTI_HANDLER.afbo, composite.drawFbo, winW, winH);
```
`:228`–`:232` replace:
```java
        MinecraftClient.getInstance().getFramebuffer().beginWrite(true);

        BOTI.copyColor(BOTI_HANDLER.afbo, MinecraftClient.getInstance().getFramebuffer());

        GL11.glDisable(GL11.GL_STENCIL_TEST);
```
with:
```java
        BOTI.copyColorToFbo(BOTI_HANDLER.afbo, composite.drawFbo, winW, winH);
        BOTI.endBotiComposite(composite);
```
(If lines below `:232` also reset stencil/depth mask, they are now handled by `endBotiComposite` — remove any duplicate `glDisable(GL_STENCIL_TEST)` / `depthMask(true)` that immediately follow, matching the door variant's cleaned tail.)

- [ ] **Step 3: RiftBOTI — entry (`:29`), backdrop (`:33`), mid depth (`:49`), exit (`:81`–`:87`)**

`:29` replace `client.getFramebuffer().endWrite();` with:
```java
BOTI.BotiCompositeState composite = BOTI.beginBotiComposite();
int winW = client.getWindow().getFramebufferWidth();
int winH = client.getWindow().getFramebufferHeight();
```
`:33` replace `BOTI.copyFramebuffer(client.getFramebuffer(), BOTI_HANDLER.afbo);` with:
```java
BOTI.copyFramebufferFromFbo(composite.drawFbo, winW, winH, BOTI_HANDLER.afbo);
```
`:49` replace `copyDepth(BOTI_HANDLER.afbo, client.getFramebuffer());` with:
```java
BOTI.copyDepthToFbo(BOTI_HANDLER.afbo, composite.drawFbo, winW, winH);
```
`:81`–`:87` replace:
```java
        client.getFramebuffer().beginWrite(true);

        BOTI.copyColor(BOTI_HANDLER.afbo, client.getFramebuffer());

        GL11.glDisable(GL11.GL_STENCIL_TEST);

        RenderSystem.depthMask(true);
```
with:
```java
        BOTI.copyColorToFbo(BOTI_HANDLER.afbo, composite.drawFbo, winW, winH);
        BOTI.endBotiComposite(composite);
```

- [ ] **Step 4: PaintingBOTI — entry (`:29`), backdrop (`:33`), mid depth (`:53`), exit (`:70`–`:76`)**

`:29` replace `client.getFramebuffer().endWrite();` with:
```java
BOTI.BotiCompositeState composite = BOTI.beginBotiComposite();
int winW = client.getWindow().getFramebufferWidth();
int winH = client.getWindow().getFramebufferHeight();
```
`:33` replace `BOTI.copyFramebuffer(client.getFramebuffer(), BOTI_HANDLER.afbo);` with:
```java
BOTI.copyFramebufferFromFbo(composite.drawFbo, winW, winH, BOTI_HANDLER.afbo);
```
`:53` replace `BOTI.copyDepth(BOTI_HANDLER.afbo, client.getFramebuffer());` with:
```java
BOTI.copyDepthToFbo(BOTI_HANDLER.afbo, composite.drawFbo, winW, winH);
```
`:70`–`:76` replace:
```java
        client.getFramebuffer().beginWrite(true);

        BOTI.copyColor(BOTI_HANDLER.afbo, client.getFramebuffer());

        GL11.glDisable(GL11.GL_STENCIL_TEST);

        RenderSystem.depthMask(true);
```
with:
```java
        BOTI.copyColorToFbo(BOTI_HANDLER.afbo, composite.drawFbo, winW, winH);
        BOTI.endBotiComposite(composite);
```

- [ ] **Step 5: Compile**

Run: `nix develop --command ./gradlew compileJava`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 6: In-game verification (all remaining variants)**

For each of exterior, rift, painting: verify under vanilla, Iris-no-pack, and Iris+shaderpack — interior renders in place, no smear, items/text normal.

- [ ] **Step 7: Commit**

```bash
git add src/main/java/dev/amble/ait/client/boti/TardisExteriorBOTI.java \
        src/main/java/dev/amble/ait/client/boti/RiftBOTI.java \
        src/main/java/dev/amble/ait/client/boti/PaintingBOTI.java
git commit -m "fix(boti): exterior/rift/painting BOTI composite into live draw target (Iris-safe)"
```

---

## Task 5: Remove instrumentation + full verification pass

**Files:**
- Modify: `src/main/java/dev/amble/ait/client/boti/TardisDoorBOTI.java` (remove the Task 1 log)

- [ ] **Step 1: Remove the temporary `BOTI-DIAG` log line** added in Task 1 Step 2. Keep `BOTI.currentDrawFbo()` (it is used by the helpers).

- [ ] **Step 2: Compile**

Run: `nix develop --command ./gradlew compileJava`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Full in-game matrix**

| Config | door | exterior | rift | painting |
|---|---|---|---|---|
| Vanilla, no Iris | unchanged | unchanged | unchanged | unchanged |
| Iris, no shaderpack | renders, no smear | renders, no smear | renders, no smear | renders, no smear |
| Iris + shaderpack | renders, no smear, items/text normal | same | same | same |

Confirm the original bug (items/text as skybox colour) is gone in the Iris+shaderpack row.

- [ ] **Step 4: Commit**

```bash
git add src/main/java/dev/amble/ait/client/boti/TardisDoorBOTI.java
git commit -m "chore(boti): drop temporary Iris draw-target instrumentation"
```

---

## Task 6 (CONDITIONAL — only if Task 1 Step 5 chose the fallback)

Only implement if Task 1 found the bound draw FBO at `WorldRenderEvents.END` is `0`/already-blitted under Iris. Otherwise skip entirely.

**Files:**
- Create: `src/main/java/dev/amble/ait/mixin/client/boti/IrisTranslucentBotiMixin.java`
- Modify: `src/main/java/dev/amble/ait/client/AITModClient.java` (`:176`)
- Modify: the relevant mixin json (`ait.client.mixins.json` or equivalent — locate via `find src/main/resources -name '*mixins*.json'`)

**Interfaces:**
- Produces: a client-side hook firing during Iris's translucent phase (pipeline + world target still bound) that invokes the same variant methods used at `END`.

- [ ] **Step 1: Locate the render seam.** The variants must run while Iris's world target is bound — during the translucent terrain pass. Add a mixin into `net.minecraft.client.render.WorldRenderer#render` at the point after translucent terrain, `@Inject` at a `RenderLayer.getTranslucent().endDrawing()`-adjacent call, guarded by `if (!DependencyChecker.hasIris() || !IrisApi... )` — but since phase A forbids Iris compile coupling, guard only with `DependencyChecker.hasIris()` and call a static dispatch that drains the same BOTI queues the `END` callbacks use.

- [ ] **Step 2: Move the five BOTI callbacks** (`exteriorBOTI`, `doorBOTI`, `gallifreyanBOTI`, `trenzaloreBOTI`, `riftBOTI`) off `WorldRenderEvents.END` and invoke them from the mixin dispatch instead, only when `hasIris()`. Keep the non-Iris `AFTER_ENTITIES` registration unchanged.

- [ ] **Step 3: Compile** — `nix develop --command ./gradlew compileJava`.

- [ ] **Step 4: In-game verify** the full matrix from Task 5 Step 3.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/dev/amble/ait/mixin/client/boti/IrisTranslucentBotiMixin.java \
        src/main/java/dev/amble/ait/client/AITModClient.java src/main/resources/*mixins*.json
git commit -m "fix(boti): run Iris BOTI composite during translucent phase (live world target)"
```

---

## Self-Review notes

- **Spec coverage:** A.1 (agnostic principle) → Tasks 2–4; A.2 (helper + variant changes, event split) → Tasks 2–4, 6; A.3 (verification-gated hook) → Task 1 + conditional Task 6; A.4 (test matrix) → Tasks 3/4/5; A.5 (state-leak risk) → `endBotiComposite`. Phase B is out of scope for this plan.
- **Vanilla parity:** in the vanilla pipeline `composite.drawFbo == client.getFramebuffer().fbo`, so the copies target the same FBO as before and behaviour is preserved.
- **Type consistency:** `BotiCompositeState` field `drawFbo`, helper names `copyColorToFbo`/`copyColorFromFbo`/`copyFramebufferFromFbo`/`copyDepthToFbo`/`beginBotiComposite`/`endBotiComposite`/`currentDrawFbo` are used identically in every task.
