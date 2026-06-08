package dev.loqor.portal.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import dev.amble.ait.AITMod;
import dev.amble.ait.client.boti.PortalParticleManager;
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
import java.util.concurrent.ConcurrentHashMap;

public class WorldGeometryRenderer {
    private final Map<ChunkSectionPos, Map<RenderLayer, VertexBuffer>> sectionBuffers = new HashMap<>();
    private final Map<ChunkSectionPos, List<BlockEntity>> sectionBlockEntities = new HashMap<>();

    // Sections whose geometry changed since the last build. Drained incrementally so a single block/light/chunk
    // update only rebuilds the affected sections instead of the whole render volume.
    private final Set<ChunkSectionPos> dirtySections = ConcurrentHashMap.newKeySet();
    private boolean needsFullRebuild = true;

    private CompletableFuture<Void> buildFuture = null;
    private BlockPos lastCenterPos = null;

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

    /** Forces a full rebuild of the whole render volume (used for the first build and on a dimension reset). */
    public void markDirty() {
        this.needsFullRebuild = true;
    }

    /** Queues just one section for rebuilding - the cheap path for block/light/chunk updates. */
    public void markSectionDirty(ChunkSectionPos pos) {
        this.dirtySections.add(pos);
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

        lastCenterPos = centerPos;

        boolean idle = buildFuture == null || buildFuture.isDone();
        if (needsFullRebuild && idle) {
            needsFullRebuild = false;
            dirtySections.clear();
            rebuildGeometry(portalWorld, centerPos, checkBehindPortal);
        } else if (!dirtySections.isEmpty() && idle) {
            Set<ChunkSectionPos> toBuild = new HashSet<>(dirtySections);
            dirtySections.removeAll(toBuild);
            rebuildSections(portalWorld, centerPos, toBuild, checkBehindPortal);
        }

        Matrix4f originalProjection = new Matrix4f(RenderSystem.getProjectionMatrix());

        RenderSystem.setProjectionMatrix(projectionMatrix, VertexSorter.BY_DISTANCE);
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);

        renderBlockEntities(portalWorld, matrices, tickDelta, centerPos);
        renderEntities(portalWorld, matrices, tickDelta, centerPos);
        renderParticles(id, matrices, tickDelta, centerPos);

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

