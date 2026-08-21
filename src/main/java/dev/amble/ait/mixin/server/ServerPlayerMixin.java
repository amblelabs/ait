package dev.amble.ait.mixin.server;

import dev.amble.ait.api.TeleportAware;
import dev.amble.ait.core.entities.FlightTardisEntity;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.control.impl.SecurityControl;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.world.TardisServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerMixin implements TeleportAware {

    @Unique private int ait$teleportEpoch;

    @Override
    public int ait$getTeleportEpoch() {
        return this.ait$teleportEpoch;
    }

    @Override
    public void ait$markTeleported() {
        this.ait$teleportEpoch++;
    }

    @Inject(method = "requestTeleportAndDismount", at = @At("HEAD"))
    private void ait$markTeleportAndDismount(double x, double y, double z, CallbackInfo ci) {
        this.ait$markTeleported();
    }

    @Inject(method = "refreshPositionAfterTeleport", at = @At("HEAD"))
    private void ait$markRefreshAfterTeleport(double x, double y, double z, CallbackInfo ci) {
        this.ait$markTeleported();
    }

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
}
