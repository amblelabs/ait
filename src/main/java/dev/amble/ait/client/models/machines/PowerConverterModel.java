package dev.amble.ait.client.models.machines;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class PowerConverterModel extends SinglePartEntityModel {
    private final ModelPart bone;

    private PowerConverterModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }
    public PowerConverterModel() {
        this(getTexturedModelData().createModel());
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -18.0F, -6.0F, 10.0F, 18.0F, 12.0F, new Dilation(0.0F))
                .uv(0, 48).cuboid(-5.0F, -13.0F, -7.5F, 10.0F, 3.0F, 2.0F, new Dilation(-0.1F))
                .uv(55, 50).cuboid(2.0F, -16.0F, -7.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 30).cuboid(-5.0F, -30.0F, -6.0F, 10.0F, 6.0F, 12.0F, new Dilation(0.0F))
                .uv(44, 0).cuboid(-5.0F, -24.0F, -4.0F, 10.0F, 6.0F, 10.0F, new Dilation(0.0F))
                .uv(44, 23).cuboid(-5.0F, -24.0F, -6.0F, 1.0F, 6.0F, 2.0F, new Dilation(0.0F))
                .uv(44, 31).cuboid(4.0F, -24.0F, -6.0F, 1.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData hacth = bone.addChild("hacth", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, 0.0F, 0.0F, 8.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -24.0F, -5.6F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public ModelPart getPart() {
        return bone;
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw,
                          float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
                       float green, float blue, float alpha) {
        bone.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}