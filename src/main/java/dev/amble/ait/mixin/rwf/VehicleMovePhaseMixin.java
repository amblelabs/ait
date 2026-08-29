package dev.amble.ait.mixin.rwf;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

import dev.amble.ait.core.entities.FlightTardisEntity;

@Mixin(ServerPlayNetworkHandler.class)
public class VehicleMovePhaseMixin {

    @Redirect(method = "onVehicleMove", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/world/ServerWorld;isSpaceEmpty(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Z"))
    private boolean ait$allowPhaseThroughBlocks(ServerWorld world, Entity entity, Box box) {
        if (entity instanceof FlightTardisEntity flight && flight.isPhasing())
            return true;

        return world.isSpaceEmpty(entity, box);
    }
}
