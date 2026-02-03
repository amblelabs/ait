package dev.amble.ait.client.boti;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.amble.ait.core.blocks.DoorBlock;
import dev.amble.lib.data.DirectedGlobalPos;
import dev.loqor.portal.client.PortalDataManager;
import dev.loqor.portal.client.WorldGeometryRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

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
    /**
     * Marks all interior renderers as dirty (need rebuild)
     */
    public static void markDirty() {
        // Mark all interior renderers as dirty
        // The renderers are now managed per-TARDIS in the BOTI class
    }
    
    /**
     * Cleans up all renderers - call when disconnecting
     */
    public static void cleanup() {
        BOTI.cleanupAllRenderers();
    }

    /**
     * Calculates the inverse of view bobbing to stabilize the camera
     * Based on GameRenderer.bobView() logic
     */
    public static Vec3d calculateInverseBobbing(PlayerEntity player, float tickDelta) {
        float f = player.horizontalSpeed - player.prevHorizontalSpeed;
        float g = -(player.horizontalSpeed + f * tickDelta);
        float h = MathHelper.lerp(tickDelta, player.prevStrideDistance, player.strideDistance);

        // Calculate the inverse of the bobbing translation
        float bobbingX = MathHelper.sin(g * (float)Math.PI) * h * 0.5F;
        float bobbingY = -Math.abs(MathHelper.cos(g * (float)Math.PI) * h);
        float bobbingZ = 0.0F;

        return new Vec3d(-bobbingX, -bobbingY, -bobbingZ);
    }

    /**
     * Calculates the inverse of view bobbing rotations
     * Based on GameRenderer.bobView() logic
     */
    private static Vec3d calculateInverseBobbingRotations(PlayerEntity player, float tickDelta) {
        float f = player.horizontalSpeed - player.prevHorizontalSpeed;
        float g = -(player.horizontalSpeed + f * tickDelta);
        float h = MathHelper.lerp(tickDelta, player.prevStrideDistance, player.strideDistance);

        // Calculate the inverse of the bobbing rotations (Z roll, X pitch)
        float rollZ = MathHelper.sin(g * (float)Math.PI) * h * 3.0F;
        float pitchX = Math.abs(MathHelper.cos(g * (float)Math.PI - 0.2F) * h) * 5.0F;

        return new Vec3d(-pitchX, 0.0F, -rollZ);
    }

    public static void renderInteriorDoorBoti(ClientTardis tardis, DoorBlockEntity door, ClientExteriorVariantSchema variant, MatrixStack stack, Identifier frameTex, AnimatedModel frame, ModelPart mask, int light, float tickDelta) {
        ExteriorVariantSchema parent = variant.parent();

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        // Initialize renderer if needed
        /*initializeRenderer();
        updateRendererProjection();*/

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

        client.getFramebuffer().endWrite();

        BOTI_HANDLER.setupFramebuffer();

        Vec3d skyColor = new Vec3d(0.5d, 0.65d, 0.9d);//PortalDataManager.get().world().getSkyColor(client.player.getPos(), client.getTickDelta());
        if (AITModClient.CONFIG.greenScreenBOTI)
            BOTI.setFramebufferColor(BOTI_HANDLER.afbo, 0, 1, 0, 1);
        else
            BOTI.setFramebufferColor(BOTI_HANDLER.afbo, (float) skyColor.x, (float) skyColor.y, (float) skyColor.z, 1);

        BOTI.copyFramebuffer(client.getFramebuffer(), BOTI_HANDLER.afbo);

        VertexConsumerProvider.Immediate botiProvider = AIT_BUF_BUILDER_STORAGE.getBotiVertexConsumer();

        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0xFF);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

        RenderSystem.depthMask(true);
        stack.push();
        StatsHandler stats = tardis.stats();
        Vector3f scale = tardis.travel().getScale();

        stack.scale((float) parent.portalWidth() * scale.x(),
                (float) parent.portalHeight() * scale.y(), scale.z());
        Vec3d vec = parent.door().getPortalPosition().add(0, -0.548, 0);
        if (vec == null) return;

        stack.translate(vec.x, vec.y, vec.z);
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
        copyDepth(BOTI_HANDLER.afbo, client.getFramebuffer());

        BOTI_HANDLER.afbo.beginWrite(false);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        GL11.glStencilMask(0x00);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);

        // ===== RENDER TARDIS INTERIOR HERE =====
        if (tardis.travel().getState() == TravelHandlerBase.State.LANDED) {
            // Get or create renderer for this specific TARDIS
            WorldGeometryRenderer interiorRenderer = BOTI.getInteriorRenderer(tardis.getUuid());
            
            if (interiorRenderer != null) {
                stack.push();
                BlockPos interiorDoorPos = door.getPos();
                if (interiorDoorPos != null) {
                    MatrixStack interiorMatrices = new MatrixStack();

                    // Apply inverse view bobbing compensation if enabled
                    if (client.options.getBobView().getValue()) {
                        Vec3d inverseBobTranslation = calculateInverseBobbing(client.player, tickDelta);
                        Vec3d inverseBobRotation = calculateInverseBobbingRotations(client.player, tickDelta);
                        
                        // Apply rotations first (in reverse order)
                        interiorMatrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)inverseBobRotation.z));
                        interiorMatrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float)inverseBobRotation.x));
                        // Then translation
                        interiorMatrices.translate(inverseBobTranslation.x, inverseBobTranslation.y, inverseBobTranslation.z);
                    }

                    Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
                    float cameraPitch = client.gameRenderer.getCamera().getPitch();
                    float cameraYaw = client.gameRenderer.getCamera().getYaw();

                    DirectedGlobalPos exteriorPos = tardis.travel().position();

                    BlockPos exteriorBlockPos = exteriorPos.getPos();
                    float exteriorFacing = exteriorPos.getRotationDegrees() - 90;

                    Vec3d offset = new Vec3d(
                            cameraPos.x - interiorDoorPos.getX(),
                            cameraPos.y - interiorDoorPos.getY(),
                            cameraPos.z - interiorDoorPos.getZ()
                    );

                    interiorMatrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(cameraPitch));
                    interiorMatrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(cameraYaw));

                    interiorMatrices.translate(offset.x, -offset.y, offset.z);
                    interiorMatrices.translate(-0.5, 0, -0.5);
                    interiorMatrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(exteriorFacing));
                    interiorMatrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(door.getFacing().asRotation() + (door.getFacing() == Direction.EAST ||
                            door.getFacing() == Direction.WEST ? -90 : 90))); // This is super jank but its working!!!!!!! - Loqor
                    interiorMatrices.translate(0.5, 0, 0.5);

                    try {
                        Direction doorFacing = Direction.fromRotation(exteriorFacing - 90);
                        interiorRenderer.setDoorFacing(doorFacing);

                        interiorMatrices.scale(-1, 1, -1);

                        interiorRenderer.render(client.world, exteriorBlockPos, interiorMatrices, tickDelta, true);
                    } catch (Exception e) {
                        // Silent fail
                    }
                }
                stack.pop();
            }
        }

        // Render vortex/effects when in flight
        stack.push();
        if (!tardis.travel().autopilot() && tardis.travel().getState() != TravelHandlerBase.State.LANDED)
            stack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float) client.player.age / ((float) 200 / tardis.travel().speed()) * 360f));
        if (!tardis.crash().isNormal())
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) client.player.age / 100 * 360f));
        stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) client.player.age / 100 * 360f));
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        stack.translate(0, 0, 500);
        stack.scale(1.5f, 1.5f, 1.5f);
        VortexRender util = stats.getVortexEffects().toRender();
        if (!tardis.travel().isLanded()) {
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

        // **NEW APPROACH: Disable stencil BEFORE switching framebuffers**
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0x00);

        // Switch to main framebuffer and copy color
        client.getFramebuffer().beginWrite(false);  // false = don't check for errors
        BOTI.copyColor(BOTI_HANDLER.afbo, client.getFramebuffer());

        // Reset all stencil state on main framebuffer
        GL11.glStencilMask(0xFF);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);

        // Ensure depth mask is enabled for normal rendering
        RenderSystem.depthMask(true);

        stack.pop();
    }
}