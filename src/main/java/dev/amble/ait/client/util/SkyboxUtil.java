package dev.amble.ait.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.renderers.VortexUtil;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.module.planet.client.renderers.CelestialBodyRenderer;
import dev.amble.ait.module.planet.client.renderers.SpaceSkyRenderer;
import dev.amble.ait.module.planet.core.space.planet.Planet;
import dev.amble.ait.module.planet.core.space.planet.PlanetRenderInfo;
import dev.amble.ait.module.planet.core.space.system.SolarSystem;
import dev.amble.ait.module.planet.core.space.system.Space;



public class SkyboxUtil extends WorldRenderer {
    private static final Identifier TARDIS_SKY = AITMod.id("textures/environment/tardis_sky.png");
    private static final Identifier SUN = AITMod.id("textures/environment/tardis_star.png");

    public static final Quaternionf[] LOOKUP = new Quaternionf[]{null, RotationAxis.POSITIVE_X.rotationDegrees(90.0f),
            RotationAxis.POSITIVE_X.rotationDegrees(-90.0f), RotationAxis.POSITIVE_X.rotationDegrees(180.0f),
            RotationAxis.POSITIVE_Z.rotationDegrees(90.0f), RotationAxis.POSITIVE_Z.rotationDegrees(-90.0f), null};

    public SkyboxUtil(MinecraftClient client, EntityRenderDispatcher entityRenderDispatcher, BlockEntityRenderDispatcher blockEntityRenderDispatcher, BufferBuilderStorage bufferBuilders) {
        super(client, entityRenderDispatcher, blockEntityRenderDispatcher, bufferBuilders);
    }

