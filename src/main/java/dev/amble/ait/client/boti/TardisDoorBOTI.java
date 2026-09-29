package dev.amble.ait.client.boti;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.DoorBlock;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.data.DirectedGlobalPos;
import dev.loqor.portal.client.PortalData;
import dev.loqor.portal.client.PortalDataManager;
import dev.loqor.portal.client.WorldGeometryRenderer;
import net.minecraft.client.render.*;
import net.minecraft.util.math.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.models.AnimatedModel;
import dev.amble.ait.client.models.boti.BotiPortalModel;
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
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class TardisDoorBOTI extends BOTI {
    public static void drawDoorApertureMask(ClientTardis tardis, DoorBlockEntity door, MatrixStack stack) {
        drawDoorApertureMask(tardis, door, stack, false);
    }

    public static void drawDoorApertureMask(ClientTardis tardis, DoorBlockEntity door, MatrixStack stack, boolean writeDepth) {
        ClientExteriorVariantSchema variant = tardis.getExterior().getVariant().getClient();
        ExteriorVariantSchema parent = variant.parent();
        Vector3f scale = tardis.travel().getScale();

        Vec3d vec = parent.door().getPortalPosition();
        if (vec == null) vec = Vec3d.ZERO;

        RenderSystem.colorMask(false, false, false, false);
        RenderSystem.depthMask(writeDepth);
        if (writeDepth) {
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(GL11.GL_ALWAYS);
        }

        VertexConsumerProvider.Immediate maskProvider = AIT_BUF_BUILDER_STORAGE.getBotiVertexConsumer();
        ModelPart maskPart = BotiPortalModel.getTexturedModelData().createModel();

        stack.push();
        stack.translate(vec.x, -vec.y - parent.portalHeight() / 2f, vec.z);
        stack.scale((float) parent.portalWidth() * scale.x(),
                (float) parent.portalHeight() * scale.y(), scale.z());
        maskPart.render(stack, maskProvider.getBuffer(RenderLayer.getDebugFilledBox()),
                0xf000f0, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
        maskProvider.draw();
        stack.pop();

        if (writeDepth)
            RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthMask(true);
    }

    public static void renderInteriorDoorBoti(ClientTardis tardis, DoorBlockEntity door, ClientExteriorVariantSchema variant, MatrixStack stack, Identifier frameTex, AnimatedModel frame, ModelPart mask, int light, float tickDelta) {
        ExteriorVariantSchema parent = variant.parent();

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        BOTI.LAST_RENDERED_DOOR.put(tardis.getUuid(), door);

        PortalData portalData = PortalDataManager.get(tardis.getUuid());
        boolean landed = tardis.travel().getState() == TravelHandlerBase.State.LANDED;

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

        BOTI.BotiCompositeState composite = BOTI.beginBotiComposite();
        int winW = composite.viewport[2];
        int winH = composite.viewport[3];

        BOTI_HANDLER.setupFramebuffer();

        Vec3d skyColor = new Vec3d(0.5d, 0.65d, 0.9d);
        if (landed && portalData != null && portalData.world() != null) {
            Vec3d exteriorFog = portalData.geometry().exteriorFogColor();
            if (exteriorFog != null) {
                skyColor = exteriorFog;
            } else {
                try {
                    skyColor = portalData.world().getSkyColor(Vec3d.of(tardis.travel().position().getPos()), tickDelta);
                } catch (Exception ignored) {
                }
            }
        }
        if (AITModClient.CONFIG.greenScreenBOTI)
            BOTI.setFramebufferColor(BOTI_HANDLER.afbo, 0, 1, 0, 1);
        else
            BOTI.setFramebufferColor(BOTI_HANDLER.afbo, (float) skyColor.x, (float) skyColor.y, (float) skyColor.z, 1);

        BOTI.copyFramebufferFromFbo(composite.drawFbo, winW, winH, BOTI_HANDLER.afbo);

        VertexConsumerProvider.Immediate botiProvider = AIT_BUF_BUILDER_STORAGE.getBotiVertexConsumer();

        BOTI.resetStencilByDraw();
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

        drawDoorApertureMask(tardis, door, stack);

        RenderSystem.depthMask(true);
        stack.push();
        StatsHandler stats = tardis.stats();
        Vector3f scale = tardis.travel().getScale();


        Vec3d vec = parent.door().getPortalPosition();
        if (vec == null) vec = Vec3d.ZERO;

        stack.translate(vec.x, -vec.y - parent.portalHeight() / 2f, vec.z);
        stack.scale((float) parent.portalWidth() * scale.x(),
                (float) parent.portalHeight() * scale.y(), scale.z());
        if (tardis.travel().getState() == TravelHandlerBase.State.LANDED) {
            RenderLayer whichOne = RenderLayer.getDebugFilledBox();
            float[] colorsForGreenScreen = AITModClient.CONFIG.greenScreenBOTI ?
                    new float[]{0, 1, 0, 1} :
                    new float[] {(float) skyColor.x, (float) skyColor.y, (float) skyColor.z};
            mask.render(stack, botiProvider.getBuffer(whichOne), 0xf000f0, OverlayTexture.DEFAULT_UV,
                    colorsForGreenScreen[0], colorsForGreenScreen[1], colorsForGreenScreen[2], 1);
        } else {
            mask.render(stack, botiProvider.getBuffer(RenderLayer.getEntityTranslucentCull(frameTex)),
                    0xf000f0, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        }
        botiProvider.draw();
        stack.pop();
        BOTI.copyDepthToFbo(BOTI_HANDLER.afbo, composite.drawFbo, winW, winH);

        BOTI_HANDLER.afbo.beginWrite(false);
        BOTI.resetDepthByDraw();

        GL11.glStencilMask(0x00);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);

        if (landed && portalData != null && portalData.world() != null) {
            WorldGeometryRenderer geometry = portalData.geometry();
            CachedDirectedGlobalPos exteriorPos = tardis.travel().position();
            BlockPos exteriorBlockPos = exteriorPos.getPos();

            try {
                float exteriorRotation = exteriorPos.getRotationDegrees();
                double normalRad = Math.toRadians(exteriorRotation);
                Vec3d doorNormal = new Vec3d(Math.sin(normalRad), 0.0, -Math.cos(normalRad));
                geometry.setDoorNormal(doorNormal);

                Camera camera = client.gameRenderer.getCamera();
                Direction interiorDoorFacing = door.getFacing().getOpposite();
                float deltaYaw = (exteriorRotation + 180f) - interiorDoorFacing.asRotation();

                Vec3d interiorDoorCenter = new Vec3d(door.getPos().getX() + 0.5, door.getPos().getY() + 1.0,
                        door.getPos().getZ() + 0.5);
                Vec3d rel = camera.getPos().subtract(interiorDoorCenter);

                double rad = Math.toRadians(deltaYaw);
                double cos = Math.cos(rad);
                double sin = Math.sin(rad);
                Vec3d relRotated = new Vec3d(rel.x * cos - rel.z * sin, rel.y, rel.x * sin + rel.z * cos);
                Vec3d eyeRelToCenter = new Vec3d(0.5, 1.0, 0.5).add(relRotated);

                float portalYaw = camera.getYaw() + deltaYaw;
                float portalPitch = camera.getPitch();

                geometry.render(tardis.getUuid(), portalData.world(), exteriorBlockPos, eyeRelToCenter,
                        portalYaw, portalPitch, tickDelta, true);
            } catch (Throwable t) {
                AITMod.LOGGER.error("Failed to render door BOTI interior", t);
            }
        }

        // Render vortex/effects when in flight
        stack.push();
        float delta = MinecraftClient.getInstance().getTickDelta() + MinecraftClient.getInstance().player.age;
        if (!tardis.travel().autopilot() && tardis.travel().getState() != TravelHandlerBase.State.LANDED)
            stack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((delta) * (tardis.travel().speed() * 0.7f)));
        if (!tardis.crash().isNormal())
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees((delta)));
        stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((delta) * (tardis.travel().speed() + 1)));
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        stack.translate(0, 0, 500);
        stack.scale(1.5f, 1.5f, 1.5f);
        VortexRender util = stats.getVortexEffects().toRender();
        if (!tardis.travel().isLanded() /*&& !tardis.flight().isFlying()*/) {
            util.setSpeed(tardis.travel().speed() < 1 ? 4 : tardis.travel().speed());
            util.render(stack);
        }
        botiProvider.draw();
        stack.pop();

        // Render door frame
        if (!tardis.getExterior().getCategory().equals(CategoryRegistry.GEOMETRIC)) {
            stack.push();
            stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            stack.scale(scale.x, scale.y, scale.z);

            frame.renderWithAnimations(tardis, door, frame.getPart(), stack,
                    botiProvider.getBuffer(AITRenderLayers.getBotiInterior(variant.texture())),
                    light, OverlayTexture.DEFAULT_UV, 1, 1F, 1.0F, 1.0F, tickDelta);
            botiProvider.draw();
            stack.pop();

            // Render emissive parts
            stack.push();
            stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            stack.scale(scale.x, scale.y, scale.z);
            if (variant.emission() != null) {
                float u = 1;
                float t = 1;
                float s = 1;

                if ((stats.getName() != null && "partytardis".equalsIgnoreCase(stats.getName())
                        || (!tardis.extra().getInsertedDisc().isEmpty()))) {
                    final float[] rgb = ClientTardisUtil.getPartyColors();
                    u = rgb[0];
                    t = rgb[1];
                    s = rgb[2];
                }

                boolean power = tardis.fuel().hasPower();
                boolean alarm = tardis.alarm().isEnabled();

                float red = power ? s : 0;
                float green = power ? alarm ? 0.3f : t : 0;
                float blue = power ? alarm ? 0.3f : u : 0;

                frame.renderWithAnimations(tardis, door, frame.getPart(), stack, botiProvider.getBuffer((DependencyChecker.hasIris() ? AITRenderLayers.tardisEmissiveCullZOffset(variant.emission()) : AITRenderLayers.getText(variant.emission()))), 0xf000f0, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0F, tickDelta);
                botiProvider.draw();
            }
            stack.pop();
        }

        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0x00);

        if (!DependencyChecker.isIrisShaderPackInUse())
            BOTI.copyColorToFbo(BOTI_HANDLER.afbo, composite.drawFbo, winW, winH);
        BOTI.endBotiComposite(composite);

        stack.pop();
    }
}