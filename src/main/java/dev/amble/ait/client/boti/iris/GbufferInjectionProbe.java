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

        MatrixStack stack = ctx.matrixStack();

        try {
            if (stencilEnabled && door != null) {
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

                // Step 1: stamp stencil=1 in the doorway aperture.
                // Apply the same door-position transforms that doorBOTI/renderInteriorDoorBoti use, so the
                // aperture quad lands at the correct screen-space pixels in the live gbuffer.
                GL11.glEnable(GL11.GL_STENCIL_TEST);
                GL11.glStencilMask(0xFF);
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

                // Step 2a: punch a depth hole in the aperture. Without this the portal world is depth-occluded by
                // the blocks in/behind the doorway (their main-scene depth is nearer than the portal geometry's
                // portal-space depth), so it "doesn't render over the blocks behind the door". Clearing depth to
                // far only where stencil==1 lets the injected world draw over them; it then writes its own depth
                // for correct self-occlusion within the aperture.
                BOTI.clearDepthInStencilRegion();

                data.geometry().debugInjectTerrainIntoGbuffer();
                data.geometry().injectBlockEntitiesAndEntities(ctx.tickDelta());

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
            } else {
                // --- Unclipped fallback: stencil not enabled or no door entity found ---
                data.geometry().debugInjectTerrainIntoGbuffer();
                data.geometry().injectBlockEntitiesAndEntities(ctx.tickDelta());
                if (!loggedSuccess) {
                    String reason = !stencilEnabled ? "stencilEnabled=false (stencil clip unavailable)"
                            : "no door entity in queue (stencil clip skipped)";
                    AITMod.LOGGER.info("Phase B gbuffer-injection probe: drew UNCLIPPED interior terrain+BE+entities "
                            + "into the gbuffer at AFTER_ENTITIES ({})", reason);
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
}
