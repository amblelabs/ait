package dev.amble.ait.mixin.client.experimental_screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.storage.LevelSummary;

@Mixin(value = LevelSummary.class)
public class LevelSummaryMixin {

    @Inject(method = "isExperimental", at = @At(value = "RETURN"), cancellable = true)
    public void isExperimental(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
