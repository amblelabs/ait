package dev.amble.ait.api.tardis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import dev.amble.ait.core.tardis.ServerTardis;

public interface WorldWithTardis {

    Lookup ait$lookup();

    boolean ait$hasLookup();

    default void ait$withLookup(Consumer<Lookup> consumer) {
        if (!this.ait$hasLookup())
            return;

        consumer.accept(this.ait$lookup());
    }

    default void ait$withLookup(ChunkPos pos, Consumer<ChunkData> consumer) {
        this.ait$withLookup(lookup -> consumer.accept(lookup.get(pos)));
    }

    default void ait$withLookup(BlockPos pos, Consumer<ChunkData> consumer) {
        this.ait$withLookup(new ChunkPos(pos), consumer);
    }

    static TardisEvents.SyncTardis forSync(PlayerTardisConsumer consumer) {
        return (player, chunk) -> {
            if (!(player.getWorld() instanceof WorldWithTardis withTardis) || !withTardis.ait$hasLookup())
                return;

            ChunkData data = withTardis.ait$lookup().get(chunk);

            if (data == null)
                return;

            consumer.accept(player, data.tardisSet);
        };
    }

    static TardisEvents.UnloadTardis forDesync(PlayerTardisConsumer consumer) {
        return (player, chunk) -> {
            if (!(player.getWorld() instanceof WorldWithTardis withTardis) || !withTardis.ait$hasLookup())
                return;

            ChunkData data = withTardis.ait$lookup().get(chunk);

            if (data == null)
                return;

            consumer.accept(player, data.tardisSet);
        };
    }

    @FunctionalInterface
    interface PlayerTardisConsumer {
        void accept(ServerPlayerEntity player, Set<ServerTardis> tardisSet);
    }

    final class Lookup extends HashMap<ChunkPos, ChunkData> {

        private final ServerWorld world;

        public Lookup(ServerWorld world) {
            this.world = world;
        }

        public void put(ChunkPos pos, ServerTardis tardis) {
            this.computeIfAbsent(pos, chunkPos -> new ChunkData(world, chunkPos)).tardisSet.add(tardis);
        }

        public void remove(ChunkPos pos, ServerTardis tardis) {
            ChunkData data = this.get(pos);

            if (data == null)
                return;

            data.remove(tardis);

            if (data.isInvalid())
                this.remove(pos);
        }
    }

    final class ChunkData {
        private final ServerWorld world;
        private final ChunkPos chunkPos;
        private final Set<ServerTardis> tardisSet;
        private int forceLoaded;

        public ChunkData(ServerWorld world, ChunkPos chunkPos, Set<ServerTardis> tardisSet) {
            this.world = world;
            this.chunkPos = chunkPos;
            this.tardisSet = tardisSet;
        }

        public ChunkData(ServerWorld world, ChunkPos chunkPos) {
            this(world, chunkPos, new HashSet<>());
        }

        public boolean isInvalid() {
            return tardisSet.isEmpty();
        }

        public void remove(ServerTardis tardis) {
            tardisSet.remove(tardis);

            if (this.isInvalid())
                this.update(true);
        }

        public void forceLoad() {
            this.forceLoaded++;
            this.update(false);
        }

        public void unforceLoad() {
            this.forceLoaded--;
            this.update(false);
        }

        public void update(boolean clean) {
            world.getChunkManager().setChunkForced(this.chunkPos, !clean && this.forceLoaded != 0);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (ChunkData) obj;
            return Objects.equals(this.chunkPos, that.chunkPos) &&
                    Objects.equals(this.tardisSet, that.tardisSet);
        }

        @Override
        public int hashCode() {
            return Objects.hash(chunkPos);
        }

        @Override
        public String toString() {
            return "ChunkData[" +
                    "chunkPos=" + chunkPos + ", " +
                    "tardisSet=" + tardisSet + ']';
        }
    }
}
