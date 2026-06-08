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

        if (MinecraftClient.getInstance().world == null
                || MinecraftClient.getInstance().player == null) return;

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

        MinecraftClient.getInstance().getFramebuffer().endWrite();

        BOTI_HANDLER.setupFramebuffer();

        BOTI.copyFramebuffer(MinecraftClient.getInstance().getFramebuffer(), BOTI_HANDLER.afbo);

        VertexConsumerProvider.Immediate portalProvider = AIT_BUF_BUILDER_STORAGE.getBotiVertexConsumer();

        // Enable stencil testing
        GL11.glEnable(GL11.GL_STENCIL_TEST);

        GL11.glStencilMask(0xFF);
        RenderSystem.depthMask(true);
        RenderSystem.colorMask(true, true, true, true);

        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

        stack.push();
        stack.translate(0, -0.7f, 0.05);
        stack.scale(0.65f, 0.65f, 0.65f);
        frame.render(stack, portalProvider.getBuffer(RenderLayer.getEntityTranslucentCull(CIRCLE_TEXTURE)), 0xf000f0, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        portalProvider.draw();
        stack.pop();

        copyDepth(BOTI_HANDLER.afbo, MinecraftClient.getInstance().getFramebuffer());

        BOTI_HANDLER.afbo.beginWrite(false);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        GL11.glStencilMask(0x00);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MinecraftClient.getInstance().getTickDelta() + MinecraftClient.getInstance().player.age));
        stack.translate(0, -1, 400);

        // --- DISABLE FOG ---
        // Save the current fog state and push it to infinity
        float oldFogStart = RenderSystem.getShaderFogStart();
        float oldFogEnd = RenderSystem.getShaderFogEnd();
        RenderSystem.setShaderFogStart(Float.MAX_VALUE);
        RenderSystem.setShaderFogEnd(Float.MAX_VALUE);

        VortexRender util = VortexRender.getCurrentInstance();
        util.render(stack);

        // Ensure the provider draws while the fog is disabled
        portalProvider.draw();

        // --- RESTORE FOG ---
        // Bring the fog back to normal for the rest of the game world
        RenderSystem.setShaderFogStart(oldFogStart);
        RenderSystem.setShaderFogEnd(oldFogEnd);

        stack.pop();

        MinecraftClient.getInstance().getFramebuffer().beginWrite(true);

        BOTI.copyColor(BOTI_HANDLER.afbo, MinecraftClient.getInstance().getFramebuffer());

        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0xFF);

        RenderSystem.depthMask(true);

        stack.pop();

    }
}
