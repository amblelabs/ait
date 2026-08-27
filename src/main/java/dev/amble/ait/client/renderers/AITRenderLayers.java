package dev.amble.ait.client.renderers;

import java.util.function.BiFunction;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import dev.amble.ait.client.util.ClientPerfFlags;
import dev.amble.ait.client.util.ClientProfiling;

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

        // The last flag is what makes RenderLayer.draw sort every quad on the CPU, which for a large
        // console is thousands of primitive centres and a full sort on each flush. It is separate from
        // the blend mode, set above, so dropping it keeps the look and skips the sort. This layer never
        // writes depth (COLOR_MASK), so the ordering only matters where glow overlaps glow.
        return RenderLayer.of("emissive_cull_z_offset" + (sorted ? "" : "_unsorted"),
                VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256,
                false, sorted, multiPhaseParameters);
    }

    private static final BiFunction<Identifier, Boolean, RenderLayer> EMISSIVE_SORTED = Util
            .memoize((texture, affectsOutline) -> emissive(texture, true));

    private static final BiFunction<Identifier, Boolean, RenderLayer> EMISSIVE_UNSORTED = Util
            .memoize((texture, affectsOutline) -> emissive(texture, false));

    public static RenderLayer tardisEmissiveCullZOffset(Identifier texture, boolean affectsOutline) {
        return ClientPerfFlags.get("sortEmissive", true)
                ? EMISSIVE_SORTED.apply(texture, affectsOutline)
                : EMISSIVE_UNSORTED.apply(texture, affectsOutline);
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
        ClientProfiling.count("ait_renderlayer_alloc");

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
