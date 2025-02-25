package dev.amble.ait.core.util;

import dev.amble.lib.data.CachedDirectedGlobalPos;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class ForcedChunkUtil {

    public static void keepChunkLoaded(ServerWorld world, BlockPos pos) {
        ChunkPos chunk = new ChunkPos(pos);
        world.setChunkForced(chunk.x, chunk.z, true);
    }

    public static void keepChunkLoaded(CachedDirectedGlobalPos globalPos) {
        keepChunkLoaded(globalPos.getWorld(), globalPos.getPos());
    }

    public static void stopForceLoading(ServerWorld world, BlockPos pos) {
        ChunkPos chunk = new ChunkPos(pos);
        world.setChunkForced(chunk.x, chunk.z, false);
    }

    public static void stopForceLoading(CachedDirectedGlobalPos globalPos) {
        stopForceLoading(globalPos.getWorld(), globalPos.getPos());
    }

    public static boolean isChunkForced(ServerWorld world, BlockPos pos) {
        ChunkPos target = new ChunkPos(pos);
        ChunkPos temp;

        for (Long i : world.getForcedChunks()) {
            temp = new ChunkPos(i);
            if (temp.equals(target))
                return true;
        }

        return false;
    }

    public static boolean isChunkForced(CachedDirectedGlobalPos globalPos) {
        return isChunkForced(globalPos.getWorld(), globalPos.getPos());
    }
}
