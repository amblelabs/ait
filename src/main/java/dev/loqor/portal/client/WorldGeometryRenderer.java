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
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Handles rendering of world geometry (blocks and block entities) with custom projection.
 * Useful for rendering TARDIS interiors, portals, or any "bigger on the inside" effects.
 */
public class WorldGeometryRenderer {
    private final Map<ChunkSectionPos, Map<RenderLayer, VertexBuffer>> sectionBuffers = new HashMap<>();
    private final List<BlockEntity> blockEntities = new CopyOnWriteArrayList<>();
    private CompletableFuture<Void> buildFuture = null;
    private BlockPos lastCenterPos = null;
    private boolean needsRebuild = true;

    private final int renderDistance;
    private Matrix4f projectionMatrix;

    // Add this field to WorldGeometryRenderer
    private Direction doorFacing = Direction.NORTH;
    private Direction lastDoorFacing = null;

    public WorldGeometryRenderer(int renderDistance) {
        this.renderDistance = renderDistance;
    }

    /**
     * Sets the projection matrix to use for rendering
     */
    public void setProjectionMatrix(Matrix4f projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
    }

    /**
     * Creates a standard orthographic projection matrix
     *
     * @param aspect Aspect ratio (width / height)
     * @param viewSize Size of the view volume
     * @param near Near clipping plane
     * @param far Far clipping plane
     */
    public void setOrthographicProjection(float aspect, float viewSize, float near, float far) {
        this.projectionMatrix = new Matrix4f().ortho(
                -viewSize * aspect, viewSize * aspect,
                -viewSize, viewSize,
                near, far
        );
    }

    /**
     * Creates a perspective projection matrix (like normal Minecraft rendering)
     *
     * @param fov Field of view in degrees (typically 70-90)
     * @param aspect Aspect ratio (width / height)
     * @param near Near clipping plane (typically 0.05)
     * @param far Far clipping plane (typically 1000+)
     */
    public void setPerspectiveProjection(float fov, float aspect, float near, float far) {
        this.projectionMatrix = new Matrix4f().perspective(
                (float) Math.toRadians(fov),
                aspect,
                near,
                far
        );
    }

    /**
     * Marks geometry for rebuild on next render
     */
    public void markDirty() {
        this.needsRebuild = true;
    }

    // Modify setDoorFacing to only mark dirty when it actually changes
    public void setDoorFacing(Direction facing) {
        if (this.lastDoorFacing != facing) {
            this.doorFacing = facing;
            this.lastDoorFacing = facing;
            markDirty(); // Only rebuild when door direction changes
        } else {
            this.doorFacing = facing; // Update without marking dirty
        }
    }

    /**
     * Main render method - call this every frame
     *
     * @param world The world to render from
     * @param centerPos Center position (usually camera/player position)
     * @param matrices View matrix transformations
     * @param tickDelta Partial tick for interpolation
     */
    public void render(World world, BlockPos centerPos, MatrixStack matrices, float tickDelta, boolean checkBehindPortal) {
        ClientWorld portalWorld = PortalDataManager.get().world();

        GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
        projectionMatrix = gameRenderer.getBasicProjectionMatrix(gameRenderer.getFov(gameRenderer.getCamera(),
                MinecraftClient.getInstance().getTickDelta(), true));

        // Rebuild geometry if needed
        if (needsRebuild && (buildFuture == null || buildFuture.isDone())) {
            lastCenterPos = centerPos;
            needsRebuild = false;
            rebuildGeometry(portalWorld, centerPos, checkBehindPortal);
        }

        // Store original projection
        Matrix4f originalProjection = new Matrix4f(RenderSystem.getProjectionMatrix());

        // Set custom projection
        RenderSystem.setProjectionMatrix(projectionMatrix, VertexSorter.BY_DISTANCE);
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);

        // Enable depth testing and blending
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.depthMask(true);

        // Render block entities
        renderBlockEntities(portalWorld, matrices, tickDelta, centerPos);

        // Apply view matrix to RenderSystem for immediate mode rendering
        MatrixStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.push();
        modelViewStack.multiplyPositionMatrix(matrices.peek().getPositionMatrix());
        RenderSystem.applyModelViewMatrix();

        // Render terrain
        if (!sectionBuffers.isEmpty()) {
            renderTerrain(matrices);
        }

