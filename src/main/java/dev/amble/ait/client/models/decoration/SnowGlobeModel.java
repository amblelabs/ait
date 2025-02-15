package dev.amble.ait.client.models.decoration;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class SnowGlobeModel extends SinglePartEntityModel {
    private final ModelPart group;
    public SnowGlobeModel(ModelPart root) {
        this.group = root.getChild("group");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData group = modelPartData.addChild("group", ModelPartBuilder.create().uv(0, 42).cuboid(-5.5F, -11.0F, -6.5F, 11.0F, 11.0F, 11.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-6.0F, 0.0F, -7.0F, 12.0F, 4.0F, 12.0F, new Dilation(0.0F))
                .uv(12, 7).cuboid(-2.0F, -1.9F, 3.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(20, 16).cuboid(1.0F, -3.0F, -6.5F, 0.0F, 3.0F, 11.0F, new Dilation(0.0F))
                .uv(45, 45).cuboid(-5.0F, -8.0F, -5.5F, 4.0F, 8.0F, 7.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-3.0F, -9.0F, -3.5F, 2.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 20.0F, 1.0F));

        ModelPartData cube_r1 = group.addChild("cube_r1", ModelPartBuilder.create().uv(0, 69).cuboid(-4.0F, -2.0F, -4.5F, 2.0F, 3.0F, 7.0F, new Dilation(0.001F)), ModelTransform.of(-0.1858F, -10.0142F, -1.0F, 0.0F, 0.0F, -0.7854F));

        ModelPartData cube_r2 = group.addChild("cube_r2", ModelPartBuilder.create().uv(0, 16).cuboid(-1.0F, 1.0F, -4.5F, 3.0F, 2.0F, 7.0F, new Dilation(0.002F)), ModelTransform.of(-5.1355F, -9.3071F, -1.0F, 0.0F, 0.0F, -0.7854F));

        ModelPartData cube_r3 = group.addChild("cube_r3", ModelPartBuilder.create().uv(12, 7).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-1.5F, -0.5F, 3.5F, 0.0F, -0.7854F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        group.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return group;
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }
}