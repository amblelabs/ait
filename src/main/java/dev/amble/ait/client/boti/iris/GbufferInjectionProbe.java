package dev.amble.ait.client.boti.iris;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.DoorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.boti.AITRenderHelper;
import dev.amble.ait.client.boti.BOTI;
import dev.amble.ait.client.boti.TardisDoorBOTI;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.core.blockentities.DoorBlockEntity;
import dev.loqor.portal.client.PortalData;
import dev.loqor.portal.client.PortalDataManager;

/**
 * THROWAWAY gbuffer-injection probe. Fires at {@code WorldRenderEvents.AFTER_ENTITIES} - which runs while Iris's
 * gbuffer is bound and BEFORE its deferred pass (unlike AFTER_TRANSLUCENT, which is post-deferred/composite and
 * showed nothing). Draws the current TARDIS's baked interior terrain into the live gbuffer with Iris's terrain
 * phase set, so Iris's own deferred+composite should light it as part of the scene - with no double-composite,
 * because we are only adding draws to the existing opaque pass, not running a nested finalizeLevelRendering.
 *
 * <p>The terrain draw is clipped to the doorway aperture via the gbuffer's stencil buffer: the aperture mask
 * from {@link TardisDoorBOTI#drawDoorApertureMask} is stamped into stencil=1 first, then the terrain draw is
 * restricted to stencil==1 pixels only. If the gbuffer has no stencil bits (Iris may allocate none), the draw
 * falls back to unclipped and the probe logs {@code GL_STENCIL_BITS=0}.
 *
 * <p>Verdict to read in-game: does the interior terrain now appear ONLY inside the doorway, shaded by the pack,
 * with the main world NOT doubled? And what is GL_STENCIL_BITS?
 */
public final class GbufferInjectionProbe {
    private static boolean loggedError = false;
    private static boolean loggedSuccess = false;
    /** Logged exactly once: the stencil-bits count of Iris's gbuffer FBO. */
    private static boolean loggedStencilBits = false;

    private GbufferInjectionProbe() {}

