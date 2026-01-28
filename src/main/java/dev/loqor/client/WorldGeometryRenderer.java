package dev.loqor.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
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
    private Frustum frustum;

    // Door facing for culling
    private Direction doorFacing = Direction.NORTH;
    private Direction lastDoorFacing = null;

    // Cache for cross-dimensional rendering
    private ProxyClientWorld proxyWorld = null;
    private RegistryKey<World> lastDimensionKey = null;

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

    /**
     * Sets the door facing direction for culling (only marks dirty if it changes)
     */
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
     * Renders geometry from a different dimension using a ProxyWorld.
     * This is the main entry point for cross-dimensional BOTI rendering.
     * Works identically in both singleplayer and multiplayer.
     *
     * @param dimensionKey The dimension to render from
     * @param centerPos Center position for rendering
     * @param matrices View matrix transformations
     * @param tickDelta Partial tick for interpolation
     */
    public void renderFromDimension(RegistryKey<World> dimensionKey, BlockPos centerPos, MatrixStack matrices, float tickDelta) {
        // Create or update ProxyWorld for this dimension
        if (proxyWorld == null || !dimensionKey.equals(lastDimensionKey)) {
            proxyWorld = new ProxyClientWorld(dimensionKey);
            proxyWorld.setRenderer(this); // Link renderer for chunk update notifications
            lastDimensionKey = dimensionKey;
            markDirty(); // Rebuild when dimension changes
        }

        // Preload chunks before rendering (works for both singleplayer and multiplayer)
        preloadChunks(centerPos);

        // Render using the proxy world
        renderWithWorld(proxyWorld, centerPos, matrices, tickDelta);
    }

    /**
     * Preloads chunks around a center position to ensure they're available for rendering.
     * This is critical for cross-dimensional rendering where chunks may not be loaded yet.
     * Works identically in both singleplayer and multiplayer via packet system.
     *
     * @param centerPos Center position to preload around
     */
    private void preloadChunks(BlockPos centerPos) {
        if (proxyWorld == null) {
            return;
        }

        // Calculate chunk radius based on render distance
        int chunkRadius = (renderDistance >> 4) + 1; // Convert block distance to chunks + 1 for safety

        // Use ProxyClientWorld's optimized batch preloading
        proxyWorld.preloadChunks(centerPos, chunkRadius);
    }



    /**
     * Called when a chunk is loaded/unloaded in the proxy world to trigger a rebuild
     * if it's in our render area.
     *
     * @param chunkPos The chunk position that was updated
     * @param centerPos The current center position for rendering
     */
    public void onChunkUpdate(ChunkPos chunkPos, BlockPos centerPos) {
        if (centerPos == null || lastCenterPos == null) {
            return;
        }

        // Check if the updated chunk is within render distance
        int chunkRadius = (renderDistance >> 4) + 1;
        ChunkPos center = new ChunkPos(lastCenterPos);

        int dx = Math.abs(chunkPos.x - center.x);
        int dz = Math.abs(chunkPos.z - center.z);

        if (dx <= chunkRadius && dz <= chunkRadius) {
            markDirty(); // Chunk in view changed, rebuild needed
        }
    }

    /**
     * Internal render method that works with any BlockRenderView.
     * This allows rendering from either a real World or a ProxyClientWorld.
     *
     * @param blockView The block view to render from
     * @param centerPos Center position for rendering
     * @param matrices View matrix transformations
     * @param tickDelta Partial tick for interpolation
     */
    private void renderWithWorld(BlockRenderView blockView, BlockPos centerPos, MatrixStack matrices, float tickDelta) {
        if (projectionMatrix == null) {
            throw new IllegalStateException("Projection matrix not set! Call setProjectionMatrix() or setPerspectiveProjection() first.");
        }

        GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
        projectionMatrix = gameRenderer.getBasicProjectionMatrix(gameRenderer.getFov(gameRenderer.getCamera(),
                MinecraftClient.getInstance().getTickDelta(), true));

        // Create frustum for culling
        Matrix4f modelViewMatrix = matrices.peek().getPositionMatrix();
        this.frustum = new Frustum(modelViewMatrix, projectionMatrix);
        this.frustum.setPosition(centerPos.getX(), centerPos.getY(), centerPos.getZ());

        // Rebuild geometry if needed
        boolean shouldRebuild = needsRebuild || !centerPos.equals(lastCenterPos);
        if (shouldRebuild && (buildFuture == null || buildFuture.isDone())) {
            lastCenterPos = centerPos.toImmutable(); // Use toImmutable() to avoid mutations
            rebuildGeometryFromWorld(blockView, centerPos);
            // Don't reset needsRebuild here - let the rebuild completion do it
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
        RenderSystem.enableCull();

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
        RenderSystem.disableCull();
        RenderSystem.setProjectionMatrix(originalProjection, VertexSorter.BY_DISTANCE);
    }

    /**
     * Main render method - call this every frame
     *
     * @param world The world to render from
     * @param centerPos Center position (usually camera/player position)
     * @param matrices View matrix transformations
     * @param tickDelta Partial tick for interpolation
     */
    public void render(World world, BlockPos centerPos, MatrixStack matrices, float tickDelta) {
        renderWithWorld(world, centerPos, matrices, tickDelta);
    }

    /**
     * Renders terrain using VBOs
     */
    private void renderTerrain(MatrixStack matrices) {
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);

        for (RenderLayer layer : RenderLayer.getBlockLayers()) {
            if (layer == RenderLayer.getTranslucent()) {
                continue; // Skip translucent for now
            }

            layer.startDrawing();

            for (Map.Entry<ChunkSectionPos, Map<RenderLayer, VertexBuffer>> entry : sectionBuffers.entrySet()) {
                ChunkSectionPos sectionPos = entry.getKey();

                // Simple distance-based culling instead of frustum
                // Skip sections too far from center
                double dx = (sectionPos.getCenterPos().getX() - lastCenterPos.getX());
                double dy = (sectionPos.getCenterPos().getY() - lastCenterPos.getY());
                double dz = (sectionPos.getCenterPos().getZ() - lastCenterPos.getZ());
                double distSq = dx * dx + dy * dy + dz * dz;

                if (distSq > renderDistance * renderDistance * 256) { // 256 = 16^2 (section size)
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
    }

    /**
     * Checks if a chunk section is visible in the frustum
     */
    private boolean isSectionVisible(ChunkSectionPos sectionPos) {
        if (frustum == null) {
            return true; // No culling if frustum not set
        }

        // Calculate section bounds (16x16x16 block section)
        int minX = sectionPos.getMinX();
        int minY = sectionPos.getMinY();
        int minZ = sectionPos.getMinZ();
        int maxX = sectionPos.getMaxX();
        int maxY = sectionPos.getMaxY();
        int maxZ = sectionPos.getMaxZ();

        // Use Minecraft's frustum to test if the box is visible
        return frustum.isVisible(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * Renders block entities following Minecraft's WorldRenderer pattern
     */
    private void renderBlockEntities(MatrixStack matrices, float tickDelta, BlockPos centerPos) {
        MinecraftClient client = MinecraftClient.getInstance();
        BlockEntityRenderDispatcher dispatcher = client.getBlockEntityRenderDispatcher();
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();

        // Configure dispatcher
        dispatcher.configure(client.world, client.gameRenderer.getCamera(), null);

        double centerX = (double) centerPos.getX();
        double centerY = (double) centerPos.getY();
        double centerZ = (double) centerPos.getZ();

        // Create a snapshot to avoid concurrent modification
        List<BlockEntity> snapshot = new ArrayList<>(blockEntities);

        // Render each block entity
        for (BlockEntity blockEntity : snapshot) {
            BlockPos blockPos = blockEntity.getPos();

            matrices.push();
            matrices.translate(
                    (double) blockPos.getX() - centerX,
                    (double) blockPos.getY() - centerY,
                    (double) blockPos.getZ() - centerZ
            );

            dispatcher.render(blockEntity, tickDelta, matrices, immediate);

            matrices.pop();
        }

        // Verify matrix stack is balanced
        checkEmpty(matrices);

        // Draw all block entity render layers
        immediate.draw(RenderLayer.getSolid());
        immediate.draw(RenderLayer.getEndPortal());
        immediate.draw(RenderLayer.getEndGateway());
        immediate.draw(TexturedRenderLayers.getEntitySolid());
        immediate.draw(TexturedRenderLayers.getEntityCutout());
        immediate.draw(TexturedRenderLayers.getBeds());
        immediate.draw(TexturedRenderLayers.getShulkerBoxes());
        immediate.draw(TexturedRenderLayers.getSign());
        immediate.draw(TexturedRenderLayers.getHangingSign());
        immediate.draw(TexturedRenderLayers.getChest());
    }

    /**
     * Checks that the matrix stack is empty
     */
    private void checkEmpty(MatrixStack matrices) {
        if (!matrices.isEmpty()) {
            throw new IllegalStateException("Matrix stack not empty");
        }
    }

    /**
     * Rebuilds all geometry asynchronously from a BlockRenderView with double-buffering.
     * This version works with any BlockView, including ProxyClientWorld.
     */
    private void rebuildGeometryFromWorld(BlockRenderView blockView, BlockPos centerPos) {
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
                            buildSectionFromWorld(blockView, centerPos, sectionX, sectionY, sectionZ,
                                    blockRenderManager, random, foundBlockEntities, tempBuffers);
                        }
                    }
                }

                // Wait for all uploads to complete
                Thread.sleep(50); // Small delay to ensure all async uploads finish

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

                    // NOW reset the dirty flag after rebuild is complete
                    needsRebuild = false;
                });

            } catch (Exception e) {
                e.printStackTrace();
                // Reset flag even on error to avoid infinite loop
                MinecraftClient.getInstance().execute(() -> needsRebuild = false);
            }
        });
    }

    /**
     * Builds geometry for a single 16x16x16 section from a BlockRenderView into a target map.
     * This version works with any BlockView, including ProxyClientWorld.
     */
    private void buildSectionFromWorld(BlockRenderView blockView, BlockPos centerPos, int sectionX, int sectionY, int sectionZ,
                                       BlockRenderManager blockRenderManager, Random random,
                                       List<BlockEntity> foundBlockEntities,
                                       Map<ChunkSectionPos, Map<RenderLayer, VertexBuffer>> targetMap) {

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

                    BlockState state = blockView.getBlockState(mutablePos);

                    // Skip air and black concrete (void)
                    if (state.isAir() || state.getBlock() == Blocks.BLACK_CONCRETE) {
                        continue;
                    }

                    // Calculate relative position
                    double relX = x - centerPos.getX();
                    double relY = y - centerPos.getY();
                    double relZ = z - centerPos.getZ();

                    // **INVERTED: Check if block is BEHIND the door plane (opposite of door facing)**
                    // Since the door is "backwards", we want to show blocks on the opposite side
                    boolean behindDoor = switch (doorFacing) {
                        case NORTH -> relZ > 0.0;   // Door faces north, but show blocks toward +Z (south)
                        case SOUTH -> relZ < 0.0;  // Door faces south, but show blocks toward -Z (north)
                        case EAST -> relX < 0.0;   // Door faces east, but show blocks toward -X (west)
                        case WEST -> relX > 0.0;    // Door faces west, but show blocks toward +X (east)
                        default -> false;
                    };

                    if (behindDoor) {
                        continue; // Skip blocks behind the door
                    }

                    // Skip fully surrounded blocks (optimization)
                    if (isFullySurrounded(blockView, mutablePos)) {
                        continue;
                    }

                    hasBlocks = true;

                    // Track block entities for later rendering
                    if (state.hasBlockEntity()) {
                        BlockEntity blockEntity = blockView.getBlockEntity(mutablePos);
                        if (blockEntity != null) {
                            synchronized (foundBlockEntities) {
                                foundBlockEntities.add(blockEntity);
                            }
                        }
                    }

                    // Render fluids
                    FluidState fluidState = state.getFluidState();
                    if (!fluidState.isEmpty()) {
                        RenderLayer fluidLayer = RenderLayers.getFluidLayer(fluidState);
                        BufferBuilder builder = builders.get(fluidLayer);
                        usedLayers.add(fluidLayer);

                        matrices.push();
                        matrices.translate(relX, relY, relZ);
                        blockRenderManager.renderFluid(mutablePos, blockView, builder, state, fluidState);
                        matrices.pop();
                    }

                    // Render blocks
                    if (state.getRenderType() != BlockRenderType.INVISIBLE) {
                        RenderLayer blockLayer = RenderLayers.getBlockLayer(state);
                        BufferBuilder builder = builders.get(blockLayer);
                        usedLayers.add(blockLayer);

                        matrices.push();
                        matrices.translate(relX, relY, relZ);
                        blockRenderManager.renderBlock(state, mutablePos, blockView, matrices, builder, true, random);
                        matrices.pop();
                    }
                }
            }
        }

        // Upload to GPU if section has any blocks
        if (hasBlocks) {
            ChunkSectionPos sectionPos = ChunkSectionPos.from(sectionX, sectionY, sectionZ);

            Map<RenderLayer, BufferBuilder.BuiltBuffer> builtBuffers = new HashMap<>();
            for (RenderLayer layer : usedLayers) {
                BufferBuilder builder = builders.get(layer);
                BufferBuilder.BuiltBuffer builtBuffer = builder.end();
                builtBuffers.put(layer, builtBuffer);
            }

            // Upload on main thread and put into target map
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
    private boolean isFullySurrounded(BlockRenderView blockView, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos adjacent = pos.offset(dir);
            BlockState adjacentState = blockView.getBlockState(adjacent);
            if (!adjacentState.isOpaqueFullCube(blockView, adjacent)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a block is fully surrounded by opaque blocks (for culling) - World version
     */
    private boolean isFullySurrounded(World world, BlockPos pos) {
        return isFullySurrounded((BlockRenderView) world, pos);
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

        // Clean up proxy world resources
        if (proxyWorld != null) {
            proxyWorld.clearCache();
            proxyWorld = null;
        }
        lastDimensionKey = null;
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

    /**
     * Gets debug info about the proxy world state
     */
    public String getDebugInfo() {
        if (proxyWorld == null) {
            return "No proxy world";
        }
        return String.format("Proxy: %s | Cached: %d chunks | Pending: %d requests | Sections: %d",
                proxyWorld.getTargetDimension().getValue(),
                proxyWorld.getCachedChunkCount(),
                proxyWorld.getPendingRequestCount(),
                getSectionCount());
    }

    public void bobView(MatrixStack matrices, float tickDelta) {
        if (MinecraftClient.getInstance().getCameraEntity() instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)MinecraftClient.getInstance().getCameraEntity();
            float f = playerEntity.horizontalSpeed - playerEntity.prevHorizontalSpeed;
            float g = -(playerEntity.horizontalSpeed + f * tickDelta);
            float h = MathHelper.lerp(tickDelta, playerEntity.prevStrideDistance, playerEntity.strideDistance);
            matrices.translate(MathHelper.sin(g * (float)Math.PI) * h * 0.5F, -Math.abs(MathHelper.cos(g * (float)Math.PI) * h), 0.0F);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.sin(g * (float)Math.PI) * h * 3.0F));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Math.abs(MathHelper.cos(g * (float)Math.PI - 0.2F) * h) * 5.0F));
        }
    }
}