package dev.amble.ait.core.tardis.util.network.s2c;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.boti.ProxyWorldManager;
import dev.loqor.client.ProxyClientWorld;

/**
 * Server-to-client packet for notifying clients about block updates in dimensions
 * they are viewing via BOTI (Bigger On The Inside).
 * 
 * This enables real-time mesh updates when blocks change in TARDIS interiors
 * or other dimensions being rendered through BOTI portals.
 */
public class BOTIBlockUpdateS2CPacket implements FabricPacket {
    public static final PacketType<BOTIBlockUpdateS2CPacket> TYPE = 
        PacketType.create(AITMod.id("boti_block_update"), BOTIBlockUpdateS2CPacket::new);
    
    private final RegistryKey<World> dimension;
    private final BlockPos pos;
    private final BlockState newState;
    
    /**
     * Creates a new block update packet.
     * 
     * @param dimension The dimension where the block changed
     * @param pos Position of the changed block
     * @param newState New block state
     */
    public BOTIBlockUpdateS2CPacket(RegistryKey<World> dimension, BlockPos pos, BlockState newState) {
        this.dimension = dimension;
        this.pos = pos;
        this.newState = newState;
    }
    
    /**
     * Reads packet from network buffer.
     */
    public BOTIBlockUpdateS2CPacket(PacketByteBuf buf) {
        this.dimension = buf.readRegistryKey(RegistryKeys.WORLD);
        this.pos = buf.readBlockPos();
        // Read block state as raw ID
        int rawId = buf.readVarInt();
        this.newState = Block.getStateFromRawId(rawId);
    }
    
    @Override
    public void write(PacketByteBuf buf) {
        buf.writeRegistryKey(dimension);
        buf.writeBlockPos(pos);
        // Write block state as raw ID
        buf.writeVarInt(Block.getRawIdFromState(newState));
    }
    
    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
    
    /**
     * Handles the packet on the client side.
     * Routes the block update to the appropriate ProxyClientWorld.
     */
    public boolean handle(ClientPlayerEntity source, PacketSender response) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        if (client.world == null) {
            return false;
        }
        
        // Get the proxy world for this dimension
        ProxyClientWorld proxyWorld = ProxyWorldManager.getInstance().getOrCreate(dimension);
        
        // Notify the proxy world of the block update
        proxyWorld.onBlockUpdate(pos, newState);
        
        return true;
    }
}
