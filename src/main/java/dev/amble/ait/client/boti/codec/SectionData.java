package dev.amble.ait.client.boti.codec;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

/**
 * Efficient storage for a 16x16x16 chunk section using palette compression.
 * Blocks are stored as bit-packed palette indices instead of full BlockState objects.
 */
public class SectionData {
    public final int chunkX;
    public final int chunkZ;
    public final int sectionY;
    private final BlockState[] palette;
    private final long[] data;
    private final int bitsPerEntry;
    
    public SectionData(int chunkX, int chunkZ, int sectionY, BlockState[] palette, int bitsPerEntry, long[] data) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.sectionY = sectionY;
        this.palette = palette;
        this.bitsPerEntry = bitsPerEntry;
        this.data = data;
    }
    
    /**
     * Gets a block state at the given local coordinates (0-15).
     * This lazily decodes from the compressed format.
     * 
     * @param x Local X coordinate (0-15)
     * @param y Local Y coordinate (0-15)
     * @param z Local Z coordinate (0-15)
     * @return The BlockState at that position
     */
    public BlockState getBlockState(int x, int y, int z) {
        // Calculate block index using Minecraft's ordering: Y * 256 + Z * 16 + X
        int index = (y << 8) | (z << 4) | x;
        
        // Calculate which long contains this block's palette index
        int entriesPerLong = 64 / bitsPerEntry;
        int longIndex = index / entriesPerLong;
        int offset = (index % entriesPerLong) * bitsPerEntry;
        
        // Extract palette index from packed data
        long mask = (1L << bitsPerEntry) - 1L;
        int paletteIndex = (int)((data[longIndex] >> offset) & mask);
        
        // Bounds check
        if (paletteIndex < 0 || paletteIndex >= palette.length) {
            return Blocks.AIR.getDefaultState();
        }
        
        return palette[paletteIndex];
    }
    
    /**
     * Gets the palette size for this section.
     */
    public int getPaletteSize() {
        return palette.length;
    }
    
    /**
     * Gets the data array length (in longs).
     */
    public int getDataLength() {
        return data.length;
    }
}
