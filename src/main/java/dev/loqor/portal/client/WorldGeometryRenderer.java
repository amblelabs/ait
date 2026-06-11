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

    // Shared, reused immediate for the block-entity / entity / particle passes. Allocating a new BufferBuilder per
    // pass per door per frame churned off-heap direct memory (each grows well past its initial size, then is left for
    // the GC's Cleaner to reclaim) - the main cause of the "leave the door open and memory overloads" spikes. The
    // passes run sequentially on the render thread, each ending in draw(), so one reused buffer is safe and keeps its
    // grown capacity across frames instead of re-allocating.
    private final VertexConsumerProvider.Immediate immediate =
            VertexConsumerProvider.immediate(new BufferBuilder(256));

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

        // Match the doorway's view bobbing. In 1.20.1 GameRenderer.renderWorld folds view bobbing (plus the
        // hurt-tilt and nausea warp) into the *projection* matrix - `projection.mul(bobMatrix)` - and leaves the
        // model-view as pure camera rotation. The doorway's stencil mask and frame are drawn during the world
        // render with that bobbed projection bound, so they wobble with the head bob. Building a fresh
        // getBasicProjectionMatrix() here would have NO bob, so the interior would sit rigid while its frame
        // wobbles around it - the "interior swims in the doorway" wobble. Reusing the live projection gives the
        // interior the identical bob (and FOV), so frame and contents move as one.
        this.portalProjection = new Matrix4f(RenderSystem.getProjectionMatrix());

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

        // Tint the fog to the exterior dimension's sky colour BEFORE the sky pass. The interior dimension's fog
        // (set earlier this frame by BackgroundRenderer - typically short-range and near-black for the TARDIS
        // interior) is otherwise still bound while vanilla renderSky draws the celestial dome and stars, fogging
        // everything but the horizon band to black ("the sky only renders the horizon, zenith is black"). This also
        // makes distant terrain and the horizon fade to the right colour. Restored in the finally below.
        float[] previousFogColor = RenderSystem.getShaderFogColor().clone();
        try {
            Vec3d fog = portalWorld.getSkyColor(Vec3d.of(centerPos), tickDelta);
            RenderSystem.setShaderFogColor((float) fog.x, (float) fog.y, (float) fog.z);
        } catch (Exception ignored) {
            // keep the existing fog colour
        }

        // Sky sits at infinity, so it only takes the rotation (no eye translation) and never writes depth.
        renderSky(id, portalWorld, portalRot, portalCamera, tickDelta);

        // The lightmap (light coord -> final RGB; it bakes in sky darkness, time of day, the dimension's ambient
        // light and gamma) is rebuilt once per frame by GameRenderer from client.world - the *interior* dimension -
        // before this door ever renders. Our terrain and entities carry light coordinates sampled from the shadow
        // world, but with the interior's ramp they get shaded as if they were inside the TARDIS (typically a flat,
        // day/night-less ramp), which reads as "lighting is broken / entities are invisible". Rebuild the ramp for
        // the exterior dimension for the duration of the portal passes, then restore it (below) so the rest of the
        // frame - main-world particles, weather, the hand - keeps the interior ramp.
        LightmapTextureManager lightmap = gameRenderer.getLightmapTextureManager();
        ClientWorld previousLightmapWorld = client.world;
        client.world = portalWorld;
        lightmap.tick();            // GameRenderer already consumed this frame's dirty flag - re-arm it
        lightmap.update(tickDelta); // recompute the ramp from the shadow world's dimension + (synced) time of day
        client.world = previousLightmapWorld;

        MatrixStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.push();
        try {
            modelViewStack.peek().getPositionMatrix().set(portalView);
            modelViewStack.peek().getNormalMatrix().set(new Matrix3f(portalView));
            RenderSystem.applyModelViewMatrix();

            // Each pass is isolated: a single throwing block-entity/entity renderer must not abort the others. The
            // old single try/catch (up in TardisDoorBOTI) meant one bad entity killed terrain's siblings *and* leaked
            // the model-view push below - which is exactly why entities and particles silently never appeared.

            if (!sectionBuffers.isEmpty()) {
                runPass("terrain", this::renderTerrain);
            }

            runPass("block entities", () -> renderBlockEntities(portalWorld, tickDelta, portalCamera));
            runPass("entities", () -> renderEntities(portalWorld, tickDelta, portalCamera));
            runPass("particles", () -> renderParticles(id, portalCamera, tickDelta));
        } finally {
            // Always unwind - otherwise the leaked push corrupts the main game's model-view matrix next frame.
            modelViewStack.pop();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.setProjectionMatrix(originalProjection, VertexSorter.BY_DISTANCE);

            // Restore both dispatcher configurations to the interior world and the real game camera.
            // renderBlockEntities/renderEntities reconfigure the shared BlockEntityRenderDispatcher and
            // EntityRenderDispatcher to point at portalWorld; any interior block entities rendered after
            // this door in the same frame's block-entity loop would then query light from the exterior
            // dimension and get the wrong (dark/transparent) lighting. Re-configure here so the
            // remaining interior block entities in the loop see the correct world and camera.
            Camera mainCamera = client.gameRenderer.getCamera();
            client.getBlockEntityRenderDispatcher().configure(previousLightmapWorld, mainCamera, client.crosshairTarget);
            client.getEntityRenderDispatcher().configure(previousLightmapWorld, mainCamera, client.targetedEntity);


            // Restore the interior dimension's lightmap for the rest of this frame (client.world is the interior
            // again here). Without this, main-world particles/weather drawn after AFTER_ENTITIES would be shaded
            // with the exterior ramp.
            lightmap.tick();
            lightmap.update(tickDelta);

            // Restore the interior fog colour for the rest of the frame.
            RenderSystem.setShaderFogColor(previousFogColor[0], previousFogColor[1], previousFogColor[2], previousFogColor[3]);
        }
    }

    /** Runs one draw pass, swallowing (and logging once) any failure so the remaining passes still render. */
    private void runPass(String name, Runnable pass) {
        try {
            pass.run();
        } catch (Throwable t) {
            AITMod.LOGGER.error("BOTI: '{}' pass failed", name, t);
        }
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
                // Don't build a section whose column hasn't streamed into the shadow world yet: reading it would
                // return all-air, and applySection would then *replace* the section's last good geometry with
                // nothing - the chunk flashes in, then vanishes ("yeeted"). Re-queue and try again once it loads.
                // (An explicit unload drops geometry via dropSection, so a genuinely gone chunk still disappears.)
                if (world.getChunk(sectionPos.getX(), sectionPos.getZ(), ChunkStatus.FULL, false) == null) {
                    dirtySections.add(sectionPos);
                    continue;
                }

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
     * The doorway is already cleared to the exterior dimension's real sky colour (see {@code TardisDoorBOTI}); on top
     * of that we ask the shadow world's own {@link WorldRenderer} (it mirrors the right dimension) to draw the
     * celestial bodies. Vanilla {@code renderSky} reads the sun/moon angle, sky colour and star brightness from the
     * renderer's {@code world} (already the shadow world) but decides the sky <em>type</em> - overworld sun/moon/stars
     * vs. End starfield vs. nether/no sky - from {@code client.world}, the <em>interior</em> dimension. The TARDIS
     * interior has no vanilla sky type, so nothing celestial ever drew. We momentarily point {@code client.world} at
     * the shadow world for the duration of the sky pass so the type matches the target dimension too; the swap is
     * restored in {@code finally}. Guarded so a sky hiccup can never take down the rest of the portal render.
     */
    private void renderSky(UUID id, ClientWorld portalWorld, Matrix4f portalRotation, Camera portalCamera,
                           float tickDelta) {
        PortalData data = PortalDataManager.get(id);
        if (data == null || data.renderer() == null)
            return;

        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld previousWorld = client.world;

        // Tessellator-drawn sky geometry (horizon ring, celestial bodies) reads
        // RenderSystem.getModelViewMatrix() for ModelViewMat. Without this push the global
        // matrix is the interior camera's full view (rotation + eye translation), which
        // displaces the pre-rotated horizon/celestial vertices hundreds of blocks off-screen,
        // leaving only a thin accidental strip visible. The VBO dome draws already receive
        // portalRotation through their explicit matrix parameter, so they are unaffected by
        // this push — both paths now agree on rotation-only, no translation.
        MatrixStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.push();
        modelViewStack.peek().getPositionMatrix().set(portalRotation);
        modelViewStack.peek().getNormalMatrix().set(new Matrix3f(portalRotation));
        RenderSystem.applyModelViewMatrix();

        try {
            client.world = portalWorld;

            MatrixStack skyStack = new MatrixStack();
            skyStack.multiplyPositionMatrix(portalRotation);

            RenderSystem.depthMask(false);

            // Bind the position program BEFORE renderSky. Vanilla draws the upper sky dome (lightSkyBuffer, a
            // POSITION-format VBO) with whatever shader RenderSystem.getShader() happens to hold - it only sets an
            // explicit shader later, for the horizon glow / sun. In the portal path the leftover shader is the
            // stencil mask's position_color program, whose vertex layout doesn't match the dome's, so the dome's
            // colour attribute is unbound and it renders black - leaving only the (separately-shaded) horizon band
            // visible. Binding the matching position program here makes the dome paint the real sky colour again.
            RenderSystem.setShader(GameRenderer::getPositionProgram);

            data.renderer().renderSky(skyStack, portalProjection, tickDelta, portalCamera, false, () -> {
                // Push the fog plane far beyond every sky element (dome ~22 blocks, sun/moon/stars ~100). The core
                // `position` shader that draws the dome applies linear_fog, and renderSky forces the fog COLOUR to
                // black (BackgroundRenderer.setFogBlack) right before the dome - so a near fog start fogged the dome's
                // lower/horizon vertices to black, the dark band that faded up into clear sky. Starting the fog past
                // all sky geometry leaves the dome at its true colour. (The terrain shown through the door is
                // short-range, so it needs no fog of its own.)
                RenderSystem.setShaderFogStart(512.0f);
                RenderSystem.setShaderFogEnd(1024.0f);
                RenderSystem.setShaderFogShape(FogShape.CYLINDER);
            });

            // Clouds are a separate pass in vanilla (WorldRenderer.renderClouds), so the doorway never drew them.
            // Draw them now, inside the same client.world swap, positioned relative to the exterior block so they sit
            // above the terrain shown through the door. Clouds are distant enough that the eye-vs-centre parallax is
            // imperceptible, so centerPos is a fine camera origin.
            if (client.options.getCloudRenderModeValue() != CloudRenderMode.OFF) {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                MatrixStack cloudStack = new MatrixStack();
                cloudStack.multiplyPositionMatrix(portalRotation);
                data.renderer().renderClouds(cloudStack, portalProjection, tickDelta,
                        centerPos.getX(), centerPos.getY(), centerPos.getZ());
            }
        } catch (Throwable t) {
            AITMod.LOGGER.error("BOTI: failed to render exterior sky", t);
        } finally {
            client.world = previousWorld;
            modelViewStack.pop();
            RenderSystem.applyModelViewMatrix();
            // Vanilla renderSky leaves these in various states; reset to sane terrain defaults.
            RenderSystem.depthMask(true);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setProjectionMatrix(portalProjection, VertexSorter.BY_DISTANCE);
        }
    }

    private void renderBlockEntities(ClientWorld portalWorld, float tickDelta, Camera portalCamera) {
        MinecraftClient client = MinecraftClient.getInstance();
        BlockEntityRenderDispatcher dispatcher = client.getBlockEntityRenderDispatcher();

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

            try {
                dispatcher.render(blockEntity, tickDelta, matrices, immediate);
            } catch (Throwable t) {
                AITMod.LOGGER.error("BOTI: failed to render block entity {}", blockEntity, t);
            } finally {
                matrices.pop();
            }
        }

        immediate.draw();
    }

    private void renderEntities(ClientWorld portalWorld, float tickDelta, Camera portalCamera) {
        MinecraftClient client = MinecraftClient.getInstance();
        EntityRenderDispatcher dispatcher = client.getEntityRenderDispatcher();

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

            try {
                int light = dispatcher.getLight(entity, tickDelta);
                dispatcher.render(entity, x, y, z, yaw, tickDelta, matrices, immediate, light);
            } catch (Throwable t) {
                // A half-synced mob (missing tracked data, etc.) must not blank the whole entity pass.
                AITMod.LOGGER.error("BOTI: failed to render entity {}", entity, t);
            }
        }

        immediate.draw();
    }

    private void renderParticles(UUID id, Camera portalCamera, float tickDelta) {
        PortalParticleManager manager = PortalDataManager.particles(id);
        if (manager == null)
            return;

        MinecraftClient client = MinecraftClient.getInstance();

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

        int startX = sectionPos.getMinX();
        int startY = sectionPos.getMinY();
        int startZ = sectionPos.getMinZ();
        int endX = startX + 15;
        int endY = startY + 15;
        int endZ = startZ + 15;

        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        // ==============================================================================
        // SCENARIO B FIX: LIGHTING PRE-PASS
        // Tell the fake world's lighting engine to check these blocks and calculate
        // flood-fill shadows and light sources before we start meshing.
        // ==============================================================================
        LightingProvider lightingProvider = world.getLightingProvider();

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    mutablePos.set(x, y, z);
                    // Queues the block to be checked for light emission/opacity
                    lightingProvider.checkBlock(mutablePos);
                }
            }
        }

        // Force the engine to process the queue immediately so the light
        // data is ready when renderBlock() asks for it below.
        // (Note: If using Mojmap instead of Yarn, this might be called runLightUpdates)
        lightingProvider.doLightUpdates();
        // ==============================================================================

        Map<RenderLayer, BufferBuilder> builders = new HashMap<>();
        Set<RenderLayer> usedLayers = new HashSet<>();

        for (RenderLayer layer : RenderLayer.getBlockLayers()) {
            BufferBuilder builder = new BufferBuilder(layer.getExpectedBufferSize());
            builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
            builders.put(layer, builder);
        }

        MatrixStack matrices = new MatrixStack();
        List<BlockEntity> foundBlockEntities = new ArrayList<>();

        // renderFluid() ignores the matrix stack and bakes its vertices at section-local coords (x&15, y&15, z&15).
        // Everything else here is baked relative to centerPos, so wrap the fluid buffers to add the constant
        // per-section shift (sectionMin - centerPos) that maps section-local space into centerPos-relative space.
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

                        // Because we ran doLightUpdates() above, this will now receive true flood-fill data
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
        return relX * normal.getX() + relY * normal.getY() + relZ * normal.getZ() < 0.0;
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

    /**
     * Immediately drops a section's geometry. Called when the shadow world unloads a chunk: the blocks are genuinely
     * gone, so we remove the buffers directly instead of scheduling a rebuild (which would now be skipped, because
     * the streaming guard in {@link #dispatchBuild} won't build an unloaded column). Must run on the render thread.
     */
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
        private final World fakeWorld; // Provides the blocks
        private final World realWorld; // Provides the light

        public HybridRenderView(World fakeWorld) {
            this.fakeWorld = fakeWorld;
            this.realWorld = MinecraftClient.getInstance().world;
        }

        // ==========================================
        // LIGHTING: Fetch from the real ClientWorld
        // ==========================================

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

        // ==========================================
        // BLOCKS: Fetch from your copied fake world
        // ==========================================

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
