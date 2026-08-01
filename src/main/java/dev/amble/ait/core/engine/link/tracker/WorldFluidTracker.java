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
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

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
        Chunk chunk = world.getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL, false);
        if (chunk == null)
            return null;

        BlockEntity be = chunk.getBlockEntity(pos);
        if (be instanceof IFluidLink link && !(be.isRemoved())) {
            return link;
        }

        return null;
    }

    /**
     * Breadth-first traversal of every {@link IFluidLink} reachable from {@code start}.
     * Insertion order is BFS order, so iterating the result yields nodes by distance from {@code start}.
     * Stops cleanly at {@code maxNodes} to bound worst-case cost on pathological networks.
     *
     * The returned map is mutable and owned by the caller. Keys are stored as immutable {@link BlockPos}.
     */
    public static LinkedHashMap<BlockPos, IFluidLink> bfs(ServerWorld world, BlockPos start, int maxNodes) {
        return bfs(world, start, maxNodes, true);
    }

    /**
     * Breadth-first traversal of the complete connected component. Network assignment must use
     * this overload so a size limit can never leave half of a component with stale references.
     */
    public static LinkedHashMap<BlockPos, IFluidLink> bfsFully(ServerWorld world, BlockPos start) {
        return bfs(world, start, 0, false);
    }

    private static LinkedHashMap<BlockPos, IFluidLink> bfs(ServerWorld world, BlockPos start, int maxNodes,
                                                           boolean bounded) {
        LinkedHashMap<BlockPos, IFluidLink> visited = new LinkedHashMap<>();
        BlockPos rootPos = start.toImmutable();
        IFluidLink first = query(world, rootPos);
        if (first == null) return visited;

        Deque<BlockPos> queue = new ArrayDeque<>();
        queue.add(rootPos);
        visited.put(rootPos, first);

        while (!queue.isEmpty() && (!bounded || visited.size() < maxNodes)) {
            BlockPos cur = queue.poll();
            for (Direction dir : Direction.values()) {
                BlockPos next = cur.offset(dir).toImmutable();
                if (visited.containsKey(next)) continue;
                IFluidLink link = query(world, next);
                if (link == null) continue;
                visited.put(next, link);
                queue.add(next);
                if (bounded && visited.size() >= maxNodes) break;
            }
        }

        return visited;
    }
}
