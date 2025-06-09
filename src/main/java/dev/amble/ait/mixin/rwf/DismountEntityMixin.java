package dev.amble.ait.mixin.rwf;

import dev.amble.ait.core.AITTags;
import dev.amble.ait.core.entities.FlightTardisEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class DismountEntityMixin extends Entity {

    public DismountEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "shouldDismount", at = @At("HEAD"), cancellable = true)
    private void ait$shouldDismount(CallbackInfoReturnable<Boolean> cir) {
        if (this.getVehicle() == null)
            return;

        if (this.getVehicle() instanceof FlightTardisEntity || this.getVehicle().getType().isIn(AITTags.EntityTypes.NON_DISMOUNTABLE))
            cir.setReturnValue(false);
    }
}