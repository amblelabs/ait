package dev.amble.ait.core.tardis.util.network.s2c;

import java.util.List;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.boti.ProxyWorldManager;
import dev.amble.ait.client.boti.codec.SectionData;
import dev.amble.ait.client.boti.codec.SectionDataCodec;

/**
 * Server-to-client packet containing highly compressed chunk data.
 * Replaces the old BOTIDataS2CPacket with a 10x smaller binary format.
 * 
 * Uses palette compression and bit-packing to minimize bandwidth usage.
 * Target: <2KB per section with ~50 unique blocks.
 */
public class BOTIChunkDataBatchS2CPacket implements FabricPacket {
    public static final PacketType<BOTIChunkDataBatchS2CPacket> TYPE = 
        PacketType.create(AITMod.id("send_chunk_batch"), BOTIChunkDataBatchS2CPacket::new);
    
    private final RegistryKey<World> dimension;
    private final PacketByteBuf data;
    
    /**
     * Constructor for creating the packet on the server side.
     */
    public BOTIChunkDataBatchS2CPacket(ServerWorld world, List<ChunkPos> chunks, 
                                       BlockPos centerPos, int radius) {
        this.dimension = world.getRegistryKey();
        
        // We'll encode all sections into a single buffer
        this.data = new PacketByteBuf(Unpooled.buffer());
        
        int sectionCount = 0;
        
        // Reserve space for section count (we'll write it at the end)
        int sectionCountIndex = data.writerIndex();
        data.writeVarInt(0); // Placeholder
        
        for (ChunkPos chunkPos : chunks) {
            WorldChunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
            
            // Only send sections in range of centerPos (optimization)
            int minY = Math.max(chunk.getBottomSectionCoord(), (centerPos.getY() - 32) >> 4);
            int maxY = Math.min(chunk.getTopSectionCoord() - 1, (centerPos.getY() + 32) >> 4);
            
            for (int sectionY = minY; sectionY <= maxY; sectionY++) {
                int sectionIndex = chunk.sectionCoordToIndex(sectionY);
                ChunkSection section = chunk.getSection(sectionIndex);
                
                if (!section.isEmpty()) {
                    SectionDataCodec.encodeSection(data, section, chunkPos, sectionY);
                    sectionCount++;
                }
            }
        }
        
        // Go back and write the actual section count
        int currentIndex = data.writerIndex();
        data.writerIndex(sectionCountIndex);
        data.writeVarInt(sectionCount);
        data.writerIndex(currentIndex);
    }
    
    /**
     * Constructor for receiving the packet on the client side.
     */
    public BOTIChunkDataBatchS2CPacket(PacketByteBuf buf) {
        this.dimension = buf.readRegistryKey(RegistryKeys.WORLD);
        // Read all data into our own buffer
        this.data = new PacketByteBuf(buf.copy());
    }
    
    @Override
    public void write(PacketByteBuf buf) {
        buf.writeRegistryKey(dimension);
        // Write our data buffer to the network buffer
        buf.writeBytes(data.copy());
    }
    
    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
    
    @SuppressWarnings("unchecked")
    public <T> boolean handle(ClientPlayerEntity source, PacketSender response) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        if (client.world == null) return false;
        
        // Read section count
        int sectionCount = data.readVarInt();
        
        // Decode all sections and send to ProxyWorldManager
        for (int i = 0; i < sectionCount; i++) {
            SectionData sectionData = SectionDataCodec.decodeSection(data);
            if (sectionData != null) {
                ProxyWorldManager.getInstance().receiveSectionData(dimension, sectionData);
            }
        }
        
        return true;
    }
}
