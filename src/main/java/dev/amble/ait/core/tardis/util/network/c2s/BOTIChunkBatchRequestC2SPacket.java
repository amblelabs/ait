package dev.amble.ait.core.tardis.util.network.c2s;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.boti.codec.SectionDataCodec;
import dev.amble.ait.core.tardis.util.network.s2c.BOTIChunkDataBatchS2CPacket;

/**
 * Client-to-server packet for requesting multiple chunks at once.
 * Replaces the old single-chunk request system with batch requests for better performance.
 */
public class BOTIChunkBatchRequestC2SPacket implements FabricPacket {
    public static final PacketType<BOTIChunkBatchRequestC2SPacket> TYPE = 
        PacketType.create(AITMod.id("request_chunk_batch"), BOTIChunkBatchRequestC2SPacket::new);
    
    private final RegistryKey<World> dimension;
    private final BlockPos centerPos;
    private final byte radius;
    private final List<ChunkPos> chunks;
    
    public BOTIChunkBatchRequestC2SPacket(RegistryKey<World> dimension, BlockPos centerPos, 
                                          byte radius, List<ChunkPos> chunks) {
        this.dimension = dimension;
        this.centerPos = centerPos;
        this.radius = radius;
        this.chunks = chunks;
    }
    
    public BOTIChunkBatchRequestC2SPacket(PacketByteBuf buf) {
        this.dimension = buf.readRegistryKey(RegistryKeys.WORLD);
        this.centerPos = buf.readBlockPos();
        this.radius = buf.readByte();
        
        int chunkCount = buf.readVarInt();
        this.chunks = new ArrayList<>(chunkCount);
        for (int i = 0; i < chunkCount; i++) {
            int x = buf.readInt();
            int z = buf.readInt();
            this.chunks.add(new ChunkPos(x, z));
        }
    }
    
    @Override
    public void write(PacketByteBuf buf) {
        buf.writeRegistryKey(dimension);
        buf.writeBlockPos(centerPos);
        buf.writeByte(radius);
        
        buf.writeVarInt(chunks.size());
        for (ChunkPos chunk : chunks) {
            buf.writeInt(chunk.x);
            buf.writeInt(chunk.z);
        }
    }
    
    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
    
    @SuppressWarnings("unchecked")
    public <T> boolean handle(ServerPlayerEntity source, PacketSender response) {
        if (source == null) return false;
        
        MinecraftServer server = source.getServer();
        if (server == null) return false;
        
        ServerWorld world = server.getWorld(this.dimension);
        if (world == null) return false;
        
        // Send the batch response
        ServerPlayNetworking.send(source, 
            new BOTIChunkDataBatchS2CPacket(world, this.chunks, this.centerPos, this.radius));
        
        return true;
    }
}
