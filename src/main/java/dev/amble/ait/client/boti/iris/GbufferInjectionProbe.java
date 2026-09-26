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
import dev.amble.ait.client.util.ClientRenderPass;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.core.blockentities.DoorBlockEntity;
import dev.loqor.portal.client.PortalData;
import dev.loqor.portal.client.PortalDataManager;

public final class GbufferInjectionProbe {
    private static boolean loggedError = false;
    private static boolean loggedSuccess = false;
    private static boolean loggedStencilBits = false;
    private static long lastTimeDiagMs = 0;

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

        boolean stencilEnabled = AITRenderHelper.getIsStencilEnabled(
                MinecraftClient.getInstance().getFramebuffer());

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

        if (door != null && door.isRemoved()) {
            BOTI.LAST_RENDERED_DOOR.remove(tardis.getUuid());
            door = null;
        }
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

        refreshPortalView(tardis, door, data);

        long now = System.currentTimeMillis();
        if (now - lastTimeDiagMs > 1000 && data.world() != null) {
            lastTimeDiagMs = now;
            net.minecraft.client.world.ClientWorld viewerWorld = MinecraftClient.getInstance().world;
            AITMod.LOGGER.info("BOTI-TIME-DIAG shadowExterior timeOfDay={} (%24000={}) skyAngle={} | viewer={} timeOfDay={}",
                    data.world().getTimeOfDay(), data.world().getTimeOfDay() % 24000L,
                    data.world().getSkyAngle(1.0f),
                    viewerWorld != null ? viewerWorld.getRegistryKey().getValue() : "null",
                    viewerWorld != null ? viewerWorld.getTimeOfDay() % 24000L : -1);
        }

        MatrixStack stack = ctx.matrixStack();

        try {
            {
                boolean wasStencilEnabled = GL11.glIsEnabled(GL11.GL_STENCIL_TEST);
                int prevStencilFunc = GL11.glGetInteger(GL11.GL_STENCIL_FUNC);
                int prevStencilRef  = GL11.glGetInteger(GL11.GL_STENCIL_REF);
                int prevStencilMask = GL11.glGetInteger(GL11.GL_STENCIL_VALUE_MASK);
                int prevStencilWriteMask = GL11.glGetInteger(GL11.GL_STENCIL_WRITEMASK);
                int prevStencilFail = GL11.glGetInteger(GL11.GL_STENCIL_FAIL);
                int prevStencilZFail = GL11.glGetInteger(GL11.GL_STENCIL_PASS_DEPTH_FAIL);
                int prevStencilZPass = GL11.glGetInteger(GL11.GL_STENCIL_PASS_DEPTH_PASS);

                GL11.glEnable(GL11.GL_STENCIL_TEST);
                GL11.glStencilMask(0xFF);
                GL11.glClearStencil(0);
                GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

                GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
                GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

                Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
                BlockPos doorPos = door.getPos();
                stack.push();
                stack.translate(0.5, 0, 0.5);
                stack.translate(doorPos.getX() - camera.getPos().getX(),
                        doorPos.getY() - camera.getPos().getY(),
                        doorPos.getZ() - camera.getPos().getZ());
                stack.scale(1, -1, -1);
                stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(
                        door.getCachedState().get(DoorBlock.FACING).asRotation()));
                stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

                TardisDoorBOTI.drawDoorApertureMask(tardis, door, stack);
                stack.pop();

                GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
                GL11.glStencilMask(0x00);
                RenderSystem.colorMask(true, true, true, true);
                RenderSystem.depthMask(true);

                BOTI.clearDepthInStencilRegion();

                net.minecraft.util.math.Vec3d fog = data.geometry().exteriorFogColor();
                boolean skyPhase = dev.amble.ait.client.boti.iris.IrisPhase.setSky();
                try {
                    if (fog != null)
                        BOTI.fillColorInStencilRegion((float) fog.x, (float) fog.y, (float) fog.z);
                    else
                        BOTI.fillColorInStencilRegion(0.5f, 0.65f, 0.9f);

                    data.geometry().injectSky(tardis.getUuid(), data.world(), ctx.tickDelta());
                } finally {
                    if (skyPhase)
                        dev.amble.ait.client.boti.iris.IrisPhase.reset();
                }

                data.geometry().debugInjectTerrainIntoGbuffer();
                data.geometry().injectBlockEntitiesAndEntities(ctx.tickDelta());
                data.geometry().debugInjectTranslucentIntoGbuffer();

                BOTI.clearDepthInStencilRegion();

                MinecraftClient mc = MinecraftClient.getInstance();
                net.minecraft.util.math.Vec3d camPos = mc.gameRenderer.getCamera().getPos();
                net.minecraft.client.render.VertexConsumerProvider.Immediate doorImm =
                        BOTI.AIT_BUF_BUILDER_STORAGE.getBotiVertexConsumer();
                boolean doorPhase = dev.amble.ait.client.boti.iris.IrisPhase.setBlockEntities();
                stack.push();
                stack.translate(door.getPos().getX() - camPos.x,
                        door.getPos().getY() - camPos.y,
                        door.getPos().getZ() - camPos.z);
                ClientRenderPass.suspend();
                try {
                    mc.getBlockEntityRenderDispatcher().render(door, ctx.tickDelta(), stack, doorImm);
                    doorImm.draw();
                } finally {
                    ClientRenderPass.resume();
                    stack.pop();
                    if (doorPhase)
                        dev.amble.ait.client.boti.iris.IrisPhase.reset();
                }

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
        }
    }
}
