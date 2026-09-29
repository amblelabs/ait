package dev.amble.ait.core.tardis.manager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;

/** A 9x6 container which only permits taking a complete temporal stack. */
public final class TemporalRecoveryScreenHandler extends GenericContainerScreenHandler {
    public TemporalRecoveryScreenHandler(int syncId, PlayerInventory playerInventory,
                                         TemporalRecoveryInventory inventory) {
        super(ScreenHandlerType.GENERIC_9X6, syncId, playerInventory, inventory, 6);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex >= 0 && slotIndex < TemporalRecoveryState.CAPACITY
                && (actionType != SlotActionType.PICKUP || button != 0))
            return;

        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        // Requiring an ordinary left click keeps extraction atomic: vanilla first
        // reserves the whole stack on the cursor, with no lossy inventory fallback.
        return ItemStack.EMPTY;
    }
}
