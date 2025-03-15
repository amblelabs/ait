package dev.amble.ait.core.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.api.link.v2.block.InteriorLinkableBlockEntity;
import dev.amble.ait.core.AITBlockEntityTypes;

public class PlaqueBlockEntity extends InteriorLinkableBlockEntity {

    public PlaqueBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.PLAQUE_BLOCK_ENTITY_TYPE, pos, state);
    }
}
