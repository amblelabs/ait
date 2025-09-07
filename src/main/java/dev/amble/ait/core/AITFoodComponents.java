package dev.amble.ait.core;

import dev.amble.lib.datagen.util.NoEnglish;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;

public class AITFoodComponents  {
    @NoEnglish
    public static final FoodComponent FOOD_CUBE = new FoodComponent.Builder().hunger(3).saturationModifier(0.3f).snack()
            .build();

    @NoEnglish
    public static final FoodComponent OVERCHARGED_FOOD_CUBE = new FoodComponent.Builder().hunger(4).saturationModifier(0.5f).snack()
            .alwaysEdible()
            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 50, 1), 1.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 1200, 3), 1.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 3000, 0), 1.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 3000, 0), 1.0f)
            .build();
}
