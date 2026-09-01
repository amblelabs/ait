package dev.amble.ait.client.boti.iris;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.data.DirectedBlockPos;
import org.lwjgl.opengl.GL11;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.boti.AITRenderHelper;
import dev.amble.ait.client.boti.BOTI;
import dev.amble.ait.client.boti.TardisExteriorBOTI;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.SkyboxUtil;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.blocks.ExteriorBlock;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;
import dev.loqor.portal.Portals;
import dev.loqor.portal.client.PortalData;
import dev.loqor.portal.client.PortalDataManager;
import dev.loqor.portal.client.WorldGeometryRenderer;

/**
 * Outside-in gbuffer injection: the mirror of {@link GbufferInjectionProbe} for a viewer standing OUTSIDE looking
 * at a TARDIS. Fires at {@code WorldRenderEvents.AFTER_ENTITIES} (Iris's gbuffer bound, pre-deferred) and draws the
 * TARDIS's live interior shadow world ({@code Portals.interiorId(uuid)}) into the exterior doorway aperture, so the
 * shaderpack lights it as part of the scene - replacing the unshaded Phase A composite in {@link TardisExteriorBOTI}
 * (whose afbo->screen blit is suppressed under a shaderpack).
 *
 * <p>Unlike the interior probe (one TARDIS - the one you're inside), several exteriors can be visible at once, so
 * this iterates {@link BOTI#LAST_RENDERED_EXTERIOR} (populated by {@link TardisExteriorBOTI} at END, because
 * EXTERIOR_RENDER_QUEUE is empty at AFTER_ENTITIES under Sodium). Each entry is injected independently, clipped to
 * its own doorway aperture via the gbuffer stencil.
 */
public final class ExteriorGbufferInjection {
    private static boolean loggedError = false;

    private ExteriorGbufferInjection() {}

    public static void run(WorldRenderContext ctx) {
        if (!DependencyChecker.isIrisShaderPackInUse())
            return;
        if (AITModClient.skipBuiltInBOTI())
            return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null)
            return;

        boolean stencilEnabled = AITRenderHelper.getIsStencilEnabled(mc.getFramebuffer());
        if (!stencilEnabled)
            return;

