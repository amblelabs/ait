package dev.amble.ait.module.planet.mixin.client;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;

import dev.amble.ait.module.planet.core.planet.Planet;
import dev.amble.ait.module.planet.core.planet.PlanetRegistry;

@Mixin(WorldRenderer.class)
public abstract class CloudMixin {

    @Inject(method="renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FDDD)V", at = @At("HEAD"), cancellable = true)
    private void ait$renderClouds(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.player == null)
            return;

        Planet planet = PlanetRegistry.getInstance().get(mc.player.getWorld());

        if (planet == null)
            return;

        if (!planet.hasClouds())
            ci.cancel();
    }
}
