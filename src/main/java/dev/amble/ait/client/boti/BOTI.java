package dev.amble.ait.client.boti;

import static dev.amble.ait.client.renderers.entities.GallifreyFallsPaintingEntityRenderer.FRAME_TEXTURE;
import static dev.amble.ait.client.renderers.entities.GallifreyFallsPaintingEntityRenderer.PAINTING_TEXTURE;
import static dev.amble.ait.client.renderers.entities.RiftEntityRenderer.CIRCLE_TEXTURE;

import java.util.LinkedList;
import java.util.Queue;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.decoration.GallifreyFallsFrameModel;
import dev.amble.ait.client.models.decoration.GallifreyFallsModel;
import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.client.models.exteriors.ExteriorModel;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.client.renderers.VortexUtil;
import dev.amble.ait.core.blockentities.DoorBlockEntity;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.entities.GallifreyFallsPaintingEntity;
import dev.amble.ait.core.entities.RiftEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;


public class BOTI {

    public static final Queue<RiftEntity> RIFT_RENDERING_QUEUE = new LinkedList<>();
    public static BOTIHandler BOTI_HANDLER = new BOTIHandler();
    public static AITBufferBuilderStorage AIT_BUF_BUILDER_STORAGE = new AITBufferBuilderStorage();
    public static Queue<DoorBlockEntity> DOOR_RENDER_QUEUE = new LinkedList<>();
    public static Queue<GallifreyFallsPaintingEntity> PAINTING_RENDER_QUEUE = new LinkedList<>();
    public static Queue<ExteriorBlockEntity> EXTERIOR_RENDER_QUEUE = new LinkedList<>();

