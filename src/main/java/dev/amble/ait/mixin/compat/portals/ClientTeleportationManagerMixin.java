package dev.amble.ait.mixin.compat.portals;

import dev.amble.ait.api.ClientWorldEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.teleportation.ClientTeleportationManager;

@Mixin(ClientTeleportationManager.class)
public class ClientTeleportationManagerMixin {

    @Shadow
    @Final
    public static MinecraftClient client;

    @Inject(method = "changePlayerDimension", at = @At("TAIL"))
    private static void onTeleported(ClientPlayerEntity player, ClientWorld fromWorld, ClientWorld toWorld, Vec3d newEyePos, CallbackInfo ci) {
        ClientWorldEvents.CHANGE_WORLD.invoker().onChange(client, toWorld);
    }
}
