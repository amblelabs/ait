package dev.amble.ait.core.entities.base;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Arm;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DummyAmbientEntity extends AmbientEntity {

    protected DummyAmbientEntity(EntityType<? extends AmbientEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return DummyLivingEntity.ARMOR;
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
    }

    @Override
    public Arm getMainArm() {
        return Arm.LEFT;
    }

    @Override
    public boolean isPushable() {
        return false;
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

    @Nullable @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.INTENTIONALLY_EMPTY;
    }

    @Override
    public FallSounds getFallSounds() {
        return new FallSounds(SoundEvents.INTENTIONALLY_EMPTY, SoundEvents.INTENTIONALLY_EMPTY);
    }

    @Nullable @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.INTENTIONALLY_EMPTY;
    }

    @Override
    protected void playBlockFallSound() {
    }

    @Override
    public boolean addStatusEffect(StatusEffectInstance effect, @Nullable Entity source) {
        return false;
    }
}
