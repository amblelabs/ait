package dev.amble.ait.core.entities.base;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import java.util.Collections;

public abstract class DummyEntity extends Entity {

    private static final Iterable<ItemStack> ARMOR = Collections.singleton(ItemStack.EMPTY);

    public DummyEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return ARMOR;
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return true;
    }

    @Override
    public boolean doesRenderOnFire() {
        return false;
    }

    @Override
    public boolean isAttackable() {
        return false;
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
