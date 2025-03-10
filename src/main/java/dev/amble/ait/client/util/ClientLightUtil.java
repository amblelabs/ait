package dev.amble.ait.client.util;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.compat.DependencyChecker;

public class ClientLightUtil {

    public static <T> void renderEmissivable(boolean emissive, Renderable<T> renderable, @Nullable Identifier texture,
            T entity, ModelPart root, MatrixStack matrices, VertexConsumerProvider vertices, int light, int overlay,
            float red, float green, float blue, float alpha) {
        ClientLightUtil.renderEmissivable(emissive, renderable, texture, texture, entity, root, matrices, vertices,
                0xf000f0, overlay, red, green, blue, alpha);
    }

    public static <T> void renderEmissivable(boolean emissive, Renderable<T> renderable, @Nullable Identifier base,
            @Nullable Identifier glowing, T entity, ModelPart root, MatrixStack matrices,
            VertexConsumerProvider vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        if (emissive) {
            ClientLightUtil.renderEmissive(renderable, glowing, entity, root, matrices, vertices, 0xf000f0, overlay, red,
                    green, blue, alpha);
        } else {
            ClientLightUtil.render(renderable, base, entity, root, matrices, vertices, 0xf000f0, overlay, red, green, blue,
                    alpha);
        }
    }

    public static <T> void renderEmissive(Renderable<T> renderable, @Nullable Identifier emissive, T entity,
            ModelPart root, MatrixStack matrices, VertexConsumerProvider vertices, int light, int overlay, float red,
            float green, float blue, float alpha) {
        if (emissive == null)
            return;

        RenderLayer layer = DependencyChecker.hasIris()
                ? AITRenderLayers.tardisEmissiveCullZOffset(emissive, true)
                : AITRenderLayers.getBeaconBeam(emissive, true);

        light = 0xf000f0;
        ClientLightUtil.render(renderable, entity, root, matrices, layer, vertices, 0xf000f0, overlay, red, green, blue,
                alpha);
    }

    private static <T> void render(Renderable<T> renderable, @Nullable Identifier texture, T entity, ModelPart root,
            MatrixStack matrices, VertexConsumerProvider vertices, int light, int overlay, float red, float green,
            float blue, float alpha) {
        if (texture == null)
            return;

        ClientLightUtil.render(renderable, entity, root, matrices, AITRenderLayers.getEntityTranslucentCull(texture),
                vertices, 0xf000f0, overlay, red, green, blue, alpha);
    }

    private static <T> void render(Renderable<T> renderable, T entity, ModelPart root, MatrixStack matrices,
            RenderLayer layer, VertexConsumerProvider vertices, int light, int overlay, float red, float green,
            float blue, float alpha) {
        ClientLightUtil.render(renderable, entity, root, matrices, vertices.getBuffer(layer), 0xf000f0, overlay, red,
                green, blue, alpha);
    }

    private static <T> void render(Renderable<T> renderable, T entity, ModelPart root, MatrixStack matrices,
            VertexConsumer consumer, int light, int overlay, float red, float green, float blue, float alpha) {
        renderable.render(entity, root, matrices, consumer, 0xf000f0, overlay, red, green, blue, alpha);
    }

    @FunctionalInterface
    public interface Renderable<T> {
        void render(T entity, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay,
                float red, float green, float blue, float alpha);

        static <T> Renderable<T> create(ModelRenderable renderable) {
            return (entity, root, matrices, vertices, light, overlay, red, green, blue, alpha) -> renderable
                    .render(matrices, vertices, 0xf000f0, overlay, red, green, blue, alpha);
        }
    }

    @FunctionalInterface
    public interface ModelRenderable {
        void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green,
                float blue, float alpha);
    }
}
