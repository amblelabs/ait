package dev.amble.ait.client.boti;


import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
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
    private float lastRenderTick = -1;

    public void renderExteriorBoti(ExteriorBlockEntity exterior, ClientExteriorVariantSchema variant, MatrixStack stack, Identifier frameTex, SinglePartEntityModel frame, ModelPart mask, int light) {
        if (!AITModClient.CONFIG.enableTardisBOTI)
            return;

        if (MinecraftClient.getInstance().world == null
                || MinecraftClient.getInstance().player == null) return;

        if (!exterior.isLinked())
            return;

        long currentTime = MinecraftClient.getInstance().getRenderTime();
        if (MinecraftClient.getInstance().getTickDelta() == lastRenderTick) {
            return;
        }

        ClientTardis tardis = exterior.tardis().get().asClient();
        ;
        if (currentTime - exterior.lastRequestTime >= 20) {
            updateChunkModel(exterior);
            exterior.lastRequestTime = currentTime;
        }
        if (currentTime < exterior.lastRequestTime) // Make sure the last request time is never greater than the current time, trust me it happens for some reason, almost like the last request time is *saved* in the TE
            exterior.lastRequestTime = 0;


        stack.push();

        MinecraftClient.getInstance().getFramebuffer().endWrite();

        BOTI_HANDLER.setupFramebuffer();

        Vec3d skyColor = MinecraftClient.getInstance().world.getSkyColor(MinecraftClient.getInstance().player.getPos(), MinecraftClient.getInstance().getTickDelta());
        if (AITModClient.CONFIG.greenScreenBOTI)
            BOTI.setFramebufferColor(BOTI_HANDLER.afbo, 0, 1, 0, 1);
        else
            BOTI.setFramebufferColor(BOTI_HANDLER.afbo, (float) skyColor.x, (float) skyColor.y, (float) skyColor.z, 1);

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

        copyDepth(BOTI_HANDLER.afbo, MinecraftClient.getInstance().getFramebuffer());

        BOTI_HANDLER.afbo.beginWrite(false);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        GL11.glStencilMask(0x00);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);

        // It's not a loop, it's a label https://www.geeksforgeeks.org/adding-labels-to-method-and-functions-in-java/
        OUTOFBOTI:
        if (AITModClient.CONFIG.shouldRenderBOTIInterior) {
            MatrixStack matrices = new MatrixStack();
            StatsHandler stats = tardis.stats();
            BlockPos targetPos = stats.targetPos();
            BlockPos doorPos = tardis.getDesktop().getDoorPos().getPos();
            Direction doorDirection = tardis.getDesktop().getDoorPos().toMinecraftDirection();
            RegistryKey<World> targetWorld = stats.getTargetWorld();

            if (doorPos != null && targetPos != null && targetWorld != null) {
                stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

                if (stats.botiChunkVBO == null) {
                    this.updateChunkModel(exterior);
                    break OUTOFBOTI;
                }

                // DON'T TOUCH THIS LOQOR
//                OUTOFVBO:
//                if (BOTIChunkVBO.shouldGenerateQuads) {
////                    stats.botiChunkVBO.render(stack, light, OverlayTexture.DEFAULT_UV);
//                    if (stats.botiChunkVBO.isWorkingInThread()) break OUTOFVBO;
//                    if (stats.botiChunkVBO.isDirty()) break OUTOFVBO;
//
//                    stats.botiChunkVBO.vertexBuffer.bind();
//                    VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.setupState();
//                    stats.botiChunkVBO.vertexBuffer.draw();
//                    VertexBuffer.unbind();
//                    VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.clearState();
//
//
//                    if (stats.botiChunkVBO != null &&
//                            stats.botiChunkVBO.isDirty() &&
//                            !stats.botiChunkVBO.isWorkingInThread())
//                        stats.botiChunkVBO.updateChunkModel(exterior);
//                }

                OUTOFBLOCKRENDERER:
                if (!BOTIChunkVBO.shouldGenerateQuads) {
                    if (stats.posState == null) {
                        updateChunkModel(exterior);
                        break OUTOFBLOCKRENDERER;
                    }
                    float tickDelta = MinecraftClient.getInstance().getTickDelta();
                    if (this.lastRenderTick == tickDelta) {
                        break OUTOFBLOCKRENDERER;
                    }

                    for (Map.Entry<BlockPos, net.minecraft.block.BlockState> entry : stats.posState.entrySet()) {
                        BlockPos pos = entry.getKey();
                        net.minecraft.block.BlockState state = entry.getValue();
                        if (state.isAir()) continue;
                        if (!behindDoor(doorPos, pos, doorDirection)) continue;

                        stack.push();
                        stack.scale(-1, 1, 1);
                        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(doorDirection.asRotation()));

                        float offsetX = doorPos.getX() + 0.5f - pos.getX();
                        float offsetY = doorPos.getY() - pos.getY();
                        float offsetZ = doorPos.getZ() + 0.5f - pos.getZ();
                        stack.translate(offsetX, offsetY, offsetZ);

                        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));
                        stack.scale(1, -1, 1);

                        BlockModelRenderer.enableBrightnessCache();
                        if (state.getBlock() instanceof FluidBlock fluidBlock) {
                            FluidState fluidState = fluidBlock.getFluidState(state);
                            MinecraftClient.getInstance().getBlockRenderManager().renderFluid(
                                    new BlockPos((int) offsetX, (int) offsetY, (int) offsetZ),
                                    MinecraftClient.getInstance().world,
                                    botiProvider.getBuffer(RenderLayers.getFluidLayer(fluidState)),
                                    state,
                                    fluidState);
                        } else {
                            MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(
                                    stack.peek(),
                                    botiProvider.getBuffer(RenderLayers.getBlockLayer(state)),
                                    state,
                                    MinecraftClient.getInstance().getBlockRenderManager().getModel(state),
                                    1, 1, 1, 210, OverlayTexture.DEFAULT_UV
                            );
                        }
                        BlockModelRenderer.disableBrightnessCache();
                        stack.pop();
                    }
                    this.lastRenderTick = tickDelta;
                }

                for (Map.Entry<BlockPos, BlockEntity> entry : stats.blockEntities.entrySet()) {
                    BlockPos offsetPos = entry.getKey();
                    BlockEntity be = entry.getValue();
                    BlockEntityRenderer<BlockEntity> renderer = MinecraftClient.getInstance().getBlockEntityRenderDispatcher().get(be);

                    if (renderer != null) {
                        stack.push();
                        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90));
                        stack.translate(doorPos.getX() - offsetPos.getX(), doorPos.getY() - offsetPos.getY(), doorPos.getZ() - offsetPos.getZ());
                        be.setWorld(MinecraftClient.getInstance().world);
                        renderer.render(be, MinecraftClient.getInstance().getTickDelta(), stack,
                                botiProvider, light, OverlayTexture.DEFAULT_UV);
                        botiProvider.draw();
                        stack.pop();
                        System.out.println("No renderer found for block entity " + be + " at " + offsetPos);
                    } else {
                        MinecraftClient.getInstance().getBlockEntityRenderDispatcher().render(be, MinecraftClient.getInstance().getTickDelta(), matrices, botiProvider);
                    }
                }
