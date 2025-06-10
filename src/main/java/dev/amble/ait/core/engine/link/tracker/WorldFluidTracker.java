package dev.amble.ait.core.engine.link.tracker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

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
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof IFluidLink link && !(be.isRemoved())) {
            return link;
        }

        return null;
    }
}
