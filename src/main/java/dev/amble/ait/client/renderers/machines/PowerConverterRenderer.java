package dev.amble.ait.client.renderers.machines;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.machines.PowerConverterModel;
import dev.amble.ait.core.blocks.PlaqueBlock;
import dev.amble.ait.core.blocks.PowerConverterBlock;

public class PowerConverterRenderer<T extends PowerConverterBlock.BlockEntity> implements BlockEntityRenderer<T> {

    public static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/machines/power_converter.png"));;
    private final PowerConverterModel model;

    public PowerConverterRenderer(BlockEntityRendererFactory.Context ctx) {
        this.model = new PowerConverterModel();
    }

    @Override
    public void render(PowerConverterBlock.BlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockState blockState = entity.getCachedState();

        matrices.push();
        matrices.scale(1.35f, 1.35f, 1.35f);
        matrices.translate(0.38, 1.5f, 0.38);

        Direction k = blockState.get(PlaqueBlock.FACING);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(k.asRotation()));

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

        this.model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE)),
                light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        matrices.pop();
    }
}
