package dev.amble.ait.client.renderers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.function.BiFunction;

@Environment(EnvType.CLIENT)
public class AITRenderLayers extends RenderLayer {

    private static final BiFunction<Identifier, Boolean, RenderLayer> EMISSIVE_CULL_Z_OFFSET = Util
            .memoize((texture, affectsOutline) -> {
                MultiPhaseParameters multiPhaseParameters = MultiPhaseParameters.builder()
                        .program(ENTITY_CUTOUT_NONULL_OFFSET_Z_PROGRAM)
                        .texture(new RenderPhase.Texture(texture, false, false))
                        .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY).cull(DISABLE_CULLING)
                        .lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).layering(VIEW_OFFSET_Z_LAYERING)
                        .build(affectsOutline);
                return RenderLayer.of("entity_cutout_no_cull_z_offset",
                        VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256,
                        false, true, multiPhaseParameters);
            });

    public static RenderLayer tardisEmissiveCullZOffset(Identifier texture, boolean affectsOutline) {
        return EMISSIVE_CULL_Z_OFFSET.apply(texture, affectsOutline);
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
