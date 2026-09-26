# BOTI Rendering under Iris — Design

Date: 2026-08-31
Branch: `feat/bigger-on-the-inside`
Status: Approved for phase A implementation; phase B is spec-only.

## Problem

The TARDIS "bigger on the inside" (BOTI) effect draws a portal's interior world
through a doorway/exterior/rift/painting. When an Iris shaderpack is enabled the
effect is visibly broken: **items and text in the world render as the skybox
colour, and neither the interior nor exterior world renders through the portal.**

### Root cause

All four BOTI variants (`TardisDoorBOTI`, `TardisExteriorBOTI`, `RiftBOTI`,
`PaintingBOTI`, all in `dev.amble.ait.client.boti`) use a stencil-enabled scratch
framebuffer (`BOTIInit.afbo`) and composite it against `client.getFramebuffer()`:

1. `client.getFramebuffer().endWrite()`
2. `BOTI_HANDLER.setupFramebuffer()` — bind `afbo`, clear its colour to the
   exterior fog/sky colour
3. `BOTI.copyFramebuffer(client.getFramebuffer(), afbo)` — copy the main FB
   colour+depth into `afbo`
4. draw the doorway mask into `afbo`'s stencil
5. `copyDepth(afbo, client.getFramebuffer())`
6. `afbo.beginWrite(false)` — draw the interior (`WorldGeometryRenderer.render`)
   into `afbo`, clipped by the stencil
7. `BOTI.copyColor(afbo, client.getFramebuffer())` — blit `afbo` back to the main
   FB
8. restore stencil/depth state

This is only correct in the vanilla pipeline, where `client.getFramebuffer()` is
the framebuffer that ends up on screen. With an Iris shaderpack active:

- `client.getFramebuffer()` is **not** the framebuffer Iris renders the world
  into (Iris owns its own gbuffer MRT targets), so the `copyColor` back to it is
  never displayed.
- The `endWrite()`/`beginWrite()` juggling on `client.getFramebuffer()` yanks the
  bound draw target away from whatever Iris had bound. Subsequent Iris passes
  (items, text) then draw into / sample the wrong target — which was cleared to
  the sky colour — producing the "renders as skybox colour" symptom.
- The current "Iris support" is only a band-aid: when Iris is present the BOTI
  render callbacks are registered on `WorldRenderEvents.END` instead of
  `AFTER_ENTITIES` (`AITModClient:176`). This changes *when* we trample Iris, not
  *that* we trample it.

### Investigation findings (Iris internals, MC 1.20.1 / Iris 1.6.x–1.7.x)

Confirmed against the Iris `1.20.1` source branch and Iris PR #425 (Immersive
Portals compatibility):

- The only stable **public** Iris API is
  `net.irisshaders.iris.api.v0.IrisApi.getInstance()` with
  `isShaderPackInUse()` and `isRenderingShadowPass()`. Everything else is
  internal and version-locked.
- Iris caches a `WorldRenderingPipeline` **per dimension** in
  `PipelineManager` (`preparePipeline(NamespacedId dimension)` creates/switches
  it). Immersive Portals renders a secondary world through the shaderpack by
  switching the active pipeline to that world's dimension, rendering, and
  restoring — this is the only known mechanism for shaderpack-shaded portal
  geometry, and it is explicitly fragile / version-locked (even IP documents
  "unfixable" portal issues).
- `WorldRenderingPipeline.setPhase(WorldRenderingPhase)` selects which gbuffer
  program Iris binds; the correct program is only bound **during** the world
  pass, not at `WorldRenderEvents.END` (which is after
  `finalizeLevelRendering()`).
