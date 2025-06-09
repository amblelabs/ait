package dev.amble.ait.core.item;

import dev.amble.ait.core.AITBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

public class ZeitonShardItem extends Item {
    public ZeitonShardItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState state = context.getWorld().getBlockState(context.getBlockPos());

        if (state.isOf(Blocks.COBBLESTONE)) {
            context.getWorld().setBlockState(context.getBlockPos(), AITBlocks.ZEITON_COBBLE.getDefaultState());
            context.getStack().decrement(1);
            return ActionResult.SUCCESS;
        }

        return super.useOnBlock(context);
    }
}
