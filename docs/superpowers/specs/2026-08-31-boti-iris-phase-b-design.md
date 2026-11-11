# BOTI Phase B — Iris-Shaded Portals via Nested Shadow-World Render

Date: 2026-08-31
Branch: `feat/bigger-on-the-inside`
Status: Approved for spec; implementation plan to follow (writing-plans).
Supersedes: the "Phase B" outline in `2026-08-31-boti-iris-rendering-design.md`.

## Goal

Make the TARDIS BOTI portal interior/exterior be lit by the active Iris
shaderpack with its **own** dimension's lighting and self-shadows — matching how
the outside world looks — for the shaderpack path only, while preserving the
robust Phase A path when no shaderpack is active.

## Why the lighter approaches are dead (established, not assumed)

Two in-game experiments plus the Iris 1.20.1 source settled the design space:

- **Compositing at `WorldRenderEvents.END` (Phase A, shipped):** survives to the
  screen but is **unshaded** — Iris has already run `finalizeLevelRendering()`
  (composite + final) by the time our composite happens, so we are painting over
  a finished image.
- **Compositing during the world pass (`AFTER_TRANSLUCENT`) + `setPhase`
  (experiment, reverted):** rendered **nothing** on screen. Confirmed by source:
  Iris's composite + final passes run in `finalizeLevelRendering()` at
  `renderLevel` RETURN — *after* `AFTER_TRANSLUCENT` — so anything we composite
  mid-world-pass is reprocessed/overwritten and never reaches the screen.

Conclusion: for the interior to be both **shaded** and **visible**, its geometry
must be inside Iris's rendering *before* the deferred + composite passes, and be
processed by Iris's own pipeline — not composited from a side buffer. Given our
geometry is a whole secondary world seen from a portal camera, the only
architecture that yields correct per-dimension lighting is to **render the
shadow world through a real `WorldRenderer` under Iris's pipeline for that
dimension** — the Immersive Portals approach.

## Iris 1.20.1 facts this design relies on

From the `1.20.1` branch of IrisShaders/Iris:

- `MixinLevelRenderer` (on `net.minecraft.client.render.WorldRenderer` /
  `LevelRenderer`): at `renderLevel` HEAD it does
  `pipeline = Iris.getPipelineManager().preparePipeline(Iris.getCurrentDimension())`;
  after buffer clear it calls `pipeline.beginLevelRendering()`; at `renderLevel`
  RETURN it calls `pipeline.finalizeLevelRendering(); pipeline = null;`.
- The `@Unique pipeline` field is per-`WorldRenderer`-instance. Our shadow world
  owns a **separate** `WorldRenderer` (`PortalData:470`), so a nested render's
  HEAD/RETURN acts on the shadow instance's field — but
  `Iris.getPipelineManager()` holds a **single shared** current-pipeline
  reference that `preparePipeline` overwrites and the nested RETURN nulls. That
  shared reference is what the outer render and various Iris uniforms read.
- `PipelineManager` caches one pipeline **per dimension**
  (`preparePipeline(NamespacedId)`), and Iris already carries IP-compatibility
  changes (multi-dimension pipelines, `getVersionCounterForSodiumShaderReload`).
- `Iris.getCurrentDimension()` derives from the client's current level, so
  swapping `client.world` to the shadow world before the nested render makes
  Iris prepare the correct exterior-dimension pipeline.
- `IrisApi.getInstance().isShaderPackInUse()` gates the whole path.

## Architecture

### Two paths, selected per frame

- `!isShaderPackInUse()` → **Phase A path** (unchanged): baked-VBO composite via
  `afbo` at `WorldRenderEvents.END`. Robust, unshaded, the permanent floor.
- `isShaderPackInUse()` → **Phase B path**: nested shadow-world render (below).

The two share the portal geometry/world plumbing (`PortalData`, shadow
`ClientWorld`, streamed chunks) but diverge entirely at draw time.

### Phase B draw path (per visible doorway, during the main world render)

1. **Hook** inside the main `renderLevel` while Iris's outer pipeline is live
   (a mixin; the exact injection point is chosen so the outer gbuffer is bound
   and the outer pipeline is prepared — candidate: just before the outer
   translucent terrain, mirroring where IP recurses portals).
2. **Save outer state:** the shared `PipelineManager` current pipeline, plus
   `client.world`, camera, projection, lightmap, and the GL state we mutate
   (bound framebuffer, viewport, stencil, depth/color masks, cull).
3. **Stencil the doorway aperture** into Iris's gbuffer depth/stencil so the
   nested render only touches the doorway pixels.
4. **Swap to the shadow world:** set `client.world = shadowWorld`, install the
   portal camera + projection; `preparePipeline(exteriorDim)` (via
   `PipelineManager`) so Iris binds the exterior dimension's programs/targets.
