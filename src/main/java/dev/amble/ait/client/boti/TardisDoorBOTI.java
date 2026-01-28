package dev.amble.ait.client.boti;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.amble.ait.core.blocks.DoorBlock;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.loqor.client.WorldGeometryRenderer;
import net.minecraft.client.render.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;

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
    // Static renderer instance - reused across all TARDIS door renders
    private static WorldGeometryRenderer interiorRenderer;
    private static boolean rendererInitialized = false;

    /**
     * Initializes the interior renderer if not already initialized
     */
    private static void initializeRenderer() {
        if (!rendererInitialized) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.getWindow() != null) {
                interiorRenderer = new WorldGeometryRenderer(50); // Large render distance for interior

                float aspect = (float) client.getWindow().getFramebufferWidth() / (float) client.getWindow().getFramebufferHeight();
                // Perspective projection - adjust FOV/near/far as needed
                interiorRenderer.setPerspectiveProjection(MinecraftClient.getInstance().options.getFov().getValue() * MinecraftClient.getInstance().player.getFovMultiplier(), aspect, 0.05f, 2000.0f);

                rendererInitialized = true;
            }
        }
    }

    public static void markDirty() {
        if (interiorRenderer == null) return;
        interiorRenderer.markDirty();
    }

    /**
     * Updates the renderer's aspect ratio when window size changes
     */
    private static void updateRendererProjection() {
        if (rendererInitialized && interiorRenderer != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            float aspect = (float) client.getWindow().getFramebufferWidth() / (float) client.getWindow().getFramebufferHeight();
            interiorRenderer.setPerspectiveProjection(MinecraftClient.getInstance().options.getFov().getValue() * MinecraftClient.getInstance().player.getFovMultiplier(), aspect, 0.05f, 2000.0f);
        }
    }

    /**
     * Cleans up the renderer - call when mod unloads
     */
    public static void cleanup() {
        if (interiorRenderer != null) {
            interiorRenderer.close();
            interiorRenderer = null;
            rendererInitialized = false;
        }
    }

    /**
     * Calculates the inverse of view bobbing to stabilize the camera
     * Based on GameRenderer.bobView() logic
     */
    private static Vec3d calculateInverseBobbing(PlayerEntity player, float tickDelta) {
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
        initializeRenderer();
        updateRendererProjection();

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

        client.getFramebuffer().endWrite();

        BOTI_HANDLER.setupFramebuffer();

        Vec3d skyColor = client.world.getSkyColor(client.player.getPos(), client.getTickDelta());
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
        Vec3d vec = parent.door().adjustPortalPos(new Vec3d(0, -0.55f, 0), Direction.NORTH);
        stack.translate(vec.x, vec.y, vec.z);
        if (tardis.travel().getState() == TravelHandlerBase.State.LANDED) {
            RenderLayer whichOne = RenderLayer.getDebugFilledBox();/*AITModClient.CONFIG.greenScreenBOTI ?
                    RenderLayer.getDebugFilledBox() : RenderLayer.getEndGateway();*/
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

        // ===== RENDER EXTERIOR WORLD HERE =====
        if (tardis.travel().getState() == TravelHandlerBase.State.LANDED && interiorRenderer != null) {
            stack.push();

            BlockPos interiorDoorPos = door.getPos();
            if (interiorDoorPos != null) {
                // Get the TARDIS exterior position and dimension (where the TARDIS is physically located)
                BlockPos exteriorPos = tardis.travel().position();
                RegistryKey<World> exteriorDimension = tardis.travel().dimensionKey();
                
                if (exteriorPos != null && exteriorDimension != null) {
                    MatrixStack exteriorMatrices = new MatrixStack();

                    // Get camera position and rotation
                    Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
                    float cameraPitch = client.gameRenderer.getCamera().getPitch();
                    float cameraYaw = client.gameRenderer.getCamera().getYaw();

                    // Start with camera position
                    Vec3d stableCameraPos = cameraPos;
                    Vec3d inverseBobbingRotations = Vec3d.ZERO;

                    // Calculate and SUBTRACT inverse bobbing if enabled (camera already has bobbing applied)
                    if (client.options.getBobView().getValue() && client.player != null) {
                        float f = client.player.horizontalSpeed - client.player.prevHorizontalSpeed;
                        float g = -(client.player.horizontalSpeed + f * tickDelta);
                        float h = MathHelper.lerp(tickDelta, client.player.prevStrideDistance, client.player.strideDistance);

                        // Calculate the bobbing that was applied to the camera
                        float bobbingX = MathHelper.sin(g * (float)Math.PI) * h * 0.5F;
                        float bobbingY = -Math.abs(MathHelper.cos(g * (float)Math.PI) * h);

                        // SUBTRACT the bobbing (don't add inverse, subtract the actual bobbing)
                        stableCameraPos = new Vec3d(
                                cameraPos.x - bobbingX,
                                cameraPos.y - bobbingY,
                                cameraPos.z
                        );

                        // Calculate inverse rotations
                        float rollZ = MathHelper.sin(g * (float)Math.PI) * h * 3.0F;
                        float pitchX = Math.abs(MathHelper.cos(g * (float)Math.PI - 0.2F) * h) * 5.0F;
                        inverseBobbingRotations = new Vec3d(-pitchX, 0.0F, -rollZ);
                    }

                    // Exterior door center (where the TARDIS is physically located)
                    Vec3d exteriorDoorCenter = new Vec3d(
                            exteriorPos.getX() + 0.5,
                            exteriorPos.getY() + 1.0,  // Standing eye height
                            exteriorPos.getZ() + 0.5
                    );

                    // Calculate offset using stable camera position
                    Vec3d offset = new Vec3d(
                            stableCameraPos.x - exteriorDoorCenter.x,
                            stableCameraPos.y - exteriorDoorCenter.y,
                            stableCameraPos.z - exteriorDoorCenter.z
                    );

                    // Apply inverse bobbing rotations BEFORE camera rotations
                    if (client.options.getBobView().getValue()) {
                        exteriorMatrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) inverseBobbingRotations.x));
                        exteriorMatrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) inverseBobbingRotations.z));
                    }

                    // Apply camera rotations
                    exteriorMatrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(cameraPitch));
                    exteriorMatrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(cameraYaw));

                    // Translate by offset
                    exteriorMatrices.translate(offset.x, -offset.y, offset.z);

                    // Render from exterior position using cross-dimensional rendering
                    try {
                        // Set door facing for frustum culling
                        Direction doorFacing = door.getCachedState().get(DoorBlock.FACING);
                        interiorRenderer.setDoorFacing(doorFacing);

                        // Render from the EXTERIOR dimension (where the TARDIS is physically located)
                        // This shows the outside world when looking through the door from inside
                        interiorRenderer.renderFromDimension(exteriorDimension, exteriorPos, exteriorMatrices, tickDelta);
                    } catch (Exception e) {
                        // Silent fail
                    }
                }
            }

            stack.pop();
        }
// ===== END EXTERIOR WORLD RENDERING =====



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

        client.getFramebuffer().beginWrite(true);

        BOTI.copyColor(BOTI_HANDLER.afbo, client.getFramebuffer());

        GL11.glDisable(GL11.GL_STENCIL_TEST);

        RenderSystem.depthMask(true);

        stack.pop();
    }
}