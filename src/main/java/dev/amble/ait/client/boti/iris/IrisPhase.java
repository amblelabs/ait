package dev.amble.ait.client.boti.iris;

import net.irisshaders.iris.Iris;
import net.irisshaders.iris.pipeline.WorldRenderingPhase;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;

/**
 * THROWAWAY-grade helper for the gbuffer-injection probe. Sets Iris's current terrain phase on the live pipeline
 * so Iris substitutes its {@code gbuffers_terrain} program for the vanilla one on the draws that follow. No Iris
 * types leak into callers (methods are named, not phase-parameterised), so a non-Iris class can call these guarded
 * by {@code hasIris()} without force-loading Iris at its own class-load time.
 *
 * <p>Only meaningful when Iris's pipeline is live (inside the world render); returns false / no-ops otherwise.
 */
public final class IrisPhase {
    private IrisPhase() {}

    private static boolean set(WorldRenderingPhase phase) {
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        if (pipeline == null) return false;
        pipeline.setPhase(phase);
        return true;
    }

    /** Bind the solid-terrain gbuffer program for subsequent draws. Returns true iff a live pipeline accepted it. */
    public static boolean setTerrainSolid() {
        return set(WorldRenderingPhase.TERRAIN_SOLID);
    }

    /** Bind the cutout-mipped-terrain gbuffer program for subsequent draws. Returns true iff a live pipeline accepted it. */
    public static boolean setTerrainCutoutMipped() {
        return set(WorldRenderingPhase.TERRAIN_CUTOUT_MIPPED);
    }

    /** Bind the cutout-terrain gbuffer program for subsequent draws. Returns true iff a live pipeline accepted it. */
    public static boolean setTerrainCutout() {
        return set(WorldRenderingPhase.TERRAIN_CUTOUT);
    }

    /** Bind the block-entities gbuffer program for subsequent draws. Returns true iff a live pipeline accepted it. */
    public static boolean setBlockEntities() {
        return set(WorldRenderingPhase.BLOCK_ENTITIES);
    }

    /** Bind the entities gbuffer program for subsequent draws. Returns true iff a live pipeline accepted it. */
    public static boolean setEntities() {
        return set(WorldRenderingPhase.ENTITIES);
    }

    /** Bind the sky program so a fill is treated as (unlit) sky rather than lit gbuffer geometry. */
    public static boolean setSky() {
        return set(WorldRenderingPhase.SKY);
    }

    /** Restore Iris's phase to NONE so Iris's own subsequent rendering isn't left mid-phase. */
    public static void reset() {
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        if (pipeline != null)
            pipeline.setPhase(WorldRenderingPhase.NONE);
    }
}