- Iris copies world depth into `MinecraftClient.getFramebuffer()` after world
  rendering (Iris PR #425), so depth is available post-world on the main FB —
  but colour compositing to it is not displayed.

The interior geometry itself is drawn in `WorldGeometryRenderer.drawLayer` via
`vbo.draw(view, proj, RenderSystem.getShader())` using **vanilla** `RenderLayer`
shader programs and **vanilla vertex formats**. Iris's terrain gbuffer program
expects an extended vertex format (normal/tangent/midTexCoord/blockId), so even
if these draws happened during the gbuffer phase they would be mis-shaded. This
is why full shaderpack shading (phase B) is a large, separate effort.

## Scope decision

Phased delivery (agreed):

- **Phase A (build now):** make BOTI render *correctly* under Iris — no smear, no
  skybox-colour bleed, interior/exterior/rift/painting all visible. The interior
  is lit by our own lightmap, **not** by the shaderpack. Robust across Iris
  versions.
- **Phase B (spec only):** full shaderpack integration so interior geometry is
  lit/shadowed by the pack. Led by a throwaway research spike on the
  extended-vertex-format problem. Version-locked to Iris internals.

---

## Phase A — Robust depth-composite fix

### A.1 Principle: framebuffer-agnostic compositing

The defect is that the BOTI variants hardcode `client.getFramebuffer()` as the
depth/colour **source** and the composite **destination**, and call
`endWrite`/`beginWrite` on it. The fix is to operate on whatever draw
framebuffer is actually bound at our hook point:

- At entry, capture the currently bound draw framebuffer
  (`GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING)`), plus the stencil,
  depth-mask, colour-mask and cull state we mutate.
- Use the captured FBO as the source for `copyColor`/`copyDepth` (its colour and
  depth are the live scene) and as the destination for the final composite blit.
- Never call `client.getFramebuffer().endWrite()/beginWrite()` — those assume the
  vanilla main FB is the world target.
- At exit, rebind the captured FBO and restore all mutated GL state so Iris's
  next pass sees exactly the state it left.

The same code path then works in both pipelines: in vanilla the captured FBO *is*
`client.getFramebuffer()`; under Iris it is Iris's active world target.

### A.2 Changes

`dev.amble.ait.client.boti.BOTI`:

- `copyColor`/`copyDepth`/`copyFramebuffer` gain the ability to source from the
  captured live framebuffer rather than `client.getFramebuffer()`. Add a small
  helper to read the bound draw FBO id and to blit `afbo` back to a given FBO id
  (not a `Framebuffer` object, since Iris's target is not a vanilla
  `Framebuffer`).
- Add `beginBotiComposite()` / `endBotiComposite()` helpers that capture and
  restore (bound draw FBO id + stencil/depth/colour-mask/cull). The four variants
  call these instead of the ad-hoc `endWrite`/`beginWrite` + manual state resets.

`TardisDoorBOTI`, `TardisExteriorBOTI`, `RiftBOTI`, `PaintingBOTI`:

- Replace `client.getFramebuffer().endWrite()` (entry) and the
  `client.getFramebuffer().beginWrite(false)` + stencil/depth-mask restores
  (exit) with the `beginBotiComposite()`/`endBotiComposite()` helpers.
- Copy source/destination become the captured FBO id, not
  `client.getFramebuffer()`.
- The Mac depth-shader path (`COPY_DEPTH_PROGRAM`, reads
  `src.getDepthAttachment()`) is unaffected for the `afbo` side; the main-FB side
  becomes a blit to the captured FBO id.

`AITModClient` (`:176`):

- Keep the `hasIris()` event-registration split for now. Once A is verified,
  both branches can converge on a single hook; the split remains as the safe
  seam if the hook point must differ under Iris (see A.3).

No change to `WorldGeometryRenderer` draw logic in phase A.

### A.3 The verification-gated unknown (hook point / target liveness)

From source alone it is not provable **which** FBO is bound at
`WorldRenderEvents.END` under Iris, nor whether a composite into it survives
Iris's final blit to screen. Implementation therefore begins with
instrumentation, not a guessed fix (systematic-debugging):

1. At the Iris BOTI hook, log `GL_DRAW_FRAMEBUFFER_BINDING`,
   `IrisApi.isShaderPackInUse()`, and (via reflection or `modCompileOnly`) the
   current pipeline phase.
2. Confirm the captured FBO already contains the world colour+depth.
3. Confirm composited portal pixels reach the screen.

If `END` is too late (Iris has already blitted to screen), the fallback is a
small Iris-guarded mixin that fires during the translucent phase (pipeline still
bound, world target still live) and drives the same framebuffer-agnostic
composite. The event-registration split in A.2 is the seam that makes this
swap-in local.

### A.4 Testing (manual, visual — inherently not unit-testable)

Matrix run in-game:

| Config | Expectation |
|---|---|
| Vanilla, no Iris | Unchanged from today |
| Iris, no shaderpack | Interior renders in doorway; items/text normal; no smear |
| Iris + BSL/Complementary | Interior renders in doorway; items/text normal; no smear |

For each Iris config verify all four variants: door, exterior, rift, painting.
Plus the A.3 instrumentation log confirming FBO liveness. Interior is expected to
be **unshaded by the pack** in phase A — that is correct for this phase, not a
bug.

### A.5 Risks

- Iris may blit to screen before our hook → mitigated by the A.3 fallback mixin.
- Iris's world target may differ in size/format from `afbo` → the blit already
  uses explicit src/dst extents; verify no scaling artifacts.
- State leaks corrupting later Iris passes → `endBotiComposite()` restores the
  full captured state; assert bound FBO on exit in debug.

---

## Phase B — Full shaderpack integration (spec only)

Goal: interior geometry lit/shadowed by the active shaderpack, matching the
outside world. Mechanism mirrors Immersive Portals: render the interior *during*
Iris's world pass with the exterior dimension's pipeline bound.

Version-locked to Iris 1.6.x/1.7.x internals; must lock a compatible Iris range
and be guarded so a mismatched Iris version degrades to phase A behaviour.

### B.0 First step — research spike (throwaway): extended-vertex-format VBOs

Highest-value unknown to de-risk first. Prove that a baked section
`VertexBuffer`, rebuilt in Iris's extended vertex format
(`net.irisshaders.iris.vertices.IrisVertexFormats` — normal/tangent/
midTexCoord/blockId), is correctly shaded by the terrain gbuffer program when
drawn with the pipeline bound. Output: a recommendation (feasible / how) — the
spike code is discarded. If this is infeasible with our off-thread baking, phase
B's design must change (e.g. draw the interior through a real `WorldRenderer`
rather than our baked VBOs), so this gates the rest of the B spec.

