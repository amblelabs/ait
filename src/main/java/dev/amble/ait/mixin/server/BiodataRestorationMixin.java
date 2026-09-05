package dev.amble.ait.mixin.server;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.manager.BiodataRestorationManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

/** Places Biodata Restoration immediately before or after ordinary totems. */
@Mixin(value = LivingEntity.class, priority = 900)
public abstract class BiodataRestorationMixin {

    @WrapOperation(method = "damage", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;tryUseTotem(Lnet/minecraft/entity/damage/DamageSource;)Z"))
    private boolean ait$resolveBiodataRestoration(LivingEntity entity, DamageSource source,
                                                   Operation<Boolean> original) {
        if (!(entity instanceof ServerPlayerEntity player))
            return original.call(entity, source);

        if (AITMod.CONFIG.preferTotemsOverBiodataRestoration) {
            if (original.call(entity, source))
                return true;
            return BiodataRestorationManager.tryRescue(player);
        }

        if (BiodataRestorationManager.tryRescue(player))
            return true;
        return original.call(entity, source);
    }
}
