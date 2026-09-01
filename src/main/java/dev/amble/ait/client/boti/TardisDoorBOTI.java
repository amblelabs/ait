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

public class TardisDoorBOTI extends BOTI {
    // The geometry renderer now lives on each PortalData (the shadow world owns it), so multiple TARDIS doors -
    // and the new exterior->interior view - each bake/draw independently. Fetched per render via PortalDataManager.

    /**
     * Draws the door-portal quad into the currently-bound framebuffer's stencil buffer, leaving stencil=1
     * inside the doorway aperture. The caller is responsible for setting up the stencil write state
     * ({@code glStencilFunc(GL_ALWAYS,1,0xFF)}, {@code glStencilOp(GL_KEEP,GL_KEEP,GL_REPLACE)}) before
     * calling this helper. Color and depth masks are suppressed internally and restored on return.
     *
     * <p>The {@code door} parameter is accepted for future use (e.g. per-door offsets); the transform is
     * currently derived entirely from {@code tardis}.
     */
    public static void drawDoorApertureMask(ClientTardis tardis, DoorBlockEntity door, MatrixStack stack) {
        ClientExteriorVariantSchema variant = tardis.getExterior().getVariant().getClient();
        ExteriorVariantSchema parent = variant.parent();
        Vector3f scale = tardis.travel().getScale();

        Vec3d vec = parent.door().getPortalPosition();
        if (vec == null) vec = Vec3d.ZERO;

        // Suppress color and depth output — we only want to stamp stencil=1 in the doorway pixels.
        RenderSystem.colorMask(false, false, false, false);
        RenderSystem.depthMask(false);

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

        // Restore color/depth output for subsequent draws.
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthMask(true);
    }

    public static void renderInteriorDoorBoti(ClientTardis tardis, DoorBlockEntity door, ClientExteriorVariantSchema variant, MatrixStack stack, Identifier frameTex, AnimatedModel frame, ModelPart mask, int light, float tickDelta) {
        ExteriorVariantSchema parent = variant.parent();

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        PortalData portalData = PortalDataManager.get(tardis.getUuid());
        boolean landed = tardis.travel().getState() == TravelHandlerBase.State.LANDED;

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

        AITMod.LOGGER.info("BOTI-DIAG hasIris={} boundDrawFbo={} mainFbo={} win={}x{}",
                dev.amble.ait.compat.DependencyChecker.hasIris(),
                BOTI.currentDrawFbo(),
                client.getFramebuffer().fbo,
                client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight());
        BOTI.BotiCompositeState composite = BOTI.beginBotiComposite();
        int winW = composite.viewport[2];
        int winH = composite.viewport[3];

        BOTI_HANDLER.setupFramebuffer();

        // Tint the doorway background with the exterior dimension's FOG colour - the horizon colour the sky pass
        // fades the dome into, and the colour vanilla fills everything below the dome's rim with. Any other choice
        // (the zenith sky colour used previously) reads as a mismatched band around the horizon wherever the
        // background peeks out between the bottom of the sky and the terrain. The geometry renderer computes it
        // every frame, so reuse last frame's value; before the first portal frame fall back to the raw sky colour,
        // then to a daytime blue if the shadow world isn't ready at all.
        Vec3d skyColor = new Vec3d(0.5d, 0.65d, 0.9d);
        if (landed && portalData != null && portalData.world() != null) {
            Vec3d exteriorFog = portalData.geometry().exteriorFogColor();
            if (exteriorFog != null) {
                skyColor = exteriorFog;
            } else {
                try {
                    skyColor = portalData.world().getSkyColor(Vec3d.of(tardis.travel().position().getPos()), tickDelta);
                } catch (Exception ignored) {
                    // keep the fallback colour
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

        // Write stencil=1 into the doorway aperture using the extracted reusable helper.
        // (colorMask/depthMask are managed inside the helper; restored to true on return.)
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

        // ===== RENDER THE EXTERIOR WORLD THROUGH THE DOORWAY =====
        if (landed && portalData != null && portalData.world() != null) {
            WorldGeometryRenderer geometry = portalData.geometry();
            CachedDirectedGlobalPos exteriorPos = tardis.travel().position();
            BlockPos exteriorBlockPos = exteriorPos.getPos();

            try {
                // Use the exterior's REAL rotation, not a snapped cardinal. The exterior can sit at any of the fine
                // RotationPropertyHelper steps (22.5 deg each), so Direction.fromRotation(...) - which rounds to
                // N/S/E/W - threw the doorway view off by up to 45 deg for diagonally-placed boxes. The outward door
                // normal at yaw E is (sin E, 0, -cos E) (equals fromRotation(E).getOpposite().getVector() on the
                // cardinals); feed that exact normal to the cull so the baked volume matches the true door plane too.
                float exteriorRotation = exteriorPos.getRotationDegrees();
                double normalRad = Math.toRadians(exteriorRotation);
                Vec3d doorNormal = new Vec3d(Math.sin(normalRad), 0.0, -Math.cos(normalRad));
                geometry.setDoorNormal(doorNormal);

                // Map the player's eye through the interior doorway into the exterior world: the view rotates with
                // the door (so every facing - not just north - looks the right way out) and parallaxes as the
                // player moves. deltaYaw turns "looking into the interior door" into "looking out the exterior door".
                // It depends on BOTH the interior door's facing and the exterior's real rotation, exactly like the
                // inverse exterior->interior transform in TardisExteriorBOTI (this reduces to its negation). Using the
                // real (exteriorRotation + 180) instead of the snapped door direction is what makes the 8/16-way
                // rotations line up instead of collapsing to the nearest cardinal.
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
                // A doorway effect should never take the whole game down; the framebuffer/stencil teardown below
                // still runs, so the next frame recovers.
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

                frame.renderWithAnimations(tardis, door, frame.getPart(), stack,
                        botiProvider.getBuffer((DependencyChecker.hasIris() ?
                                AITRenderLayers.tardisEmissiveCullZOffset(variant.emission(), true) :
                                AITRenderLayers.getText(variant.emission()))),
                        0xf000f0, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0F, tickDelta);
                botiProvider.draw();
            }
            stack.pop();
        }

        // Disable stencil before blitting the composited interior back to the live target.
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0x00);

        // Blit afbo's colour into whatever framebuffer was live at entry (Iris's world target, or the
        // vanilla main FB), then rebind it and restore the GL state we captured.
        //
        // Under an Iris shaderpack the interior is drawn instead by the gbuffer-injection path
        // (GbufferInjectionProbe at AFTER_ENTITIES), which shades it via Iris's own deferred pass. Painting the
        // unshaded afbo composite over it here would produce the "two rendered parts" conflict. So skip only the
        // blit under a shaderpack - we still ran geometry.render() above (which meshes the volume and caches the
        // portal matrices the injection reuses) and still restore GL state below. The door frame itself is drawn,
        // shaded, by the normal DoorRenderer block-entity pass.
        if (!DependencyChecker.isIrisShaderPackInUse())
            BOTI.copyColorToFbo(BOTI_HANDLER.afbo, composite.drawFbo, winW, winH);
        BOTI.endBotiComposite(composite);

        stack.pop();
    }
}