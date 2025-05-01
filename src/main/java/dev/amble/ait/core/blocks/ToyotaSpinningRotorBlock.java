package dev.amble.ait.core.blocks;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import dev.amble.ait.core.blockentities.ToyotaSpinningRotorBlockEntity;


@SuppressWarnings("deprecation")
public class ToyotaSpinningRotorBlock extends Block implements BlockEntityProvider {

    private static final VoxelShape CUBE = VoxelShapes.cuboid(
            0,
            0,
            0,
            1,
            1,
            1
    );


    public ToyotaSpinningRotorBlock(Settings settings) {
        super(settings);
    }


    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return CUBE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Nullable @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ToyotaSpinningRotorBlockEntity(pos, state);
    }
}
