package dev.amble.ait.core.blockentities;



import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.AITBlockEntityTypes;

public class RoundelFabricatorBlockEntity extends BlockEntity {

    public RoundelFabricatorBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.ROUNDEL_FABRICATOR_BLOCK_ENTITY_TYPE, pos, state);
    }
    public boolean isValid() {
        if (!this.hasWorld())
            return false;

        return this.getWorld().getBlockState(this.getPos().down()).isOf(Blocks.LOOM);
    }
}
