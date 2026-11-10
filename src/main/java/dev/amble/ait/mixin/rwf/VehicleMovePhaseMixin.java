package dev.amble.ait.mixin.rwf;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

import dev.amble.ait.core.entities.FlightTardisEntity;

@Mixin(ServerPlayNetworkHandler.class)
public class VehicleMovePhaseMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Shadow
    private boolean isHost() {
        throw new AssertionError();
    }

    @Redirect(method = "onVehicleMove", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/world/ServerWorld;isSpaceEmpty(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Z"))
    private boolean ait$trustFlightMovement(ServerWorld world, Entity entity, Box box) {
        if (entity instanceof FlightTardisEntity)
            return false;

        return world.isSpaceEmpty(entity, box);
    }

    @Redirect(method = "onVehicleMove", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;isHost()Z"))
    private boolean ait$skipFlightSpeedCheck(ServerPlayNetworkHandler handler) {
        return this.player.getRootVehicle() instanceof FlightTardisEntity || this.isHost();
    }
}
