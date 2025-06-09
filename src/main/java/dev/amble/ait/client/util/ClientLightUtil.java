package dev.amble.ait.client.util;

import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.compat.DependencyChecker;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ClientLightUtil {

    public static <T> void renderEmissivable(boolean emissive, Renderable<T> renderable, @Nullable Identifier texture, VertexConsumerProvider vertices) {
        ClientLightUtil.renderEmissivable(emissive, renderable, texture, texture, vertices);
    }

    public static <T> void renderEmissivable(boolean emissive, Renderable<T> renderable, @Nullable Identifier base,
            @Nullable Identifier glowing, VertexConsumerProvider vertices) {
        if (emissive) {
            ClientLightUtil.renderEmissive(renderable, glowing, vertices);
        } else {
            ClientLightUtil.render(renderable, base, vertices);
        }
    }

    public static <T> void renderEmissive(Renderable<T> renderable, @Nullable Identifier emissive, VertexConsumerProvider vertices) {
        if (emissive == null)
            return;

        RenderLayer layer = DependencyChecker.hasIris()
                ? AITRenderLayers.tardisEmissiveCullZOffset(emissive, true)
                : AITRenderLayers.getTextPolygonOffset(emissive);

        ClientLightUtil.render(renderable, layer, vertices);
    }

    private static <T> void render(Renderable<T> renderable, @Nullable Identifier texture, VertexConsumerProvider vertices) {
        if (texture == null)
            return;

        ClientLightUtil.render(renderable, AITRenderLayers.getEntityTranslucentCull(texture), vertices);
    }

    private static <T> void render(Renderable<T> renderable, RenderLayer layer, VertexConsumerProvider vertices) {
        renderable.render(vertices.getBuffer(layer), 0xf000f0);
    }

    @FunctionalInterface
    public interface Renderable<T> {
        void render(VertexConsumer vertices, int light);
    }
}
