package dev.amble.ait.client.models.monitors;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RotationAxis;

public class CRTMonitorModel extends SinglePartEntityModel {
    private final ModelPart crt;

    public CRTMonitorModel(ModelPart root) {
        this.crt = root.getChild("crt");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData crt = modelPartData.addChild("crt", ModelPartBuilder.create(),
                ModelTransform.pivot(-4.0F, 24.0F, 4.0F));

        ModelPartData cube_r1 = crt.addChild("cube_r1",
                ModelPartBuilder.create().uv(0, 21).cuboid(-6.0F, -14.0F, -6.0F, 3.0F, 12.0F, 12.0F, new Dilation(0.0F))
                        .uv(19, 24).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F)).uv(31, 11)
                        .cuboid(-2.0F, -4.0F, -5.0F, 8.0F, 2.0F, 10.0F, new Dilation(0.0F)).uv(31, 24)
                        .cuboid(-3.0F, -4.0F, -5.5F, 1.0F, 2.0F, 11.0F, new Dilation(0.0F)).uv(0, 0)
                        .cuboid(-3.0F, -13.0F, -5.5F, 9.0F, 9.0F, 11.0F, new Dilation(0.0F)).uv(30, 0)
                        .cuboid(-4.0F, -1.0F, -4.0F, 8.0F, 1.0F, 8.0F, new Dilation(0.0F)),
                ModelTransform.of(4.0F, 0.0F, -4.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData gallifreyan = crt.addChild("gallifreyan", ModelPartBuilder.create().uv(31, 38).cuboid(-3.0F,
                -3.0F, -0.1F, 6.0F, 6.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(4.0F, -8.0F, -10.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        matrices.push();
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180));

        crt.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);

        matrices.pop();
    }

    @Override
    public ModelPart getPart() {
        return crt;
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw,
            float headPitch) {
    }
}
