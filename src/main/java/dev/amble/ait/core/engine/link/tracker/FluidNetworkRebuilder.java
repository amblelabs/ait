package dev.amble.ait.core.engine.link.tracker;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.engine.link.IFluidLink;
import dev.amble.ait.core.engine.link.IFluidSource;
import dev.amble.ait.core.engine.link.block.FluidLinkBlockEntity;

/**
 * Owns recomputing the {@code source}/{@code last}/{@code powered} fields of every
 * {@link FluidLinkBlockEntity} in a connected component whenever the topology changes.
 *
 * Cables/subsystems no longer mutate their own links incrementally - they just call
 * {@link #markDirty(ServerWorld, BlockPos)} on placement, break, neighbour update, or
 * source level transition, and the rebuilder runs once per server tick to apply a
 * deterministic BFS-from-source spanning tree to the whole component.
 */
public final class FluidNetworkRebuilder {
    private static final int MAX_NETWORK_SIZE = 4096;

    private static final Map<ServerWorld, FluidNetworkRebuilder> INSTANCES = new WeakHashMap<>();

    private Set<BlockPos> dirty = new HashSet<>();

    private FluidNetworkRebuilder() {}

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(FluidNetworkRebuilder::tick);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> INSTANCES.clear());
    }

    public static void markDirty(ServerWorld world, BlockPos pos) {
        if (world == null || pos == null) return;
        get(world).dirty.add(pos.toImmutable());
    }

    /** Mark a node and its 6 neighbours dirty - useful when a fluid-link block has just been broken. */
    public static void markBrokenAt(ServerWorld world, BlockPos pos) {
        if (world == null || pos == null) return;
        FluidNetworkRebuilder rb = get(world);
        rb.dirty.add(pos.toImmutable());
        for (Direction dir : Direction.values()) {
            rb.dirty.add(pos.offset(dir).toImmutable());
        }
    }

    private static FluidNetworkRebuilder get(ServerWorld world) {
        return INSTANCES.computeIfAbsent(world, w -> new FluidNetworkRebuilder());
    }

    private static void tick(ServerWorld world) {
        FluidNetworkRebuilder rb = INSTANCES.get(world);
        if (rb == null || rb.dirty.isEmpty()) return;

        Set<BlockPos> seedQueue = rb.dirty;
        rb.dirty = new HashSet<>();

        Set<BlockPos> processed = new HashSet<>();
        for (BlockPos seed : seedQueue) {
            if (processed.contains(seed)) continue;
            rebuildComponent(world, seed, processed);
        }
    }

    private static void rebuildComponent(ServerWorld world, BlockPos seed, Set<BlockPos> processed) {
        LinkedHashMap<BlockPos, IFluidLink> component = WorldFluidTracker.bfs(world, seed, MAX_NETWORK_SIZE);
        if (component.isEmpty()) {
            processed.add(seed);
            return;
        }
        if (component.size() >= MAX_NETWORK_SIZE) {
            AITMod.LOGGER.warn("Fluid network at {} hit max size {} - assignment may be incomplete", seed, MAX_NETWORK_SIZE);
        }

        BlockPos sourcePos = null;
        IFluidSource source = null;
        for (Map.Entry<BlockPos, IFluidLink> e : component.entrySet()) {
            if (e.getValue() instanceof IFluidSource src) {
                sourcePos = e.getKey();
                source = src;
                break;
            }
        }

        if (source == null) {
            for (Map.Entry<BlockPos, IFluidLink> e : component.entrySet()) {
                if (e.getValue() instanceof FluidLinkBlockEntity be && !(be instanceof IFluidSource)) {
                    be.applyNetworkAssignment(null, null, null, false);
                }
                processed.add(e.getKey());
            }
            return;
        }

        boolean sourcePowered = source.level() > 0;

        Deque<BlockPos> queue = new ArrayDeque<>();
        Map<BlockPos, BlockPos> parent = new java.util.HashMap<>(component.size());
        parent.put(sourcePos, sourcePos);
        queue.add(sourcePos);

        Set<BlockPos> assigned = new HashSet<>(component.size());
        assigned.add(sourcePos);

        while (!queue.isEmpty()) {
            BlockPos cur = queue.poll();
            for (Direction dir : Direction.values()) {
                BlockPos next = cur.offset(dir);
                if (!component.containsKey(next)) continue;
                if (!assigned.add(next)) continue;
                parent.put(next, cur);
                queue.add(next);
            }
        }

        for (Map.Entry<BlockPos, IFluidLink> e : component.entrySet()) {
            BlockPos pos = e.getKey();
            IFluidLink link = e.getValue();
            processed.add(pos);

            if (link instanceof IFluidSource) continue;
            if (!(link instanceof FluidLinkBlockEntity be)) continue;

            if (!assigned.contains(pos)) {
                be.applyNetworkAssignment(null, null, null, false);
                continue;
            }

            BlockPos parentPos = parent.get(pos);
            IFluidLink parentLink = component.get(parentPos);
            be.applyNetworkAssignment(source, parentLink, parentPos, sourcePowered);
        }
    }
}
