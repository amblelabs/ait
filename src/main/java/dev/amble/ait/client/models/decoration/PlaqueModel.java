package dev.amble.ait.client.models.decoration;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class PlaqueModel extends SinglePartEntityModel {
    public final ModelPart plaque;

    public PlaqueModel(ModelPart root) {
        this.plaque = root.getChild("plaque");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData plaque = modelPartData.addChild("plaque",
                ModelPartBuilder.create().uv(0, 14)
                        .cuboid(-13.0F, -12.5F, -1.25F, 26.0F, 13.0F, 3.0F, new Dilation(0.0F)).uv(0, 0)
                        .cuboid(-12.0F, -12.0F, -1.0F, 24.0F, 12.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 22.0F, 7.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        plaque.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return plaque;
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw,
            float headPitch) {
    }
}
