package dev.amble.ait.client.models.doors.exclusive;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.tardis.handler.DoorHandler;

public class BlueBoxDoorModel extends DoorModel {
    private final ModelPart box;
    private final ModelPart base;
    private final ModelPart left_door2;
    private final ModelPart pulltoopen;
    private final ModelPart phone;
    private final ModelPart right_door2;

    public BlueBoxDoorModel(ModelPart root) {
        this.box = root.getChild("box");
        this.base = this.box.getChild("base");
        this.left_door2 = this.box.getChild("left_door2");
        this.pulltoopen = this.box.getChild("pulltoopen");
        this.phone = this.pulltoopen.getChild("phone");
        this.right_door2 = this.box.getChild("right_door2");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData box = modelPartData.addChild("box", ModelPartBuilder.create(), ModelTransform.pivot(-6.0F, 24.0F, 0.0F));

        ModelPartData base = box.addChild("base", ModelPartBuilder.create().uv(0, 48).cuboid(-20.0F, -3.0F, -20.0F, 40.0F, 3.0F, 40.0F, new Dilation(0.0F))
        .uv(39, 39).cuboid(-18.0F, -71.0F, -21.0F, 36.0F, 5.0F, 3.0F, new Dilation(0.0F))
        .uv(115, 0).cuboid(-18.0F, -75.0F, -18.0F, 36.0F, 4.0F, 36.0F, new Dilation(0.0F))
        .uv(121, 48).cuboid(-16.0F, -76.0F, -16.0F, 32.0F, 1.0F, 32.0F, new Dilation(0.0F))
        .uv(62, 193).cuboid(-10.0F, -77.0F, -10.0F, 20.0F, 1.0F, 20.0F, new Dilation(0.0F))
        .uv(39, 91).cuboid(18.0F, -71.0F, -18.0F, 3.0F, 5.0F, 36.0F, new Dilation(0.0F))
        .uv(0, 91).cuboid(-21.0F, -71.0F, -18.0F, 3.0F, 5.0F, 36.0F, new Dilation(0.0F))
        .uv(156, 133).cuboid(-14.0F, -66.0F, -18.5F, 28.0F, 1.0F, 1.0F, new Dilation(0.0F))
        .uv(156, 133).cuboid(-14.0F, -66.0F, 17.5F, 28.0F, 1.0F, 1.0F, new Dilation(0.0F))
        .uv(36, 134).cuboid(17.5F, -66.0F, -14.0F, 1.0F, 1.0F, 28.0F, new Dilation(0.0F))
        .uv(0, 134).cuboid(-18.5F, -66.0F, -14.0F, 1.0F, 1.0F, 28.0F, new Dilation(0.0F))
        .uv(31, 164).cuboid(-18.0F, -65.0F, 0.0F, 1.0F, 62.0F, 14.0F, new Dilation(0.0F))
        .uv(0, 164).cuboid(-18.0F, -65.0F, -14.0F, 1.0F, 62.0F, 14.0F, new Dilation(0.0F))
        .uv(169, 213).cuboid(-18.5F, -65.0F, -0.5F, 1.0F, 62.0F, 1.0F, new Dilation(0.0F))
        .uv(159, 213).cuboid(17.5F, -65.0F, -0.5F, 1.0F, 62.0F, 1.0F, new Dilation(0.0F))
        .uv(193, 136).cuboid(14.0F, -73.0F, -19.0F, 5.0F, 70.0F, 5.0F, new Dilation(0.0F))
        .uv(193, 136).mirrored().cuboid(-19.0F, -73.0F, -19.0F, 5.0F, 70.0F, 5.0F, new Dilation(0.0F)).mirrored(false)
        .uv(193, 136).cuboid(-19.0F, -73.0F, 14.0F, 5.0F, 70.0F, 5.0F, new Dilation(0.0F))
        .uv(193, 136).mirrored().cuboid(14.0F, -73.0F, 14.0F, 5.0F, 70.0F, 5.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r1 = base.addChild("cube_r1", ModelPartBuilder.create().uv(162, 136).cuboid(-0.5F, -31.0F, -7.0F, 1.0F, 62.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(17.5F, -34.0F, 7.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r2 = base.addChild("cube_r2", ModelPartBuilder.create().uv(131, 136).cuboid(-0.5F, -31.0F, -7.0F, 1.0F, 62.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(17.5F, -34.0F, -7.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r3 = base.addChild("cube_r3", ModelPartBuilder.create().uv(38, 38).mirrored().cuboid(-18.0F, -2.5F, -4.0F, 36.0F, 5.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -68.5F, 17.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r4 = base.addChild("cube_r4", ModelPartBuilder.create().uv(6, 50).cuboid(-18.0F, -1.5F, -18.0F, 36.0F, 3.0F, 36.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -67.25F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData left_door2 = box.addChild("left_door2", ModelPartBuilder.create().uv(93, 215).cuboid(0.0F, -33.0F, -1.0F, 14.0F, 62.0F, 1.0F, new Dilation(0.0F))
        .uv(276, 125).cuboid(12.8F, -6.0F, 0.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
        .uv(174, 213).cuboid(13.5F, -33.0F, -1.5F, 1.0F, 62.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-14.0F, -32.0F, -17.0F));

        ModelPartData cube_r5 = left_door2.addChild("cube_r5", ModelPartBuilder.create().uv(276, 200).cuboid(-0.1F, -0.1F, -0.5F, 3.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(12.1F, 27.9F, 0.5F, 0.0F, 0.0F, -1.5708F));

        ModelPartData cube_r6 = left_door2.addChild("cube_r6", ModelPartBuilder.create().uv(297, 211).cuboid(-33.5F, -3.05F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(15.55F, -5.0F, 0.25F, 0.0F, 0.0F, -1.5708F));

        ModelPartData pulltoopen = box.addChild("pulltoopen", ModelPartBuilder.create().uv(0, 12).cuboid(7.0083F, -2.3333F, -1.05F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
        .uv(128, 233).cuboid(0.0083F, -4.8333F, -0.05F, 8.0F, 10.0F, 1.0F, new Dilation(0.0F))
        .uv(85, 182).cuboid(1.0083F, -4.3333F, -0.15F, 6.0F, 9.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(-11.0083F, -42.1667F, -17.95F));

        ModelPartData phone = pulltoopen.addChild("phone", ModelPartBuilder.create().uv(278, 38).cuboid(-3.75F, -40.0F, -13.5F, 2.0F, 3.0F, 2.0F, new Dilation(-0.3F))
        .uv(278, 38).cuboid(-3.75F, -39.8F, -13.5F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
        .uv(278, 44).cuboid(-3.75F, -37.75F, -13.5F, 2.0F, 3.0F, 2.0F, new Dilation(-0.5F))
        .uv(275, 26).cuboid(-8.5F, -40.0F, -13.5F, 5.0F, 6.0F, 2.0F, new Dilation(0.0F))
        .uv(278, 35).cuboid(-7.0F, -39.5F, -11.25F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F))
        .uv(269, 36).cuboid(-5.5F, -42.0F, -13.5F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
        .uv(269, 36).cuboid(-8.5F, -42.0F, -13.5F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
        .uv(260, 36).cuboid(-5.5F, -42.0F, -13.5F, 2.0F, 2.0F, 2.0F, new Dilation(-0.2F))
        .uv(260, 36).cuboid(-8.5F, -42.0F, -13.5F, 2.0F, 2.0F, 2.0F, new Dilation(-0.2F))
        .uv(260, 26).cuboid(-9.0F, -41.5F, -14.5F, 6.0F, 8.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(9.5083F, 37.6667F, 14.95F));

        ModelPartData right_door2 = box.addChild("right_door2", ModelPartBuilder.create().uv(230, 200).cuboid(-14.0F, -33.0F, -1.0F, 14.0F, 62.0F, 1.0F, new Dilation(0.0F))
        .uv(276, 200).cuboid(-13.4F, -6.0F, 0.0F, 3.0F, 2.0F, 1.0F, new Dilation(0.0F))
        .uv(297, 211).cuboid(-13.95F, -5.5F, -0.25F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F))
        .uv(0, 0).cuboid(-12.75F, -13.0F, -2.0F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(14.0F, -32.0F, -17.0F));

        ModelPartData cube_r7 = right_door2.addChild("cube_r7", ModelPartBuilder.create().uv(0, 0).cuboid(-0.75F, -3.0F, -2.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-12.5F, -10.0F, -1.5F, 0.0F, 3.1416F, 0.0F));
        return TexturedModelData.of(modelData, 512, 512);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
                       float green, float blue, float alpha) {
        box.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, AbstractLinkableBlockEntity doorEntity, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, float tickDelta) {
        if (!AITModClient.CONFIG.animateDoors) {
            DoorHandler door = tardis.door();

            this.left_door2.yaw = (door.isLeftOpen() || door.isOpen()) ? -5.0f : 0.0F;
            this.right_door2.yaw = (door.isRightOpen() || door.areBothOpen())
                    ? 5.0f
                    : 0.0F;
        } else {
            float maxRot = 80f;
            this.left_door2.yaw = (float) Math.toRadians(maxRot*tardis.door().getLeftRot());
            this.right_door2.yaw = (float) -Math.toRadians(maxRot*tardis.door().getRightRot());
        }

        matrices.push();
        matrices.scale(0.955F, 0.955F, 0.955F);
        matrices.translate(0, -1.5, 0.1);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180));

        super.renderWithAnimations(tardis, doorEntity, root, matrices, vertices, light, overlay, red, green, blue, pAlpha, tickDelta);
        matrices.pop();
    }

    @Override
    public ModelPart getPart() {
        return box;
    }
}