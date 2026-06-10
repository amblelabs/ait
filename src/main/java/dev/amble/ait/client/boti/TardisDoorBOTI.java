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

public class TardisDoorBOTI extends BOTI {
    public static void renderInteriorDoorBoti(ClientTardis tardis, DoorBlockEntity door, ClientExteriorVariantSchema variant, MatrixStack stack, Identifier frameTex, AnimatedModel frame, ModelPart mask, int light, float tickDelta) {
        if (!AITModClient.CONFIG.enableTardisBOTI) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        // --- PASS 1: THE DEPTH SHIELD (MAIN FRAMEBUFFER) ---
        // Draw the door mask to the main world with color disabled.
        // This physically blocks clouds and weather from drawing over the portal space.
        RenderSystem.colorMask(false, false, false, false);
        RenderSystem.depthMask(true);

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        StatsHandler stats = tardis.stats();
        Vector3f scale = tardis.travel().getScale();
        ExteriorVariantSchema parent = variant.parent();

        stack.scale((float) parent.portalWidth() * scale.x(), (float) parent.portalHeight() * scale.y(), scale.z());
        Vec3d vec = parent.door().getPortalPosition();
        if (vec == null) vec = new Vec3d(0, 0, 0);
        stack.translate(vec.x, vec.y - 0.575f, vec.z);

        // Simple render of the mask to write to the main depth buffer
        mask.render(stack, AIT_BUF_BUILDER_STORAGE.getBotiVertexConsumer().getBuffer(RenderLayer.getEndGateway()), 0xf000f0, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        AIT_BUF_BUILDER_STORAGE.getBotiVertexConsumer().draw();
        stack.pop();

        RenderSystem.colorMask(true, true, true, true);

        // --- PASS 2: SETUP CUSTOM FRAMEBUFFER ---
        client.getFramebuffer().endWrite();
        BOTI_HANDLER.setupFramebuffer();
        BOTI.copyFramebuffer(client.getFramebuffer(), BOTI_HANDLER.afbo);
        BOTI_HANDLER.afbo.beginWrite(false);

        // Clear BOTH Depth and Stencil. This provides a clean slate for AMD cards.
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glEnable(GL11.GL_STENCIL_TEST);

        // --- PASS 3: STENCIL MASK (AFBO) ---
        // We mask the AFBO stencil without writing to the AFBO depth buffer.
        RenderSystem.colorMask(false, false, false, false);
        RenderSystem.depthMask(false);

        GL11.glStencilMask(0xFF);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        stack.scale((float) parent.portalWidth() * scale.x(), (float) parent.portalHeight() * scale.y(), scale.z());
        stack.translate(vec.x, vec.y - 0.575f, vec.z);

        // Render mask again for AFBO stencil
        VertexConsumerProvider.Immediate botiProvider = AIT_BUF_BUILDER_STORAGE.getBotiVertexConsumer();
        mask.render(stack, botiProvider.getBuffer(RenderLayer.getEntityTranslucentCull(frameTex)), 0xf000f0, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        botiProvider.draw();
        stack.pop();

        // --- PASS 4: VORTEX AND DOOR RENDERING ---
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthMask(true);
        GL11.glStencilMask(0x00);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);

        // RENDER VORTEX
        stack.push();
        float delta = client.getTickDelta() + client.player.age;
        if (!tardis.travel().autopilot() && tardis.travel().getState() != TravelHandlerBase.State.LANDED)
            stack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((delta) * (tardis.travel().speed() * 0.7f)));
        if (!tardis.crash().isNormal())
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees((delta)));
        stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((delta) * (tardis.travel().speed() + 1)));
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        stack.translate(0, 0, 500);
        stack.scale(1.5f, 1.5f, 1.5f);
        VortexRender util = stats.getVortexEffects().toRender();
        if (!tardis.travel().isLanded()) {
            util.setSpeed(tardis.travel().speed() < 1 ? 4 : tardis.travel().speed());
            util.render(stack);
        }
        botiProvider.draw();
        stack.pop();

        // RENDER DOOR
        if (!tardis.getExterior().getCategory().equals(CategoryRegistry.GEOMETRIC)) {
            stack.push();
            stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            stack.scale(scale.x, scale.y, scale.z);
            frame.renderWithAnimations(tardis, door, frame.getPart(), stack, botiProvider.getBuffer(AITRenderLayers.getBotiInterior(variant.texture())), light, OverlayTexture.DEFAULT_UV, 1, 1F, 1.0F, 1.0F, tickDelta);
            botiProvider.draw();
            stack.pop();

            // EMISSIVE
            stack.push();
            stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            stack.scale(scale.x, scale.y, scale.z);
            if (variant.emission() != null) {
                float u = 1, t = 1, s = 1;
                if ((stats.getName() != null && "partytardis".equalsIgnoreCase(stats.getName()) || (!tardis.extra().getInsertedDisc().isEmpty()))) {
                    final float[] rgb = ClientTardisUtil.getPartyColors();
                    u = rgb[0]; t = rgb[1]; s = rgb[2];
                }
                boolean power = tardis.fuel().hasPower();
                boolean alarm = tardis.alarm().isEnabled();
                float red = power ? s : 0;
                float green = power ? alarm ? 0.3f : t : 0;
                float blue = power ? alarm ? 0.3f : u : 0;
                frame.renderWithAnimations(tardis, door, frame.getPart(), stack, botiProvider.getBuffer((DependencyChecker.hasIris() ? AITRenderLayers.tardisEmissiveCullZOffset(variant.emission(), true) : AITRenderLayers.getText(variant.emission()))), 0xf000f0, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0F, tickDelta);
                botiProvider.draw();
            }
            stack.pop();
        }

        // --- PASS 5: COMPOSITE TO MAIN ---
        client.getFramebuffer().beginWrite(true);
        BOTI.copyColor(BOTI_HANDLER.afbo, client.getFramebuffer());

        // Cleanup
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0xFF);
        RenderSystem.depthMask(true);
    }
}
