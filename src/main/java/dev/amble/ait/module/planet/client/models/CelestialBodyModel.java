package dev.amble.ait.module.planet.client.models;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class CelestialBodyModel extends SinglePartEntityModel {
    private final ModelPart body;

    public CelestialBodyModel(ModelPart root) {
        this.body = root.getChild("body");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData body = modelPartData.addChild("body",
                ModelPartBuilder.create().uv(0, 0).cuboid(-16.0F, -17.0F, -16.0F, 32.0F, 32.0F, 32.0F, new Dilation(0.0F)).uv(0, 64),
                ModelTransform.of(0.0F, 9.0F, 0.0F, -0f, -0f, -0f));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
                       float green, float blue, float alpha) {
        body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return body;
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw,
                          float headPitch) {
    }
}
