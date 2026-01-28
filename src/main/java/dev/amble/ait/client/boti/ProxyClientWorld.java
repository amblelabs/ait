package dev.amble.ait.client.boti;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.ColorResolver;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jetbrains.annotations.Nullable;

/**
 * A virtual world that provides block data from cached chunks for BOTI rendering.
 * 
 * This class implements BlockRenderView to provide block data to WorldGeometryRenderer
 * without requiring the actual dimension to be loaded on the client. For singleplayer,
 * it can directly access the server world. For multiplayer, it uses cached chunk data
 * received via packets (TODO: implement multiplayer support).
 */
public class ProxyClientWorld implements BlockRenderView {
    // Cache with bounded size to prevent memory leaks
    private static final int MAX_CACHED_CHUNKS = 256; // ~4MB of chunk data max
    private final Map<Long, ProxyChunk> chunkCache = new HashMap<>();
    private final RegistryKey<World> dimensionKey;
    
    // Cache the server world reference for singleplayer
    @Nullable
    private ServerWorld serverWorld;
    private boolean isValid;
    private long lastValidityCheck = 0;
    private static final long VALIDITY_CHECK_INTERVAL = 1000; // Check every second
    
    /**
     * Creates a new ProxyClientWorld for the given dimension.
     * 
     * @param dimensionKey The dimension to proxy
     */
    public ProxyClientWorld(RegistryKey<World> dimensionKey) {
        this.dimensionKey = dimensionKey;
        this.serverWorld = getServerWorld();
        this.isValid = serverWorld != null;
    }
    
    /**
     * Gets the server world for this dimension in singleplayer.
     * Returns null in multiplayer or if the server is not available.
     */
    @Nullable
    private ServerWorld getServerWorld() {
        MinecraftClient client = MinecraftClient.getInstance();
        
        // Only works in singleplayer
        if (client.isInSingleplayer() && client.getServer() != null) {
            IntegratedServer server = client.getServer();
            return server.getWorld(dimensionKey);
        }
        
        return null;
    }
    
    /**
     * Refreshes the server world reference. Call this if the dimension might have changed.
     */
    public void refreshServerWorld() {
        this.serverWorld = getServerWorld();
        this.isValid = serverWorld != null;
        this.lastValidityCheck = System.currentTimeMillis();
    }
    
    /**
     * Gets the cached chunk for the given section coordinates, or creates a new empty one.
     * Implements LRU-style eviction to prevent unbounded memory growth.
     */
    private ProxyChunk getOrCreateChunk(int sectionX, int sectionY, int sectionZ) {
        long key = packSectionPos(sectionX, sectionY, sectionZ);
        
        ProxyChunk chunk = chunkCache.get(key);
        if (chunk != null) {
            return chunk;
        }
        
        // Evict oldest chunks if cache is too large
        if (chunkCache.size() >= MAX_CACHED_CHUNKS) {
            // Simple eviction: remove first entry (arbitrary, but prevents unbounded growth)
            // TODO: Implement proper LRU cache for better eviction strategy
            Long keyToRemove = chunkCache.keySet().iterator().next();
            chunkCache.remove(keyToRemove);
        }
        
        chunk = new ProxyChunk(sectionX, sectionY, sectionZ);
        chunkCache.put(key, chunk);
        return chunk;
    }
    
    /**
     * Packs section coordinates into a single long for use as a map key.
     * Note: This supports coordinates within ±2 million blocks. Beyond this range,
     * coordinates will be truncated, potentially causing cache collisions.
     * For TARDIS interiors, this range is more than sufficient.
     */
    private static long packSectionPos(int x, int y, int z) {
        return ((long) x & 0x3FFFFFL) << 42 | ((long) y & 0xFFFFFL) << 20 | ((long) z & 0x3FFFFFL);
    }
    
