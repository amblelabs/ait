package dev.loqor.portal.server;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.util.network.c2s.BOTIChunkRequestC2SPacket;
import dev.amble.ait.core.tardis.util.network.c2s.StartWatchingPortalC2SPacket;
import dev.amble.ait.core.tardis.util.network.c2s.StopWatchingPortalC2SPacket;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

/**
 * Hooks BOTIPortalTracker into server-side events
 */
public class BOTIServerEvents {
    private static boolean initialized = false;
    
    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        
        AITMod.LOGGER.info("Initializing BOTI server events");
        
        // Register packet handlers
        ServerPlayNetworking.registerGlobalReceiver(BOTIChunkRequestC2SPacket.TYPE, 
            (packet, player, responseSender) -> {
                packet.handle(player, responseSender);
            });
        
        ServerPlayNetworking.registerGlobalReceiver(StartWatchingPortalC2SPacket.TYPE,
            (packet, player, responseSender) -> {
                packet.handle(player, responseSender);
            });
        
        ServerPlayNetworking.registerGlobalReceiver(StopWatchingPortalC2SPacket.TYPE,
            (packet, player, responseSender) -> {
                packet.handle(player, responseSender);
            });
        
        // Hook into server tick to update portal tracking
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            try {
                BOTIPortalTracker.getInstance().tick();
            } catch (Exception e) {
                AITMod.LOGGER.error("Error in BOTIPortalTracker tick", e);
            }
        });
        
        // Hook into player disconnect to cleanup tracking
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            try {
                BOTIPortalTracker.getInstance().onPlayerDisconnect(handler.getPlayer());
            } catch (Exception e) {
                AITMod.LOGGER.error("Error cleaning up BOTI tracking for player", e);
            }
        });
        
        AITMod.LOGGER.info("BOTI server events initialized successfully");
    }
}
