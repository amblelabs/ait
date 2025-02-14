package dev.amble.ait.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.module.planet.client.renderers.CelestialBodyRenderer;

public class SkyboxUtil extends WorldRenderer {

    private static final Identifier TARDIS_SKY = AITMod.id("textures/environment/tardis_sky.png");
    private static final Identifier MOON_SKY = AITMod.id("textures/environment/tardis_sky.png");
    private static final Identifier EARTH = AITMod.id("textures/environment/earth.png");

    private static final Quaternionf[] LOOKUP = new Quaternionf[]{null, RotationAxis.POSITIVE_X.rotationDegrees(90.0f),
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
        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);

        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, MOON_SKY);

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

            bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, -100.0f).texture(0.0f, 0.0f).color(0, 0, 0, 255).next();

            bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, 100.0f).texture(0.0f, 16.0f).color(0, 0, 0, 255).next();

            bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, 100.0f).texture(16.0f, 16.0f).color(0, 0, 0, 255).next();

            bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, -100.0f).texture(16.0f, 0.0f).color(0, 0, 0, 255).next();

            tessellator.draw();
            matrices.pop();
        }

        float k = 20.0f;
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, new Identifier("textures/environment/sun.png"));

        matrices.push();
        matrices.translate(0, 25, 0);
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(90.0f));
        Matrix4f matrix4f2 = matrices.peek().getPositionMatrix();
        //make smaller moon size
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f2, -k, 100.0f, -k).texture(0.0f, 0.0f).next();
        bufferBuilder.vertex(matrix4f2, k, 100.0f, -k).texture(1.0f, 0.0f).next();
        bufferBuilder.vertex(matrix4f2, k, 100.0f, k).texture(1.0f, 1.0f).next();
        bufferBuilder.vertex(matrix4f2, -k, 100.0f, k).texture(0.0f, 1.0f).next();
        matrices.pop();

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0f));

        RenderSystem.setShaderColor(1, 1, 1, 1);
        BackgroundRenderer.clearFog();

        starsBuffer.bind();
        starsBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix,
                GameRenderer.getPositionProgram());

        VertexBuffer.unbind();
        fogCallback.run();

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.defaultBlendFunc();
        matrices.pop();

        Identifier id = AITMod.id("textures/environment/earth.png");
        CelestialBodyRenderer.render(new Vec3d(100, 0, 0),
                new Vector3f(10f, 10f, 10f),
                id,
                id);

        RenderSystem.depthMask(true);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    public static void renderSpaceSky(MatrixStack matrices, Runnable fogCallback, VertexBuffer starsBuffer, ClientWorld world, float tickDelta, Matrix4f projectionMatrix) {
        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);

        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, MOON_SKY);

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

            bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, -100.0f).texture(0.0f, 0.0f).color(0, 0, 0, 255).next();

            bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, 100.0f).texture(0.0f, 16.0f).color(0, 0, 0, 255).next();

            bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, 100.0f).texture(16.0f, 16.0f).color(0, 0, 0, 255).next();

            bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, -100.0f).texture(16.0f, 0.0f).color(0, 0, 0, 255).next();

            tessellator.draw();
            matrices.pop();
        }

        float k = 30.0f;
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, new Identifier("textures/environment/sun.png"));

        Matrix4f matrix4f2 = matrices.peek().getPositionMatrix();
        //make smaller moon size
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f2, -k, 100.0f, -k).texture(0.0f, 0.0f).next();
        bufferBuilder.vertex(matrix4f2, k, 100.0f, -k).texture(1.0f, 0.0f).next();
        bufferBuilder.vertex(matrix4f2, k, 100.0f, k).texture(1.0f, 1.0f).next();
        bufferBuilder.vertex(matrix4f2, -k, 100.0f, k).texture(0.0f, 1.0f).next();

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0f));

        RenderSystem.setShaderColor(1, 1, 1, 1);
        BackgroundRenderer.clearFog();

        starsBuffer.bind();
        starsBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix,
                GameRenderer.getPositionProgram());

        VertexBuffer.unbind();
        fogCallback.run();

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.defaultBlendFunc();
        matrices.pop();

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }
}
