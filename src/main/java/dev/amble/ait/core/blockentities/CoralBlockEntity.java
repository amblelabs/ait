package dev.amble.ait.core.blockentities;

import java.util.UUID;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.AITBlockEntityTypes;

public class CoralBlockEntity extends BlockEntity {

    public UUID creator;

    public CoralBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.CORAL_BLOCK_ENTITY_TYPE, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.creator = nbt.getUuid("creator");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.creator == null) return;
        nbt.putUuid("creator", this.creator);
    }
}
