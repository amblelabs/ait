package dev.amble.ait.client.models.wearables;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class SantaHatModel extends EntityModel {
    public final ModelPart hat;
    public SantaHatModel(ModelPart root) {
        this.hat = root.getChild("hat");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData hat = modelPartData.addChild("hat", ModelPartBuilder.create().uv(2, 0).cuboid(-4.0F, -9.4413F, -4.0F, 8.0F, 2.0F, 8.0F, new Dilation(0.4F))
                .uv(36, 38).cuboid(-1.0F, -13.1913F, 4.2381F, 2.0F, 2.0F, 2.0F, new Dilation(0.1F)), ModelTransform.pivot(0.0F, 23.9413F, 0.3741F));

        ModelPartData cube_r1 = hat.addChild("cube_r1", ModelPartBuilder.create().uv(32, 25).cuboid(-1.5F, -0.3667F, 0.0F, 4.0F, 3.0F, 4.0F, new Dilation(0.1F)), ModelTransform.of(-0.5F, -13.0747F, 0.8142F, 0.3927F, 0.0F, 0.0F));

        ModelPartData cube_r2 = hat.addChild("cube_r2", ModelPartBuilder.create().uv(2, 27).cuboid(-2.5F, -3.708F, -1.65F, 6.0F, 4.0F, 5.0F, new Dilation(0.1F)), ModelTransform.of(-0.5F, -10.7334F, -1.2851F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r3 = hat.addChild("cube_r3", ModelPartBuilder.create().uv(2, 14).cuboid(-3.5F, -3.35F, -2.65F, 8.0F, 4.0F, 7.0F, new Dilation(0.1F)), ModelTransform.of(-0.5F, -8.5913F, -1.5369F, -0.3927F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        hat.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    }
}