package dev.amble.ait.client.renderers.machines;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.exteriors.StallionExteriorModel;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.core.blockentities.PhoneBoxBlockEntity;
import dev.amble.ait.core.blocks.PhoneBoxBlock;

public class PhoneBoxRenderer<T extends PhoneBoxBlockEntity> implements BlockEntityRenderer<T> {

    private static final Identifier TEXTURE = AITMod.id("textures/blockentities/exteriors/stallion/stallion_default.png");
    private static final Identifier EMISSION = AITMod.id("textures/blockentities/exteriors/stallion/stallion_default_emission.png");
    private StallionExteriorModel stallionExteriorModel;

    public PhoneBoxRenderer(BlockEntityRendererFactory.Context ctx) {
        this.stallionExteriorModel = new StallionExteriorModel(StallionExteriorModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockState state = entity.getCachedState();
        float k = state.get(PhoneBoxBlock.FACING).asRotation();

        matrices.push();
        matrices.translate(0.5f, 1.5f, 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(k));

        this.stallionExteriorModel.getPart().getChild("door").yaw =  -1.35f;
        this.stallionExteriorModel.getPart().getChild("door").getChild("door_two").yaw = 2.65f;

        this.stallionExteriorModel.render(matrices, vertexConsumers.getBuffer(AITRenderLayers.getEntityCutoutNoCull(TEXTURE)),
                light, overlay, 1, 1, 1, 1);
        this.stallionExteriorModel.render(matrices, vertexConsumers.getBuffer(AITRenderLayers.getText(EMISSION)),
                0xf000f0, overlay, 1, 1, 1, 1);

        matrices.pop();
    }

    @Override
    public boolean rendersOutsideBoundingBox(T blockEntity) {
        return BlockEntityRenderer.super.rendersOutsideBoundingBox(blockEntity);
    }

    @Override
    public int getRenderDistance() {
        return BlockEntityRenderer.super.getRenderDistance();
    }

    @Override
    public boolean isInRenderDistance(T blockEntity, Vec3d pos) {
        return BlockEntityRenderer.super.isInRenderDistance(blockEntity, pos);
    }
}
