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
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Renders the slice of the exterior world a TARDIS is standing in, as seen through the interior door.
 * <p>
 * The exterior surroundings live in a {@link ClientWorld shadow world} (see {@link PortalData}); this class turns
 * the blocks/entities/particles in front of the exterior door into geometry and draws them inside the doorway's
 * stencil region. Everything is drawn through a single {@linkplain #buildPortalView portal view matrix} so terrain,
 * block entities, entities, particles and the sky all share one camera - which is what was previously broken
 * (terrain used an identity view while entities were flushed with the interior camera's matrix, so they never
 * lined up).
 */
public class WorldGeometryRenderer {
    /** How many sections to (re)build per dispatch. Keeps each off-thread batch small so a hitch never stalls. */
    private static final int BUILD_BUDGET = 6;

    /** Big stack so deep block-model / biome-colour recursion can't overflow the build thread (the old cause of the
     * silently-swallowed StackOverflowError that left chunks unbuilt). */
    private static final long BUILD_THREAD_STACK = 32L * 1024 * 1024;

    private final Map<ChunkSectionPos, Map<RenderLayer, VertexBuffer>> sectionBuffers = new HashMap<>();
    private final Map<ChunkSectionPos, List<BlockEntity>> sectionBlockEntities = new HashMap<>();

    // Sections whose geometry changed since the last build. Drained a budget at a time so a single block/light/chunk
    // update only rebuilds the affected sections instead of the whole render volume.
    private final Set<ChunkSectionPos> dirtySections = ConcurrentHashMap.newKeySet();
    private boolean needsFullRebuild = true;

    // Per-section failure counter. A build that throws (usually a transient race with chunk streaming) is retried a
    // few times, then dropped so a permanently-bad section can't spin the builder or spam the log forever. Fresh
    // data for a section (markSectionDirty) clears its count so it gets a clean shot.
    private static final int MAX_BUILD_ATTEMPTS = 3;
    private final Map<ChunkSectionPos, Integer> buildAttempts = new ConcurrentHashMap<>();

    private CompletableFuture<Void> buildFuture = null;

    /** Dedicated single-thread builder with a large stack; serialised so our builds never race each other. */
    private final ExecutorService buildExecutor = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(null, runnable, "BOTI-Geometry-Builder", BUILD_THREAD_STACK);
        thread.setDaemon(true);
        thread.setPriority(Thread.MIN_PRIORITY);
        return thread;
    });

    private final int renderDistance;

    private Direction doorFacing = Direction.NORTH;
    private Direction lastDoorFacing = null;

    // Frame-local view state, set at the top of every render() so the cull helpers and draw passes agree.
    private BlockPos centerPos = BlockPos.ORIGIN;
    private BlockPos lastBuiltCenter = null;
    private Matrix4f portalView = new Matrix4f();
    private Matrix4f portalProjection = new Matrix4f();
    private Frustum frustum = null;

    public WorldGeometryRenderer(int renderDistance) {
        this.renderDistance = renderDistance;
    }

    /** Forces a full rebuild of the whole render volume (used for the first build, dimension reset, facing change). */
    public void markDirty() {
        this.needsFullRebuild = true;
    }

    /** Queues just one section for rebuilding - the cheap path for block/light/chunk updates. */
    public void markSectionDirty(ChunkSectionPos pos) {
        this.buildAttempts.remove(pos); // fresh data - give it a clean set of retries
        this.dirtySections.add(pos);
    }

    public void setDoorFacing(Direction facing) {
        if (this.lastDoorFacing != facing) {
            markDirty();
        }
        this.doorFacing = facing;
        this.lastDoorFacing = facing;
    }

    /**
     * @param eyeRelToCenter the portal camera's eye position, expressed relative to {@code centerPos} (the exterior
     *                       block). This is the player's eye mapped through the doorway into the exterior world.
     * @param portalYaw      the portal camera's yaw (the player's yaw rotated by the door's turn through the portal)
     * @param portalPitch    the portal camera's pitch (matches the player's pitch)
     */
    public void render(UUID id, ClientWorld portalWorld, BlockPos centerPos, Vec3d eyeRelToCenter,
                       float portalYaw, float portalPitch, float tickDelta, boolean checkBehindPortal) {
        this.centerPos = centerPos;

        // Geometry is stored relative to centerPos, so if the exterior block moved (e.g. the TARDIS re-landed) the
        // whole volume has to be rebuilt around the new origin or it would draw offset.
        if (!centerPos.equals(this.lastBuiltCenter)) {
            this.lastBuiltCenter = centerPos.toImmutable();
            markDirty();
        }

        MinecraftClient client = MinecraftClient.getInstance();
        GameRenderer gameRenderer = client.gameRenderer;
        this.portalProjection = gameRenderer.getBasicProjectionMatrix(
                gameRenderer.getFov(gameRenderer.getCamera(), tickDelta, true));

        Matrix4f portalRot = buildPortalRotation(portalYaw, portalPitch);
        this.portalView = buildPortalView(portalRot, eyeRelToCenter);

        // Vanilla's Frustum convention: planes from a rotation-only view, boxes offset by the camera position. Our
        // boxes are expressed relative to centerPos, so the camera position is eyeRelToCenter.
        this.frustum = new Frustum(portalRot, portalProjection);
        this.frustum.setPosition(eyeRelToCenter.x, eyeRelToCenter.y, eyeRelToCenter.z);

        pumpBuilds(portalWorld, checkBehindPortal);

        // A throwaway camera parked at centerPos, facing the way the portal looks. Used for particle billboarding,
        // entity name-tag orientation and the sky pass. Keeping its position at centerPos (not the real eye) makes
        // particles centerPos-relative, exactly like terrain and entities, so one model-view matrix covers them all.
        Camera portalCamera = new Camera();
        portalCamera.setPos(centerPos.getX(), centerPos.getY(), centerPos.getZ());
        portalCamera.setRotation(portalYaw, portalPitch);

        Matrix4f originalProjection = new Matrix4f(RenderSystem.getProjectionMatrix());
        RenderSystem.setProjectionMatrix(portalProjection, VertexSorter.BY_DISTANCE);

        // Sky sits at infinity, so it only takes the rotation (no eye translation) and never writes depth.
        renderSky(id, portalWorld, portalRot, portalCamera, tickDelta);

        MatrixStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.push();
        modelViewStack.peek().getPositionMatrix().set(portalView);
        modelViewStack.peek().getNormalMatrix().set(new Matrix3f(portalView));
        RenderSystem.applyModelViewMatrix();

        if (!sectionBuffers.isEmpty()) {
            renderTerrain();
        }
        renderBlockEntities(portalWorld, tickDelta, portalCamera);
        renderEntities(portalWorld, tickDelta, portalCamera);
        renderParticles(id, portalCamera, tickDelta);

        modelViewStack.pop();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(originalProjection, VertexSorter.BY_DISTANCE);
    }

    /** The world-relative rotation part of the portal camera (pitch then yaw, vanilla camera convention). */
    private static Matrix4f buildPortalRotation(float yaw, float pitch) {
        return new Matrix4f()
                .rotateX((float) Math.toRadians(pitch))
                .rotateY((float) Math.toRadians(yaw + 180.0f));
    }

    /** The full portal view matrix: rotation, then translate the world so the eye sits at the origin. */
    private static Matrix4f buildPortalView(Matrix4f portalRotation, Vec3d eyeRelToCenter) {
        return new Matrix4f(portalRotation).translate(
                (float) -eyeRelToCenter.x, (float) -eyeRelToCenter.y, (float) -eyeRelToCenter.z);
    }

    // ===== Geometry build scheduling =====

    private void pumpBuilds(World world, boolean checkBehindPortal) {
        boolean idle = buildFuture == null || buildFuture.isDone();
        if (!idle)
            return;

        if (needsFullRebuild) {
            needsFullRebuild = false;
            enqueueVolume();
        }

        if (dirtySections.isEmpty())
            return;

        List<ChunkSectionPos> batch = drainBatch(BUILD_BUDGET);
        if (!batch.isEmpty())
            dispatchBuild(world, batch, checkBehindPortal);
    }

    /** Adds every section in the current render volume to the dirty set and drops buffers that fell out of it. */
    private void enqueueVolume() {
        buildAttempts.clear(); // a full rebuild gives every section a fresh set of retries
        Set<ChunkSectionPos> volume = computeVolumeSections();

        Iterator<Map.Entry<ChunkSectionPos, Map<RenderLayer, VertexBuffer>>> it = sectionBuffers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<ChunkSectionPos, Map<RenderLayer, VertexBuffer>> entry = it.next();
            if (!volume.contains(entry.getKey())) {
                for (VertexBuffer vbo : entry.getValue().values())
                    vbo.close();
                sectionBlockEntities.remove(entry.getKey());
                it.remove();
            }
        }

        dirtySections.addAll(volume);
    }

    /** Pulls up to {@code budget} in-volume sections from the dirty set, nearest first; drops the rest. */
    private List<ChunkSectionPos> drainBatch(int budget) {
        List<ChunkSectionPos> candidates = new ArrayList<>();

        Iterator<ChunkSectionPos> it = dirtySections.iterator();
        while (it.hasNext()) {
            ChunkSectionPos pos = it.next();
            if (!isSectionInVolume(pos)) {
                it.remove(); // never going to be visible - forget about it
                continue;
            }
            candidates.add(pos);
        }

        candidates.sort(Comparator.comparingDouble(this::sectionDistanceSq));

        List<ChunkSectionPos> batch = new ArrayList<>(Math.min(budget, candidates.size()));
        for (ChunkSectionPos pos : candidates) {
            if (batch.size() >= budget)
                break;
            batch.add(pos);
            dirtySections.remove(pos);
        }

        return batch;
    }

    private void dispatchBuild(World world, List<ChunkSectionPos> batch, boolean checkBehindPortal) {
        buildFuture = CompletableFuture.runAsync(() -> {
            BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
            Random random = Random.create();

            List<SectionResult> results = new ArrayList<>(batch.size());
            for (ChunkSectionPos sectionPos : batch) {
                try {
                    results.add(buildSection(world, sectionPos, blockRenderManager, random, checkBehindPortal));
                } catch (Throwable t) {
                    // One bad section (e.g. boundary data not streamed yet) must not sink the whole batch, and a
                    // StackOverflowError is a Throwable, not an Exception - catch it so it can't be swallowed by the
                    // CompletableFuture and quietly leave the doorway blank.
                    int attempts = buildAttempts.merge(sectionPos, 1, Integer::sum);
                    if (attempts == 1)
                        AITMod.LOGGER.error("BOTI: failed to build section {} (attempt {})", sectionPos, attempts, t);

                    if (attempts < MAX_BUILD_ATTEMPTS)
                        dirtySections.add(sectionPos); // transient - retry on a later frame
                }
            }

            MinecraftClient.getInstance().execute(() -> {
                for (SectionResult result : results)
                    applySection(result);
            });
        }, buildExecutor);
    }

    // ===== Volume / culling helpers (all in centerPos-relative space) =====

    private Set<ChunkSectionPos> computeVolumeSections() {
        Set<ChunkSectionPos> sections = new HashSet<>();

        int minSectionX = (centerPos.getX() - renderDistance) >> 4;
        int minSectionY = (centerPos.getY() - renderDistance) >> 4;
        int minSectionZ = (centerPos.getZ() - renderDistance) >> 4;
        int maxSectionX = (centerPos.getX() + renderDistance) >> 4;
        int maxSectionY = (centerPos.getY() + renderDistance) >> 4;
        int maxSectionZ = (centerPos.getZ() + renderDistance) >> 4;

        for (int x = minSectionX; x <= maxSectionX; x++)
            for (int y = minSectionY; y <= maxSectionY; y++)
                for (int z = minSectionZ; z <= maxSectionZ; z++) {
                    ChunkSectionPos pos = ChunkSectionPos.from(x, y, z);
                    if (isSectionInVolume(pos))
                        sections.add(pos);
                }

        return sections;
    }

    /** A section is in the volume if it is within render distance and not entirely behind the door plane. */
    private boolean isSectionInVolume(ChunkSectionPos pos) {
        double dx = pos.getMinX() + 8 - centerPos.getX();
        double dy = pos.getMinY() + 8 - centerPos.getY();
        double dz = pos.getMinZ() + 8 - centerPos.getZ();

        double reach = renderDistance + 16.0;
        if (dx * dx + dy * dy + dz * dz > reach * reach)
            return false;

        Vec3i normal = doorFacing.getVector();
        double inFront = dx * normal.getX() + dy * normal.getY() + dz * normal.getZ();
        return inFront > -16.0; // keep sections straddling the door plane
    }

    private double sectionDistanceSq(ChunkSectionPos pos) {
        double dx = pos.getMinX() + 8 - centerPos.getX();
        double dy = pos.getMinY() + 8 - centerPos.getY();
        double dz = pos.getMinZ() + 8 - centerPos.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    /** Frustum test against the portal camera so only sections actually visible through the doorway are drawn. */
    private boolean isSectionVisible(ChunkSectionPos pos) {
        if (frustum == null)
            return true;

        double minX = pos.getMinX() - centerPos.getX();
        double minY = pos.getMinY() - centerPos.getY();
        double minZ = pos.getMinZ() - centerPos.getZ();

        return frustum.isVisible(new Box(minX, minY, minZ, minX + 16, minY + 16, minZ + 16));
    }

    // ===== Draw passes =====

    private void renderTerrain() {
        // Frustum-cull once per frame, then reuse the survivors for every render layer (instead of re-testing each
        // section seven times over).
        List<Map<RenderLayer, VertexBuffer>> visible = new ArrayList<>();
        for (Map.Entry<ChunkSectionPos, Map<RenderLayer, VertexBuffer>> entry : sectionBuffers.entrySet()) {
            if (isSectionVisible(entry.getKey()))
                visible.add(entry.getValue());
        }

        if (visible.isEmpty())
            return;

        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        for (RenderLayer layer : RenderLayer.getBlockLayers()) {
            if (layer == RenderLayer.getTranslucent())
                continue;

            drawLayer(layer, visible);
        }

        drawLayer(RenderLayer.getTranslucent(), visible);

        RenderSystem.disableBlend();
    }

    private void drawLayer(RenderLayer layer, List<Map<RenderLayer, VertexBuffer>> visible) {
        layer.startDrawing();

        for (Map<RenderLayer, VertexBuffer> layerBuffers : visible) {
            VertexBuffer vbo = layerBuffers.get(layer);
            if (vbo != null) {
                vbo.bind();
                vbo.draw(portalView, portalProjection, RenderSystem.getShader());
            }
        }

        VertexBuffer.unbind();
        layer.endDrawing();
    }

    /**
     * Draws the exterior dimension's sky into the doorway so it shows the sky for wherever the TARDIS actually is.
     * <p>
     * The doorway is already cleared to the exterior dimension's real sky colour (see {@code TardisDoorBOTI}), which
     * is the bulk of the look - the right blue for an overworld day, dark for night, the nether's red murk, the End's
     * void, etc. On top of that we ask the shadow world's own {@link WorldRenderer} (it mirrors the right dimension)
     * to draw the celestial bodies. Note vanilla {@code renderSky} keys its sky <em>type</em> off the player's own
     * dimension, so sun/moon/stars only appear when the interior has a vanilla sky type; the colour is always right.
     * Guarded so a sky hiccup can never take down the rest of the portal render.
     */
    private void renderSky(UUID id, ClientWorld portalWorld, Matrix4f portalRotation, Camera portalCamera,
                           float tickDelta) {
        PortalData data = PortalDataManager.get(id);
        if (data == null || data.renderer() == null)
            return;

        try {
            MatrixStack skyStack = new MatrixStack();
            skyStack.multiplyPositionMatrix(portalRotation);

            RenderSystem.depthMask(false);
            data.renderer().renderSky(skyStack, portalProjection, tickDelta, portalCamera, false, () -> {
            });
        } catch (Throwable t) {
            AITMod.LOGGER.error("BOTI: failed to render exterior sky", t);
        } finally {
            // Vanilla renderSky leaves these in various states; reset to sane terrain defaults.
            RenderSystem.depthMask(true);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setProjectionMatrix(portalProjection, VertexSorter.BY_DISTANCE);
        }
    }

    private void renderBlockEntities(ClientWorld portalWorld, float tickDelta, Camera portalCamera) {
        MinecraftClient client = MinecraftClient.getInstance();
        BlockEntityRenderDispatcher dispatcher = client.getBlockEntityRenderDispatcher();
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();

        dispatcher.configure(portalWorld, portalCamera, client.crosshairTarget);

        MatrixStack matrices = new MatrixStack();
        List<BlockEntity> snapshot = new ArrayList<>();
        for (List<BlockEntity> sectionEntities : sectionBlockEntities.values())
            snapshot.addAll(sectionEntities);

        for (BlockEntity blockEntity : snapshot) {
            BlockPos blockPos = blockEntity.getPos();

            if (!isWithinRenderBounds(blockPos))
                continue;
            if (blockEntity instanceof DoorBlockEntity || blockEntity instanceof ExteriorBlockEntity)
                continue;

            matrices.push();
            matrices.translate(
                    blockPos.getX() - centerPos.getX(),
                    blockPos.getY() - centerPos.getY(),
                    blockPos.getZ() - centerPos.getZ());

            dispatcher.render(blockEntity, tickDelta, matrices, immediate);
            matrices.pop();
        }

        immediate.draw();
    }

    private void renderEntities(ClientWorld portalWorld, float tickDelta, Camera portalCamera) {
        MinecraftClient client = MinecraftClient.getInstance();
        EntityRenderDispatcher dispatcher = client.getEntityRenderDispatcher();
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();

        dispatcher.configure(portalWorld, portalCamera, client.targetedEntity);

        MatrixStack matrices = new MatrixStack();

        for (Entity entity : portalWorld.getEntities()) {
            if (entity == null || !isWithinRenderBounds(entity.getBlockPos()))
                continue;

            // Interpolated render position, expressed relative to the portal centre (same convention as terrain and
            // block entities). The dispatcher translates the matrix stack by these coordinates internally; the shared
            // portal view matrix (set as the global model-view) then maps them onto the doorway.
            double x = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX()) - centerPos.getX();
            double y = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY()) - centerPos.getY();
            double z = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ()) - centerPos.getZ();
            float yaw = MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw());

            int light = dispatcher.getLight(entity, tickDelta);
            dispatcher.render(entity, x, y, z, yaw, tickDelta, matrices, immediate, light);
        }

        immediate.draw();
    }

    private void renderParticles(UUID id, Camera portalCamera, float tickDelta) {
        PortalParticleManager manager = PortalDataManager.particles(id);
        if (manager == null)
            return;

        MinecraftClient client = MinecraftClient.getInstance();
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();

        // Particles bill-board relative to the camera; with the camera parked at centerPos they come out
        // centerPos-relative, matching the terrain and entities under the shared portal view matrix.
        manager.renderParticles(new MatrixStack(), immediate, client.gameRenderer.getLightmapTextureManager(),
                portalCamera, tickDelta);
        immediate.draw();
    }

    private boolean isWithinRenderBounds(BlockPos blockPos) {
        return blockPos.getX() >= centerPos.getX() - renderDistance
                && blockPos.getX() <= centerPos.getX() + renderDistance
                && blockPos.getY() >= centerPos.getY() - renderDistance
                && blockPos.getY() <= centerPos.getY() + renderDistance
                && blockPos.getZ() >= centerPos.getZ() - renderDistance
                && blockPos.getZ() <= centerPos.getZ() + renderDistance;
    }

    // ===== Section building (off-thread; no GL here) =====

    private SectionResult buildSection(World world, ChunkSectionPos sectionPos,
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

                    if (state.isAir() || state.getBlock() == Blocks.BLACK_CONCRETE)
                        continue;

                    double relX = x - centerPos.getX();
                    double relY = y - centerPos.getY();
                    double relZ = z - centerPos.getZ();

                    if (checkBehindPortal && isBehindPortal(relX, relY, relZ))
                        continue;

                    if (isFullySurrounded(world, mutablePos))
                        continue;

                    hasBlocks = true;

                    if (state.hasBlockEntity()) {
                        BlockEntity blockEntity = world.getBlockEntity(mutablePos);
                        if (blockEntity != null)
                            foundBlockEntities.add(blockEntity);
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

            if (hasBlocks && usedLayers.contains(layer))
                builtBuffers.put(layer, built);
            else
                built.release();
        }

        return new SectionResult(sectionPos, builtBuffers, foundBlockEntities);
    }

    /** True for blocks on the far side of the door plane - they can never be seen through the doorway. */
    private boolean isBehindPortal(double relX, double relY, double relZ) {
        Vec3i normal = doorFacing.getVector();
        return relX * normal.getX() + relY * normal.getY() + relZ * normal.getZ() > 0.0;
    }

    /** Uploads one section's freshly-built buffers, replacing (or removing) whatever was there before. */
    private void applySection(SectionResult result) {
        ChunkSectionPos pos = result.pos();
        buildAttempts.remove(pos); // built cleanly

        Map<RenderLayer, VertexBuffer> old = sectionBuffers.remove(pos);
        if (old != null) {
            for (VertexBuffer vbo : old.values())
                vbo.close();
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

        if (result.blockEntities().isEmpty())
            sectionBlockEntities.remove(pos);
        else
            sectionBlockEntities.put(pos, result.blockEntities());
    }

    private void clearBuffers() {
        for (Map<RenderLayer, VertexBuffer> layerMap : sectionBuffers.values()) {
            for (VertexBuffer vbo : layerMap.values())
                vbo.close();
        }

        sectionBuffers.clear();
        sectionBlockEntities.clear();
    }

    private boolean isFullySurrounded(World world, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos adjacent = pos.offset(dir);
            BlockState adjacentState = world.getBlockState(adjacent);
            if (!adjacentState.isOpaqueFullCube(world, adjacent))
                return false;
        }
        return true;
    }

    public void close() {
        clearBuffers();
        buildExecutor.shutdownNow();
    }

    public int getSectionCount() {
        return sectionBuffers.size();
    }

    public int getBlockEntityCount() {
        int count = 0;
        for (List<BlockEntity> sectionEntities : sectionBlockEntities.values())
            count += sectionEntities.size();
        return count;
    }

    private record SectionResult(ChunkSectionPos pos, Map<RenderLayer, BufferBuilder.BuiltBuffer> buffers,
                                 List<BlockEntity> blockEntities) {
    }
}
