package dev.amble.ait.client.models.exteriors;

import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.handler.DoorHandler;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

// Made with Blockbench 4.9.1
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class TardimExteriorModel extends ExteriorModel {
    private final ModelPart tardis;

    public TardimExteriorModel(ModelPart root) {
        super(RenderLayer::getEntityCutoutNoCull);
        this.tardis = root.getChild("tardis");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData tardis = modelPartData.addChild("tardis",
                ModelPartBuilder.create().uv(62, 58)
                        .cuboid(-11.0F, -32.0F, -8.0F, 3.0F, 32.0F, 16.0F, new Dilation(0.0F)).uv(0, 0)
                        .cuboid(-8.0F, -40.0F, -8.0F, 16.0F, 8.0F, 16.0F, new Dilation(0.0F)).uv(78, 26)
                        .cuboid(-8.0F, -0.02F, -8.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)).uv(62, 9)
                        .cuboid(-8.0F, 0.02F, -8.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData cube_r1 = tardis.addChild("cube_r1", ModelPartBuilder.create().uv(0, 25).cuboid(-11.0F, -32.0F,
                -8.0F, 3.0F, 32.0F, 16.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r2 = tardis.addChild("cube_r2", ModelPartBuilder.create().uv(39, 25).cuboid(-11.0F, -32.0F,
                -8.0F, 3.0F, 32.0F, 16.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData left_door = tardis.addChild("left_door", ModelPartBuilder.create().uv(23, 74).cuboid(-6.5F,
                -32.0F, -1.5F, 8.0F, 32.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(6.5F, 0.0F, -9.5F));

        ModelPartData right_door = tardis.addChild("right_door",
                ModelPartBuilder.create().uv(0, 74).cuboid(-1.5F, -32.0F, -1.5F, 8.0F, 32.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-6.5F, 0.0F, -9.5F));
        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        tardis.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return tardis;
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
    public Animation getAnimationForDoorState(DoorHandler.AnimationDoorState state) {
        return Animation.Builder.create(0).build();
    }

    @Override
    public void renderDoors(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, boolean isBOTI) {
        if (!AITModClient.CONFIG.animateDoors) {
            DoorHandler handler = tardis.door();

            this.tardis.getChild("left_door").yaw = (handler.isLeftOpen() || handler.isOpen()) ? -1.575f : 0.0F;
            this.tardis.getChild("right_door").yaw = (handler.isRightOpen() || handler.areBothOpen()) ? 1.575f : 0.0F;
        } else {
            float maxRot = 90f;
            this.tardis.getChild("left_door").yaw = -(float) Math.toRadians(maxRot * tardis.door().getLeftRot());
            this.tardis.getChild("right_door").yaw = (float) Math.toRadians(maxRot * tardis.door().getRightRot());
        }

        if (isBOTI) {
            matrices.push();
            matrices.translate(0, -1.5f, 0);
            this.tardis.getChild("left_door").render(matrices, vertices, light, overlay, red, green, blue, pAlpha);
            this.tardis.getChild("right_door").render(matrices, vertices, light, overlay, red, green, blue, pAlpha);
            matrices.pop();
        }
    }

    @Override
    public <T extends Entity & Linkable> void renderEntity(T falling, ModelPart root, MatrixStack matrices,
                                                           VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        if (falling.tardis().isEmpty())
            return;

        matrices.push();
        matrices.translate(0, -1.5f, 0);

        if (!AITModClient.CONFIG.animateDoors) {
            DoorHandler handler = falling.tardis().get().door();

            this.tardis.getChild("left_door").yaw = (handler.isLeftOpen() || handler.isOpen()) ? -1.575f : 0.0F;
            this.tardis.getChild("right_door").yaw = (handler.isRightOpen() || handler.areBothOpen()) ? 1.575f : 0.0F;
        } else {
            float maxRot = 90f;
            this.tardis.getChild("left_door").yaw = -(float) Math.toRadians(maxRot * falling.tardis().get().door().getLeftRot());
            this.tardis.getChild("right_door").yaw = (float) Math.toRadians(maxRot * falling.tardis().get().door().getRightRot());
        }

        super.renderEntity(falling, root, matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }
}
