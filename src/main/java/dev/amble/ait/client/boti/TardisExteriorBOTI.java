package dev.amble.ait.client.boti;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.loqor.client.WorldGeometryRenderer;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.models.exteriors.ExteriorModel;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.blocks.DoorBlock;
import dev.amble.ait.core.tardis.handler.BiomeHandler;
import dev.amble.ait.core.tardis.handler.StatsHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.registry.impl.exterior.ClientExteriorVariantRegistry;

public class TardisExteriorBOTI extends BOTI {
    // Static renderer instance - reused across all TARDIS exterior renders
    private static WorldGeometryRenderer exteriorRenderer;
    private static boolean rendererInitialized = false;

    /**
     * Initializes the exterior renderer if not already initialized
     */
    private static void initializeRenderer() {
        if (!rendererInitialized) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.getWindow() != null) {
                exteriorRenderer = new WorldGeometryRenderer(50); // Large render distance for exterior view

                float aspect = (float) client.getWindow().getFramebufferWidth() / (float) client.getWindow().getFramebufferHeight();
                // Perspective projection - adjust FOV/near/far as needed
                exteriorRenderer.setPerspectiveProjection(MinecraftClient.getInstance().options.getFov().getValue() * MinecraftClient.getInstance().player.getFovMultiplier(), aspect, 0.05f, 2000.0f);

                rendererInitialized = true;
            }
        }
    }

    public static void markDirty() {
        if (exteriorRenderer == null) return;
        exteriorRenderer.markDirty();
    }

    /**
     * Updates the renderer's aspect ratio when window size changes
     */
    private static void updateRendererProjection() {
        if (rendererInitialized && exteriorRenderer != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            float aspect = (float) client.getWindow().getFramebufferWidth() / (float) client.getWindow().getFramebufferHeight();
            exteriorRenderer.setPerspectiveProjection(MinecraftClient.getInstance().options.getFov().getValue() * MinecraftClient.getInstance().player.getFovMultiplier(), aspect, 0.05f, 2000.0f);
        }
    }

    /**
     * Cleans up the renderer - call when mod unloads
     */
    public static void cleanup() {
        if (exteriorRenderer != null) {
            exteriorRenderer.close();
            exteriorRenderer = null;
            rendererInitialized = false;
        }
    }

    public void renderExteriorBoti(ExteriorBlockEntity exterior, ClientExteriorVariantSchema variant, MatrixStack stack, Identifier frameTex, ExteriorModel frame, ModelPart mask, int light, float tickDelta) {
        if (MinecraftClient.getInstance().world == null
                || MinecraftClient.getInstance().player == null) return;

        if (!exterior.isLinked())
            return;

        ClientTardis tardis = exterior.tardis().get().asClient();
        MinecraftClient client = MinecraftClient.getInstance();

        // Initialize renderer if needed
        initializeRenderer();
        updateRendererProjection();

        stack.push();

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
        String name = stats.getName();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        Vector3f scale = tardis.travel().getScale();
        if (name.equalsIgnoreCase("grumm") || name.equalsIgnoreCase("dinnerbone")) {
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));
            stack.translate(0, scale.y() + 0.25f, scale.z() - 1.7f);
        }
        ExteriorVariantSchema parent = variant.parent();
        stack.scale((float) parent.portalWidth() * scale.x(),
                (float) parent.portalHeight() * scale.y(), scale.z());
        Vec3d vec = parent.adjustPortalPos(new Vec3d(0, -0.4675f, 0), (byte) 0);
        stack.translate(vec.x, vec.y, vec.z);
        RenderLayer whichOne = AITModClient.CONFIG.greenScreenBOTI ?
                RenderLayer.getDebugFilledBox() : RenderLayer.getEndGateway();
        float[] colorsForGreenScreen = AITModClient.CONFIG.greenScreenBOTI ? new float[]{0, 1, 0, 1} : new float[] {(float) skyColor.x, (float) skyColor.y, (float) skyColor.z};
        mask.render(stack, botiProvider.getBuffer(whichOne), light, OverlayTexture.DEFAULT_UV, colorsForGreenScreen[0], colorsForGreenScreen[1], colorsForGreenScreen[2], 1);
        botiProvider.draw();
        stack.pop();

        copyDepth(BOTI_HANDLER.afbo, client.getFramebuffer());

        BOTI_HANDLER.afbo.beginWrite(false);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        GL11.glStencilMask(0x00);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);

        // ===== RENDER TARDIS INTERIOR HERE =====
        if (tardis.travel().getState() == TravelHandlerBase.State.LANDED && exteriorRenderer != null) {
            stack.push();

            // Get the exterior position (this is where we're viewing FROM - inside the TARDIS)
            BlockPos exteriorPos = exterior.getPos();
            
            if (exteriorPos != null) {
                // Get the TARDIS interior dimension key from the synced TARDIS data
                // This uses server-synced UUID, so it's safe on client
                RegistryKey<World> tardisDimension = tardis.interiorDimension();
                
                MatrixStack interiorMatrices = new MatrixStack();

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

                // Interior door center - use the door position inside the TARDIS
                // This should match the interior door position
                Vec3d interiorDoorCenter = new Vec3d(
                        exteriorPos.getX() + 1.0,
                        exteriorPos.getY(),  // Standing eye height
                        exteriorPos.getZ() + 1.0
                );

                // Calculate offset using stable camera position
                Vec3d offset = new Vec3d(
                        stableCameraPos.x - interiorDoorCenter.x,
                        stableCameraPos.y - interiorDoorCenter.y,
                        stableCameraPos.z - interiorDoorCenter.z
                );

                // Apply inverse bobbing rotations BEFORE camera rotations
                if (client.options.getBobView().getValue()) {
                    interiorMatrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) inverseBobbingRotations.x));
                    interiorMatrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) inverseBobbingRotations.z));
                }

                // Apply camera rotations
                interiorMatrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(cameraPitch));
                interiorMatrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(cameraYaw));

                // Translate by offset
                interiorMatrices.translate(offset.x, -offset.y, offset.z);

                // Render from interior position using cross-dimensional rendering
                try {
                    // Get door facing for frustum culling
                    Direction doorFacing = tardis.travel().facing();
                    exteriorRenderer.setDoorFacing(doorFacing);

                    // Render from the TARDIS INTERIOR dimension
                    // This shows the inside when looking from outside
                    exteriorRenderer.renderFromDimension(tardisDimension, exteriorPos, interiorMatrices, tickDelta);
                } catch (Exception e) {
                    // Silent fail
                }
            }

            stack.pop();
        }
        // ===== END TARDIS INTERIOR RENDERING =====

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        if (name.equalsIgnoreCase("grumm") || name.equalsIgnoreCase("dinnerbone")) {
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));
            stack.translate(0, scale.y + 0.25f, scale.z -1.7f);
        }
        stack.scale(scale.x(), scale.y(), scale.z());

        frame.renderDoors(tardis, exterior, frame.getPart(), stack, botiProvider.getBuffer(AITRenderLayers.getBotiInterior(variant.texture())), light, OverlayTexture.DEFAULT_UV, 1, 1F, 1.0F, 1.0F, true);
        botiProvider.draw();
        stack.pop();

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
        stack.pop();

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
                int n = client.player.age / m + client.player.getId();
                int o = DyeColor.values().length;
                int p = n % o;
                int q = (n + 1) % o;
                float r = ((float) (client.player.age % m)) / m;
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

            frame.renderDoors(tardis, exterior, frame.getPart(), stack, botiProvider.getBuffer(AITRenderLayers.tardisEmissiveCullZOffset(variant.emission(), true)), 0xf000f0,
                    OverlayTexture.DEFAULT_UV, red, green, blue, 1, true);
            botiProvider.draw();
        }
        stack.pop();

        client.getFramebuffer().beginWrite(true);

        BOTI.copyColor(BOTI_HANDLER.afbo, client.getFramebuffer());

        GL11.glDisable(GL11.GL_STENCIL_TEST);

        stack.pop();
    }
}