    @Override
    public BlockState getBlockState(BlockPos pos) {
        // In singleplayer, directly access the server world for real-time updates
        if (serverWorld != null) {
            return serverWorld.getBlockState(pos);
        }
        
        // In multiplayer, use cached chunk data
        // TODO: Implement multiplayer chunk caching via packets
        int sectionX = pos.getX() >> 4;
        int sectionY = pos.getY() >> 4;
        int sectionZ = pos.getZ() >> 4;
        
        ProxyChunk chunk = chunkCache.get(packSectionPos(sectionX, sectionY, sectionZ));
        if (chunk == null) {
            return Blocks.AIR.getDefaultState();
        }
        
        int localX = pos.getX() & 15;
        int localY = pos.getY() & 15;
        int localZ = pos.getZ() & 15;
        
        return chunk.getBlockState(localX, localY, localZ);
    }
    
    @Override
    public FluidState getFluidState(BlockPos pos) {
        return getBlockState(pos).getFluidState();
    }
    
    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        // In singleplayer, directly access the server world
        if (serverWorld != null) {
            return serverWorld.getBlockEntity(pos);
        }
        
        // In multiplayer, we would need to deserialize block entities from NBT
        // TODO: Implement multiplayer block entity deserialization
        return null;
    }
    
    @Override
    public int getLightLevel(LightType type, BlockPos pos) {
        // Return full brightness for simplicity
        // TODO: Properly implement lighting if needed
        return 15;
    }
    
    @Override
    public int getBaseLightLevel(BlockPos pos, int ambientDarkness) {
        return 15;
    }
    
    @Override
    public float getBrightness(Direction direction, boolean shaded) {
        // Use default brightness values
        return direction == Direction.UP ? 1.0F : 
               direction == Direction.DOWN ? 0.5F : 
               direction == Direction.NORTH || direction == Direction.SOUTH ? 0.8F : 
               0.6F;
    }
    
    @Override
    public LightingProvider getLightingProvider() {
        // Return the server world's lighting provider if available
        if (serverWorld != null) {
            return serverWorld.getLightingProvider();
        }
        
        // In multiplayer, return null and rely on default lighting in rendering
        // TODO: Implement stub lighting provider for multiplayer if needed
        return null;
    }
    
    @Override
    public int getColor(BlockPos pos, ColorResolver colorResolver) {
        // Use default colors
        // TODO: Properly implement biome colors if needed
        return -1;
    }
    
    @Override
    public int getHeight() {
        return serverWorld != null ? serverWorld.getHeight() : 384;
    }
    
    @Override
    public int getBottomY() {
        return serverWorld != null ? serverWorld.getBottomY() : -64;
    }
    
    /**
     * Updates the chunk cache with new data received from the server.
     * This is called by the packet handler when chunk data arrives.
     * 
     * Expected NBT format (from BOTIDataS2CPacket):
     * - "block_states": Compound containing:
     *   - "palette": List of block state NBT compounds (encoded via BlockState.CODEC)
     *   - "data": LongArray of packed palette indices
     *   - "bitsPerEntry": Int specifying bits per palette entry (typically 4-15)
     * - "block_entities": Optional compound mapping "x_y_z" keys to block entity NBT
     * 
     * @param sectionX Section X coordinate
     * @param sectionY Section Y coordinate
     * @param sectionZ Section Z coordinate
     * @param chunkData NBT data from the server
     */
    public void updateChunkData(int sectionX, int sectionY, int sectionZ, net.minecraft.nbt.NbtCompound chunkData) {
        ProxyChunk chunk = getOrCreateChunk(sectionX, sectionY, sectionZ);
        chunk.deserialize(chunkData);
    }
    
    /**
     * Clears the chunk cache. Call this when the dimension changes or needs to be reloaded.
     */
    public void clearCache() {
        chunkCache.clear();
        refreshServerWorld();
    }
    
    /**
     * Checks if this proxy world is valid (has a server world in singleplayer).
     * Uses cached validity state with periodic refresh to avoid excessive lookups.
     */
    public boolean isValid() {
        long now = System.currentTimeMillis();
        if (now - lastValidityCheck > VALIDITY_CHECK_INTERVAL) {
            refreshServerWorld();
        }
        return isValid;
    }
    
    /**
     * Gets the dimension key for this proxy world.
     */
    public RegistryKey<World> getDimensionKey() {
        return dimensionKey;
    }
}
