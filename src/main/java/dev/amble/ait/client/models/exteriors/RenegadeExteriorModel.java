package dev.amble.ait.client.models.exteriors;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;

public class RenegadeExteriorModel extends SimpleExteriorModel {
    private final ModelPart renegade;

    public RenegadeExteriorModel(ModelPart root) {
        this.renegade = root.getChild("renegade");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData renegade = modelPartData.addChild("renegade", ModelPartBuilder.create().uv(0, 19).cuboid(-12.0F,
                -1.7F, -8.5F, 24.0F, 2.0F, 17.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData walls = renegade.addChild("walls",
                ModelPartBuilder.create().uv(59, 61).cuboid(8.0F, -38.0F, -8.0F, 3.0F, 36.0F, 16.0F, new Dilation(0.0F))
                        .uv(0, 77).cuboid(7.0F, -37.0F, -7.0F, 5.0F, 35.0F, 14.0F, new Dilation(0.0F)).uv(98, 98)
                        .cuboid(12.25F, -37.0F, -6.0F, 0.0F, 35.0F, 12.0F, new Dilation(0.0F)).uv(98, 98)
                        .cuboid(-11.25F, -37.0F, -6.0F, 0.0F, 35.0F, 12.0F, new Dilation(0.0F)).uv(59, 61)
                        .cuboid(-10.0F, -38.0F, -8.0F, 3.0F, 36.0F, 16.0F, new Dilation(0.0F)).uv(83, 5)
                        .cuboid(-11.0F, -37.0F, -7.0F, 5.0F, 35.0F, 14.0F, new Dilation(0.0F)).uv(98, 55)
                        .cuboid(-6.0F, -37.0F, 0.0F, 13.0F, 35.0F, 7.0F, new Dilation(0.0F)).uv(68, 114)
                        .cuboid(-6.0F, -37.0F, 7.25F, 13.0F, 34.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-0.5F, 0.3F, 0.0F));

        ModelPartData roof = renegade.addChild("roof",
                ModelPartBuilder.create().uv(0, 0)
                        .cuboid(-12.5F, -40.75F, -8.5F, 26.0F, 1.0F, 17.0F, new Dilation(0.2F)).uv(0, 39)
                        .cuboid(-12.0F, -39.0F, -8.0F, 25.0F, 1.0F, 16.0F, new Dilation(0.2F)).uv(0, 57)
                        .cuboid(-11.0F, -42.0F, -7.0F, 23.0F, 5.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-0.5F, 0.3F, 0.0F));

        ModelPartData door = renegade.addChild("door",
                ModelPartBuilder.create().uv(39, 114)
                        .cuboid(-0.5F, -35.0F, -0.75F, 13.0F, 35.0F, 1.0F, new Dilation(0.0F)).uv(122, 0)
                        .cuboid(0.0F, -34.5F, -1.0F, 12.0F, 34.0F, 0.0F, new Dilation(0.0F)).uv(122, 0)
                        .cuboid(0.0F, -34.5F, 0.5F, 12.0F, 34.0F, 0.0F, new Dilation(0.0F)).uv(123, 98)
                        .cuboid(1.0F, -27.5F, -1.05F, 10.0F, 20.0F, 1.0F, new Dilation(-0.0499F)).uv(70, 0)
                        .cuboid(1.0F, -22.5F, -1.3F, 10.0F, 10.0F, 1.0F, new Dilation(-0.05F)),
                ModelTransform.pivot(-6.0F, -1.7F, -6.0F));

        ModelPartData cube_r1 = door.addChild("cube_r1",
                ModelPartBuilder.create().uv(42, 77)
                        .cuboid(3.55F, 3.55F, -0.75F, 7.0F, 7.0F, 1.0F, new Dilation(0.001F)).uv(25, 77)
                        .cuboid(0.0F, 0.0F, -1.0F, 7.0F, 7.0F, 1.0F, new Dilation(0.001F)).uv(66, 19)
                        .cuboid(-3.5F, -3.5F, -1.25F, 7.0F, 7.0F, 1.0F, new Dilation(0.0F)).uv(0, 19)
                        .cuboid(-7.0F, -7.0F, -1.0F, 7.0F, 7.0F, 1.0F, new Dilation(0.001F)).uv(0, 0)
                        .cuboid(-10.55F, -10.55F, -0.75F, 7.0F, 7.0F, 1.0F, new Dilation(0.001F)),
                ModelTransform.of(6.0F, -17.5F, -0.25F, 3.1416F, 0.0F, 2.3562F));

        ModelPartData cube_r2 = door.addChild("cube_r2",
                ModelPartBuilder.create().uv(70, 0)
                        .cuboid(-5.0F, -5.0F, -1.05F, 10.0F, 10.0F, 1.0F, new Dilation(-0.05F)).uv(123, 98)
                        .cuboid(-5.0F, -10.0F, -0.8F, 10.0F, 20.0F, 1.0F, new Dilation(-0.0499F)),
                ModelTransform.of(6.0F, -17.5F, -0.25F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r3 = door.addChild("cube_r3",
                ModelPartBuilder.create().uv(42, 77)
                        .cuboid(3.55F, 3.55F, -0.75F, 7.0F, 7.0F, 1.0F, new Dilation(0.001F)).uv(0, 19)
                        .cuboid(-7.0F, -7.0F, -1.0F, 7.0F, 7.0F, 1.0F, new Dilation(0.001F)).uv(25, 77)
                        .cuboid(0.0F, 0.0F, -1.0F, 7.0F, 7.0F, 1.0F, new Dilation(0.001F)).uv(66, 19)
                        .cuboid(-3.5F, -3.5F, -1.25F, 7.0F, 7.0F, 1.0F, new Dilation(0.0F)).uv(0, 0)
                        .cuboid(-10.55F, -10.55F, -0.75F, 7.0F, 7.0F, 1.0F, new Dilation(0.001F)),
                ModelTransform.of(6.0F, -17.5F, -0.25F, 0.0F, 0.0F, 0.7854F));
        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        renegade.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
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
        matrices.push();
        matrices.translate(0, -1.5f, 0);

        super.renderEntity(falling, root, matrices, vertexConsumer, light, overlay, red, green, blue, alpha);

        matrices.pop();
    }

    @Override
    public ModelPart getPart() {
        return renegade;
    }

    @Override
    public void renderDoors(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, boolean isBOTI) {
        if (!AITModClient.CONFIG.animateDoors)
            renegade.getChild("door").yaw = tardis.door().isOpen() ? 1.75f : 0f;
        else {
            renegade.getChild("door").yaw = (float) Math.toRadians(90f * tardis.door().getLeftRot());
        }

        if (isBOTI) {
            matrices.push();
            matrices.translate(0, -1.5f, 0);
            renegade.getChild("door").render(matrices, vertices, light, overlay, red, green, blue, pAlpha);
            matrices.pop();
        }
    }
}
