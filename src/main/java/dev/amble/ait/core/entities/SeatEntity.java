package dev.amble.ait.core.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class SeatEntity extends Entity {

    public SeatEntity(EntityType<? extends SeatEntity> type, World world) {
        super(type, world);
        this.noClip = true;
    }

    @Override
    protected void initDataTracker() {}

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {}

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {}

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            if (this.getPassengerList().isEmpty()) {
                this.discard();
            }
        }
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public boolean shouldRender(double distance) {
        return false;
    }



    @Override
    public boolean isInvisible() {
        return true;
    }
}
