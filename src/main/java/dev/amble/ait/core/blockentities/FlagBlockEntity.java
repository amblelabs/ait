package dev.amble.ait.core.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.AITBlockEntityTypes;

public class FlagBlockEntity extends BlockEntity {
    public FlagBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.FLAG_BLOCK_ENTITY_TYPE, pos, state);
    }
}
