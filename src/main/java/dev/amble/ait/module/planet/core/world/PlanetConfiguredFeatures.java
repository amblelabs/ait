package dev.amble.ait.module.planet.core.world;

import java.util.List;

import dev.amble.ait.AITMod;
import dev.amble.ait.module.planet.core.PlanetBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class PlanetConfiguredFeatures {

    public static final PlanetOres MOON = new PlanetOres("anorthosite");
    public static final PlanetOres MARS = new PlanetOres("martian");

    public static final RegistryKey<ConfiguredFeature<?, ?>> MARS_BURIED_ICE = registryKey("mars_buried_ice");

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
        MOON.bootstrap(context, PlanetBlocks.ANORTHOSITE, PlanetBlocks.ANORTHOSITE_COAL_ORE, PlanetBlocks.ANORTHOSITE_IRON_ORE,
                PlanetBlocks.ANORTHOSITE_COPPER_ORE, PlanetBlocks.ANORTHOSITE_GOLD_ORE, PlanetBlocks.ANORTHOSITE_REDSTONE_ORE,
                PlanetBlocks.ANORTHOSITE_LAPIS_ORE, PlanetBlocks.ANORTHOSITE_DIAMOND_ORE, PlanetBlocks.ANORTHOSITE_EMERALD_ORE);

        MARS.bootstrap(context, PlanetBlocks.MARTIAN_STONE, PlanetBlocks.MARTIAN_COAL_ORE, PlanetBlocks.MARTIAN_IRON_ORE,
                PlanetBlocks.MARTIAN_COPPER_ORE, PlanetBlocks.MARTIAN_GOLD_ORE, PlanetBlocks.MARTIAN_REDSTONE_ORE,
                PlanetBlocks.MARTIAN_LAPIS_ORE, PlanetBlocks.MARTIAN_DIAMOND_ORE, PlanetBlocks.MARTIAN_EMERALD_ORE);

        register(context, MARS_BURIED_ICE, Feature.ORE, new OreFeatureConfig(
                List.of(OreFeatureConfig.createTarget(new BlockMatchRuleTest(PlanetBlocks.MARTIAN_STONE), Blocks.PACKED_ICE.getDefaultState())),
                33, 0.6f));
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registryKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, AITMod.id(name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context,
                                                                                   RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }

    public static final class PlanetOres {
        public final RegistryKey<ConfiguredFeature<?, ?>> coal;
        public final RegistryKey<ConfiguredFeature<?, ?>> coalBuried;
        public final RegistryKey<ConfiguredFeature<?, ?>> iron;
        public final RegistryKey<ConfiguredFeature<?, ?>> ironSmall;
        public final RegistryKey<ConfiguredFeature<?, ?>> copper;
        public final RegistryKey<ConfiguredFeature<?, ?>> goldBuried;
        public final RegistryKey<ConfiguredFeature<?, ?>> redstone;
        public final RegistryKey<ConfiguredFeature<?, ?>> lapis;
        public final RegistryKey<ConfiguredFeature<?, ?>> lapisBuried;
        public final RegistryKey<ConfiguredFeature<?, ?>> diamondSmall;
        public final RegistryKey<ConfiguredFeature<?, ?>> diamondLarge;
        public final RegistryKey<ConfiguredFeature<?, ?>> diamondBuried;
        public final RegistryKey<ConfiguredFeature<?, ?>> emerald;

        private PlanetOres(String prefix) {
            this.coal = registryKey(prefix + "_coal_ore");
            this.coalBuried = registryKey(prefix + "_coal_ore_buried");
            this.iron = registryKey(prefix + "_iron_ore");
            this.ironSmall = registryKey(prefix + "_iron_ore_small");
            this.copper = registryKey(prefix + "_copper_ore");
            this.goldBuried = registryKey(prefix + "_gold_ore_buried");
            this.redstone = registryKey(prefix + "_redstone_ore");
            this.lapis = registryKey(prefix + "_lapis_ore");
            this.lapisBuried = registryKey(prefix + "_lapis_ore_buried");
            this.diamondSmall = registryKey(prefix + "_diamond_ore_small");
            this.diamondLarge = registryKey(prefix + "_diamond_ore_large");
            this.diamondBuried = registryKey(prefix + "_diamond_ore_buried");
            this.emerald = registryKey(prefix + "_emerald_ore");
        }

        private void bootstrap(Registerable<ConfiguredFeature<?, ?>> context, Block stone, Block coalOre, Block ironOre,
                               Block copperOre, Block goldOre, Block redstoneOre, Block lapisOre, Block diamondOre, Block emeraldOre) {
            RuleTest host = new BlockMatchRuleTest(stone);

            ore(context, this.coal, host, coalOre, 17, 0f);
            ore(context, this.coalBuried, host, coalOre, 17, 0.5f);
            ore(context, this.iron, host, ironOre, 9, 0f);
            ore(context, this.ironSmall, host, ironOre, 4, 0f);
            ore(context, this.copper, host, copperOre, 10, 0f);
            ore(context, this.goldBuried, host, goldOre, 9, 0.5f);
            ore(context, this.redstone, host, redstoneOre, 8, 0f);
            ore(context, this.lapis, host, lapisOre, 7, 0f);
            ore(context, this.lapisBuried, host, lapisOre, 7, 1f);
            ore(context, this.diamondSmall, host, diamondOre, 4, 0.5f);
            ore(context, this.diamondLarge, host, diamondOre, 12, 0.7f);
            ore(context, this.diamondBuried, host, diamondOre, 8, 1f);
            ore(context, this.emerald, host, emeraldOre, 3, 0f);
        }

        private static void ore(Registerable<ConfiguredFeature<?, ?>> context, RegistryKey<ConfiguredFeature<?, ?>> key,
                                RuleTest host, Block ore, int size, float discardOnAir) {
            register(context, key, Feature.ORE, new OreFeatureConfig(
                    List.of(OreFeatureConfig.createTarget(host, ore.getDefaultState())), size, discardOnAir));
        }
    }
}
