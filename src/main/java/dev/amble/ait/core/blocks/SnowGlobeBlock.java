package dev.amble.ait.core.blocks;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.blockentities.SnowGlobeBlockEntity;
import dev.amble.ait.core.blocks.types.HorizontalDirectionalBlock;
public class SnowGlobeBlock extends HorizontalDirectionalBlock implements BlockEntityProvider {

    public SnowGlobeBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Nullable @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SnowGlobeBlockEntity(pos, state);
    }
}
