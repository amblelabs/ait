package dev.amble.ait.module.planet.mixin.gravity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import dev.amble.ait.module.planet.core.space.planet.Planet;
import dev.amble.ait.module.planet.core.space.planet.PlanetRegistry;


@Mixin({BoatEntity.class})
public abstract class BoatEntityMixin extends Entity {

    public BoatEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void ait$tick(CallbackInfo ci) {
        Planet planet = PlanetRegistry.getInstance().get(this.getWorld());

        if (planet == null)
            return;

        if (!planet.hasGravityModifier())
            return;

        Vec3d movement = this.getVelocity();

        // FIXME temp fix because im lazy :))))
        this.setVelocity(movement.x, movement.y + planet.gravity(), movement.z);
    }
}
