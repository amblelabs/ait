package dev.amble.ait.client.util;

import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import java.util.List;

public class ClientItemUtil {

    private static final Random RANDOM = Random.create(42L);
    private static final Direction[] DIRECTIONS = Direction.values();

    public static void renderBakedItemModel(BakedModel model, int color, int light, int overlay, MatrixStack matrices,
            VertexConsumerProvider provider) {
        renderBakedItemModel(model, color, light, overlay, matrices, ItemRenderer.getDirectItemGlintConsumer(provider,
                TexturedRenderLayers.getEntityTranslucentCull(), true, false));
    }

    public static void renderBakedItemModel(BakedModel model, int color, int light, int overlay, MatrixStack matrices,
            VertexConsumer vertices) {
        for (Direction direction : DIRECTIONS) {
            renderBakedItemQuads(matrices, vertices, model.getQuads(null, direction, RANDOM), color, light, overlay);
        }

        renderBakedItemQuads(matrices, vertices, model.getQuads(null, null, RANDOM), color, light, overlay);
    }

    private static void renderBakedItemQuads(MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads,
            int color, int light, int overlay) {
        MatrixStack.Entry entry = matrices.peek();

        for (BakedQuad quad : quads) {
            int i = quad.hasColor() ? color : -1;

            float f = (float) (i >> 16 & 255) / 255.0F;
            float g = (float) (i >> 8 & 255) / 255.0F;
            float h = (float) (i & 255) / 255.0F;
            vertices.quad(entry, quad, f, g, h, light, overlay);
        }
    }
}
