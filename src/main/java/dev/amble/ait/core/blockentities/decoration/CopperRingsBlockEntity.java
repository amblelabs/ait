package dev.amble.ait.core.blockentities.decoration;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.AITBlockEntityTypes;

public class CopperRingsBlockEntity extends BlockEntity {

    public CopperRingsBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.COPPER_RINGS, pos, state);
    }

}
