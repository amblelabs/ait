package dev.amble.ait.core.world;

import com.mojang.serialization.Codec;
import dev.amble.ait.AITMod;
import dev.amble.ait.core.events.ServerChunkEvents;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

@SuppressWarnings("UnstableApiUsage")
public record RiftChunkManager(ServerWorld world) {

    private static final AttachmentType<Double> ARTRON = AttachmentRegistry.createPersistent(AITMod.id("artron"), Codec.DOUBLE);
    private static final AttachmentType<Double> MAX_ARTRON = AttachmentRegistry.createPersistent(AITMod.id("max_artron"), Codec.DOUBLE);

    public static void init() {
        ServerChunkEvents.TICK.register((world, chunk) -> {
            if (world.getServer().getTicks() % 20 != 0) return;

            ChunkPos pos = chunk.getPos();
            if (!isRiftChunk(world, pos)) return;

            double current = getArtron(world, pos);
            double max = getMaxArtron(world, pos);

            if (current < max) {
                addFuel(world, pos, 1.0);
            }
        });
    }

    public static RiftChunkManager getInstance(ServerWorld world) {
        return new RiftChunkManager(world);
    }

    public static double getArtron(ServerWorld world, ChunkPos pos) {
        if (!isRiftChunk(world, pos)) return 0;
        return getChunkAttachment(world, pos, ARTRON, () -> (double) world.getRandom().nextBetween(1000, 2000));
    }

    public static double getMaxArtron(ServerWorld world, ChunkPos pos) {
        if (!isRiftChunk(world, pos)) return 0;
        return getChunkAttachment(world, pos, MAX_ARTRON, () -> (double) world.getRandom().nextBetween(1500, 3000));
    }

    public static void addFuel(ServerWorld world, ChunkPos pos, double amount) {
        if (!isRiftChunk(world, pos)) return;
        modifyAttachment(world, pos, ARTRON, d -> d + amount);
    }

    public static void setCurrentFuel(ServerWorld world, ChunkPos pos, double amount) {
        if (!isRiftChunk(world, pos)) return;
        modifyAttachment(world, pos, ARTRON, d -> amount);
    }

    private static <T> T getChunkAttachment(ServerWorld world, ChunkPos pos, AttachmentType<T> type, java.util.function.Supplier<T> defaultValue) {
        Chunk chunk = world.getChunkManager().getChunk(pos.x, pos.z, ChunkStatus.STRUCTURE_STARTS, true);
        return chunk != null ? chunk.getAttachedOrCreate(type, defaultValue) : defaultValue.get();
    }

    private static void modifyAttachment(ServerWorld world, ChunkPos pos, AttachmentType<Double> type, java.util.function.UnaryOperator<Double> modifier) {
        Chunk chunk = world.getChunkManager().getChunk(pos.x, pos.z, ChunkStatus.STRUCTURE_STARTS, true);
        if (chunk != null) {
            chunk.modifyAttached(type, modifier);
        }
    }

    public static boolean isRiftChunk(CachedDirectedGlobalPos cached) {
        return isRiftChunk(cached.getWorld(), cached.getPos());
    }

    public static boolean isRiftChunk(StructureWorldAccess world, ChunkPos pos) {
        return world != null && ChunkRandom.getSlimeRandom(pos.x, pos.z, world.getSeed(), 987234910L).nextInt(8) == 0;
    }

    public static boolean isRiftChunk(StructureWorldAccess world, BlockPos pos) {
        return isRiftChunk(world, new ChunkPos(pos));
    }

    public double getArtron(ChunkPos pos) { return getArtron(this.world, pos); }
    public double getMaxArtron(ChunkPos pos) { return getMaxArtron(this.world, pos); }
    public void addFuel(ChunkPos pos, double amount) { addFuel(this.world, pos, amount); }
    public boolean isRiftChunk(ChunkPos pos) { return isRiftChunk(this.world, pos); }
    public boolean isRiftChunk(BlockPos pos) { return isRiftChunk(this.world, pos); }
}