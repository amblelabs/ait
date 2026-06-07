package dev.loqor.portal.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import dev.amble.ait.AITMod;
import dev.amble.ait.core.blockentities.DoorBlockEntity;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

public class WorldGeometryRenderer {
    private final Map<ChunkSectionPos, Map<RenderLayer, VertexBuffer>> sectionBuffers = new HashMap<>();
    private final List<BlockEntity> blockEntities = new CopyOnWriteArrayList<>();
    private CompletableFuture<Void> buildFuture = null;
    private BlockPos lastCenterPos = null;
    private boolean needsRebuild = true;

    private final int renderDistance;
    private Matrix4f projectionMatrix;

    private Direction doorFacing = Direction.NORTH;
    private Direction lastDoorFacing = null;

    public WorldGeometryRenderer(int renderDistance) {
        this.renderDistance = renderDistance;
    }

    public void setProjectionMatrix(Matrix4f projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
    }

    public void setOrthographicProjection(float aspect, float viewSize, float near, float far) {
        this.projectionMatrix = new Matrix4f().ortho(
                -viewSize * aspect, viewSize * aspect,
                -viewSize, viewSize,
                near, far
        );
    }

    public void setPerspectiveProjection(float fov, float aspect, float near, float far) {
        this.projectionMatrix = new Matrix4f().perspective(
                (float) Math.toRadians(fov),
                aspect,
                near,
                far
        );
    }

    public void markDirty() {
        this.needsRebuild = true;
    }

    public void setDoorFacing(Direction facing) {
        if (this.lastDoorFacing != facing) {
            this.doorFacing = facing;
            this.lastDoorFacing = facing;
            markDirty();
        } else {
            this.doorFacing = facing;
        }
    }

    public void render(UUID id, World world, BlockPos centerPos, MatrixStack matrices, float tickDelta, boolean checkBehindPortal) {
        ClientWorld portalWorld = PortalDataManager.getOrCreate(id).world();

        GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
        projectionMatrix = gameRenderer.getBasicProjectionMatrix(gameRenderer.getFov(gameRenderer.getCamera(),
                MinecraftClient.getInstance().getTickDelta(), true));

        if (needsRebuild && (buildFuture == null || buildFuture.isDone())) {
            lastCenterPos = centerPos;
            needsRebuild = false;
            rebuildGeometry(portalWorld, centerPos, checkBehindPortal);
        }

        Matrix4f originalProjection = new Matrix4f(RenderSystem.getProjectionMatrix());

        RenderSystem.setProjectionMatrix(projectionMatrix, VertexSorter.BY_DISTANCE);
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);

        renderBlockEntities(portalWorld, matrices, tickDelta, centerPos);
        renderEntities(portalWorld, matrices, tickDelta, centerPos);

        MatrixStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.push();
        modelViewStack.multiplyPositionMatrix(matrices.peek().getPositionMatrix());

        RenderSystem.applyModelViewMatrix();

        if (!sectionBuffers.isEmpty()) {
            renderTerrain(matrices);
        }

