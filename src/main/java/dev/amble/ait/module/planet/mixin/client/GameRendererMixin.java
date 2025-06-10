package dev.amble.ait.module.planet.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;

import dev.amble.ait.core.entities.FlightTardisEntity;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.module.planet.core.space.planet.Planet;
import dev.amble.ait.module.planet.core.space.planet.PlanetRegistry;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "getFarPlaneDistance", at = @At("HEAD"), cancellable = true)
    private void ait$getFarPlaneDistance(CallbackInfoReturnable<Float> cir) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;
        boolean ifTardisWorld = TardisServerWorld.isTardisDimension(mc.world);
        Planet planet = PlanetRegistry.getInstance().get(mc.world);
        if (planet != null || ifTardisWorld) {
            cir.setReturnValue((64.0F * 16.0F) * 64.0F);
        }
    }

    @Inject(method = "renderNausea", at = @At("HEAD"), cancellable = true)
    private void ait$renderNausea(DrawContext context, float distortionStrength, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || !mc.player.hasVehicle()) return;
        if (mc.player.getVehicle() instanceof FlightTardisEntity)
            ci.cancel();
    }
}
