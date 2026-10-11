package dev.amble.ait.core.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import dev.amble.ait.api.SiegeInventoryProvider;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public final class SiegeInventoryUtil {

    private static final int MAX_NBT_DEPTH = 32;
    private static final int MAX_NBT_NODES = 4_096;
    private static final int MAX_LIST_ENTRIES = 512;
    private static final int MAX_TRACKED_ID_HINTS = 64;
    private static final int MAX_TRACKED_CARRIER_IDS = MAX_NBT_NODES;
    public static final String TRACKED_ID_HINTS_KEY = "AITSiegeTrackedTardises";

    public enum ScanResult {
        FOUND,
        NOT_FOUND,
        INCOMPLETE,
        INCOMPLETE_HINTED;

        public boolean mayContain() {
            return this != NOT_FOUND;
        }

        public boolean blocksEntry() {
            return this == FOUND || this == INCOMPLETE_HINTED;
        }
    }

    public record FindResult(Set<UUID> tardisIds, boolean complete) {
        public FindResult {
            tardisIds = tardisIds == null || tardisIds.isEmpty() ? Set.of() : Set.copyOf(tardisIds);
        }
    }

    private SiegeInventoryUtil() {
    }

    public static void init() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> trackIfInventoryCarrier(entity));
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> trackIfInventoryCarrier(entity));
    }

    private static void trackIfInventoryCarrier(Entity entity) {
        if (!shouldTrack(entity))
            return;

        track(entity);
    }

    public static boolean shouldTrack(Entity entity) {
        if (entity == null)
            return false;

        boolean registeredInventory = entity instanceof SiegeInventoryProvider provider
                && provider.ait$getSiegeInventories() != null
                && !provider.ait$getSiegeInventories().isEmpty();
        boolean trackedInventory = entity instanceof SiegeInventoryProvider provider
                && provider.ait$getTrackedSiegeItems() != null
                && !provider.ait$getTrackedSiegeItems().isEmpty();
        if (entity instanceof ItemEntity item) {
            ItemStack stack = item.getStack();
            return trackedInventory || !stack.isEmpty()
                    && (stack.isOf(AITItems.SIEGE_ITEM) || stack.hasNbt());
        }

        return entity instanceof Inventory || entity instanceof InventoryOwner
                || registeredInventory || trackedInventory;
    }

    public static Set<UUID> find(Inventory inventory) {
        return findResult(inventory).tardisIds();
    }

    public static FindResult findResult(Inventory inventory) {
        Set<UUID> result = new HashSet<>();
        Set<UUID> hints = new HashSet<>();
        ScanBudget budget = new ScanBudget();
        collect(inventory, result, hints, budget);
        if (!budget.isComplete())
            result.addAll(hints);
        return new FindResult(result, budget.isComplete());
    }

    public static Set<UUID> find(ItemStack stack) {
        Set<UUID> result = new HashSet<>();
        Set<UUID> hints = new HashSet<>();
        ScanBudget budget = new ScanBudget();
        collectTopLevel(stack, result, hints, budget);
        if (!budget.isComplete())
            result.addAll(hints);
        return result;
    }

    public static Set<UUID> find(Entity entity) {
        return findResult(entity).tardisIds();
    }

    public static FindResult findResult(Entity entity) {
        Set<UUID> result = new HashSet<>();
        Set<UUID> hints = new HashSet<>();
        ScanBudget budget = new ScanBudget();
        if (entity instanceof ItemEntity itemEntity) {
            ItemStack stack = itemEntity.getStack();
            if (collectTopLevel(stack, result, hints, budget))
                itemEntity.setStack(stack);
        }
        if (entity instanceof ServerPlayerEntity player) {
            ItemStack cursor = player.currentScreenHandler.getCursorStack();
            if (collectTopLevel(cursor, result, hints, budget))
                player.currentScreenHandler.setCursorStack(cursor);
        }

        for (Inventory inventory : inventories(entity))
            collect(inventory, result, hints, budget);

        if (!budget.isComplete())
            result.addAll(hints);

        return new FindResult(result, budget.isComplete());
    }

    public static void track(Entity entity) {
        if (!(entity.getWorld() instanceof ServerWorld world))
            return;

        ServerTardisManager manager = ServerTardisManager.getInstance();
        if (manager == null)
            return;

        FindResult found = isDestroyedCarrier(entity)
                ? new FindResult(Set.of(), true) : findResult(entity);
        Set<UUID> current = found.tardisIds();
        for (UUID tardisId : current) {
            manager.getTardis(world.getServer(), tardisId, tardis -> {
                if (tardis.siege().isActive())
                    tardis.returnHome().trackSiegeItemEntity(entity);
            });
        }

        if (!(entity instanceof SiegeInventoryProvider provider))
            return;

        Set<UUID> previous = provider.ait$getTrackedSiegeItems();
        if (previous == null)
            previous = Set.of();
        if (!found.complete()) {
            Set<UUID> retained = new HashSet<>(previous);
            retained.addAll(current);
            for (UUID tardisId : retained) {
                if (current.contains(tardisId))
                    continue;
                manager.getTardis(world.getServer(), tardisId, tardis -> {
                    if (tardis.siege().isActive())
                        tardis.returnHome().trackSiegeItemEntity(entity);
                });
            }
            provider.ait$setTrackedSiegeItems(limitTrackedIds(retained));
            return;
        }

        UUID carrierId = entity.getUuid();
        for (UUID tardisId : previous) {
            if (current.contains(tardisId))
                continue;

            manager.getTardis(world.getServer(), tardisId, tardis -> {
                if (tardis.siege().isActive()
                        && (isDestroyedCarrier(entity) || scan(entity, tardisId) == ScanResult.NOT_FOUND))
                    tardis.returnHome().forgetSiegeItemEntity(world, carrierId);
            });
        }
        provider.ait$setTrackedSiegeItems(limitTrackedIds(current));
    }

    private static boolean isDestroyedCarrier(Entity entity) {
        return entity.isRemoved() && entity.getRemovalReason() != null
                && entity.getRemovalReason().shouldDestroy();
    }

    public static void rememberTrackedSiegeItem(Entity entity, UUID tardisId) {
        if (!(entity instanceof SiegeInventoryProvider provider) || tardisId == null)
            return;

        Set<UUID> tracked = provider.ait$getTrackedSiegeItems();
        if (tracked != null && tracked.contains(tardisId))
            return;

        Set<UUID> updated = tracked == null ? new HashSet<>() : new HashSet<>(tracked);
        updated.add(tardisId);
        provider.ait$setTrackedSiegeItems(limitTrackedIds(updated));
    }

    public static ScanResult scan(Inventory inventory, UUID tardisId) {
        ScanBudget budget = new ScanBudget();
        return result(contains(inventory, tardisId, budget), budget);
    }

    public static ScanResult scanCarried(ServerPlayerEntity player, UUID tardisId) {
        if (player == null || tardisId == null)
            return ScanResult.NOT_FOUND;

        boolean incomplete = false;
        boolean matchingHint = false;
        if (player.currentScreenHandler != null) {
            ScanResult cursor = scan(player.currentScreenHandler.getCursorStack(), tardisId);
            if (cursor == ScanResult.FOUND)
                return ScanResult.FOUND;
            incomplete = cursor == ScanResult.INCOMPLETE;
            matchingHint = cursor == ScanResult.INCOMPLETE_HINTED;
        }

        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.size(); slot++) {
            ScanResult carried = scan(inventory.getStack(slot), tardisId);
            if (carried == ScanResult.FOUND)
                return ScanResult.FOUND;
            incomplete |= carried == ScanResult.INCOMPLETE;
            matchingHint |= carried == ScanResult.INCOMPLETE_HINTED;
        }

        return matchingHint ? ScanResult.INCOMPLETE_HINTED
                : incomplete ? ScanResult.INCOMPLETE : ScanResult.NOT_FOUND;
    }

    private static boolean contains(Inventory inventory, UUID tardisId, ScanBudget budget) {
        for (int slot = 0; slot < inventory.size(); slot++) {
            if (contains(inventory.getStack(slot), tardisId, 0, budget))
                return true;
        }

        return false;
    }

    public static ScanResult scan(ItemStack stack, UUID tardisId) {
        ScanBudget budget = new ScanBudget();
        return result(contains(stack, tardisId, 0, budget), budget);
    }

    public static ScanResult scan(Entity entity, UUID tardisId) {
        ScanBudget budget = new ScanBudget();
        if (entity instanceof ItemEntity itemEntity
                && contains(itemEntity.getStack(), tardisId, 0, budget))
            return ScanResult.FOUND;
        if (entity instanceof ServerPlayerEntity player
                && contains(player.currentScreenHandler.getCursorStack(), tardisId, 0, budget))
            return ScanResult.FOUND;

        for (Inventory inventory : inventories(entity)) {
            if (contains(inventory, tardisId, budget))
                return ScanResult.FOUND;
        }

        return result(false, budget);
    }

    private static ScanResult result(boolean found, ScanBudget budget) {
        if (found)
            return ScanResult.FOUND;

        if (budget.isComplete())
            return ScanResult.NOT_FOUND;

        return budget.hasMatchingHint() ? ScanResult.INCOMPLETE_HINTED : ScanResult.INCOMPLETE;
    }

    private static int count(Inventory inventory, UUID tardisId, ScanBudget budget) {
        int result = 0;
        for (int slot = 0; slot < inventory.size(); slot++)
            result += count(inventory.getStack(slot), tardisId, 0, budget);
        return result;
    }

    private static int count(Entity entity, UUID tardisId, ScanBudget budget) {
        int result = 0;
        if (entity instanceof ItemEntity itemEntity)
            result += count(itemEntity.getStack(), tardisId, 0, budget);
        if (entity instanceof ServerPlayerEntity player)
            result += count(player.currentScreenHandler.getCursorStack(), tardisId, 0, budget);

        for (Inventory inventory : inventories(entity))
            result += count(inventory, tardisId, budget);
        return result;
    }

    private static int count(ItemStack stack, UUID tardisId, int depth, ScanBudget budget) {
        if (stack == null || stack.isEmpty() || !budget.visit(depth))
            return 0;
        if (isSiegeItem(stack, tardisId))
            return stack.getCount();

        NbtCompound nbt = stack.getNbt();
        return nbt == null ? 0 : countFromNbt(nbt, tardisId, depth + 1, budget);
    }

    private static int countFromNbt(NbtElement element, UUID tardisId, int depth, ScanBudget budget) {
        if (element == null || !budget.visit(depth))
            return 0;

        int result = 0;
        if (element instanceof NbtCompound compound) {
            for (String key : compound.getKeys()) {
                NbtElement child = compound.get(key);
                if (child instanceof NbtCompound childCompound && isSerializedStack(childCompound)) {
                    ItemStack nested = readStack(childCompound);
                    if (nested != null && !nested.isEmpty())
                        result += count(nested, tardisId, depth + 1, budget);
                    else
                        result += countFromNbt(childCompound, tardisId, depth + 1, budget);
                } else {
                    result += countFromNbt(child, tardisId, depth + 1, budget);
                }
            }
        } else if (element instanceof NbtList list) {
            if (!budget.allowList(list.size()))
                return 0;

            for (int index = 0; index < list.size(); index++) {
                NbtElement child = list.get(index);
                if (child instanceof NbtCompound childCompound && isSerializedStack(childCompound)) {
                    ItemStack nested = readStack(childCompound);
                    if (nested != null && !nested.isEmpty())
                        result += count(nested, tardisId, depth + 1, budget);
                    else
                        result += countFromNbt(childCompound, tardisId, depth + 1, budget);
                } else {
                    result += countFromNbt(child, tardisId, depth + 1, budget);
                }
            }
        }
        return result;
    }

    public static boolean remove(Inventory inventory, UUID tardisId) {
        ScanBudget countBudget = new ScanBudget();
        int expected = count(inventory, tardisId, countBudget);
        if (expected <= 0 || !countBudget.isComplete())
            return false;

        ScanBudget removalBudget = new ScanBudget();
        int removed = removeAll(inventory, tardisId, removalBudget);
        return removalBudget.isComplete() && removed == expected;
    }

    private static int removeAll(Inventory inventory, UUID tardisId, ScanBudget budget) {
        int removed = 0;
        boolean changed = false;
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (isSiegeItem(stack, tardisId)) {
                removed += stack.getCount();
                inventory.setStack(slot, ItemStack.EMPTY);
                changed = true;
                continue;
            }

            int nested = removeNested(stack, tardisId, 0, budget);
            if (nested > 0) {
                removed += nested;
                inventory.setStack(slot, stack);
                changed = true;
            }
        }

        if (changed)
            inventory.markDirty();
        return removed;
    }

    public static boolean remove(ItemStack stack, UUID tardisId) {
        ScanBudget countBudget = new ScanBudget();
        int expected = count(stack, tardisId, 0, countBudget);
        if (expected <= 0 || !countBudget.isComplete())
            return false;

        ScanBudget removalBudget = new ScanBudget();
        int removed;
        if (isSiegeItem(stack, tardisId)) {
            removed = stack.getCount();
            stack.setCount(0);
        } else {
            removed = removeNested(stack, tardisId, 0, removalBudget);
        }
        return removalBudget.isComplete() && removed == expected;
    }

    public static boolean remove(Entity entity, UUID tardisId) {
        ScanBudget countBudget = new ScanBudget();
        int expected = count(entity, tardisId, countBudget);
        if (expected <= 0 || !countBudget.isComplete())
            return false;

        ScanBudget removalBudget = new ScanBudget();
        int removed = 0;
        boolean discardEntity = false;
        if (entity instanceof ItemEntity itemEntity) {
            ItemStack stack = itemEntity.getStack();
            if (isSiegeItem(stack, tardisId)) {
                removed += stack.getCount();
                discardEntity = true;
            } else {
                int nested = removeNested(stack, tardisId, 0, removalBudget);
                removed += nested;
                if (nested > 0)
                    itemEntity.setStack(stack);
            }
        }

        if (entity instanceof ServerPlayerEntity player) {
            ItemStack cursorStack = player.currentScreenHandler.getCursorStack();
            if (isSiegeItem(cursorStack, tardisId)) {
                removed += cursorStack.getCount();
                player.currentScreenHandler.setCursorStack(ItemStack.EMPTY);
            } else {
                int nested = removeNested(cursorStack, tardisId, 0, removalBudget);
                removed += nested;
                if (nested > 0)
                    player.currentScreenHandler.setCursorStack(cursorStack);
            }
        }

        for (Inventory inventory : inventories(entity))
            removed += removeAll(inventory, tardisId, removalBudget);

        if (discardEntity)
            entity.discard();

        return removalBudget.isComplete() && removed == expected;
    }

    private static List<Inventory> inventories(Entity entity) {
        List<Inventory> result = new ArrayList<>();
        Set<Inventory> seen = Collections.newSetFromMap(new IdentityHashMap<>());

        if (entity instanceof ServerPlayerEntity player) {
            addInventory(result, seen, player.getInventory());
            addInventory(result, seen, player.getEnderChestInventory());
        }

        if (entity instanceof Inventory inventory)
            addInventory(result, seen, inventory);

        if (entity instanceof InventoryOwner owner)
            addInventory(result, seen, owner.getInventory());

        if (entity instanceof SiegeInventoryProvider provider) {
            java.util.Collection<? extends Inventory> inventories = provider.ait$getSiegeInventories();
            if (inventories != null) {
                for (Inventory inventory : inventories)
                    addInventory(result, seen, inventory);
            }
        }

        return result;
    }

    private static void addInventory(List<Inventory> result, Set<Inventory> seen, Inventory inventory) {
        if (inventory != null && seen.add(inventory))
            result.add(inventory);
    }

    private static boolean collectTopLevel(ItemStack stack, Set<UUID> result, Set<UUID> hints,
                                           ScanBudget budget) {
        if (stack == null || stack.isEmpty())
            return false;

        Set<UUID> stackResult = new HashSet<>();
        Set<UUID> stackHints = new HashSet<>();
        addTrackedIdHints(stackHints, readTrackedIdHints(stack.getNbt()));
        boolean startedComplete = budget.isComplete();
        boolean nestedChanged = collect(stack, stackResult, stackHints, 0, budget);
        result.addAll(stackResult);
        addTrackedIdHints(hints, stackHints);
        boolean hintChanged = updateTrackedIdHints(stack, stackResult,
                startedComplete && budget.isComplete());
        return nestedChanged || hintChanged;
    }

    private static boolean collect(ItemStack stack, Set<UUID> result, Set<UUID> hints,
                                   int depth, ScanBudget budget) {
        if (stack == null || stack.isEmpty() || !budget.visit(depth))
            return false;

        UUID tardisId = getSiegeTardisId(stack);
        if (tardisId != null) {
            result.add(tardisId);
            return false;
        }

        NbtCompound nbt = stack.getNbt();
        return nbt != null && collectFromNbt(nbt, result, hints, depth + 1, budget);
    }

    private static void collect(Inventory inventory, Set<UUID> result, Set<UUID> hints,
                                ScanBudget budget) {
        if (inventory == null)
            return;

        boolean changed = false;
        for (int slot = 0; slot < inventory.size(); slot++)
            changed |= collectTopLevel(inventory.getStack(slot), result, hints, budget);
        if (changed)
            inventory.markDirty();
    }

    private static boolean collectFromNbt(NbtElement element, Set<UUID> result, Set<UUID> hints,
                                          int depth, ScanBudget budget) {
        if (!canContainSerializedStacks(element) || !budget.visit(depth))
            return false;

        boolean changed = false;
        if (element instanceof NbtCompound compound) {
            addTrackedIdHints(hints, readTrackedIdHints(compound));
            for (String key : List.copyOf(compound.getKeys())) {
                if (TRACKED_ID_HINTS_KEY.equals(key))
                    continue;

                NbtElement child = compound.get(key);
                if (child instanceof NbtCompound childCompound && isSerializedStack(childCompound)) {
                    ItemStack nested = readStack(childCompound);
                    if (nested != null && !nested.isEmpty()) {
                        Set<UUID> nestedResult = new HashSet<>();
                        Set<UUID> nestedHints = new HashSet<>();
                        boolean startedComplete = budget.isComplete();
                        boolean nestedChanged = collect(nested, nestedResult, nestedHints,
                                depth + 1, budget);
                        result.addAll(nestedResult);
                        addTrackedIdHints(hints, nestedHints);
                        boolean hintChanged = updateTrackedIdHints(nested, nestedResult,
                                startedComplete && budget.isComplete());
                        if (nestedChanged || hintChanged) {
                            compound.put(key, writeStack(nested, childCompound));
                            changed = true;
                        }
                    } else {
                        changed |= collectFromNbt(childCompound, result, hints,
                                depth + 1, budget);
                    }
                } else {
                    changed |= collectFromNbt(child, result, hints, depth + 1, budget);
                }
            }
        } else if (element instanceof NbtList list) {
            if (!budget.allowList(list.size()))
                return false;

            for (int index = 0; index < list.size(); index++) {
                NbtElement child = list.get(index);
                if (child instanceof NbtCompound childCompound && isSerializedStack(childCompound)) {
                    ItemStack nested = readStack(childCompound);
                    if (nested != null && !nested.isEmpty()) {
                        Set<UUID> nestedResult = new HashSet<>();
                        Set<UUID> nestedHints = new HashSet<>();
                        boolean startedComplete = budget.isComplete();
                        boolean nestedChanged = collect(nested, nestedResult, nestedHints,
                                depth + 1, budget);
                        result.addAll(nestedResult);
                        addTrackedIdHints(hints, nestedHints);
                        boolean hintChanged = updateTrackedIdHints(nested, nestedResult,
                                startedComplete && budget.isComplete());
                        if (nestedChanged || hintChanged) {
                            list.set(index, writeStack(nested, childCompound));
                            changed = true;
                        }
                    } else {
                        changed |= collectFromNbt(childCompound, result, hints,
                                depth + 1, budget);
                    }
                } else {
                    changed |= collectFromNbt(child, result, hints, depth + 1, budget);
                }
            }
        }
        return changed;
    }

    private static boolean updateTrackedIdHints(ItemStack stack, Set<UUID> actual, boolean complete) {
        if (stack == null || stack.isEmpty())
            return false;

        NbtCompound nbt = stack.getNbt();
        Set<UUID> previous = readTrackedIdHints(nbt);
        Set<UUID> desired = new HashSet<>();
        if (!complete)
            desired.addAll(previous);
        if (getSiegeTardisId(stack) == null && actual != null)
            desired.addAll(actual);
        desired = limitTrackedHints(desired);
        if (previous.equals(desired))
            return false;

        if (desired.isEmpty()) {
            if (nbt != null)
                writeTrackedIdHints(nbt, Set.of());
        } else {
            writeTrackedIdHints(stack.getOrCreateNbt(), desired);
        }
        return true;
    }

    private static boolean contains(ItemStack stack, UUID tardisId, int depth, ScanBudget budget) {
        if (stack == null || stack.isEmpty())
            return false;

        if (isSiegeItem(stack, tardisId))
            return true;

        NbtCompound nbt = stack.getNbt();
        if (nbt != null && readTrackedIdHints(nbt).contains(tardisId))
            budget.markMatchingHint();
        if (!budget.visit(depth))
            return false;

        return nbt != null && visitSerializedStacks(nbt, depth + 1, budget,
                nested -> contains(nested, tardisId, depth + 1, budget), hint -> {
                    if (hint.equals(tardisId))
                        budget.markMatchingHint();
                    return false;
                });
    }

    private static boolean visitSerializedStacks(NbtElement element, int depth, ScanBudget budget,
                                                 java.util.function.Predicate<ItemStack> visitor,
                                                 java.util.function.Predicate<UUID> hintVisitor) {
        if (!canContainSerializedStacks(element) || !budget.visit(depth))
            return false;

        if (element instanceof NbtCompound compound) {
            for (UUID id : readTrackedIdHints(compound)) {
                if (hintVisitor.test(id))
                    return true;
            }
            for (String key : compound.getKeys()) {
                if (TRACKED_ID_HINTS_KEY.equals(key))
                    continue;
                NbtElement child = compound.get(key);
                if (child instanceof NbtCompound childCompound && isSerializedStack(childCompound)) {
                    ItemStack nested = readStack(childCompound);
                    if (nested != null && !nested.isEmpty()) {
                        if (visitor.test(nested))
                            return true;
                    } else if (visitSerializedStacks(childCompound, depth + 1, budget, visitor, hintVisitor)) {
                        return true;
                    }
                } else if (visitSerializedStacks(child, depth + 1, budget, visitor, hintVisitor)) {
                    return true;
                }
            }
        } else if (element instanceof NbtList list) {
            if (!budget.allowList(list.size()))
                return false;

            int size = list.size();
            for (int index = 0; index < size; index++) {
                NbtElement child = list.get(index);
                if (child instanceof NbtCompound childCompound && isSerializedStack(childCompound)) {
                    ItemStack nested = readStack(childCompound);
                    if (nested != null && !nested.isEmpty()) {
                        if (visitor.test(nested))
                            return true;
                    } else if (visitSerializedStacks(childCompound, depth + 1, budget, visitor, hintVisitor)) {
                        return true;
                    }
                } else if (visitSerializedStacks(child, depth + 1, budget, visitor, hintVisitor)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean canContainSerializedStacks(@Nullable NbtElement element) {
        if (element instanceof NbtCompound)
            return true;
        if (!(element instanceof NbtList list))
            return false;

        byte heldType = list.getHeldType();
        return heldType == NbtElement.COMPOUND_TYPE || heldType == NbtElement.LIST_TYPE;
    }

    public static Set<UUID> readTrackedIdHints(@Nullable NbtCompound nbt) {
        return readTrackedIds(nbt, MAX_TRACKED_ID_HINTS);
    }

    public static Set<UUID> readTrackedCarrierIds(@Nullable NbtCompound nbt) {
        return readTrackedIds(nbt, MAX_TRACKED_CARRIER_IDS);
    }

    private static Set<UUID> readTrackedIds(@Nullable NbtCompound nbt, int maximum) {
        if (nbt == null || !nbt.contains(TRACKED_ID_HINTS_KEY, NbtElement.LIST_TYPE))
            return Set.of();

        NbtList list = nbt.getList(TRACKED_ID_HINTS_KEY, NbtElement.STRING_TYPE);
        if (list.isEmpty())
            return Set.of();

        Set<UUID> result = new HashSet<>();
        int limit = Math.min(list.size(), maximum);
        for (int index = 0; index < limit; index++) {
            try {
                result.add(UUID.fromString(list.getString(index)));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return result.isEmpty() ? Set.of() : Set.copyOf(result);
    }

    public static void writeTrackedIdHints(NbtCompound nbt, @Nullable Set<UUID> ids) {
        writeTrackedIds(nbt, ids, MAX_TRACKED_ID_HINTS);
    }

    public static void writeTrackedCarrierIds(NbtCompound nbt, @Nullable Set<UUID> ids) {
        writeTrackedIds(nbt, ids, MAX_TRACKED_CARRIER_IDS);
    }

    private static void writeTrackedIds(NbtCompound nbt, @Nullable Set<UUID> ids, int maximum) {
        if (nbt == null)
            return;
        if (ids == null || ids.isEmpty()) {
            nbt.remove(TRACKED_ID_HINTS_KEY);
            return;
        }

        List<UUID> ordered = ids.stream().filter(java.util.Objects::nonNull).sorted()
                .limit(maximum).toList();
        if (ordered.isEmpty()) {
            nbt.remove(TRACKED_ID_HINTS_KEY);
            return;
        }

        NbtList list = new NbtList();
        for (UUID id : ordered)
            list.add(NbtString.of(id.toString()));
        nbt.put(TRACKED_ID_HINTS_KEY, list);
    }

    public static Set<UUID> limitTrackedIds(@Nullable Set<UUID> ids) {
        return limitTrackedIds(ids, MAX_TRACKED_CARRIER_IDS);
    }

    private static Set<UUID> limitTrackedHints(@Nullable Set<UUID> ids) {
        return limitTrackedIds(ids, MAX_TRACKED_ID_HINTS);
    }

    private static Set<UUID> limitTrackedIds(@Nullable Set<UUID> ids, int maximum) {
        if (ids == null || ids.isEmpty())
            return Set.of();

        List<UUID> ordered = ids.stream().filter(java.util.Objects::nonNull).sorted()
                .limit(maximum).toList();
        return ordered.isEmpty() ? Set.of() : Set.copyOf(ordered);
    }

    private static void addTrackedIdHints(Set<UUID> target, @Nullable Set<UUID> additions) {
        if (target == null || additions == null || additions.isEmpty()
                || target.size() >= MAX_TRACKED_ID_HINTS)
            return;

        for (UUID id : additions.stream().filter(java.util.Objects::nonNull).sorted().toList()) {
            target.add(id);
            if (target.size() >= MAX_TRACKED_ID_HINTS)
                return;
        }
    }

    private static int removeNested(ItemStack stack, UUID tardisId, int depth, ScanBudget budget) {
        if (stack == null || stack.isEmpty() || !budget.visit(depth))
            return 0;

        NbtCompound nbt = stack.getNbt();
        return nbt == null ? 0 : removeFromNbt(nbt, tardisId, depth + 1, budget);
    }

    private static int removeFromNbt(NbtElement element, UUID tardisId, int depth, ScanBudget budget) {
        if (element == null || !budget.visit(depth))
            return 0;

        int removed = 0;
        if (element instanceof NbtCompound compound) {
            for (String key : List.copyOf(compound.getKeys())) {
                NbtElement child = compound.get(key);
                if (child instanceof NbtCompound childCompound && isSerializedStack(childCompound)) {
                    ItemStack nested = readStack(childCompound);
                    if (nested == null || nested.isEmpty()) {
                        removed += removeFromNbt(childCompound, tardisId, depth + 1, budget);
                        continue;
                    }

                    if (isSiegeItem(nested, tardisId)) {
                        removed += nested.getCount();
                        compound.remove(key);
                        continue;
                    }

                    int nestedRemoved = removeNested(nested, tardisId, depth + 1, budget);
                    if (nestedRemoved > 0) {
                        removed += nestedRemoved;
                        compound.put(key, writeStack(nested, childCompound));
                    }
                } else {
                    removed += removeFromNbt(child, tardisId, depth + 1, budget);
                }
            }
        } else if (element instanceof NbtList list) {
            int size = list.size();
            if (!budget.allowList(size))
                return 0;

            for (int index = 0, visited = 0; visited < size; visited++) {
                NbtElement child = list.get(index);
                if (child instanceof NbtCompound childCompound && isSerializedStack(childCompound)) {
                    ItemStack nested = readStack(childCompound);
                    if (nested == null || nested.isEmpty()) {
                        removed += removeFromNbt(childCompound, tardisId, depth + 1, budget);
                        index++;
                        continue;
                    }

                    if (isSiegeItem(nested, tardisId)) {
                        removed += nested.getCount();
                        list.remove(index);
                        continue;
                    }

                    int nestedRemoved = removeNested(nested, tardisId, depth + 1, budget);
                    if (nestedRemoved > 0) {
                        removed += nestedRemoved;
                        list.set(index, writeStack(nested, childCompound));
                    }
                } else {
                    removed += removeFromNbt(child, tardisId, depth + 1, budget);
                }
                index++;
            }
        }

        return removed;
    }

    private static boolean isSerializedStack(NbtCompound nbt) {
        return nbt.contains("id", NbtElement.STRING_TYPE)
                && nbt.contains("Count", NbtElement.NUMBER_TYPE);
    }

    @Nullable private static ItemStack readStack(NbtCompound nbt) {
        try {
            return ItemStack.fromNbt(nbt);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private static NbtCompound writeStack(ItemStack stack, NbtCompound original) {
        NbtCompound result = original.copy();
        NbtCompound serialized = stack.writeNbt(new NbtCompound());
        if (!serialized.contains("tag", NbtElement.COMPOUND_TYPE))
            result.remove("tag");
        for (String key : serialized.getKeys()) {
            NbtElement value = serialized.get(key);
            if (value != null)
                result.put(key, value.copy());
        }
        return result;
    }

    @Nullable private static UUID getSiegeTardisId(ItemStack stack) {
        if (stack == null || !stack.isOf(AITItems.SIEGE_ITEM))
            return null;

        try {
            return SiegeTardisItem.getTardisIdStatic(stack);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private static boolean isSiegeItem(ItemStack stack, UUID tardisId) {
        UUID linkedId = getSiegeTardisId(stack);
        return linkedId != null && linkedId.equals(tardisId);
    }

    private static final class ScanBudget {
        private int remaining = MAX_NBT_NODES;
        private boolean complete = true;
        private boolean matchingHint;

        private boolean visit(int depth) {
            if (depth > MAX_NBT_DEPTH || this.remaining <= 0) {
                this.complete = false;
                return false;
            }

            this.remaining--;
            return true;
        }

        private boolean allowList(int size) {
            if (size <= MAX_LIST_ENTRIES)
                return true;

            this.complete = false;
            return false;
        }

        private boolean isComplete() {
            return this.complete;
        }

        private void markMatchingHint() {
            this.matchingHint = true;
        }

        private boolean hasMatchingHint() {
            return this.matchingHint;
        }
    }
}
