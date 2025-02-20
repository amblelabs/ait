package dev.amble.ait.mixin.compat.gravity;

import gravity_changer.EntityTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;

import dev.amble.ait.core.entities.ConsoleControlEntity;

@Mixin(EntityTags.class)
public class EntityTagsMixin {

    @Inject(method = "canChangeGravity", at = @At("RETURN"), cancellable = true)
    private static void canChangeGravity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof ConsoleControlEntity)
            cir.setReturnValue(false);
    }
}
