package dev.amble.ait.core.blockentities.control;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.AITBlockEntityTypes;

public class RedstoneControlBlockEntity extends ControlBlockEntity {

    public RedstoneControlBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.REDSTONE_CONTROL_BLOCK_ENTITY, pos, state);
    }
}
