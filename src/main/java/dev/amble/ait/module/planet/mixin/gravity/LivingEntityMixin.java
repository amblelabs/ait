package dev.amble.ait.module.planet.mixin.gravity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import dev.amble.ait.core.AITDimensions;
import dev.amble.ait.core.AITStatusEffects;
import dev.amble.ait.core.entities.FlightTardisEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.module.planet.core.planet.Planet;
import dev.amble.ait.module.planet.core.planet.PlanetRegistry;

@Mixin(value = LivingEntity.class, priority = 1001)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    public void ait$tickMovement(CallbackInfo ci) {
        if (!this.isLogicalSideForUpdatingMovement())
            return;

        Planet planet = PlanetRegistry.getInstance().get(this.getWorld());

        if (planet == null || !planet.hasGravityModifier())
            return;

        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.isSwimming() || entity.hasNoGravity() || entity.isFallFlying() || entity.isSpectator())
            return;

        if (entity instanceof PlayerEntity player && player.getAbilities().flying)
            return;

        if (entity.getType() == EntityType.BOAT || entity.getType() == EntityType.CHEST_BOAT)
            return;

        Vec3d movement = entity.getVelocity();
        entity.setVelocity(movement.x, movement.y + planet.gravity(), movement.z);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void ait$tick(CallbackInfo ci) {
        if (this.age % 10 != 0)
            return;

        LivingEntity entity = (LivingEntity) (Object) this;
        Planet planet = PlanetRegistry.getInstance().get(this.getWorld());

        if (planet == null)
            return;

        hitDimensionThreshold(entity, 600, AITDimensions.MOON, AITDimensions.SPACE);
        hitDimensionThreshold(entity, World.OVERWORLD, 600, AITDimensions.SPACE, 256);
        hitDimensionThreshold(entity, 500, AITDimensions.MARS, AITDimensions.SPACE);

        if (entity instanceof PlayerEntity player
                && (player.isCreative() || player.isSpectator()))
            return;

        boolean oxygenated = entity.hasStatusEffect(AITStatusEffects.OXYGENATED);

        if (oxygenated)
            return;

        if (planet.isFreezing() && Planet.hasFullSuit(entity)) {
            if (entity.getType().isIn(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES))
                return;

            if (entity.getFrozenTicks() < entity.getMinFreezeDamageTicks())
                entity.setFrozenTicks(entity.getMinFreezeDamageTicks() + 20);
        }

        if (!planet.hasOxygen() && !Planet.hasOxygenInTank(entity)) {
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
        Planet planet = PlanetRegistry.getInstance().get(this.getWorld());

        if (planet != null && planet.hasNoFallDamage())
            cir.setReturnValue(false);
    }

    @Unique private static void hitDimensionThreshold(Entity entity, int tpHeight, RegistryKey<World> worldKeyA, RegistryKey<World> worldKeyB) {
        hitDimensionThreshold(entity, worldKeyA, tpHeight, worldKeyB, tpHeight);
    }

    @Unique private static void hitDimensionThreshold(Entity entity, RegistryKey<World> worldKeyA, int tpHeightA, RegistryKey<World> worldKeyB, int tpHeightB) {
        if (!(entity.getWorld() instanceof ServerWorld entityWorld))
            return;

        MinecraftServer server = entityWorld.getServer();
        int y = entity.getBlockY();

        ServerWorld worldA = server.getWorld(worldKeyA);
        ServerWorld worldB = server.getWorld(worldKeyB);

        if (entity.hasVehicle()) {
            if (entity instanceof PlayerEntity player) {
                Entity tardisEntity = player.getVehicle();
                if (tardisEntity instanceof FlightTardisEntity flightTardis) {
                    Tardis tardis = flightTardis.tardis().get();
                    if (flightTardis.tardis() == null) return;
                    if (y >= tpHeightA && entityWorld == worldA) {
                        moveToWorldWithPassenger(flightTardis, tardis, player, worldB);
                    }/* else if (y >= tpHeightB && entityWorld == worldB) {
                        moveToWorldWithPassenger(flightTardis, tardis, player, worldA);
                    }*/
                    return;
                }
            }
        }

        if (y >= tpHeightA && entityWorld == worldA) {
            entity.moveToWorld(worldB);
        }/* else if (y >= tpHeightB && entityWorld == worldB) {
            entity.moveToWorld(worldA);
        }*/
    }

    @Unique private static void moveToWorldWithPassenger(FlightTardisEntity tardisEntity, Tardis tardis, PlayerEntity player, ServerWorld b) {
        player.stopRiding();
        tardis.flight().flying().set(false);
        player.moveToWorld(b);
        tardisEntity.moveToWorld(b);
        tardis.flight().flying().set(true);
        player.startRiding(tardisEntity);
    }
}
