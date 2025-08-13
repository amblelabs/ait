package dev.amble.ait.client.models.exteriors;


import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;

public class StallionExteriorModel extends SimpleExteriorModel {
    private final ModelPart body;
    public StallionExteriorModel(ModelPart root) {
        this.body = root.getChild("body");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData door = body.addChild("door", ModelPartBuilder.create(), ModelTransform.pivot(7.5F, -20.0F, -9.0F));

        ModelPartData cube_r1 = door.addChild("cube_r1", ModelPartBuilder.create().uv(17, 122).cuboid(-9.0F, -38.0F, -8.0F, 0.0F, 37.0F, 8.0F, new Dilation(-0.001F))
        .uv(117, 0).cuboid(-9.5F, -38.0F, -8.0F, 1.0F, 37.0F, 8.0F, new Dilation(-0.001F))
        .uv(14, 26).cuboid(-9.5F, -24.0F, -1.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
        .uv(5, 16).cuboid(-10.25F, -23.5F, -1.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
        .uv(0, 16).cuboid(-8.75F, -23.5F, -1.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-7.5F, 20.0F, 9.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData door_two = door.addChild("door_two", ModelPartBuilder.create(), ModelTransform.pivot(-7.5F, -3.0F, 0.5F));

        ModelPartData cube_r2 = door_two.addChild("cube_r2", ModelPartBuilder.create().uv(0, 122).cuboid(-9.0F, -38.0F, 0.0F, 0.0F, 37.0F, 8.0F, new Dilation(-0.001F))
        .uv(109, 92).cuboid(-9.5F, -38.0F, 0.0F, 1.0F, 37.0F, 8.0F, new Dilation(-0.001F)), ModelTransform.of(0.0F, 23.0F, 8.5F, 0.0F, -1.5708F, 0.0F));

        ModelPartData bone = body.addChild("bone", ModelPartBuilder.create().uv(0, 26).cuboid(-10.0F, -1.0F, -10.0F, 20.0F, 1.0F, 20.0F, new Dilation(0.0F))
        .uv(0, 0).cuboid(-10.0F, -43.0F, -10.0F, 20.0F, 5.0F, 20.0F, new Dilation(0.0F))
        .uv(0, 48).cuboid(-9.0F, -44.0F, -9.0F, 18.0F, 1.0F, 18.0F, new Dilation(0.0F))
        .uv(81, 0).cuboid(-9.5F, -38.0F, -8.0F, 1.0F, 37.0F, 16.0F, new Dilation(-0.001F))
        .uv(35, 90).cuboid(-9.0F, -38.0F, -8.0F, 0.0F, 37.0F, 16.0F, new Dilation(-0.001F))
        .uv(136, 0).cuboid(8.0F, -38.0F, -10.0F, 2.0F, 37.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r3 = bone.addChild("cube_r3", ModelPartBuilder.create().uv(128, 92).cuboid(8.0F, -38.0F, -10.0F, 2.0F, 37.0F, 2.0F, new Dilation(0.0F))
        .uv(0, 68).cuboid(-9.5F, -38.0F, -8.0F, 1.0F, 37.0F, 16.0F, new Dilation(-0.001F))
        .uv(76, 90).cuboid(-9.0F, -38.0F, -8.0F, 0.0F, 37.0F, 16.0F, new Dilation(-0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r4 = bone.addChild("cube_r4", ModelPartBuilder.create().uv(128, 132).cuboid(8.0F, -38.0F, -10.0F, 2.0F, 37.0F, 2.0F, new Dilation(0.0F))
        .uv(57, 52).cuboid(-9.5F, -38.0F, -8.0F, 1.0F, 37.0F, 16.0F, new Dilation(-0.001F))
        .uv(100, 38).cuboid(-9.0F, -38.0F, -8.0F, 0.0F, 37.0F, 16.0F, new Dilation(-0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r5 = bone.addChild("cube_r5", ModelPartBuilder.create().uv(133, 46).cuboid(8.0F, -38.0F, -10.0F, 2.0F, 37.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData top = body.addChild("top", ModelPartBuilder.create().uv(9, 26).cuboid(-1.0F, -52.0F, 0.0F, 1.0F, 8.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, 0.0F, -0.5F));

        ModelPartData antenna = top.addChild("antenna", ModelPartBuilder.create(), ModelTransform.pivot(-0.5F, -52.0F, 0.5F));

        ModelPartData cube_r6 = antenna.addChild("cube_r6", ModelPartBuilder.create().uv(93, 92).cuboid(-10.4F, -4.3F, 0.5F, 10.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -0.5F, 0.0F, 0.0F, -0.6545F));

        ModelPartData antenna2 = top.addChild("antenna2", ModelPartBuilder.create(), ModelTransform.of(-0.5F, -52.0F, 0.5F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r7 = antenna2.addChild("cube_r7", ModelPartBuilder.create().uv(35, 80).cuboid(-10.4F, -4.3F, 0.5F, 10.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -0.5F, 0.0F, 0.0F, -0.6545F));

        ModelPartData antenna3 = top.addChild("antenna3", ModelPartBuilder.create(), ModelTransform.of(-0.5F, -52.0F, 0.5F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r8 = antenna3.addChild("cube_r8", ModelPartBuilder.create().uv(76, 60).cuboid(-10.4F, -4.3F, 0.5F, 10.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -0.5F, 0.0F, 0.0F, -0.6545F));

        ModelPartData antenna4 = top.addChild("antenna4", ModelPartBuilder.create(), ModelTransform.of(-0.5F, -52.0F, 0.5F, 0.0F, -2.3562F, 0.0F));

        ModelPartData cube_r9 = antenna4.addChild("cube_r9", ModelPartBuilder.create().uv(76, 54).cuboid(-10.4F, -4.3F, 0.5F, 10.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -0.5F, 0.0F, 0.0F, -0.6545F));

        ModelPartData antenna5 = top.addChild("antenna5", ModelPartBuilder.create(), ModelTransform.of(-0.5F, -52.0F, 0.5F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r10 = antenna5.addChild("cube_r10", ModelPartBuilder.create().uv(19, 74).cuboid(-10.4F, -4.3F, 0.5F, 10.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -0.5F, 0.0F, 0.0F, -0.6545F));

        ModelPartData antenna6 = top.addChild("antenna6", ModelPartBuilder.create(), ModelTransform.of(-0.5F, -52.0F, 0.5F, 0.0F, 2.3562F, 0.0F));

        ModelPartData cube_r11 = antenna6.addChild("cube_r11", ModelPartBuilder.create().uv(19, 68).cuboid(-10.4F, -4.3F, 0.5F, 10.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -0.5F, 0.0F, 0.0F, -0.6545F));

        ModelPartData antenna7 = top.addChild("antenna7", ModelPartBuilder.create(), ModelTransform.of(-0.5F, -52.0F, 0.5F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r12 = antenna7.addChild("cube_r12", ModelPartBuilder.create().uv(61, 6).cuboid(-10.4F, -4.3F, 0.5F, 10.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -0.5F, 0.0F, 0.0F, -0.6545F));

        ModelPartData antenna8 = top.addChild("antenna8", ModelPartBuilder.create(), ModelTransform.of(-0.5F, -52.0F, 0.5F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r13 = antenna8.addChild("cube_r13", ModelPartBuilder.create().uv(61, 0).cuboid(-10.4F, -4.3F, 0.5F, 10.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -0.5F, 0.0F, 0.0F, -0.6545F));

        ModelPartData controls = body.addChild("controls", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -30.0F, 7.0F, 7.0F, 13.0F, 2.0F, new Dilation(0.0F))
        .uv(0, 26).cuboid(-1.5F, -28.0F, 5.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));
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
    public void renderDoors(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, boolean isBOTI) {
        if (!AITModClient.CONFIG.animateDoors) {
            body.getChild("door").yaw = tardis.door().isOpen() ? -1.35f : 0f;
            body.getChild("door").getChild("door_two").yaw = tardis.door().isOpen() ? 2.65f : 0f;
        } else {
            body.getChild("door").yaw = -(float) Math.toRadians(87f * tardis.door().getLeftRot());
            body.getChild("door").getChild("door_two").yaw = (float) Math.toRadians(150f * tardis.door().getLeftRot());
        }

        if (isBOTI) {
            matrices.push();
            matrices.scale(0.95f, 0.95f, 0.95f);
            matrices.translate(10, 10f, -1);
            body.getChild("door").render(matrices, vertices, light, overlay, red, green, blue, pAlpha);
            matrices.pop();
        }

    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        matrices.scale(0.95f, 0.95f, 0.95f);
        matrices.translate(0, -1.5f, 0);

        this.renderDoors(tardis, exterior, root, matrices, vertices, light, overlay, red, green, blue, alpha, false);

        super.renderWithAnimations(tardis, exterior, root, matrices, vertices, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }

    @Override
    public <T extends Entity & Linkable> void renderEntity(T falling, ModelPart root, MatrixStack matrices,
                                                           VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        matrices.scale(0.95f, 0.95f, 0.95f);
        matrices.translate(0, -1.5f, 0);

        if (!AITModClient.CONFIG.animateDoors) {
            body.getChild("door").yaw = falling.tardis().get().door().isOpen() ? -1.35f : 0f;
            body.getChild("door").getChild("door_two").yaw = falling.tardis().get().door().isOpen() ? 2.65f : 0f;
        } else {
            float maxLeftRot = 87f;
            float maxRightRot = 150f;
            body.getChild("door").yaw = -(float) Math.toRadians(maxLeftRot * falling.tardis().get().door().getLeftRot());
            body.getChild("door").getChild("door_two").yaw = (float) Math.toRadians(maxRightRot * falling.tardis().get().door().getLeftRot());
        }

        super.renderEntity(falling, root, matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }
}