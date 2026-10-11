package dev.amble.ait.core.tardis.manager;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;

/** Persistent active-protector index and per-player TARDIS visit history. */
public final class BiodataRestorationState extends PersistentState {
    private static final String ID = "ait_biodata_restoration";
    private static final String ACTIVE_KEY = "Active";
    private static final String ELIGIBLE_KEY = "EligiblePlayers";
    private static final String PLAYERS_KEY = "Players";
    private static final String PLAYER_KEY = "Player";
    private static final String VISITS_KEY = "Visits";
    private static final String TARDIS_KEY = "Tardis";
    private static final String ORDER_KEY = "Order";
    private static final String SEQUENCE_KEY = "Sequence";

    private final Set<UUID> activeTardises = new HashSet<>();
    private final Map<UUID, Set<UUID>> eligiblePlayers = new HashMap<>();
    private final Map<UUID, Map<UUID, Long>> visits = new HashMap<>();
    private long sequence;

    public static BiodataRestorationState get(MinecraftServer server) {
        return server.getOverworld().getPersistentStateManager()
                .getOrCreate(BiodataRestorationState::fromNbt, BiodataRestorationState::new, ID);
    }

    private static BiodataRestorationState fromNbt(NbtCompound nbt) {
        BiodataRestorationState state = new BiodataRestorationState();
        state.sequence = Math.max(0, nbt.getLong(SEQUENCE_KEY));

        NbtList active = nbt.getList(ACTIVE_KEY, NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < active.size(); i++) {
            NbtCompound entry = active.getCompound(i);
            if (!entry.containsUuid(TARDIS_KEY))
                continue;

            UUID tardisId = entry.getUuid(TARDIS_KEY);
            state.activeTardises.add(tardisId);
            if (!entry.contains(ELIGIBLE_KEY, NbtElement.LIST_TYPE))
                continue;

            Set<UUID> eligiblePlayers = new HashSet<>();
            NbtList eligible = entry.getList(ELIGIBLE_KEY, NbtElement.COMPOUND_TYPE);
            for (int j = 0; j < eligible.size(); j++) {
                NbtCompound player = eligible.getCompound(j);
                if (player.containsUuid(PLAYER_KEY))
                    eligiblePlayers.add(player.getUuid(PLAYER_KEY));
            }
            state.eligiblePlayers.put(tardisId, eligiblePlayers);
        }

        NbtList players = nbt.getList(PLAYERS_KEY, NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < players.size(); i++) {
            NbtCompound playerEntry = players.getCompound(i);
            if (!playerEntry.containsUuid(PLAYER_KEY))
                continue;

            Map<UUID, Long> playerVisits = new HashMap<>();
            NbtList visitEntries = playerEntry.getList(VISITS_KEY, NbtElement.COMPOUND_TYPE);
            for (int j = 0; j < visitEntries.size(); j++) {
                NbtCompound visit = visitEntries.getCompound(j);
                if (!visit.containsUuid(TARDIS_KEY))
                    continue;

                long order = visit.getLong(ORDER_KEY);
                UUID tardisId = visit.getUuid(TARDIS_KEY);
                playerVisits.merge(tardisId, order, Math::max);
                state.sequence = Math.max(state.sequence, order);
            }

            if (!playerVisits.isEmpty())
                state.visits.put(playerEntry.getUuid(PLAYER_KEY), playerVisits);
        }

        return state;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putLong(SEQUENCE_KEY, this.sequence);

        NbtList active = new NbtList();
        for (UUID tardisId : this.activeTardises) {
            if (tardisId == null)
                continue;
            NbtCompound entry = new NbtCompound();
            entry.putUuid(TARDIS_KEY, tardisId);
            NbtList eligible = new NbtList();
            for (UUID playerId : this.eligiblePlayers.getOrDefault(tardisId, Set.of())) {
                if (playerId == null)
                    continue;
                NbtCompound player = new NbtCompound();
                player.putUuid(PLAYER_KEY, playerId);
                eligible.add(player);
            }
            entry.put(ELIGIBLE_KEY, eligible);
            active.add(entry);
        }
        nbt.put(ACTIVE_KEY, active);

        NbtList players = new NbtList();
        for (Map.Entry<UUID, Map<UUID, Long>> player : this.visits.entrySet()) {
            if (player.getKey() == null || player.getValue() == null || player.getValue().isEmpty())
                continue;

            NbtCompound playerEntry = new NbtCompound();
            playerEntry.putUuid(PLAYER_KEY, player.getKey());
            NbtList visitEntries = new NbtList();
            for (Map.Entry<UUID, Long> visit : player.getValue().entrySet()) {
                if (visit.getKey() == null || visit.getValue() == null)
                    continue;
                NbtCompound visitEntry = new NbtCompound();
                visitEntry.putUuid(TARDIS_KEY, visit.getKey());
                visitEntry.putLong(ORDER_KEY, visit.getValue());
                visitEntries.add(visitEntry);
            }

            if (visitEntries.isEmpty())
                continue;
            playerEntry.put(VISITS_KEY, visitEntries);
            players.add(playerEntry);
        }
        nbt.put(PLAYERS_KEY, players);
        return nbt;
    }

