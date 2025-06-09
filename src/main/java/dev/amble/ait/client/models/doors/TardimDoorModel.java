package dev.amble.ait.client.models.doors;

import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.tardis.handler.DoorHandler;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

public class TardimDoorModel extends DoorModel {

    private final ModelPart tardis;

    public TardimDoorModel(ModelPart root) {
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

        tardis.addChild("cube_r1", ModelPartBuilder.create().uv(39, 25).cuboid(-11.0F, -32.0F, -8.0F, 3.0F, 32.0F,
                16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));
        tardis.addChild("left_door", ModelPartBuilder.create().uv(23, 74).cuboid(-6.5F, -32.0F, -1.5F, 8.0F, 32.0F,
                3.0F, new Dilation(0.001F)), ModelTransform.pivot(6.5F, 0.0F, -9.5F));
        tardis.addChild("right_door", ModelPartBuilder.create().uv(0, 74).cuboid(-1.5F, -32.0F, -1.5F, 8.0F, 32.0F,
                3.0F, new Dilation(0.001F)), ModelTransform.pivot(-6.5F, 0.0F, -9.5F));
        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        tardis.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
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
        return tardis;
    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, AbstractLinkableBlockEntity linkableBlockEntity, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        matrices.push();
        matrices.translate(0, -1.5f, 0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));

        if (!AITModClient.CONFIG.animateDoors) {
            DoorHandler handler = tardis.door();

            this.tardis.getChild("left_door").yaw = (handler.isLeftOpen() || handler.isOpen()) ? -1.575f : 0.0F;
            this.tardis.getChild("right_door").yaw = (handler.isRightOpen() || handler.areBothOpen()) ? 1.575f : 0.0F;
        } else {
            float maxRot = 90f;

            DoorHandler handler = tardis.door();
            this.tardis.getChild("left_door").yaw = (float) -Math.toRadians(maxRot*handler.getLeftRot());
            this.tardis.getChild("right_door").yaw = (float) Math.toRadians(maxRot*handler.getRightRot());
        }

        super.renderWithAnimations(tardis, linkableBlockEntity, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
        matrices.pop();
    }
}