### B.1 Build coupling

Add Iris as `modCompileOnly` (Modrinth/JitPack maven) so `IrisApi` and the
internal pipeline classes are type-safe at compile time. All calls guarded by
`DependencyChecker.hasIris()` and `IrisApi.getInstance().isShaderPackInUse()`.

### B.2 Pipeline switch per portal

Per visible portal, during the world pass:
`Iris.getPipelineManager().preparePipeline(exteriorDimId)` → `setPhase(...)` →
draw interior (extended-format VBOs + entities/BEs via the intercepted path) →
`preparePipeline(interiorDimId)` to restore. Risks: per-frame pipeline creation
side effects, uniform/lightmap correctness, TAA/motion-blur history sharing
across portals.

### B.3 Doorway clipping inside the gbuffer framebuffer

The interior must be clipped to the doorway aperture inside Iris's gbuffer FB. IP
does this with custom shader patching. Options to evaluate in the spike/spec:
stencil in the gbuffer FB (if Iris's target has a stencil attachment), or a
depth-prepass aperture. Highest-risk unknown after B.0.

### B.4 Shadow pass

For the interior to cast/receive shaderpack shadows it must also participate in
Iris's shadow pass (`isRenderingShadowPass()`), doubling the render and raising
correctness questions. May be deferred: shaded-but-no-self-shadow is an
acceptable intermediate.

### B.5 Fallback

Any Iris version/shape mismatch, or `!isShaderPackInUse()`, degrades to the phase
A path. Phase A is therefore the permanent floor.
