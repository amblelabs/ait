package dev.amble.ait.mixin.compat.portals;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.teleportation.ServerTeleportationManager;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.core.world.TardisServerWorld;

@Mixin(ServerTeleportationManager.class)
public class ServerTeleportationManagerMixin {

    @Inject(method = "changePlayerDimension", at = @At("TAIL"))
    private static void onTeleported(ServerPlayerEntity player, ServerWorld fromWorld, ServerWorld toWorld, Vec3d newEyePos, CallbackInfo ci) {
        if (fromWorld instanceof TardisServerWorld tsw)
            TardisEvents.LEAVE_TARDIS.invoker().onLeave(tsw.getTardis(), player);

        if (toWorld instanceof TardisServerWorld tsw)
            TardisEvents.ENTER_TARDIS.invoker().onEnter(tsw.getTardis(), player);
    }
}
