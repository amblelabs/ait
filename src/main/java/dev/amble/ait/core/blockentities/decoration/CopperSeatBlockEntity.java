package dev.amble.ait.core.blockentities.decoration;

import dev.amble.ait.core.AITBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class CopperSeatBlockEntity extends BlockEntity {

    public CopperSeatBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.COPPER_SEAT, pos, state);
    }

}
