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

    /** Per-frame ceiling on the render-thread light flood-fill so a full rebuild/remesh spreads over frames. */
    private static final long LIGHT_BUDGET_NANOS = 2_000_000L;

    /** Idle frames with nothing to build before the retained builder pool's direct memory is released. */
    private static final int POOL_RETAIN_FRAMES = 600;
    private int idleFrames = 0;

    /**
     * {@link System#nanoTime()} of the last frame this doorway actually drew. A doorway that hasn't been drawn for
     * longer than the configured idle window (the player looked away, or this TARDIS's interior is loaded but not
     * currently viewed) has its baked geometry reclaimed by {@link #reclaimIfIdle} - the memory win for having several
     * TARDISes open at once. Only the render thread writes it (in {@link #render}), only the client-tick loop reads it.
     */
    private long lastRenderNanos = 0L;

    /** Set once the portal sky pass has failed. Under Iris the vanilla WorldRenderer.renderSky is mixin'd to touch
     *  Iris's pipeline, which is already torn down (null) by the time a door renders at WorldRenderEvents.END, so the
     *  sky pass throws every frame. We isolate it and log only the first failure to avoid per-frame spam; the
     *  doorway's exterior-fog fill stands in for the sky. Live sky-through-portal under a shaderpack needs a live
     *  Iris pipeline and is a Phase B concern. */
    private boolean skyPassErrorLogged = false;

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

    /** Set once {@link #close()} has run so a build still in flight can't upload (and leak) VBOs into a dead renderer. */
    private volatile boolean closed = false;

    /**
     * Reused per-section {@link BufferBuilder} sets (one entry per in-flight batch slot, each a full set of block
     * layers), confined to the single builder thread. Reusing them is the fix for the "leave the door open and memory
     * overloads" OOM: the old code allocated a fresh set of seven layer buffers - several MB of direct memory, since
     * SOLID's expected size alone is 2 MiB - for <em>every</em> section on <em>every</em> rebuild, and left them for
     * the GC's Cleaner to reclaim. A busy exterior (flowing water, redstone, mobs) rebuilds sections constantly, so
     * that direct-memory churn outran the Cleaner and exhausted the off-heap buffer pool. Reuse is safe because the
     * build → apply pipeline is serialised (see {@link #dispatchBuild}): {@link #buildFuture} only completes after
     * {@code applySection} has uploaded - and thereby released - every {@link BufferBuilder.BuiltBuffer}, so a slot's
     * builders are always idle before the next batch calls {@code begin()} on them again.
     */
    private final List<Map<RenderLayer, BufferBuilder>> builderPool = new ArrayList<>();

    /** Dedicated single-thread builder with a large stack; serialised so our builds never race each other. */
    private final ExecutorService buildExecutor = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(null, runnable, "BOTI-Geometry-Builder", BUILD_THREAD_STACK);
        thread.setDaemon(true);
        thread.setPriority(Thread.MIN_PRIORITY);
        return thread;
    });

    private final int renderDistance;

    // Outward door normal used to cull geometry behind the doorway. Stored as a float Vec3d (not a cardinal
    // Direction) so the exterior's fine rotation - it can sit at any of the 16 RotationPropertyHelper steps (22.5
    // deg each), not just N/S/E/W - culls against the true door plane instead of the nearest cardinal.
    private Vec3d doorNormal = new Vec3d(0, 0, -1);
    private Vec3d lastDoorNormal = null;

    // Frame-local view state, set at the top of every render() so the cull helpers and draw passes agree.
    private BlockPos centerPos = BlockPos.ORIGIN;
    private BlockPos lastBuiltCenter = null;
    private Matrix4f portalView = new Matrix4f();
    private Matrix4f portalProjection = new Matrix4f();
    private Frustum frustum = null;

    // Cached last-frame portal camera and world, used by injectBlockEntitiesAndEntities() so the gbuffer injector
    // can reuse the same camera/world that render() used without needing them passed at injection time.
    // One frame stale is fine for a probe; the alternative (reconstructing them in the injector) would require
    // duplicating all of render()'s eye-position / yaw / pitch computation, which is more dangerous than stale.
    private Camera lastPortalCamera = null;
    private ClientWorld lastPortalWorld = null;

    // Far plane for the doorway's sky pass only. Some TARDIS skyboxes (the time vortex) draw their geometry tens of
    // thousands of blocks away; inside a TARDIS AIT's GameRendererMixin pushes getFarPlaneDistance() to 65536 (so the
    // projection far is 65536 * 4). Through the exterior door the client is standing in the overworld, so that mixin
    // never fires - we rebuild the interior's far plane locally for the sky so the distant vortex isn't clipped.
    private static final float SKY_FAR_PLANE = 65536.0f * 4.0f;

    // Shared, reused immediate for the block-entity / entity / particle passes. Allocating a new BufferBuilder per
    // pass per door per frame churned off-heap direct memory (each grows well past its initial size, then is left for
    // the GC's Cleaner to reclaim) - the main cause of the "leave the door open and memory overloads" spikes. The
    // passes run sequentially on the render thread, each ending in draw(), so one reused buffer is safe and keeps its
    // grown capacity across frames instead of re-allocating.
    private final VertexConsumerProvider.Immediate immediate =
            VertexConsumerProvider.immediate(new BufferBuilder(256));

    /**
     * While the portal sky pass runs, the portal eye's position in the EXTERIOR world. Read by
     * {@code WorldRendererBotiMixin}: vanilla {@code renderSky} decides whether to draw its black below-horizon
     * "void plane" from {@code client.player}'s Y (the interior player sits below the overworld's y=63 darkness
     * line, which painted a black band along the doorway's horizon) and samples the sky colour's biome at the real
     * camera's position (interior coordinates that don't exist in the shadow world). The mixin substitutes this
     * position for both while it is non-null. Only touched on the render thread.
     */
    private static Vec3d portalSkyCameraPos = null;

    /** Exterior fog colour computed by the most recent {@link #render}; the doorway background is painted with it. */
    private Vec3d lastExteriorFogColor = null;

    /** Portal eye's exterior-world position from the last {@link #render}; the ambient particle spawn centres on it. */
    private Vec3d lastEyeWorldPos = null;

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

    /**
     * Drops this doorway's baked geometry if it hasn't been drawn for at least {@code idleNanos} - the memory reclaim
     * for TARDISes that are loaded but not currently being looked at (their doorway is frustum-culled, or their
     * interior isn't the one the player is standing in, so {@link #render} never runs for them). Frees the per-section
     * VBOs (the large GL / native cost) and the block-entity lists, then arms a full rebuild so a returning viewer
     * re-bakes on demand. The shadow world and its streamed chunks are untouched, so updates keep flowing in and the
     * rebuild is cheap. Must be called on the render thread (it closes GL buffers); the client-tick loop qualifies.
     *
     * @return {@code true} if geometry was reclaimed this call
     */
    public boolean reclaimIfIdle(long idleNanos) {
        if (closed || sectionBuffers.isEmpty())
            return false;
        if (System.nanoTime() - lastRenderNanos < idleNanos)
            return false;
        // Don't reclaim mid-build: a batch in flight is about to upload VBOs on the render thread (see dispatchBuild),
        // and clearing sectionBuffers now would either race that apply or immediately orphan what it uploads. Wait for
        // the pipeline to settle - one more idle tick and we reclaim then.
        if (buildFuture != null && !buildFuture.isDone())
            return false;

        for (Map<RenderLayer, VertexBuffer> layerBuffers : sectionBuffers.values())
            for (VertexBuffer vbo : layerBuffers.values())
                vbo.close();
        sectionBuffers.clear();
        sectionBlockEntities.clear();
        dirtySections.clear();
        buildAttempts.clear();
        needsFullRebuild = true; // a returning viewer re-bakes the whole volume from the (still-live) shadow world
        return true;
    }

    /** Cardinal convenience overload - used where the door genuinely is axis-aligned (e.g. the interior door). */
    public void setDoorFacing(Direction facing) {
        setDoorNormal(Vec3d.of(facing.getVector()));
    }

    /**
     * Sets the outward door normal (need not be axis-aligned - see {@link #doorNormal}). A change beyond a small
     * epsilon triggers a full rebuild, since the behind-portal cull that shapes the baked volume depends on it.
     */
    public void setDoorNormal(Vec3d normal) {
        Vec3d n = normal.normalize();
        if (lastDoorNormal == null || lastDoorNormal.squaredDistanceTo(n) > 1.0e-4)
            markDirty();
        this.doorNormal = n;
        this.lastDoorNormal = n;
    }

    /** The portal centre (exterior/interior block the volume is baked around), as of the last {@link #render}. */
    public BlockPos centerPos() {
        return this.centerPos;
    }

    /** Block radius of the baked volume - used to scope the ambient particle display ticks to what is visible. */
    public int renderDistance() {
        return this.renderDistance;
    }

    /** DIAG: number of baked sections; how many pass the current frustum; whether the portal-view cache is set. */
    public String debugState() {
        int visible = 0;
        for (ChunkSectionPos p : sectionBuffers.keySet())
            if (isSectionVisible(p)) visible++;
        return "sections=" + sectionBuffers.size() + " visible=" + visible
                + " hasCam=" + (lastPortalCamera != null) + " hasWorld=" + (lastPortalWorld != null)
                + " center=" + centerPos;
    }

    /** The portal eye's exterior-world position as of the last {@link #render}, or {@code null} before the first. */
    public Vec3d eyeWorldPos() {
        return this.lastEyeWorldPos;
    }

    /** The outward door normal (unit vector into the visible region) - the rough "look out the door" direction. */
    public Vec3d doorNormal() {
        return this.doorNormal;
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
        this.lastRenderNanos = System.nanoTime(); // published for reclaimIfIdle: this doorway drew this frame

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

        // Cache for the gbuffer injector (injectBlockEntitiesAndEntities). One frame stale is acceptable.
        this.lastPortalCamera = portalCamera;
        this.lastPortalWorld = portalWorld;

        Matrix4f originalProjection = new Matrix4f(RenderSystem.getProjectionMatrix());
        RenderSystem.setProjectionMatrix(portalProjection, VertexSorter.BY_DISTANCE);

        // The portal eye in exterior-world coordinates - the position the doorway's sky should be "seen from".
        Vec3d eyeWorldPos = new Vec3d(centerPos.getX() + eyeRelToCenter.x, centerPos.getY() + eyeRelToCenter.y,
                centerPos.getZ() + eyeRelToCenter.z);
        this.lastEyeWorldPos = eyeWorldPos; // published for the ambient-particle spawn (see eyeWorldPos())

        // Swap the frame's fog over to the exterior dimension before the sky pass. Setting the shader fog colour
        // directly is NOT enough: renderSky and renderClouds both call BackgroundRenderer.setFogBlack() mid-pass,
        // which re-applies the fog colour BackgroundRenderer computed at the start of the frame - the INTERIOR
        // dimension's (typically near-black for the TARDIS), fogging the dome's horizon to black no matter what
        // colour was bound here. BackgroundRenderer.render recomputes that cached colour from the shadow world
        // (biome fog colour, time of day, weather, sunrise tint), exactly like vanilla does at the start of a real
        // dimension's world render. Everything is restored in the finally below.
        float[] previousFogColor = RenderSystem.getShaderFogColor().clone();
        float previousFogStart = RenderSystem.getShaderFogStart();
        float previousFogEnd = RenderSystem.getShaderFogEnd();
        FogShape previousFogShape = RenderSystem.getShaderFogShape();
        try {
            this.lastExteriorFogColor = updateExteriorFog(portalWorld, eyeWorldPos, portalYaw, portalPitch, tickDelta, Math.min(client.options.getClampedViewDistance(), this.renderDistance()));
        } catch (Exception e) {
            AITMod.LOGGER.error("BOTI: failed to compute exterior fog", e);
        }

        // Sky sits at infinity, so it only takes the rotation (no eye translation) and never writes depth.
        // Isolated like the terrain/entity passes below: under Iris the mixin'd vanilla WorldRenderer.renderSky
        // dereferences Iris's pipeline, which is null once Iris has finalised the world render (this door draws at
        // WorldRenderEvents.END). Catching here - log-once to avoid per-frame spam - lets terrain and entities still
        // render through the doorway; the afbo's exterior-fog fill stands in for the sky.
        try {
            renderSky(id, portalWorld, portalRot, portalCamera, eyeWorldPos, tickDelta);
        } catch (Throwable t) {
            if (!skyPassErrorLogged) {
                AITMod.LOGGER.error("BOTI: sky pass failed (expected under Iris shaders at the END phase - "
                        + "the exterior-fog fill stands in for the sky); further occurrences suppressed", t);
                skyPassErrorLogged = true;
            }
        }

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

        // Exterior terrain fog distance. This is normally set at the tail of renderSky (for the terrain/entity
        // passes that follow), but under Iris renderSky throws before reaching it (its pipeline is null at the
        // END phase, so the mixin'd vanilla WorldRenderer.renderSky NPEs and we skip the pass). Applying it here,
        // ahead of the terrain pass, guarantees the doorway's world gets the exterior's distant fog instead of the
        // interior dimension's dense fog left in the shader state - which read as a flat "Minecraft-alpha" fog wall.
        // The fog COLOUR was already set for the exterior by updateExteriorFog above; this only sets start/end/shape.
        // In the non-Iris path renderSky sets the same values, so this is a harmless re-apply.
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

            // Recompute BackgroundRenderer's cached fog colour for the interior (anything later in the frame that
            // calls setFogBlack must get the interior colour back; this also restores the GL clear colour), then put
            // back the exact fog uniforms the interior render had - including any overrides AIT's FoggyUtils applied
            // for alarm/power-off effects, which a recompute alone would lose.
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
        if (closed)
            return;

        // Lighting flood-fill pre-pass - MUST run on this (the render) thread, never on the async builder below.
        // The shadow world's light engine is backed by thread-unsafe fastutil sets and is also mutated on this
        // thread by PortalData as chunk/light packets arrive. Touching it from the builder thread corrupted those
        // sets (builder-thread NPEs that blanked the terrain, then a fatal LongOpenHashSet.rehash AIOOBE on the
        // render thread). Doing it here keeps every light mutation single-threaded; the off-thread mesh then only
        // reads light. doLightUpdates() also commits the light PortalData staged via enqueueSectionData/setStatus.
        LightingProvider lightingProvider = world.getLightingProvider();
        BlockPos.Mutable lightPos = new BlockPos.Mutable();
        List<ChunkSectionPos> ready = new ArrayList<>(batch.size());
        long lightDeadline = System.nanoTime() + LIGHT_BUDGET_NANOS;
        int scanned = 0;
        for (; scanned < batch.size(); scanned++) {
            ChunkSectionPos sectionPos = batch.get(scanned);
            if (world.getChunk(sectionPos.getX(), sectionPos.getZ(), ChunkStatus.FULL, false) == null) {
                dirtySections.add(sectionPos); // not streamed yet - retry on a later frame
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
        // Requeue whatever the per-frame light budget didn't reach so a full rebuild / remesh spreads over frames.
        for (int i = scanned + 1; i < batch.size(); i++)
            dirtySections.add(batch.get(i));

        if (ready.isEmpty())
            return;

        lightingProvider.doLightUpdates();
        final List<ChunkSectionPos> buildBatch = ready;

        // Serialised pipeline: this future only completes once the results have been uploaded (or discarded) on the
        // render thread. Because pumpBuilds waits for it before dispatching the next batch, the reusable builder pool
        // is never touched by the builder thread while the render thread is still reading (uploading/releasing) the
        // previous batch's built buffers - the invariant that makes reusing the builders safe (see builderPool).
        CompletableFuture<Void> applied = new CompletableFuture<>();
        buildFuture = applied;

        buildExecutor.execute(() -> {
            BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
            Random random = Random.create();

            List<SectionResult> results = new ArrayList<>(buildBatch.size());
            for (int slot = 0; slot < buildBatch.size(); slot++) {
                ChunkSectionPos sectionPos = buildBatch.get(slot);

                // Don't build a section whose column hasn't streamed into the shadow world yet: reading it would
                // return all-air, and applySection would then *replace* the section's last good geometry with
                // nothing - the chunk flashes in, then vanishes ("yeeted"). Re-queue and try again once it loads.
                // (An explicit unload drops geometry via dropSection, so a genuinely gone chunk still disappears.)
                if (world.getChunk(sectionPos.getX(), sectionPos.getZ(), ChunkStatus.FULL, false) == null) {
                    dirtySections.add(sectionPos);
                    continue;
                }

                try {
                    results.add(buildSection(world, sectionPos, builderSet(slot), blockRenderManager, random, checkBehindPortal));
                } catch (Throwable t) {
                    // One bad section (e.g. boundary data not streamed yet) must not sink the whole batch, and a
                    // StackOverflowError is a Throwable, not an Exception - catch it so it can't be swallowed by the
                    // CompletableFuture and quietly leave the doorway blank. The pooled builders for this slot may be
                    // left mid-build, so throw them away and let the slot re-allocate a clean set next batch.
                    resetBuilderSet(slot);

                    int attempts = buildAttempts.merge(sectionPos, 1, Integer::sum);
                    if (attempts == 1)
                        AITMod.LOGGER.error("BOTI: failed to build section {} (attempt {})", sectionPos, attempts, t);

                    if (attempts < MAX_BUILD_ATTEMPTS)
                        dirtySections.add(sectionPos); // transient - retry on a later frame
                }
            }

            MinecraftClient.getInstance().execute(() -> {
                try {
                    if (closed) {
                        // Renderer was torn down while this batch was building - drop the built buffers rather than
                        // uploading them into VBOs that nothing would ever close (a GL-buffer leak per teardown).
                        for (SectionResult result : results)
                            for (BufferBuilder.BuiltBuffer built : result.buffers().values())
                                built.release();
                        return;
                    }
                    for (SectionResult result : results)
                        applySection(result);
                } finally {
                    applied.complete(null); // frees the pipeline (and the builder pool) for the next batch
                }
            });
        });
    }

    /**
     * Returns the reusable {@link BufferBuilder} set for the given batch slot, allocating it (and any lower slots)
     * on first use. Called only on the builder thread; safe to reuse across batches because of the serialised
     * pipeline (see {@link #builderPool}).
     */
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

    /** Drops a slot's builders (they may be left mid-{@code begin()} after a failed build) so it re-allocates clean. */
    private void resetBuilderSet(int slot) {
        if (slot < builderPool.size())
            builderPool.set(slot, newBuilderSet());
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

        double inFront = dx * doorNormal.x + dy * doorNormal.y + dz * doorNormal.z;
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

    /**
     * Recompute just the portal view/projection/frustum from the CURRENT camera-derived params, without meshing or
     * drawing. The gbuffer-injection path calls this at {@code AFTER_ENTITIES} so the injected portal matches the
     * current frame's camera instead of the 1-frame-stale view cached by the last {@code END} render - which is
     * what smears the portal contents when the camera turns. {@code centerPos} and the baked VBOs stay as cached.
     */
    public void updatePortalView(Vec3d eyeRelToCenter, float portalYaw, float portalPitch) {
        this.portalProjection = new Matrix4f(RenderSystem.getProjectionMatrix());
        Matrix4f portalRot = buildPortalRotation(portalYaw, portalPitch);
        this.portalView = buildPortalView(portalRot, eyeRelToCenter);
        this.frustum = new Frustum(portalRot, portalProjection);
        this.frustum.setPosition(eyeRelToCenter.x, eyeRelToCenter.y, eyeRelToCenter.z);
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
     * THROWAWAY gbuffer-injection probe. Draws this doorway's baked SOLID terrain straight into whatever framebuffer
     * is currently bound (called at {@code AFTER_ENTITIES}, that is Iris's main gbuffer, before the deferred pass),
     * with Iris's TERRAIN_SOLID phase set so Iris substitutes {@code gbuffers_terrain}. If Iris's deferred+composite
     * then light it, gbuffer-injection is viable. Uses the cached {@code portalView}/{@code portalProjection} from the
     * previous frame's END render (one frame stale, fine for a probe). Unclipped by design - it will splatter over the
     * opaque scene; clipping to the doorway is a later milestone. No afbo: we draw into the live gbuffer directly.
     */
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

        // Ensure injected terrain writes depth so it self-sorts and occludes correctly.
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

    /**
     * Injects the portal world's TRANSLUCENT terrain layer (glass, water, ice, stained glass) into the currently-
     * bound gbuffer, in Iris's {@code TERRAIN_TRANSLUCENT} phase ({@code gbuffers_water}). Called by the gbuffer-
     * injection paths AFTER {@link #debugInjectTerrainIntoGbuffer()} (so it blends over the already-injected opaque
     * terrain) and BEFORE the door-plane depth write. Blends with the standard translucent func and tests but does
     * NOT write depth, so it self-composites over the opaque portal terrain without occluding it.
     */
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
        RenderSystem.depthMask(false); // translucent tests against the opaque portal terrain but writes no depth

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

    /**
     * Injects the exterior portal world's block entities and entities into the currently-bound gbuffer (Iris's
     * main gbuffer at {@code AFTER_ENTITIES}), using cached state from the previous frame's {@link #render} call.
     * Mirrors the matrix save/restore in {@link #render} exactly so no matrix state leaks into the main render.
     *
     * <p>Must be called from inside the stencil-clipped section of {@link dev.amble.ait.client.boti.iris.GbufferInjectionProbe},
     * after {@link #debugInjectTerrainIntoGbuffer()}.
     *
     * @param tickDelta interpolation factor from {@code WorldRenderContext.tickDelta()}
     */
    public void injectBlockEntitiesAndEntities(float tickDelta) {
        if (lastPortalCamera == null || lastPortalWorld == null || centerPos == null)
            return;

        // Ensure depth writes are active for injected geometry (same guard as debugInjectTerrainIntoGbuffer).
        RenderSystem.enableDepthTest();
        boolean prevDepthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        RenderSystem.depthMask(true);

        // Save projection; set the portal projection for the duration of the draw (mirrors render()).
        Matrix4f originalProjection = new Matrix4f(RenderSystem.getProjectionMatrix());
        RenderSystem.setProjectionMatrix(portalProjection, VertexSorter.BY_DISTANCE);

        // Push and configure model-view to the portal view (mirrors the push in render()).
        MatrixStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.push();
        try {
            modelViewStack.peek().getPositionMatrix().set(portalView);
            modelViewStack.peek().getNormalMatrix().set(new Matrix3f(portalView));
            RenderSystem.applyModelViewMatrix();

            // Block entities — wrapped in the BLOCK_ENTITIES Iris phase so gbuffers_block draws them.
            boolean p1 = dev.amble.ait.client.boti.iris.IrisPhase.setBlockEntities();
            try {
                renderBlockEntities(lastPortalWorld, tickDelta, lastPortalCamera);
            } finally {
                if (p1) dev.amble.ait.client.boti.iris.IrisPhase.reset();
            }

            // Entities — wrapped in the ENTITIES Iris phase so gbuffers_entities draws them.
            boolean p2 = dev.amble.ait.client.boti.iris.IrisPhase.setEntities();
            try {
                renderEntities(lastPortalWorld, tickDelta, lastPortalCamera);
            } finally {
                if (p2) dev.amble.ait.client.boti.iris.IrisPhase.reset();
            }

        } finally {
            // Always restore — a leaked push corrupts the main game's model-view matrix next frame.
            modelViewStack.pop();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.setProjectionMatrix(originalProjection, VertexSorter.BY_DISTANCE);

            // Restore both dispatcher configurations to the main world/camera. renderBlockEntities and
            // renderEntities reconfigure the shared dispatchers to point at the portal world; without this
            // restore they stay pointed at the portal world until render()'s own finally runs at END,
            // meaning any interior block entities drawn between AFTER_ENTITIES and END query light from
            // the exterior dimension and get wrong (dark/transparent) lighting.
            MinecraftClient client = MinecraftClient.getInstance();
            Camera mainCamera = client.gameRenderer.getCamera();
            client.getBlockEntityRenderDispatcher().configure(client.world, mainCamera, client.crosshairTarget);
            client.getEntityRenderDispatcher().configure(client.world, mainCamera, client.targetedEntity);

            // Restore the prior depth-write state so callers aren't surprised by an unconditional enable.
            RenderSystem.depthMask(prevDepthMask);
        }
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
     * the shadow world for the duration of the sky pass so the type matches the target dimension too; the swap and the
     * GL state are restored in a {@code finally} (which does not swallow exceptions - they propagate as normal).
     */
    private void renderSky(UUID id, ClientWorld portalWorld, Matrix4f portalRotation, Camera portalCamera,
                           Vec3d eyeWorldPos, float tickDelta) {
        PortalData data = PortalDataManager.get(id);
        if (data == null || data.renderer() == null)
            return;

        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld previousWorld = client.world;

        // Vanilla renderSky draws the sun, moon and sunrise/sunset glow with BufferRenderer.drawWithGlobalProgram,
        // which transforms them by the GLOBAL model-view matrix - on top of the rotation baked into the skyStack we
        // pass below. Vanilla runs its whole sky pass with that global matrix at IDENTITY (the camera rotation lives
        // only in the matrices argument), so the celestial bodies are rotated exactly once.
        //
        // The global model-view still holds the interior camera's full view (rotation + eye translation) from the
        // main world render, which would fling the celestial vertices hundreds of blocks off-screen, so we must
        // override it - but to IDENTITY, not portalRotation. skyStack already carries portalRotation, so putting
        // portalRotation here too rotates the sun/moon TWICE (portalRotation squared) and throws them off-screen,
        // leaving only the dome (drawn via VertexBuffer.draw with an explicit matrix, which ignores the global
        // model-view) visible. That was the "dome shows but there's no sun" bug. Identity makes the sun/moon agree
        // with the dome. The dome, stars and clouds all use explicit matrices, so this value doesn't affect them.
        MatrixStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.push();
        modelViewStack.peek().getPositionMatrix().identity();
        modelViewStack.peek().getNormalMatrix().identity();
        RenderSystem.applyModelViewMatrix();

        // Two of the TARDIS skyboxes draw their contents from the REAL game camera rather than the matrices we hand
        // renderSky: SkyboxUtil.renderVortexSky puts the vortex ~50000 blocks out, and the moon/space skyboxes place
        // their planets via CelestialBodyRenderer, which reads MinecraftClient.gameRenderer.getCamera() directly (its
        // pitch/yaw and pos) instead of the sky matrix stack. Inside a real TARDIS the game camera IS the viewer, so
        // both work; through the doorway the game camera is the player standing in the OVERWORLD, looking a different
        // way (offset by the portal deltaYaw), so the planets get oriented toward wherever the player really faces -
        // usually right out of the doorway aperture, i.e. invisible. Point the real camera at the portal view for the
        // duration of the sky pass so those camera-driven bodies line up with the dome; restored in the finally.
        Camera gameCamera = client.gameRenderer.getCamera();
        Vec3d savedCamPos = gameCamera.getPos();
        float savedCamYaw = gameCamera.getYaw();
        float savedCamPitch = gameCamera.getPitch();
        gameCamera.setPos(eyeWorldPos.x, eyeWorldPos.y, eyeWorldPos.z);
        gameCamera.setRotation(portalCamera.getYaw(), portalCamera.getPitch());

        try {
            client.world = portalWorld;
            portalSkyCameraPos = eyeWorldPos; // WorldRendererBotiMixin reads this inside renderSky

            MatrixStack skyStack = new MatrixStack();
            skyStack.multiplyPositionMatrix(portalRotation);

            RenderSystem.depthMask(false);

            // The vortex skybox draws its geometry ~50000 blocks away, well past the overworld's normal ~1-2k far
            // plane that portalProjection carries out here - so it (and only it, nearer skyboxes at z ~= 100 survive)
            // gets clipped entirely. Build a sky-only projection exactly the way vanilla GameRenderer builds its own
            // (getFov + framebuffer aspect), but with the interior's far plane so the distant geometry isn't clipped.
            // Sky writes no depth, so extending the far plane is harmless for everything else. Bound on the global
            // projection too: the vortex/planets draw via Tessellator / entity consumers with the global RenderSystem
            // projection, not the matrix we pass to renderSky. Restored to portalProjection in the finally below.
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

            // Bind the position program BEFORE renderSky. Vanilla draws the upper sky dome (lightSkyBuffer, a
            // POSITION-format VBO) with whatever shader RenderSystem.getShader() happens to hold - it only sets an
            // explicit shader later, for the horizon glow / sun. In the portal path the leftover shader is the
            // stencil mask's position_color program, whose vertex layout doesn't match the dome's, so the dome's
            // colour attribute is unbound and it renders black - leaving only the (separately-shaded) horizon band
            // visible. Binding the matching position program here makes the dome paint the real sky colour again.
            RenderSystem.setShader(GameRenderer::getPositionProgram);

            // Vanilla sky fog (FOG_SKY: start 0, end view-distance, cylinder). This fog IS the sky gradient: the
            // dome is drawn in the zenith sky colour and linear fog fades its lower fragments into the horizon fog
            // colour (computed for the shadow world by updateExteriorFog, re-applied inside renderSky via
            // setFogBlack). The old "push the fog past all sky geometry" workaround killed the fade, which is why
            // the doorway sky was one flat colour with no horizon gradient.
            float viewDistanceBlocks = Math.max(client.gameRenderer.getViewDistance(), 32.0f);
            data.renderer().renderSky(skyStack, skyProjection, tickDelta, portalCamera, false, () -> {
                RenderSystem.setShaderFogStart(0.0f);
                RenderSystem.setShaderFogEnd(viewDistanceBlocks);
                RenderSystem.setShaderFogShape(FogShape.CYLINDER);
            });

            // Match vanilla's terrain fog for everything drawn after the dome (the clouds below, then the terrain/
            // entity/particle passes back in render()): clear until just short of the view distance, then a quick
            // fade. The portal volume is far smaller than the fog start so the doorway's blocks stay unfogged, but
            // the fog COLOUR stays the exterior's - renderClouds re-applies it via setFogBlack for the cloud sheet.
            RenderSystem.setShaderFogStart(viewDistanceBlocks - MathHelper.clamp(viewDistanceBlocks / 10.0f, 4.0f, 64.0f));
            RenderSystem.setShaderFogEnd(viewDistanceBlocks);
            RenderSystem.setShaderFogShape(FogShape.CYLINDER);

            // Clouds. We must NOT call data.renderer().renderClouds(...): Sodium @Overwrites
            // WorldRenderer.renderClouds on EVERY instance (including our shadow one) with its own CloudRenderer,
            // which is wired to the main client camera/world and ignores the shadow world, portal camera and matrices
            // we hand it - so through the doorway it draws nothing. Render the cloud sheet ourselves (a plain method
            // no mixin targets), positioned relative to eyeWorldPos so it lines up above the terrain shown in the door.
            // Skip clouds entirely for TARDIS interior dimensions: interiors have no sky, and their dimension effects
            // can still report a cloud height, which would paint a cloud sheet over the interior skybox. (Must be &&
            // with the negation - a previous || meant clouds still drew in interiors whenever cloud mode was on.)
            if (client.options.getCloudRenderModeValue() != CloudRenderMode.OFF
                    && !TardisServerWorld.isTardisDimension(portalWorld) && portalWorld.getRegistryKey() != AITDimensions.TIME_VORTEX_WORLD)
                renderPortalClouds(portalWorld, portalRotation, tickDelta, eyeWorldPos);
        } finally {
            portalSkyCameraPos = null;
            client.world = previousWorld;
            // Put the real game camera back exactly where the rest of the frame expects it (the terrain/entity passes
            // below use portalCamera, but everything after this door - the hand, main-world particles, weather - reads
            // the real camera again).
            gameCamera.setPos(savedCamPos.x, savedCamPos.y, savedCamPos.z);
            gameCamera.setRotation(savedCamYaw, savedCamPitch);
            modelViewStack.pop();
            RenderSystem.applyModelViewMatrix();
            // Vanilla renderSky leaves these in various states; reset to sane terrain defaults.
            RenderSystem.depthMask(true);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setProjectionMatrix(portalProjection, VertexSorter.BY_DISTANCE);
        }
    }

    /**
     * Recomputes the frame fog for the exterior dimension, exactly like vanilla does at the start of a world render:
     * {@code BackgroundRenderer.render} derives the fog colour from the shadow world's biomes / time of day /
     * weather as seen by the portal camera, and {@code setFogBlack()} applies it as the live shader fog colour
     * (and primes the cached value that renderSky/renderClouds re-apply mid-pass).
     *
     * @return the computed fog colour - the colour the sky fades into at the horizon, which is also what the
     *         doorway background should be painted with so the hand-off is seamless
     */
    public static Vec3d updateExteriorFog(ClientWorld portalWorld, Vec3d eyePos, float yaw, float pitch,
                                          float tickDelta, int renderDistance) {
        MinecraftClient client = MinecraftClient.getInstance();

        Camera fogCamera = new Camera();
        // BackgroundRenderer darkens the fog toward black within ~32 blocks of the world bottom: it multiplies the fog
        // by clamp((eyeY - bottomY) * horizonShadingRatio, 0, 1). Vanilla sets that ratio to 1.0 for FLAT worlds to
        // suppress the effect (so a superflat surface a few blocks above bedrock still looks bright), but our shadow
        // world's LevelProperties always report flatWorld=false, so it uses the normal 1/32 ratio - and a doorway onto
        // a superflat overworld fogged its horizon to near-black. We can't cheaply learn the mirrored dimension's flat
        // flag on the client, so compute the fog COLOUR as if the eye sat just above that shading band (bottomY + 34,
        // where the term clamps to 1). Only the returned colour changes; the real sky/terrain still render at eyePos.
        double fogY = Math.max(eyePos.y, portalWorld.getBottomY() + 34.0);
        fogCamera.setPos(eyePos.x, fogY, eyePos.z);
        fogCamera.setRotation(yaw, pitch);

        BackgroundRenderer.render(fogCamera, tickDelta, portalWorld, renderDistance,
                client.gameRenderer.getSkyDarkness(tickDelta));
        BackgroundRenderer.setFogBlack();

        float[] fog = RenderSystem.getShaderFogColor();
        return new Vec3d(fog[0], fog[1], fog[2]);
    }

    /** The exterior fog colour from this renderer's most recent frame, or {@code null} before the first one. */
    public Vec3d exteriorFogColor() {
        return this.lastExteriorFogColor;
    }

    /** The portal eye's exterior-world position while the portal sky pass is running, else {@code null}. */
    public static Vec3d getPortalSkyCameraPos() {
        return portalSkyCameraPos;
    }

    /** The vanilla cloud texture, sampled by our own cloud sheet (see {@link #renderPortalClouds}). */
    private static final Identifier CLOUDS_TEXTURE = new Identifier("textures/environment/clouds.png");

    /**
     * Draws a flat cloud sheet for the shadow world's dimension into the doorway.
     * <p>
     * This is a hand-rolled copy of vanilla's FAST cloud layer rather than a call to
     * {@code WorldRenderer.renderClouds}, because Sodium {@code @Overwrite}s that method on every {@link WorldRenderer}
     * instance (ours included) with its own {@code CloudRenderer} bound to the main client camera/world - so the
     * vanilla path draws nothing through the portal. The sheet is positioned relative to {@code eyePos} (the portal
     * eye in exterior-world coordinates) and drawn through {@code cloudRotation} so it lines up above the terrain the
     * doorway shows, exactly like the terrain pass ({@code R * (pos - eyeWorldPos)}).
     */
    private void renderPortalClouds(ClientWorld world, Matrix4f cloudRotation, float tickDelta, Vec3d eyePos) {
        float cloudHeight = world.getDimensionEffects().getCloudsHeight();
        if (Float.isNaN(cloudHeight))
            return; // nether / end: no clouds

        // Camera-relative cloud origin + smooth scroll, mirroring WorldRenderer#renderClouds.
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
        float g = 0.00390625F; // 1/256, the cloud texture's per-block UV step
        float texX = MathHelper.floor(ox) * g;
        float texZ = MathHelper.floor(oz) * g;
        float y = (float) Math.floor(oy / 4.0) * 4.0F; // baked cloud-sheet height (relative to the eye)

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

        // renderSky runs with the global model-view forced to identity (for the sun/moon); set it to the cloud matrix
        // for this draw only, then hand identity back so the rest of the sky pass is undisturbed.
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

        // Bias the whole entity pass toward the camera in depth. This is what keeps the entities' fake contact shadow
        // (the SHADOW_LAYER) from z-fighting the block top it lies on. Vanilla relies on that layer's
        // VIEW_OFFSET_Z_LAYERING, which pulls the decal toward the camera by scaling the model-view about its origin -
        // but that only works under camera-relative rendering, where the origin IS the eye. We render centerPos-
        // relative, so that scale biases toward the door centre and, once the player steps a few blocks from the door,
        // no longer clears the surface (the "shadow glitches into the block" report). A depth-only polygon offset is
        // camera-relative regardless of the model-view origin, so it fixes the shadow at any distance; entities aren't
        // coplanar with terrain so the tiny bias is invisible on them. It must stay enabled for the whole loop, not
        // just the final draw(): the shared Immediate flushes the SHADOW_LAYER mid-loop whenever the next entity asks
        // for a different layer, so those draws need the offset live too. Matches vanilla's POLYGON_OFFSET_LAYERING.
        RenderSystem.polygonOffset(-1.0f, -10.0f);
        RenderSystem.enablePolygonOffset();
        try {
            MatrixStack matrices = new MatrixStack();

            for (Entity entity : portalWorld.getEntities()) {
                if (entity == null || !isWithinRenderBounds(entity.getBlockPos()))
                    continue;

                // Interpolated render position, expressed relative to the portal centre (same convention as terrain
                // and block entities). The dispatcher translates the matrix stack by these coordinates internally; the
                // shared portal view matrix (set as the global model-view) then maps them onto the doorway.
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

    private SectionResult buildSection(World world, ChunkSectionPos sectionPos, Map<RenderLayer, BufferBuilder> builders,
                                       BlockRenderManager blockRenderManager, Random random, boolean checkBehindPortal) {

        int startX = sectionPos.getMinX();
        int startY = sectionPos.getMinY();
        int startZ = sectionPos.getMinZ();
        int endX = startX + 15;
        int endY = startY + 15;
        int endZ = startZ + 15;

        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        // NOTE: the lighting flood-fill pre-pass used to run here, on this builder thread. That raced PortalData's
        // render-thread light mutations and corrupted the shadow world's (thread-unsafe) light engine - a fatal
        // crash. The pre-pass now runs on the render thread in dispatchBuild() before this build is queued, so this
        // off-thread mesh only READS light (renderBlock below), which is safe.

        // builders come from the reusable pool (see builderPool); (re)begin each for this section's build.
        Set<RenderLayer> usedLayers = new HashSet<>();

        for (RenderLayer layer : RenderLayer.getBlockLayers())
            builders.get(layer).begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);

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
        return relX * doorNormal.x + relY * doorNormal.y + relZ * doorNormal.z < 0.0;
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
