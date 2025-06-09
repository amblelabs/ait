package dev.amble.ait.core.blocks;

import dev.amble.ait.core.blockentities.MachineCasingBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MachineCasingBlock extends Block implements BlockEntityProvider {

    public MachineCasingBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        if (world.isClient())
            return ActionResult.SUCCESS;

        ItemStack stack = player.getStackInHand(hand);

        if (world.getBlockEntity(pos) instanceof MachineCasingBlockEntity casing)
            casing.onUse(world, stack, player);

        return ActionResult.CONSUME;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.getBlockEntity(pos) instanceof MachineCasingBlockEntity casing)
            casing.onBreak(world);

        super.onBreak(world, pos, state, player);
    }

    @Nullable @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MachineCasingBlockEntity(pos, state);
    }
}
