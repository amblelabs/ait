package dev.amble.ait.core.tardis.util.network.c2s;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.util.network.BOTIUpdateTracker;

/**
 * Client-to-server packet to unregister a player from viewing a dimension via BOTI.
 * This stops the server from sending block update notifications for that dimension.
 */
public class BOTIUnregisterViewerC2SPacket implements FabricPacket {
    public static final PacketType<BOTIUnregisterViewerC2SPacket> TYPE = 
        PacketType.create(AITMod.id("boti_unregister_viewer"), BOTIUnregisterViewerC2SPacket::new);
    
    private final RegistryKey<World> dimension;
    
    /**
     * Creates a new viewer unregistration packet.
     * 
     * @param dimension The dimension the client is no longer viewing
     */
    public BOTIUnregisterViewerC2SPacket(RegistryKey<World> dimension) {
        this.dimension = dimension;
    }
    
    /**
     * Reads packet from network buffer.
     */
    public BOTIUnregisterViewerC2SPacket(PacketByteBuf buf) {
        this.dimension = buf.readRegistryKey(RegistryKeys.WORLD);
    }
    
    @Override
    public void write(PacketByteBuf buf) {
        buf.writeRegistryKey(dimension);
    }
    
    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
    
    /**
     * Handles the packet on the server side.
     * Unregisters the player from viewing the specified dimension.
     */
    public boolean handle(ServerPlayerEntity player, PacketSender response) {
        if (player == null) {
            return false;
        }
        
        BOTIUpdateTracker.unregisterViewer(player, dimension);
        return true;
    }
}
