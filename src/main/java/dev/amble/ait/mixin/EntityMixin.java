package dev.amble.ait.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;

import dev.amble.ait.api.ExtraPushableEntity;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "copyFrom", at = @At("TAIL"))
    public void copyFrom(Entity original, CallbackInfo ci) {
        if (this instanceof ExtraPushableEntity extra && original instanceof ExtraPushableEntity other)
            extra.ait$setPushBehaviour(other.ait$pushBehaviour());
    }
}
