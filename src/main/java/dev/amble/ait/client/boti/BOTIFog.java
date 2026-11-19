package dev.amble.ait.client.boti;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class BOTIFog {

    private static final double VOID_DEPTH = 16;

    private static float start = Float.MAX_VALUE;
    private static float end = Float.MAX_VALUE;
    private static final float[] color = new float[4];
    private static FogShape shape = FogShape.SPHERE;

    private static boolean applied;
    private static float savedStart;
    private static float savedEnd;
    private static final float[] savedColor = new float[4];
    private static FogShape savedShape = FogShape.SPHERE;

    private BOTIFog() {
    }

    public static void capture(WorldRenderContext context) {
        start = RenderSystem.getShaderFogStart();
        end = RenderSystem.getShaderFogEnd();
        shape = RenderSystem.getShaderFogShape();
        System.arraycopy(RenderSystem.getShaderFogColor(), 0, color, 0, 4);
    }

    public static void apply() {
        if (applied || RenderSystem.getShaderFogStart() != Float.MAX_VALUE)
            return;

        savedStart = RenderSystem.getShaderFogStart();
        savedEnd = RenderSystem.getShaderFogEnd();
        savedShape = RenderSystem.getShaderFogShape();
        System.arraycopy(RenderSystem.getShaderFogColor(), 0, savedColor, 0, 4);

        RenderSystem.setShaderFogStart(start);
        RenderSystem.setShaderFogEnd(end);
        RenderSystem.setShaderFogShape(shape);
        RenderSystem.setShaderFogColor(color[0], color[1], color[2], color[3]);
        applied = true;
    }

    public static void restore() {
        if (!applied)
            return;

        RenderSystem.setShaderFogStart(savedStart);
        RenderSystem.setShaderFogEnd(savedEnd);
        RenderSystem.setShaderFogShape(savedShape);
        RenderSystem.setShaderFogColor(savedColor[0], savedColor[1], savedColor[2], savedColor[3]);
        applied = false;
    }

    public static void fogVoid(MatrixStack stack, VertexConsumerProvider.Immediate provider, ModelPart mask, Vec3d fromCamera) {
        float density = density(fromCamera);

        if (density <= 0)
            return;

        mask.render(stack, provider.getBuffer(RenderLayer.getDebugQuads()), 0, OverlayTexture.DEFAULT_UV,
                color[0], color[1], color[2], density);
        provider.draw();
    }

    private static float density(Vec3d fromCamera) {
        if (end <= start || start == Float.MAX_VALUE)
            return 0;

        double distance = shape == FogShape.CYLINDER
                ? Math.max(Math.hypot(fromCamera.x, fromCamera.z), Math.abs(fromCamera.y))
                : fromCamera.length();

        distance += VOID_DEPTH;

        if (distance <= start)
            return 0;

        float t = MathHelper.clamp((float) ((distance - start) / (end - start)), 0, 1);
        return t * t * (3 - 2 * t) * color[3];
    }
}
