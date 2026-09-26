package dev.amble.ait.client.boti.iris;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.data.DirectedBlockPos;
import org.lwjgl.opengl.GL11;

import org.joml.Vector3f;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.boti.AITRenderHelper;
import dev.amble.ait.client.boti.BOTI;
import dev.amble.ait.client.boti.TardisExteriorBOTI;
import dev.amble.ait.client.models.exteriors.ExteriorModel;
import dev.amble.ait.client.renderers.AITRenderLayers;
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

        boolean doorOpen = tardis.door().getLeftRot() > 0 || variant.hasTransparentDoors();
        if (!doorOpen)
            return;

        PortalData interior = PortalDataManager.get(Portals.interiorId(tardis.getUuid()));
        if (interior == null || interior.world() == null || interior.geometry() == null
                || tardis.getDesktop() == null)
            return;

        WorldGeometryRenderer geometry = interior.geometry();

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

        Vec3d camPos = camera.getPos();
        stack.push();
        stack.translate(0.5, 0, 0.5);
        stack.translate(pos.getX() - camPos.x, pos.getY() - camPos.y, pos.getZ() - camPos.z);
        stack.scale(1, -1, -1);
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(
                RotationPropertyHelper.toDegrees(exterior.getCachedState().get(ExteriorBlock.ROTATION))));
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

        TardisExteriorBOTI.drawExteriorApertureMask(tardis, variant, stack);
        stack.pop();

        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
        GL11.glStencilMask(0x00);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthMask(true);

        BOTI.clearDepthInStencilRegion();

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

        SkyboxUtil.PORTAL_SKY_TARDIS = tardis;
        try {
            boolean skyInjectPhase = IrisPhase.setSky();
            try {
                geometry.injectSky(Portals.interiorId(tardis.getUuid()), interior.world(), ctx.tickDelta());
            } finally {
                if (skyInjectPhase)
                    IrisPhase.reset();
            }

            geometry.debugInjectTerrainIntoGbuffer();
            geometry.injectBlockEntitiesAndEntities(ctx.tickDelta());
            geometry.debugInjectTranslucentIntoGbuffer();
        } finally {
            SkyboxUtil.PORTAL_SKY_TARDIS = null;
        }

        BOTI.clearDepthInStencilRegion();
        ExteriorModel model = variant.getCachedModel();
        int light = LightmapTextureManager.pack(
                mc.world.getLightLevel(LightType.BLOCK, pos), mc.world.getLightLevel(LightType.SKY, pos));
        Vector3f doorScale = tardis.travel().getScale();
        VertexConsumerProvider.Immediate doorImm = BOTI.AIT_BUF_BUILDER_STORAGE.getBotiVertexConsumer();
        boolean doorPhase = IrisPhase.setBlockEntities();
        stack.push();
        stack.translate(0.5, 0, 0.5);
        stack.translate(pos.getX() - camPos.x, pos.getY() - camPos.y, pos.getZ() - camPos.z);
        stack.scale(1, -1, -1);
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(
                RotationPropertyHelper.toDegrees(exterior.getCachedState().get(ExteriorBlock.ROTATION))));
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        stack.scale(doorScale.x(), doorScale.y(), doorScale.z());
        try {
            model.renderDoors(tardis, exterior, model.getPart(), stack,
                    doorImm.getBuffer(AITRenderLayers.getBotiInterior(variant.texture())),
                    light, OverlayTexture.DEFAULT_UV, 1, 1F, 1.0F, 1.0F, true);
            doorImm.draw();
        } finally {
            stack.pop();
            if (doorPhase)
                IrisPhase.reset();
        }

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

        GL11.glStencilMask(0xFF);
        GL11.glStencilFunc(prevStencilFunc, prevStencilRef, prevStencilMask);
        GL11.glStencilOp(prevStencilFail, prevStencilZFail, prevStencilZPass);
        GL11.glStencilMask(prevStencilWriteMask);
        if (!wasStencilEnabled) GL11.glDisable(GL11.GL_STENCIL_TEST);
    }
}
