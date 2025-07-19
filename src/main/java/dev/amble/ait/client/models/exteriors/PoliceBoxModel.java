package dev.amble.ait.client.models.exteriors;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.handler.DoorHandler;

public class PoliceBoxModel extends ExteriorModel {
    private final ModelPart TARDIS;

    public PoliceBoxModel(ModelPart root) {
        super(RenderLayer::getEntityCutoutNoCull);
        this.TARDIS = root.getChild("TARDIS");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData TARDIS = modelPartData.addChild("TARDIS", ModelPartBuilder.create().uv(0, 0).cuboid(-19.0F, -4.0F,
                -19.0F, 38.0F, 4.0F, 38.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData Posts = TARDIS.addChild("Posts", ModelPartBuilder.create().uv(46, 223).cuboid(-18.0F, -66.0F,
                -18.0F, 4.0F, 62.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r1 = Posts.addChild("cube_r1", ModelPartBuilder.create().uv(29, 198).cuboid(-18.0F, -66.0F,
                -18.0F, 4.0F, 62.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r2 = Posts.addChild("cube_r2", ModelPartBuilder.create().uv(210, 177).cuboid(-18.0F, -66.0F,
                -18.0F, 4.0F, 62.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r3 = Posts.addChild("cube_r3", ModelPartBuilder.create().uv(218, 41).cuboid(-18.0F, -66.0F,
                -18.0F, 4.0F, 62.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData Doors = TARDIS.addChild("Doors", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData right_door = Doors.addChild("right_door",
                ModelPartBuilder.create().uv(181, 177)
                        .cuboid(0.5F, -29.5F, -0.5F, 13.0F, 55.0F, 1.0F, new Dilation(0.0F)).uv(0, 198)
                        .cuboid(0.5F, -29.5F, -1.0F, 14.0F, 55.0F, 0.0F, new Dilation(0.0F)).uv(0, 10)
                        .cuboid(9.5F, -9.5F, -1.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)).uv(5, 51)
                        .cuboid(2.5F, -9.5F, -1.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-13.5F, -29.5F, -15.5F));

        ModelPartData left_door = Doors.addChild("left_door",
                ModelPartBuilder.create().uv(189, 41)
                        .cuboid(-13.5F, -29.5F, -0.5F, 13.0F, 55.0F, 1.0F, new Dilation(0.0F)).uv(0, 0)
                        .cuboid(-12.5F, -10.5F, -1.5F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)).uv(0, 51)
                        .cuboid(-12.5F, -4.5F, -1.5F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(13.5F, -29.5F, -15.5F));

        ModelPartData Walls = TARDIS.addChild("Walls",
                ModelPartBuilder.create().uv(129, 15)
                        .cuboid(-16.0F, -60.0F, -14.0F, 1.0F, 56.0F, 28.0F, new Dilation(0.0F)).uv(59, 142)
                        .cuboid(-16.5F, -60.0F, -14.0F, 0.0F, 56.0F, 28.0F, new Dilation(0.0F)).uv(63, 227)
                        .cuboid(-14.0F, -60.0F, -16.0F, 1.0F, 56.0F, 1.0F, new Dilation(0.0F)).uv(116, 170)
                        .cuboid(13.0F, -60.0F, -16.0F, 1.0F, 56.0F, 1.0F, new Dilation(0.0F)).uv(115, 0)
                        .cuboid(-13.0F, -60.0F, -16.0F, 26.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(59, 113)
                        .cuboid(13.0F, -60.0F, -16.5F, 1.0F, 56.0F, 0.0F, new Dilation(0.0F)).uv(115, 3)
                        .cuboid(-13.0F, -60.0F, -16.5F, 26.0F, 1.0F, 0.0F, new Dilation(0.0F)).uv(62, 113)
                        .cuboid(-14.0F, -60.0F, -16.5F, 1.0F, 56.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData Wall_r1 = Walls.addChild("Wall_r1",
                ModelPartBuilder.create().uv(160, 72)
                        .cuboid(-16.5F, -60.0F, -14.0F, 0.0F, 56.0F, 28.0F, new Dilation(0.0F)).uv(93, 85)
                        .cuboid(-16.0F, -60.0F, -14.0F, 1.0F, 56.0F, 28.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData Wall_r2 = Walls.addChild("Wall_r2",
                ModelPartBuilder.create().uv(124, 142)
                        .cuboid(-16.75F, -60.0F, -14.0F, 0.0F, 56.0F, 28.0F, new Dilation(0.0F)).uv(0, 113)
                        .cuboid(-16.0F, -60.0F, -14.0F, 1.0F, 56.0F, 28.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData PCB = TARDIS.addChild("PCB", ModelPartBuilder.create().uv(181, 167).cuboid(-17.0F, -64.0F, -19.0F,
                34.0F, 5.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        ModelPartData cube_r4 = PCB.addChild("cube_r4", ModelPartBuilder.create().uv(153, 157).cuboid(-17.0F, -61.0F,
                -19.0F, 34.0F, 5.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -3.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r5 = PCB.addChild("cube_r5", ModelPartBuilder.create().uv(160, 21).cuboid(-17.0F, -61.0F,
                -19.0F, 34.0F, 5.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -3.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r6 = PCB.addChild("cube_r6", ModelPartBuilder.create().uv(160, 31).cuboid(-17.0F, -61.0F,
                -19.0F, 34.0F, 5.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -3.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData Roof = TARDIS.addChild("Roof",
                ModelPartBuilder.create().uv(0, 43)
                        .cuboid(-16.0F, -68.0F, -16.0F, 32.0F, 4.0F, 32.0F, new Dilation(0.0F)).uv(0, 43)
                        .cuboid(-17.0F, -67.5F, -17.0F, 3.0F, 4.0F, 3.0F, new Dilation(0.05F)).uv(22, 7)
                        .cuboid(-17.0F, -67.5F, 14.0F, 3.0F, 4.0F, 3.0F, new Dilation(0.05F)).uv(0, 30)
                        .cuboid(14.0F, -67.5F, -17.0F, 3.0F, 4.0F, 3.0F, new Dilation(0.05F)).uv(17, 26)
                        .cuboid(14.0F, -67.5F, 14.0F, 3.0F, 4.0F, 3.0F, new Dilation(0.05F)).uv(0, 80)
                        .cuboid(-15.0F, -70.0F, -15.0F, 30.0F, 2.0F, 30.0F, new Dilation(0.0F)).uv(0, 0)
                        .cuboid(-3.0F, -72.0F, -3.0F, 6.0F, 3.0F, 6.0F, new Dilation(0.0F)).uv(0, 10)
                        .cuboid(-3.0F, -78.0F, -3.0F, 6.0F, 1.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        ModelPartData cube_r7 = Roof.addChild("cube_r7",
                ModelPartBuilder.create().uv(17, 18).cuboid(-2.0F, -70.75F, -2.0F, 4.0F, 3.0F, 4.0F, new Dilation(0.4F))
                        .uv(0, 18).cuboid(-2.0F, -73.75F, -2.0F, 4.0F, 7.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -5.25F, 0.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData TARDIS_t = TARDIS.addChild("TARDIS_t", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData PCB_t = TARDIS_t.addChild("PCB_t", ModelPartBuilder.create().uv(0, 394).cuboid(-16.0F, -64.0F,
                -19.0F, 32.0F, 5.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        ModelPartData cube_r8 = PCB_t.addChild("cube_r8", ModelPartBuilder.create().uv(0, 404).cuboid(-16.0F, -61.0F,
                -19.0F, 32.0F, 5.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -3.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r9 = PCB_t.addChild("cube_r9", ModelPartBuilder.create().uv(0, 414).cuboid(-16.0F, -61.0F,
                -19.0F, 32.0F, 5.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -3.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r10 = PCB_t.addChild("cube_r10", ModelPartBuilder.create().uv(0, 424).cuboid(-16.0F, -61.0F,
                -19.0F, 32.0F, 5.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -3.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData Roof_t = TARDIS_t.addChild("Roof_t",
                ModelPartBuilder.create().uv(0, 294)
                        .cuboid(-17.0F, -66.5F, -17.0F, 34.0F, 3.0F, 34.0F, new Dilation(0.0F)).uv(0, 332)
                        .cuboid(-15.0F, -68.25F, -15.0F, 30.0F, 2.0F, 30.0F, new Dilation(0.0F)).uv(0, 365)
                        .cuboid(-13.0F, -70.0F, -13.0F, 26.0F, 2.0F, 26.0F, new Dilation(0.0F)).uv(13, 65)
                        .cuboid(-17.5F, -65.75F, -17.5F, 3.0F, 1.0F, 3.0F, new Dilation(0.05F)).uv(0, 58)
                        .cuboid(14.5F, -65.75F, -17.5F, 3.0F, 1.0F, 3.0F, new Dilation(0.05F)).uv(13, 58)
                        .cuboid(-17.5F, -65.75F, 14.5F, 3.0F, 1.0F, 3.0F, new Dilation(0.05F)).uv(0, 65)
                        .cuboid(14.5F, -65.75F, 14.5F, 3.0F, 1.0F, 3.0F, new Dilation(0.05F)).uv(0, 295)
                        .cuboid(-3.0F, -71.0F, -3.0F, 6.0F, 1.0F, 6.0F, new Dilation(0.0F)).uv(0, 303)
                        .cuboid(-2.0F, -72.0F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F)).uv(0, 309)
                        .cuboid(-2.0F, -78.0F, -2.0F, 4.0F, 2.0F, 4.0F, new Dilation(0.0F)).uv(9, 315)
                        .cuboid(0.0F, -76.5F, -3.0F, 0.0F, 5.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -1.25F, 0.0F));

        ModelPartData cube_r11 = Roof_t.addChild("cube_r11",
                ModelPartBuilder.create().uv(9, 316).cuboid(-1.0F, -75.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.5F))
                        .uv(0, 321).cuboid(-1.0F, -76.0F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.2F)).uv(0, 316)
                        .cuboid(-1.0F, -79.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r12 = Roof_t.addChild("cube_r12",
                ModelPartBuilder.create().uv(9, 315).cuboid(0.0F, -76.5F, -3.0F, 0.0F, 5.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));
        return TexturedModelData.of(modelData, 512, 512);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        TARDIS.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return TARDIS;
    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        matrices.push();
        matrices.scale(0.63F, 0.63F, 0.63F);
        matrices.translate(0, -1.5f, 0);

        this.renderDoors(tardis, exterior, root, matrices, vertices, light, overlay, red, green, blue, pAlpha, false);

        super.renderWithAnimations(tardis, exterior, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
        matrices.pop();
    }

    @Override
    public <T extends Entity & Linkable> void renderEntity(T falling, ModelPart root, MatrixStack matrices,
                                                           VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        if (!falling.isLinked())
            return;

        matrices.push();
        matrices.scale(0.63F, 0.63F, 0.63F);
        matrices.translate(0, -1.5f, 0);

        DoorHandler door = falling.tardis().get().door();

        if (!AITModClient.CONFIG.animateDoors) {
            this.TARDIS.getChild("Doors").getChild("left_door").yaw = (door.isLeftOpen() || door.isOpen()) ? -5F : 0.0F;
            this.TARDIS.getChild("Doors").getChild("right_door").yaw = (door.isRightOpen() || door.areBothOpen())
                    ? 5F
                    : 0.0F;
        } else {
            float maxRot = 90f;
            this.TARDIS.getChild("Doors").getChild("left_door").yaw =(float) Math.toRadians(maxRot*door.getLeftRot());
            this.TARDIS.getChild("Doors").getChild("right_door").yaw =(float) -Math.toRadians(maxRot*door.getRightRot());
        }

        super.renderEntity(falling, root, matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }

    @Override
    public Animation getAnimationForDoorState(DoorHandler.AnimationDoorState state) {
        return Animation.Builder.create(0).build();
    }

    @Override
    public void renderDoors(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, boolean isBOTI) {
        DoorHandler door = tardis.door();

        if (!AITModClient.CONFIG.animateDoors) {
            this.TARDIS.getChild("Doors").getChild("left_door").yaw = (door.isLeftOpen() || door.isOpen()) ? -5F : 0.0F;
            this.TARDIS.getChild("Doors").getChild("right_door").yaw = (door.isRightOpen() || door.areBothOpen())
                    ? 5F
                    : 0.0F;
        } else {
            float maxRot = 90f;
            this.TARDIS.getChild("Doors").getChild("left_door").yaw =(float) Math.toRadians(maxRot*door.getLeftRot());
            this.TARDIS.getChild("Doors").getChild("right_door").yaw =(float) -Math.toRadians(maxRot*door.getRightRot());
        }

        if (isBOTI) {
            matrices.push();
            matrices.scale(0.63F, 0.63F, 0.63F);
            matrices.translate(0, 0f, -0.01);
            this.TARDIS.getChild("Doors").render(matrices, vertices, light, overlay, red, green, blue, pAlpha);
            matrices.pop();
        }
    }
}
