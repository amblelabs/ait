package dev.amble.ait.client.renderers.machines;

import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.machines.UntemperedSchismModel;
import dev.amble.ait.core.blockentities.UntemperedSchismBlockEntity;

public class UntemperedSchismRenderer<T extends UntemperedSchismBlockEntity> implements BlockEntityRenderer<T> {

    public static final Identifier UNTEMPERED_SCHISM_TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/machines/untempered_schism.png"));
    private final UntemperedSchismModel untemperedSchismModel;

    public UntemperedSchismRenderer(BlockEntityRendererFactory.Context ctx) {
        this.untemperedSchismModel = new UntemperedSchismModel(UntemperedSchismModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(UntemperedSchismBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {

        BlockState blockState = entity.getCachedState();

        float f = blockState.get(HorizontalFacingBlock.FACING).asRotation();

        if (MinecraftClient.getInstance().world == null)
            return;

        matrices.push();
        matrices.translate(0.5f, 1.5f, 0.5f);

        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(f));

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

        this.untemperedSchismModel.render(matrices,
                vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(UNTEMPERED_SCHISM_TEXTURE)), light, overlay, 1.0F,
                1.0F, 1.0F, 1.0F);

        matrices.pop();
    }
}
