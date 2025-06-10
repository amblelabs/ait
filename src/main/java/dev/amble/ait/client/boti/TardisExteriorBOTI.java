package dev.amble.ait.client.boti;

import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.models.exteriors.ExteriorModel;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.BiomeHandler;
import dev.amble.ait.core.tardis.handler.StatsHandler;
import dev.amble.ait.core.tardis.util.network.c2s.BOTIChunkRequestC2SPacket;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;
import dev.amble.ait.registry.impl.exterior.ClientExteriorVariantRegistry;

public class TardisExteriorBOTI extends BOTI {
    private static final Logger LOGGER = LoggerFactory.getLogger(TardisExteriorBOTI.class);
    private float lastRenderTick = -1;
    private VertexBuffer BOTI_VBO;

    public TardisExteriorBOTI(Tardis tardis) {
        if (tardis != null) {
            this.renderChunkVBO(tardis);
        } else {
            LOGGER.warn("Tardis is null during TardisExteriorBOTI initialization");
        }
    }

    public void renderChunkVBO(Tardis tardis) {
        if (tardis == null || tardis.stats() == null || tardis.stats().posState == null || tardis.getDesktop() == null || tardis.getDesktop().getDoorPos() == null) {
            LOGGER.warn("Cannot render chunk VBO: tardis, stats, posState, or doorPos is null");
            return;
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexLightmapColorProgram);

        if (this.BOTI_VBO != null) {
            this.BOTI_VBO.close();
        }

        this.BOTI_VBO = new VertexBuffer(VertexBuffer.Usage.STATIC);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);

        Random random = Random.create(42L);
        tardis.stats().posState.forEach((pos, state) -> {
            if (behindDoor(tardis.getDesktop().getDoorPos().getPos(), pos, tardis.getDesktop().getDoorPos().toMinecraftDirection())) {
                return; // Skip blocks behind the door
            }
            for (Direction direction : Direction.values()) {
                List<BakedQuad> quads = MinecraftClient.getInstance().getBlockRenderManager().getModel(state).getQuads(state, direction, random);
                BOTIChunkVBO.addQuadsToBuffer(quads, buffer, pos.getX(), pos.getY(), pos.getZ());
            }
        });

