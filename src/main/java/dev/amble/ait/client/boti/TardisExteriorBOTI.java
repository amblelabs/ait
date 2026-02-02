package dev.amble.ait.client.boti;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.amble.lib.data.DirectedGlobalPos;
import dev.loqor.portal.client.WorldGeometryRenderer;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.models.exteriors.ExteriorModel;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.handler.BiomeHandler;
import dev.amble.ait.core.tardis.handler.StatsHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.registry.impl.exterior.ClientExteriorVariantRegistry;

public class TardisExteriorBOTI extends BOTI {
    /**
     * Marks all exterior renderers as dirty (need rebuild)
     */
    public static void markDirty() {
        // Mark all exterior renderers as dirty
        // The renderers are now managed per-TARDIS in the BOTI class
    }
    public void renderExteriorBoti(ExteriorBlockEntity exterior, ClientExteriorVariantSchema variant, MatrixStack stack, Identifier frameTex, ExteriorModel frame, ModelPart mask, int light) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        if (!exterior.isLinked())
            return;

        ClientTardis tardis = exterior.tardis().get().asClient();

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
        
        // Render stencil mask
        RenderLayer whichOne = RenderLayer.getDebugFilledBox();
        float[] colorsForGreenScreen = AITModClient.CONFIG.greenScreenBOTI ? 
                new float[]{0, 1, 0, 1} : 
                new float[] {(float) skyColor.x, (float) skyColor.y, (float) skyColor.z};
        mask.render(stack, botiProvider.getBuffer(whichOne), light, OverlayTexture.DEFAULT_UV, 
                colorsForGreenScreen[0], colorsForGreenScreen[1], colorsForGreenScreen[2], 1);
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
            WorldGeometryRenderer exteriorRenderer = BOTI.getExteriorRenderer(tardis.getUuid());
            
            if (exteriorRenderer != null) {
                stack.push();
                
                // Get interior door position - this is where we want to render from
                BlockPos interiorDoorPos = tardis.getDesktop().doorPos().getPos();
                
                if (interiorDoorPos != null) {
                    MatrixStack interiorMatrices = new MatrixStack();

                    // Get camera position and orientation
                    Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
                    float cameraPitch = client.gameRenderer.getCamera().getPitch();
                    float cameraYaw = client.gameRenderer.getCamera().getYaw();

                    // Get exterior position
                    DirectedGlobalPos exteriorPos = tardis.travel().position();
                    BlockPos exteriorBlockPos = exteriorPos.getPos();
                    float exteriorFacing = exteriorPos.getRotationDegrees() - 90;

                    // Calculate offset from camera to exterior door
                    Vec3d offset = new Vec3d(
                            cameraPos.x - exteriorBlockPos.getX(),
                            cameraPos.y - exteriorBlockPos.getY(),
                            cameraPos.z - exteriorBlockPos.getZ()
                    );

                    // Apply camera rotation
                    interiorMatrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(cameraPitch));
                    interiorMatrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(cameraYaw));

                    // Translate to exterior position, then rotate to align with interior door
                    interiorMatrices.translate(offset.x, -offset.y, offset.z);
                    interiorMatrices.translate(-0.5, 0, -0.5);
                    
                    // Rotate to match exterior facing (inverse transformation)
                    interiorMatrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-exteriorFacing));
                    
                    // Get interior door facing
                    Direction interiorDoorFacing = tardis.getDesktop().doorPos().getDirection();
                    interiorMatrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(
                        interiorDoorFacing.asRotation() + (interiorDoorFacing == Direction.EAST || 
                                interiorDoorFacing == Direction.WEST ? -90 : 90)
                    ));
                    
                    interiorMatrices.translate(0.5, 0, 0.5);

                    try {
                        // Set door facing for culling
                        Direction doorFacing = Direction.fromRotation(-exteriorFacing + 180);
                        exteriorRenderer.setDoorFacing(doorFacing);

                        interiorMatrices.scale(-1, 1, -1);

                        // Render the interior world at the interior door position
                        exteriorRenderer.render(client.world, interiorDoorPos, interiorMatrices, client.getTickDelta(), true);
                    } catch (Exception e) {
                        // Silent fail
                    }
                }
                stack.pop();
            }
        }

        GL11.glStencilMask(0x00);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);

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

            frame.renderDoors(tardis, exterior, frame.getPart(), stack, botiProvider.getBuffer(AITRenderLayers.tardisEmissiveCullZOffset(variant.emission(), true)), 0xf000f0,
                    OverlayTexture.DEFAULT_UV, red, green, blue, 1, true);
            botiProvider.draw();
        }
        stack.pop();

        MinecraftClient.getInstance().getFramebuffer().beginWrite(true);

        BOTI.copyColor(BOTI_HANDLER.afbo, MinecraftClient.getInstance().getFramebuffer());

        GL11.glDisable(GL11.GL_STENCIL_TEST);

        stack.pop();
    }
}
