package dev.amble.ait.client.renderers.entities;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.boti.BOTI;
import dev.amble.ait.client.models.decoration.GallifreyFallsModel;
import dev.amble.ait.client.models.decoration.PaintingFrameModel;
import dev.amble.ait.core.entities.RiftEntity;

@Environment(value=EnvType.CLIENT)
public class RiftEntityRenderer
        extends EntityRenderer<RiftEntity> {
    public static final Identifier RIFT_TEXTURE = AITMod.id("textures/entity/rift/rift.png");
    public static final Identifier CIRCLE_TEXTURE = AITMod.id("textures/entity/rift/circle_rift.png");
    PaintingFrameModel frame;
    GallifreyFallsModel painting;
    public RiftEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        frame = new PaintingFrameModel(PaintingFrameModel.getTexturedModelData().createModel());
        painting = new GallifreyFallsModel(GallifreyFallsModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(RiftEntity riftEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if (AITModClient.CONFIG.enableTardisBOTI) {
            BOTI.RIFT_RENDERING_QUEUE.add(riftEntity);
            return;
        }

        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        matrixStack.translate(0, -0.9, 0.05);
        matrixStack.scale(1, 1, 1);
        frame.render(matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucentCull(RIFT_TEXTURE)), 0xf000f0, OverlayTexture.DEFAULT_UV, 0.6f, 0.0f, 1, 1);
        matrixStack.pop();
    }

    @Override
    public Identifier getTexture(RiftEntity entity) {
        return null;
    }

}
