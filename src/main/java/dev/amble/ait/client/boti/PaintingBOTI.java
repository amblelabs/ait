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

        if (MinecraftClient.getInstance().world == null
                || MinecraftClient.getInstance().player == null) return;

        PaintingFrameModel model = new PaintingFrameModel(PaintingFrameModel.getTexturedModelData().createModel());

        stack.push();

        MinecraftClient.getInstance().getFramebuffer().endWrite();

        BOTI_HANDLER.setupFramebuffer();

        BOTI.copyFramebuffer(MinecraftClient.getInstance().getFramebuffer(), BOTI_HANDLER.afbo);

        VertexConsumerProvider.Immediate botiProvider = AIT_BUF_BUILDER_STORAGE.getBotiVertexConsumer();

        model.render(stack, botiProvider.getBuffer(AITRenderLayers.getEntityCutout(frameTexture)), light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        botiProvider.draw();

        stack.translate(0, 0, -0.125);

        // Enable stencil testing and clear the stencil buffer
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0xFF);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

        // Render the mask overtop the interior of the interior stuff

        RenderSystem.depthMask(true);
        stack.push();
        frame.renderWithFbo(stack, botiProvider, 0xf000f0, OverlayTexture.DEFAULT_UV, 0, 0, 0, 1, frameTexture);
        botiProvider.draw();
        BOTI.copyDepth(BOTI_HANDLER.afbo, MinecraftClient.getInstance().getFramebuffer());

        BOTI_HANDLER.afbo.beginWrite(false);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        stack.pop();

        GL11.glStencilMask(0x00);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);

        stack.push();
        stack.translate(0, 0, -4f);
        RenderSystem.enableCull();
        paintingContents.render(stack, botiProvider.getBuffer(AITRenderLayers.getBotiInterior(paintingContentsTexture)), 0xf000f0, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableCull();
        botiProvider.draw();
        stack.pop();

        MinecraftClient.getInstance().getFramebuffer().beginWrite(true);

        BOTI.copyColor(BOTI_HANDLER.afbo, MinecraftClient.getInstance().getFramebuffer());

        GL11.glDisable(GL11.GL_STENCIL_TEST);

        RenderSystem.depthMask(true);

        stack.pop();
    }
}
