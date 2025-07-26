package dev.amble.ait.module.planet.mixin.gravity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import dev.amble.ait.core.AITDimensions;
import dev.amble.ait.core.AITStatusEffects;
import dev.amble.ait.core.entities.FlightTardisEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.module.planet.core.space.planet.Planet;
import dev.amble.ait.module.planet.core.space.planet.PlanetRegistry;
import dev.amble.ait.module.planet.core.util.ISpaceImmune;

@Mixin(value = LivingEntity.class, priority = 1001)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    /*@Inject(method = "tickFallFlying", at = @At("HEAD"), cancellable = true)
    public void ait$tickFallFlying(CallbackInfo ci) {
        if (!this.isLogicalSideForUpdatingMovement())
            return;

        Planet planet = PlanetRegistry.getInstance().get(this.getWorld());

        if (planet == null || !planet.hasGravityModifier())
            return;

        *//*LivingEntity entity = (LivingEntity) (Object) this;*//*
        // todo make this better its a yikes
        if (planet.gravity() > 0) {
            ci.cancel();
        }
    }*/

    @Inject(method = "tickMovement", at = @At("TAIL"))
    public void ait$tickMovement(CallbackInfo ci) {
        if (!this.isLogicalSideForUpdatingMovement())
            return;

        Planet planet = PlanetRegistry.getInstance().get(this.getWorld());

        if (planet == null || !planet.hasGravityModifier())
            return;

        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.isSwimming() || entity.hasNoGravity()/* || entity.isFallFlying()*/ || entity.isSpectator())
            return;

        if (entity instanceof PlayerEntity player && player.getAbilities().flying)
            return;

        if (entity.getType() == EntityType.BOAT || entity.getType() == EntityType.CHEST_BOAT)
            return;

        boolean oxygenated = entity.hasStatusEffect(AITStatusEffects.OXYGENATED);

        if (oxygenated)
            return;

        Vec3d movement = entity.getVelocity();
        entity.setVelocity(movement.x, movement.y + planet.gravity(), movement.z);
    }

    @Inject(method = "tickInVoid", at = @At("HEAD"), cancellable = true)
    public void ait$tickInVoid(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof FlightTardisEntity)
            ci.cancel();
        if (entity.getWorld().getRegistryKey().equals(AITDimensions.SPACE))
            ci.cancel();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void ait$tick(CallbackInfo ci) {
        if (this.age % 10 != 0)
            return;

        LivingEntity entity = (LivingEntity) (Object) this;
        Planet planet = PlanetRegistry.getInstance().get(this.getWorld());

        if (planet == null)
            return;


        if (entity instanceof PlayerEntity player
                && player.isSpectator())
            return;

        boolean oxygenated = entity.hasStatusEffect(AITStatusEffects.OXYGENATED);

        if (oxygenated)
            return;

        if (entity instanceof ISpaceImmune)
            return;

        if (planet.isFreezing() && !Planet.hasFullSuit(entity)) {
            if (entity.getType().isIn(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES))
                return;

            if (entity.getFrozenTicks() < entity.getMinFreezeDamageTicks())
                entity.setFrozenTicks(entity.getMinFreezeDamageTicks() + 20);
        }

        if (!planet.hasOxygen() && !Planet.hasFullSuit(entity) && !Planet.hasOxygenInTank(entity)) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA,
                    200, 1, false, false));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 1,
                    200, false, false));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS,
                    200, 1, false, false));
        }
    }

    @Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
    private void ait$handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        World world = this.getWorld();
        Planet planet = PlanetRegistry.getInstance().get(world);

        if (planet != null) {
            if (planet.hasNoFallDamage())
                cir.setReturnValue(false);
            return;
        }

        // Prevent fall damage in TARDIS for loyalty OWNER (and working life support)
        LivingEntity entity = (LivingEntity)(Object) this;
        if (world instanceof TardisServerWorld tardisWorld
                && entity instanceof PlayerEntity player) {

            Tardis tardis = tardisWorld.getTardis();
            boolean hasLifeSupport = tardis.subsystems().lifeSupport().isUsable();

            if (hasLifeSupport && tardis.loyalty().get(player).isOf(Loyalty.Type.OWNER))
                cir.setReturnValue(false);
        }
    }
}
