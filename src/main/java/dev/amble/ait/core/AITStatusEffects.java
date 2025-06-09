package dev.amble.ait.core;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.effects.OxygenatedEffect;
import dev.amble.ait.core.effects.ZeitonHighEffect;
import dev.amble.ait.module.planet.core.effect.LunarRegolithEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class AITStatusEffects {
    public static StatusEffect ZEITON_HIGH = register(new ZeitonHighEffect(), "zeiton_high");

    public static StatusEffect OXYGENATED = register(new OxygenatedEffect(), "oxygenated");
    public static StatusEffect LUNAR_SICKNESS = register(new LunarRegolithEffect(), "lunar_sickness");

    public static void init() {
    }

    private static StatusEffect register(StatusEffect effect, String name) {
        return Registry.register(Registries.STATUS_EFFECT, AITMod.id(name), effect);
    }
}
