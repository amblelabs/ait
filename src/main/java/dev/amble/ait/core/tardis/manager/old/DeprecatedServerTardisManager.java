package dev.amble.ait.core.tardis.manager.old;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.api.tardis.WorldWithTardis;
import dev.amble.ait.core.events.ServerCrashEvent;
import dev.amble.ait.core.events.WorldSaveEvent;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisManager;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.tardis.manager.TardisBuilder;
import dev.amble.ait.core.tardis.manager.TardisFileManager;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.TardisMap;
import dev.amble.ait.data.properties.Value;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.drtheo.multidim.MultiDim;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class DeprecatedServerTardisManager extends TardisManager<ServerTardis, MinecraftServer> implements TardisFileManager.TardisLoader<ServerTardis> {

    private static final long DORMANT_RETRY_BASE_TICKS = 5L * 20L;
    private static final long DORMANT_RETRY_MAX_TICKS = 5L * 60L * 20L;

    protected final TardisMap.Optional<ServerTardis> lookup = new TardisMap.Optional<>();
    protected final TardisFileManager<ServerTardis> fileManager = new TardisFileManager<>();
    private final PriorityQueue<DormantAuditEntry> dormantAuditQueue = new PriorityQueue<>();
    private final HashMap<UUID, Long> dormantAuditQueued = new HashMap<>();
    private final HashMap<UUID, DormantAuditSchedule> dormantAuditSchedules = new HashMap<>();
    private final Set<UUID> knownTardisIds = new HashSet<>();
    private final Set<UUID> dormantAuditLoaded = new HashSet<>();
    private long lastDormantLoadTick = Long.MIN_VALUE;

    public DeprecatedServerTardisManager() {
        this.fileManager.setLocked(true);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.fileManager.setLocked(false);
            this.dormantAuditQueue.clear();
            this.dormantAuditQueued.clear();
            this.dormantAuditSchedules.clear();
            this.knownTardisIds.clear();
            this.dormantAuditLoaded.clear();
            this.lastDormantLoadTick = Long.MIN_VALUE;
            this.knownTardisIds.addAll(this.fileManager.getTardisList(server));
            this.knownTardisIds.forEach(id -> this.queueDormantAudit(id, 0));
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(this::saveAndReset);

        ServerCrashEvent.EVENT.register(((server, report) -> this.reset())); // just panic and reset

        WorldSaveEvent.EVENT.register(world -> {
            if (world == WorldUtil.getOverworld())
                this.save(world.getServer(), false);
        });

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            this.forEach(tardis -> {
                if (tardis.isRemoved())
                    return;

                if (server.getTicks() % 20 == 0)
                    tardis.returnHome().tickDormant(server);

                if (!tardis.shouldTick()) return;

                tardis.tick(server);
            });

            if (server.getTicks() % 20 == 0) {
                this.releaseCompletedDormantAudits(server);
                this.loadNextDormantAuditTardis(server);
            }
        });
    }

    /**
     * Lazily loads persisted TARDISes so unattended safety systems can restore
     * their state after a restart. A cold-loaded TARDIS receives a full second
     * of normal dormant ticks, then is released again unless that audit found an
     * active workflow which still needs server ticks.
     */
    private void loadNextDormantAuditTardis(MinecraftServer server) {
        if (this.fileManager.isLocked() || this.hasPendingDormantSaveFailure(server)
                || this.lastDormantLoadTick == server.getTicks())
            return;

        DormantAuditEntry audit;
        while ((audit = this.dormantAuditQueue.peek()) != null) {
            if (audit.dueTick() > server.getTicks())
                return;

            this.dormantAuditQueue.remove();
            Long scheduled = this.dormantAuditQueued.get(audit.tardisId());
            if (scheduled == null || scheduled != audit.dueTick())
                continue;

            UUID id = audit.tardisId();
            this.dormantAuditQueued.remove(id);
            Either<ServerTardis, Exception> cached = this.lookup.get(id);
            if (cached != null && cached.map(value -> true, error -> false))
                continue;
            if (cached != null)
                this.lookup.remove(id, cached);

            this.lastDormantLoadTick = server.getTicks();
            Either<ServerTardis, Exception> loaded = this.loadTardis(server, id);
            loaded.ifLeft(tardis -> {
                this.dormantAuditLoaded.add(id);
                this.dormantAuditSchedules.computeIfAbsent(id, ignored -> new DormantAuditSchedule())
                        .recordDormantLoad(server.getTicks());
            });
            return;
        }
    }

    protected boolean hasPendingDormantSaveFailure(MinecraftServer server) {
        for (UUID id : this.dormantAuditLoaded) {
            DormantAuditSchedule schedule = this.dormantAuditSchedules.get(id);
            if (schedule != null && schedule.hasPendingSaveFailure())
                return true;
        }

        return false;
    }

    private void releaseCompletedDormantAudits(MinecraftServer server) {
        for (UUID id : Set.copyOf(this.dormantAuditLoaded)) {
            Either<ServerTardis, Exception> entry = this.lookup.get(id);
            ServerTardis tardis = entry == null ? null : entry.map(value -> value, error -> null);
            if (tardis == null || tardis.isRemoved()) {
                this.dormantAuditLoaded.remove(id);
                continue;
            }

            DormantAuditSchedule schedule = this.dormantAuditSchedules.computeIfAbsent(
                    id, ignored -> new DormantAuditSchedule());
            if (!schedule.canReleaseDormantLoad(server.getTicks()))
                continue;

            if (this.requiresDormantResidency(tardis) || !this.canReleaseDormantAudit(server, tardis))
                continue;

            schedule.audit(server, tardis);
            if (this.requiresDormantResidency(tardis) || !this.canReleaseDormantAudit(server, tardis))
                continue;

            if (tardis.hasPersistentChanges()) {
                if (!schedule.canAttemptSave(server.getTicks()))
                    continue;

                TardisEvents.SAVE.invoker().onSave(server, tardis, false);
                if (!this.fileManager.saveTardisChecked(server, this, tardis)) {
                    schedule.recordSaveFailure(server.getTicks());
                    continue;
                }
                schedule.recordSaveSuccess();
            } else if (schedule.hasPendingSaveFailure()) {
                // A regular world save may have persisted this retained cold load.
                schedule.recordSaveSuccess();
            }

            // A synchronous callback may have promoted the cold-loaded instance
            // while the save event was running. Only dispose the exact entry
            // which is still owned by the audit.
            if (!this.dormantAuditLoaded.contains(id) || this.lookup.get(id) != entry
                    || !this.lookup.remove(id, entry))
                continue;

            this.dormantAuditLoaded.remove(id);
            tardis.consumeDelta(component -> { });
            tardis.getHandlers().dispose();
            if (tardis.hasWorld() && tardis.world().getTardis() == tardis)
                tardis.world().setTardis(null);
            this.queueDormantAudit(id, schedule.nextLoadTick(server.getTicks()));
        }
    }

    private boolean requiresDormantResidency(ServerTardis tardis) {
        if (tardis.shouldTick() || tardis.returnHome().requiresDormantResidency())
            return true;

        if (tardis.isRefueling() && tardis.travel().isLanded()
                && tardis.returnHome().isParkedAtExactHome())
            return true;

        return false;
    }

    /** Allows concrete managers to retain cold loads needed by external state. */
    protected boolean canReleaseDormantAudit(MinecraftServer server, ServerTardis tardis) {
        return true;
    }

    protected void queueDormantAudit(UUID id) {
        this.queueDormantAudit(id, 0);
    }

    private void queueDormantAudit(UUID id, long dueTick) {
        if (id == null || !this.knownTardisIds.contains(id) || this.hasLoadedTardis(id))
            return;

        long due = Math.max(0, dueTick);
        Long previous = this.dormantAuditQueued.get(id);
        if (previous != null && previous <= due)
            return;

        this.dormantAuditQueued.put(id, due);
        this.dormantAuditQueue.add(new DormantAuditEntry(id, due));
    }

    private void rescheduleDormantAudit(UUID id, long dueTick) {
        if (id == null || !this.knownTardisIds.contains(id) || this.hasLoadedTardis(id))
            return;

        long due = Math.max(0, dueTick);
        this.dormantAuditQueued.put(id, due);
        this.dormantAuditQueue.add(new DormantAuditEntry(id, due));
    }

    private boolean hasLoadedTardis(UUID id) {
        Either<ServerTardis, Exception> entry = this.lookup.get(id);
        return entry != null && entry.map(value -> true, error -> false);
    }

    protected void queueKnownDormantAudits() {
        this.knownTardisIds.forEach(this::queueDormantAudit);
    }

    private void promoteDormantAuditTardis(UUID id) {
        this.dormantAuditLoaded.remove(id);
    }

    /**
     * Inspects a TARDIS without promoting a cold-loaded instance to normal
     * residency. Missing instances are loaded under the dormant-audit lease and
     * therefore share its one-load-per-second budget and normal release path.
     *
     * @return {@code true} once this target was resolved (including a failed
     * load, which keeps its own manager retry); {@code false} when loading was
     * deferred and the caller should keep its current cursor.
     */
    public boolean inspectDormantTardis(@NotNull MinecraftServer server, @NotNull UUID uuid,
                                        @NotNull Consumer<ServerTardis> consumer) {
        Objects.requireNonNull(server);
        Objects.requireNonNull(uuid);
        Objects.requireNonNull(consumer);

        if (this.fileManager.isLocked())
            return false;

        Either<ServerTardis, Exception> cached = this.lookup.get(uuid);
        if (cached != null && cached.map(value -> true, error -> false)) {
            cached.ifLeft(consumer);
            return true;
        }

        DormantAuditSchedule schedule = this.dormantAuditSchedules.computeIfAbsent(
                uuid, ignored -> new DormantAuditSchedule());
        long now = server.getTicks();
        if (this.hasPendingDormantSaveFailure(server) || !schedule.canAttemptLoad(now)
                || this.lastDormantLoadTick == now)
            return false;

        if (cached != null)
            this.lookup.remove(uuid, cached);

        // Invalidate any older queue entry. A failed load will install its own
        // retry deadline; a successful lease is rescheduled when it is released.
        this.dormantAuditQueued.remove(uuid);
        this.lastDormantLoadTick = now;
        Either<ServerTardis, Exception> loaded = this.loadTardis(server, uuid);
        return loaded.map(tardis -> {
            this.dormantAuditLoaded.add(uuid);
            schedule.recordDormantLoad(now);
            consumer.accept(tardis);
            return true;
        }, error -> true);
    }

    @Override
    protected GsonBuilder createGsonBuilder(Exclude.Strategy strategy) {
        return super.createGsonBuilder(strategy)
                .registerTypeAdapter(Tardis.class, ServerTardis.creator());
    }

    public ServerTardis create(TardisBuilder builder) {
        Objects.requireNonNull(builder);

        ServerTardis tardis = builder.build();
        this.lookup.put(tardis);
        this.knownTardisIds.add(tardis.getUuid());

        return tardis;
    }

    protected void sendTardisRemoval(MinecraftServer server, ServerTardis tardis) {
        if (tardis == null)
            return;

        PacketByteBuf data = PacketByteBufs.create();
        data.writeUuid(tardis.getUuid());

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            this.sendTardisRemoval(player, data);
        }
    }

    protected void sendTardisRemoval(ServerPlayerEntity player, ServerTardis tardis) {
        PacketByteBuf data = PacketByteBufs.create();
        data.writeUuid(tardis.getUuid());

        this.sendTardisRemoval(player, data);
    }

    protected void sendTardisRemoval(ServerPlayerEntity player, PacketByteBuf data) {
        ServerPlayNetworking.send(player, REMOVE, data);
    }

    public abstract void markComponentDirty(TardisComponent component);

    public abstract void markPropertyDirty(ServerTardis tardis, Value<?> value);

    @Override
    public @Nullable ServerTardis demandTardis(@NotNull MinecraftServer server, @NotNull UUID uuid) {
        Objects.requireNonNull(uuid);

        this.promoteDormantAuditTardis(uuid);

        if (this.fileManager.isLocked())
            return null;

        Either<ServerTardis, Exception> either = this.lookup.get(uuid);

        if (either == null || !either.map(value -> true, error -> false)) {
            if (either != null)
                this.lookup.remove(uuid, either);
            either = this.loadTardis(server, uuid);
        }

        return either.map(tardis -> tardis, o -> null);
    }

    @Override
    public void getTardis(MinecraftServer server, @NotNull UUID uuid, @NotNull Consumer<ServerTardis> consumer) {
        Objects.requireNonNull(uuid);
        Objects.requireNonNull(consumer);

        this.promoteDormantAuditTardis(uuid);

        if (this.fileManager.isLocked())
            return;

        Either<ServerTardis, Exception> either = this.lookup.get(uuid);

        if (either == null || !either.map(value -> true, error -> false)) {
            if (either != null)
                this.lookup.remove(uuid, either);
            either = this.loadTardis(server, uuid);
        }

        either.ifLeft(consumer);
    }

    @Override
    public TardisMap.Optional<ServerTardis> lookup() {
        return lookup;
    }

    @Override
    public void forEach(Consumer<ServerTardis> consumer) {
        this.lookup.forEach((uuid, either) -> either.ifLeft(consumer));
    }

    @Override
    public void reset() {
        this.dormantAuditQueue.clear();
        this.dormantAuditQueued.clear();
        this.dormantAuditSchedules.clear();
        this.knownTardisIds.clear();
        this.dormantAuditLoaded.clear();
        this.lastDormantLoadTick = Long.MIN_VALUE;
        super.reset();
    }

    @NotNull public Either<ServerTardis, Exception> loadTardis(MinecraftServer server, UUID uuid) {
        Either<ServerTardis, Exception> result = this.fileManager.loadTardis(server, this, uuid, this);

        this.lookup.put(uuid, result);
        DormantAuditSchedule schedule = this.dormantAuditSchedules.computeIfAbsent(
                uuid, ignored -> new DormantAuditSchedule());
        result.ifLeft(tardis -> {
            this.knownTardisIds.add(tardis.getUuid());
            schedule.recordLoadSuccess();
        }).ifRight(error -> {
            this.lookup.remove(uuid, result);
            this.rescheduleDormantAudit(uuid, schedule.recordLoadFailure(server.getTicks()));
        });
        return result;
    }

    public void loadAll(MinecraftServer server, @Nullable Consumer<ServerTardis> consumer) {
        for (UUID id : this.fileManager.getTardisList(server)) {
            this.getTardis(server, id, consumer);
        }
    }

    public void remove(MinecraftServer server, ServerTardis tardis) {
        Objects.requireNonNull(tardis);

        tardis.door().closeDoors();
        tardis.setRemoved(true);

        CachedDirectedGlobalPos exteriorPos = tardis.travel().position();

        if (exteriorPos != null) {
            if (tardis.hasWorld()) tardis.world().getPlayers().forEach(player
                    -> TardisUtil.teleportOutside(tardis, player));

            World world = exteriorPos.getWorld();
            BlockPos pos = exteriorPos.getPos();

            world.removeBlock(pos, false);
            world.removeBlockEntity(pos);
        }

        MultiDim.get(server).queueRemove(TardisServerWorld.keyForTardis(tardis));

        this.sendTardisRemoval(server, tardis);
        tardis.getHandlers().dispose();

        UUID id = tardis.getUuid();
        this.dormantAuditQueue.removeIf(audit -> id.equals(audit.tardisId()));
        this.dormantAuditQueued.remove(id);
        this.dormantAuditSchedules.remove(id);
        this.dormantAuditLoaded.remove(id);
        this.knownTardisIds.remove(id);
        this.lookup.remove(id);
        this.fileManager.delete(server, id);
    }

    protected int knownTardisCount() {
        return this.knownTardisIds.size();
    }

    private record DormantAuditEntry(UUID tardisId, long dueTick) implements Comparable<DormantAuditEntry> {
        @Override
        public int compareTo(DormantAuditEntry other) {
            int due = Long.compare(this.dueTick, other.dueTick);
            return due != 0 ? due : this.tardisId.compareTo(other.tardisId);
        }
    }

    private static final class DormantAuditSchedule {
        private long siegeDueTick = Long.MAX_VALUE;
        private long threatDueTick = Long.MAX_VALUE;
        private long exteriorDueTick = Long.MAX_VALUE;
        private long loadRetryTick;
        private long saveRetryTick;
        private long dormantLoadTick = Long.MIN_VALUE;
        private int loadFailures;
        private int saveFailures;
        private boolean initialized;

        private void audit(MinecraftServer server, ServerTardis tardis) {
            long now = server.getTicks();
            long requestedSiegeTick = tardis.returnHome().nextDormantSiegeAuditTick(server, now);
            boolean siegeDue = !this.initialized || now >= requestedSiegeTick;
            boolean threatCandidate = tardis.returnHome().needsDormantThreatAudits();
            boolean threatDue = threatCandidate && (!this.initialized
                    || this.threatDueTick == Long.MAX_VALUE || now >= this.threatDueTick);
            boolean exteriorDue = !this.initialized || now >= this.exteriorDueTick;
            tardis.returnHome().auditDormant(server, siegeDue, threatDue, exteriorDue,
                    !this.initialized && exteriorDue);

            this.siegeDueTick = tardis.returnHome().nextDormantSiegeAuditTick(server, now);
            if (!tardis.returnHome().needsDormantThreatAudits()) {
                this.threatDueTick = Long.MAX_VALUE;
            } else if (threatDue) {
                this.threatDueTick = safeAdd(now,
                        Math.max(1, AITMod.CONFIG.automaticThreatCheckIntervalSeconds) * 20L);
            }
            if (exteriorDue)
                this.exteriorDueTick = safeAdd(now,
                        Math.max(1, AITMod.CONFIG.missingExteriorCheckIntervalSeconds) * 20L);
            this.initialized = true;
        }

        private long nextLoadTick(long now) {
            if (!this.initialized)
                return now;

            long nextAudit = Math.min(this.siegeDueTick,
                    Math.min(this.threatDueTick, this.exteriorDueTick));
            return Math.max(safeAdd(now, 20L), nextAudit - 20L);
        }

        private long recordLoadFailure(long now) {
            this.loadRetryTick = safeAdd(now, retryDelay(this.loadFailures++));
            return this.loadRetryTick;
        }

        private void recordLoadSuccess() {
            this.loadFailures = 0;
            this.loadRetryTick = 0;
        }

        private boolean canAttemptLoad(long now) {
            return now >= this.loadRetryTick;
        }

        private void recordDormantLoad(long now) {
            this.dormantLoadTick = now;
        }

        private boolean canReleaseDormantLoad(long now) {
            return now > this.dormantLoadTick;
        }

        private boolean canAttemptSave(long now) {
            return now >= this.saveRetryTick;
        }

        private boolean hasPendingSaveFailure() {
            return this.saveFailures > 0;
        }

        private void recordSaveFailure(long now) {
            this.saveRetryTick = safeAdd(now, retryDelay(this.saveFailures++));
        }

        private void recordSaveSuccess() {
            this.saveFailures = 0;
            this.saveRetryTick = 0;
        }

        private static long retryDelay(int failures) {
            int shift = Math.min(Math.max(0, failures), 6);
            return Math.min(DORMANT_RETRY_MAX_TICKS, DORMANT_RETRY_BASE_TICKS << shift);
        }

        private static long safeAdd(long value, long increment) {
            return value > Long.MAX_VALUE - increment ? Long.MAX_VALUE : value + increment;
        }
    }

    private void save(MinecraftServer server, boolean close) {
        if (close)
            this.fileManager.setLocked(true);

        this.forEach(tardis -> {
            if (tardis == null)
                return;

            TardisEvents.SAVE.invoker().onSave(server, tardis, close);
            this.fileManager.saveTardis(server, this, tardis);
        });

        if (!close)
            return;

        for (ServerWorld world : server.getWorlds()) {
            ((WorldWithTardis) world).ait$withLookup(HashMap::clear);
        }
    }

    private void saveAndReset(MinecraftServer server) {
        this.save(server, true);
        this.reset();
    }

    /**
     * @return An initialized {@link ServerTardis} without attachments.
     */
    @Override
    public ServerTardis readTardis(Gson gson, JsonObject json) {
        ServerTardis tardis = gson.fromJson(json, ServerTardis.class);
        Tardis.init(tardis, TardisComponent.InitContext.deserialize());

        return tardis;
    }

    public static ServerPlayNetworking.PlayChannelHandler receiveTardis(Receiver receiver) {
        return (server, player, handler, buf, responseSender) -> {
            ServerTardisManager.getInstance().getTardis(server, buf.readUuid(),
                    tardis -> receiver.receive(tardis, server, player, handler, buf, responseSender));
        };
    }

    @FunctionalInterface
    public interface Receiver {
        void receive(ServerTardis tardis, MinecraftServer server, ServerPlayerEntity player,
                ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender);
    }
}
