package dev.amble.ait.core.blocks;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.core.blockentities.ConsoleGeneratorBlockEntity;
import dev.amble.ait.core.engine.link.block.FluidLinkBlock;
import dev.amble.ait.core.engine.link.block.FluidLinkBlockEntity;

public class ConsoleGeneratorBlock extends FluidLinkBlock implements BlockEntityProvider {

    public ConsoleGeneratorBlock(Settings settings) {
        super(settings);
    }

    @Nullable @Override
    public FluidLinkBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ConsoleGeneratorBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ConsoleGeneratorBlockEntity be)
            be.useOn(world, player.isSneaking(), player);

        return ActionResult.SUCCESS;
    }
}
