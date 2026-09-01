package dev.amble.ait.client.boti.iris;

import java.lang.reflect.Field;

import net.irisshaders.iris.Iris;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;

import net.minecraft.client.render.WorldRenderer;

/**
 * Bridges Iris's per-{@link WorldRenderer} {@code pipeline} field so the doorway sky pass can run on the SHADOW
 * world's renderer. Iris's {@code MixinLevelRenderer} adds a private {@code pipeline} field to {@code WorldRenderer}
 * and only populates it (via {@code iris$setupPipeline}) on the MAIN renderer at {@code renderLevel} HEAD. The
 * portal sky is drawn by calling {@code renderSky} on the shadow world's {@code WorldRenderer}, whose field is
 * therefore null - so Iris's {@code renderSky} mixin ({@code beginNormalSky -> pipeline.setPhase}) NPEs.
 *
 * <p>Our own terrain injection avoids this because it goes through the GLOBAL {@code Iris.getPipelineManager()}
 * ({@link IrisPhase}); only Iris's built-in {@code renderSky} mixin uses the per-renderer field. This helper
 * temporarily copies the live main pipeline onto the shadow renderer for the duration of the sky pass, then
 * restores the previous value. All access is reflective and fails soft: if the field can't be resolved (Iris
 * absent, or renamed in a future version), the sky simply falls back to the fog backdrop.
 */
public final class IrisSkyCompat {
    private static final Object UNAVAILABLE = new Object();
    private static Field pipelineField;
    private static boolean resolved;

    private IrisSkyCompat() {}

    private static Field field() {
        if (!resolved) {
            resolved = true;
            try {
                pipelineField = WorldRenderer.class.getDeclaredField("pipeline");
            } catch (NoSuchFieldException e) {
                // @Unique may have prefixed the merged field name; fall back to type-matching.
                for (Field f : WorldRenderer.class.getDeclaredFields()) {
                    if (WorldRenderingPipeline.class.isAssignableFrom(f.getType())) {
                        pipelineField = f;
                        break;
                    }
                }
            }
            if (pipelineField != null)
                pipelineField.setAccessible(true);
        }
        return pipelineField;
    }

    /**
     * Installs the live main pipeline onto {@code renderer}'s Iris pipeline field so Iris's built-in {@code renderSky}
     * mixin doesn't NPE. Returns the previous field value to hand back to {@link #restore}, or a sentinel meaning
     * "nothing to restore" if the field or the live pipeline is unavailable.
     */
    public static Object installMainPipeline(WorldRenderer renderer) {
        Field f = field();
        if (f == null)
            return UNAVAILABLE;
        WorldRenderingPipeline main = Iris.getPipelineManager().getPipelineNullable();
        if (main == null)
            return UNAVAILABLE;
        try {
            Object prev = f.get(renderer);
            f.set(renderer, main);
            return prev;
        } catch (IllegalAccessException e) {
            return UNAVAILABLE;
        }
    }

    /**
     * Forces Iris to re-evaluate its {@code PER_FRAME} uniforms (sun/moon/celestial angle, sky colour, fog) NOW,
     * against the current {@code MinecraftClient.world}. Iris samples these once at frame start from the viewer's
     * dimension; to render the doorway's exterior world with ITS time of day (IP's "resample dimension data in the
     * forward pass" trick) the caller swaps {@code client.world} to the shadow world, calls this, draws, then swaps
     * back and calls this again so the deferred/composite pass reverts to the viewer's dimension. No-op without a
     * live pipeline.
     */
    public static void resampleFrameUniforms() {
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        if (pipeline != null)
            pipeline.getFrameUpdateNotifier().onNewFrame();
    }

    /** Restores the shadow renderer's pipeline field to the value {@link #installMainPipeline} returned. */
    public static void restore(WorldRenderer renderer, Object previous) {
        if (previous == UNAVAILABLE)
            return;
        Field f = field();
        if (f == null)
            return;
        try {
            f.set(renderer, previous);
        } catch (IllegalAccessException ignored) {
            // Best-effort restore; leaving the main pipeline installed would only matter if the shadow renderer
            // were later driven by Iris directly, which never happens.
        }
    }
}
