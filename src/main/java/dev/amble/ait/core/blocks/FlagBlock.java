package dev.amble.ait.core.blocks;

import dev.amble.ait.core.blockentities.FlagBlockEntity;
import dev.amble.ait.core.blocks.types.HorizontalDirectionalBlock;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class FlagBlock extends HorizontalDirectionalBlock implements BlockEntityProvider {

    private static final VoxelShape POLE = VoxelShapes.cuboid(
            0.45,
            0,
            0.45,
            0.55,
            2,
            0.55
    );


    public FlagBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return POLE;
    }
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Nullable @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FlagBlockEntity(pos, state);
    }
}
