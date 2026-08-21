package dev.amble.ait.core.world;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;

import dev.amble.ait.AITMod;
import dev.amble.ait.config.ArtronConfigSettings;
import dev.amble.ait.core.engine.link.tracker.FluidNetwork;
import dev.amble.ait.core.events.ServerChunkEvents;
import dev.amble.lib.data.CachedDirectedGlobalPos;

@SuppressWarnings("UnstableApiUsage")
public record RiftChunkManager(ServerWorld world) {

    private static final AttachmentType<Double> ARTRON = AttachmentRegistry.createPersistent(
            AITMod.id("artron"), Codec.DOUBLE
    );

    private static final AttachmentType<Double> MAX_ARTRON = AttachmentRegistry.createPersistent(
            AITMod.id("max_artron"), Codec.DOUBLE
    );

    public static void init() {
        FluidNetwork.init();

        ServerChunkEvents.TICK.register((world, chunk) -> {
            if (world.getServer().getTicks() % 20 != 0)
                return;

            RiftChunkManager manager = RiftChunkManager.getInstance(world);

            if (!manager.isRiftChunk(chunk.getPos()))
                return;

            double current = manager.getArtron(chunk);
            double max = manager.getMaxArtron(chunk);
            double regeneration = AITMod.CONFIG.getRiftChunkArtronRegenPerSecond();

            if (current < max && regeneration > 0)
                manager.setCurrentFuel(chunk, Math.min(current + regeneration, max));
        });
    }

    public static RiftChunkManager getInstance(ServerWorld world) {
        return new RiftChunkManager(world);
    }

    /**
     * Returns the energy of an already-loaded chunk. This method never loads or generates a chunk.
     */
    public double getArtron(ChunkPos pos) {
        Chunk chunk = this.getLoadedChunk(pos);
        return chunk == null ? 0 : this.getArtron(chunk);
    }

    public double getArtron(Chunk chunk) {
        if (!this.isRiftChunk(chunk.getPos()))
            return 0;

        return this.getOrMigrate(chunk).current();
    }

    /**
     * Returns the capacity of an already-loaded chunk. This method never loads or generates a chunk.
     */
    public double getMaxArtron(ChunkPos pos) {
        Chunk chunk = this.getLoadedChunk(pos);
        return chunk == null ? 0 : this.getMaxArtron(chunk);
    }

    public double getMaxArtron(Chunk chunk) {
        if (!this.isRiftChunk(chunk.getPos()))
            return 0;

        return this.getOrMigrate(chunk).max();
    }

    /**
     * Removes up to {@code requested} AU and returns the amount that was actually removed.
     */
    public double extractFuel(ChunkPos pos, double requested) {
        Chunk chunk = this.getLoadedChunk(pos);
        return chunk == null ? 0 : this.extractFuel(chunk, requested);
    }

    public double extractFuel(Chunk chunk, double requested) {
        double normalized = normalizeRequest(requested);

        if (normalized == 0 || !this.isRiftChunk(chunk.getPos()))
            return 0;

        ArtronData data = this.getOrMigrate(chunk);
        double extracted = Math.min(data.current(), normalized);

        if (extracted > 0)
            chunk.setAttached(ARTRON, data.current() - extracted);

        return extracted;
    }

    /**
     * Compatibility wrapper. The return value is the amount actually removed.
     */
    public double removeFuel(ChunkPos pos, double amount) {
        return this.extractFuel(pos, amount);
    }

    /**
     * Inserts up to {@code requested} AU and returns the unaccepted remainder.
     */
    public double insertFuel(ChunkPos pos, double requested) {
        double normalized = normalizeRequest(requested);
        Chunk chunk = this.getLoadedChunk(pos);
        return chunk == null ? normalized : this.insertFuel(chunk, normalized);
    }

    public double insertFuel(Chunk chunk, double requested) {
        double normalized = normalizeRequest(requested);

        if (normalized == 0)
            return 0;

        if (!this.isRiftChunk(chunk.getPos()))
            return normalized;

        ArtronData data = this.getOrMigrate(chunk);
        double accepted = Math.min(normalized, Math.max(data.max() - data.current(), 0));

        if (accepted > 0)
            chunk.setAttached(ARTRON, data.current() + accepted);

        return normalized - accepted;
    }

    public void addFuel(ChunkPos pos, double amount) {
        this.insertFuel(pos, amount);
    }

    public void setCurrentFuel(ChunkPos pos, double amount) {
        Chunk chunk = this.getLoadedChunk(pos);

        if (chunk != null)
            this.setCurrentFuel(chunk, amount);
    }

    public void setCurrentFuel(Chunk chunk, double amount) {
        if (!this.isRiftChunk(chunk.getPos()))
            return;

        ArtronData data = this.getOrMigrate(chunk);
        double clamped = MathHelper.clamp(normalizeStored(amount), 0, data.max());

        if (Double.compare(data.current(), clamped) != 0)
            chunk.setAttached(ARTRON, clamped);
    }

    @Nullable public Chunk getLoadedChunk(ChunkPos pos) {
        return this.world.getChunkManager().getWorldChunk(pos.x, pos.z);
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
        if (world == null) return false;
        return ChunkRandom.getSlimeRandom(pos.x, pos.z,
                world.getSeed(), 987234910L
        ).nextInt(8) == 0;
    }

    public static double getFuel(ServerWorld world, ChunkPos pos) {
        return getInstance(world).getArtron(pos);
    }

    public static double getMaxFuel(ServerWorld world, ChunkPos pos) {
        return getInstance(world).getMaxArtron(pos);
    }

    private ArtronData getOrMigrate(Chunk chunk) {
        Double storedCurrent = chunk.getAttached(ARTRON);
        Double storedMax = chunk.getAttached(MAX_ARTRON);

        double current;
        double max;

        if (storedCurrent == null && storedMax == null) {
            max = this.randomCapacity();
            current = max;
        } else if (storedMax == null) {
            current = normalizeStored(storedCurrent);
            max = Math.max(current, this.randomCapacity());
        } else if (storedCurrent == null) {
            max = normalizeStored(storedMax);
            current = max;
        } else {
            current = normalizeStored(storedCurrent);
            max = normalizeStored(storedMax);

            // Older versions initialized both values independently. Preserve already-stored AU
            // instead of deleting the excess during the first migration read.
            max = Math.max(max, current);
        }

        current = MathHelper.clamp(current, 0, max);

        if (storedMax == null || Double.compare(storedMax, max) != 0)
            chunk.setAttached(MAX_ARTRON, max);

        if (storedCurrent == null || Double.compare(storedCurrent, current) != 0)
            chunk.setAttached(ARTRON, current);

        return new ArtronData(current, max);
    }

    private double randomCapacity() {
        ArtronConfigSettings.Bounds bounds = AITMod.CONFIG.getRiftChunkArtronBounds();
        int minimum = bounds.minimum();
        int maximum = bounds.maximum();

        if (minimum == maximum)
            return minimum;

        // nextBetween uses an int-sized range. This special case keeps the full
        // non-negative config range valid without overflowing maximum - minimum + 1.
        if (minimum == 0 && maximum == Integer.MAX_VALUE)
            return this.world.getRandom().nextInt() & Integer.MAX_VALUE;

        return this.world.getRandom().nextBetween(minimum, maximum);
    }

    private static double normalizeRequest(double requested) {
        return Double.isFinite(requested) ? Math.max(requested, 0) : 0;
    }

    private static double normalizeStored(double stored) {
        return Double.isFinite(stored) ? Math.max(stored, 0) : 0;
    }

    private record ArtronData(double current, double max) {}
}
