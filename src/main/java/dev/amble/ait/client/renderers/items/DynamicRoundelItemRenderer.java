package dev.amble.ait.client.renderers.items;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.blockentities.RoundelBlockEntity;
import dev.amble.ait.core.blocks.AbstractRoundelBlock;
import dev.amble.ait.core.roundels.RoundelPatterns;
import dev.amble.ait.core.roundels.RoundelType;
import dev.amble.ait.core.screens.RoundelFabricatorScreen;

public class DynamicRoundelItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    private final RoundelBlockEntity renderRoundel = new RoundelBlockEntity(BlockPos.ORIGIN, AITBlocks.ROUNDEL.getDefaultState());
    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        System.out.println("HELLO");

        if (stack.isEmpty())
            return;

        if (!stack.isOf(AITItems.ROUNDEL))
            return;

        if (!(stack.getItem() instanceof BlockItem blockItem))
            return;

        Block block = blockItem.getBlock();

        if (!(block instanceof AbstractRoundelBlock roundelBlock)) return;

        this.renderRoundel.readFrom(stack, roundelBlock.getColor());

        MinecraftClient.getInstance().getBlockEntityRenderDispatcher()
                .renderEntity(this.renderRoundel, matrices, vertexConsumers, light, overlay);
        matrices.push();
        matrices.translate(0.5, 1f, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));

        ModelPart modelPart = RoundelFabricatorScreen.getTexturedModelData().createModel();
        for (int p = 0; p < 17 && p < this.renderRoundel.getPatterns().size(); ++p) {
            RoundelType pair = this.renderRoundel.getPatterns().get(p);
            float[] fs = pair.color().getColorComponents();
            if (pair.pattern().equals(RoundelPatterns.BASE)) {
                matrices.push();
                modelPart.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(pair.pattern().texture())),
                        pair.emissive() ? 0xf000f0 : light, OverlayTexture.DEFAULT_UV, fs[0], fs[1], fs[2], 1.0f);
                matrices.pop();
                continue;
            }

            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySmoothCutout(pair.pattern().texture()));

            matrices.push();
            matrices.translate(0, p >= 1 ? -0.002 * p : 0, 0);
            matrices.scale(1 + (0.0001f * p), p >= 1 ? 1 + (0.01f * p) : 1 + (0.0001f * p), 1 + (0.0001f * p));
            modelPart.render(matrices, vertexConsumer, pair.emissive() ? 0xf000f0 : light, overlay, fs[0], fs[1], fs[2], 1.0f);
            matrices.pop();
        }
        matrices.pop();
    }
}
