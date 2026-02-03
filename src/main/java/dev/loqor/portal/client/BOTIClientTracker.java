package dev.loqor.portal.client;

import dev.amble.ait.core.tardis.util.network.c2s.StartWatchingPortalC2SPacket;
import dev.amble.ait.core.tardis.util.network.c2s.StopWatchingPortalC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

/**
 * Client-side tracker to notify server when player views BOTI portals
 */
public class BOTIClientTracker {
    private static final Set<UUID> currentlyWatching = new HashSet<>();
    
    /**
     * Call this when player starts viewing a TARDIS portal
     */
    public static void startWatching(UUID tardisId, boolean isExteriorView, RegistryKey<World> targetWorld, BlockPos targetPos) {
        if (currentlyWatching.add(tardisId)) {
            // Only send if we weren't already watching
            if (ClientPlayNetworking.canSend(StartWatchingPortalC2SPacket.TYPE)) {
                ClientPlayNetworking.send(new StartWatchingPortalC2SPacket(tardisId, isExteriorView, targetWorld, targetPos));
            }
        }
    }
    
    /**
     * Call this when player stops viewing a TARDIS portal
     */
    public static void stopWatching(UUID tardisId) {
        if (currentlyWatching.remove(tardisId)) {
            // Only send if we were watching
            if (ClientPlayNetworking.canSend(StopWatchingPortalC2SPacket.TYPE)) {
                ClientPlayNetworking.send(new StopWatchingPortalC2SPacket(tardisId));
            }
        }
    }
    
    /**
     * Clear all tracking - call on disconnect
     */
    public static void clearAll() {
        currentlyWatching.clear();
    }
}
