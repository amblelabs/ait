package dev.amble.ait.core.blockentities.decoration;

import dev.amble.ait.core.AITBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ToyotaSeatBlockEntity extends BlockEntity {

    public ToyotaSeatBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.TOYOTA_SEAT, pos, state);
    }

}
