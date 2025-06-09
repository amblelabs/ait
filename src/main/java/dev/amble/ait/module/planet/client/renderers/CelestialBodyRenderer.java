package dev.amble.ait.module.planet.client.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import dev.amble.ait.AITMod;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.module.planet.client.models.CelestialBodyModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;


public class CelestialBodyRenderer {

    public static void renderFarAwayBody(Vec3d targetPosition, Vector3f scale, Identifier texture, boolean hasClouds, boolean hasAtmosphere, Vector3f atmosphereColor) {
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
        //RenderSystem.depthFunc(GL11.GL_NOTEQUAL);
        RenderSystem.setProjectionMatrix(matrixStack.peek().getPositionMatrix().perspective(90, 1, 0.05f, 10000000), VertexSorter.BY_Z);

        CelestialBodyModel.getTexturedModelData().createModel().render(matrixStack,
                provider.getBuffer(AITRenderLayers.getBeaconBeam(texture, false)),
                0xf000f0, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1f);
        provider.draw();

        if (hasAtmosphere) {
            atmosphereRenderer(matrixStack, atmosphereColor, provider, false, hasClouds);
            provider.draw();
        }
        provider.draw();
        RenderSystem.restoreProjectionMatrix();
        //RenderSystem.depthFunc(GL11.GL_EQUAL);
    }

    public static void renderStarBody(boolean isTardisSkybox, Vec3d targetPosition, Vector3f scale, Vector3f rotation, Identifier texture, boolean hasAtmosphere, Vector3f atmosphereColor) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Camera camera = mc.gameRenderer.getCamera();
        VertexConsumerProvider.Immediate provider = mc.getBufferBuilders().getEntityVertexConsumers();

        Vec3d cameraPos = camera.getPos();

        Vec3d targetPos = new Vec3d(targetPosition.getX(),targetPosition.getY(),targetPosition.getZ());

        Vec3d diff = targetPos.subtract(cameraPos);

        MatrixStack matrixStack = new MatrixStack();

        if (mc.world == null)
            return;

