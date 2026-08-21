package dev.amble.ait.core.engine.link.tracker;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.WorldChunk;

import dev.amble.ait.core.engine.link.IFluidLink;

public class WorldFluidTracker {
    public static HashMap<Direction, IFluidLink> getConnections(ServerWorld world, BlockPos pos, @Nullable Direction ignore) {
        // get all fluid links around the given position
        HashMap<Direction, IFluidLink> connections = new HashMap<>();

        for (Direction dir : Direction.values()) {
            if (dir == ignore) continue;

            IFluidLink found = query(world, pos.offset(dir));
            if (found == null) continue;

            connections.put(dir, found);
        }

        return connections;
    }
    public static LinkedList<IFluidLink> getAllConnections(ServerWorld world, BlockPos pos, @Nullable Direction ignore, HashSet<BlockPos> checkedPositions) {
        LinkedList<IFluidLink> list = new LinkedList<>();
        HashMap<Direction, IFluidLink> connections;

        IFluidLink here = query(world, pos);
        if (here == null) {
            return list;
        }

        if (checkedPositions == null) checkedPositions = new HashSet<>();
        checkedPositions.add(pos);

        LinkedList<BlockPos> toCheck = new LinkedList<>();
        toCheck.add(pos);

        while (!toCheck.isEmpty()) {
            BlockPos currentPos = toCheck.poll();
            connections = getConnections(world, currentPos, ignore);

            for (Direction direction : connections.keySet()) {
                if (direction == ignore) continue;

                BlockPos newPos = currentPos.offset(direction);
                if (checkedPositions.contains(newPos)) continue;
                if (checkedPositions.add(newPos)) {
                    toCheck.add(newPos);
                    list.add(connections.get(direction));
                }
            }
        }

        return list;
    }
    public static IFluidLink query(ServerWorld world, BlockPos pos) {
        WorldChunk chunk = world.getChunkManager().getWorldChunk(pos.getX() >> 4, pos.getZ() >> 4);
        if (chunk == null)
            return null;

        BlockEntity be = chunk.getBlockEntity(pos);
        if (be instanceof IFluidLink link && !(be.isRemoved())) {
            return link;
        }

        return null;
    }

    /**
     * Breadth-first traversal of a connected {@link IFluidLink} component without loading chunks.
     * The returned component must only be applied when {@link Discovery#truncated()} is false;
     * otherwise it is an incomplete snapshot retained solely for deduplication and diagnostics.
     */
    public static Discovery discover(ServerWorld world, BlockPos start, int maxNodes) {
        LinkedHashMap<BlockPos, IFluidLink> visited = new LinkedHashMap<>();
        if (maxNodes <= 0)
            return new Discovery(visited, true);

        BlockPos rootPos = start.toImmutable();
        IFluidLink first = query(world, rootPos);
        if (first == null)
            return new Discovery(visited, false);

        Deque<BlockPos> queue = new ArrayDeque<>();
        queue.add(rootPos);
        visited.put(rootPos, first);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            for (Direction direction : Direction.values()) {
                BlockPos next = current.offset(direction).toImmutable();
                if (visited.containsKey(next)) continue;

                IFluidLink link = query(world, next);
                if (link == null) continue;

                // Detect the first node beyond the limit without mutating part of the network.
                if (visited.size() >= maxNodes)
                    return new Discovery(visited, true);

                visited.put(next, link);
                queue.add(next);
            }
        }

        return new Discovery(visited, false);
    }

    /**
     * Compatibility accessor for callers that only need a bounded traversal result.
     */
    public static LinkedHashMap<BlockPos, IFluidLink> bfs(ServerWorld world, BlockPos start, int maxNodes) {
        return discover(world, start, maxNodes).component();
    }

    public record Discovery(LinkedHashMap<BlockPos, IFluidLink> component, boolean truncated) {
    }
}
