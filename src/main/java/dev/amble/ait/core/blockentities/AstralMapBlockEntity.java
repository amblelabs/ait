package dev.amble.ait.core.blockentities;

import dev.amble.ait.core.AITBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class AstralMapBlockEntity extends BlockEntity {

    public AstralMapBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.ASTRAL_MAP, pos, state);
    }
}
