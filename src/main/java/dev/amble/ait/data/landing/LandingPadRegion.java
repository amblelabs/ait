package dev.amble.ait.data.landing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableCollection;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.lib.util.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import dev.amble.ait.core.tardis.manager.ServerTardisManager;

public class LandingPadRegion {

    public static final Codec<LandingPadRegion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("chunk").forGetter(o -> o.getChunk().toLong()),
            Codec.INT.fieldOf("y").forGetter(LandingPadRegion::getDefaultY),
            Codec.list(LandingPadSpot.CODEC).fieldOf("spots").forGetter(o -> o.spots),
            Codec.STRING.fieldOf("code").forGetter(LandingPadRegion::getLandingCode)
    ).apply(instance, LandingPadRegion::create));

    private static final int CHUNK_LENGTH = 16;
    private static final int MAX_SPOTS = (CHUNK_LENGTH * CHUNK_LENGTH) / 4;
    private static final int MAX_PER_ROW = CHUNK_LENGTH / 2;

    private final ChunkPos chunk;
    private final List<LandingPadSpot> spots;

    private final int defaultY;
    private String landingCode;

    private static LandingPadRegion create(long chunk, int y, List<LandingPadSpot> spots, String landingCode) {
        if (spots instanceof ImmutableCollection<?>) {
            spots = new ArrayList<>(spots);
        }

        return new LandingPadRegion(new ChunkPos(chunk), y, spots, landingCode);
    }

    private LandingPadRegion(ChunkPos chunk, int y, List<LandingPadSpot> spots, String landingCode) {
        this.chunk = chunk;
        this.spots = spots;
        this.landingCode = landingCode;

        this.defaultY = y;

        if (spots.isEmpty())
            this.createAllSpots();
    }

    public LandingPadRegion(ChunkPos pos, int y, String landingCode) {
        this(pos, y, new ArrayList<>(), landingCode);
    }

    public @Nullable LandingPadSpot getFreeSpot() {
        for (LandingPadSpot spot : this.spots) {
            if (spot.isOccupied())
                continue;

            return spot;
        }

        return null; // unreachable
    }

    private LandingPadSpot createSpot() {
        if (this.spots.isEmpty())
            return new LandingPadSpot(new BlockPos(
                    this.chunk.getStartX() + 1, this.defaultY, this.chunk.getStartZ() + 1
            ));

        float rowCount = ((float) this.spots.size() / MAX_PER_ROW);
        boolean isNewRow = rowCount == Math.round(rowCount);
        BlockPos lastPos = this.spots.get(this.spots.size() - 1).getPos();

        if (!isNewRow)
            return new LandingPadSpot(lastPos.add(2, 0, 0));

        return new LandingPadSpot(new BlockPos(this.spots.get(0).getPos().getX(),
                this.defaultY, lastPos.getZ() + 2));
    }

    private LandingPadSpot generateSpot() {
        LandingPadSpot created = this.createSpot();
        this.spots.add(created);

        return created;
    }

    public List<LandingPadSpot> getSpots() {
        return this.spots;
    }

    public int getDefaultY() {
        return defaultY;
    }

    public ChunkPos getChunk() {
        return chunk;
    }

    public String getLandingCode() {
        return landingCode;
    }

    public void setLandingCode(String string) {
        landingCode = string;
    }

    private boolean isFull() {
        return this.getFreeSpot() == null; // all spots must be occupied if none is found
    }

    private void createAllSpots() {
        int toCreate = MAX_SPOTS - this.spots.size();

        for (int i = 0; i < toCreate; i++) {
            this.generateSpot();
        }
    }

    public Optional<LandingPadSpot> getSpotAt(BlockPos pos) {
        // is in this chunk?
        if (this.chunk.toLong() != new ChunkPos(pos).toLong())
            return Optional.empty();

        // is in this region?
        for (LandingPadSpot spot : this.spots) {
            BlockPos p = spot.getPos();
            if (p.getX() == pos.getX() && p.getZ() == pos.getZ()) // ignore Y
                return Optional.of(spot);
        }

        // not found
        return Optional.empty();
    }

    public LandingPadSpot createSpotAt(BlockPos pos) {
        LandingPadSpot existing = this.getSpotAt(pos).orElse(null);

        if (existing != null) return existing;

        LandingPadSpot created = new LandingPadSpot(pos);
        this.spots.add(created);

        return created;
    }

    public void removeSpotAt(BlockPos pos) {
        LandingPadSpot existing = this.getSpotAt(pos).orElse(null);
        if (existing == null) return;

        this.removeSpot(existing);
    }

    private void removeSpot(LandingPadSpot spot) {
        this.spots.remove(spot);

        if (!spot.isOccupied()) return;

        // should the tardis take off and find a new spot?
        ServerTardisManager.getInstance().getTardis(ServerLifecycleHooks.get(), spot.getReference().getId(), tardis -> tardis.landingPad().release());
    }

    @Override
    public String toString() {
        return "LandingPadRegion{" +
                "chunk=" + chunk +
                ", spots=" + spots +
                ", defaultY=" + defaultY +
                '}';
    }
}
