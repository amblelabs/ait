package dev.amble.ait.client.models.exteriors;

import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.handler.DoorHandler;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class PresentExteriorModel extends ExteriorModel {
    private final ModelPart present;
    public PresentExteriorModel(ModelPart root) {
        this.present = root.getChild("present");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData present = modelPartData.addChild("present", ModelPartBuilder.create().uv(0, 0).cuboid(-10.0F, -32.0F, -10.0F, 20.0F, 32.0F, 20.0F, new Dilation(0.0F))
        .uv(0, 52).cuboid(-10.0F, -38.6F, -10.0F, 20.0F, 6.0F, 20.0F, new Dilation(0.5F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData right_door = present.addChild("right_door", ModelPartBuilder.create().uv(64, 78).cuboid(-10.0F, -16.0F, 0.0F, 10.0F, 32.0F, 0.0F, new Dilation(0.01F)), ModelTransform.pivot(10.0F, -16.0F, -10.001F));

        ModelPartData left_door = present.addChild("left_door", ModelPartBuilder.create().uv(44, 78).cuboid(0.0F, -16.0F, 0.0F, 10.0F, 32.0F, 0.0F, new Dilation(0.01F)), ModelTransform.pivot(-10.0F, -16.0F, -10.001F));

        ModelPartData bow = present.addChild("bow", ModelPartBuilder.create().uv(0, 78).cuboid(-11.0F, -12.0F, 0.0F, 22.0F, 13.0F, 0.0F, new Dilation(0.01F)), ModelTransform.pivot(0.0F, -39.6F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        present.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        matrices.push();
        matrices.translate(0, -1.5f, 0);

        this.renderDoors(tardis, exterior, root, matrices, vertices, light, overlay, red, green, blue, pAlpha, false);

        super.renderWithAnimations(tardis, exterior, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
        matrices.pop();
    }

    @Override
    public <T extends Entity & Linkable> void renderEntity(T falling, ModelPart root, MatrixStack matrices,
                                                           VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        if (falling.tardis().isEmpty())
            return;

        matrices.push();
        matrices.translate(0, -1.5f, 0);

        if (!AITModClient.CONFIG.animateDoors) {
            DoorHandler door = falling.tardis().get().door();

            this.present.getChild("left_door").yaw = (door.isLeftOpen() || door.isOpen()) ? 8F : 0.0F;
            this.present.getChild("right_door").yaw = (door.isRightOpen() || door.areBothOpen())
                    ? -8F
                    : 0.0F;
        } else {
            float maxRot = 90f;
            this.present.getChild("left_door").yaw = (float) Math.toRadians(falling.tardis().get().door().getLeftRot() * maxRot);
            this.present.getChild("right_door").yaw = -(float) Math.toRadians(falling.tardis().get().door().getRightRot() * maxRot);
        }

        super.renderEntity(falling, root, matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }

    @Override
    public ModelPart getPart() {
        return present;
    }

    @Override
    public Animation getAnimationForDoorState(DoorHandler.AnimationDoorState state) {
        return Animation.Builder.create(0).build();
    }

    @Override
    public void renderDoors(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, boolean isBOTI) {
        if (!AITModClient.CONFIG.animateDoors) {
            DoorHandler door = tardis.door();

            this.present.getChild("left_door").yaw = (door.isLeftOpen() || door.isOpen()) ? 8F : 0.0F;
            this.present.getChild("right_door").yaw = (door.isRightOpen() || door.areBothOpen())
                    ? -8F
                    : 0.0F;
        } else {
            float maxRot = 90f;
            this.present.getChild("left_door").yaw = (float) Math.toRadians(tardis.door().getLeftRot() * maxRot);
            this.present.getChild("right_door").yaw = -(float) Math.toRadians(tardis.door().getRightRot() * maxRot);
        }

        if (isBOTI) {
            matrices.push();
            matrices.translate(0, -1.5f, -3);
            this.present.getChild("left_door").render(matrices, vertices, light, overlay, red, green, blue, pAlpha);
            this.present.getChild("right_door").render(matrices, vertices, light, overlay, red, green, blue, pAlpha);
            matrices.pop();
        }
    }
}