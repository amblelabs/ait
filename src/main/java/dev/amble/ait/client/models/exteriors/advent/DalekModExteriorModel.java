package dev.amble.ait.client.models.exteriors.advent;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.models.exteriors.SimpleExteriorModel;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.handler.DoorHandler;

public class DalekModExteriorModel extends SimpleExteriorModel {
    private final ModelPart dalekmod;

    public DalekModExteriorModel(ModelPart root) {
        this.dalekmod = root.getChild("dalekmod");

    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData dalekmod = modelPartData.addChild("dalekmod", ModelPartBuilder.create().uv(0, 82).cuboid(-12.0F, -1.0F, -12.0F, 24.0F, 1.0F, 24.0F, new Dilation(0.0F))
        .uv(0, 56).cuboid(-12.0F, -2.0F, -12.0F, 24.0F, 2.0F, 24.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData Posts = dalekmod.addChild("Posts", ModelPartBuilder.create().uv(0, 141).cuboid(8.0F, -40.0F, -11.0F, 3.0F, 39.0F, 3.0F, new Dilation(0.0F))
        .uv(0, 141).cuboid(-11.0F, -40.0F, -11.0F, 3.0F, 39.0F, 3.0F, new Dilation(0.0F))
        .uv(0, 141).cuboid(-11.0F, -40.0F, 8.0F, 3.0F, 39.0F, 3.0F, new Dilation(0.0F))
        .uv(0, 141).cuboid(8.0F, -40.0F, 8.0F, 3.0F, 39.0F, 3.0F, new Dilation(0.0F))
        .uv(12, 141).cuboid(8.0F, -38.0F, -11.0F, 3.0F, 37.0F, 3.0F, new Dilation(0.0F))
        .uv(12, 141).cuboid(-11.0F, -38.0F, -11.0F, 3.0F, 37.0F, 3.0F, new Dilation(0.0F))
        .uv(12, 141).cuboid(-11.0F, -38.0F, 8.0F, 3.0F, 37.0F, 3.0F, new Dilation(0.0F))
        .uv(12, 141).cuboid(8.0F, -38.0F, 8.0F, 3.0F, 37.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData Doors = dalekmod.addChild("Doors", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData right_door = Doors.addChild("right_door", ModelPartBuilder.create().uv(48, 141).cuboid(0.0F, -17.0F, 0.0F, 8.0F, 32.0F, 2.0F, new Dilation(0.0F))
        .uv(144, 128).cuboid(0.0F, -18.0F, 0.0F, 8.0F, 32.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-8.0F, -16.0F, -10.0F));

        ModelPartData left_door = Doors.addChild("left_door", ModelPartBuilder.create().uv(68, 162).cuboid(-8.0F, -17.0F, 0.0F, 8.0F, 32.0F, 2.0F, new Dilation(0.0F))
        .uv(88, 162).cuboid(-8.0F, -18.0F, 0.0F, 8.0F, 32.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, -16.0F, -10.0F));

        ModelPartData Walls = dalekmod.addChild("Walls", ModelPartBuilder.create().uv(0, 107).cuboid(-8.0F, -33.0F, 8.0F, 16.0F, 32.0F, 2.0F, new Dilation(0.0F))
        .uv(36, 107).cuboid(-8.0F, -34.0F, 8.0F, 16.0F, 32.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData LeftWall2px_r1 = Walls.addChild("LeftWall2px_r1", ModelPartBuilder.create().uv(36, 107).cuboid(-8.0F, -34.0F, 8.0F, 16.0F, 32.0F, 2.0F, new Dilation(0.0F))
        .uv(0, 107).cuboid(-8.0F, -33.0F, 8.0F, 16.0F, 32.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData RightWall2px_r1 = Walls.addChild("RightWall2px_r1", ModelPartBuilder.create().uv(36, 107).cuboid(-8.0F, -34.0F, 8.0F, 16.0F, 32.0F, 2.0F, new Dilation(0.0F))
        .uv(0, 107).cuboid(-8.0F, -33.0F, 8.0F, 16.0F, 32.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData PCB = dalekmod.addChild("PCB", ModelPartBuilder.create().uv(0, 0).cuboid(-12.0F, -37.0F, -12.0F, 24.0F, 4.0F, 24.0F, new Dilation(0.0F))
        .uv(0, 28).cuboid(-12.0F, -38.0F, -12.0F, 24.0F, 4.0F, 24.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData Roof = dalekmod.addChild("Roof", ModelPartBuilder.create().uv(96, 48).cuboid(-10.0F, -41.0F, -10.0F, 20.0F, 4.0F, 20.0F, new Dilation(0.0F))
        .uv(96, 72).cuboid(-10.0F, -39.0F, -10.0F, 20.0F, 2.0F, 20.0F, new Dilation(0.0F))
        .uv(96, 0).cuboid(-11.0F, -41.0F, -11.0F, 22.0F, 3.0F, 22.0F, new Dilation(0.0F))
        .uv(96, 25).cuboid(-11.0F, -38.0F, -11.0F, 22.0F, 1.0F, 22.0F, new Dilation(0.0F))
        .uv(72, 107).cuboid(-2.0F, -46.0F, -2.0F, 4.0F, 5.0F, 4.0F, new Dilation(0.0F))
        .uv(72, 116).cuboid(-2.0F, -44.0F, -2.0F, 4.0F, 5.0F, 4.0F, new Dilation(0.0F))
        .uv(156, 162).cuboid(-2.0F, -43.0F, -2.0F, 4.0F, 5.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        matrices.scale(1f, 1f, 1f);
        matrices.translate(0, -0.1, 0);
        dalekmod.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }

	@Override
    public void renderDoors(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, boolean isBOTI) {
        DoorHandler door = tardis.door();

        if (!AITModClient.CONFIG.animateDoors) {
            this.dalekmod.getChild("Doors").getChild("left_door").yaw = (door.isLeftOpen() || door.isOpen()) ? -5F : 0.0F;
            this.dalekmod.getChild("Doors").getChild("right_door").yaw = (door.isRightOpen() || door.areBothOpen())
                    ? 5F
                    : 0.0F;
        } else {
            this.dalekmod.getChild("Doors").getChild("left_door").yaw = (float) Math.toRadians(80f*door.getLeftRot());
            this.dalekmod.getChild("Doors").getChild("right_door").yaw = (float) -Math.toRadians(80f*door.getRightRot());
        }

        if (isBOTI) {
            matrices.push();
            matrices.scale(0.945F, 0.945F, 0.945F);
            matrices.translate(-0.002, -0.0012f, -0.004);
            this.dalekmod.getChild("Doors").render(matrices, vertices, light, overlay, red, green, blue, pAlpha);
            matrices.pop();
        }
    }

    @Override
    public <T extends Entity & Linkable> void renderEntity(T falling, ModelPart root, MatrixStack matrices,
                                                           VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        if (falling.tardis().isEmpty())
            return;

        matrices.push();
        matrices.scale(0.945F, 0.945F, 0.945F);
        matrices.translate(1, -1.5f, 0);

        DoorHandler door = falling.tardis().get().door();

        if (!AITModClient.CONFIG.animateDoors) {
            this.dalekmod.getChild("Doors").getChild("left_door").yaw = (door.isLeftOpen() || door.isOpen()) ? -5F : 0.0F;
            this.dalekmod.getChild("Doors").getChild("right_door").yaw = (door.isRightOpen() || door.areBothOpen())
                    ? 5F
                    : 0.0F;
        } else {
            float maxRot = 80;
            this.dalekmod.getChild("Doors").getChild("left_door").yaw = (float) Math.toRadians(maxRot*door.getLeftRot());
            this.dalekmod.getChild("Doors").getChild("right_door").yaw = (float) -Math.toRadians(maxRot*door.getRightRot());
        }

        super.renderEntity(falling, root, matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }

    @Override
    public ModelPart getPart() {
        return dalekmod;
    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        matrices.push();
        matrices.scale(0.945F, 0.945F, 0.945F);
        matrices.translate(0, -1.5f, 0);

        this.renderDoors(tardis, exterior, root, matrices, vertices, light, overlay, red, green, blue, pAlpha, false);

        super.renderWithAnimations(tardis, exterior, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
        matrices.pop();
    }
}