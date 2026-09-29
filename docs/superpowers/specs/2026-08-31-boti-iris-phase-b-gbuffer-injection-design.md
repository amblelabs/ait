# BOTI Phase B — Iris Shading via Gbuffer Injection

Date: 2026-08-31
Branch: `feat/bigger-on-the-inside`
Status: Approved (validated by in-game probe). Supersedes the nested-render design
`2026-08-31-boti-iris-phase-b-design.md`.

## Goal

Make the TARDIS BOTI portal interior be shaded by the active Iris shaderpack, by
drawing the interior geometry into Iris's **main gbuffer during the opaque pass**
so Iris's own deferred + composite passes light it as part of the scene — for the
shaderpack path only, with the robust Phase A composite as the non-shader floor.

## What the probes established (facts, not assumptions)

- **afbo-post-composite (Phase A):** works, but unshaded — we paint over Iris's
  finished image at `END`.
- **Nested `WorldRenderer.render()`:** double-composites the main world (Iris's
  nested final pass writes to the main framebuffer). Rejected.
- **Gbuffer injection at `AFTER_TRANSLUCENT`:** nothing — that event is
  *post-deferred/composite*.
- **Gbuffer injection at `AFTER_ENTITIES` + `setPhase(TERRAIN_SOLID)`
  (validated):** the interior terrain drew into the live gbuffer and came out
  **shaded** ("a little shaded") by the pack, with **no doubling**. `AFTER_ENTITIES`
  runs while Iris's gbuffer is bound and before its deferred pass. This is the
  approach.

## Architecture

### Two paths, selected per frame

- `!isShaderPackInUse()` → **Phase A** (unchanged): afbo composite at `END`.
- `isShaderPackInUse()` → **gbuffer injection**: draw the interior into Iris's
  gbuffer during the opaque pass; Iris's deferred + composite shade it.

### Injection

- Draw the interior's baked section VBOs (already in Iris's extended TERRAIN
  format when a pack is active — Iris's `MixinBufferBuilder` swaps `BLOCK`→
  `TERRAIN` on every `begin()`) into the currently-bound gbuffer framebuffer,
  with `WorldRenderingPipeline.setPhase(TERRAIN_*)` around each layer so Iris
  binds `gbuffers_terrain`.
- Use the portal view/projection (reuse Phase A's transform math).
- **Clip to the doorway aperture** so it's a portal, not a fullscreen splatter.
- Iris (`modCompileOnly`) is called only behind `hasIris()`+`isShaderPackInUse()`.

### Why no doubling

We only add draws to the existing opaque pass — we never run a nested
`finalizeLevelRendering`, so composite/final run exactly once, over the whole
scene (including our injected interior).

## Known unknowns / risks (ordered)

1. **Stencil in Iris's gbuffer (M1).** Doorway clipping needs a stencil (or
   depth) mask in the *bound gbuffer* framebuffer, not our afbo. Whether Iris's
   gbuffer has a usable stencil attachment at `AFTER_ENTITIES` is the first thing
   to verify; if not, fall back to a depth-aperture prepass.
2. **Shading fullness.** `AFTER_ENTITIES` + solid-only gave partial shading.
   Fuller shading may need injecting during `renderChunkLayer` (earlier, catches
   all deferred passes), and adding cutout/cutout-mipped/translucent layers, and
   the shadow pass (for self-shadows). Solid-at-`AFTER_ENTITIES` is the floor.
3. **Both view directions.** Inside→out works via `getCurrentTardis()`; outside→in
   must drive off the exterior/door render queue.
4. **Block-sensitive attributes.** `mc_Entity`/`at_midBlock` may be imperfect for
   off-thread-baked VBOs → POM/material-id effects may be off; normal-mapping
   likely OK. Accept as an imperfection.
5. **Version-lock.** Iris internals (`setPhase`, pipeline); pin a supported range,
   degrade to Phase A on mismatch.

## Milestones (each independently verifiable in-game)

- **M1 — Doorway clip (turn the probe into a real, clipped injection).** Verify
  stencil availability in the bound gbuffer; mask the injection to the doorway
  aperture using the door's portal quad; draw solid+cutout+cutout-mipped layers
  (not just solid) clipped. Success = interior renders **only inside the doorway**,
  shaded, no splatter, no doubling, inside→out view. Replaces the throwaway probe
  with a real (still shader-path-only) injector.
- **M2 — Shading fullness.** If M1's shading is still weak, move injection to a
  `renderChunkLayer`-adjacent mixin and/or add translucent + shadow-pass
  participation; compare quality.
- **M3 — Exterior/out→in + variants.** Drive off the exterior/door/rift/painting
  queues; both view directions.
- **M4 — Coexistence, config, cleanup.** Config toggle; Iris version guard; remove
  the leftover Phase A `BOTI-DIAG` diagnostic log; clean degrade to Phase A.

## Out of scope

Portal-in-portal recursion; interior lit by its *own* dimension's sun/shadows
(that was the rejected nested-render path — here the interior is lit by the main
world's deferred lighting); non-Iris shader mods.

## Testing

Manual/visual per milestone: vanilla (unchanged), Iris no-pack (Phase A
unchanged), Iris + BSL/Complementary (injection), door + exterior variants,
watching for clip correctness (M1), shading quality (M2), variant coverage (M3),
and clean fallback + no leftover diagnostics (M4).
