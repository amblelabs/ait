package dev.loqor.client;

import dev.amble.ait.client.boti.codec.SectionData;
import dev.amble.ait.core.tardis.util.network.c2s.BOTIChunkBatchRequestC2SPacket;
import dev.loqor.ProxyChunk;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A proxy world that fetches block data from a target dimension (server-side)
 * and caches it for client-side rendering.
 * Now supports optimized batch chunk requests and renderer notifications.
 */
public class ProxyClientWorld implements BlockRenderView {
    private final RegistryKey<World> targetDimension;
    private final Map<ChunkPos, ProxyChunk> cachedChunks = new ConcurrentHashMap<>();
    private final Set<ChunkPos> requestedChunks = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final MinecraftClient client;
    private final World fallbackWorld;

    // Reference to the renderer for chunk update notifications
    private WorldGeometryRenderer renderer;
    private BlockPos lastCenterPos;

    // Track when chunks were last requested to avoid spam
    private final Map<ChunkPos, Long> requestTimestamps = new ConcurrentHashMap<>();
    private static final long REQUEST_COOLDOWN_MS = 1000; // 1 second cooldown per chunk

    public ProxyClientWorld(RegistryKey<World> targetDimension) {
        this.targetDimension = targetDimension;
        this.client = MinecraftClient.getInstance();
        this.fallbackWorld = client.world;
    }

    /**
     * Links this proxy world to a renderer for chunk update notifications
     */
    public void setRenderer(WorldGeometryRenderer renderer) {
        this.renderer = renderer;
    }



    /**
     * Main block state accessor - uses cached chunk data received via packets
     */
    @Override
    public BlockState getBlockState(BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        ProxyChunk chunk = cachedChunks.get(chunkPos);

        if (chunk == null || chunk.isEmpty()) {
            // Chunk not in cache - will be requested via preloadChunks()
            return fallbackWorld.getBlockState(pos);
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
            // Chunk not in cache - will be requested via preloadChunks()
            return null;
        }

        return chunk.getBlockEntity(pos);
    }



    /**
     * Called when chunk data is received from server (multiplayer)
     */
    public void receiveChunkData(ChunkPos chunkPos, byte[] compressedData) {
        ProxyChunk chunk = ProxyChunk.fromBytes(chunkPos, compressedData);
        cachedChunks.put(chunkPos, chunk);
        requestedChunks.remove(chunkPos);
        notifyChunkUpdate(chunkPos);
    }

    /**
     * Clears the chunk cache
     */
    public void clearCache() {
        cachedChunks.clear();
        requestedChunks.clear();
        requestTimestamps.clear();
    }



    /**
     * Pre-loads chunks in a radius around a center position using optimized batch requests.
     * This is the main preloading method called by WorldGeometryRenderer.
     * Works identically in both singleplayer and multiplayer via packet system.
     */
    public void preloadChunks(BlockPos center, int radius) {
        this.lastCenterPos = center;
        List<ChunkPos> toRequest = new ArrayList<>();
        int centerChunkX = center.getX() >> 4;
        int centerChunkZ = center.getZ() >> 4;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                ChunkPos pos = new ChunkPos(centerChunkX + x, centerChunkZ + z);

                // Skip if already cached
                if (cachedChunks.containsKey(pos)) {
                    continue;
                }

                // Check cooldown
                Long lastRequest = requestTimestamps.get(pos);
                long now = System.currentTimeMillis();
                if (lastRequest != null && (now - lastRequest) < REQUEST_COOLDOWN_MS) {
                    continue;
                }

                if (requestedChunks.add(pos)) {
                    toRequest.add(pos);
                    requestTimestamps.put(pos, now);
                }
            }
        }

        // Send batch request via packet system (works for both singleplayer and multiplayer)
        if (!toRequest.isEmpty() && ClientPlayNetworking.canSend(BOTIChunkBatchRequestC2SPacket.TYPE)) {
            ClientPlayNetworking.send(new BOTIChunkBatchRequestC2SPacket(
                    targetDimension, center, (byte)radius, toRequest
            ));
        }
    }

    /**
     * Receives section data from server (multiplayer).
     * Called when BOTIChunkDataBatchS2CPacket is received.
     */
    public void receiveSectionData(SectionData data) {
        ChunkPos chunkPos = new ChunkPos(data.chunkX, data.chunkZ);
        ProxyChunk chunk = cachedChunks.computeIfAbsent(chunkPos, ProxyChunk::new);
        chunk.setSection(data);
        requestedChunks.remove(chunkPos);
        notifyChunkUpdate(chunkPos);
    }

    /**
     * Notifies the renderer that a chunk has been updated/loaded
     */
    private void notifyChunkUpdate(ChunkPos chunkPos) {
        if (renderer != null && lastCenterPos != null) {
            renderer.onChunkUpdate(chunkPos, lastCenterPos);
        }
    }

    /**
     * Checks if a chunk is loaded in the cache
     */
    public boolean isChunkLoaded(ChunkPos chunkPos) {
        ProxyChunk chunk = cachedChunks.get(chunkPos);
        return chunk != null && !chunk.isEmpty();
    }

    /**
     * Gets the number of cached chunks
     */
    public int getCachedChunkCount() {
        return cachedChunks.size();
    }

    /**
     * Gets the number of pending chunk requests
     */
    public int getPendingRequestCount() {
        return requestedChunks.size();
    }

    public RegistryKey<World> getTargetDimension() {
        return targetDimension;
    }

    /**
     * Called when a block is updated in the target dimension.
     * Invalidates the affected chunk and triggers a rebuild.
     * 
     * @param pos Position of the updated block
     * @param newState New block state
     */
    public void onBlockUpdate(BlockPos pos, BlockState newState) {
        ChunkPos chunkPos = new ChunkPos(pos);
        
        // Update the cached chunk if it exists
        ProxyChunk chunk = cachedChunks.get(chunkPos);
        if (chunk != null) {
            // Mark chunk as needing update by removing from requested set
            // This will allow it to be re-requested
            requestedChunks.remove(chunkPos);
            requestTimestamps.remove(chunkPos);
            
            // Notify renderer to trigger rebuild
            notifyChunkUpdate(chunkPos);
        }
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