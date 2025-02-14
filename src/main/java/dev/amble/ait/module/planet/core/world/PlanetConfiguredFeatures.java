package dev.amble.ait.module.planet.core.world;

import java.util.List;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import dev.amble.ait.AITMod;
import dev.amble.ait.module.planet.core.PlanetBlocks;

public class PlanetConfiguredFeatures {
   public static final RegistryKey<ConfiguredFeature<?, ?>> MARTIAN_COAL_ORE = registryKey("martian_coal_ore");
   public static final RegistryKey<ConfiguredFeature<?, ?>> MARTIAN_COPPER_ORE = registryKey("martian_copper_ore");
   public static final RegistryKey<ConfiguredFeature<?, ?>> MARTIAN_IRON_ORE = registryKey("martian_iron_ore");
   public static final RegistryKey<ConfiguredFeature<?, ?>> MARTIAN_GOLD_ORE = registryKey("martian_gold_ore");
   public static final RegistryKey<ConfiguredFeature<?, ?>> MARTIAN_REDSTONE_ORE = registryKey("martian_redstone_ore");
   public static final RegistryKey<ConfiguredFeature<?, ?>> MARTIAN_LAPIS_ORE = registryKey("martian_lapis_ore");
   public static final RegistryKey<ConfiguredFeature<?, ?>> MARTIAN_DIAMOND_ORE = registryKey("martian_diamond_ore");
   public static final RegistryKey<ConfiguredFeature<?, ?>> MARTIAN_EMERALD_ORE = registryKey("martian_emerald_ore");

    public static final RegistryKey<ConfiguredFeature<?, ?>> ANORTHOSITE_COAL_ORE = registryKey("anorthosite_coal_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> ANORTHOSITE_COPPER_ORE = registryKey("anorthosite_copper_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> ANORTHOSITE_IRON_ORE = registryKey("anorthosite_iron_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> ANORTHOSITE_GOLD_ORE = registryKey("anorthosite_gold_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> ANORTHOSITE_REDSTONE_ORE = registryKey("anorthosite_redstone_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> ANORTHOSITE_LAPIS_ORE = registryKey("anorthosite_lapis_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> ANORTHOSITE_DIAMOND_ORE = registryKey("anorthosite_diamond_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> ANORTHOSITE_EMERALD_ORE = registryKey("anorthosite_emerald_ore");

   public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
       RuleTest martianStoneReplaceables = new BlockMatchRuleTest(PlanetBlocks.MARTIAN_STONE);
       RuleTest anorthositeReplaceables = new BlockMatchRuleTest(PlanetBlocks.ANORTHOSITE);

       List<OreFeatureConfig.Target> marsOres =
               List.of(OreFeatureConfig.createTarget(martianStoneReplaceables, PlanetBlocks.MARTIAN_COAL_ORE.getDefaultState()),
                       OreFeatureConfig.createTarget(martianStoneReplaceables, PlanetBlocks.MARTIAN_COPPER_ORE.getDefaultState()),
                       OreFeatureConfig.createTarget(martianStoneReplaceables, PlanetBlocks.MARTIAN_IRON_ORE.getDefaultState()),
                       OreFeatureConfig.createTarget(martianStoneReplaceables, PlanetBlocks.MARTIAN_GOLD_ORE.getDefaultState()),
                       OreFeatureConfig.createTarget(martianStoneReplaceables, PlanetBlocks.MARTIAN_REDSTONE_ORE.getDefaultState()),
                       OreFeatureConfig.createTarget(martianStoneReplaceables, PlanetBlocks.MARTIAN_LAPIS_ORE.getDefaultState()),
                       OreFeatureConfig.createTarget(martianStoneReplaceables, PlanetBlocks.MARTIAN_DIAMOND_ORE.getDefaultState()),
                       OreFeatureConfig.createTarget(martianStoneReplaceables, PlanetBlocks.MARTIAN_EMERALD_ORE.getDefaultState()));


       List<OreFeatureConfig.Target> anorthorsiteOres =
               List.of(OreFeatureConfig.createTarget(anorthositeReplaceables, PlanetBlocks.ANORTHOSITE_COAL_ORE.getDefaultState()),
                       OreFeatureConfig.createTarget(anorthositeReplaceables, PlanetBlocks.ANORTHOSITE_COPPER_ORE.getDefaultState()),
                       OreFeatureConfig.createTarget(anorthositeReplaceables, PlanetBlocks.ANORTHOSITE_IRON_ORE.getDefaultState()),
                       OreFeatureConfig.createTarget(anorthositeReplaceables, PlanetBlocks.ANORTHOSITE_GOLD_ORE.getDefaultState()),
                       OreFeatureConfig.createTarget(anorthositeReplaceables, PlanetBlocks.ANORTHOSITE_REDSTONE_ORE.getDefaultState()),
                       OreFeatureConfig.createTarget(anorthositeReplaceables, PlanetBlocks.ANORTHOSITE_LAPIS_ORE.getDefaultState()),
                       OreFeatureConfig.createTarget(anorthositeReplaceables, PlanetBlocks.ANORTHOSITE_DIAMOND_ORE.getDefaultState()),
                       OreFeatureConfig.createTarget(anorthositeReplaceables, PlanetBlocks.ANORTHOSITE_EMERALD_ORE.getDefaultState()));


       register(context, MARTIAN_COAL_ORE, Feature.ORE, new OreFeatureConfig(marsOres, 12));
       register(context, MARTIAN_COPPER_ORE, Feature.ORE, new OreFeatureConfig(marsOres, 10));
       register(context, MARTIAN_IRON_ORE, Feature.ORE, new OreFeatureConfig(marsOres, 8));
       register(context, MARTIAN_GOLD_ORE, Feature.ORE, new OreFeatureConfig(marsOres, 6));
       register(context, MARTIAN_REDSTONE_ORE, Feature.ORE, new OreFeatureConfig(marsOres, 6));
       register(context, MARTIAN_LAPIS_ORE, Feature.ORE, new OreFeatureConfig(marsOres, 4));
       register(context, MARTIAN_DIAMOND_ORE, Feature.ORE, new OreFeatureConfig(marsOres, 3));
       register(context, MARTIAN_EMERALD_ORE, Feature.ORE, new OreFeatureConfig(marsOres, 1));


       register(context, ANORTHOSITE_COAL_ORE, Feature.ORE, new OreFeatureConfig(anorthorsiteOres, 12));
       register(context, ANORTHOSITE_COPPER_ORE, Feature.ORE, new OreFeatureConfig(anorthorsiteOres, 10));
       register(context, ANORTHOSITE_IRON_ORE, Feature.ORE, new OreFeatureConfig(anorthorsiteOres, 8));
       register(context, ANORTHOSITE_GOLD_ORE, Feature.ORE, new OreFeatureConfig(anorthorsiteOres, 6));
       register(context, ANORTHOSITE_REDSTONE_ORE, Feature.ORE, new OreFeatureConfig(anorthorsiteOres, 6));
       register(context, ANORTHOSITE_LAPIS_ORE, Feature.ORE, new OreFeatureConfig(anorthorsiteOres, 4));
       register(context, ANORTHOSITE_DIAMOND_ORE, Feature.ORE, new OreFeatureConfig(anorthorsiteOres, 3));
       register(context, ANORTHOSITE_EMERALD_ORE, Feature.ORE, new OreFeatureConfig(anorthorsiteOres, 1));
   }

    public static RegistryKey<ConfiguredFeature<?, ?>> registryKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, AITMod.id(name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context,
                                                                                   RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }


}
