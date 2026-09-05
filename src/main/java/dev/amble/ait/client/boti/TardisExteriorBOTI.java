package dev.amble.ait.client.boti;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.amble.lib.data.DirectedGlobalPos;
import net.minecraft.client.render.*;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;

import dev.amble.ait.AITMod;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.data.DirectedBlockPos;
import dev.loqor.portal.Portals;
import dev.loqor.portal.client.PortalData;
import dev.loqor.portal.client.PortalDataManager;
import dev.loqor.portal.client.WorldGeometryRenderer;

import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.models.boti.BotiPortalModel;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.client.models.exteriors.ExteriorModel;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.SkyboxUtil;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.handler.BiomeHandler;
import dev.amble.ait.core.tardis.handler.StatsHandler;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.registry.impl.exterior.ClientExteriorVariantRegistry;

public class TardisExteriorBOTI extends BOTI {
    public void renderExteriorBoti(ExteriorBlockEntity exterior, ClientExteriorVariantSchema variant, MatrixStack stack, Identifier frameTex, ExteriorModel frame, ModelPart mask, int light) {
        if (MinecraftClient.getInstance().world == null
                || MinecraftClient.getInstance().player == null) return;

        if (!exterior.isLinked())
            return;

        ClientTardis tardis = exterior.tardis().get().asClient();

        // Cache this exterior BE for the exterior gbuffer-injection: it runs at AFTER_ENTITIES (before this
        // END-phase call), and EXTERIOR_RENDER_QUEUE is empty there under Sodium, so the injection reuses last
        // frame's exterior from here to stamp the aperture (mirror of TardisDoorBOTI's LAST_RENDERED_DOOR).
        BOTI.LAST_RENDERED_EXTERIOR.put(tardis.getUuid(), exterior);

        stack.push();

        // Split into framebuffer work and geometry work. A single zone around the whole portal cannot
        // tell "the blits are expensive" from "drawing the interior is expensive", which is the only
        // thing worth knowing here.
        Profiler profiler = client.getProfiler();
        profiler.push("ait:boti_ext_fbo_setup");
        profiler.visit("ait_boti_ext_portals");

        client.getFramebuffer().endWrite();
        BOTI.BotiCompositeState composite = BOTI.beginBotiComposite();
        int winW = composite.viewport[2];
        int winH = composite.viewport[3];

        BOTI_HANDLER.setupFramebuffer();

        Vec3d skyColor = MinecraftClient.getInstance().world.getSkyColor(MinecraftClient.getInstance().player.getPos(), MinecraftClient.getInstance().getTickDelta());
        if (AITModClient.CONFIG.greenScreenBOTI)
            BOTI.setFramebufferColor(BOTI_HANDLER.afbo, 0, 1, 0, 1);
        else
            BOTI.setFramebufferColor(BOTI_HANDLER.afbo, (float) skyColor.x, (float) skyColor.y, (float) skyColor.z, 1);

        BOTI.copyFramebufferFromFbo(composite.drawFbo, winW, winH, BOTI_HANDLER.afbo);
        profiler.visit("ait_boti_blit");

        profiler.swap("ait:boti_ext_mask");

        VertexConsumerProvider.Immediate botiProvider = AIT_BUF_BUILDER_STORAGE.getBotiVertexConsumer();

        BOTI.resetStencilByDraw();
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

        RenderSystem.depthMask(true);
        stack.push();
        StatsHandler stats = tardis.stats();
        String name = stats.getName();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        Vector3f scale = tardis.travel().getScale();
        if (name.equalsIgnoreCase("grumm") || name.equalsIgnoreCase("dinnerbone")) {
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));
            stack.translate(0, scale.y() + 0.25f, scale.z() - 1.7f);
        }
        ExteriorVariantSchema parent = variant.parent();

        Vec3d vec = parent.getPortalPosition();
        if (vec == null) vec = Vec3d.ZERO;

        stack.translate(vec.x, -vec.y - parent.portalHeight() / 2f, vec.z);
        stack.scale((float) parent.portalWidth() * scale.x(),
                (float) parent.portalHeight() * scale.y(), scale.z());
        RenderLayer whichOne = RenderLayer.getDebugFilledBox();
        float[] colorsForGreenScreen = AITModClient.CONFIG.greenScreenBOTI ? new float[]{0, 1, 0, 1} : new float[] {0f, 0f, 0f};
        mask.render(stack, botiProvider.getBuffer(whichOne), light, OverlayTexture.DEFAULT_UV, colorsForGreenScreen[0], colorsForGreenScreen[1], colorsForGreenScreen[2], 1);
        botiProvider.draw();
        profiler.visit("ait_boti_draw_flush");
        stack.pop();

        profiler.swap("ait:boti_ext_fbo_depth");
        BOTI.copyDepthToFbo(BOTI_HANDLER.afbo, composite.drawFbo, winW, winH);
        profiler.visit("ait_boti_blit");

        BOTI_HANDLER.afbo.beginWrite(false);
        BOTI.resetDepthByDraw();

        profiler.swap("ait:boti_ext_doors");

        GL11.glStencilMask(0x00);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);

        // ===== RENDER THE LIVE INTERIOR THROUGH THE EXTERIOR DOORWAY =====
        // The server streams the interior (around its door) into a shadow world under the derived interior id. Map
        // the player's eye - taken relative to the exterior door - through into the interior door's frame (the
        // mirror of TardisDoorBOTI's interior->exterior mapping) and draw that shadow world into the masked opening.
        // If the stream hasn't arrived yet, the doorway keeps the mask fill drawn above as a graceful fallback.
        PortalData interior = PortalDataManager.get(Portals.interiorId(tardis.getUuid()));
        if (interior != null && interior.world() != null && tardis.getDesktop() != null) {
            try {
                WorldGeometryRenderer geometry = interior.geometry();

                DirectedBlockPos interiorDoor = tardis.getDesktop().getDoorPos();
                BlockPos interiorDoorPos = interiorDoor.getPos();
                Direction interiorFacing = interiorDoor.toMinecraftDirection().getOpposite();
                geometry.setDoorFacing(interiorFacing);

                CachedDirectedGlobalPos exteriorPos = tardis.travel().position();
                Direction exteriorFacing = Direction.fromRotation(exteriorPos.getRotationDegrees()).getOpposite();
                Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();

                // Reverse of the interior->exterior turn: looking into the exterior door looks out the interior door.
                float deltaYaw = interiorFacing.asRotation() - (tardis.travel().position().getRotationDegrees());

                BlockPos extBlock = exteriorPos.getPos();
                Vec3d exteriorDoorCenter = new Vec3d(extBlock.getX() + 0.5, extBlock.getY() + 1.0, extBlock.getZ() + 0.5);
                Vec3d rel = camera.getPos().subtract(exteriorDoorCenter);

                double rad = Math.toRadians(deltaYaw);
                double cos = Math.cos(rad);
                double sin = Math.sin(rad);
                Vec3d relRotated = new Vec3d(rel.x * cos - rel.z * sin, rel.y, rel.x * sin + rel.z * cos);
                Vec3d eyeRelToCenter = new Vec3d(0.5, 1.0, 0.5).add(relRotated);

                float portalYaw = camera.getYaw() + deltaYaw;
                float portalPitch = camera.getPitch();

                // Tell SkyboxMixin which TARDIS's interior sky to draw for this pass. Without it, getCurrentTardis()
                // is null (the viewer is outside, in no TARDIS) and the interior would render the dimension's blank
                // vanilla sky instead of the TARDIS's chosen skybox. Cleared in the finally so nothing else uses it.
                SkyboxUtil.PORTAL_SKY_TARDIS = tardis;
                try {
                    geometry.render(Portals.interiorId(tardis.getUuid()), interior.world(), interiorDoorPos,
                            eyeRelToCenter, portalYaw, portalPitch, MinecraftClient.getInstance().getTickDelta(), true);
                } finally {
                    SkyboxUtil.PORTAL_SKY_TARDIS = null;
                }
            } catch (Throwable t) {
                AITMod.LOGGER.error("Failed to render exterior BOTI interior", t);
            }
        }

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        if (name.equalsIgnoreCase("grumm") || name.equalsIgnoreCase("dinnerbone")) {
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));
            stack.translate(0, scale.y + 0.25f, scale.z -1.7f);
        }
        stack.scale(scale.x(), scale.y(), scale.z());

        frame.renderDoors(tardis, exterior, frame.getPart(), stack, botiProvider.getBuffer(AITRenderLayers.getBotiInterior(variant.texture())), light, OverlayTexture.DEFAULT_UV, 1, 1F, 1.0F, 1.0F, true);
        botiProvider.draw();
        profiler.visit("ait_boti_draw_flush");
        stack.pop();

        profiler.swap("ait:boti_ext_biome");

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        if (name.equalsIgnoreCase("grumm") || name.equalsIgnoreCase("dinnerbone")) {
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));
            stack.translate(0, scale.y() + 0.25f, scale.z() -1.7f);
        }
        stack.scale(scale.x(), scale.y(), scale.z());

        if (variant != ClientExteriorVariantRegistry.CORAL_GROWTH) {
            BiomeHandler handler = exterior.tardis().get().handler(TardisComponent.Id.BIOME);
            Identifier biomeTexture = handler.getBiomeKey().get(variant.overrides());
            if (biomeTexture != null)
                frame.renderDoors(tardis, exterior, frame.getPart(), stack,
                        botiProvider.getBuffer(AITRenderLayers.getEntityTranslucentCull(biomeTexture)),
                        light, OverlayTexture.DEFAULT_UV, 1, 1F, 1.0F, 1.0F, true);
        }
        botiProvider.draw();
        profiler.visit("ait_boti_draw_flush");
        stack.pop();

        profiler.swap("ait:boti_ext_emission");

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        if (name.equalsIgnoreCase("grumm") || name.equalsIgnoreCase("dinnerbone")) {
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));
            stack.translate(0, scale.y + 0.25f, scale.z -1.7f);
        }
        stack.scale(scale.x(), scale.y(), scale.z());
        if (variant.emission() != null) {
            float u;
            float t;
            float s;

            if ((stats.getName() != null && "partytardis".equals(stats.getName().toLowerCase()) || (!exterior.tardis().get().extra().getInsertedDisc().isEmpty()))) {
                int m = 25;
                int n = MinecraftClient.getInstance().player.age / m + MinecraftClient.getInstance().player.getId();
                int o = DyeColor.values().length;
                int p = n % o;
                int q = (n + 1) % o;
                float r = ((float) (MinecraftClient.getInstance().player.age % m)) / m;
                float[] fs = SheepEntity.getRgbColor(DyeColor.byId(p));
                float[] gs = SheepEntity.getRgbColor(DyeColor.byId(q));
                s = fs[0] * (1f - r) + gs[0] * r;
                t = fs[1] * (1f - r) + gs[1] * r;
                u = fs[2] * (1f - r) + gs[2] * r;
            } else {
                float[] hs = new float[]{1.0f, 1.0f, 1.0f};
                s = hs[0];
                t = hs[1];
                u = hs[2];
            }

            boolean power = tardis.fuel().hasPower();
            boolean alarms = tardis.alarm().isEnabled();

            float red = power ? s : alarms ? 0.3f : 0;
            float green = power ? alarms ? 0.3f : t : 0;
            float blue = power ? alarms ? 0.3f : u : 0;

            frame.renderDoors(tardis, exterior, frame.getPart(), stack, botiProvider.getBuffer(AITRenderLayers.tardisEmissiveCullZOffset(variant.emission())), LightmapTextureManager.MAX_LIGHT_COORDINATE,
                    OverlayTexture.DEFAULT_UV, red, green, blue, 1, true);
            botiProvider.draw();
            profiler.visit("ait_boti_draw_flush");
        }
        stack.pop();

        profiler.swap("ait:boti_ext_fbo_resolve");

        client.getFramebuffer().beginWrite(true);
        // Under a shaderpack the Phase A afbo->screen blit is suppressed: ExteriorGbufferInjection re-draws the
        // interior into Iris's live gbuffer at AFTER_ENTITIES so the pack shades it, instead of compositing this
        // unshaded afbo over the deferred output. We still run everything above so geometry.render() bakes the VBOs
        // and populates its portal-view cache (which the injection reuses next frame); only the blit is skipped.
        if (!DependencyChecker.isIrisShaderPackInUse())
            BOTI.copyColorToFbo(BOTI_HANDLER.afbo, composite.drawFbo, winW, winH);
        BOTI.endBotiComposite(composite);

        stack.pop();
    }

    /**
     * Stamps the exterior doorway portal quad into the currently-bound framebuffer's stencil buffer, leaving
     * stencil=1 inside the aperture. The mirror of {@link TardisDoorBOTI#drawDoorApertureMask} for the outside-in
     * direction: it uses the exterior variant's own portal position/width/height (not the door's). The caller sets
     * up the outer door-block transform and the stencil write state ({@code glStencilFunc(GL_ALWAYS,1,0xFF)},
     * {@code glStencilOp(GL_KEEP,GL_KEEP,GL_REPLACE)}) before calling. Color/depth masks are suppressed internally
     * and restored on return.
     */
    public static void drawExteriorApertureMask(ClientTardis tardis, ClientExteriorVariantSchema variant, MatrixStack stack) {
        drawExteriorApertureMask(tardis, variant, stack, false);
    }

    /**
     * As above, but when {@code writeDepth} is true the mask writes the door-plane DEPTH (colour suppressed,
     * {@code depthFunc(ALWAYS)}) instead of the stencil. The injection uses this after drawing the interior to
     * replace the portal-space depth in the aperture with the exterior door's real main-scene depth, so main-world
     * entities/block-entities/particles drawn afterward occlude the portal correctly (things in front of the door
     * draw over it; glass behind it stays occluded).
     */
    public static void drawExteriorApertureMask(ClientTardis tardis, ClientExteriorVariantSchema variant, MatrixStack stack, boolean writeDepth) {
        ExteriorVariantSchema parent = variant.parent();
        Vector3f scale = tardis.travel().getScale();

        Vec3d vec = parent.getPortalPosition();
        if (vec == null) vec = Vec3d.ZERO;
        BOTI.copyColor(BOTI_HANDLER.afbo, client.getFramebuffer());

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
}
