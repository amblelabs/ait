package dev.amble.ait.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.network.ServerPlayerEntity;

import dev.amble.ait.core.entities.FlightTardisEntity;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.control.impl.SecurityControl;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.tardis.util.network.BOTIUpdateTracker;
import dev.amble.ait.core.world.TardisServerWorld;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void ait$tick(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        // if player is in tardis and y is less than -100 save them
        // if leave-behind is on, and they do not have a key + enough loyalty, then evict them instead
        if (player.getY() <= -100 && player.getServerWorld() instanceof TardisServerWorld tardisWorld) {
            ServerTardis serverTardis = tardisWorld.getTardis();

            if (!SecurityControl.hasMatchingKey(player, serverTardis) && serverTardis.travel().leaveBehind().get())
                TardisUtil.teleportOutside(serverTardis, player);
            else
                TardisUtil.teleportInside(serverTardis, player);

            player.fallDistance = 0;
        }
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    public void ait$attack(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (player.hasVehicle() && player.getVehicle() instanceof FlightTardisEntity)
            ci.cancel();
    }

    /**
     * Clean up BOTI viewer registrations when player disconnects.
     * Prevents memory leaks by removing disconnected players from BOTIUpdateTracker.
     */
    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void ait$onDisconnect(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        BOTIUpdateTracker.unregisterAll(player);
    }
}
