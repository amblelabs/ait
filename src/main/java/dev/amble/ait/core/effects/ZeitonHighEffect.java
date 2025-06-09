package dev.amble.ait.core.effects;

import dev.amble.ait.core.AITStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class ZeitonHighEffect extends StatusEffect {
    public ZeitonHighEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x8fbaff);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    public static boolean isHigh(LivingEntity entity) {
        return entity.hasStatusEffect(AITStatusEffects.ZEITON_HIGH);
    }
}
