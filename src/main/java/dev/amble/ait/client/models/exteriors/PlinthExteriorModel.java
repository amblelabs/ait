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

public class PlinthExteriorModel extends ExteriorModel {
    private final ModelPart plinth;

    public PlinthExteriorModel(ModelPart root) {
        this.plinth = root.getChild("plinth");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData plinth = modelPartData.addChild("plinth", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData door = plinth.addChild("door", ModelPartBuilder.create().uv(72, 61).cuboid(-12.0F, -42.0F, 0.0F,
                12.0F, 42.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(6.0F, -3.0F, -8.0F));

        ModelPartData body = plinth.addChild("body",
                ModelPartBuilder.create().uv(0, 0).cuboid(6.0F, -45.0F, -8.0F, 2.0F, 42.0F, 16.0F, new Dilation(0.0F))
                        .uv(0, 58).cuboid(-9.0F, -3.0F, -9.0F, 18.0F, 3.0F, 18.0F, new Dilation(0.0F)).uv(54, 40)
                        .cuboid(-9.0F, -48.0F, -9.0F, 18.0F, 3.0F, 18.0F, new Dilation(0.0F)).uv(0, 79)
                        .cuboid(-6.0F, -45.0F, 6.0F, 12.0F, 42.0F, 2.0F, new Dilation(0.0F)).uv(36, 0)
                        .cuboid(-8.0F, -45.0F, -8.0F, 2.0F, 42.0F, 16.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        plinth.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
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

        if (!AITModClient.CONFIG.animateDoors)
            plinth.getChild("door").yaw = falling.tardis().get().door().isOpen() ? -1.75f : 0f;
        else {
            float maxRot = 90f;
            plinth.getChild("door").yaw = (float) Math.toRadians(maxRot*falling.tardis().get().door().getLeftRot());
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
        if (!AITModClient.CONFIG.animateDoors)
            plinth.getChild("door").yaw = tardis.door().isOpen() ? -1.75f : 0f;
        else {
            float maxRot = 90f;
            plinth.getChild("door").yaw = -(float) Math.toRadians(maxRot*tardis.door().getLeftRot());
        }

        if (isBOTI) {
            matrices.push();
            matrices.translate(0, -1.5f, 0);
            plinth.getChild("door").render(matrices, vertices, light, overlay, red, green, blue, pAlpha);
            matrices.pop();
        }
    }

    @Override
    public ModelPart getPart() {
        return plinth;
    }
}
