package dev.amble.ait.client.boti;

import java.nio.ByteBuffer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;

import dev.amble.ait.AITMod;

public class BOTIInit {
    public Framebuffer afbo;

    /** One-shot flag: dump the afbo's real stencil attachment state to the log once, to diagnose the Mac smear. */
    private static boolean DIAG_LOGGED = false;

    public void setupFramebuffer() {
        Window window = MinecraftClient.getInstance().getWindow();

        if (afbo == null || afbo.textureWidth != window.getFramebufferWidth() || afbo.textureHeight != window.getFramebufferHeight()) {
            afbo = new SimpleFramebuffer(window.getFramebufferWidth(), window.getFramebufferHeight(), true, MinecraftClient.IS_SYSTEM_MAC);;
        }

        afbo.beginWrite(false);
        afbo.checkFramebufferStatus();

        if (!AITRenderHelper.getIsStencilEnabled(afbo)) {
            AITRenderHelper.setIsStencilEnabled(afbo, true);
        }

        logStencilDiagnostics();
    }

    /**
     * Functionally probes, exactly once, whether the bound afbo's stencil actually gates rendering on this GPU.
     * Attachment introspection is rejected on Apple's GL-over-Metal (GL_INVALID_ENUM), so instead we drive the real
     * stencil pipeline and read back COLOUR (which is reliable everywhere): stencil is set to 1 in a tiny centre region
     * only, then red is painted where stencil == 1 over a black background. We then read a centre pixel and a corner
     * pixel.
     *   centre red + corner black  -> stencil works (bug is elsewhere)
     *   centre red + corner RED     -> stencil test never gates: the interior draws everywhere -> the smear
     *   centre black               -> stencil writes/test are dropping entirely
     */
    private void logStencilDiagnostics() {
        if (DIAG_LOGGED)
            return;
        DIAG_LOGGED = true;

        int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
        AITMod.LOGGER.error("[BOTI-DIAG] mac={} vendor={} renderer={} version={}", MinecraftClient.IS_SYSTEM_MAC,
                GL11.glGetString(GL11.GL_VENDOR), GL11.glGetString(GL11.GL_RENDERER), GL11.glGetString(GL11.GL_VERSION));
        AITMod.LOGGER.error("[BOTI-DIAG] afbo.fbo={} fbStatus=0x{} (COMPLETE=0x{}) stencilFlag={}", afbo.fbo,
                Integer.toHexString(status), Integer.toHexString(GL30.GL_FRAMEBUFFER_COMPLETE),
                AITRenderHelper.getIsStencilEnabled(afbo));

        int w = afbo.textureWidth;
        int h = afbo.textureHeight;

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        RenderSystem.viewport(0, 0, w, h);
        GL11.glGetError(); // clear

        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0xFF);

        // stencil := 0 everywhere
        GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 0xFF);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
        probeQuad(-1f, -1f, 1f, 1f, 0f, 0f, 0f, false);

        // stencil := 1 in a tiny centre region only
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
        probeQuad(-0.03f, -0.03f, 0.03f, 0.03f, 0f, 0f, 0f, false);

        // colour := black everywhere (preserve stencil bits)
        GL11.glStencilMask(0x00);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        probeQuad(-1f, -1f, 1f, 1f, 0f, 0f, 0f, true);

        // colour := red only where stencil == 1
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        probeQuad(-1f, -1f, 1f, 1f, 1f, 0f, 0f, true);

        int drawErr = GL11.glGetError();

        ByteBuffer centre = BufferUtils.createByteBuffer(4);
        GL11.glReadPixels(w / 2, h / 2, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, centre);
        ByteBuffer corner = BufferUtils.createByteBuffer(4);
        GL11.glReadPixels(4, 4, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, corner);
        int readErr = GL11.glGetError();

        AITMod.LOGGER.error("[BOTI-DIAG] STENCIL PROBE centre=({},{},{}) corner=({},{},{}) drawErr=0x{} readErr=0x{} -> {}",
                centre.get(0) & 0xFF, centre.get(1) & 0xFF, centre.get(2) & 0xFF,
                corner.get(0) & 0xFF, corner.get(1) & 0xFF, corner.get(2) & 0xFF,
                Integer.toHexString(drawErr), Integer.toHexString(readErr),
                verdict(centre, corner));

        GL11.glStencilMask(0xFF);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
    }

    private static String verdict(ByteBuffer centre, ByteBuffer corner) {
        boolean centreRed = (centre.get(0) & 0xFF) > 128 && (centre.get(1) & 0xFF) < 128;
        boolean cornerRed = (corner.get(0) & 0xFF) > 128 && (corner.get(1) & 0xFF) < 128;
        if (centreRed && !cornerRed)
            return "STENCIL WORKS";
        if (centreRed)
            return "STENCIL NOT GATING (this is the smear)";
        return "STENCIL WRITES/TEST DROPPED";
    }

    /** Draws a screen-space quad (NDC rect, z=0 to avoid the far-plane clip) in the given colour for the probe. */
    private static void probeQuad(float x0, float y0, float x1, float y1, float r, float g, float b, boolean writeColor) {
        RenderSystem.colorMask(writeColor, writeColor, writeColor, writeColor);
        RenderSystem.depthMask(false);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_ALWAYS);
        RenderSystem.disableCull();

        Matrix4f prevProjection = new Matrix4f(RenderSystem.getProjectionMatrix());
        VertexSorter prevSorter = RenderSystem.getVertexSorting();
        RenderSystem.setProjectionMatrix(new Matrix4f(), VertexSorter.BY_DISTANCE);

        MatrixStack modelView = RenderSystem.getModelViewStack();
        modelView.push();
        modelView.loadIdentity();
        RenderSystem.applyModelViewMatrix();

        RenderSystem.setShader(GameRenderer::getPositionProgram);
        RenderSystem.setShaderColor(r, g, b, 1f);

        BufferBuilder builder = Tessellator.getInstance().getBuffer();
        builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        builder.vertex(x0, y0, 0.0).next();
        builder.vertex(x1, y0, 0.0).next();
        builder.vertex(x1, y1, 0.0).next();
        builder.vertex(x0, y1, 0.0).next();
        BufferRenderer.drawWithGlobalProgram(builder.end());

        modelView.pop();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(prevProjection, prevSorter);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    public void endFBO() {
        afbo.clear(MinecraftClient.IS_SYSTEM_MAC);
        afbo.endWrite();
    }

}
