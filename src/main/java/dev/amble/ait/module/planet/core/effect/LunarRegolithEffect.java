package dev.amble.ait.module.planet.core.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class LunarRegolithEffect extends StatusEffect {

    public LunarRegolithEffect() {
        super(StatusEffectCategory.HARMFUL, 0xF5F5F5); // Customize color
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, amplifier, false, false));
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 100, amplifier, false, false));

        int lunarDuration = entity.getStatusEffect(this).getDuration();

        int delayBeforeEffect = lunarDuration - 1000;

        if (delayBeforeEffect <= 0) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 6, false, false));

        }
    }
}
