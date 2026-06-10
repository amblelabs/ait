package dev.amble.ait.client.boti;

import static dev.amble.ait.client.renderers.entities.RiftEntityRenderer.CIRCLE_TEXTURE;
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

public class RiftBOTI extends BOTI {
    public static void renderRiftBoti(MatrixStack stack, SinglePartEntityModel frame, int pack) {
        if (!AITModClient.CONFIG.enableTardisBOTI)
            return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

        VertexConsumerProvider.Immediate portalProvider = AIT_BUF_BUILDER_STORAGE.getBotiVertexConsumer();

        // === PASS 1: THE DEPTH SHIELD (MAIN FRAMEBUFFER) ===
        // We draw the doorway mask directly to the main game world with color disabled.
        // This physically blocks clouds and weather from drawing over the portal space.
        RenderSystem.colorMask(false, false, false, false);
        RenderSystem.depthMask(true);

        stack.push();
        stack.translate(0, -0.7f, 0.05);
        stack.scale(0.65f, 0.65f, 0.65f);
        frame.render(stack, portalProvider.getBuffer(RenderLayer.getEntityTranslucentCull(CIRCLE_TEXTURE)), 0xf000f0, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        portalProvider.draw();
        stack.pop();

        // Re-enable color for the rest of the rendering
        RenderSystem.colorMask(true, true, true, true);

        // === PASS 2: SETUP CUSTOM FRAMEBUFFER ===
        client.getFramebuffer().endWrite();
        BOTI_HANDLER.setupFramebuffer();
        BOTI.copyFramebuffer(client.getFramebuffer(), BOTI_HANDLER.afbo);
        BOTI_HANDLER.afbo.beginWrite(false);

        // Clear AFBO entirely to prevent the AMD partial-clear corruption bug
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glEnable(GL11.GL_STENCIL_TEST);

        // === PASS 3: STENCIL MASK (AFBO) ===
        // We need the mask in the stencil buffer, but we MUST NOT write depth here.
        // If we write depth, the vortex will fail the depth test against the flat doorway!
        RenderSystem.colorMask(false, false, false, false);
        RenderSystem.depthMask(false); // <-- CRITICAL: Disables depth writes for the mask

        GL11.glStencilMask(0xFF);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

        stack.push();
        stack.translate(0, -0.7f, 0.05);
        stack.scale(0.65f, 0.65f, 0.65f);
        frame.render(stack, portalProvider.getBuffer(RenderLayer.getEntityTranslucentCull(CIRCLE_TEXTURE)), 0xf000f0, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        portalProvider.draw();
        stack.pop();

        // === PASS 4: VORTEX RENDERING ===
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthMask(true); // <-- Re-enable depth writes for the vortex itself
        GL11.glStencilMask(0x00);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(client.getTickDelta() + client.player.age));
        stack.translate(0, -1, 400);

        // Temporarily push fog to infinity so the vortex is always visible
        float oldFogStart = RenderSystem.getShaderFogStart();
        float oldFogEnd = RenderSystem.getShaderFogEnd();
        RenderSystem.setShaderFogStart(Float.MAX_VALUE);
        RenderSystem.setShaderFogEnd(Float.MAX_VALUE);

        VortexRender util = VortexRender.getCurrentInstance();
        util.render(stack);
        portalProvider.draw();

        // Restore fog
        RenderSystem.setShaderFogStart(oldFogStart);
        RenderSystem.setShaderFogEnd(oldFogEnd);

        stack.pop();

        // === PASS 5: COMPOSITE TO MAIN ===
        client.getFramebuffer().beginWrite(true);
        BOTI.copyColor(BOTI_HANDLER.afbo, client.getFramebuffer());

        // Clean up states
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0xFF);
        RenderSystem.depthMask(true);

        stack.pop();
    }
}
