package dev.amble.ait.module.gun.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.MathHelper;

import dev.amble.ait.module.gun.core.item.BaseGunItem;
import dev.amble.ait.module.gun.core.item.GunItems;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Unique private boolean goBackFOV;
    @Unique private final double targetFOV = 45;
    @Unique private double currentFOV = targetFOV;

    @Inject(method = "getFov(Lnet/minecraft/client/render/Camera;FZ)D", at = @At("RETURN"), cancellable = true)
    public void ait$getFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        if (!changingFov) return;

        double d = cir.getReturnValue();
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        double newFov = setADS(d, MinecraftClient.getInstance().player);
        if (d != newFov) cir.setReturnValue(newFov);
    }

    @Unique private static boolean isADS(ClientPlayerEntity player) {
        return MinecraftClient.getInstance().options.useKey.isPressed() && MinecraftClient.getInstance().options.getPerspective().isFirstPerson() && player.getMainHandStack().getItem() instanceof BaseGunItem;
    }

    @Unique private double setADS(double fov, ClientPlayerEntity player) {
        double realTargetFOV = Math.max((player.getMainHandStack().getItem() == GunItems.CULT_STASER_RIFLE ? 10 : 30), currentFOV - (player.getMainHandStack().getItem() == GunItems.CULT_STASER_RIFLE ? 70 : targetFOV));
        if (isADS(player)) {
            float speed = player.getMainHandStack().getItem() == GunItems.CULT_STASER_RIFLE ? 0.2f : 0.4f;
            currentFOV = MathHelper.lerp(Math.min(speed * MinecraftClient.getInstance().getTickDelta(), speed), currentFOV, realTargetFOV);
            goBackFOV = true;
            return currentFOV;
        }
        else if (goBackFOV && Math.abs(currentFOV - fov) > 0.00001) {
            currentFOV = MathHelper.lerp(Math.min(0.95f * MinecraftClient.getInstance().getTickDelta(), 0.95f), currentFOV, fov);
            return currentFOV;
        } else {
            currentFOV = fov;
            goBackFOV = false;
            return fov;
        }
    }
}
