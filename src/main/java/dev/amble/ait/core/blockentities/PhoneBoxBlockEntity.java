package dev.amble.ait.core.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.AITBlockEntityTypes;

public class PhoneBoxBlockEntity extends BlockEntity {
    public PhoneBoxBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.PHONE_BOX_BLOCK_ENTITY_TYPE, pos, state);
    }
}
