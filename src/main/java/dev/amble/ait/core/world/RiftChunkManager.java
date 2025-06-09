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
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;

@SuppressWarnings("UnstableApiUsage")
public record RiftChunkManager(ServerWorld world) {

    private static final AttachmentType<Double> ARTRON = AttachmentRegistry.createPersistent(
            AITMod.id("artron"), Codec.DOUBLE
    );

    private static final AttachmentType<Double> MAX_ARTRON = AttachmentRegistry.createPersistent(
            AITMod.id("max_artron"), Codec.DOUBLE
    );

    public static void init() {
        ServerChunkEvents.TICK.register((world, chunk) -> {
            if (world.getServer().getTicks() % 20 != 0)
                return;

            RiftChunkManager manager = RiftChunkManager.getInstance(world);
            ChunkPos pos = chunk.getPos();

            if (!manager.isRiftChunk(pos))
                return;

            if (manager.getMaxArtron(pos) < manager.getArtron(pos))
                manager.addFuel(chunk.getPos(), 1);
        });
    }

    public static RiftChunkManager getInstance(ServerWorld world) {
        return new RiftChunkManager(world);
    }

    public double getArtron(ChunkPos pos) {
        if (!this.isRiftChunk(pos))
            return 0;

        Chunk shouldBeProtoChunk = this.world.getChunkManager().getChunk(pos.x, pos.z, ChunkStatus.STRUCTURE_STARTS, true);

        if (!(shouldBeProtoChunk instanceof ProtoChunk protoChunk))
            return 0;

        return protoChunk.getAttachedOrCreate(ARTRON, () -> (double) world.getRandom().nextBetween(100, 800));
    }

    public double getMaxArtron(ChunkPos pos) {
        if (!this.isRiftChunk(pos))
            return 0;

        Chunk shouldBeProtoChunk = this.world.getChunkManager().getChunk(pos.x, pos.z, ChunkStatus.STRUCTURE_STARTS, true);

        if (!(shouldBeProtoChunk instanceof ProtoChunk protoChunk))
            return 0;

        return protoChunk.getAttachedOrCreate(ARTRON, () -> (double) world.getRandom().nextBetween(300, 1000));
    }

    public double removeFuel(ChunkPos pos, double amount) {
        if (!this.isRiftChunk(pos))
            return 0;

        double artron = this.getArtron(pos);
        artron -= artron < amount ? 0 : amount;

        Chunk shouldBeProtoChunk = this.world.getChunkManager().getChunk(pos.x, pos.z, ChunkStatus.STRUCTURE_STARTS, true);
        if (shouldBeProtoChunk instanceof ProtoChunk protoChunk) {
            protoChunk.setAttached(ARTRON, artron);
        }
        return artron - amount;
    }

    public void addFuel(ChunkPos pos, double amount) {
        if (!this.isRiftChunk(pos))
            return;

        RiftChunkManager.addFuel(this.world, pos, amount);
    }

    public void setCurrentFuel(ChunkPos pos, double amount) {
        Chunk shouldBeProtoChunk = this.world.getChunkManager().getChunk(pos.x, pos.z, ChunkStatus.STRUCTURE_STARTS, true);

        if (shouldBeProtoChunk instanceof ProtoChunk protoChunk) {
            protoChunk.modifyAttached(ARTRON, d -> amount);
        }
    }

    public boolean isRiftChunk(ChunkPos chunkPos) {
        return RiftChunkManager.isRiftChunk(this.world, chunkPos);
    }

    public boolean isRiftChunk(BlockPos pos) {
        return RiftChunkManager.isRiftChunk(world, pos);
    }

    public static boolean isRiftChunk(CachedDirectedGlobalPos cached) {
        return isRiftChunk(cached.getWorld(), cached.getPos());
    }

    public static boolean isRiftChunk(StructureWorldAccess world, BlockPos pos) {
        return isRiftChunk(world, new ChunkPos(pos));
    }

    public static boolean isRiftChunk(StructureWorldAccess world, ChunkPos pos) {
        return ChunkRandom.getSlimeRandom(pos.x, pos.z,
                world.getSeed(), 987234910L
        ).nextInt(8) == 0;
    }

    private static void addFuel(ServerWorldAccess world, ChunkPos pos, double amount) {
        Chunk shouldBeProtoChunk = world.getChunkManager().getChunk(pos.x, pos.z, ChunkStatus.STRUCTURE_STARTS, true);

        if (shouldBeProtoChunk instanceof ProtoChunk protoChunk) {
            protoChunk.modifyAttached(ARTRON, d -> d + amount);
        }
    }

    public static double getFuel(ServerWorld world, ChunkPos pos) {
        if (!isRiftChunk(world, pos))
            return 0;

        Chunk shouldBeProtoChunk = world.getChunkManager().getChunk(pos.x, pos.z, ChunkStatus.STRUCTURE_STARTS, true);

        if (!(shouldBeProtoChunk instanceof ProtoChunk protoChunk))
            return 0;

        return protoChunk.getAttachedOrCreate(ARTRON, () -> (double) world.getRandom().nextBetween(100, 800));
    }

    public static double getMaxFuel(ServerWorld world, ChunkPos pos) {
        if (!isRiftChunk(world, pos))
            return 0;

        Chunk shouldBeProtoChunk = world.getChunkManager().getChunk(pos.x, pos.z, ChunkStatus.STRUCTURE_STARTS, true);

        if (!(shouldBeProtoChunk instanceof ProtoChunk protoChunk))
            return 0;

        return protoChunk.getAttachedOrCreate(MAX_ARTRON, () -> (double) world.getRandom().nextBetween(300, 1000));
    }
}
