package dev.amble.ait.core.blocks;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;

import dev.amble.ait.core.blockentities.PlaqueBlockEntity;
import dev.amble.ait.core.blocks.types.HorizontalDirectionalBlock;
import dev.amble.ait.core.util.ShapeUtil;

public class PlaqueBlock extends HorizontalDirectionalBlock implements BlockEntityProvider {

    protected static final VoxelShape SHAPE = Block.createCuboidShape(-0.25 * 16, 0.125 * 16, 0.875 * 16, 1.25 * 16,
            0.875 * 16, 16);

    public PlaqueBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return ShapeUtil.rotate(Direction.NORTH, state.get(FACING), SHAPE);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return ShapeUtil.rotate(Direction.NORTH, state.get(FACING), SHAPE);
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    @Nullable @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PlaqueBlockEntity(pos, state);
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockRenderView renderView, BlockPos pos, Direction side,
            @Nullable BlockState sourceState, @Nullable BlockPos sourcePos) {
        return super.getAppearance(state, renderView, pos, side, sourceState, sourcePos);
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
