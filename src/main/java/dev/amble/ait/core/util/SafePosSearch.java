package dev.amble.ait.core.util;

import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.drtheo.queue.api.ActionQueue;
import dev.drtheo.queue.api.util.Value;
import dev.drtheo.scheduler.api.TimeUnit;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;

public class SafePosSearch {

    private static final int SAFE_RADIUS = 3;

    @Nullable
    public static Pair<ActionQueue, Value<BlockPos>> findSafe(CachedDirectedGlobalPos globalPos,
                                                              TravelHandlerBase.GroundSearch vSearch,
                                                              boolean hSearch) {
        ServerWorld world = globalPos.getWorld();
        BlockPos pos = globalPos.getPos();

        final Chunk chunk = globalPos.getWorld().getChunk(pos);

        if (isSafe(chunk, pos))
            return null;

        Value<BlockPos> result = new Value<>(null);
        ActionQueue queue = new ActionQueue();

        if (hSearch) {
            queue = findSafeXZ(queue, result, world, pos, SAFE_RADIUS).thenRun(() -> {
                if (result.value != null)
                    globalPos.pos(result.value);
            });
        }

        queue = switch (vSearch) {
            case CEILING -> findSafeCeiling(queue, result, world, pos);
            case FLOOR -> findSafeFloor(queue, result, world, pos);
            case MEDIAN -> findSafeMedian(queue, result, world, pos);
            case NONE -> queue;
        };

        return new Pair<>(queue, result);
    }

