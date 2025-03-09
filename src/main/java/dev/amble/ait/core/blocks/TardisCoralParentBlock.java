package dev.amble.ait.core.blocks;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class TardisCoralParentBlock
        extends Block
        implements Waterloggable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);

    public TardisCoralParentBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(WATERLOGGED, true));
    }

    @Nullable protected static boolean isInWater(BlockState state, BlockView world, BlockPos pos) {
        if ((Boolean)state.get(WATERLOGGED)) {
            return true;
        } else {
            Direction[] var3 = Direction.values();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Direction direction = var3[var5];
                if (world.getFluidState(pos.offset(direction)).isIn(FluidTags.WATER)) {
                    return true;
                }
            }

            return false;
        }
    }

    @Nullable public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return (BlockState)this.getDefaultState().with(WATERLOGGED, fluidState.isIn(FluidTags.WATER) && fluidState.getLevel() == 8);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.get(WATERLOGGED)) {
            world.getFluidTickScheduler().isTicking(pos, Fluids.WATER);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {

        if (!this.canPlaceAt(state, world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }



    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction.getOpposite());
            if (world.getBlockState(neighborPos).isSideSolidFullSquare(world, neighborPos, direction)) {
                return true;
            }
        }
        return false;
    }


    @Nullable protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{WATERLOGGED});
    }

    @Nullable public FluidState getFluidState(BlockState state) {
        return (Boolean)state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }
}