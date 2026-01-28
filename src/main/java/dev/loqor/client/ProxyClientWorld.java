package dev.loqor.client;

import dev.loqor.ProxyChunk;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.ColorResolver;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionTypes;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * A proxy world that fetches block data from a target dimension (server-side)
 * and caches it for client-side rendering
 */
public class ProxyClientWorld implements BlockRenderView {
    private final RegistryKey<World> targetDimension;
    private final Map<ChunkPos, ProxyChunk> cachedChunks = new HashMap<>();
    private final MinecraftClient client;
    private final World fallbackWorld;

    public ProxyClientWorld(RegistryKey<World> targetDimension) {
        this.targetDimension = targetDimension;
        this.client = MinecraftClient.getInstance();
        this.fallbackWorld = client.world;
    }

    /**
     * Main block state accessor - tries to get from server world, falls back to cache
     */
    @Override
    public BlockState getBlockState(BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        ProxyChunk chunk = cachedChunks.get(chunkPos);

        if (chunk == null || chunk.isEmpty()) {
            // Try to fetch from server
            chunk = fetchChunk(chunkPos);
            cachedChunks.put(chunkPos, chunk);
        }

        return chunk.getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return getBlockState(pos).getFluidState();
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        ProxyChunk chunk = cachedChunks.get(chunkPos);

        if (chunk == null || chunk.isEmpty()) {
            chunk = fetchChunk(chunkPos);
            cachedChunks.put(chunkPos, chunk);
        }

        return chunk.getBlockEntity(pos);
    }

    /**
     * Fetches a chunk from the server world (singleplayer) or requests it (multiplayer)
     */
    private ProxyChunk fetchChunk(ChunkPos chunkPos) {
        // Singleplayer: direct access to integrated server
        if (client.getServer() != null) {
            ServerWorld serverWorld = client.getServer().getWorld(targetDimension);
            if (serverWorld != null) {
                Chunk serverChunk = serverWorld.getChunk(chunkPos.x, chunkPos.z);
                return ProxyChunk.fromServerChunk(serverChunk);
            }
        }

        // Multiplayer: request via packet (TODO - implement packet system)
        // For now, return empty chunk
        return ProxyChunk.empty();
    }

    /**
     * Called when chunk data is received from server (multiplayer)
     */
    public void receiveChunkData(ChunkPos chunkPos, byte[] compressedData) {
        ProxyChunk chunk = ProxyChunk.fromBytes(chunkPos, compressedData);
        cachedChunks.put(chunkPos, chunk);
    }

    /**
     * Clears the chunk cache
     */
    public void clearCache() {
        cachedChunks.clear();
    }

    /**
     * Pre-loads chunks in a radius around a center position
     */
    public void preloadChunks(BlockPos center, int radius) {
        int centerChunkX = center.getX() >> 4;
        int centerChunkZ = center.getZ() >> 4;

        for (int x = centerChunkX - radius; x <= centerChunkX + radius; x++) {
            for (int z = centerChunkZ - radius; z <= centerChunkZ + radius; z++) {
                ChunkPos chunkPos = new ChunkPos(x, z);
                if (!cachedChunks.containsKey(chunkPos)) {
                    fetchChunk(chunkPos);
                }
            }
        }
    }

    public RegistryKey<World> getTargetDimension() {
        return targetDimension;
    }

    // BlockView required methods - delegate to fallback world or provide defaults

    @Override
    public int getHeight() {
        return fallbackWorld != null ? fallbackWorld.getHeight() : 384;
    }

    @Override
    public int getBottomY() {
        return fallbackWorld != null ? fallbackWorld.getBottomY() : -64;
    }

    @Override
    public int getLightLevel(LightType type, BlockPos pos) {
        return 15; // Full brightness for simplicity
    }

    @Override
    public int getBaseLightLevel(BlockPos pos, int ambientDarkness) {
        return 15;
    }

    @Override
    public boolean isSkyVisible(BlockPos pos) {
        return true;
    }

    @Override
    public RegistryEntry<Biome> getBiomeFabric(BlockPos pos) {
        if (fallbackWorld != null) {
            return fallbackWorld.getBiome(pos);
        }
        // Return a default biome
        DynamicRegistryManager registryManager = client.getNetworkHandler().getRegistryManager();
        return registryManager.get(RegistryKeys.BIOME).entryOf(BiomeKeys.PLAINS);
    }

    @Override
    public int getColor(BlockPos pos, ColorResolver colorResolver) {
        return colorResolver.getColor(getBiomeFabric(pos).value(), pos.getX(), pos.getZ());
    }

    @Override
    public float getBrightness(Direction direction, boolean shaded) {
        return 1.0f;
    }

    @Nullable
    public LightingProvider getLightingProvider() {
        return fallbackWorld != null ? fallbackWorld.getLightingProvider() : null;
    }
}