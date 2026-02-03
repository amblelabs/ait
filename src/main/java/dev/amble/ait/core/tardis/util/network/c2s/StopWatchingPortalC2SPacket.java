package dev.amble.ait.core.tardis.util.network.c2s;

import dev.loqor.portal.server.BOTIPortalTracker;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import dev.amble.ait.AITMod;

import java.util.UUID;

/**
 * Client-to-Server packet to notify server when player stops watching a BOTI portal
 */
public class StopWatchingPortalC2SPacket implements FabricPacket {
    public static final PacketType<StopWatchingPortalC2SPacket> TYPE = 
        PacketType.create(AITMod.id("stop_watching_portal"), StopWatchingPortalC2SPacket::new);
    
    private final UUID tardisId;
    
    public StopWatchingPortalC2SPacket(UUID tardisId) {
        this.tardisId = tardisId;
    }
    
    public StopWatchingPortalC2SPacket(PacketByteBuf buf) {
        this.tardisId = buf.readUuid();
    }
    
    @Override
    public void write(PacketByteBuf buf) {
        buf.writeUuid(tardisId);
    }
    
    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
    
    public boolean handle(ServerPlayerEntity player, PacketSender responseSender) {
        if (player == null || player.getServer() == null) {
            return false;
        }
        
        // Notify the portal tracker on the server thread
        player.getServer().execute(() -> {
            BOTIPortalTracker.getInstance().onPlayerStopWatchingPortal(player, tardisId);
        });
        
        return true;
    }
}
