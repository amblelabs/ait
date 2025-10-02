/* (C) TAMA Studios 2025 */
package dev.codiak;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.util.TardisUtil;

public class BOTIUtils {
    public static List<BakedQuad> getModelFromBlock(
            BlockState state, BlockPos pos, Random rand, Map<BlockPos, BotiChunkContainer> map) {
        BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
        Direction[] directions = Direction.values();

        BakedModel model = blockRenderManager.getModels().getModel(state);

        List<BakedQuad> quads = new java.util.ArrayList<>(List.of());
        for (Direction dir : directions) {
            BlockPos neighbourPos = pos.offset(dir);
            BotiChunkContainer neighborContainer = map.get(neighbourPos);
            if (neighborContainer != null) {
                if (BOTIUtils.shouldRenderFace(
                        state, neighborContainer.state, MinecraftClient.getInstance().world, pos, dir, neighbourPos))
                    quads.addAll(model.getQuads(state, dir, rand));
            } else quads.addAll(model.getQuads(state, dir, rand));
        }
        return quads;
    }

    public static Map<BlockPos, BotiChunkContainer> getMapFromContainerList(List<BotiChunkContainer> list) {
        Map<BlockPos, BotiChunkContainer> map = new HashMap<>(list.size());
        for (BotiChunkContainer container : list) {
            map.put(container.pos, container);
        }
        return map;
    }

    public static VertexBuffer buildModelVBO(List<BotiChunkContainer> containers) {
        MinecraftClient mc = MinecraftClient.getInstance();

        BufferBuilder buffer = new BufferBuilder((int) Math.pow(16, 3));
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);

        MatrixStack stack = new MatrixStack();
        Map<BlockPos, BotiChunkContainer> chunkMap = getMapFromContainerList(containers);