    public static void run(WorldRenderContext ctx) {
        if (!DependencyChecker.isIrisShaderPackInUse())
            return;

        ClientTardis tardis = ClientTardisUtil.getCurrentTardis();
        if (tardis == null)
            return;

        PortalData data = PortalDataManager.get(tardis.getUuid());
        if (data == null || data.geometry() == null)
            return;

        // Determine stencil availability from the AIT framebuffer flag (avoids the invalid
        // GL11.glGetInteger(GL11.GL_STENCIL_BITS) query that emits GL_INVALID_ENUM in a core GL profile).
        boolean stencilEnabled = AITRenderHelper.getIsStencilEnabled(
                MinecraftClient.getInstance().getFramebuffer());

        // Log once so we know whether the clip is active. Query the ACTUAL stencil-attachment size of the bound
        // draw framebuffer with the core-profile-valid glGetFramebufferAttachmentParameteri (the earlier
        // GL_STENCIL_BITS query was invalid). This is the evidence that decides the clip bleed: if the AIT flag
        // says stencil is enabled but the bound FBO reports 0 stencil bits, then Iris owns/rebinds the target and
        // our WindowFramebuffer stencil attachment never reaches the FBO Iris draws into - so no clip is possible
        // via stencil and we need a different confinement.
        if (!loggedStencilBits) {
            int boundFbo = BOTI.currentDrawFbo();
            int fboStencilSize = org.lwjgl.opengl.GL30.glGetFramebufferAttachmentParameteri(
                    org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER,
                    org.lwjgl.opengl.GL30.GL_STENCIL_ATTACHMENT,
                    org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_ATTACHMENT_STENCIL_SIZE);
            AITMod.LOGGER.info("Phase B gbuffer stencil probe: aitFlagStencilEnabled={} boundFBO={} "
                    + "actualFboStencilBits={}", stencilEnabled, boundFbo, fboStencilSize);
            loggedStencilBits = true;
        }

        // Get the interior door. DOOR_RENDER_QUEUE is empty at AFTER_ENTITIES under Sodium (block entities render
        // after this event), so prefer the cache TardisDoorBOTI populates at END (one frame stale - fine, like the
        // portal-matrix cache). Fall back to a live queue scan in case it's populated in some setups.
        DoorBlockEntity door = BOTI.LAST_RENDERED_DOOR.get(tardis.getUuid());
        if (door == null) {
            for (DoorBlockEntity candidate : BOTI.DOOR_RENDER_QUEUE) {
                if (candidate != null && candidate.isLinked()
                        && tardis.getUuid().equals(candidate.tardis().get().getUuid())) {
                    door = candidate;
                    break;
                }
            }
        }

        // The injection needs a live door to define the aperture. If the cached door was destroyed, drop it and
        // don't inject - otherwise the portal lingers after the door is gone. Stencil clipping is also required now
        // that it's confirmed working (no unclipped fallback - that only ever splattered).
        if (door != null && door.isRemoved()) {
            BOTI.LAST_RENDERED_DOOR.remove(tardis.getUuid());
            door = null;
        }
        // Only inject when the door is actually open (matches doorBOTI's render condition). A closed opaque door has
        // no portal; without this the injection keeps drawing the (stale-cached) portal over the shut doors.
        if (door != null) {
            boolean doorOpen = tardis.door().getLeftRot() > 0
                    || tardis.getExterior().getVariant().getClient().hasTransparentDoors();
            if (!doorOpen) {
                BOTI.LAST_RENDERED_DOOR.remove(tardis.getUuid());
                door = null;
            }
        }
        if (door == null || !stencilEnabled)
            return;

        // Recompute the portal view from the CURRENT camera so the injected content matches this frame instead of
        // the 1-frame-stale view cached by the last END render (that lag is what smears the portal on camera turn).
        refreshPortalView(tardis, door, data);

        MatrixStack stack = ctx.matrixStack();

        try {
            {
                // --- Stencil-clipped injection path ---
                // --- Stencil-clipped injection path ---

                // Capture the GL stencil state so we can restore it fully afterward.
                boolean wasStencilEnabled = GL11.glIsEnabled(GL11.GL_STENCIL_TEST);
                int prevStencilFunc = GL11.glGetInteger(GL11.GL_STENCIL_FUNC);
                int prevStencilRef  = GL11.glGetInteger(GL11.GL_STENCIL_REF);
                int prevStencilMask = GL11.glGetInteger(GL11.GL_STENCIL_VALUE_MASK);
                int prevStencilWriteMask = GL11.glGetInteger(GL11.GL_STENCIL_WRITEMASK);
                int prevStencilFail = GL11.glGetInteger(GL11.GL_STENCIL_FAIL);
                int prevStencilZFail = GL11.glGetInteger(GL11.GL_STENCIL_PASS_DEPTH_FAIL);
                int prevStencilZPass = GL11.glGetInteger(GL11.GL_STENCIL_PASS_DEPTH_PASS);

                // Step 0: clear the stencil buffer to 0. We added this stencil attachment ourselves; MC/Iris don't
                // clear it each frame, so without this every frame's aperture stamp ACCUMULATES - the injection
                // (stencil==1) then draws at every past door position too, smearing the portal across the screen as
                // the door/camera moves. glClear honours the stencil write mask, so set it to 0xFF first.
                GL11.glEnable(GL11.GL_STENCIL_TEST);
                GL11.glStencilMask(0xFF);
                GL11.glClearStencil(0);
                GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

                // Step 1: stamp stencil=1 in the doorway aperture.
                // Apply the same door-position transforms that doorBOTI/renderInteriorDoorBoti use, so the
                // aperture quad lands at the correct screen-space pixels in the live gbuffer.
                GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
                GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

                Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
                BlockPos doorPos = door.getPos();
                stack.push();
                // Mirror doorBOTI's outer translate: door block to camera-relative space.
                stack.translate(0.5, 0, 0.5);
                stack.translate(doorPos.getX() - camera.getPos().getX(),
                        doorPos.getY() - camera.getPos().getY(),
                        doorPos.getZ() - camera.getPos().getZ());
                stack.scale(1, -1, -1);
                stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(
                        door.getCachedState().get(DoorBlock.FACING).asRotation()));
                // Mirror renderInteriorDoorBoti's first inner push.
                stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

                // drawDoorApertureMask suppresses colorMask/depthMask internally.
                TardisDoorBOTI.drawDoorApertureMask(tardis, door, stack);
                stack.pop();

                // Step 2: clip the terrain draw to stencil==1.
                GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
                GL11.glStencilMask(0x00);
                RenderSystem.colorMask(true, true, true, true);
                RenderSystem.depthMask(true);

                // Step 2a: punch a depth hole in the aperture so the portal draws OVER the blocks/room behind the
                // door (their main-scene depth would otherwise occlude the portal's unrelated portal-space depth).
                BOTI.clearDepthInStencilRegion();

                // Step 2a2: paint the exterior sky/fog colour as a backdrop, so aperture regions with no injected
                // terrain (the sky) show that colour instead of the interior scene behind them. Draw it in the SKY
                // phase so Iris treats it as (unlit) sky rather than lit gbuffer geometry - otherwise the deferred
                // pass blows the flat quad out to white.
                net.minecraft.util.math.Vec3d fog = data.geometry().exteriorFogColor();
                boolean skyPhase = dev.amble.ait.client.boti.iris.IrisPhase.setSky();
                try {
                    if (fog != null)
                        BOTI.fillColorInStencilRegion((float) fog.x, (float) fog.y, (float) fog.z);
                    else
                        BOTI.fillColorInStencilRegion(0.5f, 0.65f, 0.9f);
                } finally {
                    if (skyPhase)
                        dev.amble.ait.client.boti.iris.IrisPhase.reset();
                }

                // Step 2b: inject the portal world into the aperture (shaded by Iris via the terrain/entity phases).
                data.geometry().debugInjectTerrainIntoGbuffer();
                data.geometry().injectBlockEntitiesAndEntities(ctx.tickDelta());
                data.geometry().debugInjectTranslucentIntoGbuffer();

                // Step 2b2: re-clear the aperture depth so the door re-render below draws reliably ON TOP of the
                // portal - the portal's portal-space depth isn't comparable to the door's main-space depth, so a
                // plain depth test would let the portal win. Clearing to far first makes the door (LEQUAL) always win.
                BOTI.clearDepthInStencilRegion();

                // Step 2c: re-render the door on top - still stencil-clipped to the aperture, with normal depth
                // test/write - so the open door panels/frame occlude the portal and their pixels+depth (wiped by
                // the depth-clear and overdrawn by the injection) are restored. Using the block-entity dispatcher
                // renders the door with its own exact transform/animation; BLOCK_ENTITIES phase so Iris shades it.
                MinecraftClient mc = MinecraftClient.getInstance();
                net.minecraft.util.math.Vec3d camPos = mc.gameRenderer.getCamera().getPos();
                net.minecraft.client.render.VertexConsumerProvider.Immediate doorImm =
                        BOTI.AIT_BUF_BUILDER_STORAGE.getBotiVertexConsumer();
                boolean doorPhase = dev.amble.ait.client.boti.iris.IrisPhase.setBlockEntities();
                // Use the CONTEXT matrix stack (it carries the camera view rotation, same as the aperture mask
                // uses) - a fresh identity stack renders the door billboarded to the camera. Just translate to the
                // door's camera-relative position and let the dispatcher apply the door's own transform/animation.
                stack.push();
                stack.translate(door.getPos().getX() - camPos.x,
                        door.getPos().getY() - camPos.y,
                        door.getPos().getZ() - camPos.z);
                try {
                    mc.getBlockEntityRenderDispatcher().render(door, ctx.tickDelta(), stack, doorImm);
                    doorImm.draw();
                } finally {
                    stack.pop();
                    if (doorPhase)
                        dev.amble.ait.client.boti.iris.IrisPhase.reset();
                }

                // Step 2d: write the interior DOOR-PLANE depth into the aperture (replacing the injected portal-space
                // depth) so main-world geometry drawn after this event (block entities, particles, translucent glass/
                // water) occludes the portal correctly - things in front of the door draw over it, things behind stay
                // hidden - instead of the old NEAR flatten that made the portal cover everything in front of it.
                // Clear to far first so the door-plane depth writes regardless of the mask layer's own depth func.
                BOTI.clearDepthInStencilRegion();
                stack.push();
                stack.translate(0.5, 0, 0.5);
                stack.translate(door.getPos().getX() - camPos.x,
                        door.getPos().getY() - camPos.y,
                        door.getPos().getZ() - camPos.z);
                stack.scale(1, -1, -1);
                stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(
                        door.getCachedState().get(DoorBlock.FACING).asRotation()));
                stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                TardisDoorBOTI.drawDoorApertureMask(tardis, door, stack, true);
                stack.pop();

                // Step 3: fully restore stencil state.
                GL11.glStencilMask(0xFF);
                GL11.glStencilFunc(prevStencilFunc, prevStencilRef, prevStencilMask);
                GL11.glStencilOp(prevStencilFail, prevStencilZFail, prevStencilZPass);
                GL11.glStencilMask(prevStencilWriteMask);
                if (!wasStencilEnabled) GL11.glDisable(GL11.GL_STENCIL_TEST);

                if (!loggedSuccess) {
                    AITMod.LOGGER.info("Phase B gbuffer-injection probe: drew STENCIL-CLIPPED interior terrain+BE+entities "
                            + "into the gbuffer at AFTER_ENTITIES (stencilEnabled={}, door={})",
                            stencilEnabled, door.getPos());
                    loggedSuccess = true;
                }
            }
        } catch (Throwable t) {
            if (!loggedError) {
                AITMod.LOGGER.error("Phase B gbuffer-injection probe threw", t);
                loggedError = true;
            }
        }
    }

    /**
     * Recompute the portal view from the CURRENT camera and push it into the geometry renderer, so the injected
     * portal tracks the live camera instead of the 1-frame-stale view the END render cached. Mirrors the transform
     * TardisDoorBOTI.renderInteriorDoorBoti feeds to geometry.render(): map the eye through the interior door into
     * the exterior world (deltaYaw depends on the door facing and the exterior's real rotation).
     */
    private static void refreshPortalView(ClientTardis tardis, DoorBlockEntity door, PortalData data) {
        try {
            Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
            float exteriorRotation = tardis.travel().position().getRotationDegrees();
            net.minecraft.util.math.Direction interiorDoorFacing = door.getFacing().getOpposite();
            float deltaYaw = (exteriorRotation + 180f) - interiorDoorFacing.asRotation();

            net.minecraft.util.math.Vec3d interiorDoorCenter = new net.minecraft.util.math.Vec3d(
                    door.getPos().getX() + 0.5, door.getPos().getY() + 1.0, door.getPos().getZ() + 0.5);
            net.minecraft.util.math.Vec3d rel = camera.getPos().subtract(interiorDoorCenter);

            double rad = Math.toRadians(deltaYaw);
            double cos = Math.cos(rad), sin = Math.sin(rad);
            net.minecraft.util.math.Vec3d relRotated = new net.minecraft.util.math.Vec3d(
                    rel.x * cos - rel.z * sin, rel.y, rel.x * sin + rel.z * cos);
            net.minecraft.util.math.Vec3d eyeRelToCenter =
                    new net.minecraft.util.math.Vec3d(0.5, 1.0, 0.5).add(relRotated);

            data.geometry().updatePortalView(eyeRelToCenter, camera.getYaw() + deltaYaw, camera.getPitch());
        } catch (Throwable ignored) {
            // Fall back to the cached view rather than crashing the probe.
        }
    }
}
