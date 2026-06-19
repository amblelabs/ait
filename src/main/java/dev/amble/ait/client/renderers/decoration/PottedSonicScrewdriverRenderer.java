package dev.amble.ait.client.renderers.decoration;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.core.blockentities.PottedSonicScrewdriverBlockEntity;

public class PottedSonicScrewdriverRenderer implements BlockEntityRenderer<PottedSonicScrewdriverBlockEntity> {
    private static final float SCALE = 0.5f;
    private static final float BASE_Y = 4f / 16f;
    private static final float LIFT = 0.531f;
    private static final float[][] SLOTS = {{ 0.00f,  0.00f,  10f, 0f,   0f },
            { -0.07f, -0.04f, 60f, 1f, -20f }, { 0.07f,  0.04f, -40f, 1f,  20f }, {0.03f,  0.07f, 150f, 0f, -20f }, { -0.05f, 0.06f, -110f, 0f,  20f }, { 0.05f, -0.06f, 100f, 1f, -15f }};

    public PottedSonicScrewdriverRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(PottedSonicScrewdriverBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        List<ItemStack> sonics = entity.getSonics();
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        for (int i = 0; i < sonics.size(); i++) {
            ItemStack stack = sonics.get(i);
            float[] slot = SLOTS[Math.min(i, SLOTS.length - 1)];
            matrices.push();
            matrices.translate(0.5f + slot[0], BASE_Y, 0.5f + slot[1]);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(slot[2]));
            if (slot[4] != 0f) {
                if (slot[3] != 0f)
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(slot[4]));
                else
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(slot[4]));
            }
            matrices.scale(SCALE, SCALE, SCALE);
            matrices.translate(0f, LIFT, 0f);
            itemRenderer.renderItem(stack, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
            matrices.pop();
        }
    }
}
