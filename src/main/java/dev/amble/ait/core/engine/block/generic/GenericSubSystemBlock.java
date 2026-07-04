package dev.amble.ait.core.engine.block.generic;

import org.jetbrains.annotations.Nullable;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

import dev.amble.ait.core.engine.block.SubSystemBlock;
import dev.amble.ait.core.engine.link.IFluidLink;
import dev.amble.ait.core.engine.link.block.FluidLinkBlockEntity;

public class GenericSubSystemBlock extends SubSystemBlock {

    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
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

    public GenericSubSystemBlock(Settings settings) {
        super(settings, null);
        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(UP, false)
                .with(DOWN, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.withConnectionProperties(ctx.getWorld(), ctx.getBlockPos());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        boolean canConnect = neighborState.isOf(this) || neighborState.getBlock() instanceof IFluidLink;
        return state.with(FACING_PROPERTIES.get(direction), canConnect);
    }

    public BlockState withConnectionProperties(BlockView world, BlockPos pos) {
        return this.getDefaultState()
                .with(DOWN, this.canConnect(world, pos.down()))
                .with(UP, this.canConnect(world, pos.up()))
                .with(NORTH, this.canConnect(world, pos.north()))
                .with(EAST, this.canConnect(world, pos.east()))
                .with(SOUTH, this.canConnect(world, pos.south()))
                .with(WEST, this.canConnect(world, pos.west()));
    }

    private boolean canConnect(BlockView world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isOf(this) || state.getBlock() instanceof IFluidLink;
    }

    @Override
    public @Nullable FluidLinkBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GenericStructureSystemBlockEntity(pos, state);
    }
}