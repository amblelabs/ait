package dev.amble.ait.client.models.doors;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.tardis.handler.DoorHandler;

// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class PresentDoorModel extends DoorModel {
    private final ModelPart present;
    public PresentDoorModel(ModelPart root) {
        this.present = root.getChild("present");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData present = modelPartData.addChild("present", ModelPartBuilder.create().uv(20, 72).cuboid(-10.0F, -38.6F, -8.0F, 20.0F, 6.0F, 0.0F, new Dilation(0.5F))
        .uv(53, 22).cuboid(10.0F, -39.0F, -8.001F, 1.0F, 39.0F, 1.0F, new Dilation(0.0F))
        .uv(53, 22).cuboid(-11.0F, -39.0F, -8.001F, 1.0F, 39.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData right_door = present.addChild("right_door", ModelPartBuilder.create().uv(64, 78).cuboid(-10.0F, -16.0F, 0.0F, 10.0F, 32.0F, 0.0F, new Dilation(0.01F)), ModelTransform.pivot(10.0F, -16.0F, -8.001F));

        ModelPartData left_door = present.addChild("left_door", ModelPartBuilder.create().uv(44, 78).cuboid(0.0F, -16.0F, 0.0F, 10.0F, 32.0F, 0.0F, new Dilation(0.01F)), ModelTransform.pivot(-10.0F, -16.0F, -8.001F));

        ModelPartData bow = present.addChild("bow", ModelPartBuilder.create().uv(0, 78).cuboid(-11.0F, -12.0F, 0.0F, 22.0F, 13.0F, 0.0F, new Dilation(0.01F)), ModelTransform.pivot(0.0F, -39.6F, -7.9F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        present.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, AbstractLinkableBlockEntity linkableBlockEntity, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, float tickDelta) {
        DoorHandler door = tardis.door();

        if (!AITModClient.CONFIG.animateDoors) {
            this.present.getChild("left_door").yaw = (door.isLeftOpen() || door.isOpen()) ? 8F : 0.0F;
            this.present.getChild("right_door").yaw = (door.isRightOpen() || door.areBothOpen())
                    ? -8F
                    : 0.0F;
        } else {
            float maxRot = 90f;
            this.present.getChild("left_door").yaw = (float) Math.toRadians(maxRot*door.getLeftRot());
            this.present.getChild("right_door").yaw = (float) -Math.toRadians(maxRot*door.getRightRot());
        }

        matrices.push();
        matrices.translate(0, -1.5, 0);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180));

        super.renderWithAnimations(tardis, linkableBlockEntity, root, matrices, vertices, light, overlay, red, green, blue, pAlpha, tickDelta);
        matrices.pop();
    }

    @Override
    public ModelPart getPart() {
        return present;
    }
}