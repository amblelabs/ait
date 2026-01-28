package dev.amble.ait.mixin.boti;

import dev.amble.ait.core.tardis.util.network.BOTIUpdateTracker;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * CRITICAL: Intercepts chunk tracking to include BOTI viewers.
 * 
 * This makes Minecraft send ALL packets (entities, particles, block entities, etc.)
 * to players viewing a dimension through BOTI, even though they're not physically there.
 * 
 * Without this, BOTI viewers only get our custom BOTIBlockUpdateS2CPacket and miss
 * everything else Minecraft normally sends to players in a dimension.
 */
@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ThreadedAnvilChunkStorageBOTIMixin {
    
    @Shadow @Final ServerWorld world;
    
    @Shadow public abstract List<ServerPlayerEntity> getPlayersWatchingChunk(ChunkPos pos, boolean onlyOnWatchDistanceEdge);
    
    /**
     * Intercepts broadcast packet sending to add BOTI viewers.
     * 
     * When Minecraft sends a packet to all players watching a chunk, we:
     * 1. Get the normal list of watchers (players physically in this dimension)
     * 2. Add BOTI viewers (players in other dimensions viewing this one)
     * 3. Send the packet to both groups
     */
    @Inject(
        method = "sendToOtherNearbyPlayers(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/packet/Packet;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void ait$interceptBroadcastPacket(ServerPlayerEntity except, Packet<?> packet, CallbackInfo ci) {
        // Get the chunk position from the player's current position
        ChunkPos chunkPos = except.getChunkPos();
        
        // Get normal watchers (players physically in this dimension)
        List<ServerPlayerEntity> watchers = this.getPlayersWatchingChunk(chunkPos, false);
        
        // Get BOTI viewers for this dimension
        List<ServerPlayerEntity> botiViewers = BOTIUpdateTracker.getViewers(world.getRegistryKey());
        
        // Send to normal watchers
        for (ServerPlayerEntity watcher : watchers) {
            if (watcher != except) {
                watcher.networkHandler.sendPacket(packet);
            }
        }
        
        // Send to BOTI viewers (they're not in the normal watcher list)
        for (ServerPlayerEntity viewer : botiViewers) {
            if (viewer != except && !watchers.contains(viewer)) {
                viewer.networkHandler.sendPacket(packet);
            }
        }
        
        // Cancel the original method - we've handled packet sending
        ci.cancel();
    }
}