        // Snapshot the cache so we can prune stale entries without a concurrent-modification hazard.
        List<Map.Entry<UUID, ExteriorBlockEntity>> entries =
                new ArrayList<>(BOTI.LAST_RENDERED_EXTERIOR.entrySet());
        for (Map.Entry<UUID, ExteriorBlockEntity> entry : entries) {
            ExteriorBlockEntity exterior = entry.getValue();
            if (exterior == null || exterior.isRemoved() || !exterior.isLinked()) {
                BOTI.LAST_RENDERED_EXTERIOR.remove(entry.getKey());
                continue;
            }
            try {
                injectOne(ctx, mc, exterior);
            } catch (Throwable t) {
                if (!loggedError) {
                    AITMod.LOGGER.error("Exterior gbuffer-injection threw", t);
                    loggedError = true;
                }
            }
        }
    }

    private static void injectOne(WorldRenderContext ctx, MinecraftClient mc, ExteriorBlockEntity exterior) {
        ClientTardis tardis = exterior.tardis().get().asClient();
        ClientExteriorVariantSchema variant = tardis.getExterior().getVariant().getClient();

        // Only inject when the door is actually open (matches exteriorBOTI's render condition). A shut opaque door
        // has no portal; without this the injection would keep drawing the interior over the closed exterior doors.
        boolean doorOpen = tardis.door().getLeftRot() > 0 || variant.hasTransparentDoors();
        if (!doorOpen)
            return;

        PortalData interior = PortalDataManager.get(Portals.interiorId(tardis.getUuid()));
        if (interior == null || interior.world() == null || interior.geometry() == null
                || tardis.getDesktop() == null)
            return;

        WorldGeometryRenderer geometry = interior.geometry();

        // Recompute the portal view from the CURRENT camera (fresh, so the injected interior tracks the live camera
        // instead of the 1-frame-stale view cached by the last END render - the reverse of the interior->exterior
        // mapping in TardisDoorBOTI, identical to the mapping in TardisExteriorBOTI.renderExteriorBoti).
        Camera camera = mc.gameRenderer.getCamera();
        DirectedBlockPos interiorDoor = tardis.getDesktop().getDoorPos();
        Direction interiorFacing = interiorDoor.toMinecraftDirection().getOpposite();
        geometry.setDoorFacing(interiorFacing);

        CachedDirectedGlobalPos exteriorPos = tardis.travel().position();
        float deltaYaw = interiorFacing.asRotation() - exteriorPos.getRotationDegrees();

        BlockPos extBlock = exteriorPos.getPos();
        Vec3d exteriorDoorCenter = new Vec3d(extBlock.getX() + 0.5, extBlock.getY() + 1.0, extBlock.getZ() + 0.5);
        Vec3d rel = camera.getPos().subtract(exteriorDoorCenter);
        double rad = Math.toRadians(deltaYaw);
        double cos = Math.cos(rad), sin = Math.sin(rad);
        Vec3d relRotated = new Vec3d(rel.x * cos - rel.z * sin, rel.y, rel.x * sin + rel.z * cos);
        Vec3d eyeRelToCenter = new Vec3d(0.5, 1.0, 0.5).add(relRotated);
        float portalYaw = camera.getYaw() + deltaYaw;
        float portalPitch = camera.getPitch();
        geometry.updatePortalView(eyeRelToCenter, portalYaw, portalPitch);

        MatrixStack stack = ctx.matrixStack();
        BlockPos pos = exterior.getPos();

        // Capture the GL stencil state so we can restore it fully afterward.
        boolean wasStencilEnabled = GL11.glIsEnabled(GL11.GL_STENCIL_TEST);
        int prevStencilFunc = GL11.glGetInteger(GL11.GL_STENCIL_FUNC);
        int prevStencilRef  = GL11.glGetInteger(GL11.GL_STENCIL_REF);
        int prevStencilMask = GL11.glGetInteger(GL11.GL_STENCIL_VALUE_MASK);
        int prevStencilWriteMask = GL11.glGetInteger(GL11.GL_STENCIL_WRITEMASK);
        int prevStencilFail = GL11.glGetInteger(GL11.GL_STENCIL_FAIL);
        int prevStencilZFail = GL11.glGetInteger(GL11.GL_STENCIL_PASS_DEPTH_FAIL);
        int prevStencilZPass = GL11.glGetInteger(GL11.GL_STENCIL_PASS_DEPTH_PASS);

        // Step 0: clear the stencil (we own this attachment; MC/Iris don't clear it, so stamps would otherwise
        // accumulate and smear across frames). Then stamp stencil=1 in the exterior doorway aperture.
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0xFF);
        GL11.glClearStencil(0);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

        // Build the exterior door-block transform (mirrors AITModClient.exteriorBOTI + renderExteriorBoti's outer
        // transforms), then hand off to the aperture-mask helper for the portal-quad stamp.
        Vec3d camPos = camera.getPos();
        stack.push();
        stack.translate(0.5, 0, 0.5);
        stack.translate(pos.getX() - camPos.x, pos.getY() - camPos.y, pos.getZ() - camPos.z);
        stack.scale(1, -1, -1);
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(
                RotationPropertyHelper.toDegrees(exterior.getCachedState().get(ExteriorBlock.ROTATION))));
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        // (grumm/dinnerbone flip is intentionally omitted here - rare, and only affects the mask orientation.)

        TardisExteriorBOTI.drawExteriorApertureMask(tardis, variant, stack);
        stack.pop(); // aperture stamp done - the passes below are matrix-independent (fullscreen quads / portalView).

        // Step 2: clip everything below to stencil==1.
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
        GL11.glStencilMask(0x00);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthMask(true);

        // Punch the aperture depth to far so the injected interior draws over the exterior scene behind the door.
        BOTI.clearDepthInStencilRegion();

        // Backdrop: the exterior dimension's sky/fog colour, drawn in the SKY phase so Iris doesn't blow the flat
        // quad out to white in the deferred pass. Fills the aperture regions with no injected terrain (e.g. the
        // interior's sky/gaps) so they don't show the exterior scene behind the portal.
        Vec3d fog = geometry.exteriorFogColor();
        boolean skyPhase = IrisPhase.setSky();
        try {
            if (fog != null)
                BOTI.fillColorInStencilRegion((float) fog.x, (float) fog.y, (float) fog.z);
            else
                BOTI.fillColorInStencilRegion(0.5f, 0.65f, 0.9f);
        } finally {
            if (skyPhase)
                IrisPhase.reset();
        }

        // Tell SkyboxMixin which TARDIS's interior sky to draw (the viewer is outside, so getCurrentTardis() is null).
        SkyboxUtil.PORTAL_SKY_TARDIS = tardis;
        try {
            // Inject the interior world into the aperture, shaded by Iris via the terrain/entity phases. No occluder
            // re-render is needed here (unlike the interior probe): the real ExteriorRenderer draws the box+doors
            // normally AFTER this event, so re-rendering the whole BE only painted its dark backing over the opening.
            geometry.debugInjectTerrainIntoGbuffer();
            geometry.injectBlockEntitiesAndEntities(ctx.tickDelta());
            geometry.debugInjectTranslucentIntoGbuffer();
        } finally {
            SkyboxUtil.PORTAL_SKY_TARDIS = null;
        }

        // Replace the injected portal-space depth in the aperture with the exterior DOOR-PLANE depth, so main-world
        // geometry drawn after this event (block entities, particles, the box+doors, translucent water/glass)
        // occludes the portal correctly: things in front of the door draw over it; things behind stay hidden.
        // (Previously the aperture was flattened to NEAR, which made the portal draw over everything in front of it.)
        // Clear the aperture depth to far first so the door-plane depth writes regardless of the mask layer's own
        // depth func (getDebugFilledBox may force LEQUAL, which then passes over the far value).
        BOTI.clearDepthInStencilRegion();
        stack.push();
        stack.translate(0.5, 0, 0.5);
        stack.translate(pos.getX() - camPos.x, pos.getY() - camPos.y, pos.getZ() - camPos.z);
        stack.scale(1, -1, -1);
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(
                RotationPropertyHelper.toDegrees(exterior.getCachedState().get(ExteriorBlock.ROTATION))));
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        TardisExteriorBOTI.drawExteriorApertureMask(tardis, variant, stack, true);
        stack.pop();

        // Fully restore stencil state.
        GL11.glStencilMask(0xFF);
        GL11.glStencilFunc(prevStencilFunc, prevStencilRef, prevStencilMask);
        GL11.glStencilOp(prevStencilFail, prevStencilZFail, prevStencilZPass);
        GL11.glStencilMask(prevStencilWriteMask);
        if (!wasStencilEnabled) GL11.glDisable(GL11.GL_STENCIL_TEST);
    }
}
