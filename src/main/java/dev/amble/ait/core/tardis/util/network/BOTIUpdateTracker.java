package dev.amble.ait.core.tardis.util.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.core.tardis.util.network.s2c.BOTIBlockUpdateS2CPacket;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central tracker for BOTI (Bigger On The Inside) dimension viewers and block update routing.
 * 
 * This class tracks which players are viewing which dimensions via BOTI portals/renders
 * and routes block updates to the appropriate clients for real-time mesh updates.
 * 
 * Thread-safe: Uses ConcurrentHashMap for multi-threaded server access.
 */
public class BOTIUpdateTracker {
    /**
     * Maps players to the set of dimensions they are currently viewing via BOTI.
     * A player can view multiple dimensions simultaneously (e.g., multiple TARDIS doors).
     */
    private static final Map<ServerPlayerEntity, Set<RegistryKey<World>>> viewingDimensions = 
        new ConcurrentHashMap<>();
    
    /**
     * Registers a player as viewing a specific dimension via BOTI.
     * 
     * @param player The player viewing the dimension
     * @param dimension The dimension being viewed
     */
    public static void registerViewer(ServerPlayerEntity player, RegistryKey<World> dimension) {
        viewingDimensions.computeIfAbsent(player, k -> ConcurrentHashMap.newKeySet())
            .add(dimension);
    }
    
    /**
     * Unregisters a player from viewing a specific dimension.
     * 
     * @param player The player no longer viewing the dimension
     * @param dimension The dimension no longer being viewed
     */
    public static void unregisterViewer(ServerPlayerEntity player, RegistryKey<World> dimension) {
        Set<RegistryKey<World>> dimensions = viewingDimensions.get(player);
        if (dimensions != null) {
            dimensions.remove(dimension);
            if (dimensions.isEmpty()) {
                viewingDimensions.remove(player);
            }
        }
    }
    
    /**
     * Unregisters a player from all BOTI views.
     * Should be called when player disconnects.
     * 
     * @param player The player to unregister
     */
    public static void unregisterAll(ServerPlayerEntity player) {
        viewingDimensions.remove(player);
    }
    
    /**
     * Notifies all players viewing a dimension about a block update.
     * 
     * This is called by the ServerWorldMixin when a block state changes.
     * It sends BOTIBlockUpdateS2CPacket to all clients that are currently
     * viewing this dimension via BOTI.
     * 
     * Thread-safe: Creates a snapshot of the entry set to avoid ConcurrentModificationException.
     * 
     * @param world The world where the block changed
     * @param pos Position of the changed block
     * @param state New block state
     */
    public static void notifyBlockUpdate(ServerWorld world, BlockPos pos, BlockState state) {
        RegistryKey<World> dimension = world.getRegistryKey();
        
        // Create a snapshot to avoid concurrent modification during iteration
        Map.Entry<ServerPlayerEntity, Set<RegistryKey<World>>>[] snapshot = 
            viewingDimensions.entrySet().toArray(new Map.Entry[0]);
        
        // Find all players viewing this dimension
        for (Map.Entry<ServerPlayerEntity, Set<RegistryKey<World>>> entry : snapshot) {
            ServerPlayerEntity player = entry.getKey();
            Set<RegistryKey<World>> viewedDimensions = entry.getValue();
            
            // Check if this player is viewing the dimension where the block changed
            if (viewedDimensions.contains(dimension)) {
                // Send block update packet to this player
                ServerPlayNetworking.send(player, 
                    new BOTIBlockUpdateS2CPacket(dimension, pos, state));
            }
        }
    }
    
    /**
     * Gets the number of players currently viewing any BOTI dimensions.
     * 
     * @return Number of active BOTI viewers
     */
    public static int getViewerCount() {
        return viewingDimensions.size();
    }
    
    /**
     * Gets the number of players viewing a specific dimension.
     * 
     * @param dimension The dimension to check
     * @return Number of players viewing this dimension
     */
    public static int getViewerCount(RegistryKey<World> dimension) {
        int count = 0;
        for (Set<RegistryKey<World>> dimensions : viewingDimensions.values()) {
            if (dimensions.contains(dimension)) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Checks if any players are viewing a specific dimension.
     * 
     * @param dimension The dimension to check
     * @return True if at least one player is viewing this dimension
     */
    public static boolean hasViewers(RegistryKey<World> dimension) {
        for (Set<RegistryKey<World>> dimensions : viewingDimensions.values()) {
            if (dimensions.contains(dimension)) {
                return true;
            }
        }
        return false;
    }
}
