package dev.amble.ait.core.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import dev.amble.ait.core.AITEntityTypes;

public class RiftEntity extends Entity {
    public RiftEntity(EntityType<?> type, World world) {
        super(AITEntityTypes.RIFT_ENTITY, world);
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }
}