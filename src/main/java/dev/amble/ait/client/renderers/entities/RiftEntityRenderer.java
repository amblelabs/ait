package dev.amble.ait.client.renderers.entities;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.boti.BOTI;
import dev.amble.ait.client.models.decoration.PaintingFrameModel;
import dev.amble.ait.core.entities.RiftEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class RiftEntityRenderer
        extends EntityRenderer<RiftEntity> {
    public static final Identifier RIFT_TEXTURE = AITMod.id("textures/entity/rift/rift.png");
    public static final Identifier CIRCLE_TEXTURE = AITMod.id("textures/entity/rift/circle_rift.png");
    PaintingFrameModel frame;
    public RiftEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        frame = new PaintingFrameModel(PaintingFrameModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(RiftEntity riftEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if (AITModClient.CONFIG.enableTardisBOTI) {
            BOTI.RIFT_RENDERING_QUEUE.add(riftEntity);
            return;
        }

        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(riftEntity.getYaw() + 180));
        matrixStack.translate(0, 0.3, 0);
        matrixStack.scale(1, 1, 1);

        renderCircleQuad(
                matrixStack,
                vertexConsumerProvider.getBuffer(RenderLayer.getEndGateway()),
                0xf000f0,
                OverlayTexture.DEFAULT_UV,
                0.6f, 0.0f, 1.0f, 1.0f,
                4.5f
        );

        // The endGateway renderLayer culls the backface so rendering it twice is fine
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));
        renderCircleQuad(
                matrixStack,
                vertexConsumerProvider.getBuffer(RenderLayer.getEndGateway()),
                0xf000f0,
                OverlayTexture.DEFAULT_UV,
                0.6f, 0.0f, 1.0f, 1.0f,
                4.5f
        );

        matrixStack.pop();
    }

    private static void renderCircleQuad(MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha, float size) {
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f positionMatrix = entry.getPositionMatrix();
        Matrix3f normalMatrix = entry.getNormalMatrix();

        float half = size / 2.0f;

        vertexConsumer.vertex(positionMatrix, -half, -half, 0).color(red, green, blue, alpha).texture(0.0f, 0.5f).overlay(overlay).light(light).normal(normalMatrix, 0.0f, 0.0f, 1.0f).next();
        vertexConsumer.vertex(positionMatrix, half, -half, 0).color(red, green, blue, alpha).texture(0.5f, 0.5f).overlay(overlay).light(light).normal(normalMatrix, 0.0f, 0.0f, 1.0f).next();
        vertexConsumer.vertex(positionMatrix, half, half, 0).color(red, green, blue, alpha).texture(0.5f, 0.0f).overlay(overlay).light(light).normal(normalMatrix, 0.0f, 0.0f, 1.0f).next();
        vertexConsumer.vertex(positionMatrix, -half, half, 0).color(red, green, blue, alpha).texture(0.0f, 0.0f).overlay(overlay).light(light).normal(normalMatrix, 0.0f, 0.0f, 1.0f).next();
    }

    @Override
    public Identifier getTexture(RiftEntity entity) {
        return null;
    }

}
