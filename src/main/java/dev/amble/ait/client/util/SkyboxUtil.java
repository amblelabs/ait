package dev.amble.ait.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.module.planet.client.renderers.CelestialBodyRenderer;
import dev.amble.ait.module.planet.client.renderers.SpaceSkyRenderer;
import dev.amble.ait.module.planet.core.space.planet.Planet;
import dev.amble.ait.module.planet.core.space.planet.PlanetRenderInfo;
import dev.amble.ait.module.planet.core.space.system.SolarSystem;
import dev.amble.ait.module.planet.core.space.system.Space;


public class SkyboxUtil extends WorldRenderer {

    private static final Identifier TARDIS_SKY = AITMod.id("textures/environment/tardis_sky.png");
//    private static final Identifier EARTH = AITMod.id("textures/environment/earth.png");
//    private static final Identifier MONDAS = AITMod.id("textures/environment/mondas.png");
//    private static final Identifier MOON = AITMod.id("textures/environment/moon.png");
//    private static final Identifier MARS = AITMod.id("textures/environment/mars.png");
    private static final Identifier SATURN = AITMod.id("textures/environment/saturn.png");
    private static final Identifier SATURN_RING = AITMod.id("textures/environment/saturn_ring.png");
    private static final Identifier SUN = AITMod.id("textures/environment/tardis_star.png");

    public static final Quaternionf[] LOOKUP = new Quaternionf[]{null, RotationAxis.POSITIVE_X.rotationDegrees(90.0f),
            RotationAxis.POSITIVE_X.rotationDegrees(-90.0f), RotationAxis.POSITIVE_X.rotationDegrees(180.0f),
            RotationAxis.POSITIVE_Z.rotationDegrees(90.0f), RotationAxis.POSITIVE_Z.rotationDegrees(-90.0f), null};

    public SkyboxUtil(MinecraftClient client, EntityRenderDispatcher entityRenderDispatcher, BlockEntityRenderDispatcher blockEntityRenderDispatcher, BufferBuilderStorage bufferBuilders) {
        super(client, entityRenderDispatcher, blockEntityRenderDispatcher, bufferBuilders);
    }

    public static void renderTardisSky(WorldRenderContext context) {
        SkyboxUtil.renderTardisSky(context.matrixStack());
    }

