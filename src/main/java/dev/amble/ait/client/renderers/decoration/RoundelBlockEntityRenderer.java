package dev.amble.ait.client.renderers.decoration;

import java.util.List;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.core.blockentities.RoundelBlockEntity;
import dev.amble.ait.core.blocks.RoundelBlock;
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
        ModelPartData cube = modelPartData.addChild("cube", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 8.0F, 0.0F));
        return TexturedModelData.of(modelData, 16, 16);
    }

    @Override
    public void render(RoundelBlockEntity roundelBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        List<Pair<RoundelPattern, DyeColor>> list = roundelBlockEntity.getPatterns();
        matrixStack.push();
        matrixStack.translate(0.5, 1f, 0.5);
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(roundelBlockEntity.getCachedState().get(RoundelBlock.FACING).asRotation()));
        RoundelBlockEntityRenderer.renderBlock(roundelBlockEntity, this.cube, matrixStack, vertexConsumerProvider, i, j, list);
        matrixStack.pop();
    }

    public static void renderBlock(RoundelBlockEntity roundelBlockEntity, ModelPart modelPart, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, List<Pair<RoundelPattern, DyeColor>> patterns) {
        //modelPart.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(patterns.get(0).getFirst().texture())), light, overlay, 1f, 1f, 1f, 1.0f);
        for (int i = 0; i < 17 && i < patterns.size(); ++i) {
            Pair<RoundelPattern, DyeColor> pair = patterns.get(i);
            float[] fs = pair.getSecond().getColorComponents();
            if (pair.getFirst().equals(RoundelPatterns.BASE)) {
                Identifier dynamicTex = pair.getFirst().usesDynamicTexture() && roundelBlockEntity.getDynamicTextureBlockState() != null ?
                        Registries.BLOCK.getId(roundelBlockEntity.getDynamicTextureBlockState().getBlock()).withPrefixedPath("textures/block").withSuffixedPath(".png") : pair.getFirst().texture();
                        pair.getFirst().texture();
                modelPart.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(dynamicTex)),
                        pair.getFirst().emissive() ? 0xf000f0 : light, overlay, fs[0], fs[1], fs[2], 1.0f);
                continue;
            }

            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(AITRenderLayers.tardisEmissiveCullZOffset(pair.getFirst().texture(), false));

            matrices.push();
            matrices.translate(0, i >= 1 ? -0.001 * i : 0, 0);
            matrices.scale(1 + (0.0001f * i), i >= 1 ? 1 + (0.001f * i) : 1 + (0.0001f * i), 1 + (0.0001f * i));
            modelPart.render(matrices, vertexConsumer, pair.getFirst().emissive() ? 0xf000f0 : light, overlay, fs[0], fs[1], fs[2], 1.0f);
            matrices.pop();
        }
    }
}
