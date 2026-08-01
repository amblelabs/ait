package dev.amble.ait.core.engine.link.tracker;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.engine.link.IFluidLink;
import dev.amble.ait.core.engine.link.IFluidSource;
import dev.amble.ait.core.engine.link.block.FluidLinkBlockEntity;

/**
 * Synchronous, transactional rebuilds of loaded fluid-link components.
 *
 * <p>The discovery pass never loads chunks and is bounded. A component is only reassigned after
 * the complete loaded component has been discovered, so hitting the limit cannot leave half of a
 * network pointing at stale or conflicting sources.</p>
 */
public final class FluidNetwork {
    private static final int MAX_NETWORK_SIZE = 4096;
    private static final long WARNING_INTERVAL_TICKS = 20L * 10L;
    private static final Map<RegistryKey<World>, Long> LAST_OVERSIZED_WARNING = new HashMap<>();

    private static boolean initialized;

    private FluidNetwork() {}

    /**
     * Restores in-memory network assignments when chunks containing fluid links are loaded.
     */
    public static void init() {
        if (initialized) return;
        initialized = true;

        net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents.CHUNK_LOAD.register((world, chunk) ->
                world.getServer().execute(() -> rebuildLoadedChunk(world, chunk)));
    }

    /**
     * Rebuild the component containing {@code seed}. If {@code seed} no longer holds an
     * {@link IFluidLink} block entity (for example while it is being broken), use
     * {@link #rebuildAround(ServerWorld, BlockPos)} instead.
     */
    public static void rebuildFrom(ServerWorld world, BlockPos seed) {
        if (world == null || seed == null) return;

        WorldFluidTracker.Discovery discovery = WorldFluidTracker.discover(world, seed, MAX_NETWORK_SIZE);
        applyDiscovery(world, seed, discovery);
    }

    /**
     * Rebuild every loaded component touching one of the six neighbours of {@code center}, walking
     * each component at most once. Used when {@code center} itself no longer contains a fluid link.
     */
    public static void rebuildAround(ServerWorld world, BlockPos center) {
        if (world == null || center == null) return;

        Set<BlockPos> handled = new HashSet<>();

        for (Direction direction : Direction.values()) {
            BlockPos neighbour = center.offset(direction).toImmutable();
            if (handled.contains(neighbour) || WorldFluidTracker.query(world, neighbour) == null) continue;

            WorldFluidTracker.Discovery discovery = WorldFluidTracker.discover(world, neighbour, MAX_NETWORK_SIZE);
            handled.addAll(discovery.component().keySet());
            applyDiscovery(world, neighbour, discovery);
        }
    }

    private static void rebuildLoadedChunk(ServerWorld world, WorldChunk chunk) {
        Set<BlockPos> handled = new HashSet<>();

        // Rebuilding can notify block listeners, so iterate over a stable snapshot of the map.
        for (Map.Entry<BlockPos, BlockEntity> entry : new ArrayList<>(chunk.getBlockEntities().entrySet())) {
            BlockEntity blockEntity = entry.getValue();
            if (!(blockEntity instanceof IFluidLink) || blockEntity.isRemoved()) continue;

            BlockPos seed = entry.getKey().toImmutable();
            if (handled.contains(seed)) continue;

            WorldFluidTracker.Discovery discovery = WorldFluidTracker.discover(world, seed, MAX_NETWORK_SIZE);
            handled.addAll(discovery.component().keySet());
            applyDiscovery(world, seed, discovery);
        }
    }

    private static boolean applyDiscovery(ServerWorld world, BlockPos seed, WorldFluidTracker.Discovery discovery) {
        LinkedHashMap<BlockPos, IFluidLink> component = discovery.component();
        if (component.isEmpty()) return true;

        if (discovery.truncated()) {
            warnOversizedNetwork(world, seed);
            return false;
        }

        rebuildComponent(component);
        return true;
    }

