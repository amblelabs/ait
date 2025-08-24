package dev.amble.ait.client.models.decoration;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

public class ToyotaSeatModel extends Model {
    private final ModelPart chair;

    public ToyotaSeatModel(ModelPart root) {
        super(RenderLayer::getEntityCutout);
        this.chair = root.getChild("chair");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData chair = modelPartData.addChild("chair", ModelPartBuilder.create().uv(0, 27).cuboid(-8.0F, -8.0F, -8.0F, 16.0F, 3.0F, 16.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData cube_r1 = chair.addChild("cube_r1", ModelPartBuilder.create().uv(1, 47).cuboid(-8.0F, -5.0F, 2.0F, 16.0F, 5.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-8.0F, -9.0F, -14.0F, 16.0F, 4.0F, 23.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -9.0F, 0.0F, -1.7017F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    public void setAngles(MatrixStack matrices, ModelTransformationMode renderMode) {
        if (renderMode == ModelTransformationMode.FIXED)
            return;
        matrices.translate(0.5, -1.25f, -0.5);
        matrices.scale(0.6f, 0.6f, 0.6f);

        if (renderMode == ModelTransformationMode.GUI) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(22.5f));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45f));
            matrices.translate(-0.1, 0.85f, 0);
            matrices.scale(0.8f, 0.8f, 0.8f);
        }

        if (renderMode == ModelTransformationMode.HEAD) {
            matrices.translate(0, -0.725f, 0);
            matrices.scale(2.725f, 2.725f, 2.725f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        }
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        chair.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}
