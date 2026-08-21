package dev.amble.ait.core.tardis.manager;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.item.TemporalRecoveryItemUtil;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.data.Loyalty;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.PersistentState;

/** Stores a bounded server-global history of item stacks which were genuinely destroyed. */
public final class TemporalRecoveryState extends PersistentState {
    public static final int CAPACITY = 54;

    private static final String ID = "ait_temporal_recovery";
    private static final String PLAYERS_KEY = "Players";
    private static final String PLAYER_KEY = "Player";
    private static final String ITEMS_KEY = "Items";
    private static final String ENTRY_KEY = "Entry";
    private static final String STACK_KEY = "Stack";
    private static final String TIME_KEY = "Time";

    private final Map<UUID, List<LostItem>> histories = new HashMap<>();

    public static TemporalRecoveryState get(MinecraftServer server) {
        return server.getOverworld().getPersistentStateManager()
                .getOrCreate(TemporalRecoveryState::fromNbt, TemporalRecoveryState::new, ID);
    }

    private static TemporalRecoveryState fromNbt(NbtCompound nbt) {
        TemporalRecoveryState state = new TemporalRecoveryState();
        state.readPlayers(nbt.getList(PLAYERS_KEY, NbtElement.COMPOUND_TYPE));
        if (state.normalizeHistories())
            state.markDirty();
        return state;
    }

