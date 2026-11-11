package dev.amble.ait.mixin.rwf;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.LivingEntity;

import dev.amble.ait.core.entities.FlightTardisEntity;

@Mixin(InGameHud.class)
public class MountHealthMixin {

    @Inject(method = "getRiddenEntity", at = @At("HEAD"), cancellable = true)
    private void ait$hideFlightTardisHealth(CallbackInfoReturnable<LivingEntity> cir) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null && client.player.getVehicle() instanceof FlightTardisEntity)
            cir.setReturnValue(null);
    }
}
