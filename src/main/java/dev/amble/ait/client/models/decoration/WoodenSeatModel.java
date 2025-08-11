package dev.amble.ait.client.models.decoration;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class WoodenSeatModel extends SinglePartEntityModel {
    private final ModelPart chair;

    public WoodenSeatModel(ModelPart root) {
        this.chair = root.getChild("chair");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData chair = modelPartData.addChild("chair", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, 6.6617F, -15.6515F, 16.0F, 3.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 9.3383F, 7.6515F));

        ModelPartData cube_r1 = chair.addChild("cube_r1", ModelPartBuilder.create().uv(0, 37).mirrored().cuboid(6.0F, -8.0F, -3.5F, 2.0F, 15.0F, 2.0F, new Dilation(-0.001F)).mirrored(false)
                .uv(0, 37).cuboid(-8.0F, -8.0F, -3.5F, 2.0F, 15.0F, 2.0F, new Dilation(-0.001F))
                .uv(63, 37).cuboid(-8.0F, -12.0F, -1.0F, 16.0F, 4.0F, 2.0F, new Dilation(-0.001F))
                .uv(48, 59).cuboid(-8.0F, -8.0F, -1.5F, 16.0F, 15.0F, 3.0F, new Dilation(-0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

        ModelPartData cube_r2 = chair.addChild("cube_r2", ModelPartBuilder.create().uv(24, 55).cuboid(-1.0F, -2.5F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-6.4F, 12.1617F, -14.2373F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r3 = chair.addChild("cube_r3", ModelPartBuilder.create().uv(16, 55).cuboid(-1.0F, -2.5F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-6.4F, 12.1617F, -1.0657F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r4 = chair.addChild("cube_r4", ModelPartBuilder.create().uv(8, 55).cuboid(-1.0F, -2.5F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(6.4F, 12.1617F, -1.0657F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r5 = chair.addChild("cube_r5", ModelPartBuilder.create().uv(48, 47).cuboid(-3.5F, -1.5F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.2F))
                .uv(0, 19).cuboid(-3.5F, -1.5F, -6.0F, 8.0F, 2.0F, 16.0F, new Dilation(-0.001F)), ModelTransform.of(7.3042F, 4.9696F, -9.6515F, 0.0F, 0.0F, 1.7453F));

        ModelPartData cube_r6 = chair.addChild("cube_r6", ModelPartBuilder.create().uv(48, 47).mirrored().cuboid(1.5F, -1.5F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.2F)).mirrored(false)
                .uv(0, 19).mirrored().cuboid(-4.5F, -1.5F, -6.0F, 8.0F, 2.0F, 16.0F, new Dilation(-0.001F)).mirrored(false), ModelTransform.of(-7.3042F, 4.9696F, -9.6515F, 0.0F, 0.0F, -1.7453F));

        ModelPartData cube_r7 = chair.addChild("cube_r7", ModelPartBuilder.create().uv(0, 55).cuboid(-1.0F, -2.5F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(6.4F, 12.1617F, -14.2373F, 0.0F, -0.7854F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }
    @Override
    public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        chair.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return chair;
    }

}