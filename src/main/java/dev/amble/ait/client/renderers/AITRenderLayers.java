package dev.amble.ait.client.renderers;

import java.util.Set;
import java.util.function.Function;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;


@Environment(EnvType.CLIENT)
public class AITRenderLayers extends RenderLayer {

    private static RenderLayer emissive(Identifier texture, boolean sorted) {
        RenderPhase.Texture texture2 = new RenderPhase.Texture(texture, false, false);
        MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
                .program(RenderPhase.EYES_PROGRAM)
                .texture(texture2)
                .cull(DISABLE_CULLING)
                .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                .lightmap(ENABLE_LIGHTMAP)
                .writeMaskState(COLOR_MASK)
                .depthTest(RenderPhase.LEQUAL_DEPTH_TEST)
                .build(false);

        // The last flag is what makes RenderLayer.draw hand the buffer a sorter, and that is more than
        // a sort: BufferBuilder.setSorter also builds a Vector3f per quad for the primitive centres,
        // and build() then writes an explicit index buffer instead of reusing the shared sequential
        // one. Copper's emission pass is around 6800 quads, so per flush that is 6800 allocations, a
        // 6800-element sort and about 41000 index writes.
        //
        // It is separate from the blend mode set above. The layer never writes depth (COLOR_MASK), so
        // ordering can only matter where emissive geometry overlaps emissive geometry inside one batch.
        //
        // Mostly the textures make that moot: 106 of the mod's 113 emission textures are strictly
        // binary alpha, where SRC_ALPHA blending is order-independent. Seven are not, and one is not
        // marginal: hourglass_default_emission.png carries 1372 partial texels including a flat run of
        // 768 at alpha 100, which is deliberate semi-transparent glow. Crystalline (64 texels),
        // steam_copper (24), steam_playpal (2) and bookshelf_default (34) are the rest. On those, a
        // glow-over-glow overlap composites in submission order here where it used to composite in
        // depth order.
        //
        // Vertex alpha is a separate exposure and is not covered by the texture argument at all, which
        // is what the sorted layer below is for.
        return RenderLayer.of(sorted ? "emissive_cull_z_offset_sorted" : "emissive_cull_z_offset_unsorted",
                VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256,
                false, sorted, multiPhaseParameters);
    }

    /**
     * Emission textures whose partial alpha is large enough that draw order is visible, so they get the
     * sorted layer whoever asks for them. Sortedness is a property of the texture here, not of the call
     * site, which keeps one texture mapped to one layer object.
     *
     * <p>Only hourglass qualifies. Its 1372 partial texels include a flat run of 768 at alpha 100,
     * which is deliberate semi-transparent glow rather than an edge ramp. The other six partial-alpha
     * emission textures are accepted unsorted: crystalline and its two recolours have 64 texels each at
     * alpha 199, bookshelf_default 34, steam_copper 24 at alpha 4, steam_playpal 2. Crystalline was
     * checked against a sorted arm in game and showed nothing above its own animation noise.
     */
    private static final Set<Identifier> SORT_SENSITIVE_EMISSION = Set.of(
            new Identifier("ait", "textures/blockentities/consoles/hourglass_default_emission.png"));

    private static final Function<Identifier, RenderLayer> EMISSIVE_SORTED = Util
            .memoize(texture -> emissive(texture, true));

    private static final Function<Identifier, RenderLayer> EMISSIVE_UNSORTED = Util
            .memoize(texture -> emissive(texture, false));

    /**
     * The emissive layer for geometry drawn at full vertex alpha, which is every caller but two.
     *
     * <p>Returns the sorted layer anyway for the textures in {@link #SORT_SENSITIVE_EMISSION}.
     *
     * <p>Unsorted. At alpha 1 the blend leaves nothing for the ordering to change except where opaque
     * glow overlaps opaque glow, and the sort is the single most expensive thing the console's
     * emission pass does. Measured on Copper, interior, landed, interleaved arms inside one client
     * session: the {@code monitor} zone went from 1.371-1.404 ms to 0.743-0.830 ms, disjoint ranges,
     * while the two emission zones, which are negative controls this cannot affect, moved by under
     * 0.06 ms. Frame time moved too but its per-rep ranges overlap at n=3, so it is not claimed.
     *
     * <p>The cost lands in {@code monitor} only when monitor text is enabled and the variant overrides
     * {@code renderMonitorText}: that call is what next asks {@code Immediate} for a buffer, and the
     * flush of this layer is billed to whichever zone is open at the time. With the text off it moves
     * to {@code sonic_port} or later. It is the same work either way.
     */
    public static RenderLayer tardisEmissiveCullZOffset(Identifier texture) {
        return SORT_SENSITIVE_EMISSION.contains(texture)
                ? EMISSIVE_SORTED.apply(texture)
                : EMISSIVE_UNSORTED.apply(texture);
    }

