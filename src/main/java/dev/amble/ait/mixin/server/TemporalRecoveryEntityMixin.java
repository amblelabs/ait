package dev.amble.ait.mixin.server;

import dev.amble.ait.api.TemporalItemOwnership;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;

@Mixin(Entity.class)
public abstract class TemporalRecoveryEntityMixin {

    @Inject(method = "setRemoved", at = @At("HEAD"))
    private void ait$recordDestroyedTemporalItem(Entity.RemovalReason reason, CallbackInfo ci) {
        if (reason == Entity.RemovalReason.KILLED && (Object) this instanceof ItemEntity item
                && !item.getStack().isEmpty())
            ((TemporalItemOwnership) item).ait$recordTemporalDestruction();
    }
}
