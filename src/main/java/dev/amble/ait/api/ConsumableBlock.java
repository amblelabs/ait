package dev.amble.ait.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * Implement this in a Block to allow external systems (like hoppers)
 * to insert items directly into the block, without needing a block entity.
 */
public interface ConsumableBlock {

    /**
     * Returns true if the block can accept the item stack from the specified direction.
     */
    default boolean canAcceptItem(World world, BlockPos pos, ItemStack stack, Direction from) {
        return true;
    }

    /**
     * Attempts to insert the given item into the block from the given direction.
     *
     * @param world World context
     * @param pos Block position
     * @param stack Stack to insert
     * @param from Direction the item is inserted from
     * @param simulate If true, the insertion should only be simulated
     * @return The remaining stack (not inserted), or ItemStack.EMPTY if fully consumed
     */
    ItemStack insertItem(World world, BlockPos pos, ItemStack stack, Direction from, boolean simulate);
}
