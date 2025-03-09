package dev.amble.ait.client.models.decoration;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class RiftModel extends SinglePartEntityModel {
    public ModelPart rift;

    public RiftModel(ModelPart root) {
        this.rift = root.getChild("rift");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData rift = modelPartData.addChild("rift", ModelPartBuilder.create().uv(0, 0).cuboid(-32.0F, -33.0F, 0.0F, 64.0F, 64.0F, 0.0F, new Dilation(0.01F)), ModelTransform.pivot(0.0F, -7.0F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }
    @Override
    public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        rift.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return rift;
    }
}
