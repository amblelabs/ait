package dev.amble.ait.core.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import dev.amble.ait.core.engine.link.IFluidLink;
import dev.amble.ait.core.engine.link.block.CableBlockEntity;
import dev.amble.ait.core.engine.link.block.FluidLinkBlock;
import dev.amble.ait.core.engine.link.block.FluidLinkBlockEntity;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

import java.util.Map;

public class CableBlock extends FluidLinkBlock implements Waterloggable {

    private static final Direction[] FACINGS = Direction.values();
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final BooleanProperty WEST = Properties.WEST;
    public static final BooleanProperty UP = Properties.UP;
    public static final BooleanProperty DOWN = Properties.DOWN;
    public static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ImmutableMap.copyOf(Util.make(Maps.newEnumMap(Direction.class), directions -> {
        directions.put(Direction.NORTH, NORTH);
        directions.put(Direction.EAST, EAST);
        directions.put(Direction.SOUTH, SOUTH);
        directions.put(Direction.WEST, WEST);
        directions.put(Direction.UP, UP);
        directions.put(Direction.DOWN, DOWN);
    }));
    protected final VoxelShape[] connectionsToShape;

    public CableBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(WATERLOGGED, false).with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(UP, false).with(DOWN, false));
        this.connectionsToShape = this.generateFacingsToShapeMap(0.2f);
    }

    private VoxelShape[] generateFacingsToShapeMap(float radius) {
        float f = 0.5f - radius;
        float g = 0.5f + radius;
        VoxelShape voxelShape = Block.createCuboidShape(f * 16.0f, f * 16.0f, f * 16.0f, g * 16.0f, g * 16.0f, g * 16.0f);
        VoxelShape[] voxelShapes = new VoxelShape[FACINGS.length];
        for (int i = 0; i < FACINGS.length; ++i) {
            Direction direction = FACINGS[i];
            voxelShapes[i] = VoxelShapes.cuboid(0.5 + Math.min(-(float) radius, (double)direction.getOffsetX() * 0.5), 0.5 + Math.min(-(float) radius, (double)direction.getOffsetY() * 0.5), 0.5 + Math.min(-(float) radius, (double)direction.getOffsetZ() * 0.5), 0.5 + Math.max(radius, (double)direction.getOffsetX() * 0.5), 0.5 + Math.max(radius, (double)direction.getOffsetY() * 0.5), 0.5 + Math.max(radius, (double)direction.getOffsetZ() * 0.5));
        }
        VoxelShape[] voxelShapes2 = new VoxelShape[64];
        for (int j = 0; j < 64; ++j) {
            VoxelShape voxelShape2 = voxelShape;
            for (int k = 0; k < FACINGS.length; ++k) {
                if ((j & 1 << k) == 0) continue;
                voxelShape2 = VoxelShapes.union(voxelShape2, voxelShapes[k]);
            }
            voxelShapes2[j] = voxelShape2;
        }
        return voxelShapes2;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        }
        if (state.get(WATERLOGGED))
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        boolean bl = neighborState.isOf(this) || neighborState.getBlock() instanceof IFluidLink;
        return state.with(FACING_PROPERTIES.get(direction), bl);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.connectionsToShape[this.getConnectionMask(state)];
    }

    protected int getConnectionMask(BlockState state) {
        int i = 0;
        for (int j = 0; j < FACINGS.length; ++j) {
            if (!state.get(FACING_PROPERTIES.get(FACINGS[j])))
                continue;

            i |= 1 << j;
        }

        return i;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return this.withConnectionProperties(ctx.getWorld(), ctx.getBlockPos())
                .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    public BlockState withConnectionProperties(BlockView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos.down());
        BlockState blockState2 = world.getBlockState(pos.up());
        BlockState blockState3 = world.getBlockState(pos.north());
        BlockState blockState4 = world.getBlockState(pos.east());
        BlockState blockState5 = world.getBlockState(pos.south());
        BlockState blockState6 = world.getBlockState(pos.west());
        return this.getDefaultState()
                .with(DOWN, blockState.isOf(this) || blockState.getBlock() instanceof IFluidLink)
                .with(UP, blockState2.isOf(this) || blockState2.getBlock() instanceof IFluidLink)
                .with(NORTH, blockState3.isOf(this) || blockState3.getBlock() instanceof IFluidLink)
                .with(EAST, blockState4.isOf(this) || blockState4.getBlock() instanceof IFluidLink)
                .with(SOUTH, blockState5.isOf(this) || blockState5.getBlock() instanceof IFluidLink)
                .with(WEST, blockState6.isOf(this) || blockState6.getBlock() instanceof IFluidLink);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN).add(WATERLOGGED);
    }

    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return !(Boolean) state.get(WATERLOGGED);
    }

    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public FluidLinkBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CableBlockEntity(pos, state);
    }
}