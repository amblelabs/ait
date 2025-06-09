package dev.amble.ait.core.engine.link.block;

import dev.amble.ait.core.AITBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class FullCableBlockEntity extends FluidLinkBlockEntity{

    public FullCableBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.FULL_CABLE_BLOCK_ENTITY_TYPE, pos, state);
    }
}
