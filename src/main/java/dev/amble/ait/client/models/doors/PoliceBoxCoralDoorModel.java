package dev.amble.ait.client.models.doors;

import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.tardis.handler.DoorHandler;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

public class PoliceBoxCoralDoorModel extends DoorModel {

    private final ModelPart TARDIS;

    public PoliceBoxCoralDoorModel(ModelPart root) {
        super(RenderLayer::getEntityCutoutNoCull);
        this.TARDIS = root.getChild("TARDIS");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData TARDIS = modelPartData.addChild("TARDIS", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 28.0F, 0.0F));

        ModelPartData Posts = TARDIS.addChild("Posts", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        Posts.addChild("cube_r1", ModelPartBuilder.create().uv(236, 42).cuboid(-18.0F, -60.0F, -17.0F, 4.0F, 56.0F,
                3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -32.0F, 0.0F, 1.5708F, 0.0F));
        Posts.addChild("cube_r2", ModelPartBuilder.create().uv(236, 102).cuboid(-2.0F, -30.0F, 17.0F, 3.0F, 30.0F, 4.0F,
                new Dilation(0.0F)), ModelTransform.of(0.0F, -34.5F, -18.0F, -1.5708F, 1.4835F, -1.5708F));
        Posts.addChild("cube_r3", ModelPartBuilder.create().uv(236, 127).cuboid(-2.0F, 0.0F, 17.0F, 3.0F, 31.0F, 4.0F,
                new Dilation(0.0F)), ModelTransform.of(0.0F, -34.6743F, -18.0F, 1.5708F, 1.4835F, 1.5708F));
        Posts.addChild("cube_r4", ModelPartBuilder.create().uv(251, 127).cuboid(-2.0F, 0.0F, 17.0F, 3.0F, 31.0F, 4.0F,
                new Dilation(0.0F)), ModelTransform.of(-38.0F, -34.6743F, -18.0F, 1.5708F, 1.4835F, 1.5708F));
        Posts.addChild("cube_r5", ModelPartBuilder.create().uv(251, 102).cuboid(-2.0F, -30.0F, 17.0F, 3.0F, 30.0F, 4.0F,
                new Dilation(0.0F)), ModelTransform.of(-38.0F, -34.5F, -18.0F, -1.5708F, 1.4835F, -1.5708F));
        Posts.addChild("cube_r6", ModelPartBuilder.create().uv(251, 41).cuboid(-17.0F, -60.0F, -18.0F, 3.0F, 56.0F,
                4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -32.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData Doors = TARDIS.addChild("Doors", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData right_door = Doors.addChild("right_door",
                ModelPartBuilder.create().uv(181, 177)
                        .cuboid(0.5F, -29.5F, -0.5F, 13.0F, 55.0F, 1.0F, new Dilation(0.0F)).uv(0, 198)
                        .cuboid(0.5F, -29.5F, -1.0F, 14.0F, 55.0F, 0.0F, new Dilation(0.0F)).uv(0, 10)
                        .cuboid(9.5F, -9.5F, -1.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)).uv(5, 51)
                        .cuboid(2.5F, -9.5F, -1.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-13.5F, -29.5F, -15.5F));

        right_door.addChild("phone",
                ModelPartBuilder.create().uv(268, 37)
                        .cuboid(-3.75F, -40.0F, -13.5F, 2.0F, 3.0F, 2.0F, new Dilation(-0.3F)).uv(268, 37)
                        .cuboid(-3.75F, -39.8F, -13.5F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)).uv(268, 43)
                        .cuboid(-3.75F, -37.75F, -13.5F, 2.0F, 3.0F, 2.0F, new Dilation(-0.5F)).uv(265, 25)
                        .cuboid(-8.5F, -40.0F, -13.5F, 5.0F, 6.0F, 2.0F, new Dilation(0.0F)).uv(268, 34)
                        .cuboid(-7.0F, -39.5F, -11.25F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)).uv(259, 35)
                        .cuboid(-5.5F, -42.0F, -13.5F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(259, 35)
                        .cuboid(-8.5F, -42.0F, -13.5F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(250, 35)
                        .cuboid(-5.5F, -42.0F, -13.5F, 2.0F, 2.0F, 2.0F, new Dilation(-0.2F)).uv(250, 35)
                        .cuboid(-8.5F, -42.0F, -13.5F, 2.0F, 2.0F, 2.0F, new Dilation(-0.2F)).uv(250, 25)
                        .cuboid(-9.0F, -41.5F, -14.5F, 6.0F, 8.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(12.5F, 28.5F, 14.5F));

        right_door.addChild("phone_t",
                ModelPartBuilder.create().uv(266, 74)
                        .cuboid(-9.25F, -40.0F, -13.5F, 2.0F, 3.0F, 2.0F, new Dilation(-0.3F)).uv(266, 74)
                        .cuboid(-9.25F, -39.8F, -13.5F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)).uv(266, 80)
                        .cuboid(-9.0F, -37.75F, -13.5F, 2.0F, 3.0F, 2.0F, new Dilation(-0.5F)).uv(266, 60)
                        .cuboid(-7.5F, -41.0F, -14.5F, 3.0F, 7.0F, 3.0F, new Dilation(0.0F)).uv(266, 71)
                        .cuboid(-7.0F, -40.0F, -11.25F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)).uv(266, 71)
                        .cuboid(-7.0F, -37.0F, -11.25F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.pivot(13.25F, 28.5F, 14.5F));

        Doors.addChild("left_door",
                ModelPartBuilder.create().uv(189, 41)
                        .cuboid(-13.5F, -29.5F, -0.5F, 13.0F, 55.0F, 1.0F, new Dilation(0.0F)).uv(0, 0)
                        .cuboid(-12.5F, -10.5F, -1.5F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)).uv(0, 51)
                        .cuboid(-12.5F, -4.5F, -1.5F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(13.5F, -29.5F, -15.5F));

        TARDIS.addChild("Walls",
                ModelPartBuilder.create().uv(63, 227)
                        .cuboid(-14.0F, -60.0F, -16.0F, 1.0F, 56.0F, 1.0F, new Dilation(0.0F)).uv(116, 170)
                        .cuboid(13.0F, -60.0F, -16.0F, 1.0F, 56.0F, 1.0F, new Dilation(0.0F)).uv(115, 0)
                        .cuboid(-13.0F, -60.0F, -16.0F, 26.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(59, 113)
                        .cuboid(13.0F, -60.0F, -16.5F, 1.0F, 56.0F, 0.0F, new Dilation(0.0F)).uv(115, 3)
                        .cuboid(-13.0F, -60.0F, -16.5F, 26.0F, 1.0F, 0.0F, new Dilation(0.0F)).uv(62, 113)
                        .cuboid(-14.0F, -60.0F, -16.5F, 1.0F, 56.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData PCB = TARDIS.addChild("PCB",
                ModelPartBuilder.create().uv(241, 2)
                        .cuboid(-17.0F, -64.0F, -19.0F, 34.0F, 5.0F, 6.0F, new Dilation(0.0F)).uv(243, 3)
                        .cuboid(-16.0F, -60.0F, -17.0F, 32.0F, 0.0F, 4.0F, new Dilation(0.0F)).uv(241, 14)
                        .cuboid(-16.0F, -63.0F, -17.0F, 32.0F, 3.0F, 0.0F, new Dilation(0.0F)).uv(241, 18)
                        .cuboid(-1.0F, -63.0F, -14.0F, 2.0F, 3.0F, 0.0F, new Dilation(0.0F)).uv(243, 5)
                        .cuboid(16.0F, -63.0F, -17.0F, 0.0F, 3.0F, 4.0F, new Dilation(0.0F)).uv(277, 5)
                        .cuboid(-16.0F, -63.0F, -17.0F, 0.0F, 3.0F, 4.0F, new Dilation(0.0F)).uv(243, 3)
                        .cuboid(-16.0F, -63.0F, -17.0F, 32.0F, 0.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        PCB.addChild("lights",
                ModelPartBuilder.create().uv(241, 28)
                        .cuboid(10.0F, -59.3F, -16.5F, 2.0F, 2.0F, 2.0F, new Dilation(-0.15F)).uv(241, 22)
                        .cuboid(10.0F, -60.4F, -16.5F, 2.0F, 3.0F, 2.0F, new Dilation(-0.3F)).uv(241, 22)
                        .cuboid(4.0F, -60.4F, -16.5F, 2.0F, 3.0F, 2.0F, new Dilation(-0.3F)).uv(241, 28)
                        .cuboid(4.0F, -59.3F, -16.5F, 2.0F, 2.0F, 2.0F, new Dilation(-0.15F)).uv(241, 22)
                        .cuboid(-6.0F, -60.4F, -16.5F, 2.0F, 3.0F, 2.0F, new Dilation(-0.3F)).uv(241, 28)
                        .cuboid(-6.0F, -59.3F, -16.5F, 2.0F, 2.0F, 2.0F, new Dilation(-0.15F)).uv(241, 22)
                        .cuboid(-12.0F, -60.4F, -16.5F, 2.0F, 3.0F, 2.0F, new Dilation(-0.3F)).uv(241, 28)
                        .cuboid(-12.0F, -59.3F, -16.5F, 2.0F, 2.0F, 2.0F, new Dilation(-0.15F)),
                ModelTransform.pivot(0.0F, -3.0F, 0.0F));

        ModelPartData TARDIS_t = TARDIS.addChild("TARDIS_t", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData PCB_t = TARDIS_t.addChild("PCB_t",
                ModelPartBuilder.create().uv(0, 434)
                        .cuboid(-17.0F, -64.0F, -19.0F, 34.0F, 5.0F, 6.0F, new Dilation(0.0F)).uv(8, 435)
                        .cuboid(-11.0F, -60.0F, -17.0F, 22.0F, 0.0F, 4.0F, new Dilation(0.0F)).uv(0, 446)
                        .cuboid(-11.0F, -63.0F, -17.0F, 22.0F, 3.0F, 0.0F, new Dilation(0.0F)).uv(0, 450)
                        .cuboid(-1.0F, -63.0F, -14.0F, 2.0F, 3.0F, 0.0F, new Dilation(0.0F)).uv(0, 437)
                        .cuboid(11.0F, -63.0F, -17.0F, 0.0F, 3.0F, 4.0F, new Dilation(0.0F)).uv(38, 437)
                        .cuboid(-11.0F, -63.0F, -17.0F, 0.0F, 3.0F, 4.0F, new Dilation(0.0F)).uv(21, 435)
                        .cuboid(-11.0F, -63.0F, -17.0F, 22.0F, 0.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        PCB_t.addChild("lights2",
                ModelPartBuilder.create().uv(241, 28)
                        .cuboid(10.0F, -59.3F, -16.5F, 2.0F, 2.0F, 2.0F, new Dilation(-0.15F)).uv(241, 22)
                        .cuboid(10.0F, -60.4F, -16.5F, 2.0F, 3.0F, 2.0F, new Dilation(-0.3F)).uv(241, 22)
                        .cuboid(4.0F, -60.4F, -16.5F, 2.0F, 3.0F, 2.0F, new Dilation(-0.3F)).uv(241, 28)
                        .cuboid(4.0F, -59.3F, -16.5F, 2.0F, 2.0F, 2.0F, new Dilation(-0.15F)).uv(241, 22)
                        .cuboid(-6.0F, -60.4F, -16.5F, 2.0F, 3.0F, 2.0F, new Dilation(-0.3F)).uv(241, 28)
                        .cuboid(-6.0F, -59.3F, -16.5F, 2.0F, 2.0F, 2.0F, new Dilation(-0.15F)).uv(241, 22)
                        .cuboid(-12.0F, -60.4F, -16.5F, 2.0F, 3.0F, 2.0F, new Dilation(-0.3F)).uv(241, 28)
                        .cuboid(-12.0F, -59.3F, -16.5F, 2.0F, 2.0F, 2.0F, new Dilation(-0.15F)),
                ModelTransform.pivot(0.0F, -3.0F, 0.0F));

        PCB_t.addChild("lights_t2",
                ModelPartBuilder.create().uv(267, 55)
                        .cuboid(7.0F, -59.3F, -16.5F, 2.0F, 2.0F, 2.0F, new Dilation(-0.15F)).uv(267, 49)
                        .cuboid(7.0F, -60.4F, -16.5F, 2.0F, 3.0F, 2.0F, new Dilation(-0.3F)).uv(267, 49)
                        .cuboid(1.75F, -60.4F, -16.5F, 2.0F, 3.0F, 2.0F, new Dilation(-0.3F)).uv(267, 55)
                        .cuboid(1.75F, -59.3F, -16.5F, 2.0F, 2.0F, 2.0F, new Dilation(-0.15F)).uv(267, 49)
                        .cuboid(-3.75F, -60.4F, -16.5F, 2.0F, 3.0F, 2.0F, new Dilation(-0.3F)).uv(267, 55)
                        .cuboid(-3.75F, -59.3F, -16.5F, 2.0F, 2.0F, 2.0F, new Dilation(-0.15F)).uv(267, 49)
                        .cuboid(-9.0F, -60.4F, -16.5F, 2.0F, 3.0F, 2.0F, new Dilation(-0.3F)).uv(267, 55)
                        .cuboid(-9.0F, -59.3F, -16.5F, 2.0F, 2.0F, 2.0F, new Dilation(-0.15F)),
                ModelTransform.pivot(0.0F, -3.0F, 0.0F));

        return TexturedModelData.of(modelData, 512, 512);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        TARDIS.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, AbstractLinkableBlockEntity doorEntity, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        DoorHandler door = tardis.door();

        if (!AITModClient.CONFIG.animateDoors) {
            this.TARDIS.getChild("Doors").getChild("left_door").yaw = (door.isLeftOpen() || door.isOpen()) ? -5F : 0.0F;
            this.TARDIS.getChild("Doors").getChild("right_door").yaw = (door.isRightOpen() || door.areBothOpen())
                    ? 5F
                    : 0.0F;
        } else {
            float maxRot = 90f;
            this.TARDIS.getChild("Doors").getChild("left_door").yaw = (float) Math.toRadians(maxRot*door.getLeftRot());
            this.TARDIS.getChild("Doors").getChild("right_door").yaw = (float) -Math.toRadians(maxRot*door.getRightRot());
        }

        matrices.push();
        matrices.scale(0.631F, 0.631F, 0.631F);
        matrices.translate(0, -1.5f, -0.35);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180));

        super.renderWithAnimations(tardis, doorEntity, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
        matrices.pop();
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
        return TARDIS;
    }
}
