package dev.amble.ait.core.blocks;

import net.minecraft.block.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import dev.amble.ait.core.AITBlocks;

public class BuddingZeitonBlock extends AmethystBlock {
    public static final int GROW_CHANCE = 5;
    private static final Direction[] DIRECTIONS = Direction.values();

    public BuddingZeitonBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (random.nextInt(5) == 0) {
            Direction direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            Block block = null;
            if (canGrowIn(blockState)) {
                block = AITBlocks.SMALL_ZEITON_BUD;
            } else if (blockState.isOf(AITBlocks.SMALL_ZEITON_BUD)
                    && blockState.get(AmethystClusterBlock.FACING) == direction) {
                block = AITBlocks.MEDIUM_ZEITON_BUD;
            } else if (blockState.isOf(AITBlocks.MEDIUM_ZEITON_BUD)
                    && blockState.get(AmethystClusterBlock.FACING) == direction) {
                block = AITBlocks.LARGE_ZEITON_BUD;
            } else if (blockState.isOf(AITBlocks.LARGE_ZEITON_BUD)
                    && blockState.get(AmethystClusterBlock.FACING) == direction) {
                block = AITBlocks.ZEITON_CLUSTER;
            }

            if (block != null) {
                BlockState blockState2 = block.getDefaultState().with(AmethystClusterBlock.FACING, direction)
                        .with(AmethystClusterBlock.WATERLOGGED, blockState.getFluidState().getFluid() == Fluids.WATER);
                world.setBlockState(blockPos, blockState2);
            }
        }
    }

    public static boolean canGrowIn(BlockState state) {
        return state.isAir() || state.isOf(Blocks.WATER) && state.getFluidState().getLevel() == 8;
    }
}
