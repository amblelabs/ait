package dev.amble.ait.core.blockentities.decoration;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.AITBlockEntityTypes;

public class BrassStatueBlockEntity extends BlockEntity {

    private String variant;

    public BrassStatueBlockEntity(BlockPos pos, BlockState state, String variant) {
        super(AITBlockEntityTypes.BRASS_STATUE, pos, state);
        this.variant = variant;
    }

    public String getVariant() {
        return variant;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putString("variant", variant);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        variant = nbt.getString("variant");
    }
}
