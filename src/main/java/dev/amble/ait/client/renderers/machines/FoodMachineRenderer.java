package dev.amble.ait.client.renderers.machines;


import dev.amble.ait.client.models.machines.FoodMachineModel;
import dev.amble.ait.core.blockentities.FoodMachineBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.AITMod;
import net.minecraft.util.math.RotationPropertyHelper;

public class FoodMachineRenderer<T extends FoodMachineBlockEntity> implements BlockEntityRenderer<T> {

    public static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/machines/food_machine.png");

    private final FoodMachineModel foodMachineModel;



    public FoodMachineRenderer(BlockEntityRendererFactory.Context ctx) {
        this.foodMachineModel = new FoodMachineModel(FoodMachineModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(FoodMachineBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockState blockState = entity.getCachedState();
        int k = blockState.get(SkullBlock.ROTATION);
        float h = 180.0f - RotationPropertyHelper.toDegrees(k);


        matrices.push();
        matrices.scale(1f, 1f, 1f);


        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        matrices.translate(0.5, -1.5f, -0.5);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(h));

        this.foodMachineModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE)),
                light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        matrices.pop();
    }

}
