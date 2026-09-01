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

    /** Bind the solid-terrain gbuffer program for subsequent draws. Returns true iff a live pipeline accepted it. */
    public static boolean setTerrainSolid() {
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        if (pipeline == null) return false;
        pipeline.setPhase(WorldRenderingPhase.TERRAIN_SOLID);
        return true;
    }

    /** Restore Iris's phase to NONE so Iris's own subsequent rendering isn't left mid-phase. */
    public static void reset() {
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        if (pipeline != null)
            pipeline.setPhase(WorldRenderingPhase.NONE);
    }
}