    public static void renderTardisSky(MatrixStack matrices) {
        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);

        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, TARDIS_SKY);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        for (int i = 0; i < 6; i++) {
            matrices.push();

            Quaternionf rot = LOOKUP[i];

            if (rot != null) {
                matrices.multiply(rot);
            }

            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

            bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, -100.0f).texture(0.0f, 0.0f).color(40, 40, 40, 255).next();

            bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, 100.0f).texture(0.0f, 16.0f).color(40, 40, 40, 255).next();

            bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, 100.0f).texture(16.0f, 16.0f).color(40, 40, 40, 255).next();

            bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, -100.0f).texture(16.0f, 0.0f).color(40, 40, 40, 255).next();

            tessellator.draw();
            matrices.pop();
        }

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }
    public static void renderMoonSky(MatrixStack matrices, Runnable fogCallback, VertexBuffer starsBuffer, ClientWorld world, float tickDelta, Matrix4f projectionMatrix) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-405f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(300f));
        matrices.scale(100, 100, 100);

        drawSpace(tessellator, bufferBuilder, matrices);

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0f));

        RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 1);
        BackgroundRenderer.clearFog();

        starsBuffer.bind();
        starsBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix,
                GameRenderer.getPositionProgram());

        VertexBuffer.unbind();
        fogCallback.run();

        RenderSystem.depthMask(false);
        RenderSystem.depthFunc(GL11.GL_ALWAYS);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.defaultBlendFunc();
        matrices.pop();
        matrices.pop();

        // Planet Rendering
        Vec3d cameraPos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
        renderStarBody(matrices, SUN,
                new Vec3d(cameraPos.getX() + 270, cameraPos.getY() + 200, cameraPos.getZ() + 30), new
                        Vector3f(12f, 12f, 12f),
                new Vector3f(12, 45, 0), false,
                new Vector3f(0.5f, 0, 0f));

        renderSkyBody(matrices, AITMod.id("textures/environment/earth.png"),
                new Vec3d(cameraPos.getX() - 530, cameraPos.getY() + 40, cameraPos.getZ() + 10), new
                        Vector3f(76f, 76f, 76f),
                new Vector3f(-22.5f, 45f, 0), true, true,
                new Vector3f(0.18f, 0.35f, 0.60f));
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(GL11.GL_LESS);
    }

    public static void renderMarsSky(MatrixStack matrices, Runnable fogCallback, VertexBuffer starsBuffer, ClientWorld world, float tickDelta, Matrix4f projectionMatrix) {
        RenderSystem.setShaderColor(1, 1, 1, 0.1f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-405f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0f + 300f));
        matrices.scale(100, 100, 100);

        drawSpace(tessellator, bufferBuilder, matrices);

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0f));
        //matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0f));
        RenderSystem.setShaderColor(1, 1, 1, 1f);
        RenderSystem.setShaderColor(0.85f, 0.85f, 0.85f, 1);
        BackgroundRenderer.clearFog();

        starsBuffer.bind();
        starsBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix,
                GameRenderer.getPositionProgram());

        VertexBuffer.unbind();
        fogCallback.run();

        RenderSystem.depthMask(false);
        RenderSystem.depthFunc(GL11.GL_ALWAYS);
        RenderSystem.defaultBlendFunc();
        matrices.pop();
        matrices.pop();
        RenderSystem.setShaderColor(1, 1, 1, 1f);

        // Planet Rendering
        Vec3d cameraPos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
        renderStarBody(matrices, SUN,
                new Vec3d(cameraPos.getX() + 270, cameraPos.getY() + 200, cameraPos.getZ() + 30), new
                        Vector3f(12f, 12f, 12f),
                new Vector3f(12, 45, 0), false,
                new Vector3f(0.5f, 0, 0f));
        RenderSystem.setShaderColor(1, 1, 1, 1f);
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(GL11.GL_LESS);
    }

    public static void renderSpaceSky(MatrixStack matrices, Runnable fogCallback, VertexBuffer starsBuffer, ClientWorld world, float tickDelta, Matrix4f projectionMatrix) {
        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-405f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(300f));
        matrices.scale(100, 100, 100);

        drawSpace(tessellator, bufferBuilder, matrices);

        RenderSystem.depthMask(false);
        RenderSystem.depthFunc(GL11.GL_ALWAYS);
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0f));

        RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 1);
        BackgroundRenderer.clearFog();

        starsBuffer.bind();
        starsBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix,
                GameRenderer.getPositionProgram());

        VertexBuffer.unbind();
        fogCallback.run();

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.defaultBlendFunc();
        matrices.pop();
        RenderSystem.depthFunc(GL11.GL_EQUAL);
        matrices.pop();

        // Planet Rendering todo - move info into PlanetRenderInfo !!!
        renderStarBody(matrices, SUN,
                new Vec3d(0, 1000, 0),
                new Vector3f(1000f, 1000f, 1000f),
                new Vector3f(12, 45, 0),
                false,
                new Vector3f(0.5f, 0, 0f));


        for (SolarSystem system : Space.getInstance().systems) {
            RenderSystem.depthMask(true);
            RenderSystem.depthFunc(GL11.GL_LESS);
            for (Planet planet : system) {
                PlanetRenderInfo render = planet.render();
                if (render.isEmpty()) continue;

                renderCelestialBody(matrices, render.texture(), render.position(), render.scale(), render.rotation(), render.clouds(), render.atmosphere(), render.color());
            }
        }

        renderCelestialBody(matrices, SATURN_RING,
                new Vec3d(-29000, 1300, -21000),
                new Vector3f(2500f, 2, 2500f),
                new Vector3f(0, 0, 0),
                false, false,
                new Vector3f(0.5f, 1, 1));

        renderCelestialBody(matrices, SATURN,
                new Vec3d(-29000, 2000, -21000),
                new Vector3f(1400f, 1400f, 1400f),
                new Vector3f(0, 0, 0),
                false, true,
                new Vector3f(0.55f, 0.4f, 0.2f));




        RenderSystem.setShaderColor(1, 1, 1, 1);

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    /**
     * Renders a celestial body in space.
     */

    private static void renderSkyBody(MatrixStack matrices, Identifier texture, Vec3d position, Vector3f scale, Vector3f rotation, boolean clouds, boolean atmosphere, Vector3f color) {
        matrices.push();
        CelestialBodyRenderer.renderComprehendableBody(position, scale, rotation, texture, true, clouds, atmosphere, color);
        matrices.pop();
    }

    private static void renderCelestialBody(MatrixStack matrices, Identifier texture, Vec3d position, Vector3f scale, Vector3f rotation, boolean clouds, boolean atmosphere, Vector3f color) {
        matrices.push();
        CelestialBodyRenderer.renderComprehendableBody(position, scale, rotation, texture, false, clouds, atmosphere, color);
        matrices.pop();
    }

    private static void renderStarBody(MatrixStack matrices, Identifier texture, Vec3d position, Vector3f scale, Vector3f rotation, boolean atmosphere, Vector3f color) {
        matrices.push();
        CelestialBodyRenderer.renderStarBody(position, scale, rotation, texture, atmosphere, color);
        matrices.pop();
    }

    private static void drawSpace(Tessellator tessellator, BufferBuilder bufferBuilder, MatrixStack matrices) {
        RenderSystem.setShaderColor(0.25f, 0.25f, 0.25f, 1);
        SpaceSkyRenderer cubeMap = new SpaceSkyRenderer(AITMod.id("textures/environment/space_sky/panorama"));
        cubeMap.draw(tessellator, bufferBuilder, matrices);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1);
    }
}
