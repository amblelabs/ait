package dev.amble.ait.client.boti.iris;

import java.lang.reflect.Field;

import net.irisshaders.iris.Iris;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;

import net.minecraft.client.render.WorldRenderer;

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

    public static void resampleFrameUniforms() {
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        if (pipeline != null)
            pipeline.getFrameUpdateNotifier().onNewFrame();
    }

    public static void restore(WorldRenderer renderer, Object previous) {
        if (previous == UNAVAILABLE)
            return;
        Field f = field();
        if (f == null)
            return;
        try {
            f.set(renderer, previous);
        } catch (IllegalAccessException ignored) {
        }
    }
}
