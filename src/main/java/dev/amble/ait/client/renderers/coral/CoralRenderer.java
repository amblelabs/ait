package dev.amble.ait.client.renderers.coral;

import net.minecraft.block.BlockState;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.coral.CoralGrowthModel;
import dev.amble.ait.core.blockentities.CoralBlockEntity;
import dev.amble.ait.core.blocks.CoralPlantBlock;

public class CoralRenderer<T extends CoralBlockEntity> implements BlockEntityRenderer<T> {

    public static final Identifier CORAL_GROWTH_TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/coral/coral_growth.png");

    private final CoralGrowthModel coralModel;

    public CoralRenderer(BlockEntityRendererFactory.Context ctx) {
        this.coralModel = new CoralGrowthModel(CoralGrowthModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
            int light, int overlay) {
        matrices.push();
        matrices.translate(0.5, 0, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        BlockState blockState = entity.getCachedState();
        float f = blockState.get(CoralPlantBlock.FACING).asRotation();
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(f));
        ModelPart currentAgeModel = getCurrentAge(blockState.get(CoralPlantBlock.AGE), this.coralModel);
        currentAgeModel.render(matrices,
                vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(CORAL_GROWTH_TEXTURE, true)), light,
                overlay, 1, 1, 1, 1);
        matrices.pop();
    }

    public ModelPart getCurrentAge(int age, CoralGrowthModel coralModel) {
        return switch (age) {
            case 1 -> coralModel.two;
            case 2 -> coralModel.three;
            case 3 -> coralModel.four;
            case 4 -> coralModel.five;
            case 5, 6, 7 -> coralModel.six;
            default -> coralModel.one;
        };
    }
}