5. **Nested render:** call the shadow `WorldRenderer.render(matrices, tickDelta,
   …, portalCamera, gameRenderer, lightmap, portalProjection)` — the full world
   render. Iris intercepts it (gbuffer → shadow pass → deferred → composite),
   producing correctly-shaded interior pixels in the doorway region.
6. **Restore outer state:** re-prepare / restore the outer pipeline reference so
   the outer render's remaining phases and uniforms see a live pipeline again
   (this is the crux — see Risks); restore `client.world`, camera, projection,
   lightmap, GL state.

### Coexistence & configuration

- A config toggle (default off until proven) selects the Phase B path when a
  shaderpack is active; off falls back to Phase A even under shaders.
- Iris added as `modCompileOnly` (Modrinth/JitPack maven) so `IrisApi`,
  `Iris`, `PipelineManager`, `WorldRenderingPipeline`, `WorldRenderingPhase` are
  type-safe. All uses guarded by `DependencyChecker.hasIris()` +
  `isShaderPackInUse()`. Locked to a compatible Iris version range; a mismatch
  degrades to Phase A.

## Risks (all load-bearing; ordered by how likely each is to sink the approach)

1. **Outer-pipeline corruption by the nested render (make-or-break).** The
   nested `renderLevel` nulls the shared `PipelineManager` pipeline at RETURN;
   the outer render still needs it. IP required Iris-internal `PipelineManager`
   changes to save/restore across nested worlds. We must achieve the equivalent
   from our side (save the outer pipeline object, re-`preparePipeline(mainDim)`
   after, verify Iris's outer mixin still functions). If Iris's lifecycle can't
   be made re-entrant from outside, the approach is infeasible on this Iris
   version. **This is Milestone 1.**
2. **Performance.** A full nested render *including the shadow pass, deferred,
   and composite* per doorway per frame can multiply frame cost. Mitigations:
   the existing frustum/idle gates, at most one interior per frame, possibly
   half-res nested targets. May cap how many portals can be shaded at once.
3. **Shadow `WorldRenderer` completeness.** Ours was only ever driven for
   `renderSky` + chunk scheduling; a full `render(...)` needs its chunk render
   dispatch, buffer sources, and entity/BE rendering all valid for a
   non-`MinecraftClient.worldRenderer` instance. Vanilla assumes one active
   `WorldRenderer`; nested use may hit static/singleton assumptions.
4. **Stencil clip inside Iris's gbuffer.** Requires a stencil attachment on
   Iris's gbuffer framebuffer and correct interaction with Iris's own depth. If
   unavailable, fall back to a depth-aperture prepass.
5. **Version-lock / fragility.** Deep Iris internals; pin a supported Iris
   range and degrade to Phase A on mismatch. Even IP documents unfixable portal
   artifacts; parity with IP quality is the ceiling, not perfection.

## Milestones (plan will follow this order; each is independently verifiable in-game)

- **M1 — Feasibility gate: nested render + pipeline save/restore.** Add Iris
  `modCompileOnly`. For ONE doorway under a shaderpack, save outer state, swap
  `client.world`/camera/pipeline, run the shadow `WorldRenderer.render(...)`
  unclipped (interior may splatter across the screen), restore. **Success =
  the outer world still renders correctly afterward AND the interior appears
  shaded by the pack.** Failure here = stop; the approach is infeasible on this
  Iris version and Phase A remains the ceiling.
- **M2 — Doorway clip.** Constrain the nested render to the doorway aperture via
  stencil/depth so it reads as a portal, not a fullscreen overlay.
- **M3 — Camera/parallax correctness.** Match the portal view transform (reuse
  Phase A's `buildPortalView`/rotation math) so the interior aligns with the
  doorway and parallaxes correctly; both door (in→out) and exterior (out→in).
- **M4 — Perf + gating.** Apply frustum/idle gates, one-interior-per-frame cap,
  optional reduced-res nested targets; measure frame cost with N portals.
- **M5 — Coexistence + config + version lock.** Config toggle, Iris version
  range guard, clean degrade to Phase A; verify vanilla and Iris-no-pack paths
  unchanged.

## Out of scope

- Portal-in-portal recursion.
- Matching IP's every edge-case fix; we accept IP-level imperfections.
- Non-Iris shader mods (OptiFine/Oculus are not targets here).

## Testing

Inherently visual/manual (no unit tests for GL). Per milestone, the in-game
matrix: vanilla (unchanged), Iris + no pack (Phase A, unchanged), Iris + BSL/
Complementary (Phase B), across door + exterior variants, watching for: outer
world integrity after the nested render (M1), doorway clipping (M2), alignment
(M3), frame time (M4), and clean fallback (M5).
