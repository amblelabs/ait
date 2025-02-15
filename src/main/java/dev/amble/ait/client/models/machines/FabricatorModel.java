package dev.amble.ait.client.models.machines; // Made with Blockbench 4.10.1
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class FabricatorModel extends SinglePartEntityModel {
    private final ModelPart bone;

    public FabricatorModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        modelPartData.addChild("bone",
                ModelPartBuilder.create().uv(0, 33)
                        .cuboid(-2.0F, -14.0F, 3.95F, 4.0F, 12.0F, 4.0F, new Dilation(0.001F)).uv(0, 0)
                        .cuboid(-8.0F, -2.0F, -8.05F, 16.0F, 2.0F, 16.0F, new Dilation(0.0F)).uv(0, 19)
                        .cuboid(-3.0F, -13.0F, -4.05F, 6.0F, 1.0F, 12.0F, new Dilation(0.0F)).uv(25, 19)
                        .cuboid(-3.0F, -2.5F, -3.05F, 6.0F, 0.0F, 11.0F, new Dilation(0.005F)).uv(32, 31)
                        .cuboid(-2.5F, -14.0F, -2.55F, 5.0F, 3.0F, 5.0F, new Dilation(0.0F)).uv(17, 40)
                        .cuboid(-2.5F, -10.9F, -2.55F, 5.0F, 0.0F, 5.0F, new Dilation(0.005F)).uv(38, 40)
                        .cuboid(-10.0F, -2.0F, -2.05F, 2.0F, 5.0F, 4.0F, new Dilation(0.0F)).uv(0, 0)
                        .cuboid(8.0F, -2.0F, -2.05F, 2.0F, 5.0F, 4.0F, new Dilation(0.0F)).uv(49, 0)
                        .cuboid(-2.0F, -2.0F, 7.95F, 4.0F, 5.0F, 2.0F, new Dilation(0.0F)).uv(17, 46)
                        .cuboid(-2.0F, -2.0F, -10.05F, 4.0F, 5.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 24.0F, 0.05F));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        bone.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return bone;
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw,
            float headPitch) {
    }
}