//            }
            }
        }


        stack.push();

        // TODO come back to this rotation stuff momentarily
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0));

        ((ExteriorModel) frame).renderDoors(tardis, exterior, frame.getPart(), stack, botiProvider.getBuffer(AITRenderLayers.getBotiInterior(variant.texture())), light, OverlayTexture.DEFAULT_UV, 1, 1F, 1.0F, 1.0F, true);
        botiProvider.draw();
        stack.pop();

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0));

        if (variant != ClientExteriorVariantRegistry.CORAL_GROWTH) {
            BiomeHandler handler = exterior.tardis().get().handler(TardisComponent.Id.BIOME);
            Identifier biomeTexture = handler.getBiomeKey().get(variant.overrides());
            if (biomeTexture != null)
                ((ExteriorModel) frame).renderDoors(tardis, exterior, frame.getPart(), stack,
                        botiProvider.getBuffer(AITRenderLayers.getEntityCutoutNoCullZOffset(biomeTexture)),
                        light, OverlayTexture.DEFAULT_UV, 1, 1F, 1.0F, 1.0F, true);
        }
        botiProvider.draw();
        stack.pop();

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0));
        if (variant.emission() != null)
            ((ExteriorModel) frame).renderDoors(tardis, exterior, frame.getPart(), stack, botiProvider.getBuffer(DependencyChecker.hasIris() ? AITRenderLayers.tardisEmissiveCullZOffset(variant.emission(), true) : AITRenderLayers.getBeaconBeam(variant.emission(), true)), 0xf000f0,
                    OverlayTexture.DEFAULT_UV, exterior.tardis().get().alarm().isEnabled() ?
                            !exterior.tardis().get().fuel().hasPower() ? 0.25f : 1f : 1f,
                    exterior.tardis().get().alarm().isEnabled() ? !exterior.tardis().get().fuel().hasPower() ? 0.01f : 0.3f : 1f,
                    exterior.tardis().get().alarm().isEnabled() ? !exterior.tardis().get().fuel().hasPower() ? 0.01f : 0.3f : 1f, 1f, true);
        botiProvider.draw();
        stack.pop();

        MinecraftClient.getInstance().getFramebuffer().beginWrite(true);

        BOTI.copyColor(BOTI_HANDLER.afbo, MinecraftClient.getInstance().getFramebuffer());

        GL11.glDisable(GL11.GL_STENCIL_TEST);

        stack.pop();
    }

    private void updateChunkModel(ExteriorBlockEntity exteriorBlockEntity) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.world == null || exteriorBlockEntity.getWorld() == null || exteriorBlockEntity.tardis() == null) return;

        Tardis tardis = exteriorBlockEntity.tardis().get();

        long currentTime = mc.world.getTime();

        if (exteriorBlockEntity.tardis().get().stats().getTargetWorld() == World.OVERWORLD)
            return;

        //TODO: Implement a real query time not some hacky non working bullshit
//        if (exteriorBlockEntity.lastRequestTime == 0 || currentTime - exteriorBlockEntity.lastRequestTime >= 20) {
        ClientPlayNetworking.send(new BOTIChunkRequestC2SPacket(exteriorBlockEntity.getPos(), tardis.stats().getTargetWorld(), tardis.stats().targetPos()));
        exteriorBlockEntity.lastRequestTime = currentTime;
//        }
    }

    public boolean behindDoor(BlockPos DoorPos, BlockPos pos, Direction doorFacing) {
        int x = pos.getX();
        int z = pos.getZ();
        int dx = DoorPos.getX();
        int dz = DoorPos.getZ();

        switch (doorFacing) {
            case UP,
                 DOWN -> { // If the door is facing Up or Down, something is severely wrong, and we shouldn't even attempt to render the blocks in the first place
                return false;
            }
            case NORTH -> {
                return z >= dz;
            }
            case SOUTH -> {
                return z <= dz;
            }
            case EAST -> {
                return x <= dx;
            }
            case WEST -> {
                return x >= dx;
            }
        }
        return false;
    }
}
