package dev.amble.ait.module.planet.core.space.planet;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITDimensions;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionOptions;

import java.util.function.Predicate;

public class PlanetBiomeSelectors {
    private static final RegistryKey<Registry<DimensionOptions>> DIMENSION_KEY =
            RegistryKey.ofRegistry(AITMod.id("dimension"));

    public static final RegistryKey<DimensionOptions> MARS_DIMENSION_OPTIONS =
            RegistryKey.of(DIMENSION_KEY, AITDimensions.MARS.getValue());

    public static final RegistryKey<DimensionOptions> MOON_DIMENSION_OPTIONS =
            RegistryKey.of(DIMENSION_KEY, AITDimensions.MOON.getValue());

    public static final RegistryKey<DimensionOptions> SPACE_DIMENSION_OPTIONS =
            RegistryKey.of(DIMENSION_KEY, AITDimensions.SPACE.getValue());

    public static Predicate<BiomeSelectionContext> foundInMars() {
        return context -> context.canGenerateIn(MARS_DIMENSION_OPTIONS);
    }

    public static Predicate<BiomeSelectionContext> foundInMoon() {
        return context -> context.canGenerateIn(MOON_DIMENSION_OPTIONS);
    }

    public static Predicate<BiomeSelectionContext> foundInSpace() {
        return context -> context.canGenerateIn(SPACE_DIMENSION_OPTIONS);
    }
}
