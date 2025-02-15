package dev.amble.ait.module.planet.client.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.module.planet.client.models.CelestialBodyModel;


public class CelestialBodyRenderer {

    public static void renderFarAwayBody(Vec3d targetPosition, Vector3f scale, Identifier texture, boolean hasAtmosphere, Vector3f atmosphereColor) {
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

        CelestialBodyModel.getTexturedModelData().createModel().render(matrixStack,
                provider.getBuffer(AITRenderLayers.getBeaconBeam(texture, false)),
                0xf000f0, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1f);
        provider.draw();

        if (hasAtmosphere) {
            atmosphereRenderer(matrixStack, atmosphereColor, provider);
            provider.draw();
        }
        provider.draw();
    }

    public static void renderComprehendableBody(Vec3d targetPosition, Vector3f scale, Identifier texture, boolean hasAtmosphere, Vector3f atmosphereColor) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Camera camera = mc.gameRenderer.getCamera();
        VertexConsumerProvider.Immediate provider = mc.getBufferBuilders().getEntityVertexConsumers();

        Vec3d cameraPos = camera.getPos();

        Vec3d targetPos = new Vec3d(targetPosition.getX(),targetPosition.getY(),targetPosition.getZ());

        Vec3d diff = targetPos.subtract(cameraPos);

        MatrixStack matrixStack = new MatrixStack();
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
        matrixStack.translate(diff.x, diff.y, diff.z);
        matrixStack.scale(scale.x, scale.y, scale.z);

        BackgroundRenderer.clearFog();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(GL11.GL_NEVER);

        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

        CelestialBodyModel.getTexturedModelData().createModel().render(matrixStack,
                provider.getBuffer(AITRenderLayers.getBeaconBeam(texture, false)),
                0xf000f0, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1f);
        provider.draw();

        if (hasAtmosphere) {
            atmosphereRenderer(matrixStack, atmosphereColor, provider);
            provider.draw();
        }
        RenderSystem.depthFunc(GL11.GL_EQUAL);
    }

    public static void atmosphereRenderer(MatrixStack matrixStack, Vector3f color, VertexConsumerProvider.Immediate provider) {
        ModelPart model = CelestialBodyModel.getTexturedModelData().createModel();
        for (int i = 0; i < 5; i++) {
            float alpha = (float) (0.16f - Math.log(i + 1) * 0.02f);
            matrixStack.push();
            float gg = 1.0f + ((i != 0 ? i : i + 1) * 0.01f);
            matrixStack.scale(gg, gg, gg);
            Identifier texture = i == 1 ? new Identifier("textures/environment/clouds.png") : AITMod.id("textures/environment/atmosphere.png");
            RenderSystem.setShaderTexture(0, texture);
            if (i != 1) {
                model.render(matrixStack,
                        provider.getBuffer(AITRenderLayers.getBeaconBeam(texture, true)),
                        0xf000f0, OverlayTexture.DEFAULT_UV, color.x + (0.05f * i + i), color.y + (0.05f * i + i), color.z + (0.05f * i + i), alpha);
            } else if (color.x != 0.5) {
                model.render(matrixStack,
                        provider.getBuffer(AITRenderLayers.getBeaconBeam(texture, true)),
                        0xf000f0, OverlayTexture.DEFAULT_UV, 1, 1, 1, 0.6f);
                matrixStack.scale(1.01f, 1.01f, 1.01f);
                model.render(matrixStack,
                        provider.getBuffer(AITRenderLayers.getBeaconBeam(texture, true)),
                        0xf000f0, OverlayTexture.DEFAULT_UV, 1, 1, 1, 0.6f);
            }
            matrixStack.pop();
        }
    }
}
