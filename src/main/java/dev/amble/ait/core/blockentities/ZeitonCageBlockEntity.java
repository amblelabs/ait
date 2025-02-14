package dev.amble.ait.core.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.AITBlockEntityTypes;

public class ZeitonCageBlockEntity extends BlockEntity {

    public ZeitonCageBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.ZEITON_CAGE_BLOCK_ENTITY_TYPE, pos, state);
    }

}
