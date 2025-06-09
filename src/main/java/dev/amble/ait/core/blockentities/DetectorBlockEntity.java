package dev.amble.ait.core.blockentities;

import dev.amble.ait.api.tardis.link.v2.block.InteriorLinkableBlockEntity;
import dev.amble.ait.core.AITBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class DetectorBlockEntity extends InteriorLinkableBlockEntity {

    public DetectorBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.DETECTOR_BLOCK_ENTITY_TYPE, pos, state);
    }
}
