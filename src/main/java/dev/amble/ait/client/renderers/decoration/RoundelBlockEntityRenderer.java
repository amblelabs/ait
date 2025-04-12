package dev.amble.ait.client.renderers.decoration;

import java.util.List;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;

import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.blockentities.RoundelBlockEntity;
import dev.amble.ait.core.roundels.RoundelPattern;
import dev.amble.ait.core.roundels.RoundelPatterns;

@Environment(value=EnvType.CLIENT)
public class RoundelBlockEntityRenderer
        implements BlockEntityRenderer<RoundelBlockEntity> {
    private final ModelPart cube;
    public RoundelBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.cube = getTexturedModelData().createModel();
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData cube = modelPartData.addChild("cube", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        return TexturedModelData.of(modelData, 16, 16);
    }

    @Override
    public void render(RoundelBlockEntity roundelBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        List<Pair<RoundelPattern, DyeColor>> list = roundelBlockEntity.getPatterns();
        matrixStack.push();
        matrixStack.translate(0, 1, 1);
        matrixStack.scale(1f, -1f, -1f);
        RoundelBlockEntityRenderer.renderBlock(this.cube, matrixStack, vertexConsumerProvider, i, j, list);
        matrixStack.pop();
    }

    public static void renderBlock(ModelPart modelPart, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, List<Pair<RoundelPattern, DyeColor>> patterns) {
        RoundelBlockEntityRenderer.renderBlock(modelPart, matrices, vertexConsumers, light, overlay, patterns, false);
    }

    public static void renderBlock(ModelPart modelPart, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, List<Pair<RoundelPattern, DyeColor>> patterns, boolean glint) {
        /*MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(
                matrices.peek(),
                vertexConsumers.getBuffer(RenderLayer.getEntityCutout(RoundelPatterns.BASE.texture())),
                AITBlocks.ROUNDEL.getDefaultState(),
                MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(AITBlocks.ROUNDEL.getDefaultState()),
                1, 1, 1, light, overlay);*/

        if (patterns.contains(Pair.of(RoundelPatterns.BASE, DyeColor.WHITE))) {
            VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(RoundelPatterns.BASE.texture()));
            for (int k = 0; k <= 6; k++) {
                for (BakedQuad q : MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(
                        AITBlocks.ROUNDEL.getDefaultState()).getQuads(null, ModelHelper
                        .faceFromIndex(k), MinecraftClient.getInstance().world.getRandom())) {
                    vertices.quad(matrices.peek(), q, 1, 1, 1, light, overlay);
                }
            }
        }

        /*modelPart.render(matrices,
                vertexConsumers.getBuffer(RenderLayer.getEntitySolid(RoundelPatterns.BASE.texture())),
                light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);*/
        for (int i = 0; i < 17 && i < patterns.size(); ++i) {
            Pair<RoundelPattern, DyeColor> pair = patterns.get(i);
            float[] fs = pair.getSecond().getColorComponents();

            /*MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(
                    matrices.peek(),
                    vertexConsumers.getBuffer(RenderLayer.getEntityCutout(pair.getFirst().texture())),
                    AITBlocks.ROUNDEL.getDefaultState(),
                    MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(AITBlocks.ROUNDEL.getDefaultState()),
                    fs[0], fs[1], fs[2], light, overlay);*/

            VertexConsumer verticio = vertexConsumers.getBuffer(RenderLayer.getEntityNoOutline(pair.getFirst().texture()));
            for (int k = 0; k <= 6; k++) {
                for (BakedQuad q : MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(AITBlocks.ROUNDEL.getDefaultState()).getQuads(null, ModelHelper
                        .faceFromIndex(k), MinecraftClient.getInstance().world.getRandom())) {
                    matrices.push();
                    verticio.quad(matrices.peek(), q, fs[0], fs[1], fs[2], light, overlay);
                    matrices.pop();
                }
            }

            /*modelPart.render(matrices,
                    vertexConsumers.getBuffer(RenderLayer.getEntityCutout(pair.getFirst().texture())),
                    pair.getFirst().emissive() ? 0xf000f0 : light, overlay, fs[0], fs[1], fs[2], 1.0f);*/
        }
    }
}
