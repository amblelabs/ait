package dev.amble.ait.client.boti;

import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.models.decoration.PaintingFrameModel;
import dev.amble.ait.client.renderers.AITRenderLayers;

public class PaintingBOTI extends BOTI {
    public static void renderBOTIPainting(MatrixStack stack, PaintingFrameModel frame,
                                          int light, SinglePartEntityModel paintingContents, Identifier frameTexture, Identifier paintingContentsTexture) {
        if (!AITModClient.CONFIG.enableTardisBOTI)
            return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        // Use the frame model for the shield
        PaintingFrameModel model = new PaintingFrameModel(PaintingFrameModel.getTexturedModelData().createModel());
        VertexConsumerProvider.Immediate botiProvider = AIT_BUF_BUILDER_STORAGE.getBotiVertexConsumer();

        // === PASS 1: THE DEPTH SHIELD (MAIN FRAMEBUFFER) ===
        // Draw the frame mask to the main world with color disabled to block clouds/weather.
        RenderSystem.colorMask(false, false, false, false);
        RenderSystem.depthMask(true);

        stack.push();
        // Draw the frame model so the game knows something solid exists here
        model.render(stack, botiProvider.getBuffer(AITRenderLayers.getEntityCutout(frameTexture)), light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        botiProvider.draw();
        stack.pop();

        RenderSystem.colorMask(true, true, true, true);

        // === PASS 2: SETUP CUSTOM FRAMEBUFFER ===
        client.getFramebuffer().endWrite();
        BOTI_HANDLER.setupFramebuffer();
        BOTI.copyFramebuffer(client.getFramebuffer(), BOTI_HANDLER.afbo);
        BOTI_HANDLER.afbo.beginWrite(false);

        // Clear both Depth/Stencil to prevent AMD partial-clear corruption
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glEnable(GL11.GL_STENCIL_TEST);

        // === PASS 3: STENCIL MASK (AFBO) ===
        RenderSystem.colorMask(false, false, false, false);
        RenderSystem.depthMask(false);

        GL11.glStencilMask(0xFF);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

        stack.push();
        // Render frame again to set the stencil mask
        frame.renderWithFbo(stack, botiProvider, 0xf000f0, OverlayTexture.DEFAULT_UV, 0, 0, 0, 1, frameTexture);
        botiProvider.draw();
        stack.pop();

        // === PASS 4: PAINTING CONTENT (AFBO) ===
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthMask(true);
        GL11.glStencilMask(0x00);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);

        stack.push();
        stack.translate(0, 0, -4f); // Original Z offset logic
        RenderSystem.enableCull();
        paintingContents.render(stack, botiProvider.getBuffer(AITRenderLayers.getBotiInterior(paintingContentsTexture)), 0xf000f0, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableCull();
        botiProvider.draw();
        stack.pop();

        // === PASS 5: COMPOSITE TO MAIN ===
        client.getFramebuffer().beginWrite(true);
        BOTI.copyColor(BOTI_HANDLER.afbo, client.getFramebuffer());

        // Cleanup
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0xFF);
        RenderSystem.depthMask(true);
    }
}
