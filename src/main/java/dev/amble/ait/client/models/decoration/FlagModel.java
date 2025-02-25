package dev.amble.ait.client.models.decoration;// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports


import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class FlagModel extends SinglePartEntityModel {
    private final ModelPart flag;
    public FlagModel(ModelPart root) {
        this.flag = root.getChild("flag");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData flag = modelPartData.addChild("flag", ModelPartBuilder.create().uv(0, 0).cuboid(-0.5F, -32.0F, -0.5F, 1.0F, 32.0F, 1.0F, new Dilation(0.0F))
                .uv(25, 15).cuboid(-1.0F, -34.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData clamp = flag.addChild("clamp", ModelPartBuilder.create().uv(22, 15).cuboid(0.0F, -7.0F, 0.0F, 1.0F, 14.0F, 0.0F, new Dilation(0.05F)), ModelTransform.pivot(0.5F, -25.0F, 0.0F));

        ModelPartData flag_elements = flag.addChild("flag_elements", ModelPartBuilder.create().uv(5, 0).cuboid(0.0F, -7.0F, 0.5F, 8.0F, 14.0F, 0.0F, new Dilation(0.05F)), ModelTransform.pivot(1.5F, -25.0F, -0.5F));

        ModelPartData element2 = flag_elements.addChild("element2", ModelPartBuilder.create().uv(22, 0).cuboid(0.0F, -7.0F, 0.0F, 8.0F, 14.0F, 0.0F, new Dilation(0.05F)), ModelTransform.pivot(8.0F, 0.0F, 0.5F));

        ModelPartData element3 = element2.addChild("element3", ModelPartBuilder.create().uv(5, 15).cuboid(0.0F, -7.0F, 0.0F, 8.0F, 14.0F, 0.0F, new Dilation(0.05F)), ModelTransform.pivot(8.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        flag.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return flag;
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }
}