    public static void renderGallifreyFallsPainting(MatrixStack stack, SinglePartEntityModel singlePartEntityModel, int light) {
        if (!AITMod.CONFIG.CLIENT.ENABLE_TARDIS_BOTI)
            return;

        if (MinecraftClient.getInstance().world == null
                || MinecraftClient.getInstance().player == null) return;

        GallifreyFallsFrameModel model = new GallifreyFallsFrameModel(GallifreyFallsFrameModel.getTexturedModelData().createModel());

        stack.push();

        MinecraftClient.getInstance().getFramebuffer().endWrite();

        BOTI_HANDLER.setupFramebuffer();

        BOTI.copyFramebuffer(MinecraftClient.getInstance().getFramebuffer(), BOTI_HANDLER.afbo);

        VertexConsumerProvider.Immediate botiProvider = AIT_BUF_BUILDER_STORAGE.getBotiVertexConsumer();

        model.render(stack, botiProvider.getBuffer(AITRenderLayers.getEntityCutout(FRAME_TEXTURE)), light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
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
        ((GallifreyFallsFrameModel) singlePartEntityModel).renderWithFbo(stack, botiProvider, null, 0xf000f0, OverlayTexture.DEFAULT_UV, 0, 0, 0, 1);
        botiProvider.draw();
        copyDepth(BOTI_HANDLER.afbo, MinecraftClient.getInstance().getFramebuffer());
        // RenderSystem.depthMask(false);

        BOTI_HANDLER.afbo.beginWrite(false);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        stack.pop();

        GL11.glStencilMask(0x00);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);

        // Render the falls model (this should only render inside the frame)
        stack.push();
        stack.translate(0, 0, -1.5f);
        RenderSystem.enableCull();
        GallifreyFallsModel.getTexturedModelData().createModel().render(stack, botiProvider.getBuffer(AITRenderLayers.getBotiInterior(PAINTING_TEXTURE)), 0xf000f0, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableCull();
        botiProvider.draw();
        stack.pop();

        MinecraftClient.getInstance().getFramebuffer().beginWrite(true);

        BOTI.copyColor(BOTI_HANDLER.afbo, MinecraftClient.getInstance().getFramebuffer());

        GL11.glDisable(GL11.GL_STENCIL_TEST);

        RenderSystem.depthMask(true);

        stack.pop();
    }
    public static void renderInteriorDoorBoti(Tardis tardis1, DoorBlockEntity door, ClientExteriorVariantSchema variant, MatrixStack stack, Identifier frameTex, SinglePartEntityModel frame, ModelPart mask, int light) {
        if (!variant.parent().hasPortals()) return;

        if (!AITMod.CONFIG.CLIENT.ENABLE_TARDIS_BOTI)
            return;

        if (MinecraftClient.getInstance().world == null
                || MinecraftClient.getInstance().player == null) return;

        Tardis tardis = tardis1;

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

        MinecraftClient.getInstance().getFramebuffer().endWrite();

        BOTI_HANDLER.setupFramebuffer();

        BOTI.copyFramebuffer(MinecraftClient.getInstance().getFramebuffer(), BOTI_HANDLER.afbo);

        VertexConsumerProvider.Immediate botiProvider = AIT_BUF_BUILDER_STORAGE.getBotiVertexConsumer();

        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0xFF);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

        RenderSystem.depthMask(true);
        stack.push();
        Vec3d vec = variant.parent().door().adjustPortalPos(new Vec3d(0, -1.1725f, 0), Direction.NORTH);
        stack.translate(vec.x, vec.y, vec.z);
        stack.scale((float) variant.parent().portalWidth(), (float) variant.parent().portalHeight(), 1f);
        if (tardis.travel().getState() == TravelHandlerBase.State.LANDED)
            mask.render(stack, botiProvider.getBuffer(RenderLayer.getEndGateway()), 0xf000f0, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        else {
            mask.render(stack, botiProvider.getBuffer(RenderLayer.getEntityTranslucentCull(frameTex)), 0xf000f0, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        }
        botiProvider.draw();
        stack.pop();
        copyDepth(BOTI_HANDLER.afbo, MinecraftClient.getInstance().getFramebuffer());

        BOTI_HANDLER.afbo.beginWrite(false);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        GL11.glStencilMask(0x00);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);

        stack.push();
        if (!tardis.travel().autopilot() && tardis.travel().getState() != TravelHandlerBase.State.LANDED)
            stack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float) MinecraftClient.getInstance().player.age / ((float) 200 / tardis.travel().speed()) * 360f));
        if (!tardis.crash().isNormal())
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) MinecraftClient.getInstance().player.age / 100 * 360f));
        stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) MinecraftClient.getInstance().player.age / 100 * 360f));
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        stack.translate(0, 0, 500);
        stack.scale(1.5f, 1.5f, 1.5f);
        VortexUtil util = tardis.stats().getVortexEffects().toUtil();
        if (!tardis.travel().isLanded() /*&& !tardis.flight().isFlying()*/) {
            util.renderVortex(stack);
            /*// TODO not a clue if this will work but oh well - Loqor
            stack.push();
            stack.scale(0.9f, 0.9f, 0.9f);
            util.renderVortex(stack);
            stack.pop();*/
        }
        botiProvider.draw();
        stack.pop();

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

        ((DoorModel) frame).renderWithAnimations(door, frame.getPart(), stack, botiProvider.getBuffer(AITRenderLayers.getBotiInterior(variant.texture())), light, OverlayTexture.DEFAULT_UV, 1, 1F, 1.0F, 1.0F);
        //((DoorModel) frame).render(stack, botiProvider.getBuffer(AITRenderLayers.getBotiInterior(variant.texture())), light, OverlayTexture.DEFAULT_UV, 1, 1F, 1.0F, 1.0F);
        botiProvider.draw();
        stack.pop();

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        if (variant.emission() != null && tardis.fuel().hasPower())
            ((DoorModel) frame).renderWithAnimations(door, frame.getPart(), stack, botiProvider.getBuffer(AITRenderLayers.getBotiInteriorEmission(variant.emission())), 0xf000f0, OverlayTexture.DEFAULT_UV, 1, tardis.alarm().enabled().get() ? 0.3f : 1F, tardis.alarm().enabled().get() ? 3.0f : 1.0F, 1.0F);
        //((DoorModel) frame).render(stack, botiProvider.getBuffer(AITRenderLayers.getBotiInteriorEmission(variant.emission())), 0xf000f0, OverlayTexture.DEFAULT_UV, 1, 1F, 1.0F, 1.0F);
        botiProvider.draw();
        stack.pop();

        MinecraftClient.getInstance().getFramebuffer().beginWrite(true);

        BOTI.copyColor(BOTI_HANDLER.afbo, MinecraftClient.getInstance().getFramebuffer());

        GL11.glDisable(GL11.GL_STENCIL_TEST);

        RenderSystem.depthMask(true);

        stack.pop();
    }

    public static void renderExteriorBoti(ExteriorBlockEntity exterior, ClientExteriorVariantSchema variant, MatrixStack stack, Identifier frameTex, SinglePartEntityModel frame, ModelPart mask, int light) {
        if (!AITMod.CONFIG.CLIENT.ENABLE_TARDIS_BOTI)
            return;

        if (MinecraftClient.getInstance().world == null
                || MinecraftClient.getInstance().player == null) return;

        stack.push();

        MinecraftClient.getInstance().getFramebuffer().endWrite();

        BOTI_HANDLER.setupFramebuffer();

        BOTI.copyFramebuffer(MinecraftClient.getInstance().getFramebuffer(), BOTI_HANDLER.afbo);

        VertexConsumerProvider.Immediate botiProvider = AIT_BUF_BUILDER_STORAGE.getBotiVertexConsumer();

        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0xFF);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

        RenderSystem.depthMask(true);
        stack.push();
        Vec3d vec = variant.parent().adjustPortalPos(new Vec3d(0, -1.1725f, 0), (byte) 0);
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        stack.translate(vec.x, vec.y, vec.z);
        stack.scale((float) variant.parent().portalWidth(), (float) variant.parent().portalHeight(), 1f);

        if (exterior.tardis().get().travel().getState() == TravelHandlerBase.State.LANDED)
            mask.render(stack, botiProvider.getBuffer(RenderLayer.getEndGateway()), 0xf000f0, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        else
            mask.render(stack, botiProvider.getBuffer(RenderLayer.getEntityTranslucentCull(frameTex)), 0xf000f0, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        botiProvider.draw();
        stack.pop();

        copyDepth(BOTI_HANDLER.afbo, MinecraftClient.getInstance().getFramebuffer());

        BOTI_HANDLER.afbo.beginWrite(false);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        GL11.glStencilMask(0x00);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);

        //stack.push();
        //stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0));
        //stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        //stack.translate(-0.5f, -0.1, 11);
        // nothing for now
        //botiProvider.draw();
        //stack.pop();

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

        ((ExteriorModel) frame).renderDoors(exterior, frame.getPart(), stack, botiProvider.getBuffer(AITRenderLayers.getBotiInterior(variant.texture())), light, OverlayTexture.DEFAULT_UV, 1, 1F, 1.0F, 1.0F, true);
        botiProvider.draw();
        stack.pop();

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        if (variant.emission() != null && exterior.tardis().get().fuel().hasPower())
            ((ExteriorModel) frame).renderDoors(exterior, frame.getPart(), stack, botiProvider.getBuffer(AITRenderLayers.getBotiInteriorEmission(variant.emission())), 0xf000f0, OverlayTexture.DEFAULT_UV, 1, exterior.tardis().get().alarm().enabled().get() ? 0.3f : 1F, exterior.tardis().get().alarm().enabled().get() ? 0.3f : 1.0F, 1.0F, true);
        botiProvider.draw();
        stack.pop();

        MinecraftClient.getInstance().getFramebuffer().beginWrite(true);

        BOTI.copyColor(BOTI_HANDLER.afbo, MinecraftClient.getInstance().getFramebuffer());

        GL11.glDisable(GL11.GL_STENCIL_TEST);

        stack.pop();
    }

    public static void renderRiftBoti(MatrixStack stack, SinglePartEntityModel frame, int pack) {
        if (!AITMod.CONFIG.CLIENT.ENABLE_TARDIS_BOTI)
            return;

        if (MinecraftClient.getInstance().world == null
                || MinecraftClient.getInstance().player == null) return;

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

        MinecraftClient.getInstance().getFramebuffer().endWrite();

        BOTI_HANDLER.setupFramebuffer();

        BOTI.copyFramebuffer(MinecraftClient.getInstance().getFramebuffer(), BOTI_HANDLER.afbo);

        VertexConsumerProvider.Immediate portalProvider = AIT_BUF_BUILDER_STORAGE.getBotiVertexConsumer();

        // Enable stencil testing and clear the stencil buffer
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0xFF);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

        RenderSystem.depthMask(true);
        stack.push();
        stack.translate(0, -0.9, 0.05);
        stack.scale(1, 1, 1);
        frame.render(stack, portalProvider.getBuffer(RenderLayer.getEntityTranslucentCull(CIRCLE_TEXTURE)), 0xf000f0, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        portalProvider.draw();
        stack.pop();
        copyDepth(BOTI_HANDLER.afbo, MinecraftClient.getInstance().getFramebuffer());

        BOTI_HANDLER.afbo.beginWrite(false);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        GL11.glStencilMask(0x00);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);

        stack.push();
        //stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
        stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) MinecraftClient.getInstance().player.age / 50 * 360f));
        stack.translate(0, -1, 500);
        VortexUtil util = new VortexUtil("war");
        util.renderVortex(stack);
        portalProvider.draw();
        stack.pop();

        MinecraftClient.getInstance().getFramebuffer().beginWrite(true);

        BOTI.copyColor(BOTI_HANDLER.afbo, MinecraftClient.getInstance().getFramebuffer());

        GL11.glDisable(GL11.GL_STENCIL_TEST);

        RenderSystem.depthMask(true);

        stack.pop();
    }

    private static void copyFramebuffer(Framebuffer src, Framebuffer dest) {
        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, src.fbo);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, dest.fbo);
        GlStateManager._glBlitFrameBuffer(0, 0, src.textureWidth, src.textureHeight, 0, 0, dest.textureWidth, dest.textureHeight, GlConst.GL_DEPTH_BUFFER_BIT | GlConst.GL_COLOR_BUFFER_BIT, GlConst.GL_NEAREST);
    }

    private static void copyColor(Framebuffer src, Framebuffer dest) {
        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, src.fbo);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, dest.fbo);
        GlStateManager._glBlitFrameBuffer(0, 0, src.textureWidth, src.textureHeight, 0, 0, dest.textureWidth, dest.textureHeight, GlConst.GL_COLOR_BUFFER_BIT, GlConst.GL_NEAREST);
    }

    private static void copyDepth(Framebuffer src, Framebuffer dest) {
        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, src.fbo);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, dest.fbo);
        GlStateManager._glBlitFrameBuffer(0, 0, src.textureWidth, src.textureHeight, 0, 0, dest.textureWidth, dest.textureHeight, GlConst.GL_DEPTH_BUFFER_BIT, GlConst.GL_NEAREST);
    }
}