    private void readPlayers(NbtList players) {
        for (int i = 0; i < players.size(); i++) {
            NbtCompound playerEntry = players.getCompound(i);
            if (!playerEntry.containsUuid(PLAYER_KEY))
                continue;

            UUID playerId = playerEntry.getUuid(PLAYER_KEY);
            List<LostItem> items = this.histories.computeIfAbsent(playerId, ignored -> new ArrayList<>());
            Set<UUID> knownEntries = new HashSet<>();
            for (LostItem item : items)
                knownEntries.add(item.id());

            NbtList entries = playerEntry.getList(ITEMS_KEY, NbtElement.COMPOUND_TYPE);
            for (int j = 0; j < entries.size(); j++) {
                NbtCompound itemEntry = entries.getCompound(j);
                if (!itemEntry.containsUuid(ENTRY_KEY)
                        || !itemEntry.contains(STACK_KEY, NbtElement.COMPOUND_TYPE))
                    continue;

                UUID entryId = itemEntry.getUuid(ENTRY_KEY);
                ItemStack stack = ItemStack.fromNbt(itemEntry.getCompound(STACK_KEY));
                if (!stack.isEmpty() && knownEntries.add(entryId))
                    items.add(new LostItem(entryId, stack, itemEntry.getLong(TIME_KEY)));
            }
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList players = new NbtList();
        for (Map.Entry<UUID, List<LostItem>> playerEntry : this.histories.entrySet()) {
            if (playerEntry.getKey() == null || playerEntry.getValue() == null || playerEntry.getValue().isEmpty())
                continue;

            NbtCompound playerNbt = new NbtCompound();
            playerNbt.putUuid(PLAYER_KEY, playerEntry.getKey());
            NbtList items = new NbtList();
            for (LostItem lost : playerEntry.getValue()) {
                if (lost == null || lost.id() == null || lost.stack() == null || lost.stack().isEmpty())
                    continue;

                NbtCompound itemNbt = new NbtCompound();
                itemNbt.putUuid(ENTRY_KEY, lost.id());
                itemNbt.put(STACK_KEY, lost.stack().writeNbt(new NbtCompound()));
                itemNbt.putLong(TIME_KEY, lost.time());
                items.add(itemNbt);
            }

            if (!items.isEmpty()) {
                playerNbt.put(ITEMS_KEY, items);
                players.add(playerNbt);
            }
        }

        nbt.put(PLAYERS_KEY, players);
        return nbt;
    }

    public boolean recordDestroyed(ItemEntity entity, ItemStack destroyed, @Nullable UUID owner) {
        if (entity == null || destroyed == null || destroyed.isEmpty() || owner == null
                || entity.getWorld().isClient() || entity.getServer() == null)
            return false;

        List<LostItem> items = this.histories.computeIfAbsent(owner, ignored -> new ArrayList<>());
        items.add(new LostItem(UUID.randomUUID(), destroyed.copy(),
                entity.getServer().getOverworld().getTime()));
        compactHistory(items);
        while (items.size() > CAPACITY)
            items.remove(0);
        this.markDirty();
        return true;
    }

    public List<LostItem> latest(UUID tardisId, UUID playerId) {
        List<LostItem> stored = this.histories.get(playerId);
        if (stored == null || stored.isEmpty())
            return List.of();

        List<LostItem> result = new ArrayList<>(Math.min(stored.size(), CAPACITY));
        for (int i = stored.size() - 1; i >= 0 && result.size() < CAPACITY; i--) {
            LostItem entry = stored.get(i);
            if (TemporalRecoveryItemUtil.blocksRecovery(entry.stack(), tardisId))
                continue;
            result.add(new LostItem(entry.id(), entry.stack().copy(), entry.time()));
        }
        return result;
    }

    public boolean open(ServerTardis tardis, ServerPlayerEntity player) {
        if (!this.canView(tardis, player))
            return false;

        player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                (syncId, playerInventory, ignored) -> new TemporalRecoveryScreenHandler(syncId, playerInventory,
                        new TemporalRecoveryInventory(this, tardis, player)),
                Text.translatable("container.ait.temporal_recovery")));
        return true;
    }

    public ItemStack claim(ServerTardis tardis, ServerPlayerEntity player, UUID entryId) {
        if (!this.canClaim(tardis, player) || entryId == null)
            return ItemStack.EMPTY;

        List<LostItem> items = this.histories.get(player.getUuid());
        if (items == null)
            return ItemStack.EMPTY;

        int index = -1;
        for (int i = 0; i < items.size(); i++) {
            if (entryId.equals(items.get(i).id())) {
                index = i;
                break;
            }
        }
        if (index < 0)
            return ItemStack.EMPTY;

        ItemStack stored = items.get(index).stack();
        if (TemporalRecoveryItemUtil.blocksRecovery(stored, tardis.getUuid()))
            return ItemStack.EMPTY;

        int fuelCost = fuelCost(stored);
        synchronized (tardis.fuel()) {
            if (!tardis.fuel().hasPower() || tardis.getFuel() < fuelCost)
                return ItemStack.EMPTY;
            tardis.removeFuel(fuelCost);
        }

        ItemStack recovered = stored.copy();
        items.remove(index);
        if (items.isEmpty())
            this.histories.remove(player.getUuid());
        this.markDirty();

        tardis.loyalty().subLevel(player, Math.max(0, AITMod.CONFIG.temporalRecoveryLoyaltyCost));
        tardis.subsystems().engine().removeDurability(Math.max(0, AITMod.CONFIG.temporalRecoveryEngineDamage));
        player.getServerWorld().spawnParticles(AITMod.CORAL_PARTICLE, player.getX(), player.getBodyY(0.5),
                player.getZ(), 30, 0.55, 0.9, 0.55, 0.08);
        if (tardis.fuel().isOutOfFuel())
            tardis.fuel().disablePower();
        return recovered;
    }

    public boolean canUse(ServerTardis tardis, ServerPlayerEntity player) {
        return this.canView(tardis, player);
    }

    public boolean canView(ServerTardis tardis, ServerPlayerEntity player) {
        return tardis != null && player != null && !tardis.isRemoved() && tardis.getUuid() != null
                && tardis.hasWorld() && player.getServerWorld() == tardis.world()
                && isParkedAtExactHome(tardis)
                && tardis.loyalty().get(player).type() != Loyalty.Type.REJECT;
    }

    public boolean canClaim(ServerTardis tardis, ServerPlayerEntity player) {
        return this.canView(tardis, player) && tardis.fuel().hasPower()
                && tardis.loyalty().get(player).isOf(Loyalty.Type.PILOT);
    }

    private boolean normalizeHistories() {
        boolean changed = false;
        var iterator = this.histories.entrySet().iterator();
        while (iterator.hasNext()) {
            List<LostItem> items = iterator.next().getValue();
            if (items == null || items.isEmpty()) {
                iterator.remove();
                changed = true;
                continue;
            }

            if (!isChronological(items)) {
                items.sort(Comparator.comparingLong(LostItem::time));
                changed = true;
            }
            changed |= compactHistory(items);
            if (items.size() > CAPACITY) {
                items.subList(0, items.size() - CAPACITY).clear();
                changed = true;
            }
            if (items.isEmpty()) {
                iterator.remove();
                changed = true;
            }
        }
        return changed;
    }

    private static boolean compactHistory(List<LostItem> items) {
        if (items == null || items.isEmpty())
            return false;

        List<LostItem> newestFirst = new ArrayList<>(items.size());
        Map<StackKey, ArrayDeque<LostItem>> partialStacks = new HashMap<>();
        boolean changed = false;

        for (int i = items.size() - 1; i >= 0; i--) {
            LostItem source = items.get(i);
            if (source == null || source.stack() == null || source.stack().isEmpty()) {
                changed = true;
                continue;
            }

            ItemStack remaining = source.stack().copy();
            int maxCount = serializedMaxCount(remaining);
            ArrayDeque<LostItem> matching = partialStacks.computeIfAbsent(
                    StackKey.of(remaining), ignored -> new ArrayDeque<>());
            if (maxCount > 1) {
                while (!matching.isEmpty() && !remaining.isEmpty()) {
                    LostItem target = matching.peekFirst();
                    ItemStack targetStack = target.stack();
                    int targetMaximum = serializedMaxCount(targetStack);
                    if (targetStack.getCount() >= targetMaximum) {
                        matching.removeFirst();
                        continue;
                    }

                    int moved = Math.min(remaining.getCount(), targetMaximum - targetStack.getCount());
                    targetStack.increment(moved);
                    remaining.decrement(moved);
                    changed = true;
                    if (targetStack.getCount() >= targetMaximum)
                        matching.removeFirst();
                }
            }

            boolean retainedSourceId = false;
            while (!remaining.isEmpty()) {
                int count = Math.min(maxCount, remaining.getCount());
                UUID id = !retainedSourceId && source.id() != null ? source.id() : UUID.randomUUID();
                LostItem retained = new LostItem(id, remaining.copyWithCount(count), source.time());
                newestFirst.add(retained);
                if (count < maxCount)
                    matching.addLast(retained);
                retainedSourceId = true;
                remaining.decrement(count);
                if (!remaining.isEmpty() || source.id() == null)
                    changed = true;
            }
        }

        if (!changed)
            return false;

        Collections.reverse(newestFirst);
        items.clear();
        items.addAll(newestFirst);
        return true;
    }

    private static boolean isChronological(List<LostItem> items) {
        long previousTime = Long.MIN_VALUE;
        for (LostItem item : items) {
            if (item == null)
                continue;
            if (previousTime > item.time())
                return false;
            previousTime = item.time();
        }
        return true;
    }

    private static int serializedMaxCount(ItemStack stack) {
        return Math.max(1, Math.min(stack.getMaxCount(), Byte.MAX_VALUE));
    }

    private static int fuelCost(ItemStack stack) {
        long base = stack.getMaxCount() <= 1
                ? Math.max(0, AITMod.CONFIG.temporalRecoveryUnstackableFuelCost)
                : (long) Math.max(0, AITMod.CONFIG.temporalRecoveryStackItemFuelCost) * stack.getCount();
        long rarity = switch (stack.getRarity()) {
            case COMMON -> Math.max(0, AITMod.CONFIG.temporalRecoveryCommonFuelCost);
            case UNCOMMON -> Math.max(0, AITMod.CONFIG.temporalRecoveryUncommonFuelCost);
            case RARE -> Math.max(0, AITMod.CONFIG.temporalRecoveryRareFuelCost);
            case EPIC -> Math.max(0, AITMod.CONFIG.temporalRecoveryEpicFuelCost);
            default -> Math.max(0, AITMod.CONFIG.temporalRecoveryEpicFuelCost);
        };
        return (int) Math.min(Integer.MAX_VALUE, base + rarity);
    }

    private static boolean isParkedAtExactHome(ServerTardis tardis) {
        CachedDirectedGlobalPos home = tardis.stats().getHome();
        CachedDirectedGlobalPos position = tardis.travel().position();
        return tardis.travel().isLanded() && home != null && position != null
                && home.getDimension().equals(position.getDimension())
                && home.getPos().equals(position.getPos());
    }

    private record StackKey(Item item, @Nullable NbtCompound nbt) {
        private static StackKey of(ItemStack stack) {
            return new StackKey(stack.getItem(), stack.getNbt());
        }
    }

    public record LostItem(UUID id, ItemStack stack, long time) {
    }
}
