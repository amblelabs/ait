package dev.amble.ait.core.tardis.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dev.amble.ait.core.tardis.ServerTardis;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

/** Server-authoritative, extraction-only view over a player's temporal echoes. */
public final class TemporalRecoveryInventory implements Inventory {
    private final TemporalRecoveryState state;
    private final ServerTardis tardis;
    private final ServerPlayerEntity player;
    private final List<SlotEntry> entries = new ArrayList<>(TemporalRecoveryState.CAPACITY);

    public TemporalRecoveryInventory(TemporalRecoveryState state, ServerTardis tardis,
                                     ServerPlayerEntity player) {
        this.state = state;
        this.tardis = tardis;
        this.player = player;

        List<TemporalRecoveryState.LostItem> latest = state.latest(tardis.getUuid(), player.getUuid());
        for (TemporalRecoveryState.LostItem item : latest)
            this.entries.add(new SlotEntry(item.id(), item.stack().copy()));
        while (this.entries.size() < TemporalRecoveryState.CAPACITY)
            this.entries.add(new SlotEntry(null, ItemStack.EMPTY));
    }

    @Override
    public int size() {
        return TemporalRecoveryState.CAPACITY;
    }

    @Override
    public boolean isEmpty() {
        for (SlotEntry entry : this.entries) {
            if (!entry.stack().isEmpty())
                return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return validSlot(slot) ? this.entries.get(slot).stack() : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (!validSlot(slot))
            return ItemStack.EMPTY;

        ItemStack stack = this.entries.get(slot).stack();
        // A temporal echo is a single atomic stack. This also closes partial-click
        // and drag paths which could otherwise charge once and duplicate a remainder.
        if (stack.isEmpty() || amount < stack.getCount())
            return ItemStack.EMPTY;

        return this.claim(slot);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return validSlot(slot) ? this.claim(slot) : ItemStack.EMPTY;
    }

    private ItemStack claim(int slot) {
        SlotEntry entry = this.entries.get(slot);
        if (entry.id() == null || entry.stack().isEmpty())
            return ItemStack.EMPTY;

        ItemStack claimed = this.state.claim(this.tardis, this.player, entry.id());
        if (!claimed.isEmpty())
            this.entries.set(slot, new SlotEntry(null, ItemStack.EMPTY));
        return claimed;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        // Extraction-only inventory. ScreenHandler still calls this when returning
        // a failed partial operation, so deliberately leave the authoritative slot.
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean canPlayerUse(net.minecraft.entity.player.PlayerEntity player) {
        return player == this.player && this.state.canUse(this.tardis, this.player);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return false;
    }

    @Override
    public void clear() {
    }

    private boolean validSlot(int slot) {
        return slot >= 0 && slot < this.entries.size();
    }

    private record SlotEntry(UUID id, ItemStack stack) {
    }
}
