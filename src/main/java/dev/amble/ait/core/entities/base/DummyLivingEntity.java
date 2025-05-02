package dev.amble.ait.core.entities.base;

import java.util.Collections;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtOps;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Arm;
import net.minecraft.world.World;

public abstract class DummyLivingEntity extends LivingEntity {

    protected static final Iterable<ItemStack> ARMOR = Collections.singleton(ItemStack.EMPTY);
    private Brain<?> brain;

    protected DummyLivingEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        NbtOps nbtOps = NbtOps.INSTANCE;
        this.brain = this.deserializeBrain(new Dynamic<>(nbtOps, nbtOps.createMap(ImmutableMap.of(nbtOps.createString("memories"), nbtOps.emptyMap()))));
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
    public Brain<?> getBrain() {
        return this.brain;
    }

    @Override
    public boolean addStatusEffect(StatusEffectInstance effect, @Nullable Entity source) {
        return false;
    }

    public static DefaultAttributeContainer.Builder createDummyAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0);
    }
}
