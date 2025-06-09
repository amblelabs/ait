package dev.amble.ait.client.models.doors;

import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.tardis.handler.DoorHandler;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

public class PipeDoorModel extends DoorModel {
    private final ModelPart tardis;
    public PipeDoorModel(ModelPart root) {
        this.tardis = root.getChild("tardis");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData tardis = modelPartData.addChild("tardis", ModelPartBuilder.create().uv(0, 0).cuboid(-7.5F, -14.5F, -7.0F, 15.0F, 7.0F, 15.0F, new Dilation(0.0F))
        .uv(0, 22).cuboid(-6.5F, -8.0F, -6.0F, 13.0F, 8.0F, 13.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -22.5F, -0.5F, 0.0F, 0.0F, -3.1416F));
        return TexturedModelData.of(modelData, 128, 128);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        tardis.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, AbstractLinkableBlockEntity linkableBlockEntity, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        matrices.push();
        matrices.translate(0, -1f, 0);
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90f));

        this.tardis.pivotY = !tardis.door().isOpen() ? -22f : -8f;
        super.renderWithAnimations(tardis, linkableBlockEntity, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
        matrices.pop();
    }

    @Override
    public ModelPart getPart() {
        return tardis;
    }

    @Override
    public Animation getAnimationForDoorState(DoorHandler.AnimationDoorState state) {
        return Animation.Builder.create(0).build();
    }
}