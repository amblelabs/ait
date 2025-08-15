package dev.amble.ait.mixin.client.rendering;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.client.util.SkyboxUtil;
import dev.amble.ait.core.AITDimensions;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.world.TardisServerWorld;

@Mixin(WorldRenderer.class)
public abstract class SkyboxMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    protected abstract void renderEndSky(MatrixStack matrices);

    @Shadow
    private @Nullable ClientWorld world;

    @Shadow
    private @Nullable VertexBuffer lightSkyBuffer;

    @Shadow
    @Final
    private static Identifier SUN;

    @Shadow
    @Final
    private static Identifier MOON_PHASES;

    @Shadow
    private @Nullable VertexBuffer starsBuffer;

    @Shadow
    private @Nullable VertexBuffer darkSkyBuffer;

    @Shadow
    public abstract void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline,
            Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager,
            Matrix4f projectionMatrix);

    @Shadow protected abstract void renderStars();

    @Unique private static WorldRenderContext context;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void init(CallbackInfo ci) {
        WorldRenderEvents.AFTER_SETUP.register(ctx -> context = ctx);
    }

    @Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At("HEAD"), cancellable = true)
    public void ait$renderSky(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera,
            boolean thickFog, Runnable fogCallback, CallbackInfo ci) {
        if (this.world == null)
            return;

        if (TardisServerWorld.isTardisDimension(this.world)) {
            this.renderSkyDynamically(matrices, projectionMatrix, tickDelta, camera, fogCallback, ci);
            this.world.getProfiler().swap("projector");
        }

        if (this.world.getRegistryKey() == AITDimensions.TIME_VORTEX_WORLD) {
            SkyboxUtil.renderVortexSky(matrices);
            ci.cancel();
        }

        if (this.world.getRegistryKey() == AITDimensions.MOON) {
            SkyboxUtil.renderMoonSky(matrices, fogCallback, this.starsBuffer, world, tickDelta, projectionMatrix);
            ci.cancel();
        }

        if (this.world.getRegistryKey() == AITDimensions.MARS) {
            SkyboxUtil.renderMarsSky(matrices, fogCallback, this.starsBuffer, world, tickDelta, projectionMatrix, ci);
        }

        if (this.world.getRegistryKey() == AITDimensions.SPACE) {
            SkyboxUtil.renderSpaceSky(false, matrices, fogCallback, this.starsBuffer, world, tickDelta, projectionMatrix);
            ci.cancel();
        }
    }

    @Unique private void renderSkyDynamically(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera,
            Runnable fogCallback, CallbackInfo ci) {
        if (!AITModClient.CONFIG.environmentProjector || context == null) {
            SkyboxUtil.renderTardisSky(matrices);
            ci.cancel();

            return;
        }

        if (this.world == null)
            return;

        Tardis tardis = ClientTardisUtil.getCurrentTardis();

        if (tardis == null || tardis.stats() == null || tardis.stats().skybox() == null)
            return;

        RegistryKey<World> skyboxWorld = tardis.stats().skybox().get();
        Direction skyboxDirection = tardis.stats().skyboxDirection().get();

        if (skyboxWorld == World.OVERWORLD) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(skyboxDirection.asRotation()));
            this.renderOverworldSky(matrices, projectionMatrix, tickDelta, camera, fogCallback);
            matrices.pop();

            ci.cancel();
            return;
        }

        if (skyboxWorld == World.END) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(skyboxDirection.asRotation()));
            this.renderEndSky(matrices);
            matrices.pop();
            ci.cancel();
            return;
        }

        if (skyboxWorld == World.NETHER) {
            //this.renderEndPortalEffect(matrices, projectionMatrix, tickDelta, camera, fogCallback);
            // I will do this later I really don't care
            SkyboxUtil.renderTardisSky(matrices);
            ci.cancel();
            return;
        }

        if (skyboxWorld == AITDimensions.SPACE) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(skyboxDirection.asRotation()));
            SkyboxUtil.renderSpaceSky(true, matrices, fogCallback, this.starsBuffer, world, tickDelta, projectionMatrix);
            matrices.pop();
            ci.cancel();
            return;
        }

        if (skyboxWorld == AITDimensions.MOON) {
            SkyboxUtil.renderMoonSky(skyboxDirection.asRotation(), matrices, fogCallback, this.starsBuffer, world, tickDelta, projectionMatrix);
            ci.cancel();
            return;
        }

        if (skyboxWorld == AITDimensions.MARS) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(skyboxDirection.asRotation()));
            SkyboxUtil.renderMarsSky(matrices, fogCallback, this.starsBuffer, world, tickDelta, projectionMatrix, ci);
            matrices.pop();
            return;
        }

        if (skyboxWorld == AITDimensions.TIME_VORTEX_WORLD) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(skyboxDirection.asRotation()));
            SkyboxUtil.renderVortexSky(matrices, tardis);
            matrices.pop();
            ci.cancel();
            return;
        }

        DimensionRenderingRegistry.SkyRenderer renderer = DimensionRenderingRegistry.getSkyRenderer(skyboxWorld);

        if (renderer != null) {
            renderer.render(context);
            ci.cancel();
        }
    }

    @Unique private void renderOverworldSky(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera,
            Runnable fogCallback) {
        float q;
        float p;
        float o;
        int m;
        float k;
        float i;

        Vec3d vec3d = world.getSkyColor(camera.getPos(), tickDelta);

        float f = (float) vec3d.x;
        float g = (float) vec3d.y;
        float h = (float) vec3d.z;

        BackgroundRenderer.setFogBlack();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor(f, g, h, 1.0f);

        ShaderProgram shaderProgram = RenderSystem.getShader();

        this.lightSkyBuffer.bind();
        this.lightSkyBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, shaderProgram);

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

            for (int n = 0; n <= 16; n++) {
                o = (float) n * ((float) Math.PI * 2) / 16.0f;
                p = MathHelper.sin(o);
                q = MathHelper.cos(o);

                bufferBuilder.vertex(matrix4f, p * 120.0f, q * 120.0f, -q * 40.0f * fs[3])
                        .color(fs[0], fs[1], fs[2], 0.0f).next();
            }

            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            matrices.pop();
        }

        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE,
                GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);

        matrices.push();
        i = 1.0f - world.getRainGradient(tickDelta);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, i);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0f));

        Matrix4f matrix4f2 = matrices.peek().getPositionMatrix();

        k = 30.0f;
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, SUN);

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f2, -k, 100.0f, -k).texture(0.0f, 0.0f).next();
        bufferBuilder.vertex(matrix4f2, k, 100.0f, -k).texture(1.0f, 0.0f).next();
        bufferBuilder.vertex(matrix4f2, k, 100.0f, k).texture(1.0f, 1.0f).next();
        bufferBuilder.vertex(matrix4f2, -k, 100.0f, k).texture(0.0f, 1.0f).next();

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        k = 20.0f;

        RenderSystem.setShaderTexture(0, MOON_PHASES);
        int r = world.getMoonPhase();
        int s = r % 4;
        m = r / 4 % 2;

        float t = (float) (s) / 4.0f;
        o = (float) (m) / 2.0f;
        p = (float) (s + 1) / 4.0f;
        q = (float) (m + 1) / 2.0f;

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f2, -k, -100.0f, k).texture(p, q).next();
        bufferBuilder.vertex(matrix4f2, k, -100.0f, k).texture(t, q).next();
        bufferBuilder.vertex(matrix4f2, k, -100.0f, -k).texture(t, o).next();
        bufferBuilder.vertex(matrix4f2, -k, -100.0f, -k).texture(p, o).next();

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        float u = world.method_23787(tickDelta) * i;

        if (u > 0.0f) {
            RenderSystem.setShaderColor(u, u, u, u);
            BackgroundRenderer.clearFog();

            this.starsBuffer.bind();
            this.starsBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix,
                    GameRenderer.getPositionProgram());

            VertexBuffer.unbind();
            fogCallback.run();
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        matrices.pop();

        RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
        double d = this.client.player.getCameraPosVec(tickDelta).y
                - this.world.getLevelProperties().getSkyDarknessHeight(this.world);

        if (d < 0.0) {
            matrices.push();
            matrices.translate(0.0f, 12.0f, 0.0f);

            this.darkSkyBuffer.bind();
            this.darkSkyBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, shaderProgram);

            VertexBuffer.unbind();
            matrices.pop();
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.depthMask(true);
    }

    @Unique private void renderEndPortalEffect(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera,
                                            Runnable fogCallback) {

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
            RenderSystem.setShader(GameRenderer::getRenderTypeEndGatewayProgram);
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
        RenderSystem.depthMask(true);
    }
}
