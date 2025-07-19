package dev.amble.ait.core.engine.link.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.AITBlockEntityTypes;

public class CableBlockEntity extends FluidLinkBlockEntity {

    public CableBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.CABLE_BLOCK_ENTITY_TYPE, pos, state);
    }
}
