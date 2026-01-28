package dev.amble.ait.client.boti;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkSection;

/**
 * Lightweight chunk representation for BOTI rendering.
 * Caches BlockStates and BlockEntities from server chunks for cross-dimensional rendering.
 * 
 * This class stores a 16x16x16 section of blocks and their associated block entities,
 * allowing the client to render TARDIS interior blocks even when the interior dimension
 * is not loaded.
 */
public class ProxyChunk {
    private final BlockState[][][] blockStates;
    private final Map<BlockPos, NbtCompound> blockEntities;
    private final int sectionX;
    private final int sectionY;
    private final int sectionZ;
    
    /**
     * Creates a new ProxyChunk for the given section coordinates.
     * 
     * @param sectionX Section X coordinate (chunk X)
     * @param sectionY Section Y coordinate (Y >> 4)
     * @param sectionZ Section Z coordinate (chunk Z)
     */
    public ProxyChunk(int sectionX, int sectionY, int sectionZ) {
        this.sectionX = sectionX;
        this.sectionY = sectionY;
        this.sectionZ = sectionZ;
        this.blockStates = new BlockState[16][16][16];
        this.blockEntities = new HashMap<>();
        
        // Initialize with air
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    blockStates[x][y][z] = Blocks.AIR.getDefaultState();
                }
            }
        }
    }
    
    /**
     * Gets the block state at the given position within this section.
     * 
     * @param x Local X coordinate (0-15)
     * @param y Local Y coordinate (0-15)
     * @param z Local Z coordinate (0-15)
     * @return The block state at the position
     */
    public BlockState getBlockState(int x, int y, int z) {
        if (x < 0 || x >= 16 || y < 0 || y >= 16 || z < 0 || z >= 16) {
            return Blocks.AIR.getDefaultState();
        }
        return blockStates[x][y][z];
    }
    
    /**
     * Sets the block state at the given position within this section.
     * 
     * @param x Local X coordinate (0-15)
     * @param y Local Y coordinate (0-15)
     * @param z Local Z coordinate (0-15)
     * @param state The block state to set
     */
    public void setBlockState(int x, int y, int z, BlockState state) {
        if (x < 0 || x >= 16 || y < 0 || y >= 16 || z < 0 || z >= 16) {
            return;
        }
        blockStates[x][y][z] = state;
    }
    
    /**
     * Gets the block entity NBT data at the given position.
     * 
     * @param pos The block position
     * @return The block entity NBT, or null if none exists
     */
    public NbtCompound getBlockEntity(BlockPos pos) {
        return blockEntities.get(pos);
    }
    
    /**
     * Sets the block entity NBT data at the given position.
     * 
     * @param pos The block position
     * @param nbt The block entity NBT data
     */
    public void setBlockEntity(BlockPos pos, NbtCompound nbt) {
        if (nbt != null) {
            blockEntities.put(pos, nbt);
        }
    }
    
    /**
     * Deserializes chunk data from NBT received from the server.
     * 
     * @param chunkData The NBT compound containing chunk data
     */
    public void deserialize(NbtCompound chunkData) {
        if (!chunkData.contains("block_states")) {
            return;
        }
        
        NbtCompound blockStatesNbt = chunkData.getCompound("block_states");
        
        // Read palette
        NbtList palette = blockStatesNbt.getList("palette", 10); // 10 = TAG_COMPOUND
        BlockState[] paletteArray = new BlockState[palette.size()];
        
        for (int i = 0; i < palette.size(); i++) {
            NbtCompound stateNbt = palette.getCompound(i);
            paletteArray[i] = BlockState.CODEC.parse(NbtOps.INSTANCE, stateNbt)
                    .result()
                    .orElse(Blocks.AIR.getDefaultState());
        }
        
        // Read data array
        long[] data = blockStatesNbt.getLongArray("data");
        int bitsPerEntry = blockStatesNbt.getInt("bitsPerEntry");
        
        // Validate bitsPerEntry to prevent division by zero and invalid bit operations
        if (bitsPerEntry <= 0 || bitsPerEntry > 64) {
            System.err.println("Invalid bitsPerEntry value: " + bitsPerEntry + ". Skipping chunk deserialization.");
            return;
        }
        
        int entriesPerLong = 64 / bitsPerEntry;
        
        // Decode block states
        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    int index = y * 256 + z * 16 + x;
                    int longIndex = index / entriesPerLong;
                    int offset = (index % entriesPerLong) * bitsPerEntry;
                    
                    if (longIndex >= data.length) {
                        continue;
                    }
                    
                    long value = data[longIndex];
                    int paletteIndex = (int) ((value >> offset) & ((1L << bitsPerEntry) - 1));
                    
                    // Validate palette index to prevent array out of bounds
                    if (paletteIndex >= 0 && paletteIndex < paletteArray.length) {
                        blockStates[x][y][z] = paletteArray[paletteIndex];
                    } else {
                        System.err.println("Invalid palette index: " + paletteIndex + " (palette size: " + paletteArray.length + ")");
                    }
                }
            }
        }
        
        // Read block entities
        if (chunkData.contains("block_entities")) {
            NbtCompound blockEntitiesNbt = chunkData.getCompound("block_entities");
            
            for (String key : blockEntitiesNbt.getKeys()) {
                String[] parts = key.split("_");
                if (parts.length == 3) {
                    try {
                        int x = Integer.parseInt(parts[0]);
                        int y = Integer.parseInt(parts[1]);
                        int z = Integer.parseInt(parts[2]);
                        
                        BlockPos pos = new BlockPos(
                                (sectionX << 4) + x,
                                (sectionY << 4) + y,
                                (sectionZ << 4) + z
                        );
                        
                        blockEntities.put(pos, blockEntitiesNbt.getCompound(key));
                    } catch (NumberFormatException e) {
                        // Skip invalid keys
                    }
                }
            }
        }
    }
    
    /**
     * Gets the section X coordinate.
     */
    public int getSectionX() {
        return sectionX;
    }
    
    /**
     * Gets the section Y coordinate.
     */
    public int getSectionY() {
        return sectionY;
    }
    
    /**
     * Gets the section Z coordinate.
     */
    public int getSectionZ() {
        return sectionZ;
    }
}
