package dev.amble.ait.client.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import dev.amble.ait.AITMod;

/**
 * Client render switches that can be flipped while the game is running.
 *
 * <p>A system property needs a restart to change, and restarting between the halves of a comparison
 * is what lets the machine drift between them: a scenario that cannot be affected by the switch
 * still moved fifteen percent between two phases measured minutes apart. Flipping without a restart
 * lets the halves be interleaved instead, so drift falls on both equally.
 *
 * <p>Defaults come from the matching system property, so a launch argument still works.
 */
@Environment(EnvType.CLIENT)
public final class ClientPerfFlags {

    private static final Map<String, Boolean> FLAGS = new ConcurrentHashMap<>();

    private ClientPerfFlags() {}

    public static boolean get(String name, boolean fallback) {
        return FLAGS.computeIfAbsent(name, key -> {
            String property = System.getProperty("ait." + key);
            return property == null ? fallback : !"false".equalsIgnoreCase(property);
        });
    }

    public static void set(String name, boolean value) {
        FLAGS.put(name, value);
        AITMod.LOGGER.info("[ait-perf] flag {} = {}", name, value);
    }
}
