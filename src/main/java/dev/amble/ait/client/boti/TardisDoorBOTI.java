package dev.amble.ait.client.boti;

import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.models.AnimatedModel;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.client.renderers.VortexRender;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.core.blockentities.DoorBlockEntity;
import dev.amble.ait.core.tardis.handler.StatsHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.registry.impl.CategoryRegistry;
import org.lwjgl.opengl.GL30;

public class TardisDoorBOTI extends BOTI {
    public static void renderInteriorDoorBoti(ClientTardis tardis, DoorBlockEntity door, ClientExteriorVariantSchema variant, MatrixStack stack, Identifier frameTex, AnimatedModel frame, ModelPart mask, int light, float tickDelta) {
        ExteriorVariantSchema parent = variant.parent();

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

        Vec3d skyColor = client.world.getSkyColor(client.player.getPos(), client.getTickDelta());
        if (AITModClient.CONFIG.greenScreenBOTI)
            BOTI.setFramebufferColor(BOTI_HANDLER.afbo, 0, 1, 0, 1);
        else
            BOTI.setFramebufferColor(BOTI_HANDLER.afbo, (float) skyColor.x, (float) skyColor.y, (float) skyColor.z, 1);

        BOTI.copyFramebuffer(client.getFramebuffer(), BOTI_HANDLER.afbo);

        // Bind the custom framebuffer for rendering
        BOTI_HANDLER.afbo.beginWrite(false);

        VertexConsumerProvider.Immediate botiProvider = AIT_BUF_BUILDER_STORAGE.getBotiVertexConsumer();

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
        StatsHandler stats = tardis.stats();
        Vector3f scale = tardis.travel().getScale();

        stack.scale((float) parent.portalWidth() * scale.x(),
                (float) parent.portalHeight() * scale.y(), scale.z());
        Vec3d vec = parent.door().adjustPortalPos(new Vec3d(0, -0.55f, 0), Direction.NORTH);
        stack.translate(vec.x, vec.y, vec.z);
        if (tardis.travel().getState() == TravelHandlerBase.State.LANDED) {
            RenderLayer whichOne = AITModClient.CONFIG.greenScreenBOTI ?
                    RenderLayer.getDebugFilledBox() : RenderLayer.getEndGateway();
            float[] colorsForGreenScreen = AITModClient.CONFIG.greenScreenBOTI ? new float[]{0, 1, 0, 1} : new float[] {(float) skyColor.x, (float) skyColor.y, (float) skyColor.z};
            mask.render(stack, botiProvider.getBuffer(whichOne), 0xf000f0, OverlayTexture.DEFAULT_UV, colorsForGreenScreen[0], colorsForGreenScreen[1], colorsForGreenScreen[2], 1);
        } else {
            mask.render(stack, botiProvider.getBuffer(RenderLayer.getEntityTranslucentCull(frameTex)), 0xf000f0, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        }
        botiProvider.draw();
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
        if (!tardis.travel().autopilot() && tardis.travel().getState() != TravelHandlerBase.State.LANDED)
            stack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float) client.player.age / ((float) 200 / tardis.travel().speed()) * 360f));
        if (!tardis.crash().isNormal())
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) client.player.age / 100 * 360f));
        stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) client.player.age / 100 * 360f));
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        stack.translate(0, 0, 500);
        stack.scale(1.5f, 1.5f, 1.5f);
        VortexRender util = stats.getVortexEffects().toRender();
        if (!tardis.travel().isLanded() /*&& !tardis.flight().isFlying()*/) {
            util.render(stack);
            /*// TODO not a clue if this will work but oh well - Loqor
            stack.push();
            stack.scale(0.9f, 0.9f, 0.9f);
            util.renderVortex(stack);
            stack.pop();*/
        }
        botiProvider.draw();
        stack.pop();

        if (!tardis.getExterior().getCategory().equals(CategoryRegistry.GEOMETRIC)) {
            stack.push();
            stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            stack.scale(scale.x, scale.y, scale.z);

            // TODO: use DoorRenderer/ClientLightUtil instead.
            frame.renderWithAnimations(tardis, door, frame.getPart(), stack, botiProvider.getBuffer(AITRenderLayers.getBotiInterior(variant.texture())), light, OverlayTexture.DEFAULT_UV, 1, 1F, 1.0F, 1.0F, tickDelta);
            //((DoorModel) frame).render(stack, botiProvider.getBuffer(AITRenderLayers.getBotiInterior(variant.texture())), light, OverlayTexture.DEFAULT_UV, 1, 1F, 1.0F, 1.0F);
            botiProvider.draw();
            stack.pop();

            stack.push();
            stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            stack.scale(scale.x, scale.y, scale.z);
            if (variant.emission() != null) {
                float u = 1;
                float t = 1;
                float s = 1;

                if ((stats.getName() != null && "partytardis".equalsIgnoreCase(stats.getName()) || (!tardis.extra().getInsertedDisc().isEmpty()))) {
                    final float[] rgb = ClientTardisUtil.getPartyColors();

                    u = rgb[0];
                    t = rgb[1];
                    s = rgb[2];
                }

                boolean power = tardis.fuel().hasPower();
                boolean alarm = tardis.alarm().isEnabled();

                float red = power ? s : 0;
                float green = power ? alarm ? 0.3f : t : 0;
                float blue = power ? alarm ? 0.3f : u:  0;

                frame.renderWithAnimations(tardis, door, frame.getPart(), stack, botiProvider.getBuffer((DependencyChecker.hasIris() ? AITRenderLayers.tardisEmissiveCullZOffset(variant.emission(), true) : AITRenderLayers.getText(variant.emission()))), 0xf000f0, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0F, tickDelta);
                botiProvider.draw();
            }
            stack.pop();
        }

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