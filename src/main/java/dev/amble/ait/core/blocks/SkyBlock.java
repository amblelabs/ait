package dev.amble.ait.core.blocks;

import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.blockentities.SkyBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class SkyBlock extends Block implements BlockEntityProvider {
	public SkyBlock(Settings settings) {
		super(settings);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		if (world.getBlockEntity(pos) instanceof SkyBlockEntity be) {
			be.onNeighbourChange();
		}

		super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (world.getBlockEntity(pos) instanceof SkyBlockEntity be) {
			be.onNeighbourChange();
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return AITBlockEntityTypes.SKY_BLOCK.instantiate(pos, state);
	}
}
