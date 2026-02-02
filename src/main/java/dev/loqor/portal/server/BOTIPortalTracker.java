package dev.loqor.portal.server;

import dev.amble.ait.AITMod;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server-side tracker for players viewing BOTI portals
 * Manages chunk loading and updates for players viewing through portals
 */
public class BOTIPortalTracker {
    private static BOTIPortalTracker instance;
    
    // Track which players are viewing which portals
    private final Map<ServerPlayerEntity, Set<PortalView>> playerPortalViews = new WeakHashMap<>();
    
    // Track which chunks are being viewed through portals
    private final Map<ChunkPos, Set<ServerPlayerEntity>> chunkViewers = new ConcurrentHashMap<>();
    
    private BOTIPortalTracker() {
        // Private constructor for singleton
    }
    
    public static BOTIPortalTracker getInstance() {
        if (instance == null) {
            instance = new BOTIPortalTracker();
        }
        return instance;
    }
    
    /**
     * Called when a player starts watching a portal
     * @param player The player viewing the portal
     * @param tardisId The UUID of the TARDIS
     * @param isExteriorView True if viewing interior from exterior, false if viewing exterior from interior
     * @param targetPos The position being viewed through the portal
     * @param targetWorld The world being viewed through the portal
     */
    public void onPlayerStartWatchingPortal(ServerPlayerEntity player, UUID tardisId, boolean isExteriorView, 
                                           BlockPos targetPos, ServerWorld targetWorld) {
        Set<PortalView> views = playerPortalViews.computeIfAbsent(player, k -> new HashSet<>());
        
        PortalView view = new PortalView(tardisId, isExteriorView, targetPos, targetWorld);
        views.add(view);
        
        AITMod.LOGGER.info("Player {} started watching portal for TARDIS {} (exterior view: {})", 
            player.getName().getString(), tardisId, isExteriorView);
    }
    
    /**
     * Called when a player stops watching a portal
     * @param player The player who was viewing the portal
     * @param tardisId The UUID of the TARDIS
     */
    public void onPlayerStopWatchingPortal(ServerPlayerEntity player, UUID tardisId) {
        Set<PortalView> views = playerPortalViews.get(player);
        if (views != null) {
            views.removeIf(view -> view.tardisId.equals(tardisId));
            if (views.isEmpty()) {
                playerPortalViews.remove(player);
            }
        }
        
        AITMod.LOGGER.info("Player {} stopped watching portal for TARDIS {}", 
            player.getName().getString(), tardisId);
    }
    
    /**
     * Periodic tick to send chunk updates to players viewing portals
     * Should be called every server tick or periodically
     */
    public void tick() {
        // For each player viewing a portal
        for (Map.Entry<ServerPlayerEntity, Set<PortalView>> entry : playerPortalViews.entrySet()) {
            ServerPlayerEntity player = entry.getKey();
            Set<PortalView> views = entry.getValue();
            
            // For each portal view this player has
            for (PortalView view : views) {
                // Calculate which chunks should be loaded for this view
                int viewDistance = 8; // Could be configurable
                ChunkPos centerChunk = new ChunkPos(view.targetPos);
                
                // Load chunks in a radius around the target position
                for (int x = -viewDistance; x <= viewDistance; x++) {
                    for (int z = -viewDistance; z <= viewDistance; z++) {
                        ChunkPos chunkPos = new ChunkPos(centerChunk.x + x, centerChunk.z + z);
                        
                        // Track that this player is viewing this chunk
                        chunkViewers.computeIfAbsent(chunkPos, k -> new HashSet<>()).add(player);
                        
                        // TODO: Send chunk data to player if not already sent
                        // This would involve creating packets and sending them to the client
                        // The packets would be handled by PortalDataManager on the client side
                    }
                }
            }
        }
    }
    
    /**
     * Called when a chunk is loaded on the server
     * Notify relevant players who are viewing this chunk through a portal
     */
    public void onChunkLoad(ServerWorld world, ChunkPos pos) {
        Set<ServerPlayerEntity> viewers = chunkViewers.get(pos);
        if (viewers != null && !viewers.isEmpty()) {
            AITMod.LOGGER.debug("Chunk {} loaded, {} players viewing through portal", pos, viewers.size());
            // TODO: Send chunk data to viewers
        }
    }
    
    /**
     * Called when a chunk is unloaded on the server
     * Clean up tracking for this chunk
     */
    public void onChunkUnload(ServerWorld world, ChunkPos pos) {
        Set<ServerPlayerEntity> viewers = chunkViewers.remove(pos);
        if (viewers != null && !viewers.isEmpty()) {
            AITMod.LOGGER.debug("Chunk {} unloaded, was viewed by {} players", pos, viewers.size());
            // TODO: Notify clients that chunk is no longer available
        }
    }
    
    /**
     * Clean up when a player disconnects
     */
    public void onPlayerDisconnect(ServerPlayerEntity player) {
        playerPortalViews.remove(player);
        
        // Remove player from all chunk viewer sets
        chunkViewers.values().forEach(viewers -> viewers.remove(player));
        
        AITMod.LOGGER.info("Cleaned up portal tracking for disconnected player {}", 
            player.getName().getString());
    }
    
    /**
     * Represents a portal that a player is viewing through
     */
    public static class PortalView {
        public final UUID tardisId;
        public final boolean isExteriorView;
        public final BlockPos targetPos;
        public final ServerWorld targetWorld;
        
        public PortalView(UUID tardisId, boolean isExteriorView, BlockPos targetPos, ServerWorld targetWorld) {
            this.tardisId = tardisId;
            this.isExteriorView = isExteriorView;
            this.targetPos = targetPos;
            this.targetWorld = targetWorld;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PortalView that = (PortalView) o;
            return isExteriorView == that.isExteriorView && 
                   tardisId.equals(that.tardisId);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(tardisId, isExteriorView);
        }
    }
}
