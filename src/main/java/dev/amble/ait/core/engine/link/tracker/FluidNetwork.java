package dev.amble.ait.core.engine.link.tracker;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.engine.link.IFluidLink;
import dev.amble.ait.core.engine.link.IFluidSource;
import dev.amble.ait.core.engine.link.block.FluidLinkBlockEntity;

/**
 * Synchronous, in-place rebuild of a fluid-link connected component.
 *
 * Cables / subsystems / engines call {@link #rebuildFrom(ServerWorld, BlockPos)} from their
 * own block-update callbacks; the rebuild walks the component once via BFS, picks a
 * deterministic source, then assigns {@code source} / {@code last} / {@code lastPos} /
 * {@code powered} to every connected node via {@link FluidLinkBlockEntity#applyNetworkAssignment}.
 *
 * Re-running the rebuild on a settled network is a no-op: {@code applyNetworkAssignment}
 * compares against the existing fields and only broadcasts when something actually changed.
 */
public final class FluidNetwork {
    private static final int LARGE_NETWORK_WARNING_SIZE = 4096;

    private FluidNetwork() {}

    /**
     * Rebuild the component containing {@code seed}. If {@code seed} no longer holds an
     * {@link IFluidLink} block entity (e.g. the source caller is mid-break), use
     * {@link #rebuildAround(ServerWorld, BlockPos)} instead.
     */
    public static void rebuildFrom(ServerWorld world, BlockPos seed) {
        if (world == null || seed == null) return;
        LinkedHashMap<BlockPos, IFluidLink> component = WorldFluidTracker.bfsFully(world, seed);
        if (component.isEmpty()) return;
        rebuildComponent(seed, component);
    }

    /**
     * Rebuild every component that touches one of the 6 neighbours of {@code center}, walking
     * each component exactly once. Use when {@code center} itself no longer holds a fluid-link
     * block entity (i.e. on-break).
     */
    public static void rebuildAround(ServerWorld world, BlockPos center) {
        if (world == null || center == null) return;
        Set<BlockPos> handled = new HashSet<>();
        for (Direction dir : Direction.values()) {
            BlockPos n = center.offset(dir);
            if (handled.contains(n)) continue;
            if (WorldFluidTracker.query(world, n) == null) continue;
            LinkedHashMap<BlockPos, IFluidLink> component = WorldFluidTracker.bfsFully(world, n);
            if (component.isEmpty()) continue;
            handled.addAll(component.keySet());
            rebuildComponent(n, component);
        }
    }

    private static void rebuildComponent(BlockPos seed, LinkedHashMap<BlockPos, IFluidLink> component) {
        if (component.size() >= LARGE_NETWORK_WARNING_SIZE) {
            AITMod.LOGGER.warn("Large fluid network at {} contains {} nodes; rebuilding the complete component",
                    seed, component.size());
        }

        BlockPos sourcePos = pickSource(component);
        IFluidSource source = sourcePos == null ? null : (IFluidSource) component.get(sourcePos);

        if (source == null) {
            for (Map.Entry<BlockPos, IFluidLink> e : component.entrySet()) {
                if (e.getValue() instanceof FluidLinkBlockEntity be && !(be instanceof IFluidSource)) {
                    be.applyNetworkAssignment(null, null, null, false);
                }
            }
            return;
        }

        Map<BlockPos, BlockPos> parent = spanningTree(component, sourcePos);
        boolean sourcePowered = source.level() > 0;

        for (Map.Entry<BlockPos, IFluidLink> e : component.entrySet()) {
            BlockPos pos = e.getKey();
            IFluidLink link = e.getValue();
            if (link instanceof IFluidSource) continue;
            if (!(link instanceof FluidLinkBlockEntity be)) continue;

            BlockPos parentPos = parent.get(pos);
            if (parentPos == null) {
                be.applyNetworkAssignment(null, null, null, false);
                continue;
            }
            be.applyNetworkAssignment(source, component.get(parentPos), parentPos, sourcePowered);
        }
    }

    private static BlockPos pickSource(LinkedHashMap<BlockPos, IFluidLink> component) {
        BlockPos bestPos = null;
        IFluidSource bestSource = null;

        for (Map.Entry<BlockPos, IFluidLink> e : component.entrySet()) {
            if (!(e.getValue() instanceof IFluidSource candidate)) continue;

            if (bestSource == null || isPreferredSource(candidate, e.getKey(), bestSource, bestPos)) {
                bestSource = candidate;
                bestPos = e.getKey();
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

    private static int compareBlockPos(BlockPos a, BlockPos b) {
        int c = Integer.compare(a.getX(), b.getX());
        if (c != 0) return c;
        c = Integer.compare(a.getY(), b.getY());
        if (c != 0) return c;
        return Integer.compare(a.getZ(), b.getZ());
    }

    private static Map<BlockPos, BlockPos> spanningTree(LinkedHashMap<BlockPos, IFluidLink> component, BlockPos root) {
        Map<BlockPos, BlockPos> parent = new HashMap<>(component.size());
        Deque<BlockPos> queue = new ArrayDeque<>();
        queue.add(root);
        parent.put(root, root);
        while (!queue.isEmpty()) {
            BlockPos cur = queue.poll();
            for (Direction dir : Direction.values()) {
                BlockPos next = cur.offset(dir);
                if (!component.containsKey(next)) continue;
                if (parent.containsKey(next)) continue;
                parent.put(next, cur);
                queue.add(next);
            }
        }
        return parent;
    }
}
