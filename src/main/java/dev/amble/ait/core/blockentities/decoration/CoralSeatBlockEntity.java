package dev.amble.ait.core.blockentities.decoration;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.AITBlockEntityTypes;

public class CoralSeatBlockEntity extends BlockEntity {

    public CoralSeatBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.CORAL_SEAT, pos, state);
    }

}
