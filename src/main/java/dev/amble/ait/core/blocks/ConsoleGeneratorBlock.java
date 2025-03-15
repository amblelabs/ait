package dev.amble.ait.core.blocks;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.core.blockentities.ConsoleGeneratorBlockEntity;
import dev.amble.ait.core.blocks.types.HorizontalDirectionalBlock;

public class ConsoleGeneratorBlock extends HorizontalDirectionalBlock implements BlockEntityProvider {

    public ConsoleGeneratorBlock(Settings settings) {
        super(settings);
    }

    @Nullable @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ConsoleGeneratorBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
            ItemStack itemStack) {

        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ConsoleGeneratorBlockEntity consoleGeneratorBlockEntity)
            consoleGeneratorBlockEntity.useOn(world, player.isSneaking(), player);
        return ActionResult.SUCCESS;
    }
}
