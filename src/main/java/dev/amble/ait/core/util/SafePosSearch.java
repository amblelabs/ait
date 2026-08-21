package dev.amble.ait.core.util;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Consumer;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.util.ServerLifecycleHooks;
import dev.drtheo.queue.api.ActionQueue;
import dev.drtheo.queue.api.util.Value;
import dev.drtheo.scheduler.api.TimeUnit;
import dev.drtheo.scheduler.api.common.Scheduler;
import dev.drtheo.scheduler.api.common.TaskStage;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

public class SafePosSearch {

    private static final int SAFE_RADIUS = 3;
    private static final Queue<Runnable> EXTENDED_SEARCH_QUEUE = new ArrayDeque<>();
    private static boolean extendedSearchActive;

    static {
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> clearExtendedSearches());
    }

    public static void wrapSafe(CachedDirectedGlobalPos globalPos, Kind vSearch,
                                boolean hSearch, Consumer<CachedDirectedGlobalPos> posConsumer) {
        wrapSafe(globalPos, vSearch, hSearch, SAFE_RADIUS,
                result -> posConsumer.accept(result.position()));
    }

    public static void wrapSafe(CachedDirectedGlobalPos globalPos, Kind vSearch,
                                boolean hSearch, int horizontalRadius, Consumer<SearchResult> resultConsumer) {
        if (globalPos.getWorld() == null)
            globalPos.init(ServerLifecycleHooks.get());

        if (globalPos.getWorld() == null) {
            resultConsumer.accept(new SearchResult(globalPos, false));
            return;
        }

        if (horizontalRadius > SAFE_RADIUS) {
            enqueueExtendedSearch(() -> runSafeSearch(globalPos, vSearch, hSearch, horizontalRadius, result -> {
                try {
                    resultConsumer.accept(result);
                } finally {
                    finishExtendedSearch();
                }
            }));
            return;
        }

        runSafeSearch(globalPos, vSearch, hSearch, horizontalRadius, resultConsumer);
    }

    private static void runSafeSearch(CachedDirectedGlobalPos globalPos, Kind vSearch,
                                      boolean hSearch, int horizontalRadius, Consumer<SearchResult> resultConsumer) {
        Value<BlockPos> ref = new Value<>(null);
        ActionQueue queue = findSafe(globalPos, vSearch, hSearch, horizontalRadius, ref);

        if (queue != null) {
            queue.thenRun(() -> {
                CachedDirectedGlobalPos resultPos = globalPos;
                boolean foundSafePosition = ref.value != null;

                if (foundSafePosition)
                    resultPos = resultPos.pos(ref.value);

                resultConsumer.accept(new SearchResult(resultPos, foundSafePosition));
            }).execute();
        } else {
            resultConsumer.accept(new SearchResult(globalPos, true));
        }
    }

    private static void enqueueExtendedSearch(Runnable search) {
        Runnable next = null;
        synchronized (EXTENDED_SEARCH_QUEUE) {
            EXTENDED_SEARCH_QUEUE.add(search);
            if (!extendedSearchActive) {
                extendedSearchActive = true;
                next = EXTENDED_SEARCH_QUEUE.remove();
            }
        }

        if (next != null)
            runExtendedSearch(next);
    }

    private static void finishExtendedSearch() {
        Runnable next;
        synchronized (EXTENDED_SEARCH_QUEUE) {
            next = EXTENDED_SEARCH_QUEUE.poll();
            if (next == null)
                extendedSearchActive = false;
        }

        if (next != null)
            Scheduler.get().runTaskLater(() -> runExtendedSearch(next),
                    TaskStage.END_SERVER_TICK, TimeUnit.TICKS, 1);
    }

    private static void runExtendedSearch(Runnable search) {
        try {
            search.run();
        } catch (RuntimeException | Error error) {
            finishExtendedSearch();
            throw error;
        }
    }

    private static void clearExtendedSearches() {
        synchronized (EXTENDED_SEARCH_QUEUE) {
            EXTENDED_SEARCH_QUEUE.clear();
            extendedSearchActive = false;
        }
    }

    /**
     * @return {@literal null} when the position is already safe, {@link ActionQueue} otherwise.
     */
    @Nullable public static ActionQueue findSafe(CachedDirectedGlobalPos globalPos,
                                        Kind vSearch, boolean hSearch, Value<BlockPos> ref) {
        return findSafe(globalPos, vSearch, hSearch, SAFE_RADIUS, ref);
    }

    /**
     * @return {@literal null} when the position is already safe, {@link ActionQueue} otherwise.
     */
    @Nullable public static ActionQueue findSafe(CachedDirectedGlobalPos globalPos,
                                        Kind vSearch, boolean hSearch, int horizontalRadius, Value<BlockPos> ref) {
        if (horizontalRadius < 0)
            throw new IllegalArgumentException("Horizontal search radius cannot be negative");

        ServerWorld world = globalPos.getWorld();
        BlockPos pos = globalPos.getPos();

        final Chunk chunk = world.getChunk(pos);

        if (isSafe(chunk, pos))
            return null;

        ActionQueue queue = new ActionQueue();

        if (hSearch) {
            queue = findSafeXZ(queue, ref, world, pos, vSearch, horizontalRadius).thenRun(() -> {
                if (ref.value != null)
                    globalPos.pos(ref.value);
            });
        }

        return switch (vSearch) {
            case CEILING -> findSafeCeiling(queue, ref, world, pos);
            case FLOOR -> findSafeFloor(queue, ref, world, pos);
            case MEDIAN -> findSafeMedian(queue, ref, world, pos);
            case NONE -> queue;
        };
    }

    private static ActionQueue findSafeCeiling(ActionQueue queue, Value<BlockPos> result, ServerWorld world, BlockPos original) {
        return queue.thenRun(() -> {
            if (result.value != null)
                return;

            int y = world.getChunk(original).sampleHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                    original.getX() & 15, original.getZ() & 15) + 1;

            result.value = original.withY(y);
        });
    }

    private static ActionQueue findSafeFloor(ActionQueue queue, Value<BlockPos> result, ServerWorld world, BlockPos original) {
        final SafeFloorHolder holder = new SafeFloorHolder(world, original);

        return queue.thenRunSteps(() -> {
            if (result.value != null)
                return true;

            Iter state = holder.checkAndAdvance();

            if (state == Iter.SUCCESS)
                result.value = holder.cursor;

            return state != Iter.CONTINUE;
        }, TaskStage.startWorldTick(world), TimeUnit.TICKS, 1, 3);
    }

    private static ActionQueue findSafeMedian(ActionQueue queue, Value<BlockPos> result, ServerWorld world, BlockPos original) {
        final SafeMedianHolder holder = new SafeMedianHolder(world, original);

        return queue.thenRunSteps(() -> {
            if (result.value != null)
                return true;

            DoubleIter state = holder.checkAndAdvance();

            if (state == DoubleIter.SUCCESS_A) {
                result.value = holder.upCursor;
            } else if (state == DoubleIter.SUCCESS_B) {
                result.value = holder.downCursor;
            }

            return state != DoubleIter.CONTINUE;
        }, TaskStage.startWorldTick(world), TimeUnit.TICKS, 1, 3);
    }

    private static ActionQueue findSafeXZ(ActionQueue queue, Value<BlockPos> result, ServerWorld world,
                                          BlockPos original, Kind vSearch, int radius) {
        BlockPos.Mutable pos = original.mutableCopy();

        final SafeXZHolder holder = new SafeXZHolder(world, pos, vSearch, radius);
        int budgetMillis = radius > SAFE_RADIUS ? 1 : 3;

        return queue.thenRunSteps(() -> {
            Iter state = holder.checkAndAdvance();

            if (state == Iter.SUCCESS)
                result.value = holder.pos.toImmutable();

            return state != Iter.CONTINUE;
        }, TaskStage.startWorldTick(world), TimeUnit.TICKS, 1, budgetMillis);
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
        int directionX;
        int directionZ;
        int remainingSteps;
        Chunk prevChunk;
        ChunkPos prevChunkPos;
        final ServerWorld world;
        final BlockPos.Mutable pos;
        final int centerX;
        final int centerY;
        final int centerZ;
        final Kind vSearch;
        final int radius;

        public SafeXZHolder(ServerWorld world, BlockPos.Mutable pos, Kind vSearch, int radius) {
            this.world = world;
            this.pos = pos;
            this.centerX = pos.getX();
            this.centerY = pos.getY();
            this.centerZ = pos.getZ();
            this.vSearch = vSearch;
            this.radius = radius;
            this.x = 0;
            this.z = 0;
            this.directionX = 0;
            this.directionZ = -1;
            int diameter = this.radius * 2 + 1;
            this.remainingSteps = diameter * diameter;
        }

        public Iter checkAndAdvance() {
            while (this.remainingSteps-- > 0) {
                int currentX = this.x;
                int currentZ = this.z;
                this.advanceSpiral();

                if (currentX * currentX + currentZ * currentZ > this.radius * this.radius)
                    continue;

                pos.setX(this.centerX + currentX).setZ(this.centerZ + currentZ);

                ChunkPos tempPos = new ChunkPos(pos);
                if (!tempPos.equals(this.prevChunkPos)) {
                    this.prevChunkPos = tempPos;
                    this.prevChunk = world.getChunkManager().getChunk(tempPos.x, tempPos.z, ChunkStatus.FULL, false);
                }

                if (this.prevChunk == null)
                    return Iter.CONTINUE;

                if (isSafe(this.prevChunk, pos))
                    return Iter.SUCCESS;

                if (this.vSearch == Kind.NONE)
                    return Iter.CONTINUE;

                int surfaceY = this.prevChunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                        pos.getX() & 15, pos.getZ() & 15) + 1;
                int deltaY = surfaceY - this.centerY;
                if (currentX * currentX + deltaY * deltaY + currentZ * currentZ > this.radius * this.radius)
                    return Iter.CONTINUE;

                pos.setY(surfaceY);
                if (isSafe(this.prevChunk, pos))
                    return Iter.SUCCESS;

                pos.setY(this.centerY);
                return Iter.CONTINUE;
            }

            return Iter.FAIL;
        }

        private void advanceSpiral() {
            if (this.x == this.z || (this.x < 0 && this.x == -this.z)
                    || (this.x > 0 && this.x == 1 - this.z)) {
                int previousX = this.directionX;
                this.directionX = -this.directionZ;
                this.directionZ = previousX;
            }

            this.x += this.directionX;
            this.z += this.directionZ;
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

    public record SearchResult(CachedDirectedGlobalPos position, boolean foundSafePosition) {
    }

    public enum Kind implements StringIdentifiable {
        NONE {
            @Override
            public Kind next() {
                return FLOOR;
            }
        },
        FLOOR {
            @Override
            public Kind next() {
                return CEILING;
            }
        },
        CEILING {
            @Override
            public Kind next() {
                return MEDIAN;
            }
        },
        MEDIAN {
            @Override
            public Kind next() {
                return NONE;
            }
        };

        @Override
        public String asString() {
            return toString();
        }

        public MutableText text() {
            return Text.translatable("message.ait.control.ylandtype." + this.asString().toLowerCase());
        }

        public abstract Kind next();
    }
}
