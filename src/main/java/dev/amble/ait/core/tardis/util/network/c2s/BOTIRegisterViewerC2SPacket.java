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
 * Client-to-server packet to register a player as viewing a dimension via BOTI.
 * This allows the server to send block update notifications to the client.
 */
public class BOTIRegisterViewerC2SPacket implements FabricPacket {
    public static final PacketType<BOTIRegisterViewerC2SPacket> TYPE = 
        PacketType.create(AITMod.id("boti_register_viewer"), BOTIRegisterViewerC2SPacket::new);
    
    private final RegistryKey<World> dimension;
    
    /**
     * Creates a new viewer registration packet.
     * 
     * @param dimension The dimension the client is viewing
     */
    public BOTIRegisterViewerC2SPacket(RegistryKey<World> dimension) {
        this.dimension = dimension;
    }
    
    /**
     * Reads packet from network buffer.
     */
    public BOTIRegisterViewerC2SPacket(PacketByteBuf buf) {
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
     * Registers the player as viewing the specified dimension.
     */
    @SuppressWarnings("unchecked")
    public <T> boolean handle(ServerPlayerEntity player, PacketSender response) {
        if (player == null) {
            return false;
        }
        
        BOTIUpdateTracker.registerViewer(player, dimension);
        return true;
    }
}
