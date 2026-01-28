package dev.loqor;

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
 * A lightweight chunk representation that caches block data from server chunks
 */
public class ProxyChunk {
    private final ChunkPos pos;
    private final Map<BlockPos, BlockState> blockStates = new HashMap<>();
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

        // Copy all block states in the chunk
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = serverChunk.getBottomY(); y < serverChunk.getTopY(); y++) {
                    BlockPos pos = new BlockPos(
                            serverChunk.getPos().getStartX() + x,
                            y,
                            serverChunk.getPos().getStartZ() + z
                    );

                    BlockState state = serverChunk.getBlockState(pos);
                    if (!state.isAir()) {
                        proxy.blockStates.put(pos, state);

                        // Copy block entity if present
                        BlockEntity be = serverChunk.getBlockEntity(pos);
                        if (be != null) {
                            proxy.blockEntities.put(pos, be);
                        }
                    }
                }
            }
        }

        return proxy;
    }

    public BlockState getBlockState(BlockPos pos) {
        if (isEmpty) {
            return Blocks.AIR.getDefaultState();
        }
        return blockStates.getOrDefault(pos, Blocks.AIR.getDefaultState());
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