package dev.amble.ait.core.tardis.manager;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Either;
import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.api.tardis.WorldWithTardis;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.manager.old.DeprecatedServerTardisManager;
import dev.amble.ait.core.tardis.util.NetworkUtil;
import dev.amble.ait.data.properties.Value;
import dev.amble.ait.registry.impl.TardisComponentRegistry;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.util.ServerLifecycleHooks;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.jetbrains.annotations.NotNull;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class ServerTardisManager extends DeprecatedServerTardisManager {

    private static final int EXACT_HOME_FILES_PER_TICK = 4;

    private static ServerTardisManager instance;

    private final Set<ServerTardis> delta = new HashSet<>();
    private final Map<UUID, ExactHome> exactHomesByTardis = new HashMap<>();
    private final Map<ExactHome, Set<UUID>> exactHomeClaims = new HashMap<>();
    private final ArrayDeque<UUID> exactHomeClaimQueue = new ArrayDeque<>();
    private final Set<UUID> exactHomeClaimOverrides = new HashSet<>();
    private ExactHomeIndexState exactHomeIndexState = ExactHomeIndexState.UNINITIALIZED;
    private long exactHomeIndexRetryTick;
    private long exactHomeIndexLastWarningTick = Long.MIN_VALUE;

    public static void init() {
        instance = new ServerTardisManager();
    }

    private ServerTardisManager() {
        TardisEvents.SYNC_TARDIS.register(WorldWithTardis.forSync((player, tardisSet) -> {
            if (this.fileManager.isLocked())
                return;

            if (AITMod.CONFIG.sendBulk && tardisSet.size() >= 8) {
                this.sendTardisBulk(player, tardisSet);
                return;
            }

            this.sendTardisAll(player, tardisSet);
        }));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server)
                -> this.sendTardisAll(handler.getPlayer(), NetworkUtil.findLinkedItems(handler.getPlayer())));

        if (DEMENTIA) {
            TardisEvents.UNLOAD_TARDIS.register(WorldWithTardis.forDesync((player, tardisSet) -> {
                for (ServerTardis tardis : tardisSet) {
                    if (isInvalid(tardis))
                        continue;

                    this.sendTardisRemoval(player, tardis);
                }
            }));
        }

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (this.fileManager.isLocked())
                return;

            this.tickExactHomeClaims(server);

            for (ServerTardis tardis : this.delta) {
                if (isInvalid(tardis))
                    continue;

                if (!tardis.hasDelta())
                    continue;

                PacketByteBuf buf = this.prepareSendDelta(tardis);
                tardis.consumeDelta(component -> this.writeComponent(component, buf));

                NetworkUtil.getSubscribedPlayers(tardis).forEach(
                        watching -> ServerPlayNetworking.send(watching, SEND_COMPONENT, buf)
                );
            }

            this.delta.clear();
        });
    }

    @Override
    public ServerTardis create(TardisBuilder builder) {
        if (this.isFull())
            return null;

        MinecraftServer server = ServerLifecycleHooks.get();
        CachedDirectedGlobalPos position = builder.getPosition();
        if (position != null && (server == null
                || !this.canClaimExactHome(server, builder.getUuid(), position)))
            return null;

        ServerTardis result = super.create(builder);
        if (result == null)
            return null;

        this.updateExactHomeClaim(server, result.getUuid(), result.stats().getHome());
        this.sendTardisAll(Set.of(result));
        return result;
    }

    @Override
    public @NotNull Either<ServerTardis, Exception> loadTardis(MinecraftServer server, UUID uuid) {
        Either<ServerTardis, Exception> result = super.loadTardis(server, uuid);
        result.ifLeft(tardis -> this.updateExactHomeClaim(server, tardis.getUuid(), tardis.stats().getHome()));
        return result;
    }

    public Set<UUID> getPersistedTardisIds(MinecraftServer server) {
        return new HashSet<>(this.fileManager.getTardisList(server));
    }

    /** Checks an exact home claim without loading dormant TARDISes or worlds. */
    public boolean canClaimExactHome(MinecraftServer server, UUID claimant, CachedDirectedGlobalPos proposed) {
        return this.exactHomeStatus(server, claimant, proposed) == HomeClaimStatus.AVAILABLE;
    }

    public HomeClaimStatus exactHomeStatus(MinecraftServer server, UUID claimant,
                                           CachedDirectedGlobalPos proposed) {
        if (server == null || claimant == null || proposed == null
                || !server.isOnThread() || proposed.getDimension() == null || proposed.getPos() == null)
            return HomeClaimStatus.UNAVAILABLE;

        return this.exactHomeStatus(ExactHome.of(proposed), claimant);
    }

    public HomeClaimStatus exactHomeStatus(ServerWorld world, BlockPos position) {
        if (world == null || position == null || !world.getServer().isOnThread())
            return HomeClaimStatus.UNAVAILABLE;

        return this.exactHomeStatus(
                new ExactHome(world.getRegistryKey().getValue(), position.toImmutable()), null);
    }

    public void updateExactHomeClaim(MinecraftServer server, UUID tardisId, CachedDirectedGlobalPos home) {
        if (server == null || tardisId == null || !server.isOnThread()
                || home == null || home.getDimension() == null || home.getPos() == null)
            return;
        if (this.exactHomeIndexState == ExactHomeIndexState.FAILED)
            return;

        this.replaceExactHomeClaim(tardisId, ExactHome.of(home));
        if (this.exactHomeIndexState != ExactHomeIndexState.READY)
            this.exactHomeClaimOverrides.add(tardisId);
    }

    private HomeClaimStatus exactHomeStatus(ExactHome home, UUID claimant) {
        if (home == null || this.exactHomeIndexState != ExactHomeIndexState.READY)
            return HomeClaimStatus.UNAVAILABLE;

        Set<UUID> claims = this.exactHomeClaims.get(home);
        if (claims == null || claims.isEmpty()
                || claimant != null && claims.size() == 1 && claims.contains(claimant))
            return HomeClaimStatus.AVAILABLE;
        return HomeClaimStatus.OCCUPIED;
    }

    private void tickExactHomeClaims(MinecraftServer server) {
        if (this.exactHomeIndexState == ExactHomeIndexState.RETRY_WAIT) {
            if (server.getTicks() < this.exactHomeIndexRetryTick)
                return;

            this.exactHomesByTardis.clear();
            this.exactHomeClaims.clear();
            this.exactHomeClaimQueue.clear();
            this.exactHomeClaimOverrides.clear();
            this.exactHomeIndexState = ExactHomeIndexState.UNINITIALIZED;
        }

        if (this.exactHomeIndexState == ExactHomeIndexState.UNINITIALIZED) {
            try {
                this.exactHomeClaimQueue.addAll(this.fileManager.getTardisListChecked(server));
                this.exactHomeIndexState = ExactHomeIndexState.BUILDING;
            } catch (IOException | RuntimeException exception) {
                this.failExactHomeClaims(server, exception);
                return;
            }
        }
        if (this.exactHomeIndexState != ExactHomeIndexState.BUILDING)
            return;

        try {
            for (int processed = 0; processed < EXACT_HOME_FILES_PER_TICK
                    && !this.exactHomeClaimQueue.isEmpty(); processed++) {
                UUID id = this.exactHomeClaimQueue.removeFirst();
                if (this.exactHomeClaimOverrides.contains(id))
                    continue;

                TardisFileManager.StoredHome stored = this.fileManager.readStoredHome(server, id);
                this.replaceExactHomeClaim(id, new ExactHome(stored.dimension(), stored.position()));
            }

            if (this.exactHomeClaimQueue.isEmpty()) {
                List<ServerTardis> loaded = new ArrayList<>();
                this.forEach(loaded::add);
                for (ServerTardis tardis : loaded) {
                    if (isInvalid(tardis) || tardis.getUuid() == null)
                        continue;

                    ExactHome home = ExactHome.of(tardis.stats().getHome());
                    if (home == null)
                        throw new IllegalStateException("Loaded TARDIS has no valid home: " + tardis.getUuid());
                    this.replaceExactHomeClaim(tardis.getUuid(), home);
                }
                this.exactHomeClaimOverrides.clear();
                this.exactHomeIndexState = ExactHomeIndexState.READY;
            }
        } catch (IOException | RuntimeException exception) {
            this.failExactHomeClaims(server, exception);
        }
    }

    private void failExactHomeClaims(MinecraftServer server, Exception exception) {
        this.exactHomesByTardis.clear();
        this.exactHomeClaims.clear();
        this.exactHomeClaimQueue.clear();
        this.exactHomeClaimOverrides.clear();
        boolean retryable = !(exception instanceof JsonSyntaxException)
                && (exception instanceof SecurityException || causedByIOException(exception));
        this.exactHomeIndexState = retryable ? ExactHomeIndexState.RETRY_WAIT : ExactHomeIndexState.FAILED;
        this.exactHomeIndexRetryTick = server.getTicks() + 5L * 20L;

        if (this.exactHomeIndexLastWarningTick == Long.MIN_VALUE
                || server.getTicks() - this.exactHomeIndexLastWarningTick >= 60L * 20L) {
            AITMod.LOGGER.warn("Could not build the exact home claim index; rejecting home operations", exception);
            this.exactHomeIndexLastWarningTick = server.getTicks();
        }
    }

    private static boolean causedByIOException(Throwable throwable) {
        while (throwable != null) {
            if (throwable instanceof IOException)
                return true;
            throwable = throwable.getCause();
        }
        return false;
    }

    private void replaceExactHomeClaim(UUID tardisId, ExactHome home) {
        ExactHome previous = home == null
                ? this.exactHomesByTardis.remove(tardisId)
                : this.exactHomesByTardis.put(tardisId, home);
        if (previous != null) {
            Set<UUID> previousClaims = this.exactHomeClaims.get(previous);
            if (previousClaims != null) {
                previousClaims.remove(tardisId);
                if (previousClaims.isEmpty())
                    this.exactHomeClaims.remove(previous);
            }
        }
        if (home != null)
            this.exactHomeClaims.computeIfAbsent(home, ignored -> new HashSet<>()).add(tardisId);
    }

    public ServerTardis getLoadedTardis(UUID uuid) {
        if (uuid == null)
            return null;

        Either<ServerTardis, ?> result = this.lookup.get(uuid);
        return result == null ? null : result.map(tardis -> tardis, error -> null);
    }

    @Override
    public void remove(MinecraftServer server, ServerTardis tardis) {
        UUID uuid = tardis == null ? null : tardis.getUuid();
        super.remove(server, tardis);

        if (uuid == null)
            return;

        try {
            if (!this.fileManager.hasTardisFileChecked(server, uuid)) {
                this.replaceExactHomeClaim(uuid, null);
                if (this.exactHomeIndexState != ExactHomeIndexState.READY)
                    this.exactHomeClaimOverrides.add(uuid);
            }
        } catch (IOException | RuntimeException exception) {
            AITMod.LOGGER.warn("Could not confirm removal of TARDIS {}; retaining its exact home claim",
                    uuid, exception);
        }
    }

    private void sendTardis(ServerPlayerEntity player, PacketByteBuf data) {
        ServerPlayNetworking.send(player, SEND, data);
    }

    private void writeSend(ServerTardis tardis, PacketByteBuf buf) {
        buf.writeUuid(tardis.getUuid());
        buf.writeString(this.networkGson.toJson(tardis, ServerTardis.class));
    }

    private void writeComponent(TardisComponent component, PacketByteBuf buf) {
        String rawId = TardisComponentRegistry.getInstance().get(component);

        buf.writeString(rawId);
        buf.writeString(this.networkGson.toJson(component));
    }

    private PacketByteBuf prepareSend(ServerTardis tardis) {
        PacketByteBuf data = PacketByteBufs.create();
        this.writeSend(tardis, data);

        return data;
    }

    private PacketByteBuf prepareSendDelta(ServerTardis tardis) {
        PacketByteBuf data = PacketByteBufs.create();

        data.writeUuid(tardis.getUuid());
        data.writeShort(tardis.getDeltaSize());

        return data;
    }

    protected void sendTardisBulk(ServerPlayerEntity player, Set<ServerTardis> set) {
        PacketByteBuf data = PacketByteBufs.create();
        data.writeInt(set.size());

        for (ServerTardis tardis : set) {
            if (isInvalid(tardis))
                continue;

            this.writeSend(tardis, data);
        }

        ServerPlayNetworking.send(player, SEND_BULK, data);
    }

    protected void sendTardisAll(ServerPlayerEntity player, Set<ServerTardis> set) {
        for (ServerTardis tardis : set) {
            if (isInvalid(tardis))
                continue;

            TardisEvents.SEND_TARDIS.invoker().send(tardis, player);
            this.sendTardis(player, this.prepareSend(tardis));
        }
    }

    protected void sendTardisAll(Set<ServerTardis> set) {
        for (ServerTardis tardis : set) {
            if (isInvalid(tardis))
                continue;

            PacketByteBuf buf = this.prepareSend(tardis);

            NetworkUtil.getSubscribedPlayers(tardis).forEach(
                    watching -> {
                        TardisEvents.SEND_TARDIS.invoker().send(tardis, watching);
                        this.sendTardis(watching, buf);
                    }
            );
        }
    }

    public void mark(ServerWorld world, ServerTardis tardis, ChunkPos chunk) {
        ((WorldWithTardis) world).ait$lookup().put(chunk, tardis);

        NetworkUtil.getSubscribedPlayers(tardis).forEach(player ->
                TardisEvents.SYNC_TARDIS.invoker().sync(player, chunk));
    }

    public void unmark(ServerWorld world, ServerTardis tardis, ChunkPos chunk) {
        ((WorldWithTardis) world).ait$withLookup(lookup -> lookup.remove(chunk, tardis));
    }

    @Override
    public void markComponentDirty(TardisComponent component) {
        if (this.fileManager.isLocked())
            return;

        if (!(component.tardis() instanceof ServerTardis tardis))
            return;

        if (isInvalid(tardis))
            return;

        tardis.markDirty(component);
        this.delta.add(tardis);
    }

    @Override
    public void markPropertyDirty(ServerTardis tardis, Value<?> value) {
        this.markComponentDirty(value.getHolder());
    }

    @Override
    public void reset() {
        this.delta.clear();
        this.exactHomesByTardis.clear();
        this.exactHomeClaims.clear();
        this.exactHomeClaimQueue.clear();
        this.exactHomeClaimOverrides.clear();
        this.exactHomeIndexState = ExactHomeIndexState.UNINITIALIZED;
        this.exactHomeIndexRetryTick = 0L;
        this.exactHomeIndexLastWarningTick = Long.MIN_VALUE;
        super.reset();
    }

    public boolean isFull() {
        int max = AITMod.CONFIG.maxTardises;
        return max > 0 && this.lookup.size() >= max;
    }

    private record ExactHome(Identifier dimension, BlockPos position) {
        private static ExactHome of(CachedDirectedGlobalPos home) {
            if (home == null || home.getDimension() == null || home.getPos() == null)
                return null;
            return new ExactHome(home.getDimension().getValue(), home.getPos().toImmutable());
        }
    }

    public enum HomeClaimStatus {
        AVAILABLE,
        OCCUPIED,
        UNAVAILABLE
    }

    private enum ExactHomeIndexState {
        UNINITIALIZED,
        BUILDING,
        READY,
        RETRY_WAIT,
        FAILED
    }

    private static boolean isInvalid(ServerTardis tardis) {
        return tardis == null || tardis.isRemoved();
    }

    public static ServerTardisManager getInstance() {
        return instance;
    }
}
