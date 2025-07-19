package dev.amble.ait.mixin.rwf;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {

    @Accessor
    boolean getJumping();
}
