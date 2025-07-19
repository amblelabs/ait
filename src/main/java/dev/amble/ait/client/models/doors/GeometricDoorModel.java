package dev.amble.ait.client.models.doors;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.util.math.MatrixStack;

import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.tardis.handler.DoorHandler;

public class GeometricDoorModel extends DoorModel {

    private final ModelPart geometric;

    public GeometricDoorModel(ModelPart root) {
        this.geometric = root.getChild("geometric");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData geometric = modelPartData.addChild("geometric",
                ModelPartBuilder.create().uv(23, 23)
                        .cuboid(-16.0F, -44.0F, 7.0F, 32.0F, 44.0F, 1.0F, new Dilation(0.05F)).uv(48, 24)
                        .cuboid(-16.0F, -44.0F, 8.05F, 32.0F, 44.0F, 0.0F, new Dilation(0.005F)).uv(113, 0)
                        .cuboid(-8.0F, -38.0F, 6.9F, 16.0F, 36.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        geometric.addChild("door", ModelPartBuilder.create().uv(98, 97).cuboid(-8.0F, -40.0F, -7.0F, 16.0F, 40.0F, 2.0F,
                new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 1.0F, 0.0F, 3.1416F, 0.0F));
        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        geometric.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, AbstractLinkableBlockEntity doorEntity, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        DoorHandler door = tardis.door();

        this.geometric.getChild("door").pivotZ = door.isOpen() ? 5.05f : 1f;

        matrices.push();
        matrices.scale(1F, 1F, 1F);
        matrices.translate(0, -1.5, 0);

        super.renderWithAnimations(tardis, doorEntity, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
        matrices.pop();
    }

    @Override
    public Animation getAnimationForDoorState(DoorHandler.AnimationDoorState state) {
        return Animation.Builder.create(0).build();/*return switch (state) {
            case CLOSED -> DoorAnimations.INTERIOR_BOTH_CLOSE_ANIMATION;
            case FIRST -> DoorAnimations.INTERIOR_FIRST_OPEN_ANIMATION;
            case SECOND -> DoorAnimations.INTERIOR_SECOND_OPEN_ANIMATION;
            case BOTH -> DoorAnimations.INTERIOR_BOTH_OPEN_ANIMATION;
        };*/
    }

    @Override
    public ModelPart getPart() {
        return geometric;
    }

}
