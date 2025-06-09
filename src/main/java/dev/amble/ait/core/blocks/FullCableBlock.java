package dev.amble.ait.core.blocks;

import dev.amble.ait.core.engine.link.block.FluidLinkBlock;
import dev.amble.ait.core.engine.link.block.FluidLinkBlockEntity;
import dev.amble.ait.core.engine.link.block.FullCableBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class FullCableBlock extends FluidLinkBlock {

    public FullCableBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public FluidLinkBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FullCableBlockEntity(pos, state);
    }
}
