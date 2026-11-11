package dev.amble.ait.client.boti.iris;

import net.irisshaders.iris.Iris;
import net.irisshaders.iris.pipeline.WorldRenderingPhase;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;

public final class IrisPhase {
    private IrisPhase() {}

    private static boolean set(WorldRenderingPhase phase) {
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        if (pipeline == null) return false;
        pipeline.setPhase(phase);
        return true;
    }

    public static boolean setTerrainSolid() {
        return set(WorldRenderingPhase.TERRAIN_SOLID);
    }

    public static boolean setTerrainCutoutMipped() {
        return set(WorldRenderingPhase.TERRAIN_CUTOUT_MIPPED);
    }

    public static boolean setTerrainCutout() {
        return set(WorldRenderingPhase.TERRAIN_CUTOUT);
    }

    public static boolean setTerrainTranslucent() {
        return set(WorldRenderingPhase.TERRAIN_TRANSLUCENT);
    }

    public static boolean setBlockEntities() {
        return set(WorldRenderingPhase.BLOCK_ENTITIES);
    }

    public static boolean setEntities() {
        return set(WorldRenderingPhase.ENTITIES);
    }

    public static boolean setSky() {
        return set(WorldRenderingPhase.SKY);
    }

    public static void reset() {
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        if (pipeline != null)
            pipeline.setPhase(WorldRenderingPhase.NONE);
    }
}
