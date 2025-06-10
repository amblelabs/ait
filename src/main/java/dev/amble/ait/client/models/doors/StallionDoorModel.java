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

public class StallionDoorModel extends DoorModel {
    private final ModelPart body;
    public StallionDoorModel(ModelPart root) {
        this.body = root.getChild("body");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData door = body.addChild("door", ModelPartBuilder.create(), ModelTransform.pivot(7.5F, -20.0F, -7.0F));

        ModelPartData cube_r1 = door.addChild("cube_r1", ModelPartBuilder.create().uv(17, 122).cuboid(-9.0F, -38.0F, -8.0F, 0.0F, 37.0F, 8.0F, new Dilation(-0.001F))
        .uv(117, 0).cuboid(-9.5F, -38.0F, -8.0F, 1.0F, 37.0F, 8.0F, new Dilation(-0.001F))
        .uv(14, 26).cuboid(-9.5F, -24.0F, -1.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
        .uv(5, 16).cuboid(-10.25F, -23.5F, -1.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
        .uv(0, 16).cuboid(-8.75F, -23.5F, -1.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-7.5F, 20.0F, 9.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData door_two = door.addChild("door_two", ModelPartBuilder.create(), ModelTransform.pivot(-7.5F, -3.0F, 0.5F));

        ModelPartData cube_r2 = door_two.addChild("cube_r2", ModelPartBuilder.create().uv(0, 122).cuboid(-9.0F, -38.0F, 0.0F, 0.0F, 37.0F, 8.0F, new Dilation(-0.001F))
        .uv(109, 92).cuboid(-9.5F, -38.0F, 0.0F, 1.0F, 37.0F, 8.0F, new Dilation(-0.001F)), ModelTransform.of(0.0F, 23.0F, 8.5F, 0.0F, -1.5708F, 0.0F));

        ModelPartData bone = body.addChild("bone", ModelPartBuilder.create().uv(18, 44).cuboid(-10.0F, -1.0F, -10.0F, 20.0F, 1.0F, 2.0F, new Dilation(0.0F))
        .uv(83, 164).cuboid(-10.0F, -43.0F, -10.0F, 20.0F, 5.0F, 2.0F, new Dilation(0.0F))
        .uv(136, 0).cuboid(8.0F, -38.0F, -10.0F, 2.0F, 37.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 2.0F));

        ModelPartData cube_r3 = bone.addChild("cube_r3", ModelPartBuilder.create().uv(128, 92).cuboid(8.0F, -38.0F, -10.0F, 2.0F, 37.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData top = body.addChild("top", ModelPartBuilder.create(), ModelTransform.pivot(0.5F, 0.0F, 1.5F));

        ModelPartData antenna = top.addChild("antenna", ModelPartBuilder.create(), ModelTransform.pivot(-0.5F, -52.0F, 0.5F));

        ModelPartData antenna2 = top.addChild("antenna2", ModelPartBuilder.create(), ModelTransform.of(-0.5F, -52.0F, 0.5F, 0.0F, -0.7854F, 0.0F));

        ModelPartData antenna3 = top.addChild("antenna3", ModelPartBuilder.create(), ModelTransform.of(-0.5F, -52.0F, 0.5F, 0.0F, -1.5708F, 0.0F));

        ModelPartData antenna4 = top.addChild("antenna4", ModelPartBuilder.create(), ModelTransform.of(-0.5F, -52.0F, 0.5F, 0.0F, -2.3562F, 0.0F));

        ModelPartData antenna5 = top.addChild("antenna5", ModelPartBuilder.create(), ModelTransform.of(-0.5F, -52.0F, 0.5F, 0.0F, 3.1416F, 0.0F));

        ModelPartData antenna6 = top.addChild("antenna6", ModelPartBuilder.create(), ModelTransform.of(-0.5F, -52.0F, 0.5F, 0.0F, 2.3562F, 0.0F));

        ModelPartData antenna7 = top.addChild("antenna7", ModelPartBuilder.create(), ModelTransform.of(-0.5F, -52.0F, 0.5F, 0.0F, 1.5708F, 0.0F));

        ModelPartData antenna8 = top.addChild("antenna8", ModelPartBuilder.create(), ModelTransform.of(-0.5F, -52.0F, 0.5F, 0.0F, 0.7854F, 0.0F));
        return TexturedModelData.of(modelData, 256, 256);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return body;
    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, AbstractLinkableBlockEntity linkableBlockEntity, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        matrices.push();
        matrices.scale(0.95f, 0.95f, 0.95f);
        matrices.translate(0, -1.5f, 0);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180f));

        if (!AITModClient.CONFIG.animateDoors) {
            body.getChild("door").yaw = tardis.door().isOpen() ? -1.35f : 0f;
            body.getChild("door").getChild("door_two").yaw = tardis.door().isOpen() ? 2.65f : 0f;
        } else {
            float maxLeftRot = 87f;
            float maxRightRot = 150f;

            body.getChild("door").yaw = -(float) Math.toRadians(maxLeftRot*tardis.door().getLeftRot());
            body.getChild("door").getChild("door_two").yaw = (float) Math.toRadians(maxRightRot*tardis.door().getLeftRot());
        }

        super.renderWithAnimations(tardis, linkableBlockEntity, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);

        matrices.pop();
    }

    @Override
    public Animation getAnimationForDoorState(DoorHandler.AnimationDoorState state) {
        return Animation.Builder.create(0).build();
    }
}