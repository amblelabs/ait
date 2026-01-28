package dev.loqor;

import dev.amble.ait.client.boti.codec.SectionData;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * A lightweight chunk representation that caches block data from server chunks.
 * Now uses efficient palette-based storage for sections instead of per-block HashMap.
 */
public class ProxyChunk {
    private final ChunkPos pos;
    private final Map<Integer, SectionData> sections = new HashMap<>(); // sectionY -> data
    private final Map<BlockPos, BlockEntity> blockEntities = new HashMap<>();
    private boolean isEmpty = false;

    public ProxyChunk(ChunkPos pos) {
        this.pos = pos;
    }

    /**
     * Creates an empty proxy chunk (placeholder until data arrives)
     */
    public static ProxyChunk empty() {
        ProxyChunk chunk = new ProxyChunk(new ChunkPos(0, 0));
        chunk.isEmpty = true;
        return chunk;
    }

    /**
     * Creates a proxy chunk from a server chunk
     */
    public static ProxyChunk fromServerChunk(Chunk serverChunk) {
        ProxyChunk proxy = new ProxyChunk(serverChunk.getPos());

        // Copy all block states in the chunk (using old method for now, keeps compatibility)
        // This is only used in singleplayer where we have direct access to server chunks
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = serverChunk.getBottomY(); y < serverChunk.getTopY(); y++) {
                    BlockPos pos = new BlockPos(
                            serverChunk.getPos().getStartX() + x,
                            y,
                            serverChunk.getPos().getStartZ() + z
                    );

                    // Copy block entity if present
                    BlockEntity be = serverChunk.getBlockEntity(pos);
                    if (be != null) {
                        proxy.blockEntities.put(pos, be);
                    }
                }
            }
        }

        return proxy;
    }
    
    /**
     * Sets a section in this chunk using efficient compressed storage.
     * This is called when receiving section data from the server.
     */
    public void setSection(SectionData data) {
        sections.put(data.sectionY, data);
    }

    /**
     * Fast block state lookup using efficient section storage.
     * Falls back to server chunk access in singleplayer if section not loaded.
     */
    public BlockState getBlockState(BlockPos pos) {
        if (isEmpty) {
            return Blocks.AIR.getDefaultState();
        }
        
        // Calculate section Y coordinate
        int sectionY = pos.getY() >> 4;
        SectionData section = sections.get(sectionY);
        
        if (section == null) {
            // Section not loaded yet, return air
            return Blocks.AIR.getDefaultState();
        }
        
        // Get local coordinates within the section
        int localX = pos.getX() & 15;
        int localY = pos.getY() & 15;
        int localZ = pos.getZ() & 15;
        
        return section.getBlockState(localX, localY, localZ);
    }

    /**
     * Marks a section as dirty, requiring it to be re-requested from the server.
     * This is used when a block update occurs and we need fresh data.
     * 
     * Note: Direct block state updates in the palette-compressed format would require
     * rebuilding the entire section's palette and data array, which is expensive.
     * Instead, we invalidate the section and let it be re-requested on the next render.
     * 
     * @param pos Position of the block that changed
     * @return true if section was invalidated, false if not loaded
     */
    public boolean invalidateSection(BlockPos pos) {
        if (isEmpty) {
            return false;
        }
        
        // Calculate section Y coordinate
        int sectionY = pos.getY() >> 4;
        
        // Remove the section from cache - it will be re-requested
        SectionData removed = sections.remove(sectionY);
        return removed != null;
    }

    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        if (isEmpty) {
            return null;
        }
        return blockEntities.get(pos);
    }

    public ChunkPos getPos() {
        return pos;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    /**
     * Deserializes chunk data from compressed bytes
     */
    public static ProxyChunk fromBytes(ChunkPos pos, byte[] compressedData) {
        // TODO: Implement deserialization for multiplayer support
        // For now, return empty
        return empty();
    }

    /**
     * Serializes chunk data to compressed bytes
     */
    public byte[] toBytes() {
        // TODO: Implement serialization for multiplayer support
        return new byte[0];
    }
}