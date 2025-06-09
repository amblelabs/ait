package dev.amble.ait.mixin.client.rendering;

import dev.amble.ait.core.entities.FlightTardisEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "doesRenderOnFire", at = @At("HEAD"), cancellable = true)
    public void ait$doesRenderOnFire(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (!entity.hasVehicle()) return;

        if (entity.getVehicle() instanceof FlightTardisEntity) {
            cir.setReturnValue(false);
        }
    }
}
