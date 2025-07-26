package dev.amble.ait.client.models.doors;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.tardis.handler.DoorHandler;

public class CapsuleDoorModel extends DoorModel {
    private final ModelPart body;

    public CapsuleDoorModel(ModelPart root) {
        super(RenderLayer::getEntityCutoutNoCull);
        this.body = root.getChild("body");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 3.0F, -15.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData top = body.addChild("top",
                ModelPartBuilder.create().uv(87, 15)
                        .cuboid(-12.0F, -36.1F, -12.0F, 24.0F, 0.0F, 10.0F, new Dilation(0.0F)).uv(15, 40)
                        .cuboid(-12.0F, -33.89F, -12.0F, 24.0F, 0.0F, 9.0F, new Dilation(0.0F)).uv(61, 114)
                        .cuboid(-4.9706F, -36.0F, -12.0F, 9.9411F, 2.0F, 8.0F, new Dilation(0.001F)),
                ModelTransform.pivot(0.0F, 21.0F, 0.0F));

        top.addChild("octagon_r1", ModelPartBuilder.create().uv(125, 35).cuboid(-4.9706F, -2.0F, -12.0F, 3.0F, 2.0F,
                24.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -34.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        top.addChild("octagon_r2", ModelPartBuilder.create().uv(20, 101).cuboid(-4.9706F, -2.0F, -12.0F, 9.9411F, 2.0F,
                4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -34.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        top.addChild("octagon_r3", ModelPartBuilder.create().uv(93, 85).cuboid(-4.9706F, -2.0F, -12.0F, 9.9411F, 2.0F,
                4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -34.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData middle = body.addChild("middle", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 21.0F, 0.0F));

        middle.addChild("octagon_r4", ModelPartBuilder.create().uv(48, 135).cuboid(-2.2365F, -34.0F, 9.5F, 8.0F, 32.0F,
                2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, -0.7854F, 3.1416F));

        middle.addChild("octagon_r5", ModelPartBuilder.create().uv(142, 128).cuboid(-5.7635F, -34.0F, 9.5F, 8.0F, 32.0F,
                2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 0.7854F, 3.1416F));

        middle.addChild("back", ModelPartBuilder.create().uv(146, 0).cuboid(-12.0F, -34.0F, -4.0F, 24.0F, 32.0F, 2.0F,
                new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bottom = body.addChild("bottom",
                ModelPartBuilder.create().uv(135, 85)
                        .cuboid(-5.0294F, -2.0F, -12.0F, 10.0F, 2.0F, 7.0F, new Dilation(0.001F)).uv(14, 64)
                        .cuboid(-12.0F, 0.01F, -12.0F, 24.0F, 0.0F, 10.0F, new Dilation(0.0F)).uv(14, 15)
                        .cuboid(-12.0F, -2.1F, -12.0F, 24.0F, 0.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 21.0F, 0.0F));

        bottom.addChild("octagon_r6", ModelPartBuilder.create().uv(125, 68).cuboid(-4.9706F, -2.0F, -12.0F, 3.0F, 2.0F,
                24.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        bottom.addChild("octagon_r7", ModelPartBuilder.create().uv(138, 55).cuboid(-4.9706F, -2.0F, -12.0F, 9.9411F,
                2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        bottom.addChild("octagon_r8", ModelPartBuilder.create().uv(20, 128).cuboid(-4.9706F, -2.0F, -12.0F, 9.9411F,
                2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData doors = body.addChild("doors", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, -2.0F, -17.0F, 0.0F, 3.1416F, 0.0F));

        doors.addChild("door_right", ModelPartBuilder.create().uv(161, 95).cuboid(0.4706F, -11.0F, -0.5F, 6.0F, 32.0F,
                1.0F, new Dilation(0.0F)), ModelTransform.pivot(-6.5F, 0.0F, -8.5F));

        doors.addChild("door_left", ModelPartBuilder.create().uv(162, 162).cuboid(-6.5294F, -11.0F, -0.5F, 6.0F, 32.0F,
                1.0F, new Dilation(0.0F)), ModelTransform.pivot(6.5F, 0.0F, -8.5F));

        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public Animation getAnimationForDoorState(DoorHandler.AnimationDoorState state) {
        return Animation.Builder.create(0).build();/*return switch (state) {
            case CLOSED -> DoorAnimations.INTERIOR_BOTH_CLOSE_ANIMATION;
            case FIRST -> DoorAnimations.INTERIOR_FIRST_OPEN_ANIMATION;
            case SECOND -> DoorAnimations.INTERIOR_SECOND_OPEN_ANIMATION;
            case BOTH -> DoorAnimations.INTERIOR_BOTH_OPEN_ANIMATION;
        };*/
    }

    @Override
    public ModelPart getPart() {
        return body;
    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, AbstractLinkableBlockEntity linkableBlockEntity, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        matrices.push();

        matrices.translate(0, -1.5f, 0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));

        DoorHandler door = tardis.door();

        if (!AITModClient.CONFIG.animateDoors) {
            this.body.getChild("doors").getChild("door_left").yaw = (door.isLeftOpen() || door.isOpen()) ? -5F : 0.0F;
            this.body.getChild("doors").getChild("door_right").yaw = (door.isRightOpen() || door.areBothOpen())
                    ? 5F
                    : 0.0F;
        } else {
            float maxRot = 90f;
            this.body.getChild("doors").getChild("door_left").yaw = (float) Math.toRadians(maxRot*door.getLeftRot());
            this.body.getChild("doors").getChild("door_right").yaw = (float) -Math.toRadians(maxRot*door.getRightRot());
        }

        if (AITModClient.CONFIG.enableTardisBOTI)
            this.getPart().getChild("middle").getChild("back").visible = false;

        super.renderWithAnimations(tardis, linkableBlockEntity, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);

        matrices.pop();
    }
}