                if (isSectionCulled(sectionPos)) {
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

            if (isSectionCulled(sectionPos)) {
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

    private boolean isSectionCulled(ChunkSectionPos sectionPos) {
        double dx = sectionPos.getCenterPos().getX() - lastCenterPos.getX();
        double dy = sectionPos.getCenterPos().getY() - lastCenterPos.getY();
        double dz = sectionPos.getCenterPos().getZ() - lastCenterPos.getZ();

        return dx * dx + dy * dy + dz * dz > renderDistance * renderDistance * 256;
    }

    private void renderBlockEntities(ClientWorld portalWorld, MatrixStack matrices, float tickDelta, BlockPos centerPos) {
        MinecraftClient client = MinecraftClient.getInstance();
        BlockEntityRenderDispatcher dispatcher = client.getBlockEntityRenderDispatcher();
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();

        dispatcher.configure(portalWorld, client.gameRenderer.getCamera(), client.crosshairTarget);

        List<BlockEntity> snapshot = new ArrayList<>();
        for (List<BlockEntity> sectionEntities : sectionBlockEntities.values()) {
            snapshot.addAll(sectionEntities);
        }

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

    private void renderParticles(UUID id, MatrixStack matrices, float tickDelta, BlockPos centerPos) {
        PortalParticleManager manager = PortalDataManager.particles(id);
        if (manager == null)
            return;

        MinecraftClient client = MinecraftClient.getInstance();
        Camera realCamera = client.gameRenderer.getCamera();

        // ParticleManager draws particles relative to the camera's position, so park a throwaway camera at the
        // portal centre (matching the player's facing). That lines particles up with the terrain and entities,
        // which are all drawn relative to centerPos.
        Camera portalCamera = new Camera();
        portalCamera.setPos(centerPos.getX(), centerPos.getY(), centerPos.getZ());
        portalCamera.setRotation(realCamera.getYaw(), realCamera.getPitch());

        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();

        MatrixStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.push();
        manager.renderParticles(matrices, immediate, client.gameRenderer.getLightmapTextureManager(),
                portalCamera, tickDelta);
        modelViewStack.pop();
        RenderSystem.applyModelViewMatrix();
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
        rebuildSections(world, centerPos, sectionsInVolume(centerPos), checkBehindPortal, true);
    }

    private void rebuildSections(World world, BlockPos centerPos, Set<ChunkSectionPos> sections, boolean checkBehindPortal) {
        rebuildSections(world, centerPos, sections, checkBehindPortal, false);
    }

    private void rebuildSections(World world, BlockPos centerPos, Set<ChunkSectionPos> sections,
                                 boolean checkBehindPortal, boolean full) {
        buildFuture = CompletableFuture.runAsync(() -> {
            try {
                BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
                Random random = Random.create();

                List<SectionResult> results = new ArrayList<>();
                for (ChunkSectionPos sectionPos : sections) {
                    results.add(buildSection(world, centerPos, sectionPos, blockRenderManager, random, checkBehindPortal));
                }

                MinecraftClient.getInstance().execute(() -> {
                    if (full) {
                        clearBuffers();
                    }

                    for (SectionResult result : results) {
                        applySection(result);
                    }
                });
            } catch (Exception e) {
                AITMod.LOGGER.error("Failed to rebuild geometry", e);
            }
        });
    }

    private Set<ChunkSectionPos> sectionsInVolume(BlockPos centerPos) {
        Set<ChunkSectionPos> sections = new HashSet<>();

        int minSectionX = (centerPos.getX() - renderDistance) >> 4;
        int minSectionY = (centerPos.getY() - renderDistance) >> 4;
        int minSectionZ = (centerPos.getZ() - renderDistance) >> 4;
        int maxSectionX = (centerPos.getX() + renderDistance) >> 4;
        int maxSectionY = (centerPos.getY() + renderDistance) >> 4;
        int maxSectionZ = (centerPos.getZ() + renderDistance) >> 4;

        for (int sectionX = minSectionX; sectionX <= maxSectionX; sectionX++) {
            for (int sectionY = minSectionY; sectionY <= maxSectionY; sectionY++) {
                for (int sectionZ = minSectionZ; sectionZ <= maxSectionZ; sectionZ++) {
                    sections.add(ChunkSectionPos.from(sectionX, sectionY, sectionZ));
                }
            }
        }

        return sections;
    }

    /** Builds one section's geometry off-thread. Returns CPU buffers + block entities; no GL work happens here. */
    private SectionResult buildSection(World world, BlockPos centerPos, ChunkSectionPos sectionPos,
                                       BlockRenderManager blockRenderManager, Random random, boolean checkBehindPortal) {

        Map<RenderLayer, BufferBuilder> builders = new HashMap<>();
        Set<RenderLayer> usedLayers = new HashSet<>();

        for (RenderLayer layer : RenderLayer.getBlockLayers()) {
            BufferBuilder builder = new BufferBuilder(layer.getExpectedBufferSize());
            builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
            builders.put(layer, builder);
        }

        MatrixStack matrices = new MatrixStack();
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        List<BlockEntity> foundBlockEntities = new ArrayList<>();

        int startX = sectionPos.getMinX();
        int startY = sectionPos.getMinY();
        int startZ = sectionPos.getMinZ();
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
                            foundBlockEntities.add(blockEntity);
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

        Map<RenderLayer, BufferBuilder.BuiltBuffer> builtBuffers = new HashMap<>();

        for (RenderLayer layer : RenderLayer.getBlockLayers()) {
            BufferBuilder.BuiltBuffer built = builders.get(layer).end();

            if (hasBlocks && usedLayers.contains(layer)) {
                builtBuffers.put(layer, built);
            } else {
                built.release();
            }
        }

        return new SectionResult(sectionPos, builtBuffers, foundBlockEntities);
    }

    /** Uploads one section's freshly-built buffers, replacing (or removing) whatever was there before. */
    private void applySection(SectionResult result) {
        ChunkSectionPos pos = result.pos();

        Map<RenderLayer, VertexBuffer> old = sectionBuffers.remove(pos);
        if (old != null) {
            for (VertexBuffer vbo : old.values()) {
                vbo.close();
            }
        }

        if (result.buffers().isEmpty()) {
            sectionBlockEntities.remove(pos);
            return;
        }

        Map<RenderLayer, VertexBuffer> layerBuffers = new HashMap<>();
        for (Map.Entry<RenderLayer, BufferBuilder.BuiltBuffer> entry : result.buffers().entrySet()) {
            VertexBuffer vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);
            vbo.bind();
            vbo.upload(entry.getValue());
            VertexBuffer.unbind();

            layerBuffers.put(entry.getKey(), vbo);
        }

        sectionBuffers.put(pos, layerBuffers);

        if (result.blockEntities().isEmpty()) {
            sectionBlockEntities.remove(pos);
        } else {
            sectionBlockEntities.put(pos, result.blockEntities());
        }
    }

    private void clearBuffers() {
        for (Map<RenderLayer, VertexBuffer> layerMap : sectionBuffers.values()) {
            for (VertexBuffer vbo : layerMap.values()) {
                vbo.close();
            }
        }

        sectionBuffers.clear();
        sectionBlockEntities.clear();
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
        clearBuffers();
    }

    public int getSectionCount() {
        return sectionBuffers.size();
    }

    public int getBlockEntityCount() {
        int count = 0;
        for (List<BlockEntity> sectionEntities : sectionBlockEntities.values()) {
            count += sectionEntities.size();
        }
        return count;
    }

    private record SectionResult(ChunkSectionPos pos, Map<RenderLayer, BufferBuilder.BuiltBuffer> buffers,
                                 List<BlockEntity> blockEntities) {
    }
}
