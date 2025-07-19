package dev.amble.ait.core.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

import dev.amble.ait.core.AITStatusEffects;

public class OxygenatedEffect extends StatusEffect {
    public OxygenatedEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x8fbaff);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    public static boolean isOxygenated(LivingEntity entity) {
        return entity.hasStatusEffect(AITStatusEffects.OXYGENATED);
    }
}
