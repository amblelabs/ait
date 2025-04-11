package dev.amble.ait.client.models.machines;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
public class RoundelFabricatorModel extends SinglePartEntityModel {
    private final ModelPart fabricator;
    public RoundelFabricatorModel(ModelPart root) {
        this.fabricator = root.getChild("fabricator");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData fabricator = modelPartData.addChild("fabricator", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -2.0F, -8.0F, 16.0F, 2.0F, 16.0F, new Dilation(0.0F))
                .uv(0, 19).cuboid(-7.0F, -3.0F, -7.0F, 14.0F, 1.0F, 5.0F, new Dilation(0.0F))
                .uv(11, 44).cuboid(-6.0F, -3.5F, -6.0F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(0, 26).cuboid(-1.0F, -4.0F, -1.0F, 8.0F, 2.0F, 8.0F, new Dilation(0.0F))
                .uv(33, 26).cuboid(-9.0F, -2.0F, -2.0F, 1.0F, 7.0F, 4.0F, new Dilation(0.0F))
                .uv(24, 38).cuboid(-2.0F, -2.0F, 8.0F, 4.0F, 7.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 37).cuboid(8.0F, -2.0F, -2.0F, 1.0F, 7.0F, 4.0F, new Dilation(0.0F))
                .uv(35, 38).cuboid(-2.0F, -2.0F, -9.0F, 4.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData group = fabricator.addChild("group", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r1 = group.addChild("cube_r1", ModelPartBuilder.create().uv(11, 37).cuboid(-6.5F, -3.5F, -2.0F, 1.0F, 1.0F, 5.0F, new Dilation(0.0F))
                .uv(39, 19).cuboid(-8.0F, -4.0F, 3.0F, 4.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.3927F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        fabricator.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return fabricator;
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }
}