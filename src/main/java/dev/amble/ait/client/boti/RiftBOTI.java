package dev.amble.ait.client.boti;

import static dev.amble.ait.client.renderers.entities.RiftEntityRenderer.RIFT_TEXTURE;

import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.renderers.VortexRender;
import org.lwjgl.opengl.GL30;

public class RiftBOTI extends BOTI {
    public static void renderRiftBoti(MatrixStack stack, SinglePartEntityModel frame, int pack) {
        if (!AITModClient.CONFIG.enableTardisBOTI)
            return;

        if (MinecraftClient.getInstance().world == null
                || MinecraftClient.getInstance().player == null) return;

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

        MinecraftClient client = MinecraftClient.getInstance();

        // Store the current viewport state
        int[] viewport = new int[4];
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);

        client.getFramebuffer().endWrite();

        BOTI_HANDLER.setupFramebuffer();

        // Clear the framebuffer before copying
        BOTI_HANDLER.afbo.beginWrite(false);

        // Ensure viewport matches framebuffer dimensions on Apple
        GL11.glViewport(0, 0, BOTI_HANDLER.afbo.textureWidth, BOTI_HANDLER.afbo.textureHeight);

        GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        BOTI_HANDLER.afbo.endWrite();

        // Restore viewport
        GL11.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);

        BOTI.copyFramebuffer(client.getFramebuffer(), BOTI_HANDLER.afbo);

        // Bind the custom framebuffer for rendering
        BOTI_HANDLER.afbo.beginWrite(false);

        VertexConsumerProvider.Immediate portalProvider = AIT_BUF_BUILDER_STORAGE.getBotiVertexConsumer();

        // Only enable stencil if the framebuffer actually has a stencil attachment
        boolean hasStencil = BOTI_HANDLER.afbo.getDepthAttachment() > -1; // Check if stencil exists

        if (hasStencil) {
            GL11.glEnable(GL11.GL_STENCIL_TEST);
            GL11.glStencilMask(0xFF);
            GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
            GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
            GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        }

        RenderSystem.depthMask(true);
        stack.push();
        stack.translate(0, -0.9, 0.05);
        stack.scale(1, 1, 1);
        frame.render(stack, portalProvider.getBuffer(RenderLayer.getEntityTranslucentCull(RIFT_TEXTURE)), 0xf000f0, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        portalProvider.draw();
        stack.pop();

        // End write BEFORE copying depth
        BOTI_HANDLER.afbo.endWrite();

        copyDepth(BOTI_HANDLER.afbo, client.getFramebuffer());

        BOTI_HANDLER.afbo.beginWrite(false);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        if (hasStencil) {
            GL11.glStencilMask(0x00);
            GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
        }

        stack.push();
        //stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
        stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) client.player.age / 50 * 360f));
        stack.translate(0, -1, 500);
        VortexRender util = VortexRender.getCurrentInstance();
        util.render(stack);
        portalProvider.draw();
        stack.pop();

        // End write BEFORE copying back
        BOTI_HANDLER.afbo.endWrite();

// NEW: Copy stencil buffer to main framebuffer
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, BOTI_HANDLER.afbo.fbo);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, client.getFramebuffer().fbo);
        GL30.glBlitFramebuffer(
                0, 0, BOTI_HANDLER.afbo.textureWidth, BOTI_HANDLER.afbo.textureHeight,
                0, 0, client.getFramebuffer().textureWidth, client.getFramebuffer().textureHeight,
                GL30.GL_STENCIL_BUFFER_BIT, // Only copy stencil
                GL30.GL_NEAREST
        );

        client.getFramebuffer().beginWrite(false); // Don't clear!

// Enable stencil test for the color copy
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
        GL11.glStencilMask(0x00); // Don't write to stencil

        BOTI.copyColor(BOTI_HANDLER.afbo, client.getFramebuffer());

        if (hasStencil) {
            GL11.glStencilMask(0xFF);
            GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 0xFF);
            GL11.glDisable(GL11.GL_STENCIL_TEST);
        }

        RenderSystem.depthMask(true);

        // Restore viewport explicitly
        GL11.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);

        // Ensure all rendering is flushed
        RenderSystem.getModelViewStack().loadIdentity();
        RenderSystem.applyModelViewMatrix();

        stack.pop();
    }
}