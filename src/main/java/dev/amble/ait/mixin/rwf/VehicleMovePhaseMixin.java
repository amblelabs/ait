package dev.amble.ait.mixin.rwf;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

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

    @WrapOperation(method = "onVehicleMove", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/world/ServerWorld;isSpaceEmpty(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Z"))
    private boolean ait$trustFlightMovement(ServerWorld world, Entity entity, Box box, Operation<Boolean> original) {
        if (entity instanceof FlightTardisEntity)
            return false;

        return original.call(world, entity, box);
    }

    @WrapOperation(method = "onVehicleMove", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;isHost()Z"))
    private boolean ait$skipFlightSpeedCheck(ServerPlayNetworkHandler handler, Operation<Boolean> original) {
        return this.player.getRootVehicle() instanceof FlightTardisEntity || original.call(handler);
    }
}