    public void recordVisit(UUID playerId, UUID tardisId) {
        if (playerId == null || tardisId == null)
            return;

        if (this.sequence == Long.MAX_VALUE)
            this.renumberVisits();

        this.visits.computeIfAbsent(playerId, ignored -> new HashMap<>()).put(tardisId, ++this.sequence);
        this.markDirty();
    }

    public UUID getLastVisitedTardis(UUID playerId) {
        Map<UUID, Long> playerVisits = this.visits.get(playerId);
        if (playerVisits == null || playerVisits.isEmpty())
            return null;

        UUID latest = null;
        long latestOrder = Long.MIN_VALUE;
        for (Map.Entry<UUID, Long> visit : playerVisits.entrySet()) {
            if (visit.getKey() != null && visit.getValue() != null && visit.getValue() > latestOrder) {
                latest = visit.getKey();
                latestOrder = visit.getValue();
            }
        }
        return latest;
    }

    public Map<UUID, Long> getVisits(UUID playerId) {
        Map<UUID, Long> playerVisits = this.visits.get(playerId);
        return playerVisits == null ? Map.of() : Collections.unmodifiableMap(new HashMap<>(playerVisits));
    }

    public Set<UUID> getActiveTardises() {
        return Collections.unmodifiableSet(new HashSet<>(this.activeTardises));
    }

    public Set<UUID> getEligibleActiveTardises(UUID playerId) {
        if (playerId == null)
            return Set.of();

        Set<UUID> result = new HashSet<>();
        for (UUID tardisId : this.activeTardises) {
            Set<UUID> eligible = this.eligiblePlayers.get(tardisId);
            if (eligible != null && eligible.contains(playerId))
                result.add(tardisId);
        }
        return Collections.unmodifiableSet(result);
    }

    public boolean needsEligibilityIndex(UUID tardisId) {
        return tardisId != null && this.activeTardises.contains(tardisId)
                && !this.eligiblePlayers.containsKey(tardisId);
    }

    public void setEligiblePlayers(UUID tardisId, Set<UUID> players) {
        if (tardisId == null || !this.activeTardises.contains(tardisId))
            return;

        Set<UUID> updated = players == null ? new HashSet<>() : new HashSet<>(players);
        Set<UUID> previous = this.eligiblePlayers.put(tardisId, updated);
        if (!updated.equals(previous))
            this.markDirty();
    }

    public void setEligible(UUID tardisId, UUID playerId, boolean eligible) {
        if (tardisId == null || playerId == null || !this.activeTardises.contains(tardisId))
            return;

        Set<UUID> players = this.eligiblePlayers.get(tardisId);
        if (players == null)
            return;

        boolean changed = eligible ? players.add(playerId) : players.remove(playerId);
        if (changed)
            this.markDirty();
    }

    public void setActive(UUID tardisId, boolean active) {
        if (tardisId == null)
            return;

        boolean changed = active ? this.activeTardises.add(tardisId) : this.activeTardises.remove(tardisId);
        if (!active)
            changed |= this.eligiblePlayers.remove(tardisId) != null;
        if (changed)
            this.markDirty();
    }

    public void removeTardis(UUID tardisId) {
        if (tardisId == null)
            return;

        boolean changed = this.activeTardises.remove(tardisId);
        changed |= this.eligiblePlayers.remove(tardisId) != null;
        for (Map<UUID, Long> playerVisits : this.visits.values())
            changed |= playerVisits.remove(tardisId) != null;
        this.visits.values().removeIf(Map::isEmpty);
        if (changed)
            this.markDirty();
    }

    private void renumberVisits() {
        this.sequence = 0;
        this.visits.values().stream().flatMap(map -> map.entrySet().stream())
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> entry.setValue(++this.sequence));
    }
}
