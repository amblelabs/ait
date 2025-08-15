package dev.amble.ait.client.models.exteriors;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;

public class BoothExteriorModel extends SimpleExteriorModel {

    private final ModelPart k2;

    public BoothExteriorModel(ModelPart root) {
        this.k2 = root.getChild("k2");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData k2 = modelPartData.addChild("k2",
                ModelPartBuilder.create().uv(0, 0).cuboid(-9.5F, -2.0F, -9.5F, 18.0F, 2.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.5F, 24.0F, 0.5F));

        ModelPartData Posts = k2.addChild("Posts", ModelPartBuilder.create().uv(58, 103).cuboid(-9.0F, -36.0F, -9.0F,
                2.0F, 34.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r1 = Posts.addChild("cube_r1", ModelPartBuilder.create().uv(58, 103).cuboid(-8.0F, -36.0F,
                -9.0F, 2.0F, 34.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r2 = Posts.addChild("cube_r2", ModelPartBuilder.create().uv(58, 103).cuboid(-8.0F, -36.0F,
                -8.0F, 2.0F, 34.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r3 = Posts.addChild("cube_r3", ModelPartBuilder.create().uv(58, 103).cuboid(-9.0F, -36.0F,
                -8.0F, 2.0F, 34.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData Roof = k2.addChild("Roof",
                ModelPartBuilder.create().uv(52, 49)
                        .cuboid(-9.0F, -37.0F, -9.0F, 17.0F, 2.0F, 17.0F, new Dilation(0.0F)).uv(52, 27)
                        .cuboid(-9.0F, -37.0F, -9.0F, 17.0F, 2.0F, 17.0F, new Dilation(0.25F)).uv(0, 21)
                        .cuboid(-9.0F, -45.0F, -9.0F, 17.0F, 5.0F, 17.0F, new Dilation(0.0F)).uv(0, 44)
                        .cuboid(-9.0F, -45.0F, -9.0F, 17.0F, 4.0F, 17.0F, new Dilation(0.4F)).uv(57, 5)
                        .cuboid(-8.5F, -40.25F, -8.5F, 16.0F, 4.0F, 16.0F, new Dilation(0.0F)).uv(0, 66)
                        .cuboid(-8.5F, -40.75F, -8.5F, 16.0F, 1.0F, 16.0F, new Dilation(0.3F)),
                ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        ModelPartData Walls = k2.addChild("Walls", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r4 = Walls.addChild("cube_r4",
                ModelPartBuilder.create().uv(94, 104)
                        .cuboid(-5.5F, -35.5F, -8.75F, 12.0F, 33.0F, 0.0F, new Dilation(0.01F)).uv(0, 84)
                        .cuboid(-6.0F, -36.0F, -9.25F, 13.0F, 34.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r5 = Walls.addChild("cube_r5",
                ModelPartBuilder.create().uv(94, 104)
                        .cuboid(-6.5F, -35.5F, -7.75F, 12.0F, 33.0F, 0.0F, new Dilation(0.01F)).uv(0, 84)
                        .cuboid(-7.0F, -36.0F, -8.25F, 13.0F, 34.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r6 = Walls.addChild("cube_r6",
                ModelPartBuilder.create().uv(94, 69).cuboid(-6.0F, -36.0F, 8.0F, 13.0F, 34.0F, 0.0F, new Dilation(0.0F))
                        .uv(29, 84).cuboid(-6.0F, -36.0F, -8.25F, 13.0F, 34.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData Door = k2.addChild("Door",
                ModelPartBuilder.create().uv(65, 69)
                        .cuboid(0.0F, -20.0F, -0.25F, 13.0F, 34.0F, 1.0F, new Dilation(0.0F)).uv(0, 4)
                        .cuboid(11.5F, -5.0F, -0.85F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)).uv(0, 0)
                        .cuboid(11.0F, -5.5F, -0.35F, 2.0F, 3.0F, 0.0F, new Dilation(0.0F)).uv(94, 104)
                        .cuboid(0.5F, -19.5F, 0.25F, 12.0F, 33.0F, 0.0F, new Dilation(0.01F)),
                ModelTransform.pivot(-7.0F, -16.0F, -9.0F));
        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        k2.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        matrices.push();
        matrices.scale(1f, 1f, 1f);
        matrices.translate(0, -1.5f, 0);
        this.renderDoors(tardis, exterior, root, matrices, vertices, light, overlay, red, green, blue, pAlpha, false);

        super.renderWithAnimations(tardis, exterior, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
        matrices.pop();
    }

    @Override
    public void renderDoors(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, boolean isBOTI) {
        if (!AITModClient.CONFIG.animateDoors)
            this.k2.getChild("Door").yaw = tardis.door().isOpen() ? 1.575F : 0.0F;
        else {
            float maxRot = 90f;
            this.k2.getChild("Door").yaw = (float) Math.toRadians(maxRot * tardis.door().getLeftRot());
        }

        if (isBOTI) {
            matrices.push();
            matrices.scale(1f, 1f, 1f);
            matrices.translate(0, -1.5f, 0);
            this.k2.getChild("Door").render(matrices, vertices, light, overlay, red, green, blue, pAlpha);
            matrices.pop();
        }
    }

    @Override
    public <T extends Entity & Linkable> void renderEntity(T falling, ModelPart root, MatrixStack matrices,
                                                           VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        if (falling.tardis().isEmpty())
            return;

        matrices.push();
        if (!AITModClient.CONFIG.animateDoors)
            this.k2.getChild("Door").yaw = falling.tardis().get().door().isOpen() ? 1.575F : 0.0F;
        else {
            float maxRot = 90f;
            this.k2.getChild("Door").yaw = (float) Math.toRadians(maxRot * falling.tardis().get().door().getLeftRot());
        }
        matrices.scale(1f, 1f, 1f);
        matrices.translate(0, -1.5f, 0);

        super.renderEntity(falling, root, matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }

    @Override
    public ModelPart getPart() {
        return k2;
    }
}