        chunkMap.forEach((pos, container) -> {
            BlockColors colors = mc.getBlockColors();
            int color = colors.getColor(container.state, MinecraftClient.getInstance().world, container.pos, 0);

            // Extract RGB components (normalize to 0-1 range)
            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8) & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;

            Random rand = Random.create(pos.asLong());
            stack.push();
            stack.translate(pos.getX(), pos.getY(), pos.getZ());

            if (container.IsFluid) {
                FluidState fluidState = container.fluidState;
                if (!fluidState.isEmpty()) {
                    FluidQuadCollector fluidCollector = new FluidQuadCollector();

                    MinecraftClient.getInstance()
                            .getBlockRenderManager()
                            .renderFluid(pos, MinecraftClient.getInstance().world, fluidCollector, container.state, fluidState);

                    // Now feed collector.getVertices() into VBO
                    for (FluidQuadCollector.FluidVertex v : fluidCollector.getVertices()) {
                        buffer.vertex(v.x, v.y, v.z)
                                .color(v.r, v.g, v.b, v.a)
                                .texture(v.u, v.v)
                                .light(container.light)
                                .next();
                    }
                }
            }

            for (BakedQuad quad : getModelFromBlock(container.state, pos, rand, chunkMap)) {
                // Clamp brightness between 0.0 and 1.0
                float brightness = Math.max(0.0f, Math.min(1.0f, (float) container.light / 0xf000f0));

                // Apply brightness to base RGB values and clamp to [0, 1]
                float rLit = Math.max(0.0f, Math.min(1.0f, r * brightness));
                float gLit = Math.max(0.0f, Math.min(1.0f, g * brightness));
                float bLit = Math.max(0.0f, Math.min(1.0f, b * brightness));

                // putBulkData exists on BufferBuilder in Fabric mappings; parameters may vary by patch
                buffer.quad(
                        stack.peek(),
                        quad,
                        rLit,
                        gLit,
                        bLit,
                        container.light,
                        OverlayTexture.DEFAULT_UV);
            }

            stack.pop();
        });
        BufferBuilder.BuiltBuffer rendered = buffer.end();

        VertexBuffer vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);
        vbo.bind();
        vbo.upload(rendered);
        VertexBuffer.unbind();

        return vbo;
    }

    public static void updateChunkModel(AbstractPortalBlockEntity blockEntity) {
        if (blockEntity.getPos() == null) return;
        if (blockEntity.getTargetWorld() == null) return;
        if (blockEntity.getTargetPos() == null) return;
        if (MinecraftClient.getInstance().world == null) return;

        if(!blockEntity.containers.isEmpty()) blockEntity.containers.clear();
        if(!blockEntity.blockEntities.isEmpty()) blockEntity.blockEntities.clear();

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(blockEntity.getPos());
        buf.writeRegistryKey(blockEntity.getTargetWorld());
        buf.writeBlockPos(blockEntity.getTargetPos());

        ClientPlayNetworking.send(TardisUtil.BOTI_REQUEST_CHUNK_C2S, buf);

        blockEntity.lastRequestTime = MinecraftClient.getInstance().world.getTime();
    }

    public static boolean shouldRenderFace(
            BlockState state, BlockState neighbor, World level, BlockPos pos, Direction dir, BlockPos secondPos) {
        return !state.isSolid() || !neighbor.isSolid();
    }

    public static void Render(MatrixStack matrixStack, VertexConsumerProvider buffer, AbstractPortalBlockEntity portal) {

        matrixStack.push();
        matrixStack.translate(-6, -6, -6);

        if (portal.MODEL_VBO == null) {
            portal.MODEL_VBO = BOTIUtils.buildModelVBO(portal.containers);
        } else {
            matrixStack.push();

            // Set up the correct shader
            RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapProgram);

            // Set up shader lights
            assert GameRenderer.getPositionColorTexLightmapProgram() != null;
            RenderSystem.setupShaderLights(GameRenderer.getPositionColorTexLightmapProgram());

            // Enable depth test and disable culling
            RenderSystem.enableDepthTest();
            RenderSystem.disableCull();

            // Bind VBO and draw
            try {
                portal.MODEL_VBO.bind();
                portal.MODEL_VBO.draw(matrixStack.peek().getPositionMatrix(),
                        RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
                VertexBuffer.unbind();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Restore GL state
            RenderSystem.enableCull();
            RenderSystem.disableDepthTest();

            matrixStack.pop();
        };

        portal.blockEntities.forEach((pos, ent) -> {
            matrixStack.push();
            matrixStack.translate(pos.getX(), pos.getY(), pos.getZ());
            MinecraftClient.getInstance()
                    .getBlockEntityRenderDispatcher()
                    .render(ent, MinecraftClient.getInstance().getTickDelta(), matrixStack, buffer);
            matrixStack.pop();
        });
        matrixStack.pop();
    }

    public static void updateMe(AbstractPortalBlockEntity portal) {
        long currentTime = MinecraftClient.getInstance().world.getTime();

        if (currentTime - portal.lastUpdateTime >= 120) {
            BOTIUtils.updateChunkModel(portal);
            portal.lastUpdateTime = currentTime;
        }
    }

    public static void PortalChunkDataPacketS2C(ServerPlayerEntity player, AbstractPortalBlockEntity portalTile, World level) {
        BlockPos portalPos = portalTile.getPos();
        BlockPos targetPos = portalTile.getTargetPos();

        int maxBlocks = 70000;
        try {
            ArrayList<BotiChunkContainer> containers = new ArrayList<>();
            List<List<BotiChunkContainer>> containerLists = new ArrayList<>();
            int chunksToRender = 6;
            for (int u = -chunksToRender / 2;
                 u < chunksToRender / 2;
                 u++) {
                for (int v = -chunksToRender / 2;
                     v < chunksToRender / 2;
                     v++) {
                    ChunkPos chunkPos = new ChunkPos(
                            new BlockPos(targetPos.getX() + (u * 16), targetPos.getY(), targetPos.getZ() + (v * 16)));
                    level.getChunkManager().getWorldChunk(chunkPos.x, chunkPos.z, true);
                    WorldChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);
                    ChunkSection section = chunk.getSection(chunk.getSectionIndex(targetPos.getY()));

                    for (int y = 0; y < 16; y++) {
                        for (int x = 0; x < 16; x++) {
                            for (int z = 0; z < 16; z++) {
                                BlockState state = section.getBlockState(x, y, z);
                                FluidState fluidState = section.getFluidState(x, y, z);

                                if (!state.isAir()) {
                                    BlockPos pos = new BlockPos(x + (u * 16), y, z + (v * 16));

                                    if (fluidState.isEmpty())
                                        containers.add(new BotiChunkContainer(
                                                level,
                                                state,
                                                pos,
                                                BlockUtils.getPackedLight(
                                                        level,
                                                        BlockUtils.fromChunkAndLocal(chunkPos, new BlockPos(x, y, z))
                                                                .withY(targetPos.getY()))));
                                    else
                                        containers.add(new BotiChunkContainer(
                                                level,
                                                state,
                                                fluidState,
                                                pos,
                                                BlockUtils.getPackedLight(
                                                        level,
                                                        BlockUtils.fromChunkAndLocal(chunkPos, new BlockPos(x, y, z))
                                                                .withY(targetPos.getY()))));
                                }
                                if (containers.size() >= maxBlocks) {
                                    containerLists.add((List<BotiChunkContainer>) containers.clone());
                                    containers.clear();
                                }
                            }
                        }
                    }
                }
            }
            if (!containers.isEmpty()) {
                containerLists.add((List<BotiChunkContainer>) containers.clone());
                containers.clear();
            }

            for (int i = 0; i < containerLists.size(); i++) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(portalPos);
                BotiChunkContainer.encodeList(containerLists.get(i), buf);
                buf.writeInt(i);
                buf.writeInt(containerLists.size());
                ServerPlayNetworking.send(player, TardisUtil.BOTI_CHUNK_S2C, buf);
            }
            // 126142 (Too big)
            // 71267 (prob could go higher before hitting the limit but this works at 6-ish chunks)

        } catch (Exception e) {
            AITMod.LOGGER.error("Exception in packet construction: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
