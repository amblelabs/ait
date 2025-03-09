package dev.amble.ait.client.boti;


import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.TardisComponent;
import dev.amble.ait.client.models.exteriors.ExteriorModel;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.BiomeHandler;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;
import dev.amble.ait.registry.impl.exterior.ClientExteriorVariantRegistry;


public class TardisExteriorBOTI extends BOTI {
    public void renderExteriorBoti(ExteriorBlockEntity exterior, ClientExteriorVariantSchema variant, MatrixStack stack, Identifier frameTex, SinglePartEntityModel frame, ModelPart mask, int light) {
        if (!AITMod.CONFIG.CLIENT.ENABLE_TARDIS_BOTI)
            return;

        if (MinecraftClient.getInstance().world == null
                || MinecraftClient.getInstance().player == null) return;

        Tardis tardis = exterior.tardis().get();

        if (tardis == null) return;

        stack.push();

        MinecraftClient.getInstance().getFramebuffer().endWrite();

        BOTI_HANDLER.setupFramebuffer();

        Vec3d skyColor = MinecraftClient.getInstance().world.getSkyColor(MinecraftClient.getInstance().player.getPos(), MinecraftClient.getInstance().getTickDelta());
        BOTI.setFramebufferColor(BOTI_HANDLER.afbo, (float) skyColor.x, (float) skyColor.y, (float) skyColor.z, 1);

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
        mask.render(stack, botiProvider.getBuffer(AITMod.CONFIG.CLIENT.SHOULD_RENDER_BOTI_INTERIOR ? RenderLayer.getDebugFilledBox() : RenderLayer.getEndGateway()), light, OverlayTexture.DEFAULT_UV, (float) skyColor.x, (float) skyColor.y, (float) skyColor.z, 1);
        botiProvider.draw();
        stack.pop();

        copyDepth(BOTI_HANDLER.afbo, MinecraftClient.getInstance().getFramebuffer());

        BOTI_HANDLER.afbo.beginWrite(false);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        GL11.glStencilMask(0x00);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

        ((ExteriorModel) frame).renderDoors(exterior, frame.getPart(), stack, botiProvider.getBuffer(AITRenderLayers.getBotiInterior(variant.texture())), light, OverlayTexture.DEFAULT_UV, 1, 1F, 1.0F, 1.0F, true);
        botiProvider.draw();
        stack.pop();

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

        if (variant != ClientExteriorVariantRegistry.CORAL_GROWTH) {
            BiomeHandler handler = exterior.tardis().get().handler(TardisComponent.Id.BIOME);
            Identifier biomeTexture = handler.getBiomeKey().get(variant.overrides());
            if (biomeTexture != null)
                ((ExteriorModel) frame).renderDoors(exterior, frame.getPart(), stack,
                        botiProvider.getBuffer(AITRenderLayers.getEntityCutoutNoCullZOffset(biomeTexture)),
                        light, OverlayTexture.DEFAULT_UV, 1, 1F, 1.0F, 1.0F, true);
        }
        botiProvider.draw();
        stack.pop();

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        if (variant.emission() != null)
            ((ExteriorModel) frame).renderDoors(exterior, frame.getPart(), stack, botiProvider.getBuffer(DependencyChecker.hasIris() ? AITRenderLayers.tardisEmissiveCullZOffset(variant.emission(), true) : AITRenderLayers.getBeaconBeam(variant.emission(), true)), 0xf000f0,
                    OverlayTexture.DEFAULT_UV, exterior.tardis().get().alarm().enabled().get() ?
                            !exterior.tardis().get().fuel().hasPower() ? 0.25f : 1f : 1f,
                    exterior.tardis().get().alarm().enabled().get() ? !exterior.tardis().get().fuel().hasPower() ? 0.01f : 0.3f : 1f,
                    exterior.tardis().get().alarm().enabled().get() ? !exterior.tardis().get().fuel().hasPower() ? 0.01f : 0.3f : 1f, 1f, true);
        botiProvider.draw();
        stack.pop();

        MinecraftClient.getInstance().getFramebuffer().beginWrite(true);

        BOTI.copyColor(BOTI_HANDLER.afbo, MinecraftClient.getInstance().getFramebuffer());

        GL11.glDisable(GL11.GL_STENCIL_TEST);

        stack.pop();
    }
}
