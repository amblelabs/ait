package dev.amble.ait.core.engine.block.generic;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.engine.block.SubSystemBlock;
import dev.amble.ait.core.engine.link.block.FluidLinkBlockEntity;

public class GenericSubSystemBlock extends SubSystemBlock {

    public GenericSubSystemBlock(Settings settings) {
        super(settings, null);
    }

    @Override
    public @Nullable FluidLinkBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GenericStructureSystemBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
