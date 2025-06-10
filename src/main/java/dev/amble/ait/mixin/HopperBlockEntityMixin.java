package dev.amble.ait.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import dev.amble.ait.api.ConsumableBlock;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {

    @Inject(method = "insert(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/inventory/Inventory;)Z",
            at = @At("HEAD"), cancellable = true)
    private static void ait$insertIntoConsumableBlock(
            World world, BlockPos pos, BlockState state, Inventory inventory, CallbackInfoReturnable<Boolean> cir
    ) {
        Direction direction = state.get(HopperBlock.FACING);
        BlockPos targetPos = pos.offset(direction);
        BlockState targetState = world.getBlockState(targetPos);

        if (!(targetState.getBlock() instanceof ConsumableBlock block)) return;

        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (stack.isEmpty()) continue;

            if (!block.canAcceptItem(world, targetPos, stack, direction.getOpposite())) continue;

            ItemStack attempt = stack.copyWithCount(1);
            ItemStack leftover = block.insertItem(world, targetPos, attempt, direction.getOpposite(), false);

            if (leftover.isEmpty()) {
                stack.decrement(1);
                cir.setReturnValue(true);
                return;
            }
        }
    }

}
