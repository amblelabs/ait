package dev.amble.ait.core.blocks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

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

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull World world, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        return (world1, blockPos, blockState, ticker) -> {
            if (ticker instanceof ToyotaSpinningRotorBlockEntity toyotaSpinningRotorBlockEntity) {
                toyotaSpinningRotorBlockEntity.tick(world, blockPos, blockState, toyotaSpinningRotorBlockEntity);
            }
        };
    }

    @Nullable @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ToyotaSpinningRotorBlockEntity(pos, state);
    }
}
