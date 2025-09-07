package dev.amble.ait.core.blocks.decoration;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.blockentities.decoration.CopperRingsBlockEntity;


public class CopperRingsBlock extends BlockWithEntity {

    public CopperRingsBlock(Settings settings) {
        super(settings);
    }

    @Nullable @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CopperRingsBlockEntity(pos, state);
    }
}
