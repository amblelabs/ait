package dev.amble.ait.mixin.server;

import dev.amble.ait.core.entities.FlightTardisEntity;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.world.TardisServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void ait$tick(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        // if player is in tardis and y is less than -100 save them
        if (player.getY() <= -100 && player.getServerWorld() instanceof TardisServerWorld tardisWorld) {
            TardisUtil.teleportInside(tardisWorld.getTardis(), player);
            player.fallDistance = 0;
        }
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    public void ait$attack(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (player.hasVehicle() && player.getVehicle() instanceof FlightTardisEntity)
            ci.cancel();
    }
}
