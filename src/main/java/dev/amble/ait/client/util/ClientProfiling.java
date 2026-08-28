package dev.amble.ait.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.MinecraftClient;

/**
 * Counters for the vanilla profiler, so an F3+L dump can report how many of a thing this mod did as
 * well as how long it took.
 *
 * <p>Counters exist alongside the timing zones because "is each one expensive" and "are there
 * thousands of them" want different fixes, and because a timer alone cannot tell a path that never
 * ran from a path that ran and did nothing.
 *
 * <p>This is free when nothing is profiling: the client holds {@code DummyProfiler} until a recording
 * starts, and its {@code visit} does nothing.
 *
 * <p>Zones are pushed on the caller's own profiler rather than through here, because a renderer
 * already has a world or an entity to ask. This exists for code that has neither, and for the
 * counters. Fetch the profiler per call either way: the client swaps its profiler object out every
 * frame, so a reference held in a field or a constructor pins {@code DummyProfiler} forever and every
 * zone reads zero.
 */
@Environment(EnvType.CLIENT)
public final class ClientProfiling {

    public static void count(String marker) {
        count(marker, 1);
    }

    public static void count(String marker, int amount) {
        MinecraftClient.getInstance().getProfiler().visit(marker, amount);
    }
}
