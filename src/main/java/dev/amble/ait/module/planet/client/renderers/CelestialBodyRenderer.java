package dev.amble.ait.module.planet.client.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.decoration.TardisStarModel;
import dev.amble.ait.client.renderers.AITRenderLayers;


public class CelestialBodyRenderer {

    public static final Identifier TARDIS_STAR_TEXTURE = AITMod.id("textures/environment/tardis_star.png");
    private static final float HALF_SQRT_3 = (float) (Math.sqrt(3.0) / 2.0);

    public static void render(Vec3d targetPosition, Vector3f scale, Identifier texture, Identifier overlayTexture) {
        renderStar(targetPosition, scale, texture, overlayTexture);
    }

    public static void renderStar(Vec3d targetPosition, Vector3f scale, Identifier texture, Identifier overlayTexture) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Camera camera = mc.gameRenderer.getCamera();
        VertexConsumerProvider.Immediate provider = mc.getBufferBuilders().getEntityVertexConsumers();

        Vec3d cameraPos = camera.getPos();

        Vec3d targetPos = new Vec3d(camera.getPos().getX() + targetPosition.getX(),
                camera.getPos().getY() + targetPosition.getY(),
                camera.getPos().getZ() + targetPosition.getZ());

        Vec3d diff = targetPos.subtract(cameraPos);

        MatrixStack matrixStack = new MatrixStack();
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
        matrixStack.translate(diff.x, diff.y, diff.z);
        matrixStack.scale(scale.x, scale.y, scale.z);

        BackgroundRenderer.clearFog();
        RenderSystem.depthFunc(GL11.GL_NOTEQUAL);
        TardisStarModel.getTexturedModelData().createModel().render(matrixStack,
                provider.getBuffer(AITRenderLayers.tardisEmissiveCullZOffset(texture, false)),
                LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 1, 1, 1, 0.5f);
        provider.draw();

        matrixStack.scale(0.9f, 0.9f, 0.9f);
        TardisStarModel.getTexturedModelData().createModel().render(matrixStack,
                provider.getBuffer(AITRenderLayers.tardisEmissiveCullZOffset(overlayTexture, false)),
                LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1f);
        provider.draw();
        RenderSystem.depthFunc(GL11.GL_EQUAL);
    }
}