    public static void renderVortexSky(MatrixStack matrices, Tardis tardis) {
        VortexUtil util = tardis.stats().getVortexEffects().toUtil();
        matrices.push();
        float scale = 100f;
        float zOffset = 500 * scale;
        if (!tardis.travel().autopilot() && tardis.travel().getState() != TravelHandlerBase.State.LANDED)
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float) MinecraftClient.getInstance().player.age / ((float) 200 / tardis.travel().speed()) * 360f));
        if (!tardis.crash().isNormal())
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) MinecraftClient.getInstance().player.age / 100 * 360f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) MinecraftClient.getInstance().player.age / 100 * 360f));
        matrices.translate(0, 0, zOffset);
        matrices.scale(scale, scale, scale);

        util.renderVortex(matrices);
        util.renderVortexLayer(matrices, 1.5f);
        util.renderVortexLayer(matrices, 2.5f);
        matrices.pop();
    }

    public static void renderVortexSky(MatrixStack matrices) {
        VortexUtil util = new VortexUtil("darkness");
        matrices.push();
        float scale = 100f;
        float zOffset = 500 * scale;
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float) MinecraftClient.getInstance().player.age / ((float) 200 / 5) * 360f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) MinecraftClient.getInstance().player.age / 100 * 360f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) MinecraftClient.getInstance().player.age / 100 * 360f));
        matrices.translate(0, 0, zOffset);
        matrices.scale(scale, scale, scale);

        util.renderVortex(matrices);
        util.renderVortexLayer(matrices, 1.5f);
        util.renderVortexLayer(matrices, 2.5f);
        matrices.pop();
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
        renderMoonSky(0, matrices, fogCallback, starsBuffer, world, tickDelta, projectionMatrix);
    }
    public static void renderMoonSky(float tallDrinkOfWater, MatrixStack matrices, Runnable fogCallback, VertexBuffer starsBuffer, ClientWorld world, float tickDelta, Matrix4f projectionMatrix) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-405f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0f + 300f));
        matrices.scale(100, 100, 100);

        drawSpace(tessellator, bufferBuilder, matrices);

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
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
        matrices.push();
        renderStarBody(false, matrices, SUN,
                new Vec3d(cameraPos.getX() + 270, cameraPos.getY() - 120, cameraPos.getZ() + 0), new
                        Vector3f(12f, 12f, 12f),
                new Vector3f(12, 45, 0),
                true,
                new Vector3f(0.3f, 0.15f, 0.01f));

        renderSkyBody(tallDrinkOfWater + (Direction.fromRotation(tallDrinkOfWater).equals(Direction.WEST) || Direction.fromRotation(tallDrinkOfWater).equals(Direction.EAST) ? -90f : 90f), false, matrices, AITMod.id("textures/environment/earth.png"),
                new Vec3d(cameraPos.getX() - 530, cameraPos.getY() + 40, cameraPos.getZ() + 10), new
                        Vector3f(76f, 76f, 76f),
                new Vector3f(-22.5f, 45f, 0), true, true,
                new Vector3f(0.18f, 0.35f, 0.60f));
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(GL11.GL_LESS);
        matrices.pop();
    }

    public static void renderMarsSky(MatrixStack matrices, Runnable fogCallback, VertexBuffer starsBuffer, ClientWorld world, float tickDelta, Matrix4f projectionMatrix, CallbackInfo ci) {
        float q;
        float p;
        float o;
        float k;
        float i;
        fogCallback.run();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        Vec3d vec3d = world.getSkyColor(MinecraftClient.getInstance().gameRenderer.getCamera().getPos(), tickDelta);
        float f = (float)vec3d.x;
        float g = (float)vec3d.y;
        float h = (float)vec3d.z;
        BackgroundRenderer.setFogBlack();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor(f, g, h, 1.0f);
        VertexBuffer.unbind();
        RenderSystem.enableBlend();
        float[] fs = world.getDimensionEffects().getFogColorOverride(world.getSkyAngle(tickDelta), tickDelta);
        if (fs != null) {
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
            i = MathHelper.sin(world.getSkyAngleRadians(tickDelta)) < 0.0f ? 180.0f : 0.0f;
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0f));
            float j = fs[0];
            k = fs[1];
            float l = fs[2];
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(matrix4f, 0.0f, 100.0f, 0.0f).color(j, k, l, fs[3]).next();
            int m = 16;
            for (int n = 0; n <= 16; ++n) {
                o = (float)n * ((float)Math.PI * 2) / 16.0f;
                p = MathHelper.sin(o);
                q = MathHelper.cos(o);
                bufferBuilder.vertex(matrix4f, p * 120.0f, q * 120.0f, -q * 40.0f * fs[3]).color(fs[0], fs[1], fs[2], 0.0f).next();
            }
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            matrices.pop();
        }
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_CONSTANT_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-405f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0f + 300f));
        matrices.scale(100, 100, 100);

        if (fs != null)
            RenderSystem.setShaderColor(fs[0] + 0.45f, fs[1] + 0.45f, fs[2] + 0.45f, 0.1f);
        else
            RenderSystem.setShaderColor(0.8f, 1.0f, 1.0f, 0.1f);
        SpaceSkyRenderer cubeMap = new SpaceSkyRenderer(AITMod.id("textures/environment/space_sky/panorama"));
        cubeMap.draw(tessellator, bufferBuilder, matrices);
        RenderSystem.setShaderColor(1, 1, 1, 1f);

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0f));
        //matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0f));
        BackgroundRenderer.clearFog();

        starsBuffer.bind();
        starsBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix,
                GameRenderer.getPositionProgram());

        VertexBuffer.unbind();
        fogCallback.run();

        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(GL11.GL_ALWAYS);
        RenderSystem.defaultBlendFunc();
        matrices.pop();
        RenderSystem.setShaderColor(1, 1, 1, 1f);

        matrices.pop();
        matrices.push();
        i = 1.0f;
        if (fs != null)
            RenderSystem.setShaderColor(fs[0] + 0.45f, fs[1] + 0.45f, fs[2] + 0.45f, i);
        else
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, i);
        Vec3d cameraPos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
        renderStarBody(false, matrices, SUN,
                new Vec3d(cameraPos.getX(), cameraPos.getY() + 250, cameraPos.getZ() + 0), new
                        Vector3f(6f, 6f, 6f),
                new Vector3f(45, 12, 12), false,
                new Vector3f(0f, 0.03f, 0.03f));
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        matrices.pop();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.depthMask(true);
        ci.cancel();
    }

    public static void renderSpaceSky(boolean isTardisSkybox, MatrixStack matrices, Runnable fogCallback, VertexBuffer starsBuffer, ClientWorld world, float tickDelta, Matrix4f projectionMatrix) {
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
        renderStarBody(isTardisSkybox, matrices, SUN,
                new Vec3d(0, 1000, 0),
                new Vector3f(1000f, 1000f, 1000f),
                new Vector3f(12, 45, 0),
                true,
                new Vector3f(0.1f, 0.05f, 0.0f));


        for (SolarSystem system : Space.getInstance().systems) {
            RenderSystem.depthMask(true);
            RenderSystem.depthFunc(GL11.GL_LESS);
            for (Planet planet : system) {
                PlanetRenderInfo render = planet.render();
                if (render.isEmpty()) continue;

                renderCelestialBody(isTardisSkybox, matrices, render.texture(), render.position(), render.scale(), render.rotation(), render.clouds(), render.atmosphere(), render.color(), render.hasRings());
            }
        }

        RenderSystem.setShaderColor(1, 1, 1, 1);

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    /**
     * Renders a celestial body in space.
     */

    private static void renderSkyBody(boolean isTardisSkybox, MatrixStack matrices, Identifier texture, Vec3d position, Vector3f scale, Vector3f rotation, boolean clouds, boolean atmosphere, Vector3f color) {
        matrices.push();
        CelestialBodyRenderer.renderComprehendableBody(isTardisSkybox, position, scale, rotation, texture, true, clouds, atmosphere, color, false);
        matrices.pop();
    }

    private static void renderSkyBody(float rotationOf, boolean isTardisSkybox, MatrixStack matrices, Identifier texture, Vec3d position, Vector3f scale, Vector3f rotation, boolean clouds, boolean atmosphere, Vector3f color) {
        matrices.push();
        CelestialBodyRenderer.renderComprehendableBody(rotationOf, isTardisSkybox, position, scale, rotation, texture, true, clouds, atmosphere, color, false);
        matrices.pop();
    }

    private static void renderCelestialBody(boolean isTardisSkybox, MatrixStack matrices, Identifier texture, Vec3d position, Vector3f scale, Vector3f rotation, boolean clouds, boolean atmosphere, Vector3f color, boolean hasRings) {
        matrices.push();
        CelestialBodyRenderer.renderComprehendableBody(isTardisSkybox, position, scale, rotation, texture, false, clouds, atmosphere, color, hasRings);
        matrices.pop();
    }

    private static void renderStarBody(boolean isTardisSkybox, MatrixStack matrices, Identifier texture, Vec3d position, Vector3f scale, Vector3f rotation, boolean atmosphere, Vector3f color) {
        matrices.push();
        CelestialBodyRenderer.renderStarBody(isTardisSkybox, position, scale, rotation, texture, atmosphere, color);
        matrices.pop();
    }

    private static void drawSpace(Tessellator tessellator, BufferBuilder bufferBuilder, MatrixStack matrices) {
        RenderSystem.setShaderColor(0.25f, 0.25f, 0.25f, 1);
        SpaceSkyRenderer cubeMap = new SpaceSkyRenderer(AITMod.id("textures/environment/space_sky/panorama"));
        cubeMap.draw(tessellator, bufferBuilder, matrices);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1);
    }
}