        // Restore state
        modelViewStack.pop();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.setProjectionMatrix(originalProjection, VertexSorter.BY_DISTANCE);
    }

    /**
     * Renders terrain using VBOs
     */
    private void renderTerrain(MatrixStack matrices) {
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);

        // Prepare blending for transparency
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        for (RenderLayer layer : RenderLayer.getBlockLayers()) {
            if (layer == RenderLayer.getTranslucent()) {
                continue; // Skip for now, render translucent last
            }

            layer.startDrawing();

            for (Map.Entry<ChunkSectionPos, Map<RenderLayer, VertexBuffer>> entry : sectionBuffers.entrySet()) {
                ChunkSectionPos sectionPos = entry.getKey();

                // Cull distant sections
                double dx = (sectionPos.getCenterPos().getX() - lastCenterPos.getX());
                double dy = (sectionPos.getCenterPos().getY() - lastCenterPos.getY());
                double dz = (sectionPos.getCenterPos().getZ() - lastCenterPos.getZ());
                double distSq = dx * dx + dy * dy + dz * dz;

                if (distSq > renderDistance * renderDistance * 256) {
                    continue; // Skip sections too far
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

        // Render fluid layers last
        RenderLayer translucentLayer = RenderLayer.getTranslucent();
        translucentLayer.startDrawing();

        for (Map.Entry<ChunkSectionPos, Map<RenderLayer, VertexBuffer>> entry : sectionBuffers.entrySet()) {
            ChunkSectionPos sectionPos = entry.getKey();

            double dx = (sectionPos.getCenterPos().getX() - lastCenterPos.getX());
            double dy = (sectionPos.getCenterPos().getY() - lastCenterPos.getY());
            double dz = (sectionPos.getCenterPos().getZ() - lastCenterPos.getZ());
            double distSq = dx * dx + dy * dy + dz * dz;

            if (distSq > renderDistance * renderDistance * 256) {
                continue; // Skip sections too far
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

        RenderSystem.disableBlend(); // Restore state
    }

    /**
     * Renders block entities following Minecraft's WorldRenderer pattern
     */
    private void renderBlockEntities(ClientWorld portalWorld, MatrixStack matrices, float tickDelta, BlockPos centerPos) {
        MinecraftClient client = MinecraftClient.getInstance();
        BlockEntityRenderDispatcher dispatcher = client.getBlockEntityRenderDispatcher();
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();

        // Configure dispatcher
        dispatcher.configure(portalWorld, client.gameRenderer.getCamera(), null);

        // Create a snapshot to avoid concurrent modification
        List<BlockEntity> snapshot = new ArrayList<>(blockEntities);

        // Render block entities, restricted to valid sections
        for (BlockEntity blockEntity : snapshot) {
            BlockPos blockPos = blockEntity.getPos();

            // Check if this block entity belongs inside the rendering area
            if (!isWithinRenderBounds(blockPos, centerPos)) {
                continue; // Skip block entities outside the valid render area
            }

            if (blockEntity instanceof DoorBlockEntity || blockEntity instanceof ExteriorBlockEntity/* && blockPos == centerPos*/) continue;

            matrices.push();
            matrices.translate(
                    blockPos.getX() - centerPos.getX(),
                    blockPos.getY() - centerPos.getY(),
                    blockPos.getZ() - centerPos.getZ()
            );

            dispatcher.render(blockEntity, tickDelta, matrices, immediate);
            matrices.pop();
        }

        // Verify matrix stack is balanced
        assertEmpty(matrices);

        // Render all vertex consumer layers
    }

    /**
     * Determines if a block entity is within the valid render bounds
     */
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

    /**
     * Checks that the matrix stack is empty
     */
    private void assertEmpty(MatrixStack matrices) {
        if (!matrices.isEmpty()) {
            throw new IllegalStateException("Matrix stack not empty");
        }
    }

    /**
     * Rebuilds all geometry asynchronously with double-buffering
     */
    private void rebuildGeometry(World world, BlockPos centerPos, boolean checkBehindPortal) {
        buildFuture = CompletableFuture.runAsync(() -> {
            try {
                // DON'T clear old buffers yet - keep them for rendering
                // Build into a temporary map
                Map<ChunkSectionPos, Map<RenderLayer, VertexBuffer>> tempBuffers = Collections.synchronizedMap(new HashMap<>());

                List<BlockEntity> foundBlockEntities = new ArrayList<>();
                BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
                Random random = Random.create();

                // Calculate section bounds
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

                // Build each section into temporary buffers
                for (int sectionX = minSectionX; sectionX <= maxSectionX; sectionX++) {
                    for (int sectionY = minSectionY; sectionY <= maxSectionY; sectionY++) {
                        for (int sectionZ = minSectionZ; sectionZ <= maxSectionZ; sectionZ++) {
                            buildSectionToMap(world, centerPos, sectionX, sectionY, sectionZ,
                                    blockRenderManager, random, foundBlockEntities, tempBuffers, checkBehindPortal);
                        }
                    }
                }

                // Swap buffers on main thread - atomic operation
                MinecraftClient.getInstance().execute(() -> {
                    // Clean up OLD buffers
                    for (Map<RenderLayer, VertexBuffer> layerMap : sectionBuffers.values()) {
                        for (VertexBuffer vbo : layerMap.values()) {
                            vbo.close();
                        }
                    }
                    sectionBuffers.clear();

                    // Swap in NEW buffers
                    sectionBuffers.putAll(tempBuffers);

                    // Update block entities
                    blockEntities.clear();
                    blockEntities.addAll(foundBlockEntities);
                });

            } catch (Exception e) {
                AITMod.LOGGER.error("Failed to rebuild geometry", e);
            }
        });
    }

    /**
     * Builds geometry for a single 16x16x16 section into a target map
     */
    private void buildSectionToMap(World world, BlockPos centerPos, int sectionX, int sectionY, int sectionZ,
                                   BlockRenderManager blockRenderManager, Random random,
                                   List<BlockEntity> foundBlockEntities,
                                   Map<ChunkSectionPos, Map<RenderLayer, VertexBuffer>> targetMap, boolean checkBehindPortal) {

        Map<RenderLayer, BufferBuilder> builders = new HashMap<>();
        Set<RenderLayer> usedLayers = new HashSet<>();

        // Initialize buffer builders for each layer
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

        // Iterate through all blocks in section
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    mutablePos.set(x, y, z);

                    BlockState state = world.getBlockState(mutablePos);

                    // Skip air and black concrete (void)
                    if (state.isAir() || state.getBlock() == Blocks.BLACK_CONCRETE) {
                        continue;
                    }

                    // Calculate relative position
                    double relX = x - centerPos.getX();
                    double relY = y - centerPos.getY();
                    double relZ = z - centerPos.getZ();

                    // Inverted check: block behind door plane
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

                    // Skip fully surrounded blocks (optimization)
                    if (isFullySurrounded(world, mutablePos)) {
                        continue;
                    }

                    hasBlocks = true;

                    // Track block entities for later rendering
                    if (state.hasBlockEntity()) {
                        BlockEntity blockEntity = world.getBlockEntity(mutablePos);
                        if (blockEntity != null) {
                            synchronized (foundBlockEntities) {
                                foundBlockEntities.add(blockEntity);
                            }
                        }
                    }

                    // Render fluids
                    FluidState fluidState = state.getFluidState();
                    if (!fluidState.isEmpty()) {
                        RenderLayer fluidLayer = RenderLayers.getFluidLayer(fluidState); // Translucent fluid layer
                        BufferBuilder builder = builders.get(fluidLayer);
                        usedLayers.add(fluidLayer);

                        matrices.push();
                        matrices.translate(relX, relY, relZ); // Correctly position the fluid block
                        blockRenderManager.renderFluid(mutablePos, world, builder, state, fluidState); // Render fluid
                        matrices.pop();
                    }

                    // Render blocks
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

        // Upload to GPU and insert results into the target map on the main thread
        if (hasBlocks) {
            ChunkSectionPos sectionPos = ChunkSectionPos.from(sectionX, sectionY, sectionZ);

            Map<RenderLayer, BufferBuilder.BuiltBuffer> builtBuffers = new HashMap<>();
            for (RenderLayer layer : usedLayers) {
                BufferBuilder builder = builders.get(layer);
                BufferBuilder.BuiltBuffer builtBuffer = builder.end();
                builtBuffers.put(layer, builtBuffer);
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
        }
    }

    /**
     * Checks if a block is fully surrounded by opaque blocks (for culling)
     */
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

    /**
     * Cleans up resources - MUST be called when done
     */
    public void close() {
        for (Map<RenderLayer, VertexBuffer> layerMap : sectionBuffers.values()) {
            for (VertexBuffer vbo : layerMap.values()) {
                vbo.close();
            }
        }
        sectionBuffers.clear();
        blockEntities.clear();
    }

    /**
     * Gets the number of rendered sections
     */
    public int getSectionCount() {
        return sectionBuffers.size();
    }

    /**
     * Gets the number of rendered block entities
     */
    public int getBlockEntityCount() {
        return blockEntities.size();
    }
}