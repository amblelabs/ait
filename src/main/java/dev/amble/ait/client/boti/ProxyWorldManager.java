package dev.amble.ait.client.boti;

import dev.amble.ait.client.boti.codec.SectionData;
import dev.loqor.client.ProxyClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global manager for ProxyClientWorld instances.
 * Manages cached proxy worlds for different dimensions used in BOTI rendering.
 */
public class ProxyWorldManager {
    private static final ProxyWorldManager INSTANCE = new ProxyWorldManager();
    
    private final Map<RegistryKey<World>, ProxyClientWorld> proxyWorlds = new ConcurrentHashMap<>();
    
    private ProxyWorldManager() {
    }
    
    public static ProxyWorldManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * Gets or creates a ProxyClientWorld for the given dimension.
     */
    public ProxyClientWorld getOrCreate(RegistryKey<World> dimension) {
        return proxyWorlds.computeIfAbsent(dimension, ProxyClientWorld::new);
    }
    
    /**
     * Receives section data and routes it to the appropriate ProxyClientWorld.
     */
    public void receiveSectionData(RegistryKey<World> dimension, SectionData data) {
        ProxyClientWorld world = proxyWorlds.get(dimension);
        if (world != null) {
            world.receiveSectionData(data);
        }
    }
    
    /**
     * Clears all cached proxy worlds.
     */
    public void clearAll() {
        proxyWorlds.clear();
    }
    
    /**
     * Clears a specific proxy world.
     */
    public void clear(RegistryKey<World> dimension) {
        ProxyClientWorld world = proxyWorlds.remove(dimension);
        if (world != null) {
            world.clearCache();
        }
    }
    
    /**
     * Gets the number of active proxy worlds.
     */
    public int getActiveWorldCount() {
        return proxyWorlds.size();
    }
    
    /**
     * Gets all proxy worlds (for debugging).
     * Returns an unmodifiable view to prevent external modification.
     */
    public Map<RegistryKey<World>, ProxyClientWorld> getAllProxyWorlds() {
        return java.util.Collections.unmodifiableMap(proxyWorlds);
    }
}
