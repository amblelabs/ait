package dev.amble.ait.client.boti;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.core.blockentities.DoorBlockEntity;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.entities.BOTIPaintingEntity;
import dev.amble.ait.core.entities.RiftEntity;

public class BOTI {
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static final Queue<RiftEntity> RIFT_RENDERING_QUEUE = new LinkedList<>();
    public static BOTIInit BOTI_HANDLER = new BOTIInit();
    public static AITBufferBuilderStorage AIT_BUF_BUILDER_STORAGE = new AITBufferBuilderStorage();
    public static Queue<DoorBlockEntity> DOOR_RENDER_QUEUE = new LinkedList<>();
    public static Queue<BOTIPaintingEntity> GALLIFREYAN_RENDER_QUEUE = new LinkedList<>();
    public static Queue<BOTIPaintingEntity> TRENZALORE_PAINTING_QUEUE = new LinkedList<>();
    public static Queue<ExteriorBlockEntity> EXTERIOR_RENDER_QUEUE = new LinkedList<>();
    private static boolean HAS_BEEN_WARNED = false;

    private static boolean BLIT_DIAG_LOGGED = false;

    public static void copyFramebuffer(Framebuffer src, Framebuffer dest) {
        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, src.fbo);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, dest.fbo);
        boolean diag = !BLIT_DIAG_LOGGED;
        if (diag) GL11.glGetError();
        GlStateManager._glBlitFrameBuffer(0, 0, src.textureWidth, src.textureHeight, 0, 0, dest.textureWidth, dest.textureHeight, GlConst.GL_DEPTH_BUFFER_BIT | GlConst.GL_COLOR_BUFFER_BIT, GlConst.GL_NEAREST);
        if (diag) AITMod.LOGGER.error("[BOTI-DIAG] copyFramebuffer blit(COLOR|DEPTH) err=0x{} src.fbo={} dest.fbo={}",
                Integer.toHexString(GL11.glGetError()), src.fbo, dest.fbo);
    }

    public static void copyColor(Framebuffer src, Framebuffer dest) {
        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, src.fbo);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, dest.fbo);
        boolean diag = !BLIT_DIAG_LOGGED;
        if (diag) GL11.glGetError();
        GlStateManager._glBlitFrameBuffer(0, 0, src.textureWidth, src.textureHeight, 0, 0, dest.textureWidth, dest.textureHeight, GlConst.GL_COLOR_BUFFER_BIT, GlConst.GL_NEAREST);
        if (diag) {
            AITMod.LOGGER.error("[BOTI-DIAG] copyColor blit(COLOR) err=0x{} src.fbo={} dest.fbo={}",
                    Integer.toHexString(GL11.glGetError()), src.fbo, dest.fbo);
            BLIT_DIAG_LOGGED = true; // copyColor is the last blit in a pass; stop after logging all three
        }
    }

    public static void copyDepth(Framebuffer src, Framebuffer dest) {
        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, src.fbo);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, dest.fbo);
        boolean diag = !BLIT_DIAG_LOGGED;
        if (diag) GL11.glGetError();
        GlStateManager._glBlitFrameBuffer(0, 0, src.textureWidth, src.textureHeight, 0, 0, dest.textureWidth, dest.textureHeight, GlConst.GL_DEPTH_BUFFER_BIT, GlConst.GL_NEAREST);
        if (diag) AITMod.LOGGER.error("[BOTI-DIAG] copyDepth blit(DEPTH) err=0x{} src.fbo={} dest.fbo={} (0x502=INVALID_OPERATION => mismatched depth formats, the smear)",
                Integer.toHexString(GL11.glGetError()), src.fbo, dest.fbo);
    }

    public static void setFramebufferColor(Framebuffer src, float r, float g, float b, float a) {
        src.setClearColor(r, g, b, a);
    }

    /** Restores core GL state after a BOTI pass so a failure can never leave the screen black. */
    public static void resetState() {
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        RenderSystem.depthMask(true);
        client.getFramebuffer().beginWrite(true);
    }

    private static final Matrix4f IDENTITY_MATRIX = new Matrix4f();

    // The BOTI framebuffer's depth attachment is a *packed* depth-stencil texture (see the framebuffer mixins, which
    // force GL_DEPTH24_STENCIL8 / GL_DEPTH32F_STENCIL8). NVIDIA honours a single-aspect glClear of one half of that
    // combined attachment, but Apple / AMD / Intel GL drivers silently drop it - so the stencil was never actually
    // reset and last frame's mask bled the interior across the screen (the "smear"). A per-fragment draw is honoured
    // on every driver, so we reset each aspect by drawing a full-screen quad instead of calling glClear.

    /** Resets the bound framebuffer's stencil to 0 everywhere, leaving colour and depth untouched. */
    public static void resetStencilByDraw() {
        logClearEnvOnce();
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0xFF);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 0xFF);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
        drawFullscreenQuad(false, false);
    }

    /** Resets the bound framebuffer's depth to the far plane, leaving colour and the current stencil mask untouched. */
    public static void resetDepthByDraw() {
        logStencilPatternOnce(); // read back the real stencil pattern the mask produced, before we touch stencil state
        GL11.glStencilMask(0x00); // preserve the mask bits already written for this pass
        GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 0xFF); // let the quad cover the whole buffer regardless of stencil
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        drawFullscreenQuad(false, true);
    }

    private static boolean CLEAR_ENV_LOGGED = false;
    private static boolean STENCIL_PATTERN_LOGGED = false;

    /** One-shot: capture the live GL environment (viewport/scissor/bound fb) when a real pass clears its stencil. */
    private static void logClearEnvOnce() {
        if (CLEAR_ENV_LOGGED)
            return;
        CLEAR_ENV_LOGGED = true;

        int[] vp = new int[4];
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, vp);
        int[] sc = new int[4];
        GL11.glGetIntegerv(GL11.GL_SCISSOR_BOX, sc);
        boolean scissor = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
        boolean stencil = GL11.glIsEnabled(GL11.GL_STENCIL_TEST);
        int drawFb = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        int readFb = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);

        AITMod.LOGGER.error("[BOTI-DIAG] clearEnv viewport=[{},{},{},{}] scissorTest={} scissorBox=[{},{},{},{}] stencilTest={} drawFb={} readFb={} afbo.fbo={} afbo={}x{}",
                vp[0], vp[1], vp[2], vp[3], scissor, sc[0], sc[1], sc[2], sc[3], stencil, drawFb, readFb,
                BOTI_HANDLER.afbo.fbo, BOTI_HANDLER.afbo.textureWidth, BOTI_HANDLER.afbo.textureHeight);
    }

    /**
     * One-shot: scan the afbo's ENTIRE stencil buffer right after the mask was drawn (the read framebuffer is the afbo
     * here) and histogram it. This avoids the two-point sampling blind spot (the doorway may not sit under a fixed
     * pixel). ones==0 => the mask wrote no stencil at all; ones≈whole buffer => it spread everywhere. Trust this only if
     * the STENCIL-INDEX self-test in the functional probe reported centre=1 corner=0.
     */
    private static void logStencilPatternOnce() {
        if (STENCIL_PATTERN_LOGGED)
            return;
        STENCIL_PATTERN_LOGGED = true;

        int w = BOTI_HANDLER.afbo.textureWidth;
        int h = BOTI_HANDLER.afbo.textureHeight;
        GL11.glGetError();

        int prevAlign = GL11.glGetInteger(GL11.GL_PACK_ALIGNMENT);
        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1); // tightly packed rows so w*h bytes maps 1:1

        ByteBuffer buf = BufferUtils.createByteBuffer(w * h);
        GL11.glReadPixels(0, 0, w, h, GL11.GL_STENCIL_INDEX, GL11.GL_UNSIGNED_BYTE, buf);
        int readErr = GL11.glGetError();

        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, prevAlign);

        int max = 0;
        int ones = 0;
        int nonzero = 0;
        int total = w * h;
        for (int i = 0; i < total; i++) {
            int v = buf.get(i) & 0xFF;
            if (v > max) max = v;
            if (v != 0) nonzero++;
            if (v == 1) ones++;
        }

        AITMod.LOGGER.error("[BOTI-DIAG] STENCIL HISTOGRAM after mask: max={} ones={}/{} nonzero={} ({}%) readErr=0x{}",
                max, ones, total, nonzero, Math.round(100.0 * nonzero / total), Integer.toHexString(readErr));
    }

    /**
     * Draws a screen-filling quad through the position program with identity matrices, writing only the aspects
     * requested. Depth test is forced to ALWAYS (and enabled) so the fragments always run - without an enabled depth
     * test the driver writes neither depth nor the stencil depth-pass op.
     *
     * The quad sits at NDC z = 0 (the centre of the frustum), NOT at the far plane. A quad at z = 1 lands exactly on
     * the far clip plane; the GL spec treats z == w as inside, but Apple's GL driver clips those fragments away, so the
     * clear produced zero fragments and neither aspect was reset (stale stencil -> smear, stale depth -> the interior
     * drew over the whole screen / went white). To still reset DEPTH to the far value we clamp glDepthRange to [1,1] for
     * the duration of the write, so every fragment stores far depth regardless of its geometry z. GL state the
     * surrounding pass relies on (depth func/range, colour mask, depth mask, cull) is restored on the way out.
     */
    private static void drawFullscreenQuad(boolean writeColor, boolean writeDepth) {
        RenderSystem.colorMask(writeColor, writeColor, writeColor, writeColor);
        RenderSystem.depthMask(writeDepth);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_ALWAYS);
        RenderSystem.disableCull();

        // Force every fragment to store the far depth value without putting the geometry on the clip boundary.
        if (writeDepth)
            GL11.glDepthRange(1.0, 1.0);

        Matrix4f prevProjection = new Matrix4f(RenderSystem.getProjectionMatrix());
        VertexSorter prevSorter = RenderSystem.getVertexSorting();
        RenderSystem.setProjectionMatrix(IDENTITY_MATRIX, VertexSorter.BY_DISTANCE);

        MatrixStack modelView = RenderSystem.getModelViewStack();
        modelView.push();
        modelView.loadIdentity();
        RenderSystem.applyModelViewMatrix();

        RenderSystem.setShader(GameRenderer::getPositionProgram);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        BufferBuilder builder = Tessellator.getInstance().getBuffer();
        builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        builder.vertex(-1.0, -1.0, 0.0).next();
        builder.vertex(1.0, -1.0, 0.0).next();
        builder.vertex(1.0, 1.0, 0.0).next();
        builder.vertex(-1.0, 1.0, 0.0).next();
        BufferRenderer.drawWithGlobalProgram(builder.end());

        modelView.pop();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(prevProjection, prevSorter);

        if (writeDepth)
            GL11.glDepthRange(0.0, 1.0);

        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
    }

    /** Solid white block texture, tinted to fill the portal background with a flat sky colour. */
    public static final Identifier SKY_TEXTURE = new Identifier("textures/block/white_concrete.png");

    /** Snaps a portal offset (block centre → portal) to a cardinal outward normal for BOTI transforms. */
    public static float[] horizontalNormal(double dx, double dz, float fallbackFacing) {
        if (Math.abs(dx) < 1.0E-4 && Math.abs(dz) < 1.0E-4) {
            Direction dir = Direction.fromRotation(fallbackFacing);
            return new float[]{dir.getOffsetX(), dir.getOffsetZ()};
        }

        if (Math.abs(dx) >= Math.abs(dz))
            return new float[]{Math.signum((float) dx), 0};

        return new float[]{0, Math.signum((float) dz)};
    }

    /**
     * Warns the user if they are missing Indium and have a non-Nvidia card.
     */
    public static void tryWarn(MinecraftClient client) {
        if (HAS_BEEN_WARNED)
            return;

        if (warn(client)) AITModClient.CONFIG.enableTardisBOTI = false;

        HAS_BEEN_WARNED = true;
    }

    /**
     * @return {@code true} if successfully warned the player, {@code false} otherwise
     */
    private static boolean warn(MinecraftClient client) {
        if (DependencyChecker.hasMacOs()) {
            tryWarnMac(client);
            return true;
        }

        if (DependencyChecker.hasIndium())
            return false;

        if (!DependencyChecker.hasNvidiaCard()) {
            tryWarnAmd(client);
            return true;
        }

        return false;
    }

    private static void tryWarnMac(MinecraftClient client) {
        client.player.sendMessage(Text.translatable("message.ait.boti.indium_required.mac").formatted(Formatting.RED), false);
    }

    private static void tryWarnAmd(MinecraftClient client) {
        client.player.sendMessage(Text.translatable("message.ait.boti.indium_required.amd").formatted(Formatting.RED), false);
    }
}
