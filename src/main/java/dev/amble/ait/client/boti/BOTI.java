package dev.amble.ait.client.boti;

import java.util.LinkedList;
import java.util.Queue;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgram;
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

    // The main framebuffer and the afbo have different depth formats (main is depth-only / DEPTH32F, the afbo is packed
    // GL_DEPTH24_STENCIL8). A glBlitFramebuffer that includes GL_DEPTH_BUFFER_BIT requires the depth formats to match
    // exactly, or the call is GL_INVALID_OPERATION and copies NOTHING - all-or-nothing. NVIDIA quietly tolerates the
    // mismatch; Apple's GL-over-Metal enforces the spec, so a combined COLOR|DEPTH blit failed and took the colour copy
    // down with it. As the afbo is never cleared, its colour then never refreshed and last frame's pixels accumulated:
    // the "smear". So colour and depth are always blitted as SEPARATE calls - the colour copy (all we need for the
    // visible result) can never be blocked by the depth copy failing.

    public static void copyFramebuffer(Framebuffer src, Framebuffer dest) {
        copyColor(src, dest);
        copyDepth(src, dest);
    }

    public static void copyColor(Framebuffer src, Framebuffer dest) {
        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, src.fbo);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, dest.fbo);
        GlStateManager._glBlitFrameBuffer(0, 0, src.textureWidth, src.textureHeight, 0, 0, dest.textureWidth, dest.textureHeight, GlConst.GL_COLOR_BUFFER_BIT, GlConst.GL_NEAREST);
    }

    /** Core shader (assets/ait/shaders/core/copy_depth) that samples a depth texture and writes gl_FragDepth. */
    public static ShaderProgram COPY_DEPTH_PROGRAM;

    /**
     * Copies depth from {@code src} to {@code dest}. A glBlitFramebuffer(GL_DEPTH_BUFFER_BIT) requires identical depth
     * formats, which the main framebuffer (depth-only) and the afbo (packed GL_DEPTH24_STENCIL8) do NOT have - so it is
     * rejected (GL_INVALID_OPERATION) on Apple's strict GL driver and the portal's depth never reached the main buffer
     * (world translucents like glass then drew over the doorway). Instead we sample src's depth texture and write
     * gl_FragDepth through a full-screen quad, which is per-fragment and format-agnostic, so it works on every driver.
     * Falls back to the blit only if the shader hasn't loaded yet.
     */
    public static void copyDepth(Framebuffer src, Framebuffer dest) {
        // Only Apple's strict GL driver rejects the mismatched-format depth blit; everywhere else the blit works and
        // stays the path (unchanged, proven). Use the shader copy only where the blit is actually broken.
        if (!MinecraftClient.IS_SYSTEM_MAC || COPY_DEPTH_PROGRAM == null || src.getDepthAttachment() <= 0) {
            blitDepth(src, dest);
            return;
        }

        dest.beginWrite(true); // bind dest as the draw framebuffer and set its viewport

        RenderSystem.colorMask(false, false, false, false);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_ALWAYS);
        RenderSystem.disableCull();

        Matrix4f prevProjection = new Matrix4f(RenderSystem.getProjectionMatrix());
        VertexSorter prevSorter = RenderSystem.getVertexSorting();
        RenderSystem.setProjectionMatrix(IDENTITY_MATRIX, VertexSorter.BY_DISTANCE);
        MatrixStack modelView = RenderSystem.getModelViewStack();
        modelView.push();
        modelView.loadIdentity();
        RenderSystem.applyModelViewMatrix();

        RenderSystem.setShaderTexture(0, src.getDepthAttachment());
        RenderSystem.setShader(() -> COPY_DEPTH_PROGRAM);

        BufferBuilder builder = Tessellator.getInstance().getBuffer();
        builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        builder.vertex(-1.0, -1.0, 0.0).texture(0.0f, 0.0f).next();
        builder.vertex(1.0, -1.0, 0.0).texture(1.0f, 0.0f).next();
        builder.vertex(1.0, 1.0, 0.0).texture(1.0f, 1.0f).next();
        builder.vertex(-1.0, 1.0, 0.0).texture(0.0f, 1.0f).next();
        BufferRenderer.drawWithGlobalProgram(builder.end());

        modelView.pop();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(prevProjection, prevSorter);

        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
    }

    /** Fallback depth transfer via blit (works where depth formats match, e.g. NVIDIA; no-ops on Apple). */
    private static void blitDepth(Framebuffer src, Framebuffer dest) {
        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, src.fbo);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, dest.fbo);
        GlStateManager._glBlitFrameBuffer(0, 0, src.textureWidth, src.textureHeight, 0, 0, dest.textureWidth, dest.textureHeight, GlConst.GL_DEPTH_BUFFER_BIT, GlConst.GL_NEAREST);
        GL11.glGetError(); // swallow a possible GL_INVALID_OPERATION so it doesn't leak into MC's error checks
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

    // The afbo's depth attachment is a packed depth-stencil texture (GL_DEPTH24_STENCIL8, see the framebuffer mixins).
    // We reset each aspect by drawing a full-screen quad rather than a single-aspect glClear: it costs nothing extra
    // here and a per-fragment draw is honoured identically on every driver, so the clear path can never become the
    // odd-one-out across GPUs. (The Mac "smear" was NOT a clear problem - it was the depth-inclusive framebuffer blit
    // failing on mismatched formats; see copyFramebuffer.)

    /** Resets the bound framebuffer's stencil to 0 everywhere, leaving colour and depth untouched. */
    public static void resetStencilByDraw() {
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0xFF);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 0xFF);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
        drawFullscreenQuad(false, false);
    }

    /** Resets the bound framebuffer's depth to the far plane, leaving colour and the current stencil mask untouched. */
    public static void resetDepthByDraw() {
        GL11.glStencilMask(0x00); // preserve the mask bits already written for this pass
        GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 0xFF); // let the quad cover the whole buffer regardless of stencil
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        drawFullscreenQuad(false, true);
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
