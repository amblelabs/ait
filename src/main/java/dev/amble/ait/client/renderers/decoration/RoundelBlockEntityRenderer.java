package dev.amble.ait.client.renderers.decoration;

import java.util.List;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;

import dev.amble.ait.core.blockentities.RoundelBlockEntity;
import dev.amble.ait.core.roundels.RoundelPattern;
import dev.amble.ait.core.roundels.RoundelPatterns;

@Environment(value=EnvType.CLIENT)
public class RoundelBlockEntityRenderer
        implements BlockEntityRenderer<RoundelBlockEntity> {
    public RoundelBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(RoundelBlockEntity roundelBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        List<Pair<RoundelPattern, DyeColor>> list = roundelBlockEntity.getPatterns();
        BlockState blockState = roundelBlockEntity.getCachedState();
        matrixStack.push();
        matrixStack.scale(0.6666667f, -0.6666667f, -0.6666667f);
        //RoundelBlockEntityRenderer.renderCanvas(blockState, matrixStack, vertexConsumerProvider, i, j, list);
        matrixStack.pop();
    }

    public static void renderCanvas(BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, List<Pair<RoundelPattern, DyeColor>> patterns) {
        RoundelBlockEntityRenderer.renderCanvas(state, matrices, vertexConsumers, light, overlay, patterns, false);
    }

    public static void renderCanvas(BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, List<Pair<RoundelPattern, DyeColor>> patterns, boolean glint) {
        MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(
            matrices.peek(),
            vertexConsumers.getBuffer(RenderLayer.getEntityNoOutline(RoundelPatterns.BASE.texture())),
            state,
            MinecraftClient.getInstance().getBlockRenderManager().getModel(state),
            1.0f, 1.0f, 1.0f, light, overlay
        );
        for (int i = 0; i < 17 && i < patterns.size(); ++i) {
            Pair<RoundelPattern, DyeColor> pair = patterns.get(i);
            float[] fs = pair.getSecond().getColorComponents();
            MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(
                    matrices.peek(),
                    vertexConsumers.getBuffer(RenderLayer.getEntityNoOutline(pair.getFirst().texture())),
                    state,
                    MinecraftClient.getInstance().getBlockRenderManager().getModel(state),
                    fs[0], fs[1], fs[2], light, overlay
            );
        }
    }
}
