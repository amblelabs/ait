package dev.amble.ait.core.engine.link.block;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.Direction;

public abstract class DirectionalFluidLinkBlock extends FluidLinkBlock {
    public DirectionalFluidLinkBlock(Settings settings) {
        super(settings);

        this.setDefaultState(this.stateManager.getDefaultState().with(HorizontalFacingBlock.FACING, Direction.NORTH));
    }

    @Nullable @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(HorizontalFacingBlock.FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HorizontalFacingBlock.FACING);
    }
}
