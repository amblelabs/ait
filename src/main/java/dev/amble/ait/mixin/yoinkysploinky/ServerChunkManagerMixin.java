package dev.amble.ait.mixin.yoinkysploinky;

import com.mojang.datafixers.util.Either;
import dev.amble.ait.AITMod;
import dev.amble.ait.api.MojangYoinkySploinky;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.concurrent.CompletableFuture;

@Mixin(ServerChunkManager.class)
public abstract class ServerChunkManagerMixin implements MojangYoinkySploinky {

    @Shadow
    @Final
    private long[] chunkPosCache;

    @Shadow
    @Final
    private ChunkStatus[] chunkStatusCache;

    @Shadow
    @Final
    private Chunk[] chunkCache;

    @Shadow
    protected abstract CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunkFuture(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create);

    @Shadow
    protected abstract void putInCache(long pos, Chunk chunk, ChunkStatus status);

    @Shadow
    @Final
    private ServerChunkManager.MainThreadExecutor mainThreadExecutor;

    @Override
    public ThreadExecutor<?> moj$mainThreadExecutor() {
        return mainThreadExecutor;
    }

    @Override
    public void moj$putInCache(long pos, Chunk chunk, ChunkStatus status) {
        AITMod.LOGGER.info("Yoinky Sploinky: cached chunk at {}x{}", ChunkPos.getPackedX(pos), ChunkPos.getPackedZ(pos));
        this.putInCache(pos, chunk, status);
    }

    @Override
    public @Nullable Chunk moj$getChunkCached(long l, ChunkStatus leastStatus, boolean create) {
        for(int i = 0; i < 4; ++i) {
            if (l == this.chunkPosCache[i] && leastStatus == this.chunkStatusCache[i]) {
                Chunk chunk = this.chunkCache[i];
                if (chunk != null || !create) {
                    return chunk;
                }
            }
        }

        return null;
    }

    @Override
    public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> moj$getChunkFuture0(int chunkX, int chunkZ, @NotNull ChunkStatus leastStatus, boolean create) {
        return this.getChunkFuture(chunkX, chunkZ, leastStatus, create);
    }
}
