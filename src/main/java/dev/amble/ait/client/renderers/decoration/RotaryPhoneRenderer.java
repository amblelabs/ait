package dev.amble.ait.client.renderers.decoration;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.decoration.RotaryPhoneModel;
import dev.amble.ait.core.blockentities.RotaryPhoneBlockEntity;
import dev.amble.ait.core.blocks.RotaryPhoneBlock;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.AnimationState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class RotaryPhoneRenderer implements BlockEntityRenderer<RotaryPhoneBlockEntity> {

    public static final Identifier TEXTURE = AITMod.id("textures/blockentities/decoration/rotary_phone.png");

    private static final AnimationState IDLE = new AnimationState();
    private static RotaryPhoneModel model;

    public RotaryPhoneRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    private static RotaryPhoneModel model() {
        if (model == null)
            model = new RotaryPhoneModel(RotaryPhoneModel.getTexturedModelData().createModel());

        return model;
    }

    @Override
    public void render(RotaryPhoneBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        RotaryPhoneModel model = model();

        matrices.push();
        matrices.translate(0.5, 1.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(RotaryPhoneBlock.degrees(entity.getCachedState())));

        model.animate(entity.ringState, entity.getAge() + tickDelta, entity.isLifted());
        model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE)), light, overlay, 1, 1, 1, 1);

        matrices.pop();
    }

    public static void renderItem(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        RotaryPhoneModel model = model();

        matrices.push();
        matrices.translate(0.5, 1.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

        model.animate(IDLE, 0, false);
        model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE)), light, overlay, 1, 1, 1, 1);

        matrices.pop();
    }
}
