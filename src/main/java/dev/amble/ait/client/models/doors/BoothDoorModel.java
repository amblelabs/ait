package dev.amble.ait.client.models.doors;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.models.exteriors.BoothExteriorModel;
import dev.amble.ait.client.tardis.ClientTardis;

public class BoothDoorModel extends DoorModel {

    private final ModelPart k2;

    public BoothDoorModel(ModelPart root) {
        super(RenderLayer::getEntityCutoutNoCull);
        this.k2 = root.getChild("k2");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData k2 = modelPartData.addChild("k2", ModelPartBuilder.create(),
                ModelTransform.pivot(0.5F, 26.0F, 1.0F));

        ModelPartData Posts = k2.addChild("Posts", ModelPartBuilder.create().uv(58, 103).cuboid(-9.0F, -36.0F, -9.0F,
                2.0F, 34.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        Posts.addChild("cube_r1", ModelPartBuilder.create().uv(58, 103).cuboid(-9.0F, -36.0F, -8.0F, 2.0F, 34.0F, 2.0F,
                new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        k2.addChild("Roof",
                ModelPartBuilder.create().uv(106, 8).cuboid(-9.0F, -37.0F, -9.0F, 17.0F, 2.0F, 2.0F, new Dilation(0.0F))
                        .uv(106, 13).cuboid(-9.0F, -37.0F, -9.0F, 17.0F, 2.0F, 2.0F, new Dilation(0.25F)).uv(106, 0)
                        .cuboid(-9.0F, -45.0F, -9.0F, 17.0F, 5.0F, 2.0F, new Dilation(0.0F)).uv(122, 21)
                        .cuboid(-9.0F, -45.0F, -9.0F, 17.0F, 4.0F, 2.0F, new Dilation(0.4F)).uv(122, 28)
                        .cuboid(-8.5F, -40.25F, -8.5F, 16.0F, 4.0F, 1.0F, new Dilation(0.0F)).uv(102, 60)
                        .cuboid(-8.5F, -40.75F, -8.5F, 16.0F, 1.0F, 1.0F, new Dilation(0.3F)),
                ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        k2.addChild("Door",
                ModelPartBuilder.create().uv(65, 69)
                        .cuboid(0.0F, -20.0F, -0.25F, 13.0F, 34.0F, 1.0F, new Dilation(0.0F)).uv(0, 4)
                        .cuboid(11.5F, -5.0F, -0.85F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)).uv(0, 0)
                        .cuboid(11.0F, -5.5F, -0.35F, 2.0F, 3.0F, 0.0F, new Dilation(0.0F)).uv(94, 104)
                        .cuboid(0.5F, -19.5F, 0.25F, 12.0F, 33.0F, 0.0F, new Dilation(0.01F)),
                ModelTransform.pivot(-7.0F, -16.0F, -9.0F));

        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public ModelPart getPart() {
        return k2;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        BoothExteriorModel boothExteriorModel = new BoothExteriorModel(BoothExteriorModel.getTexturedModelData().createModel());
        ModelPart part = boothExteriorModel.getPart();
        part.getChild("Door").visible = false;
        part.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

	@Override
    public void renderWithAnimations(ClientTardis tardis, AbstractLinkableBlockEntity linkableBlockEntity, ModelPart root, MatrixStack matrices,
	                                 VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, float tickDelta) {
        matrices.push();
        if (!AITModClient.CONFIG.animateDoors)
            this.k2.getChild("Door").yaw = tardis.door().isOpen() ? 1.575F : 0.0F;
        else {
            float maxRot = 90f;
            this.k2.getChild("Door").yaw = (float) Math.toRadians(maxRot*tardis.door().getLeftRot());
        }

        matrices.scale(1f, 1f, 1f);
        matrices.translate(0, -1.5f, 0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));

        super.renderWithAnimations(tardis, linkableBlockEntity, root, matrices, vertices, light, overlay, red, green, blue, pAlpha, tickDelta);
        matrices.pop();
    }
}
