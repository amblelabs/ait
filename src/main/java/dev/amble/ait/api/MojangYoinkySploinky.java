package dev.amble.ait.api;

import com.mojang.datafixers.util.Either;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface MojangYoinkySploinky {

    static MojangYoinkySploinky get(ServerWorld world) {
        return get(world.getChunkManager());
    }

    static MojangYoinkySploinky get(ServerChunkManager manager) {
        return (MojangYoinkySploinky) manager;
    }

    @Nullable Chunk moj$getChunkCached(long l, ChunkStatus leastStatus, boolean create);

    void moj$putInCache(long pos, Chunk chunk, ChunkStatus status);
    ThreadExecutor<?> moj$mainThreadExecutor();

    CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> moj$getChunkFuture0(
            int chunkX,
            int chunkZ,
            @NotNull ChunkStatus leastStatus,
            boolean create
    );

    default CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> moj$getChunkFuture(
            int chunkX,
            int chunkZ,
            @NotNull ChunkStatus leastStatus,
            boolean create
    ) {
        long l = ChunkPos.toLong(chunkX, chunkZ);
        Chunk cached = moj$getChunkCached(l, leastStatus, create);

        if (cached != null)
            return CompletableFuture.completedFuture(Either.left(cached));

        return this.moj$getChunkFuture0(chunkX, chunkZ, leastStatus, create).thenApplyAsync(either -> either.ifLeft(chunk ->
                this.moj$putInCache(l, chunk, leastStatus)), this.moj$mainThreadExecutor());
    }

    default CompletableFuture<Chunk> moj$getChunkFutureOrThrow(
            int chunkX,
            int chunkZ,
            @NotNull ChunkStatus leastStatus,
            boolean create
    ) {
        return moj$getChunkFuture(chunkX, chunkZ, leastStatus, create).thenApply(either -> either.map(chunkx -> chunkx, unloaded -> {
            if (create) {
                throw Util.throwOrPause(new IllegalStateException("Chunk not there when requested: " + unloaded));
            } else {
                return null;
            }
        }));
    }

    default CompletableFuture<Chunk> moj$getChunkFutureOrThrow(BlockPos pos, @NotNull ChunkStatus leastStatus, boolean create) {
        int x = ChunkSectionPos.getSectionCoord(pos.getX());
        int z = ChunkSectionPos.getSectionCoord(pos.getZ());
        return moj$getChunkFutureOrThrow(x, z, leastStatus, create);
    }

    static CompletableFuture<BlockState> getBlockState(ServerWorld world, BlockPos pos, boolean create) {
        MojangYoinkySploinky yoinky = get(world);

        return yoinky.moj$getChunkFutureOrThrow(pos, ChunkStatus.FULL, create)
                .thenApplyAsync(chunk -> chunk.getBlockState(pos), yoinky.moj$mainThreadExecutor());
    }

    static CompletableFuture<BlockState> getBlockState(ServerWorld world, BlockPos pos) {
        return getBlockState(world, pos, true);
    }
}
