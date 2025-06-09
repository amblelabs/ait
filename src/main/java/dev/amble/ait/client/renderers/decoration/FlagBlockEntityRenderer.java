package dev.amble.ait.client.renderers.decoration;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.decoration.FlagModel;
import dev.amble.ait.core.blockentities.FlagBlockEntity;
import dev.amble.ait.core.blocks.FlagBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class FlagBlockEntityRenderer<T extends FlagBlockEntity> implements BlockEntityRenderer<T> {
    public static final Identifier FLAG_TEXTURE = AITMod.id("textures/blockentities/decoration/us_flag.png");

    public FlagModel flagModel = new FlagModel(FlagModel.getTexturedModelData().createModel());


    public FlagBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {

    }

    @Override
    public void render(FlagBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5, 1.5f, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        float k = entity.getCachedState().get(FlagBlock.FACING).asRotation();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(k));
        flagModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(FLAG_TEXTURE)), light, overlay, 1, 1, 1, 1);
        matrices.pop();
    }
}
