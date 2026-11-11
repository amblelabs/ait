package dev.amble.ait.mixin.rwf;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;

import dev.amble.ait.core.entities.FlightTardisEntity;

@Mixin(InGameHud.class)
public class FlightHudMixin {

    @Unique
    private static boolean ait$isFlying() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.player != null && client.player.getVehicle() instanceof FlightTardisEntity;
    }

    @Inject(method = {"renderHotbar", "renderStatusBars", "renderExperienceBar", "renderMountHealth",
            "renderMountJumpBar", "renderHeldItemTooltip"}, at = @At("HEAD"), cancellable = true)
    private void ait$hideVanillaHud(CallbackInfo ci) {
        if (ait$isFlying())
            ci.cancel();
    }
}