        modelViewStack.pop();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(originalProjection, VertexSorter.BY_DISTANCE);
    }

    private void renderTerrain(MatrixStack matrices) {
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        for (RenderLayer layer : RenderLayer.getBlockLayers()) {
            if (layer == RenderLayer.getTranslucent()) {
                continue;
            }

            layer.startDrawing();

            for (Map.Entry<ChunkSectionPos, Map<RenderLayer, VertexBuffer>> entry : sectionBuffers.entrySet()) {
                ChunkSectionPos sectionPos = entry.getKey();

                double dx = (sectionPos.getCenterPos().getX() - lastCenterPos.getX());
                double dy = (sectionPos.getCenterPos().getY() - lastCenterPos.getY());
                double dz = (sectionPos.getCenterPos().getZ() - lastCenterPos.getZ());
                double distSq = dx * dx + dy * dy + dz * dz;

                if (distSq > renderDistance * renderDistance * 256) {
                    continue;
                }

                Map<RenderLayer, VertexBuffer> layerBuffers = entry.getValue();
                VertexBuffer vbo = layerBuffers.get(layer);

                if (vbo != null) {
                    vbo.bind();
                    vbo.draw(matrices.peek().getPositionMatrix(), projectionMatrix, RenderSystem.getShader());
                }
            }

            VertexBuffer.unbind();
            layer.endDrawing();
        }

        RenderLayer translucentLayer = RenderLayer.getTranslucent();
        translucentLayer.startDrawing();

        for (Map.Entry<ChunkSectionPos, Map<RenderLayer, VertexBuffer>> entry : sectionBuffers.entrySet()) {
            ChunkSectionPos sectionPos = entry.getKey();

            double dx = (sectionPos.getCenterPos().getX() - lastCenterPos.getX());
            double dy = (sectionPos.getCenterPos().getY() - lastCenterPos.getY());
            double dz = (sectionPos.getCenterPos().getZ() - lastCenterPos.getZ());
            double distSq = dx * dx + dy * dy + dz * dz;

            if (distSq > renderDistance * renderDistance * 256) {
                continue;
            }

            Map<RenderLayer, VertexBuffer> layerBuffers = entry.getValue();
            VertexBuffer vbo = layerBuffers.get(translucentLayer);

            if (vbo != null) {
                vbo.bind();
                vbo.draw(matrices.peek().getPositionMatrix(), projectionMatrix, RenderSystem.getShader());
            }
        }

        VertexBuffer.unbind();
        translucentLayer.endDrawing();

        RenderSystem.disableBlend();
    }

    private void renderBlockEntities(ClientWorld portalWorld, MatrixStack matrices, float tickDelta, BlockPos centerPos) {
        MinecraftClient client = MinecraftClient.getInstance();
        BlockEntityRenderDispatcher dispatcher = client.getBlockEntityRenderDispatcher();
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();

        dispatcher.configure(portalWorld, client.gameRenderer.getCamera(), client.crosshairTarget);

        List<BlockEntity> snapshot = new ArrayList<>(blockEntities);

        for (BlockEntity blockEntity : snapshot) {
            BlockPos blockPos = blockEntity.getPos();

            if (!isWithinRenderBounds(blockPos, centerPos)) {
                continue;
            }

            if (blockEntity instanceof DoorBlockEntity || blockEntity instanceof ExteriorBlockEntity) continue;

            matrices.push();
            matrices.translate(
                    blockPos.getX() - centerPos.getX(),
                    blockPos.getY() - centerPos.getY(),
                    blockPos.getZ() - centerPos.getZ()
            );

            dispatcher.render(blockEntity, tickDelta, matrices, immediate);
            matrices.pop();
        }

        immediate.draw();
        assertEmpty(matrices);
    }

    private void renderEntities(ClientWorld portalWorld, MatrixStack matrices, float tickDelta, BlockPos centerPos) {
        MinecraftClient client = MinecraftClient.getInstance();
        EntityRenderDispatcher dispatcher = client.getEntityRenderDispatcher();
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();

        dispatcher.configure(portalWorld, client.gameRenderer.getCamera(), client.targetedEntity);

        for (Entity entity : portalWorld.getEntities()) {
            if (entity == null || !isWithinRenderBounds(entity.getBlockPos(), centerPos))
                continue;

            // Interpolated render position, expressed relative to the portal centre (same convention as the
            // block-entity pass). The dispatcher translates the matrix stack by these coordinates internally.
            double x = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX()) - centerPos.getX();
            double y = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY()) - centerPos.getY();
            double z = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ()) - centerPos.getZ();
            float yaw = MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw());

            int light = dispatcher.getLight(entity, tickDelta);
            dispatcher.render(entity, x, y, z, yaw, tickDelta, matrices, immediate, light);
        }

        immediate.draw();
    }

    private boolean isWithinRenderBounds(BlockPos blockPos, BlockPos centerPos) {
        int minX = centerPos.getX() - renderDistance;
        int minY = centerPos.getY() - renderDistance;
        int minZ = centerPos.getZ() - renderDistance;

        int maxX = centerPos.getX() + renderDistance;
        int maxY = centerPos.getY() + renderDistance;
        int maxZ = centerPos.getZ() + renderDistance;

        return blockPos.getX() >= minX && blockPos.getX() <= maxX &&
                blockPos.getY() >= minY && blockPos.getY() <= maxY &&
                blockPos.getZ() >= minZ && blockPos.getZ() <= maxZ;
    }

    private void assertEmpty(MatrixStack matrices) {
        if (!matrices.isEmpty()) {
            throw new IllegalStateException("Matrix stack not empty");
        }
    }

    private void rebuildGeometry(World world, BlockPos centerPos, boolean checkBehindPortal) {
        buildFuture = CompletableFuture.runAsync(() -> {
            try {
                Map<ChunkSectionPos, Map<RenderLayer, VertexBuffer>> tempBuffers = Collections.synchronizedMap(new HashMap<>());

                List<BlockEntity> foundBlockEntities = new ArrayList<>();
                BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
                Random random = Random.create();

                int minX = centerPos.getX() - renderDistance;
                int minY = centerPos.getY() - renderDistance;
                int minZ = centerPos.getZ() - renderDistance;
                int maxX = centerPos.getX() + renderDistance;
                int maxY = centerPos.getY() + renderDistance;
                int maxZ = centerPos.getZ() + renderDistance;

                int minSectionX = minX >> 4;
                int minSectionY = minY >> 4;
                int minSectionZ = minZ >> 4;
                int maxSectionX = maxX >> 4;
                int maxSectionY = maxY >> 4;
                int maxSectionZ = maxZ >> 4;

                for (int sectionX = minSectionX; sectionX <= maxSectionX; sectionX++) {
                    for (int sectionY = minSectionY; sectionY <= maxSectionY; sectionY++) {
                        for (int sectionZ = minSectionZ; sectionZ <= maxSectionZ; sectionZ++) {
                            buildSectionToMap(world, centerPos, sectionX, sectionY, sectionZ,
                                    blockRenderManager, random, foundBlockEntities, tempBuffers, checkBehindPortal);
                        }
                    }
                }

                MinecraftClient.getInstance().execute(() -> {
                    for (Map<RenderLayer, VertexBuffer> layerMap : sectionBuffers.values()) {
                        for (VertexBuffer vbo : layerMap.values()) {
                            vbo.close();
                        }
                    }
                    sectionBuffers.clear();

                    sectionBuffers.putAll(tempBuffers);

                    blockEntities.clear();
                    blockEntities.addAll(foundBlockEntities);
                });

            } catch (Exception e) {
                AITMod.LOGGER.error("Failed to rebuild geometry", e);
            }
        });
    }

    private void buildSectionToMap(World world, BlockPos centerPos, int sectionX, int sectionY, int sectionZ,
                                   BlockRenderManager blockRenderManager, Random random,
                                   List<BlockEntity> foundBlockEntities,
                                   Map<ChunkSectionPos, Map<RenderLayer, VertexBuffer>> targetMap, boolean checkBehindPortal) {

        Map<RenderLayer, BufferBuilder> builders = new HashMap<>();
        Set<RenderLayer> usedLayers = new HashSet<>();

        for (RenderLayer layer : RenderLayer.getBlockLayers()) {
            BufferBuilder builder = new BufferBuilder(layer.getExpectedBufferSize());
            builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
            builders.put(layer, builder);
        }

        MatrixStack matrices = new MatrixStack();
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        int startX = sectionX << 4;
        int startY = sectionY << 4;
        int startZ = sectionZ << 4;
        int endX = startX + 15;
        int endY = startY + 15;
        int endZ = startZ + 15;

        boolean hasBlocks = false;

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    mutablePos.set(x, y, z);

                    BlockState state = world.getBlockState(mutablePos);

                    if (state.isAir() || state.getBlock() == Blocks.BLACK_CONCRETE) {
                        continue;
                    }

                    double relX = x - centerPos.getX();
                    double relY = y - centerPos.getY();
                    double relZ = z - centerPos.getZ();

                    boolean behindPortal = switch (doorFacing) {
                        case NORTH -> relZ > 0.0;
                        case SOUTH -> relZ < -0.0;
                        case EAST -> relX < -0.0;
                        case WEST -> relX > 0.0;
                        default -> false;
                    };

                    if (behindPortal && checkBehindPortal) {
                        continue;
                    }

                    if (isFullySurrounded(world, mutablePos)) {
                        continue;
                    }

                    hasBlocks = true;

                    if (state.hasBlockEntity()) {
                        BlockEntity blockEntity = world.getBlockEntity(mutablePos);
                        if (blockEntity != null) {
                            synchronized (foundBlockEntities) {
                                foundBlockEntities.add(blockEntity);
                            }
                        }
                    }

                    FluidState fluidState = state.getFluidState();
                    if (!fluidState.isEmpty()) {
                        RenderLayer fluidLayer = RenderLayers.getFluidLayer(fluidState);
                        BufferBuilder builder = builders.get(fluidLayer);
                        usedLayers.add(fluidLayer);

                        matrices.push();
                        matrices.translate(relX, relY, relZ);
                        blockRenderManager.renderFluid(mutablePos, world, builder, state, fluidState);
                        matrices.pop();
                    }

                    if (state.getRenderType() != BlockRenderType.INVISIBLE) {
                        RenderLayer blockLayer = RenderLayers.getBlockLayer(state);
                        BufferBuilder builder = builders.get(blockLayer);
                        usedLayers.add(blockLayer);

                        matrices.push();
                        matrices.translate(relX, relY, relZ);
                        blockRenderManager.renderBlock(state, mutablePos, world, matrices, builder, true, random);
                        matrices.pop();
                    }
                }
            }
        }

        if (hasBlocks) {
            ChunkSectionPos sectionPos = ChunkSectionPos.from(sectionX, sectionY, sectionZ);

            Map<RenderLayer, BufferBuilder.BuiltBuffer> builtBuffers = new HashMap<>();
            for (RenderLayer layer : usedLayers) {
                BufferBuilder builder = builders.get(layer);
                BufferBuilder.BuiltBuffer builtBuffer = builder.end();
                builtBuffers.put(layer, builtBuffer);
            }

            for (RenderLayer layer : RenderLayer.getBlockLayers()) {
                if (!usedLayers.contains(layer)) {
                    builders.get(layer).end().release();
                }
            }

            MinecraftClient.getInstance().execute(() -> {
                Map<RenderLayer, VertexBuffer> layerBuffers = new HashMap<>();

                for (Map.Entry<RenderLayer, BufferBuilder.BuiltBuffer> entry : builtBuffers.entrySet()) {
                    RenderLayer layer = entry.getKey();
                    BufferBuilder.BuiltBuffer builtBuffer = entry.getValue();

                    VertexBuffer vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);
                    vbo.bind();
                    vbo.upload(builtBuffer);
                    VertexBuffer.unbind();

                    layerBuffers.put(layer, vbo);
                }

                if (!layerBuffers.isEmpty()) {
                    targetMap.put(sectionPos, layerBuffers);
                }
            });
        } else {
            for (BufferBuilder builder : builders.values()) {
                builder.end().release();
            }
        }
    }

    private boolean isFullySurrounded(World world, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos adjacent = pos.offset(dir);
            BlockState adjacentState = world.getBlockState(adjacent);
            if (!adjacentState.isOpaqueFullCube(world, adjacent)) {
                return false;
            }
        }
        return true;
    }

    public void close() {
        for (Map<RenderLayer, VertexBuffer> layerMap : sectionBuffers.values()) {
            for (VertexBuffer vbo : layerMap.values()) {
                vbo.close();
            }
        }
        sectionBuffers.clear();
        blockEntities.clear();
    }

    public int getSectionCount() {
        return sectionBuffers.size();
    }

    public int getBlockEntityCount() {
        return blockEntities.size();
    }
}