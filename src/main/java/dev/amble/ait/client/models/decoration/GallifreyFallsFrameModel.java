package dev.amble.ait.client.models.decoration;// Made with Blockbench 4.10.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports


import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import dev.amble.ait.client.renderers.entities.GallifreyFallsPaintingEntityRenderer;

public class GallifreyFallsFrameModel extends SinglePartEntityModel {
    private final ModelPart frame;
    public GallifreyFallsFrameModel(ModelPart root) {
        this.frame = root.getChild("frame");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData frame = modelPartData.addChild("frame", ModelPartBuilder.create().uv(0, 0).cuboid(-24.0F, -32.0F, -9.0F, 48.0F, 32.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData plane = frame.addChild("plane", ModelPartBuilder.create().uv(0, 33).cuboid(-19.0F, -25.0F, -1.0F, 38.0F, 22.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 22.0F, -6.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        frame.getChild("plane").visible = false;
        frame.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    public void renderWithFbo(MatrixStack matrices, VertexConsumerProvider vertexConsumer, Framebuffer buffer, int light, int overlay, float red, float green, float blue, float alpha) {
        frame.getChild("plane").render(matrices, vertexConsumer.getBuffer(RenderLayer.getEntityTranslucentCull(GallifreyFallsPaintingEntityRenderer.FRAME_TEXTURE)), light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return frame;
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }
}