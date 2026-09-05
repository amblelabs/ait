package dev.amble.ait.client.boti;

import java.util.HashMap;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

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
    public static final Collection<RiftEntity> RIFT_RENDERING_QUEUE = new LinkedList<>();
    public static BOTIInit BOTI_HANDLER = new BOTIInit();
    public static AITBufferBuilderStorage AIT_BUF_BUILDER_STORAGE = new AITBufferBuilderStorage();
    public static Collection<DoorBlockEntity> DOOR_RENDER_QUEUE = new LinkedList<>();
    public static Collection<BOTIPaintingEntity> GALLIFREYAN_RENDER_QUEUE = new LinkedList<>();
    public static Collection<BOTIPaintingEntity> TRENZALORE_PAINTING_QUEUE = new LinkedList<>();
    public static Collection<ExteriorBlockEntity> EXTERIOR_RENDER_QUEUE = new LinkedList<>();
    public static Queue<DoorBlockEntity> DOOR_RENDER_QUEUE = new LinkedList<>();
    /** Last interior door rendered per TARDIS, cached by TardisDoorBOTI (which always has it, at END). The
     *  gbuffer-injection probe reuses it next frame to stamp the doorway stencil aperture, because
     *  DOOR_RENDER_QUEUE is empty at AFTER_ENTITIES under Sodium (block entities render after that event). */
    public static final Map<UUID, DoorBlockEntity> LAST_RENDERED_DOOR = new HashMap<>();
    public static Queue<BOTIPaintingEntity> GALLIFREYAN_RENDER_QUEUE = new LinkedList<>();
    public static Queue<BOTIPaintingEntity> TRENZALORE_PAINTING_QUEUE = new LinkedList<>();
    public static Queue<ExteriorBlockEntity> EXTERIOR_RENDER_QUEUE = new LinkedList<>();
    /** Last exterior BE rendered per TARDIS, cached by TardisExteriorBOTI (which has it at END). The exterior
     *  gbuffer-injection ({@link dev.amble.ait.client.boti.iris.ExteriorGbufferInjection}) reuses it next frame
     *  to stamp the exterior doorway stencil aperture, because EXTERIOR_RENDER_QUEUE is empty at AFTER_ENTITIES
     *  under Sodium (block entities render after that event) - the mirror of {@link #LAST_RENDERED_DOOR}. */
    public static final Map<UUID, ExteriorBlockEntity> LAST_RENDERED_EXTERIOR = new HashMap<>();
    private static boolean HAS_BEEN_WARNED = false;

    /** The GL id of the framebuffer currently bound for drawing. Under Iris this is Iris's live world
     *  target, not client.getFramebuffer(); in the vanilla pipeline the two are the same. */
    public static int currentDrawFbo() {
        return GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
    }

    public static void copyFramebuffer(Framebuffer src, Framebuffer dest) {
        copyColor(src, dest);
        copyDepth(src, dest);
    }

    public static void copyColor(Framebuffer src, Framebuffer dest) {
        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, src.fbo);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, dest.fbo);
        GlStateManager._glBlitFrameBuffer(0, 0, src.textureWidth, src.textureHeight, 0, 0, dest.textureWidth, dest.textureHeight, GlConst.GL_COLOR_BUFFER_BIT, GlConst.GL_NEAREST);
    }

    public static ShaderProgram COPY_DEPTH_PROGRAM;

    public static void copyDepth(Framebuffer src, Framebuffer dest) {
        if (!MinecraftClient.IS_SYSTEM_MAC || COPY_DEPTH_PROGRAM == null || src.getDepthAttachment() <= 0) {
            blitDepth(src, dest);
            return;
        }

        dest.beginWrite(true);

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

    private static void blitDepth(Framebuffer src, Framebuffer dest) {
        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, src.fbo);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, dest.fbo);
        GlStateManager._glBlitFrameBuffer(0, 0, src.textureWidth, src.textureHeight, 0, 0, dest.textureWidth, dest.textureHeight, GlConst.GL_DEPTH_BUFFER_BIT, GlConst.GL_NEAREST);
        GL11.glGetError();
    }

    /** Blit a Framebuffer's colour into a raw destination FBO id (e.g. Iris's live world target). */
    public static void copyColorToFbo(Framebuffer src, int destFbo, int w, int h) {
        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, src.fbo);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, destFbo);
        GlStateManager._glBlitFrameBuffer(0, 0, src.textureWidth, src.textureHeight,
                0, 0, w, h, GlConst.GL_COLOR_BUFFER_BIT, GlConst.GL_NEAREST);
    }

    /** Blit a raw source FBO id's colour into a Framebuffer (e.g. the live scene -> afbo backdrop). */
    public static void copyColorFromFbo(int srcFbo, int w, int h, Framebuffer dest) {
        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, srcFbo);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, dest.fbo);
        GlStateManager._glBlitFrameBuffer(0, 0, w, h,
                0, 0, dest.textureWidth, dest.textureHeight, GlConst.GL_COLOR_BUFFER_BIT, GlConst.GL_NEAREST);
    }

    /** Copy the live scene (raw source FBO id) colour+depth into afbo, replacing copyFramebuffer(main, afbo). */
    public static void copyFramebufferFromFbo(int srcFbo, int w, int h, Framebuffer dest) {
        copyColorFromFbo(srcFbo, w, h, dest);
        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, srcFbo);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, dest.fbo);
        GlStateManager._glBlitFrameBuffer(0, 0, w, h,
                0, 0, dest.textureWidth, dest.textureHeight, GlConst.GL_DEPTH_BUFFER_BIT, GlConst.GL_NEAREST);
        GL11.glGetError();
    }

    /** Copy afbo's depth into a raw destination FBO id. Mirrors copyDepth's non-Mac blit and Mac shader path. */
    public static void copyDepthToFbo(Framebuffer src, int destFbo, int w, int h) {
        if (!MinecraftClient.IS_SYSTEM_MAC || COPY_DEPTH_PROGRAM == null || src.getDepthAttachment() <= 0) {
            GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, src.fbo);
            GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, destFbo);
            GlStateManager._glBlitFrameBuffer(0, 0, src.textureWidth, src.textureHeight,
                    0, 0, w, h, GlConst.GL_DEPTH_BUFFER_BIT, GlConst.GL_NEAREST);
            GL11.glGetError();
            return;
        }

        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, destFbo);
        RenderSystem.viewport(0, 0, w, h);

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

    /** Captured GL state for one BOTI composite, so the callback restores exactly what it found and never
     *  leaves Iris's next pass on the wrong target or with dirty stencil/depth state. */
    public static final class BotiCompositeState {
        int drawFbo;
        final int[] viewport = new int[4];
        boolean stencilEnabled;
        boolean depthMask;
    }

    /** Capture the live draw target + the GL state the composite mutates. Call at the very start of a variant. */
    public static BotiCompositeState beginBotiComposite() {
        BotiCompositeState s = new BotiCompositeState();
        s.drawFbo = currentDrawFbo();
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, s.viewport);
        s.stencilEnabled = GL11.glIsEnabled(GL11.GL_STENCIL_TEST);
        s.depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        return s;
    }

    /** Rebind the captured target + restore state. Call after the afbo colour has been blitted back. */
    public static void endBotiComposite(BotiCompositeState s) {
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, s.drawFbo);
        RenderSystem.viewport(s.viewport[0], s.viewport[1], s.viewport[2], s.viewport[3]);
        GL11.glStencilMask(0xFF);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        if (!s.stencilEnabled) GL11.glDisable(GL11.GL_STENCIL_TEST);
        RenderSystem.depthMask(s.depthMask);
        RenderSystem.enableCull();
    }

    public static void setFramebufferColor(Framebuffer src, float r, float g, float b, float a) {
        src.setClearColor(r, g, b, a);
    }

    private static final Matrix4f IDENTITY_MATRIX = new Matrix4f();

    public static void resetStencilByDraw() {
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0xFF);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 0xFF);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
        drawFullscreenQuad(false, false);
    }

    public static void resetDepthByDraw() {
        GL11.glStencilMask(0x00);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        drawFullscreenQuad(false, true);
    }

    /**
     * Writes far depth (1.0) wherever the CURRENT stencil test passes, without touching colour. Used by the
     * gbuffer-injection path to punch a depth "hole" in the doorway aperture: the caller sets the stencil test to
     * the aperture (e.g. {@code glStencilFunc(GL_EQUAL,1,...)}), calls this, and the injected portal world then
     * draws over whatever blocks were behind the door instead of being depth-occluded by them. The stencil test
     * is left as the caller set it (this only draws; it does not change stencil func/op).
     */
    public static void clearDepthInStencilRegion() {
        drawFullscreenQuad(false, true);
    }

    /**
     * Writes NEAR depth (0.0) wherever the CURRENT stencil test passes. Called after the portal injection so
     * translucent geometry drawn later (e.g. glass behind/around the door, rendered in the post-AFTER_ENTITIES
     * translucent pass) is depth-occluded by the aperture instead of showing through the portal. The already-drawn
     * portal colour is unaffected; only the depth used for subsequent tests is flattened to the front.
     */
    public static void writeNearDepthInStencilRegion() {
        drawFullscreenQuad(false, true, 0.0);
    }

    /**
     * Fills the CURRENT stencil region with a solid colour (no depth write). Used as the portal's sky/fog backdrop:
     * regions of the aperture with no injected terrain (the sky) would otherwise show the interior scene behind
     * them; this paints the exterior fog colour there first, exactly like Phase A clears its afbo to the fog colour.
     */
    public static void fillColorInStencilRegion(float r, float g, float b) {
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthMask(false);
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

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder builder = Tessellator.getInstance().getBuffer();
        builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        builder.vertex(-1.0, -1.0, 0.0).color(r, g, b, 1f).next();
        builder.vertex(1.0, -1.0, 0.0).color(r, g, b, 1f).next();
        builder.vertex(1.0, 1.0, 0.0).color(r, g, b, 1f).next();
        builder.vertex(-1.0, 1.0, 0.0).color(r, g, b, 1f).next();
        BufferRenderer.drawWithGlobalProgram(builder.end());

        modelView.pop();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(prevProjection, prevSorter);

        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
    }

    private static void drawFullscreenQuad(boolean writeColor, boolean writeDepth) {
        drawFullscreenQuad(writeColor, writeDepth, 1.0);
    }

    private static void drawFullscreenQuad(boolean writeColor, boolean writeDepth, double depthValue) {
        RenderSystem.colorMask(writeColor, writeColor, writeColor, writeColor);
        RenderSystem.depthMask(writeDepth);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_ALWAYS);
        RenderSystem.disableCull();

        if (writeDepth)
            GL11.glDepthRange(depthValue, depthValue);

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

    public static void tryWarn(MinecraftClient client) {
        if (HAS_BEEN_WARNED)
            return;

        if (warn(client)) AITModClient.CONFIG.enableTardisBOTI = false;

        HAS_BEEN_WARNED = true;
    }

    private static boolean warn(MinecraftClient client) {
        if (DependencyChecker.hasIndium())
            return false;

        return false;
    }
}