    /**
     * The emissive layer for geometry drawn at partial vertex alpha, where draw order is visible.
     *
     * <p>Two callers. {@code TardisStar} draws two nested star models into one batch, the outer at
     * alpha 0.5 and the inner at alpha 1, with culling disabled so both faces of both shells are
     * submitted; unsorted, the opaque core would land last and paint over the shell that is supposed
     * to veil it. {@code ExteriorRenderer} draws the emission at the demat and remat fade alpha, which
     * sweeps continuously through the partial range on every takeoff and landing.
     *
     * <p>The star is 24 quads, so sorting it costs nothing worth measuring. The exterior is around 342,
     * and it pays the sort on every landed TARDIS even though only demat and remat need it. That is
     * deliberate: picking per frame on the current alpha would hand out two different layer objects for
     * one texture, and the moment one TARDIS is fading while another sits landed that alternates every
     * frame, which costs more than the sort it saves.
     *
     * <p>Exterior emission identifiers already reach both layers, because {@code ExteriorRenderer} is
     * here while {@code DoorRenderer}, {@code FlightTardisRenderer}, {@code FallingTardisRenderer},
     * {@code SnowGlobeRenderer} and the BOTI paths draw the same textures unsorted. {@code Immediate}
     * keys buffers on layer identity, so that costs one extra flush when both appear in a frame. It is
     * accepted: no {@code AITRenderLayers} layer has a dedicated buffer in {@code BufferBuilderStorage},
     * so every switch between them already flushes. Console emissions never collide, being a separate
     * texture directory and registry.
     */
    public static RenderLayer tardisEmissiveCullZOffsetSorted(Identifier texture) {
        return EMISSIVE_SORTED.apply(texture);
    }

    private AITRenderLayers(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode,
                            int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction,
                            Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    public static RenderLayer getBoti() {
        MultiPhaseParameters parameters = MultiPhaseParameters.builder()
                .texture(RenderPhase.MIPMAP_BLOCK_ATLAS_TEXTURE)
                .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                .layering(RenderPhase.NO_LAYERING)
                .build(false);
        return RenderLayer.of("boti", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
                VertexFormat.DrawMode.QUADS, 256, false, true, parameters);
    }

    public static RenderLayer getBotiInteriorEmission(Identifier texture) {
        MultiPhaseParameters parameters = MultiPhaseParameters.builder()
                .texture(new Texture(texture, false, false))
                .program(ENTITY_CUTOUT_NONULL_OFFSET_Z_PROGRAM)
                .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                .cull(DISABLE_CULLING)
                .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                .lightmap(ENABLE_LIGHTMAP)
                .overlay(ENABLE_OVERLAY_COLOR)
                .depthTest(RenderPhase.LEQUAL_DEPTH_TEST)
                .build(false);
        return RenderLayer.of("boti_interior_emission", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                VertexFormat.DrawMode.QUADS, 256, false, true, parameters);
    }

    public static RenderLayer getBotiInterior(Identifier texture) {
        // Not memoized, unlike EMISSIVE_CULL_Z_OFFSET above. Counted so the per-frame allocation rate is
        // visible rather than inferred.
        MinecraftClient.getInstance().getProfiler().visit("ait_renderlayer_alloc");

        MultiPhaseParameters parameters = MultiPhaseParameters.builder()
                .texture(new Texture(texture, false, false))
                .program(ENTITY_CUTOUT_NONULL_PROGRAM)
                .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                .cull(ENABLE_CULLING)
                .layering(RenderPhase.NO_LAYERING)
                .lightmap(ENABLE_LIGHTMAP)
                .overlay(ENABLE_OVERLAY_COLOR)
                .depthTest(RenderPhase.LEQUAL_DEPTH_TEST)
                .build(false);
        return RenderLayer.of("boti_interior", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                VertexFormat.DrawMode.QUADS, 256, false, true, parameters);
    }
}
