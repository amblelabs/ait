package dev.amble.ait.mixin.server;

import dev.amble.ait.core.engine.impl.HomeEntityCapture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin {

    @Inject(method = "spawn", at = @At("HEAD"), cancellable = true)
    private static void ait$captureHomeExperience(ServerWorld world, Vec3d position, int amount,
                                                   CallbackInfo ci) {
        if (HomeEntityCapture.tryCaptureExperience(world, position, amount))
            ci.cancel();
    }
}
