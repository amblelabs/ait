package dev.amble.ait.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.profiler.Profiler;

/**
 * Zones and counters for the vanilla profiler, so an F3+L dump can attribute frame time to this mod
 * rather than leaving it in the generic buckets around the block entity and particle stages.
 *
 * <p>Everything here is free when nothing is profiling: the client holds {@code DummyProfiler} until a
 * recording starts, and its push/pop/visit do nothing.
 *
 * <p>Counters exist alongside timers because "is each one expensive" and "are there thousands of them"
 * want different fixes, and because a timer alone cannot tell a path that never ran from a path that ran
 * and did nothing.
 *
 * <p>Fetch the profiler per call. The client swaps its profiler object out every frame, so a reference
 * held in a field or a constructor pins {@code DummyProfiler} forever and every zone below reads zero.
 */
@Environment(EnvType.CLIENT)
public final class ClientProfiling {

    private ClientProfiling() {}

    public static Profiler get() {
        return MinecraftClient.getInstance().getProfiler();
    }

    public static void push(String zone) {
        get().push(zone);
    }

    public static void swap(String zone) {
        get().swap(zone);
    }

    public static void pop() {
        get().pop();
    }

    public static void count(String marker) {
        get().visit(marker, 1);
    }

    public static void count(String marker, int amount) {
        get().visit(marker, amount);
    }
}
