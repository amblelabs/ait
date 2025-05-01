package dev.amble.ait.api.tardis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import net.minecraft.server.network.ServerPlayerEntity;
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

    static TardisEvents.SyncTardis forSync(PlayerTardisConsumer consumer) {
        return (player, chunk) -> {
            if (!(player.getWorld() instanceof WorldWithTardis withTardis) || !withTardis.ait$hasLookup())
                return;

            Set<ServerTardis> tardisSet = withTardis.ait$lookup().get(chunk.getPos());

            if (tardisSet == null)
                return;

            consumer.accept(player, tardisSet);
        };
    }

    static TardisEvents.UnloadTardis forDesync(PlayerTardisConsumer consumer) {
        return (player, chunk) -> {
            if (!(player.getWorld() instanceof WorldWithTardis withTardis) || !withTardis.ait$hasLookup())
                return;

            Set<ServerTardis> tardisSet = withTardis.ait$lookup().get(chunk);

            if (tardisSet == null)
                return;

            consumer.accept(player, tardisSet);
        };
    }

    @FunctionalInterface
    interface PlayerTardisConsumer {
        void accept(ServerPlayerEntity player, Set<ServerTardis> tardisSet);
    }

    final class Lookup extends HashMap<ChunkPos, Set<ServerTardis>> {

        public void put(ChunkPos pos, ServerTardis tardis) {
            this.computeIfAbsent(pos, chunkPos -> new HashSet<>()).add(tardis);
        }

        public void remove(ChunkPos pos, ServerTardis tardis) {
            Set<ServerTardis> set = this.get(pos);

            if (set == null)
                return;

            set.remove(tardis);

            if (set.isEmpty())
                this.remove(pos);
        }
    }
}
