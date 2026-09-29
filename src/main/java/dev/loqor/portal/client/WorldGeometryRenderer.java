package dev.loqor.portal.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import dev.amble.ait.AITMod;
import dev.amble.ait.client.boti.PortalParticleManager;
import dev.amble.ait.core.AITDimensions;
import dev.amble.ait.core.blockentities.DoorBlockEntity;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.world.TardisServerWorld;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.ColorResolver;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorldGeometryRenderer {
    private static final int BUILD_BUDGET = 6;

    private static final long LIGHT_BUDGET_NANOS = 2_000_000L;

    private static final int POOL_RETAIN_FRAMES = 600;
    private int idleFrames = 0;

    private long lastRenderNanos = 0L;

    private boolean skyPassErrorLogged = false;

    private boolean skyInjectErrorLogged = false;

    private static final long BUILD_THREAD_STACK = 32L * 1024 * 1024;

    private final Map<ChunkSectionPos, Map<RenderLayer, VertexBuffer>> sectionBuffers = new HashMap<>();
    private final Map<ChunkSectionPos, List<BlockEntity>> sectionBlockEntities = new HashMap<>();

    private final Set<ChunkSectionPos> dirtySections = ConcurrentHashMap.newKeySet();
    private boolean needsFullRebuild = true;

    private static final int MAX_BUILD_ATTEMPTS = 3;
    private final Map<ChunkSectionPos, Integer> buildAttempts = new ConcurrentHashMap<>();

    private CompletableFuture<Void> buildFuture = null;

    private volatile boolean closed = false;

    private final List<Map<RenderLayer, BufferBuilder>> builderPool = new ArrayList<>();

    private final ExecutorService buildExecutor = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(null, runnable, "BOTI-Geometry-Builder", BUILD_THREAD_STACK);
        thread.setDaemon(true);
        thread.setPriority(Thread.MIN_PRIORITY);
        return thread;
    });

    private final int renderDistance;

    private Vec3d doorNormal = new Vec3d(0, 0, -1);
    private Vec3d lastDoorNormal = null;

    private BlockPos centerPos = BlockPos.ORIGIN;
    private BlockPos lastBuiltCenter = null;
    private Matrix4f portalView = new Matrix4f();
    private Matrix4f portalProjection = new Matrix4f();
    private Matrix4f portalRot = new Matrix4f();
    private Frustum frustum = null;

    private Camera lastPortalCamera = null;
    private ClientWorld lastPortalWorld = null;

    private static final float SKY_FAR_PLANE = 65536.0f * 4.0f;

    private final VertexConsumerProvider.Immediate immediate =
            VertexConsumerProvider.immediate(new BufferBuilder(256));

    private static Vec3d portalSkyCameraPos = null;

    private Vec3d lastExteriorFogColor = null;

    private Vec3d lastEyeWorldPos = null;

    public WorldGeometryRenderer(int renderDistance) {
        this.renderDistance = renderDistance;
    }

    public void markDirty() {
        this.needsFullRebuild = true;
    }

    public void markSectionDirty(ChunkSectionPos pos) {
        this.buildAttempts.remove(pos);
        this.dirtySections.add(pos);
    }

    public boolean reclaimIfIdle(long idleNanos) {
        if (closed || sectionBuffers.isEmpty())
            return false;
        if (System.nanoTime() - lastRenderNanos < idleNanos)
            return false;
        if (buildFuture != null && !buildFuture.isDone())
            return false;

        for (Map<RenderLayer, VertexBuffer> layerBuffers : sectionBuffers.values())
            for (VertexBuffer vbo : layerBuffers.values())
                vbo.close();
        sectionBuffers.clear();
        sectionBlockEntities.clear();
        dirtySections.clear();
        buildAttempts.clear();
        needsFullRebuild = true;
        return true;
    }

    public void setDoorFacing(Direction facing) {
        setDoorNormal(Vec3d.of(facing.getVector()));
    }

    public void setDoorNormal(Vec3d normal) {
        Vec3d n = normal.normalize();
        if (lastDoorNormal == null || lastDoorNormal.squaredDistanceTo(n) > 1.0e-4)
            markDirty();
        this.doorNormal = n;
        this.lastDoorNormal = n;
    }

    public BlockPos centerPos() {
        return this.centerPos;
    }

    public int renderDistance() {
        return this.renderDistance;
    }

    public Vec3d eyeWorldPos() {
        return this.lastEyeWorldPos;
    }

    public Vec3d doorNormal() {
        return this.doorNormal;
    }

    public void render(UUID id, ClientWorld portalWorld, BlockPos centerPos, Vec3d eyeRelToCenter,
                       float portalYaw, float portalPitch, float tickDelta, boolean checkBehindPortal) {
        this.centerPos = centerPos;
        this.lastRenderNanos = System.nanoTime();

        if (!centerPos.equals(this.lastBuiltCenter)) {
            this.lastBuiltCenter = centerPos.toImmutable();
            markDirty();
        }

        MinecraftClient client = MinecraftClient.getInstance();
        GameRenderer gameRenderer = client.gameRenderer;

        this.portalProjection = new Matrix4f(RenderSystem.getProjectionMatrix());

        Matrix4f portalRot = buildPortalRotation(portalYaw, portalPitch);
        this.portalRot = portalRot;
        this.portalView = buildPortalView(portalRot, eyeRelToCenter);

        this.frustum = new Frustum(portalRot, portalProjection);
        this.frustum.setPosition(eyeRelToCenter.x, eyeRelToCenter.y, eyeRelToCenter.z);

        pumpBuilds(portalWorld, checkBehindPortal);

        Camera portalCamera = new Camera();
        portalCamera.setPos(centerPos.getX(), centerPos.getY(), centerPos.getZ());
        portalCamera.setRotation(portalYaw, portalPitch);

        this.lastPortalCamera = portalCamera;
        this.lastPortalWorld = portalWorld;

        Matrix4f originalProjection = new Matrix4f(RenderSystem.getProjectionMatrix());
        RenderSystem.setProjectionMatrix(portalProjection, VertexSorter.BY_DISTANCE);

        Vec3d eyeWorldPos = new Vec3d(centerPos.getX() + eyeRelToCenter.x, centerPos.getY() + eyeRelToCenter.y,
                centerPos.getZ() + eyeRelToCenter.z);
        this.lastEyeWorldPos = eyeWorldPos;

        float[] previousFogColor = RenderSystem.getShaderFogColor().clone();
        float previousFogStart = RenderSystem.getShaderFogStart();
        float previousFogEnd = RenderSystem.getShaderFogEnd();
        FogShape previousFogShape = RenderSystem.getShaderFogShape();
        try {
            this.lastExteriorFogColor = updateExteriorFog(portalWorld, eyeWorldPos, portalYaw, portalPitch, tickDelta, Math.min(client.options.getClampedViewDistance(), this.renderDistance()));
        } catch (Exception e) {
            AITMod.LOGGER.error("BOTI: failed to compute exterior fog", e);
        }

        if (!dev.amble.ait.compat.DependencyChecker.isIrisShaderPackInUse()) {
            try {
                renderSky(id, portalWorld, portalRot, portalCamera, eyeWorldPos, tickDelta);
            } catch (Throwable t) {
                if (!skyPassErrorLogged) {
                    AITMod.LOGGER.error("BOTI: sky pass failed (expected under Iris shaders at the END phase - "
                            + "the exterior-fog fill stands in for the sky); further occurrences suppressed", t);
                    skyPassErrorLogged = true;
                }
            }
        }

        LightmapTextureManager lightmap = gameRenderer.getLightmapTextureManager();
        ClientWorld previousLightmapWorld = client.world;
        client.world = portalWorld;
        lightmap.tick();
        lightmap.update(tickDelta);
        client.world = previousLightmapWorld;

        float terrainFogView = Math.max(client.gameRenderer.getViewDistance(), 32.0f);
        RenderSystem.setShaderFogStart(terrainFogView - MathHelper.clamp(terrainFogView / 10.0f, 4.0f, 64.0f));
        RenderSystem.setShaderFogEnd(terrainFogView);
        RenderSystem.setShaderFogShape(FogShape.CYLINDER);

        MatrixStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.push();
        try {
            modelViewStack.peek().getPositionMatrix().set(portalView);
            modelViewStack.peek().getNormalMatrix().set(new Matrix3f(portalView));
            RenderSystem.applyModelViewMatrix();

            if (!sectionBuffers.isEmpty()) {
                runPass("terrain", this::renderTerrain);
            }

            runPass("block entities", () -> renderBlockEntities(portalWorld, tickDelta, portalCamera));
            runPass("entities", () -> renderEntities(portalWorld, tickDelta, portalCamera));
            runPass("particles", () -> renderParticles(id, portalCamera, tickDelta));
        } finally {
            modelViewStack.pop();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.setProjectionMatrix(originalProjection, VertexSorter.BY_DISTANCE);

            Camera mainCamera = client.gameRenderer.getCamera();
            client.getBlockEntityRenderDispatcher().configure(previousLightmapWorld, mainCamera, client.crosshairTarget);
            client.getEntityRenderDispatcher().configure(previousLightmapWorld, mainCamera, client.targetedEntity);


            lightmap.tick();
            lightmap.update(tickDelta);

            try {
                BackgroundRenderer.render(mainCamera, tickDelta, previousLightmapWorld,
                        client.options.getClampedViewDistance(), gameRenderer.getSkyDarkness(tickDelta));
            } catch (Exception e) {
                AITMod.LOGGER.error("BOTI: failed to restore interior fog", e);
            }
            RenderSystem.setShaderFogColor(previousFogColor[0], previousFogColor[1], previousFogColor[2], previousFogColor[3]);
            RenderSystem.setShaderFogStart(previousFogStart);
            RenderSystem.setShaderFogEnd(previousFogEnd);
            RenderSystem.setShaderFogShape(previousFogShape);
        }
    }

    private void runPass(String name, Runnable pass) {
        try {
            pass.run();
        } catch (Throwable t) {
            AITMod.LOGGER.error("BOTI: '{}' pass failed", name, t);
        }
    }

    private static Matrix4f buildPortalRotation(float yaw, float pitch) {
        return new Matrix4f()
                .rotateX((float) Math.toRadians(pitch))
                .rotateY((float) Math.toRadians(yaw + 180.0f));
    }

    private static Matrix4f buildPortalView(Matrix4f portalRotation, Vec3d eyeRelToCenter) {
        return new Matrix4f(portalRotation).translate(
                (float) -eyeRelToCenter.x, (float) -eyeRelToCenter.y, (float) -eyeRelToCenter.z);
    }

    private void pumpBuilds(World world, boolean checkBehindPortal) {
        boolean idle = buildFuture == null || buildFuture.isDone();
        if (!idle)
            return;

        if (needsFullRebuild) {
            needsFullRebuild = false;
            enqueueVolume();
        }

        if (dirtySections.isEmpty()) {
            if (!builderPool.isEmpty() && ++idleFrames > POOL_RETAIN_FRAMES) {
                builderPool.clear();
                idleFrames = 0;
            }
            return;
        }

        idleFrames = 0;
        List<ChunkSectionPos> batch = drainBatch(BUILD_BUDGET);
        if (!batch.isEmpty())
            dispatchBuild(world, batch, checkBehindPortal);
    }

    private void enqueueVolume() {
        buildAttempts.clear();
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

    private List<ChunkSectionPos> drainBatch(int budget) {
        List<ChunkSectionPos> candidates = new ArrayList<>();

        Iterator<ChunkSectionPos> it = dirtySections.iterator();
        while (it.hasNext()) {
            ChunkSectionPos pos = it.next();
            if (!isSectionInVolume(pos)) {
                it.remove();
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
        if (closed)
            return;

        LightingProvider lightingProvider = world.getLightingProvider();
        BlockPos.Mutable lightPos = new BlockPos.Mutable();
        List<ChunkSectionPos> ready = new ArrayList<>(batch.size());
        long lightDeadline = System.nanoTime() + LIGHT_BUDGET_NANOS;
        int scanned = 0;
        for (; scanned < batch.size(); scanned++) {
            ChunkSectionPos sectionPos = batch.get(scanned);
            if (world.getChunk(sectionPos.getX(), sectionPos.getZ(), ChunkStatus.FULL, false) == null) {
                dirtySections.add(sectionPos);
                continue;
            }

            int startX = sectionPos.getMinX(), startY = sectionPos.getMinY(), startZ = sectionPos.getMinZ();
            for (int x = startX; x <= startX + 15; x++)
                for (int y = startY; y <= startY + 15; y++)
                    for (int z = startZ; z <= startZ + 15; z++)
                        lightingProvider.checkBlock(lightPos.set(x, y, z));
            ready.add(sectionPos);

            if (System.nanoTime() >= lightDeadline)
                break;
        }
        for (int i = scanned + 1; i < batch.size(); i++)
            dirtySections.add(batch.get(i));

        if (ready.isEmpty())
            return;

        lightingProvider.doLightUpdates();
        final List<ChunkSectionPos> buildBatch = ready;

        CompletableFuture<Void> applied = new CompletableFuture<>();
        buildFuture = applied;

        buildExecutor.execute(() -> {
            BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
            Random random = Random.create();

            List<SectionResult> results = new ArrayList<>(buildBatch.size());
            for (int slot = 0; slot < buildBatch.size(); slot++) {
                ChunkSectionPos sectionPos = buildBatch.get(slot);

                if (world.getChunk(sectionPos.getX(), sectionPos.getZ(), ChunkStatus.FULL, false) == null) {
                    dirtySections.add(sectionPos);
                    continue;
                }

                try {
                    results.add(buildSection(world, sectionPos, builderSet(slot), blockRenderManager, random, checkBehindPortal));
                } catch (Throwable t) {
                    resetBuilderSet(slot);

                    int attempts = buildAttempts.merge(sectionPos, 1, Integer::sum);
                    if (attempts == 1)
                        AITMod.LOGGER.error("BOTI: failed to build section {} (attempt {})", sectionPos, attempts, t);

                    if (attempts < MAX_BUILD_ATTEMPTS)
                        dirtySections.add(sectionPos);
                }
            }

            MinecraftClient.getInstance().execute(() -> {
                try {
                    if (closed) {
                        for (SectionResult result : results)
                            for (BufferBuilder.BuiltBuffer built : result.buffers().values())
                                built.release();
                        return;
                    }
                    for (SectionResult result : results)
                        applySection(result);
                } finally {
                    applied.complete(null);
                }
            });
        });
    }

    private Map<RenderLayer, BufferBuilder> builderSet(int slot) {
        while (builderPool.size() <= slot)
            builderPool.add(newBuilderSet());
        return builderPool.get(slot);
    }

    private static Map<RenderLayer, BufferBuilder> newBuilderSet() {
        Map<RenderLayer, BufferBuilder> set = new HashMap<>();
        for (RenderLayer layer : RenderLayer.getBlockLayers())
            set.put(layer, new BufferBuilder(layer.getExpectedBufferSize()));
        return set;
    }

    private void resetBuilderSet(int slot) {
        if (slot < builderPool.size())
            builderPool.set(slot, newBuilderSet());
    }

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

    private boolean isSectionInVolume(ChunkSectionPos pos) {
        double dx = pos.getMinX() + 8 - centerPos.getX();
        double dy = pos.getMinY() + 8 - centerPos.getY();
        double dz = pos.getMinZ() + 8 - centerPos.getZ();

        double reach = renderDistance + 16.0;
        if (dx * dx + dy * dy + dz * dz > reach * reach)
            return false;

        double inFront = dx * doorNormal.x + dy * doorNormal.y + dz * doorNormal.z;
        return inFront > -16.0;
    }

    private double sectionDistanceSq(ChunkSectionPos pos) {
        double dx = pos.getMinX() + 8 - centerPos.getX();
        double dy = pos.getMinY() + 8 - centerPos.getY();
        double dz = pos.getMinZ() + 8 - centerPos.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    private boolean isSectionVisible(ChunkSectionPos pos) {
        if (frustum == null)
            return true;

        double minX = pos.getMinX() - centerPos.getX();
        double minY = pos.getMinY() - centerPos.getY();
        double minZ = pos.getMinZ() - centerPos.getZ();

        return frustum.isVisible(new Box(minX, minY, minZ, minX + 16, minY + 16, minZ + 16));
    }

    public void updatePortalView(Vec3d eyeRelToCenter, float portalYaw, float portalPitch) {
        this.portalProjection = new Matrix4f(RenderSystem.getProjectionMatrix());
        Matrix4f portalRot = buildPortalRotation(portalYaw, portalPitch);
        this.portalRot = portalRot;
        this.portalView = buildPortalView(portalRot, eyeRelToCenter);
        this.frustum = new Frustum(portalRot, portalProjection);
        this.frustum.setPosition(eyeRelToCenter.x, eyeRelToCenter.y, eyeRelToCenter.z);
    }

    public void injectSky(UUID id, ClientWorld portalWorld, float tickDelta) {
        if (lastPortalCamera == null || lastEyeWorldPos == null || centerPos == null)
            return;
        PortalData data = PortalDataManager.get(id);
        if (data == null || data.renderer() == null)
            return;

        Matrix4f savedProjection = new Matrix4f(RenderSystem.getProjectionMatrix());
        VertexSorter savedSorter = RenderSystem.getVertexSorting();
        float[] savedFogColor = RenderSystem.getShaderFogColor().clone();
        float savedFogStart = RenderSystem.getShaderFogStart();
        float savedFogEnd = RenderSystem.getShaderFogEnd();
        FogShape savedFogShape = RenderSystem.getShaderFogShape();
        float[] savedShaderColor = RenderSystem.getShaderColor().clone();
        boolean savedBlend = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean savedCull = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        boolean savedDepthTest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        boolean savedDepthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        int savedDepthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);

        Object prevPipeline = dev.amble.ait.client.boti.iris.IrisSkyCompat.installMainPipeline(data.renderer());

        MinecraftClient mc = MinecraftClient.getInstance();
        ClientWorld prevWorld = mc.world;
        mc.world = portalWorld;
        dev.amble.ait.client.boti.iris.IrisSkyCompat.resampleFrameUniforms();
        try {
            renderSky(id, portalWorld, portalRot, lastPortalCamera, lastEyeWorldPos, tickDelta);
        } catch (Throwable t) {
            if (!skyInjectErrorLogged) {
                AITMod.LOGGER.error("BOTI: doorway sky injection (AFTER_ENTITIES) failed; falling back to the fog "
                        + "backdrop; further occurrences suppressed", t);
                skyInjectErrorLogged = true;
            }
        } finally {
            mc.world = prevWorld;
            dev.amble.ait.client.boti.iris.IrisSkyCompat.resampleFrameUniforms();
            dev.amble.ait.client.boti.iris.IrisSkyCompat.restore(data.renderer(), prevPipeline);

            RenderSystem.setProjectionMatrix(savedProjection, savedSorter);
            RenderSystem.setShaderFogColor(savedFogColor[0], savedFogColor[1], savedFogColor[2], savedFogColor[3]);
            RenderSystem.setShaderFogStart(savedFogStart);
            RenderSystem.setShaderFogEnd(savedFogEnd);
            RenderSystem.setShaderFogShape(savedFogShape);
            RenderSystem.setShaderColor(savedShaderColor[0], savedShaderColor[1], savedShaderColor[2], savedShaderColor[3]);
            if (savedBlend) RenderSystem.enableBlend(); else RenderSystem.disableBlend();
            if (savedCull) RenderSystem.enableCull(); else RenderSystem.disableCull();
            if (savedDepthTest) RenderSystem.enableDepthTest(); else RenderSystem.disableDepthTest();
            RenderSystem.depthMask(savedDepthMask);
            RenderSystem.depthFunc(savedDepthFunc);
        }
    }

    private void renderTerrain() {
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

    public void debugInjectTerrainIntoGbuffer() {
        if (sectionBuffers.isEmpty())
            return;

        List<Map<RenderLayer, VertexBuffer>> visible = new ArrayList<>();
        for (Map.Entry<ChunkSectionPos, Map<RenderLayer, VertexBuffer>> entry : sectionBuffers.entrySet()) {
            if (isSectionVisible(entry.getKey()))
                visible.add(entry.getValue());
        }
        if (visible.isEmpty())
            return;

        RenderSystem.enableDepthTest();
        boolean prevDepthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        RenderSystem.depthMask(true);

        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);

        boolean phased = dev.amble.ait.client.boti.iris.IrisPhase.setTerrainSolid();
        try {
            drawLayer(RenderLayer.getSolid(), visible);
        } finally {
            if (phased)
                dev.amble.ait.client.boti.iris.IrisPhase.reset();
        }

        phased = dev.amble.ait.client.boti.iris.IrisPhase.setTerrainCutoutMipped();
        try {
            drawLayer(RenderLayer.getCutoutMipped(), visible);
        } finally {
            if (phased)
                dev.amble.ait.client.boti.iris.IrisPhase.reset();
        }

        phased = dev.amble.ait.client.boti.iris.IrisPhase.setTerrainCutout();
        try {
            drawLayer(RenderLayer.getCutout(), visible);
        } finally {
            if (phased)
                dev.amble.ait.client.boti.iris.IrisPhase.reset();
        }

        RenderSystem.depthMask(prevDepthMask);
    }

    public void debugInjectTranslucentIntoGbuffer() {
        if (sectionBuffers.isEmpty())
            return;

        List<Map<RenderLayer, VertexBuffer>> visible = new ArrayList<>();
        for (Map.Entry<ChunkSectionPos, Map<RenderLayer, VertexBuffer>> entry : sectionBuffers.entrySet()) {
            if (isSectionVisible(entry.getKey()))
                visible.add(entry.getValue());
        }
        if (visible.isEmpty())
            return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        boolean prevDepthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        RenderSystem.depthMask(false);

        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);

        boolean phased = dev.amble.ait.client.boti.iris.IrisPhase.setTerrainTranslucent();
        try {
            drawLayer(RenderLayer.getTranslucent(), visible);
        } finally {
            if (phased)
                dev.amble.ait.client.boti.iris.IrisPhase.reset();
        }

        RenderSystem.depthMask(prevDepthMask);
        RenderSystem.disableBlend();
    }

    public void injectBlockEntitiesAndEntities(float tickDelta) {
        if (lastPortalCamera == null || lastPortalWorld == null || centerPos == null)
            return;

        RenderSystem.enableDepthTest();
        boolean prevDepthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        RenderSystem.depthMask(true);

        Matrix4f originalProjection = new Matrix4f(RenderSystem.getProjectionMatrix());
        RenderSystem.setProjectionMatrix(portalProjection, VertexSorter.BY_DISTANCE);

        MatrixStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.push();
        try {
            modelViewStack.peek().getPositionMatrix().set(portalView);
            modelViewStack.peek().getNormalMatrix().set(new Matrix3f(portalView));
            RenderSystem.applyModelViewMatrix();

            boolean p1 = dev.amble.ait.client.boti.iris.IrisPhase.setBlockEntities();
            try {
                renderBlockEntities(lastPortalWorld, tickDelta, lastPortalCamera);
            } finally {
                if (p1) dev.amble.ait.client.boti.iris.IrisPhase.reset();
            }

            boolean p2 = dev.amble.ait.client.boti.iris.IrisPhase.setEntities();
            try {
                renderEntities(lastPortalWorld, tickDelta, lastPortalCamera);
            } finally {
                if (p2) dev.amble.ait.client.boti.iris.IrisPhase.reset();
            }

        } finally {
            modelViewStack.pop();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.setProjectionMatrix(originalProjection, VertexSorter.BY_DISTANCE);

            MinecraftClient client = MinecraftClient.getInstance();
            Camera mainCamera = client.gameRenderer.getCamera();
            client.getBlockEntityRenderDispatcher().configure(client.world, mainCamera, client.crosshairTarget);
            client.getEntityRenderDispatcher().configure(client.world, mainCamera, client.targetedEntity);

            RenderSystem.depthMask(prevDepthMask);
        }
    }

    private void renderSky(UUID id, ClientWorld portalWorld, Matrix4f portalRotation, Camera portalCamera,
                           Vec3d eyeWorldPos, float tickDelta) {
        PortalData data = PortalDataManager.get(id);
        if (data == null || data.renderer() == null)
            return;

        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld previousWorld = client.world;

        MatrixStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.push();
        modelViewStack.peek().getPositionMatrix().identity();
        modelViewStack.peek().getNormalMatrix().identity();
        RenderSystem.applyModelViewMatrix();

        Camera gameCamera = client.gameRenderer.getCamera();
        Vec3d savedCamPos = gameCamera.getPos();
        float savedCamYaw = gameCamera.getYaw();
        float savedCamPitch = gameCamera.getPitch();
        gameCamera.setPos(eyeWorldPos.x, eyeWorldPos.y, eyeWorldPos.z);
        gameCamera.setRotation(portalCamera.getYaw(), portalCamera.getPitch());

        try {
            client.world = portalWorld;
            portalSkyCameraPos = eyeWorldPos;

            MatrixStack skyStack = new MatrixStack();
            skyStack.multiplyPositionMatrix(portalRotation);

            RenderSystem.depthMask(false);

            Matrix4f skyProjection = portalProjection;
            try {
                double fovDeg = client.gameRenderer.getFov(portalCamera, tickDelta, true);
                float aspect = (float) client.getWindow().getFramebufferWidth()
                        / (float) client.getWindow().getFramebufferHeight();
                skyProjection = new Matrix4f().setPerspective((float) (fovDeg * (Math.PI / 180.0)), aspect, 0.05f,
                        SKY_FAR_PLANE);
            } catch (Exception e) {
                AITMod.LOGGER.error("BOTI: failed to build sky projection; far skyboxes may be clipped", e);
            }
            RenderSystem.setProjectionMatrix(skyProjection, VertexSorter.BY_DISTANCE);

            RenderSystem.setShader(GameRenderer::getPositionProgram);

            float viewDistanceBlocks = Math.max(client.gameRenderer.getViewDistance(), 32.0f);
            data.renderer().renderSky(skyStack, skyProjection, tickDelta, portalCamera, false, () -> {
                RenderSystem.setShaderFogStart(0.0f);
                RenderSystem.setShaderFogEnd(viewDistanceBlocks);
                RenderSystem.setShaderFogShape(FogShape.CYLINDER);
            });

            RenderSystem.setShaderFogStart(viewDistanceBlocks - MathHelper.clamp(viewDistanceBlocks / 10.0f, 4.0f, 64.0f));
            RenderSystem.setShaderFogEnd(viewDistanceBlocks);
            RenderSystem.setShaderFogShape(FogShape.CYLINDER);

            if (client.options.getCloudRenderModeValue() != CloudRenderMode.OFF
                    && !TardisServerWorld.isTardisDimension(portalWorld) && portalWorld.getRegistryKey() != AITDimensions.TIME_VORTEX_WORLD)
                renderPortalClouds(portalWorld, portalRotation, tickDelta, eyeWorldPos);
        } finally {
            portalSkyCameraPos = null;
            client.world = previousWorld;
            gameCamera.setPos(savedCamPos.x, savedCamPos.y, savedCamPos.z);
            gameCamera.setRotation(savedCamYaw, savedCamPitch);
            modelViewStack.pop();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.depthMask(true);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setProjectionMatrix(portalProjection, VertexSorter.BY_DISTANCE);
        }
    }

    public static Vec3d updateExteriorFog(ClientWorld portalWorld, Vec3d eyePos, float yaw, float pitch,
                                          float tickDelta, int renderDistance) {
        MinecraftClient client = MinecraftClient.getInstance();

        Camera fogCamera = new Camera();
        double fogY = Math.max(eyePos.y, portalWorld.getBottomY() + 34.0);
        fogCamera.setPos(eyePos.x, fogY, eyePos.z);
        fogCamera.setRotation(yaw, pitch);

        BackgroundRenderer.render(fogCamera, tickDelta, portalWorld, renderDistance,
                client.gameRenderer.getSkyDarkness(tickDelta));
        BackgroundRenderer.setFogBlack();

        float[] fog = RenderSystem.getShaderFogColor();
        return new Vec3d(fog[0], fog[1], fog[2]);
    }

    public Vec3d exteriorFogColor() {
        return this.lastExteriorFogColor;
    }

    public static Vec3d getPortalSkyCameraPos() {
        return portalSkyCameraPos;
    }

    private static final Identifier CLOUDS_TEXTURE = new Identifier("textures/environment/clouds.png");

    private void renderPortalClouds(ClientWorld world, Matrix4f cloudRotation, float tickDelta, Vec3d eyePos) {
        float cloudHeight = world.getDimensionEffects().getCloudsHeight();
        if (Float.isNaN(cloudHeight))
            return;

        double drift = (world.getTime() + tickDelta) * 0.03;
        double ox = (eyePos.x + drift) / 12.0;
        double oy = cloudHeight - eyePos.y + 0.33;
        double oz = eyePos.z / 12.0 + 0.33;
        ox -= MathHelper.floor(ox / 2048.0) * 2048;
        oz -= MathHelper.floor(oz / 2048.0) * 2048;
        float fracX = (float) (ox - MathHelper.floor(ox));
        float fracY = (float) (oy / 4.0 - MathHelper.floor(oy / 4.0)) * 4.0F;
        float fracZ = (float) (oz - MathHelper.floor(oz));

        Vec3d color = world.getCloudsColor(tickDelta);
        float cr = (float) color.x, cg = (float) color.y, cb = (float) color.z;
        float g = 0.00390625F;
        float texX = MathHelper.floor(ox) * g;
        float texZ = MathHelper.floor(oz) * g;
        float y = (float) Math.floor(oy / 4.0) * 4.0F;

        BufferBuilder builder = Tessellator.getInstance().getBuffer();
        builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
        for (int qx = -32; qx < 32; qx += 32) {
            for (int qz = -32; qz < 32; qz += 32) {
                builder.vertex(qx, y, qz + 32).texture(qx * g + texX, (qz + 32) * g + texZ).color(cr, cg, cb, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                builder.vertex(qx + 32, y, qz + 32).texture((qx + 32) * g + texX, (qz + 32) * g + texZ).color(cr, cg, cb, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                builder.vertex(qx + 32, y, qz).texture((qx + 32) * g + texX, qz * g + texZ).color(cr, cg, cb, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                builder.vertex(qx, y, qz).texture(qx * g + texX, qz * g + texZ).color(cr, cg, cb, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
            }
        }
        BufferBuilder.BuiltBuffer built = builder.end();

        RenderSystem.setShader(GameRenderer::getPositionTexColorNormalProgram);
        RenderSystem.setShaderTexture(0, CLOUDS_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);

        MatrixStack modelView = RenderSystem.getModelViewStack();
        modelView.push();
        modelView.peek().getPositionMatrix().set(cloudRotation);
        modelView.scale(12.0F, 1.0F, 12.0F);
        modelView.translate(-fracX, fracY, -fracZ);
        RenderSystem.applyModelViewMatrix();
        try {
            BufferRenderer.drawWithGlobalProgram(built);
        } finally {
            modelView.pop();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
        }
    }

    private void renderBlockEntities(ClientWorld portalWorld, float tickDelta, Camera portalCamera) {
        MinecraftClient client = MinecraftClient.getInstance();
        BlockEntityRenderDispatcher dispatcher = client.getBlockEntityRenderDispatcher();

        dispatcher.configure(portalWorld, portalCamera, client.crosshairTarget);

        MatrixStack matrices = new MatrixStack();
        Box cameraBox = new Box(portalCamera.getBlockPos());

        for (List<BlockEntity> sectionEntities : sectionBlockEntities.values()) {
            for (BlockEntity blockEntity : sectionEntities) {
                BlockPos blockPos = blockEntity.getPos();

                if (!isWithinRenderBounds(blockPos))
                    continue;

                if ((blockEntity instanceof DoorBlockEntity || blockEntity instanceof ExteriorBlockEntity) && cameraBox.contains(blockPos.toCenterPos()))
                    continue;

                matrices.push();
                matrices.translate(
                        blockPos.getX() - centerPos.getX(),
                        blockPos.getY() - centerPos.getY(),
                        blockPos.getZ() - centerPos.getZ());

                try {
                    dispatcher.render(blockEntity, tickDelta, matrices, immediate);
                } catch (Throwable t) {
                    AITMod.LOGGER.error("BOTI: failed to render block entity {}", blockEntity, t);
                } finally {
                    matrices.pop();
                }
            }
        }

        immediate.draw();
    }

    private void renderEntities(ClientWorld portalWorld, float tickDelta, Camera portalCamera) {
        MinecraftClient client = MinecraftClient.getInstance();
        EntityRenderDispatcher dispatcher = client.getEntityRenderDispatcher();

        dispatcher.configure(portalWorld, portalCamera, client.targetedEntity);

        RenderSystem.polygonOffset(-1.0f, -10.0f);
        RenderSystem.enablePolygonOffset();
        try {
            MatrixStack matrices = new MatrixStack();

            for (Entity entity : portalWorld.getEntities()) {
                if (entity == null || !isWithinRenderBounds(entity.getBlockPos()))
                    continue;

                double x = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX()) - centerPos.getX();
                double y = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY()) - centerPos.getY();
                double z = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ()) - centerPos.getZ();
                float yaw = MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw());

                try {
                    int light = dispatcher.getLight(entity, tickDelta);
                    dispatcher.render(entity, x, y, z, yaw, tickDelta, matrices, immediate, light);
                } catch (Throwable t) {
                    AITMod.LOGGER.error("BOTI: failed to render entity {}", entity, t);
                }
            }

            immediate.draw();
        } finally {
            RenderSystem.polygonOffset(0.0f, 0.0f);
            RenderSystem.disablePolygonOffset();
        }
    }

    private void renderParticles(UUID id, Camera portalCamera, float tickDelta) {
        PortalParticleManager manager = PortalDataManager.particles(id);
        if (manager == null)
            return;

        MinecraftClient client = MinecraftClient.getInstance();

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

    private SectionResult buildSection(World world, ChunkSectionPos sectionPos, Map<RenderLayer, BufferBuilder> builders,
                                       BlockRenderManager blockRenderManager, Random random, boolean checkBehindPortal) {

        int startX = sectionPos.getMinX();
        int startY = sectionPos.getMinY();
        int startZ = sectionPos.getMinZ();
        int endX = startX + 15;
        int endY = startY + 15;
        int endZ = startZ + 15;

        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        Set<RenderLayer> usedLayers = new HashSet<>();

        for (RenderLayer layer : RenderLayer.getBlockLayers())
            builders.get(layer).begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);

        MatrixStack matrices = new MatrixStack();
        List<BlockEntity> foundBlockEntities = new ArrayList<>();

        double fluidOffsetX = startX - centerPos.getX();
        double fluidOffsetY = startY - centerPos.getY();
        double fluidOffsetZ = startZ - centerPos.getZ();
        Map<RenderLayer, OffsetVertexConsumer> fluidConsumers = new HashMap<>();

        boolean hasBlocks = false;

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    mutablePos.set(x, y, z);

                    BlockState state = world.getBlockState(mutablePos);

                    if (state.isAir())
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
                        usedLayers.add(fluidLayer);

                        OffsetVertexConsumer fluidConsumer = fluidConsumers.computeIfAbsent(fluidLayer,
                                layer -> new OffsetVertexConsumer(builders.get(layer),
                                        fluidOffsetX, fluidOffsetY, fluidOffsetZ));

                        blockRenderManager.renderFluid(mutablePos, world, fluidConsumer, state, fluidState);
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

    private boolean isBehindPortal(double relX, double relY, double relZ) {
        return relX * doorNormal.x + relY * doorNormal.y + relZ * doorNormal.z < 0.0;
    }

    private void applySection(SectionResult result) {
        ChunkSectionPos pos = result.pos();
        buildAttempts.remove(pos);

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

    public void dropSection(ChunkSectionPos pos) {
        dirtySections.remove(pos);
        buildAttempts.remove(pos);

        Map<RenderLayer, VertexBuffer> old = sectionBuffers.remove(pos);
        if (old != null) {
            for (VertexBuffer vbo : old.values())
                vbo.close();
        }

        sectionBlockEntities.remove(pos);
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
        closed = true;
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

    private class HybridRenderView implements BlockRenderView {
        private final World fakeWorld;
        private final World realWorld;

        public HybridRenderView(World fakeWorld) {
            this.fakeWorld = fakeWorld;
            this.realWorld = MinecraftClient.getInstance().world;
        }

        @Override
        public int getLightLevel(LightType type, BlockPos pos) {
            if (realWorld == null) return 15;
            return realWorld.getLightLevel(type, pos);
        }

        @Override
        public int getBaseLightLevel(BlockPos pos, int ambientDarkness) {
            if (realWorld == null) return 15728880;
            return realWorld.getBaseLightLevel(pos, ambientDarkness);
        }

        @Override
        public float getBrightness(Direction direction, boolean shaded) {
            if (realWorld == null) return 1.0f;
            return realWorld.getBrightness(direction, shaded);
        }

        @Override
        public LightingProvider getLightingProvider() {
            return realWorld != null ? realWorld.getLightingProvider() : fakeWorld.getLightingProvider();
        }

        @Nullable
        @Override
        public BlockEntity getBlockEntity(BlockPos pos) {
            return fakeWorld.getBlockEntity(pos);
        }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            return fakeWorld.getBlockState(pos);
        }

        @Override
        public FluidState getFluidState(BlockPos pos) {
            return fakeWorld.getFluidState(pos);
        }

        @Override
        public int getColor(BlockPos pos, ColorResolver colorResolver) {
            return fakeWorld.getColor(pos, colorResolver);
        }

        @Override
        public int getHeight() { return fakeWorld.getHeight(); }

        @Override
        public int getBottomY() { return fakeWorld.getBottomY(); }
    }
}
