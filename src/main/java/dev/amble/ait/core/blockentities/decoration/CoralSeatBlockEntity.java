package dev.amble.ait.core.blockentities.decoration;

import dev.amble.ait.core.AITBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class CoralSeatBlockEntity extends BlockEntity {

    public CoralSeatBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.CORAL_SEAT, pos, state);
    }

}