    private static ActionQueue findSafeCeiling(ActionQueue queue, Value<BlockPos> result, World world, BlockPos original) {
        return queue.thenRun(() -> {
            int y = world.getChunk(original).sampleHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                    original.getX() & 15, original.getZ() & 15) + 1;

            result.value = original.withY(y);
        });
    }

    private static ActionQueue findSafeFloor(ActionQueue queue, Value<BlockPos> result, World world, BlockPos original) {
        final SafeFloorHolder holder = new SafeFloorHolder(world, original);

        return queue.thenRunSteps(() -> {
            Iter state = holder.checkAndAdvance();

            if (state == Iter.SUCCESS)
                result.value = holder.cursor;

            return state != Iter.CONTINUE;
        }, TimeUnit.TICKS, 1, 3);
    }

    private static ActionQueue findSafeMedian(ActionQueue queue, Value<BlockPos> result, World world, BlockPos original) {
        final SafeMedianHolder holder = new SafeMedianHolder(world, original);

        return queue.thenRunSteps(() -> {
            DoubleIter state = holder.checkAndAdvance();

            if (state == DoubleIter.SUCCESS_A) {
                result.value = holder.upCursor;
            } else if (state == DoubleIter.SUCCESS_B) {
                result.value = holder.downCursor;
            }

            return state != DoubleIter.CONTINUE;
        }, TimeUnit.TICKS, 1, 3);
    }

    private static ActionQueue findSafeXZ(ActionQueue queue, Value<BlockPos> result, World world, BlockPos original, int radius) {
        BlockPos.Mutable pos = original.mutableCopy();

        int minX = pos.getX() - radius;
        int maxX = pos.getX() + radius;

        int minZ = pos.getZ() - radius;
        int maxZ = pos.getZ() + radius;

        final SafeXZHolder holder = new SafeXZHolder(world, pos, maxX, maxZ, minX, minZ);

        return queue.thenRunSteps(() -> {
            Iter state = holder.checkAndAdvance();

            if (state == Iter.SUCCESS)
                result.value = holder.pos.toImmutable();

            return state != Iter.CONTINUE;
        }, TimeUnit.TICKS, 1, 3); // every tick, while the taken time is less than 3ms (1tick = 50ms, 2/50 of a tick, which is 4%)
    }

    @SuppressWarnings("deprecation")
    private static boolean isSafe(Chunk chunk, BlockPos pos) {
        BlockState floor = chunk.getBlockState(pos.down());

        if (!floor.blocksMovement())
            return false;

        BlockState curUp = chunk.getBlockState(pos);
        BlockState aboveUp = chunk.getBlockState(pos.up());

        return !curUp.blocksMovement() && !aboveUp.blocksMovement();
    }

    @SuppressWarnings("deprecation")
    private static boolean isSafe(BlockState floor, BlockState block1, BlockState block2) {
        return floor.blocksMovement() && !block1.blocksMovement() && !block2.blocksMovement();
    }

    static class SafeXZHolder {
        int x;
        int z;
        Chunk prevChunk;
        final World world;
        final BlockPos.Mutable pos;
        final int maxX;
        final int maxZ;
        final int minX;

        public SafeXZHolder(World world, BlockPos.Mutable pos, int maxX, int maxZ, int minX, int minZ) {
            this.world = world;
            this.pos = pos;
            this.maxX = maxX;
            this.maxZ = maxZ;
            this.minX = minX;
            this.x = minX;
            this.z = minZ;
        }

        public Iter checkAndAdvance() {
            if (z >= maxZ)
                return Iter.FAIL;

            if (x >= maxX) {
                x = minX;
                z += 1;

                return Iter.CONTINUE;
            }

            pos.setX(x).setZ(z);

            ChunkPos tempPos = new ChunkPos(pos);
            if (prevChunk == null || !prevChunk.getPos().equals(tempPos))
                prevChunk = world.getChunk(tempPos.x, tempPos.z);

            if (isSafe(prevChunk, pos))
                return Iter.SUCCESS;

            x += 1;
            return Iter.CONTINUE;
        }

        public BlockPos pos() {
            return pos.toImmutable();
        }
    }

    static class SafeFloorHolder {
        BlockPos cursor;
        BlockState floor;
        BlockState current;
        BlockState above;

        final Chunk chunk;
        final int maxY;

        public SafeFloorHolder(World world, BlockPos pos) {
            this.chunk = world.getChunk(pos);
            this.maxY = chunk.getTopY();

            int minY = chunk.getBottomY();
            this.cursor = pos.withY(minY + 2);

            this.floor = chunk.getBlockState(cursor.down());
            this.current = chunk.getBlockState(cursor);
            this.above = chunk.getBlockState(cursor.up());
        }

        public Iter checkAndAdvance() {
            if (cursor.getY() >= maxY)
                return Iter.FAIL;

            if (isSafe(floor, current, above))
                return Iter.SUCCESS;

            cursor = cursor.up();

            floor = current;
            current = above;
            above = chunk.getBlockState(cursor);

            return Iter.CONTINUE;
        }
    }

    static class SafeMedianHolder {

        BlockPos upCursor;
        BlockState floorUp;
        BlockState curUp;
        BlockState aboveUp;

        BlockPos downCursor;
        BlockState floorDown;
        BlockState curDown;
        BlockState aboveDown;

        final Chunk chunk;

        public SafeMedianHolder(World world, BlockPos pos) {
            this.chunk = world.getChunk(pos);

            this.upCursor = pos.up();
            this.floorUp = chunk.getBlockState(upCursor.down());
            this.curUp = chunk.getBlockState(upCursor);
            this.aboveUp = chunk.getBlockState(upCursor.up());

            this.downCursor = pos.down();
            this.floorDown = chunk.getBlockState(downCursor.down());
            this.curDown = chunk.getBlockState(downCursor);
            this.aboveDown = chunk.getBlockState(downCursor.up());
        }

        public DoubleIter checkAndAdvance() {
            boolean canGoUp = upCursor.getY() < chunk.getTopY();
            boolean canGoDown = downCursor.getY() > chunk.getBottomY();

            if (!canGoUp && !canGoDown)
                return DoubleIter.FAIL;

            if (canGoUp) {
                if (isSafe(floorUp, curUp, aboveUp)) {
                    upCursor = upCursor.down();
                    return DoubleIter.SUCCESS_A;
                }

                upCursor = upCursor.up();

                floorUp = curUp;
                curUp = aboveUp;
                aboveUp = chunk.getBlockState(upCursor);
            }

            if (canGoDown) {
                if (isSafe(floorDown, curDown, aboveDown)) {
                    downCursor = downCursor.up();
                    return DoubleIter.SUCCESS_B;
                }

                downCursor = downCursor.down();

                curDown = aboveDown;
                aboveDown = floorDown;
                floorDown = chunk.getBlockState(downCursor);
            }

            return DoubleIter.CONTINUE;
        }
    }

    enum Iter {
        SUCCESS,
        FAIL,
        CONTINUE
    }

    enum DoubleIter {
        SUCCESS_A,
        SUCCESS_B,
        FAIL,
        CONTINUE
    }
}
