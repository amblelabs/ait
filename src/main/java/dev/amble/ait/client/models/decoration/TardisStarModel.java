package dev.amble.ait.client.models.decoration; // Made with Blockbench 4.10.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class TardisStarModel extends SinglePartEntityModel {
    private final ModelPart star;

    public TardisStarModel(ModelPart root) {
        this.star = root.getChild("star");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData star = modelPartData.addChild("star",
                ModelPartBuilder.create().uv(0, 0)
                        .cuboid(-16.0F, -17.0F, -16.0F, 32.0F, 32.0F, 32.0F, new Dilation(0.0F)).uv(0, 64)
                        .cuboid(-16.0F, -17.0F, -16.0F, 32.0F, 32.0F, 32.0F, new Dilation(0.25F)),
                ModelTransform.of(0.0F, 9.0F, 0.0F, -0.6384F, -0.187F, -0.5372F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        star.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return star;
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw,
            float headPitch) {
    }
}
