package dev.amble.ait.client.models.doors;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.tardis.handler.DoorHandler;

public class RenegadeDoorModel extends DoorModel {
    private final ModelPart renegade;

    public RenegadeDoorModel(ModelPart root) {
        this.renegade = root.getChild("renegade");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData renegade = modelPartData.addChild("renegade", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 26.0F, 0.0F));

        renegade.addChild("walls",
                ModelPartBuilder.create().uv(0, 127).cuboid(8.0F, -38.0F, -8.0F, 3.0F, 36.0F, 4.0F, new Dilation(0.0F))
                        .uv(15, 127).cuboid(7.0F, -37.0F, -7.0F, 5.0F, 35.0F, 2.0F, new Dilation(0.0F)).uv(0, 127)
                        .cuboid(-10.0F, -38.0F, -8.0F, 3.0F, 36.0F, 4.0F, new Dilation(0.0F)).uv(15, 127)
                        .cuboid(-11.0F, -37.0F, -7.0F, 5.0F, 35.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-0.5F, 0.3F, 0.0F));

        renegade.addChild("roof",
                ModelPartBuilder.create().uv(30, 151)
                        .cuboid(-12.5F, -40.75F, -8.5F, 26.0F, 1.0F, 5.0F, new Dilation(0.2F)).uv(30, 158)
                        .cuboid(-12.0F, -39.0F, -8.0F, 25.0F, 1.0F, 4.0F, new Dilation(0.2F)).uv(0, 168)
                        .cuboid(-11.0F, -42.0F, -7.0F, 23.0F, 5.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-0.5F, 0.3F, 0.0F));

        ModelPartData door = renegade.addChild("door",
                ModelPartBuilder.create().uv(39, 114)
                        .cuboid(-0.5F, -35.0F, -0.75F, 13.0F, 35.0F, 1.0F, new Dilation(0.0F)).uv(122, 0)
                        .cuboid(0.0F, -34.5F, -1.0F, 12.0F, 34.0F, 0.0F, new Dilation(0.0F)).uv(122, 0)
                        .cuboid(0.0F, -34.5F, 0.5F, 12.0F, 34.0F, 0.0F, new Dilation(0.0F)).uv(123, 98)
                        .cuboid(1.0F, -27.5F, -1.05F, 10.0F, 20.0F, 1.0F, new Dilation(-0.0499F)).uv(70, 0)
                        .cuboid(1.0F, -22.5F, -1.3F, 10.0F, 10.0F, 1.0F, new Dilation(-0.05F)),
                ModelTransform.pivot(-6.0F, -1.7F, -6.0F));

        door.addChild("cube_r1",
                ModelPartBuilder.create().uv(42, 77)
                        .cuboid(3.55F, 3.55F, -0.75F, 7.0F, 7.0F, 1.0F, new Dilation(0.001F)).uv(25, 77)
                        .cuboid(0.0F, 0.0F, -1.0F, 7.0F, 7.0F, 1.0F, new Dilation(0.001F)).uv(66, 19)
                        .cuboid(-3.5F, -3.5F, -1.25F, 7.0F, 7.0F, 1.0F, new Dilation(0.0F)).uv(0, 19)
                        .cuboid(-7.0F, -7.0F, -1.0F, 7.0F, 7.0F, 1.0F, new Dilation(0.001F)).uv(0, 0)
                        .cuboid(-10.55F, -10.55F, -0.75F, 7.0F, 7.0F, 1.0F, new Dilation(0.001F)),
                ModelTransform.of(6.0F, -17.5F, -0.25F, 3.1416F, 0.0F, 2.3562F));

        door.addChild("cube_r2",
                ModelPartBuilder.create().uv(70, 0)
                        .cuboid(-5.0F, -5.0F, -1.05F, 10.0F, 10.0F, 1.0F, new Dilation(-0.05F)).uv(123, 98)
                        .cuboid(-5.0F, -10.0F, -0.8F, 10.0F, 20.0F, 1.0F, new Dilation(-0.0499F)),
                ModelTransform.of(6.0F, -17.5F, -0.25F, -3.1416F, 0.0F, 3.1416F));

        door.addChild("cube_r3",
                ModelPartBuilder.create().uv(42, 77)
                        .cuboid(3.55F, 3.55F, -0.75F, 7.0F, 7.0F, 1.0F, new Dilation(0.001F)).uv(0, 19)
                        .cuboid(-7.0F, -7.0F, -1.0F, 7.0F, 7.0F, 1.0F, new Dilation(0.001F)).uv(25, 77)
                        .cuboid(0.0F, 0.0F, -1.0F, 7.0F, 7.0F, 1.0F, new Dilation(0.001F)).uv(66, 19)
                        .cuboid(-3.5F, -3.5F, -1.25F, 7.0F, 7.0F, 1.0F, new Dilation(0.0F)).uv(0, 0)
                        .cuboid(-10.55F, -10.55F, -0.75F, 7.0F, 7.0F, 1.0F, new Dilation(0.001F)),
                ModelTransform.of(6.0F, -17.5F, -0.25F, 0.0F, 0.0F, 0.7854F));

        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        renegade.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, AbstractLinkableBlockEntity linkableBlockEntity, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        matrices.push();
        matrices.translate(0, -1.5f, 0);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180f));

        if (!AITModClient.CONFIG.animateDoors) {
            renegade.getChild("door").yaw = tardis.door().isOpen() ? 1.75f : 0f;
        } else {
            float maxRot = 90f;
            renegade.getChild("door").yaw = (float) Math.toRadians(maxRot*tardis.door().getLeftRot());
        }
        super.renderWithAnimations(tardis, linkableBlockEntity, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);

        matrices.pop();
    }

    @Override
    public ModelPart getPart() {
        return renegade;
    }

    @Override
    public Animation getAnimationForDoorState(DoorHandler.AnimationDoorState state) {
        return Animation.Builder.create(0).build();
    }
}