        BufferBuilder.BuiltBuffer builtBuffer = buffer.end();
        this.BOTI_VBO.bind();
        this.BOTI_VBO.upload(builtBuffer);
        VertexBuffer.unbind();
    }

    public void renderExteriorBoti(ExteriorBlockEntity exterior, ClientExteriorVariantSchema variant, MatrixStack stack, Identifier frameTex, SinglePartEntityModel frame, ModelPart mask, int light) {
        if (!AITModClient.CONFIG.enableTardisBOTI || MinecraftClient.getInstance().world == null || MinecraftClient.getInstance().player == null || !exterior.isLinked()) {
            return;
        }

        ClientTardis tardis = exterior.tardis().get().asClient();
        if (tardis == null) {
            LOGGER.warn("ClientTardis is null for exterior at {}", exterior.getPos());
            return;
        }

        long currentTime = MinecraftClient.getInstance().world.getTime();
        if (exterior.lastRequestTime > currentTime) {
            exterior.lastRequestTime = 0; // Reset invalid lastRequestTime
        }
        if (currentTime - exterior.lastRequestTime >= 20) {
            updateChunkModel(exterior);
            exterior.lastRequestTime = currentTime;
        }

        float tickDelta = MinecraftClient.getInstance().getTickDelta();
        if (this.lastRenderTick == tickDelta) {
            return;
        }

        stack.push();
        try {
            if (BOTI_HANDLER.afbo == null) {
                LOGGER.error("Framebuffer (afbo) is null, cannot render BOTI");
                return;
            }

            // Save current OpenGL state
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableCull();

            BOTI_HANDLER.setupFramebuffer();
            Vec3d skyColor = MinecraftClient.getInstance().world.getSkyColor(MinecraftClient.getInstance().player.getPos(), tickDelta);
            BOTI.setFramebufferColor(BOTI_HANDLER.afbo, AITModClient.CONFIG.greenScreenBOTI ? 0 : (float) skyColor.x,
                    AITModClient.CONFIG.greenScreenBOTI ? 1 : (float) skyColor.y,
                    AITModClient.CONFIG.greenScreenBOTI ? 0 : (float) skyColor.z, 1);
            BOTI.copyFramebuffer(MinecraftClient.getInstance().getFramebuffer(), BOTI_HANDLER.afbo);

            VertexConsumerProvider.Immediate botiProvider = AIT_BUF_BUILDER_STORAGE.getBotiVertexConsumer();
            GL11.glEnable(GL11.GL_STENCIL_TEST);
            GL11.glStencilMask(0xFF);
            GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
            GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
            GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

            RenderSystem.depthMask(true);
            stack.push();
            Vec3d vec = variant.parent().adjustPortalPos(new Vec3d(0, -1.1725f, 0), (byte) 0);
            stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            stack.translate(vec.x, vec.y, vec.z);
            stack.scale((float) variant.parent().portalWidth(), (float) variant.parent().portalHeight(), 1f);
            mask.render(stack, botiProvider.getBuffer(AITModClient.CONFIG.shouldRenderBOTIInterior ? RenderLayer.getDebugFilledBox() : RenderLayer.getEndGateway()), light, OverlayTexture.DEFAULT_UV, (float) skyColor.x, (float) skyColor.y, (float) skyColor.z, 1);
            botiProvider.draw();
            stack.pop();

            BOTI.copyDepth(BOTI_HANDLER.afbo, MinecraftClient.getInstance().getFramebuffer());
            BOTI_HANDLER.afbo.beginWrite(false);
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

            GL11.glStencilMask(0x00);
            GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);

            if (AITModClient.CONFIG.shouldRenderBOTIInterior) {
                renderInterior(tardis, exterior, stack, botiProvider, light, tickDelta);
            }

            renderExteriorDoors(tardis, exterior, variant, frame, stack, botiProvider, light);
            MinecraftClient.getInstance().getFramebuffer().beginWrite(true);
            BOTI.copyColor(BOTI_HANDLER.afbo, MinecraftClient.getInstance().getFramebuffer());

        } finally {
            // Reset OpenGL state to prevent interference with other renderers
            GL11.glStencilMask(0xFF);
            GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
            GL11.glDisable(GL11.GL_STENCIL_TEST);
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableCull();
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // Clear main framebuffer
            stack.pop();
        }

        this.lastRenderTick = tickDelta;
    }

    private void renderInterior(ClientTardis tardis, ExteriorBlockEntity exterior, MatrixStack stack, VertexConsumerProvider.Immediate botiProvider, int light, float tickDelta) {
        StatsHandler stats = tardis.stats();
        BlockPos targetPos = stats.targetPos();
        BlockPos doorPos = tardis.getDesktop() != null && tardis.getDesktop().getDoorPos() != null ? tardis.getDesktop().getDoorPos().getPos() : null;
        Direction doorDirection = tardis.getDesktop() != null && tardis.getDesktop().getDoorPos() != null ? tardis.getDesktop().getDoorPos().toMinecraftDirection() : null;
        RegistryKey<World> targetWorld = stats.getTargetWorld();

        if (doorPos == null || targetPos == null || targetWorld == null) {
            LOGGER.warn("Cannot render interior: doorPos, targetPos, or targetWorld is null");
            updateChunkModel(exterior);
            return;
        }

        if (stats.botiChunkVBO == null || stats.posState == null) {
            LOGGER.warn("Cannot render interior: botiChunkVBO or posState is null");
            updateChunkModel(exterior);
            return;
        }

        if (!BOTIChunkVBO.shouldGenerateQuads && this.BOTI_VBO != null) {
            stack.push();
            try {
                stack.scale(1, -1, -1);
                stack.translate(14.5f, -4, -20.35F);
                this.BOTI_VBO.bind();
                RenderSystem.setShader(GameRenderer::getPositionTexLightmapColorProgram);
                RenderSystem.enableDepthTest();
                RenderSystem.depthMask(true);
                this.BOTI_VBO.draw(stack.peek().getPositionMatrix(), RenderSystem.getProjectionMatrix(), GameRenderer.getPositionTexLightmapColorProgram());
                VertexBuffer.unbind();
            } finally {
                stack.pop();
            }
        }

        for (Map.Entry<BlockPos, BlockEntity> entry : stats.blockEntities.entrySet()) {
            BlockPos offsetPos = entry.getKey();
            BlockEntity be = entry.getValue();
            BlockEntityRenderer<BlockEntity> renderer = MinecraftClient.getInstance().getBlockEntityRenderDispatcher().get(be);
            if (renderer != null) {
                stack.push();
                try {
                    stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90));
                    stack.translate(doorPos.getX() - offsetPos.getX(), doorPos.getY() - offsetPos.getY(), doorPos.getZ() - offsetPos.getZ());
                    renderer.render(be, tickDelta, stack, botiProvider, light, OverlayTexture.DEFAULT_UV);
                } catch (Exception e) {
                    LOGGER.error("Failed to render block entity {} at {}: {}", be, offsetPos, e.getMessage());
                } finally {
                    stack.pop();
                }
            } else {
                LOGGER.warn("No renderer found for block entity {} at {}", be, offsetPos);
            }
        }
        botiProvider.draw();
    }

    private void renderExteriorDoors(ClientTardis tardis, ExteriorBlockEntity exterior, ClientExteriorVariantSchema variant, SinglePartEntityModel frame, MatrixStack stack, VertexConsumerProvider.Immediate botiProvider, int light) {
        stack.push();
        try {
            stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0));
            ((ExteriorModel) frame).renderDoors(tardis, exterior, frame.getPart(), stack, botiProvider.getBuffer(AITRenderLayers.getBotiInterior(variant.texture())), light, OverlayTexture.DEFAULT_UV, 1, 1F, 1.0F, 1.0F, true);
            botiProvider.draw();
        } finally {
            stack.pop();
        }

        if (variant != ClientExteriorVariantRegistry.CORAL_GROWTH) {
            stack.push();
            try {
                stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0));
                BiomeHandler handler = exterior.tardis().get().handler(TardisComponent.Id.BIOME);
                Identifier biomeTexture = handler.getBiomeKey().get(variant.overrides());
                if (biomeTexture != null) {
                    ((ExteriorModel) frame).renderDoors(tardis, exterior, frame.getPart(), stack, botiProvider.getBuffer(AITRenderLayers.getEntityCutoutNoCullZOffset(biomeTexture)), light, OverlayTexture.DEFAULT_UV, 1, 1F, 1.0F, 1.0F, true);
                }
                botiProvider.draw();
            } finally {
                stack.pop();
            }
        }

        if (variant.emission() != null) {
            stack.push();
            try {
                stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0));
                RenderLayer layer = DependencyChecker.hasIris() ? AITRenderLayers.tardisEmissiveCullZOffset(variant.emission(), true) : AITRenderLayers.getBeaconBeam(variant.emission(), true);
                if (!DependencyChecker.hasIris()) {
                    LOGGER.warn("Iris dependency not found, using fallback beacon beam render layer");
                }
                ((ExteriorModel) frame).renderDoors(tardis, exterior, frame.getPart(), stack, botiProvider.getBuffer(layer), 0xf000f0,
                        OverlayTexture.DEFAULT_UV, exterior.tardis().get().alarm().isEnabled() ?
                                !exterior.tardis().get().fuel().hasPower() ? 0.25f : 1f : 1f,
                        exterior.tardis().get().alarm().isEnabled() ? !exterior.tardis().get().fuel().hasPower() ? 0.01f : 0.3f : 1f,
                        exterior.tardis().get().alarm().isEnabled() ? !exterior.tardis().get().fuel().hasPower() ? 0.01f : 0.3f : 1f, 1f, true);
                botiProvider.draw();
            } finally {
                stack.pop();
            }
        }
    }

    private void updateChunkModel(ExteriorBlockEntity exteriorBlockEntity) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || exteriorBlockEntity.getWorld() == null || exteriorBlockEntity.tardis() == null) {
            LOGGER.warn("Cannot update chunk model: world or tardis is null");
            return;
        }

        Tardis tardis = exteriorBlockEntity.tardis().get();
        if (tardis.stats().getTargetWorld() == World.OVERWORLD) {
            return;
        }

        long currentTime = mc.world.getTime();
        if (exteriorBlockEntity.lastRequestTime == 0 || currentTime - exteriorBlockEntity.lastRequestTime >= 20) {
            ClientPlayNetworking.send(new BOTIChunkRequestC2SPacket(exteriorBlockEntity.getPos(), tardis.stats().getTargetWorld(), tardis.stats().targetPos()));
            exteriorBlockEntity.lastRequestTime = currentTime;
        }
    }

    public boolean behindDoor(BlockPos doorPos, BlockPos pos, Direction doorFacing) {
        if (doorFacing == null) {
            return false;
        }

        int x = pos.getX();
        int z = pos.getZ();
        int dx = doorPos.getX();
        int dz = doorPos.getZ();

        switch (doorFacing) {
            case UP, DOWN -> {
                return false; // Invalid door facing, skip rendering
            }
            case NORTH -> {
                return z <= dz;
            }
            case SOUTH -> {
                return z >= dz;
            }
            case EAST -> {
                return x >= dx;
            }
            case WEST -> {
                return x <= dx;
            }
        }
        return false;
    }

    public void close() {
        if (this.BOTI_VBO != null) {
            this.BOTI_VBO.close();
            this.BOTI_VBO = null;
        }
    }
}