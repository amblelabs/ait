package dev.amble.ait.client.models.exteriors;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.handler.DoorHandler;

public class ClassicHudolinExteriorModel extends ExteriorModel {
    private final ModelPart classic;

    public ClassicHudolinExteriorModel(ModelPart root) {
        this.classic = root.getChild("classic");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData classic = modelPartData.addChild("classic", ModelPartBuilder.create().uv(0, 0).cuboid(-18.0F,
                -2.0F, -18.0F, 36.0F, 3.0F, 36.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 23.0F, 0.0F));

        ModelPartData Posts_Hud = classic.addChild("Posts_Hud", ModelPartBuilder.create().uv(29, 189).cuboid(-17.0F,
                -62.0F, -17.0F, 4.0F, 60.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r1 = Posts_Hud.addChild("cube_r1", ModelPartBuilder.create().uv(169, 153).cuboid(-18.0F,
                -64.0F, -18.0F, 4.0F, 60.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(1.0F, 2.0F, -1.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r2 = Posts_Hud.addChild("cube_r2", ModelPartBuilder.create().uv(177, 21).cuboid(-18.0F,
                -64.0F, -18.0F, 4.0F, 60.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(-1.0F, 2.0F, -1.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r3 = Posts_Hud.addChild("cube_r3", ModelPartBuilder.create().uv(186, 153).cuboid(-17.0F,
                -64.0F, -18.0F, 4.0F, 60.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(-1.0F, 2.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData Doors = classic.addChild("Doors", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData right_door = Doors.addChild("right_door",
                ModelPartBuilder.create().uv(0, 189)
                        .cuboid(-0.5F, -25.5F, -0.5F, 13.0F, 53.0F, 1.0F, new Dilation(0.0F)).uv(203, 76)
                        .cuboid(-0.5F, -25.5F, -1.0F, 13.0F, 53.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-12.5F, -29.5F, -14.5F));

        ModelPartData left_door = Doors.addChild("left_door",
                ModelPartBuilder.create().uv(194, 21)
                        .cuboid(-12.5F, -25.5F, -0.5F, 13.0F, 53.0F, 1.0F, new Dilation(0.0F)).uv(30, 17)
                        .cuboid(-11.5F, -4.5F, -1.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(12.5F, -29.5F, -14.5F));

        ModelPartData Walls = classic.addChild("Walls",
                ModelPartBuilder.create().uv(120, 13)
                        .cuboid(-15.0F, -58.0F, -13.0F, 1.0F, 56.0F, 26.0F, new Dilation(0.0F)).uv(55, 138)
                        .cuboid(-15.5F, -57.0F, -13.0F, 0.0F, 55.0F, 26.0F, new Dilation(0.0F)).uv(29, 106)
                        .cuboid(-13.0F, -58.0F, -15.0F, 26.0F, 3.0F, 1.0F, new Dilation(0.0F)).uv(109, 1)
                        .cuboid(-13.0F, -57.0F, -15.5F, 26.0F, 2.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData Wall_r1 = Walls.addChild("Wall_r1",
                ModelPartBuilder.create().uv(150, 71)
                        .cuboid(-15.5F, -57.0F, -13.0F, 0.0F, 55.0F, 26.0F, new Dilation(0.0F)).uv(87, 80)
                        .cuboid(-15.0F, -58.0F, -13.0F, 1.0F, 56.0F, 26.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData Wall_r2 = Walls.addChild("Wall_r2",
                ModelPartBuilder.create().uv(116, 138)
                        .cuboid(-15.75F, -57.0F, -13.0F, 0.0F, 55.0F, 26.0F, new Dilation(0.0F)).uv(0, 106)
                        .cuboid(-15.0F, -58.0F, -13.0F, 1.0F, 56.0F, 26.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData PCB_Hud = classic.addChild("PCB_Hud", ModelPartBuilder.create().uv(237, 200).cuboid(-14.0F,
                -61.0F, -19.0F, 28.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r4 = PCB_Hud.addChild("cube_r4", ModelPartBuilder.create().uv(237, 173).cuboid(-14.0F,
                -58.0F, -19.0F, 28.0F, 4.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -3.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r5 = PCB_Hud.addChild("cube_r5", ModelPartBuilder.create().uv(237, 182).cuboid(-14.0F,
                -58.0F, -19.0F, 28.0F, 4.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -3.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r6 = PCB_Hud.addChild("cube_r6", ModelPartBuilder.create().uv(237, 191).cuboid(-14.0F,
                -58.0F, -19.0F, 28.0F, 4.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -3.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData Roof_Hud = classic.addChild("Roof_Hud",
                ModelPartBuilder.create().uv(0, 39)
                        .cuboid(-15.0F, -64.5F, -15.0F, 30.0F, 5.0F, 30.0F, new Dilation(0.0F)).uv(230, 18)
                        .cuboid(-14.0F, -55.0F, -14.0F, 28.0F, 0.0F, 28.0F, new Dilation(0.0F)).uv(17, 25)
                        .cuboid(-16.5F, -63.0F, -16.5F, 3.0F, 2.0F, 3.0F, new Dilation(0.05F)).uv(17, 19)
                        .cuboid(-16.5F, -63.0F, 13.5F, 3.0F, 2.0F, 3.0F, new Dilation(0.05F)).uv(19, 11)
                        .cuboid(13.5F, -63.0F, -16.5F, 3.0F, 2.0F, 3.0F, new Dilation(0.05F)).uv(19, 0)
                        .cuboid(13.5F, -63.0F, 13.5F, 3.0F, 2.0F, 3.0F, new Dilation(0.05F)).uv(0, 75)
                        .cuboid(-14.0F, -66.0F, -14.0F, 28.0F, 2.0F, 28.0F, new Dilation(0.0F)).uv(0, 0)
                        .cuboid(-3.0F, -68.0F, -3.0F, 6.0F, 4.0F, 6.0F, new Dilation(0.0F)).uv(0, 11)
                        .cuboid(-3.0F, -74.0F, -3.0F, 6.0F, 1.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r7 = Roof_Hud.addChild("cube_r7",
                ModelPartBuilder.create().uv(0, 19).cuboid(-2.0F, -72.75F, -2.0F, 4.0F, 7.0F, 4.0F, new Dilation(0.0F))
                        .uv(0, 45).cuboid(-3.5F, -70.75F, 0.0F, 7.0F, 5.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -2.25F, 0.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r8 = Roof_Hud.addChild("cube_r8",
                ModelPartBuilder.create().uv(0, 39).cuboid(-3.5F, -71.75F, 0.0F, 7.0F, 5.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -1.25F, 0.0F, 0.0F, -0.7854F, 0.0F));
        return TexturedModelData.of(modelData, 512, 512);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        classic.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return classic;
    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        matrices.push();
        matrices.scale(0.64F, 0.64F, 0.64F);
        matrices.translate(0, -1.5f, 0);

        this.renderDoors(tardis, exterior, root, matrices, vertices, light, overlay, red, green, blue, pAlpha, false);

        super.renderWithAnimations(tardis, exterior, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
        matrices.pop();
    }

    @Override
    public Animation getAnimationForDoorState(DoorHandler.AnimationDoorState state) {
        return Animation.Builder.create(0).build();
    }

    @Override
    public void renderDoors(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, boolean isBOTI) {
        if (!AITModClient.CONFIG.animateDoors) {
            DoorHandler door = tardis.door();

            this.classic.getChild("Doors").getChild("left_door").yaw = (door.isLeftOpen() || door.isOpen()) ? -5F : 0.0F;
            this.classic.getChild("Doors").getChild("right_door").yaw = (door.isRightOpen() || door.areBothOpen())
                    ? 5F
                    : 0.0F;
        } else {
            float maxRot = 90f;

            DoorHandler door = tardis.door();
            this.classic.getChild("Doors").getChild("left_door").yaw = (float) Math.toRadians(maxRot * door.getLeftRot());
            this.classic.getChild("Doors").getChild("right_door").yaw = -(float) Math.toRadians(maxRot * door.getRightRot());
        }

        if (isBOTI) {
            matrices.push();
            matrices.scale(0.64F, 0.64F, 0.64F);
            matrices.translate(0, -0.06f, 0);
            this.classic.getChild("Doors").render(matrices, vertices, light, overlay, red, green, blue, pAlpha);
            matrices.pop();
        }
    }

    @Override
    public <T extends Entity & Linkable> void renderEntity(T falling, ModelPart root, MatrixStack matrices,
                                                           VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        matrices.scale(0.64F, 0.64F, 0.64F);
        matrices.translate(0, -1.5f, 0);

        super.renderEntity(falling, root, matrices, vertexConsumer, light, overlay, red, green, blue, alpha);

        matrices.pop();
    }
}
