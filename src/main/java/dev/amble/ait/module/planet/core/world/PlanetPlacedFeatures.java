package dev.amble.ait.module.planet.core.world;

import java.util.List;

import dev.amble.ait.AITMod;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;

public class PlanetPlacedFeatures {

    public static final RegistryKey<PlacedFeature> MARS_BURIED_ICE = registerKey("mars_buried_ice");

    public static void boostrap(Registerable<PlacedFeature> context) {
        RegistryEntryLookup<ConfiguredFeature<?, ?>> features = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        ores(context, features, "anorthosite", PlanetConfiguredFeatures.MOON);
        ores(context, features, "martian", PlanetConfiguredFeatures.MARS);

        register(context, MARS_BURIED_ICE, features.getOrThrow(PlanetConfiguredFeatures.MARS_BURIED_ICE),
                PlanetOrePlacement.modifiersWithRarity(4, HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(60))));
    }

    private static void ores(Registerable<PlacedFeature> context, RegistryEntryLookup<ConfiguredFeature<?, ?>> features,
                             String prefix, PlanetConfiguredFeatures.PlanetOres ores) {
        String base = prefix + "_ore_";

        register(context, registerKey(base + "coal_upper"), features.getOrThrow(ores.coal),
                PlanetOrePlacement.modifiersWithCount(30, HeightRangePlacementModifier.uniform(YOffset.fixed(136), YOffset.getTop())));
        register(context, registerKey(base + "coal_lower"), features.getOrThrow(ores.coalBuried),
                PlanetOrePlacement.modifiersWithCount(20, HeightRangePlacementModifier.trapezoid(YOffset.fixed(0), YOffset.fixed(192))));

        register(context, registerKey(base + "iron_upper"), features.getOrThrow(ores.iron),
                PlanetOrePlacement.modifiersWithCount(90, HeightRangePlacementModifier.trapezoid(YOffset.fixed(80), YOffset.fixed(384))));
        register(context, registerKey(base + "iron_middle"), features.getOrThrow(ores.iron),
                PlanetOrePlacement.modifiersWithCount(10, HeightRangePlacementModifier.trapezoid(YOffset.fixed(-24), YOffset.fixed(56))));
        register(context, registerKey(base + "iron_small"), features.getOrThrow(ores.ironSmall),
                PlanetOrePlacement.modifiersWithCount(10, HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(72))));

        register(context, registerKey(base + "copper"), features.getOrThrow(ores.copper),
                PlanetOrePlacement.modifiersWithCount(16, HeightRangePlacementModifier.trapezoid(YOffset.fixed(-16), YOffset.fixed(112))));

        register(context, registerKey(base + "gold"), features.getOrThrow(ores.goldBuried),
                PlanetOrePlacement.modifiersWithCount(4, HeightRangePlacementModifier.trapezoid(YOffset.fixed(-64), YOffset.fixed(32))));
        register(context, registerKey(base + "gold_lower"), features.getOrThrow(ores.goldBuried),
                PlanetOrePlacement.modifiers(CountPlacementModifier.of(UniformIntProvider.create(0, 1)),
                        HeightRangePlacementModifier.uniform(YOffset.fixed(-64), YOffset.fixed(-48))));

        register(context, registerKey(base + "redstone"), features.getOrThrow(ores.redstone),
                PlanetOrePlacement.modifiersWithCount(4, HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(15))));
        register(context, registerKey(base + "redstone_lower"), features.getOrThrow(ores.redstone),
                PlanetOrePlacement.modifiersWithCount(8, HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(-32), YOffset.aboveBottom(32))));

        register(context, registerKey(base + "lapis"), features.getOrThrow(ores.lapis),
                PlanetOrePlacement.modifiersWithCount(2, HeightRangePlacementModifier.trapezoid(YOffset.fixed(-32), YOffset.fixed(32))));
        register(context, registerKey(base + "lapis_buried"), features.getOrThrow(ores.lapisBuried),
                PlanetOrePlacement.modifiersWithCount(4, HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(64))));

        register(context, registerKey(base + "diamond"), features.getOrThrow(ores.diamondSmall),
                PlanetOrePlacement.modifiersWithCount(7, HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(-80), YOffset.aboveBottom(80))));
        register(context, registerKey(base + "diamond_large"), features.getOrThrow(ores.diamondLarge),
                PlanetOrePlacement.modifiersWithRarity(9, HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(-80), YOffset.aboveBottom(80))));
        register(context, registerKey(base + "diamond_buried"), features.getOrThrow(ores.diamondBuried),
                PlanetOrePlacement.modifiersWithCount(4, HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(-80), YOffset.aboveBottom(80))));

        register(context, registerKey(base + "emerald"), features.getOrThrow(ores.emerald),
                PlanetOrePlacement.modifiersWithCount(100, HeightRangePlacementModifier.trapezoid(YOffset.fixed(-16), YOffset.fixed(480))));
    }

    public static RegistryKey<PlacedFeature> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, AITMod.id(name));
    }

    private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key, RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
