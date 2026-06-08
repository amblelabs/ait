package dev.amble.ait.core.blocks;

import net.minecraft.block.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class TardisCoralFanBlock extends Block implements Waterloggable {
    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    private static final VoxelShape UP_SHAPE = VoxelShapes.cuboid(0.125, 0, 0.125, 0.875, 0.5, 0.875);
    private static final VoxelShape DOWN_SHAPE = VoxelShapes.cuboid(0.125, 0.5, 0.125, 0.875, 1, 0.875);
    private static final VoxelShape NORTH_SHAPE = VoxelShapes.cuboid(0.125, 0.125, 0.5, 0.875, 0.875, 1);
    private static final VoxelShape SOUTH_SHAPE = VoxelShapes.cuboid(0.125, 0.125, 0, 0.875, 0.875, 0.5);
    private static final VoxelShape EAST_SHAPE = VoxelShapes.cuboid(0, 0.125, 0.125, 0.5, 0.875, 0.875);
    private static final VoxelShape WEST_SHAPE = VoxelShapes.cuboid(0.5, 0.125, 0.125, 1, 0.875, 0.875);

    public TardisCoralFanBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.UP)
                .with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(FACING);

        return switch (facing) {
            case UP -> UP_SHAPE;
            case DOWN -> DOWN_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case EAST -> EAST_SHAPE;
            case WEST -> WEST_SHAPE;
        };
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        WorldView world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction face = ctx.getSide();

        BlockPos attachedPos = pos.offset(face.getOpposite());
        BlockState attachedState = world.getBlockState(attachedPos);

        if (canPlaceOn(attachedState, face.getOpposite())) {
            return this.getDefaultState()
                    .with(FACING, face)
                    .with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
        }

        for (Direction direction : Direction.values()) {
            attachedPos = pos.offset(direction.getOpposite());
            attachedState = world.getBlockState(attachedPos);
            if (canPlaceOn(attachedState, direction.getOpposite())) {
                return this.getDefaultState()
                        .with(FACING, direction)
                        .with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
            }
        }

        return null;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction facing = state.get(FACING);
        BlockPos attachedPos = pos.offset(facing.getOpposite());
        BlockState attachedState = world.getBlockState(attachedPos);
        return canPlaceOn(attachedState, facing.getOpposite());
    }

    private boolean canPlaceOn(BlockState state, Direction direction) {
        return state.isSideSolidFullSquare(
                BlockView.class.cast(null), BlockPos.ORIGIN, direction);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
                                                BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        if (direction == state.get(FACING).getOpposite()) {
            if (!state.canPlaceAt(world, pos)) {
                return Blocks.AIR.getDefaultState();
            }
        }

        return state;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }
}