    private static void warnOversizedNetwork(ServerWorld world, BlockPos seed) {
        long now = world.getServer().getTicks();
        RegistryKey<World> key = world.getRegistryKey();
        Long previous = LAST_OVERSIZED_WARNING.get(key);

        if (previous != null && now >= previous && now - previous < WARNING_INTERVAL_TICKS) return;

        LAST_OVERSIZED_WARNING.put(key, now);
        AITMod.LOGGER.warn("Fluid network at {} in {} exceeds the {} node safety limit; keeping its previous assignments",
                seed, key.getValue(), MAX_NETWORK_SIZE);
    }

    private static void rebuildComponent(LinkedHashMap<BlockPos, IFluidLink> component) {
        BlockPos sourcePos = pickSource(component);
        IFluidSource source = sourcePos == null ? null : (IFluidSource) component.get(sourcePos);

        if (source == null) {
            for (Map.Entry<BlockPos, IFluidLink> entry : component.entrySet()) {
                if (entry.getValue() instanceof FluidLinkBlockEntity blockEntity
                        && !(blockEntity instanceof IFluidSource)) {
                    blockEntity.applyNetworkAssignment(null, null, null, false);
                }
            }
            return;
        }

        Map<BlockPos, BlockPos> parent = spanningTree(component, sourcePos);
        boolean sourcePowered = source.level() > 0;

        for (Map.Entry<BlockPos, IFluidLink> entry : component.entrySet()) {
            BlockPos pos = entry.getKey();
            IFluidLink link = entry.getValue();
            if (link instanceof IFluidSource || !(link instanceof FluidLinkBlockEntity blockEntity)) continue;

            BlockPos parentPos = parent.get(pos);
            if (parentPos == null) {
                blockEntity.applyNetworkAssignment(null, null, null, false);
                continue;
            }

            blockEntity.applyNetworkAssignment(source, component.get(parentPos), parentPos, sourcePowered);
        }
    }

    private static BlockPos pickSource(LinkedHashMap<BlockPos, IFluidLink> component) {
        BlockPos bestPos = null;
        IFluidSource bestSource = null;

        for (Map.Entry<BlockPos, IFluidLink> entry : component.entrySet()) {
            if (!(entry.getValue() instanceof IFluidSource candidate)) continue;

            if (bestSource == null || isPreferredSource(candidate, entry.getKey(), bestSource, bestPos)) {
                bestSource = candidate;
                bestPos = entry.getKey();
            }
        }

        return bestPos;
    }

    private static boolean isPreferredSource(IFluidSource candidate, BlockPos candidatePos,
                                             IFluidSource current, BlockPos currentPos) {
        double candidateLevel = candidate.level();
        double currentLevel = current.level();
        boolean candidatePowered = candidateLevel > 0;
        boolean currentPowered = currentLevel > 0;

        if (candidatePowered != currentPowered) return candidatePowered;

        if (candidatePowered) {
            int byLevel = Double.compare(candidateLevel, currentLevel);
            if (byLevel != 0) return byLevel > 0;
        }

        return compareBlockPos(candidatePos, currentPos) < 0;
    }

    private static int compareBlockPos(BlockPos first, BlockPos second) {
        int comparison = Integer.compare(first.getX(), second.getX());
        if (comparison != 0) return comparison;

        comparison = Integer.compare(first.getY(), second.getY());
        if (comparison != 0) return comparison;

        return Integer.compare(first.getZ(), second.getZ());
    }

    private static Map<BlockPos, BlockPos> spanningTree(LinkedHashMap<BlockPos, IFluidLink> component, BlockPos root) {
        Map<BlockPos, BlockPos> parent = new HashMap<>(component.size());
        Deque<BlockPos> queue = new ArrayDeque<>();
        queue.add(root);
        parent.put(root, root);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            for (Direction direction : Direction.values()) {
                BlockPos next = current.offset(direction);
                if (!component.containsKey(next) || parent.containsKey(next)) continue;

                parent.put(next, current);
                queue.add(next);
            }
        }

        return parent;
    }
}