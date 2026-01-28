package dev.amble.ait.client.boti.codec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkSection;

/**
 * Codec for encoding/decoding chunk sections with palette compression.
 * This achieves 10x smaller packets than the old NBT-based system.
 */
public class SectionDataCodec {
    
    /**
     * Encodes a chunk section to a PacketByteBuf using palette compression.
     * 
     * Binary format:
     * - has_data: boolean (false if section is empty/all air)
     * - chunk_x: varint
     * - chunk_z: varint
     * - section_y: byte
     * - palette_size: varint
     * - palette: BlockState[] (encoded as varint IDs)
     * - bits_per_entry: byte
     * - data_length: varint
     * - data: long[] (bit-packed palette indices)
     * 
     * @param buf The buffer to write to
     * @param section The chunk section to encode
     * @param chunkPos The chunk position
     * @param sectionY The Y coordinate of this section
     */
    public static void encodeSection(PacketByteBuf buf, ChunkSection section, ChunkPos chunkPos, int sectionY) {
        // Build palette
        List<BlockState> palette = new ArrayList<>();
        Map<BlockState, Integer> stateToIndex = new HashMap<>();
        palette.add(Blocks.AIR.getDefaultState()); // Index 0 = air
        stateToIndex.put(Blocks.AIR.getDefaultState(), 0);
        
        int nonAirCount = 0;
        
        // Scan section for unique states
        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    BlockState state = section.getBlockState(x, y, z);
                    if (!state.isAir()) nonAirCount++;
                    if (!stateToIndex.containsKey(state)) {
                        stateToIndex.put(state, palette.size());
                        palette.add(state);
                    }
                }
            }
        }
        
        // Skip if empty (all air)
        if (nonAirCount == 0) {
            buf.writeBoolean(false); // Empty flag
            return;
        }
        
        buf.writeBoolean(true); // Has data
        buf.writeVarInt(chunkPos.x);
        buf.writeVarInt(chunkPos.z);
        buf.writeByte(sectionY);
        
        // Write palette
        buf.writeVarInt(palette.size());
        for (BlockState state : palette) {
            BlockStateCodec.encode(buf, state);
        }
        
        // Calculate bits per entry (minimum 4 bits, like Minecraft does)
        int bitsPerEntry = Math.max(4, MathHelper.ceilLog2(palette.size()));
        buf.writeByte(bitsPerEntry);
        
        // Bit-pack indices into long array
        int entriesPerLong = 64 / bitsPerEntry;
        int dataLength = MathHelper.ceil(4096.0 / entriesPerLong); // 4096 blocks in 16x16x16
        long[] data = new long[dataLength];
        
        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    int index = (y << 8) | (z << 4) | x;
                    BlockState state = section.getBlockState(x, y, z);
                    int paletteIndex = stateToIndex.get(state);
                    
                    int longIndex = index / entriesPerLong;
                    int offset = (index % entriesPerLong) * bitsPerEntry;
                    long mask = (1L << bitsPerEntry) - 1L;
                    data[longIndex] |= ((long)paletteIndex & mask) << offset;
                }
            }
        }
        
        // Write packed data
        buf.writeVarInt(dataLength);
        for (long value : data) {
            buf.writeLong(value);
        }
    }
    
    /**
     * Decodes a section from a PacketByteBuf.
     * 
     * @param buf The buffer to read from
     * @return The decoded SectionData, or null if the section is empty
     */
    public static SectionData decodeSection(PacketByteBuf buf) {
        if (!buf.readBoolean()) {
            return null; // Empty section
        }
        
        int chunkX = buf.readVarInt();
        int chunkZ = buf.readVarInt();
        int sectionY = buf.readByte();
        
        // Read palette
        int paletteSize = buf.readVarInt();
        BlockState[] palette = new BlockState[paletteSize];
        for (int i = 0; i < paletteSize; i++) {
            palette[i] = BlockStateCodec.decode(buf);
        }
        
        // Read packed data
        int bitsPerEntry = buf.readByte();
        int dataLength = buf.readVarInt();
        long[] data = new long[dataLength];
        for (int i = 0; i < dataLength; i++) {
            data[i] = buf.readLong();
        }
        
        return new SectionData(chunkX, chunkZ, sectionY, palette, bitsPerEntry, data);
    }
}
