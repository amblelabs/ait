package dev.amble.ait.client.models.doors;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.tardis.handler.DoorHandler;

public class ClassicHudolinDoorModel extends DoorModel {
    private final ModelPart hudolin;

    public ClassicHudolinDoorModel(ModelPart root) {
        super(RenderLayer::getEntityCutoutNoCull);
        this.hudolin = root.getChild("hudolin");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData hudolin = modelPartData.addChild("hudolin", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 26.0F, -6.0F));

        ModelPartData Posts = hudolin.addChild("Posts", ModelPartBuilder.create().uv(220, 162).cuboid(-17.0F, -62.0F, -1.0F, 4.0F, 60.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r1 = Posts.addChild("cube_r1", ModelPartBuilder.create().uv(203, 162).cuboid(-17.0F, -66.0F, -18.0F, 4.0F, 60.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 4.0F, 16.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData Doors = hudolin.addChild("Doors", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData right_door = Doors.addChild("right_door", ModelPartBuilder.create().uv(0, 189).cuboid(-0.5F, -25.5F, -0.5F, 13.0F, 53.0F, 1.0F, new Dilation(0.0F))
                .uv(203, 76).cuboid(-0.5F, -25.5F, -1.0F, 13.0F, 53.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(-12.5F, -29.5F, 1.5F));

        ModelPartData phone = right_door.addChild("phone", ModelPartBuilder.create().uv(253, 132).cuboid(-10.0F, -40.5F, 7.5F, 6.0F, 8.0F, 1.0F, new Dilation(0.0F))
                .uv(271, 150).cuboid(-4.75F, -36.75F, 8.5F, 2.0F, 3.0F, 2.0F, new Dilation(-0.5F))
                .uv(271, 144).cuboid(-4.75F, -39.0F, 8.5F, 2.0F, 3.0F, 2.0F, new Dilation(-0.3F))
                .uv(271, 144).cuboid(-4.75F, -38.8F, 8.5F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                .uv(264, 153).cuboid(-7.25F, -39.0F, 10.75F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(268, 132).cuboid(-9.5F, -39.0F, 8.5F, 5.0F, 6.0F, 2.0F, new Dilation(0.0F))
                .uv(262, 142).cuboid(-9.5F, -41.0F, 8.5F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(262, 142).cuboid(-6.5F, -41.0F, 8.5F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(271, 141).cuboid(-8.0F, -38.5F, 10.75F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(253, 142).cuboid(-9.5F, -41.0F, 8.5F, 2.0F, 2.0F, 2.0F, new Dilation(-0.2F))
                .uv(253, 142).cuboid(-6.5F, -41.0F, 8.5F, 2.0F, 2.0F, 2.0F, new Dilation(-0.2F)), ModelTransform.pivot(13.0F, 31.5F, -7.7F));

        ModelPartData left_door = Doors.addChild("left_door", ModelPartBuilder.create().uv(194, 21).cuboid(-12.5F, -25.5F, -0.5F, 13.0F, 53.0F, 1.0F, new Dilation(0.0F))
                .uv(30, 17).cuboid(-11.5F, -4.5F, -1.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(5, 31).cuboid(-8.0F, -7.0F, -0.51F, 3.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(12.5F, -29.5F, 1.5F));

        ModelPartData Walls = hudolin.addChild("Walls", ModelPartBuilder.create().uv(29, 106).cuboid(-13.0F, -58.0F, 1.0F, 26.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(109, 0).cuboid(-13.0F, -58.0F, 0.5F, 26.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData PCB = hudolin.addChild("PCB", ModelPartBuilder.create().uv(237, 162).cuboid(-14.0F, -61.0F, -2.0F, 28.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -1.0F, 0.0F));
        return TexturedModelData.of(modelData, 512, 512);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        hudolin.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return hudolin;
    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, AbstractLinkableBlockEntity doorEntity, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, float tickDelta) {
        matrices.push();
        matrices.scale(0.64F, 0.64F, 0.64F);
        matrices.translate(0, -1.5, 0.35);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180));

        if (!AITModClient.CONFIG.animateDoors) {
            DoorHandler door = tardis.door();

            this.hudolin.getChild("Doors").getChild("left_door").yaw = (door.isLeftOpen() || door.isOpen()) ? -5F : 0.0F;
            this.hudolin.getChild("Doors").getChild("right_door").yaw = (door.isRightOpen() || door.areBothOpen())
                    ? 5F
                    : 0.0F;
        } else {
            float maxRot = 90f;
            this.hudolin.getChild("Doors").getChild("left_door").yaw = (float) Math.toRadians(maxRot*tardis.door().getLeftRot());
            this.hudolin.getChild("Doors").getChild("right_door").yaw = (float) -Math.toRadians(maxRot*tardis.door().getRightRot());
        }

        super.renderWithAnimations(tardis, doorEntity, root, matrices, vertices, light, overlay, red, green, blue, pAlpha, tickDelta);
        matrices.pop();
    }
}