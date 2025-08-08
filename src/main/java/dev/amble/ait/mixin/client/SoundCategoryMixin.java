package dev.amble.ait.mixin.client;


import dev.amble.ait.core.AITSoundCategories;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(SoundCategory.class)
public abstract class SoundCategoryMixin {
    @Invoker("<init>")
    private static SoundCategory invokeConstructor(String name, int ordinal, String displayName) {
        throw new AssertionError();
    }

    @Shadow
    @Final
    @Mutable
    private static SoundCategory[] field_15255;

    @Inject(method = "<clinit>", at = @At(value = "FIELD",
            target = "Lnet/minecraft/sound/SoundCategory;field_15255:[Lnet/minecraft/sound/SoundCategory;",
            shift = At.Shift.AFTER))
    private static void addCustomSoundCategory(CallbackInfo ci) {
        ArrayList<SoundCategory> categories = new ArrayList<>(Arrays.asList(field_15255));
        SoundCategory hums = invokeConstructor("HUMS", categories.size(), "hums");
        categories.add(hums);
        field_15255 = categories.toArray(new SoundCategory[0]);

        AITSoundCategories.HUMS = hums;
    }
}