        matrixStack.push();

        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
        if (isTardisSkybox) {
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(mc.world.getSkyAngle(mc.getTickDelta()) * 360.0f));
            matrixStack.translate(0, -4000, 0);
            matrixStack.scale(0.25f, 0.25f, 0.25f);
        }
        matrixStack.translate(diff.x, diff.y, diff.z);
        matrixStack.scale(scale.x, scale.y, scale.z);

        BackgroundRenderer.clearFog();


        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation.y()));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotation.x()));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation.z()));

        CelestialBodyModel model = new CelestialBodyModel(CelestialBodyModel.getTexturedModelData().createModel());

        RenderSystem.depthMask(true);

        //RenderSystem.setShaderColor(atmosphereColor.x + 0.25f, atmosphereColor.y + 0.25f, atmosphereColor.z + 0.25f, 1f);
        model.render(matrixStack,
                provider.getBuffer(AITRenderLayers.getBeaconBeam(texture, false)),
                0xf000f00, OverlayTexture.DEFAULT_UV, 1 - atmosphereColor.x, 1 - atmosphereColor.y, 1 - atmosphereColor.z, 1f);
        provider.draw();
        //RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        if (hasAtmosphere) {
            atmosphereRenderer(matrixStack, atmosphereColor, provider, true,false);
            provider.draw();
        }
        //RenderSystem.depthFunc(GL11.GL_EQUAL);
        matrixStack.pop();
    }

    public static void renderComprehendableBody(boolean isTardisSkybox, Vec3d targetPosition, Vector3f scale, Vector3f rotation, Identifier texture, boolean isSkyRendered, boolean hasClouds, boolean hasAtmosphere, Vector3f atmosphereColor, boolean hasRings) {
        renderComprehendableBody(0, isTardisSkybox, targetPosition, scale, rotation, texture, isSkyRendered, hasClouds, hasAtmosphere, atmosphereColor, hasRings);
    }

    public static void renderComprehendableBody(float skyboxRot, boolean isTardisSkybox, Vec3d targetPosition, Vector3f scale, Vector3f rotation, Identifier texture, boolean isSkyRendered, boolean hasClouds, boolean hasAtmosphere, Vector3f atmosphereColor, boolean hasRings) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Camera camera = mc.gameRenderer.getCamera();
        VertexConsumerProvider.Immediate provider = mc.getBufferBuilders().getEntityVertexConsumers();

        Vec3d cameraPos = camera.getPos();

        Vec3d targetPos = new Vec3d(targetPosition.getX(),targetPosition.getY(),targetPosition.getZ());

        Vec3d diff = targetPos.subtract(cameraPos);

        MatrixStack matrixStack = new MatrixStack();
        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F + skyboxRot));
        if (isTardisSkybox) {
            matrixStack.translate(0, 4000, 0);
            matrixStack.scale(0.25f, 0.25f, 0.25f);
        }
        matrixStack.translate(diff.x, diff.y, diff.z);
        matrixStack.scale(scale.x, scale.y, scale.z);

        BackgroundRenderer.clearFog();
        //RenderSystem.depthMask(true);
        if (isSkyRendered) {
            RenderSystem.depthMask(true);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthFunc(GL11.GL_ALWAYS);
        }

        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation.y()));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180 + rotation.x()));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation.z()));

        CelestialBodyModel celestialBodyModel = new CelestialBodyModel(CelestialBodyModel.getTexturedModelData().createModel());
        celestialBodyModel.render(matrixStack,
                provider.getBuffer(AITRenderLayers.getEntityNoOutline(texture)),
                0xf000f0, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1f);
        if (hasRings) {
            celestialBodyModel.ring.render(matrixStack,
                    provider.getBuffer(AITRenderLayers.getEntityNoOutline(texture)),
                    0xf000f0, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1f);
        }
        provider.draw();

        if (hasAtmosphere) {
            atmosphereRenderer(matrixStack, atmosphereColor, provider, false, hasClouds);
            provider.draw();
        }

        if (isSkyRendered) {
            RenderSystem.depthMask(false);
            GL11.glDepthFunc(GL11.GL_EQUAL);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }
        matrixStack.pop();
    }

    public static void atmosphereRenderer(MatrixStack matrixStack, Vector3f color, VertexConsumerProvider.Immediate provider, boolean isStar, boolean hasClouds) {
        CelestialBodyModel model = new CelestialBodyModel(CelestialBodyModel.getTexturedModelData().createModel());
        for (int i = 0; i < 6; i++) {
            float alpha = (float) (0.1f - Math.log(i + 1) * 0.001f);
            matrixStack.push();
            float gg = 1.0f + ((i != 0 ? i : i + 1) * 0.025f);
            matrixStack.scale(gg, gg, gg);
            float delta = (MinecraftClient.getInstance().getTickDelta() + MinecraftClient.getInstance().player.age) * 0.00001f;
            RenderLayer renderLayer = AITRenderLayers.getItemEntityTranslucentCull(new Identifier("textures/environment/clouds.png"));//RenderLayer.getEnergySwirl(new Identifier("textures/environment/clouds.png"), delta % 1.0F, (delta * 0.1F) % 1.0F);
            Identifier texture = AITMod.id("textures/environment/atmosphere.png");
            if (i != 1) {
                //System.out.println("min" + (Math.min(color.z + (0.05f * i + i), 1.0f)));
                model.render(matrixStack,
                        provider.getBuffer(isStar && (i == 2 || i == 3 || i == 4) ?
                                AITRenderLayers.getEyes(texture) : AITRenderLayers.getItemEntityTranslucentCull(texture)),
                        15728864, OverlayTexture.DEFAULT_UV,  1 + Math.min(color.x + (0.015f * i), 5.0f), 1 + Math.min(color.y + (0.015f * i), 5.0f), 1 + Math.min(color.z + (0.015f * i), 5.0f), -1 + alpha);
            } else if (hasClouds) {
                model.render(matrixStack,
                        provider.getBuffer(renderLayer),
                        15728864, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1F);
                matrixStack.scale(1.01f, 1.01f, 1.01f);
                model.render(matrixStack,
                        provider.getBuffer(renderLayer),
                        15728864, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1F);
            }
            matrixStack.pop();
        }
    }
}
