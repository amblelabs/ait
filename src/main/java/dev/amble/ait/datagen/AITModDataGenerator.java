package dev.amble.ait.datagen;

import static dev.amble.ait.core.AITItems.isUnlockedOnThisDay;
import static net.minecraft.data.server.recipe.RecipeProvider.*;
import static net.minecraft.data.server.recipe.RecipeProvider.createSlabRecipe;

import java.util.Calendar;
import java.util.concurrent.CompletableFuture;

import dev.amble.lib.datagen.lang.AmbleLanguageProvider;
import dev.amble.lib.datagen.lang.LanguageType;
import dev.amble.lib.datagen.sound.AmbleSoundProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITEntityTypes;
import dev.amble.ait.core.AITItemGroups;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.datagen.datagen_providers.*;
import dev.amble.ait.datagen.datagen_providers.loot.AITBlockLootTables;
import dev.amble.ait.module.ModuleRegistry;
import dev.amble.ait.module.gun.core.entity.GunEntityTypes;
import dev.amble.ait.module.planet.core.PlanetBlocks;
import dev.amble.ait.module.planet.core.PlanetItems;
import dev.amble.ait.module.planet.core.world.PlanetConfiguredFeatures;
import dev.amble.ait.module.planet.core.world.PlanetPlacedFeatures;

public class
AITModDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        generateLanguages(pack);
        generateItemTags(pack);
        generateBlockTags(pack);
        generateGameEventTags(pack);
        generatePaintingTags(pack);
        generateEntityTypeTags(pack);
        generateRecipes(pack);
        generateBlockModels(pack);
        generateSoundData(pack);
        generateAdvancements(pack);
        generateLoot(pack);
        generatePoi(pack);
        generateWorldFeatures(pack);
    }

    public void generateLoot(FabricDataGenerator.Pack pack) {
        pack.addProvider(AITBlockLootTables::new);
    }

    public void generatePoi(FabricDataGenerator.Pack pack) {
        pack.addProvider(AITPoiTagProvider::new);
    }

    private void generateAdvancements(FabricDataGenerator.Pack pack) {
        pack.addProvider(AITAchievementProvider::new);
    }

    private void generateWorldFeatures(FabricDataGenerator.Pack pack) {
        pack.addProvider(AITWorldGeneratorProvider::new);
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, PlanetConfiguredFeatures::bootstrap);
        registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, PlanetPlacedFeatures::boostrap);
    }

    public void generateRecipes(FabricDataGenerator.Pack pack) {
        pack.addProvider((((output, registriesFuture) -> {
            AITRecipeProvider provider = new AITRecipeProvider(output);

            ModuleRegistry.instance().iterator().forEachRemaining(module -> module.getDataGenerator().ifPresent(dataGenerator -> {
                dataGenerator.recipes(provider);
            }));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITItems.IRON_KEY, 1)
                    .pattern(" N ").pattern("IEI").pattern("IRI").input('N', Items.IRON_NUGGET)
                    .input('I', Items.IRON_INGOT).input('E', Items.ENDER_PEARL).input('R', Items.REDSTONE)
                    .criterion(hasItem(Items.IRON_NUGGET), conditionsFromItem(Items.IRON_NUGGET))
                    .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                    .criterion(hasItem(Items.ENDER_PEARL), conditionsFromItem(Items.ENDER_PEARL))
                    .criterion(hasItem(Items.REDSTONE), conditionsFromItem(Items.REDSTONE)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, AITItems.PLASMIC_MATERIAL, 4)
                    .pattern("CCC")
                    .pattern("SBS")
                    .pattern("CCC")
                    .input('B', Items.SLIME_BALL)
                    .input('S', AITItems.SUPERHEATED_ZEITON)
                    .input('C', Items.LIGHT_GRAY_CONCRETE_POWDER)
                    .criterion(hasItem(Items.SLIME_BALL), conditionsFromItem(Items.SLIME_BALL))
                    .criterion(hasItem(AITItems.SUPERHEATED_ZEITON), conditionsFromItem(AITItems.SUPERHEATED_ZEITON))
                    .criterion(hasItem(Items.LIGHT_GRAY_CONCRETE_POWDER), conditionsFromItem(Items.LIGHT_GRAY_CONCRETE_POWDER)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITBlocks.CORAL_PLANT, 1)
                    .pattern("CCC")
                    .pattern("CCC")
                    .pattern("CCC")
                    .input('C', AITItems.CORAL_FRAGMENT)
                    .criterion(hasItem(AITItems.CORAL_FRAGMENT), conditionsFromItem(AITItems.CORAL_FRAGMENT)));

            provider.addBlastFurnaceRecipe(CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(AITItems.ZEITON_SHARD),
                            RecipeCategory.MISC, AITItems.SUPERHEATED_ZEITON, 0.2f, 500)
                    .criterion(hasItem(AITItems.ZEITON_SHARD), conditionsFromItem(AITItems.ZEITON_SHARD)));

            provider.addShapedRecipe(
                    ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, AITBlocks.ZEITON_BLOCK, 1)
                            .pattern("ZZ ").pattern("ZZ ").pattern("   ").input('Z', AITItems.ZEITON_SHARD)
                            .criterion(hasItem(AITItems.ZEITON_SHARD), conditionsFromItem(AITItems.ZEITON_SHARD)));

            provider.addShapedRecipe(
                    ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, AITItems.GOLD_KEY_UPGRADE_SMITHING_TEMPLATE, 1)
                            .pattern("GGG").pattern("GNG").pattern("GGG")
                            .input('N', Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE).input('G', Items.GOLD_NUGGET)
                            .criterion(hasItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                                    conditionsFromItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE))
                            .criterion(hasItem(Items.GOLD_NUGGET), conditionsFromItem(Items.GOLD_NUGGET)));

            provider.addShapelessRecipe(ShapelessRecipeJsonBuilder
                    .create(RecipeCategory.BREWING, AITItems.ZEITON_DUST, 4).input(AITItems.ZEITON_SHARD)
                    .criterion(hasItem(AITItems.ZEITON_SHARD), conditionsFromItem(AITItems.ZEITON_SHARD)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder
                    .create(RecipeCategory.MISC, AITItems.CHARGED_ZEITON_CRYSTAL, 1).pattern("ZZZ").pattern("CAC")
                    .pattern("ZZZ").input('Z', AITItems.ZEITON_SHARD).input('C', AITBlocks.ZEITON_CLUSTER)
                    .input('A', AITItems.ARTRON_COLLECTOR)
                    .criterion(hasItem(AITItems.ZEITON_SHARD), conditionsFromItem(AITItems.ZEITON_SHARD))
                    .criterion(hasItem(AITBlocks.ZEITON_CLUSTER), conditionsFromItem(AITBlocks.ZEITON_CLUSTER))
                    .criterion(hasItem(AITItems.ARTRON_COLLECTOR), conditionsFromItem(AITItems.ARTRON_COLLECTOR)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder
                    .create(RecipeCategory.MISC, AITItems.NETHERITE_KEY_UPGRADE_SMITHING_TEMPLATE, 1).pattern("SSS")
                    .pattern("SGS").pattern("SSS").input('G', AITItems.GOLD_KEY_UPGRADE_SMITHING_TEMPLATE)
                    .input('S', Items.NETHERITE_SCRAP)
                    .criterion(hasItem(AITItems.GOLD_KEY_UPGRADE_SMITHING_TEMPLATE),
                            conditionsFromItem(AITItems.GOLD_KEY_UPGRADE_SMITHING_TEMPLATE))
                    .criterion(hasItem(Items.NETHERITE_SCRAP), conditionsFromItem(Items.NETHERITE_SCRAP)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder
                    .create(RecipeCategory.MISC, AITItems.CLASSIC_KEY_UPGRADE_SMITHING_TEMPLATE, 1).pattern("SAS")
                    .pattern("INI").pattern("SAS").input('I', Items.NETHERITE_INGOT)
                    .input('N', AITItems.NETHERITE_KEY_UPGRADE_SMITHING_TEMPLATE).input('S', Items.NETHERITE_SCRAP)
                    .input('A', Items.AMETHYST_SHARD)
                    .criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromItem(Items.NETHERITE_INGOT))
                    .criterion(hasItem(AITItems.NETHERITE_KEY_UPGRADE_SMITHING_TEMPLATE),
                            conditionsFromItem(AITItems.NETHERITE_KEY_UPGRADE_SMITHING_TEMPLATE))
                    .criterion(hasItem(Items.NETHERITE_SCRAP), conditionsFromItem(Items.NETHERITE_SCRAP))
                    .criterion(hasItem(Items.AMETHYST_SHARD), conditionsFromItem(Items.AMETHYST_SHARD)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder
                    .create(RecipeCategory.TOOLS, AITBlocks.ARTRON_COLLECTOR_BLOCK, 1).pattern(" L ").pattern(" R ")
                    .pattern("SBS").input('L', Items.LIGHTNING_ROD).input('R', Items.IRON_INGOT)
                    .input('S', Items.SMOOTH_STONE_SLAB).input('B', Items.REDSTONE_BLOCK)
                    .criterion(hasItem(Items.LIGHTNING_ROD), conditionsFromItem(Items.LIGHTNING_ROD))
                    .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                    .criterion(hasItem(Items.SMOOTH_STONE_SLAB), conditionsFromItem(Items.SMOOTH_STONE_SLAB))
                    .criterion(hasItem(Items.REDSTONE_BLOCK), conditionsFromItem(Items.REDSTONE_BLOCK)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITItems.ARTRON_COLLECTOR, 1)
                    .pattern("CCC").pattern("IRI").pattern("CCC").input('C', Items.COPPER_INGOT)
                    .input('I', Items.IRON_INGOT).input('R', Items.REDSTONE_BLOCK)
                    .criterion(hasItem(Items.COPPER_INGOT), conditionsFromItem(Items.COPPER_INGOT))
                    .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                    .criterion(hasItem(Items.REDSTONE_BLOCK), conditionsFromItem(Items.REDSTONE_BLOCK)));

            provider.addShapedRecipe(
                    ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITItems.RIFT_SCANNER, 1).pattern(" A ")
                            .pattern("IDI").pattern("QRQ").input('A', Items.AMETHYST_SHARD).input('I', Items.IRON_INGOT)
                            .input('D', Items.DIAMOND).input('R', Items.REDSTONE_BLOCK).input('Q', Items.QUARTZ)
                            .criterion(hasItem(Items.AMETHYST_SHARD), conditionsFromItem(Items.AMETHYST_SHARD))
                            .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                            .criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
                            .criterion(hasItem(Items.REDSTONE_BLOCK), conditionsFromItem(Items.REDSTONE_BLOCK))
                            .criterion(hasItem(Items.QUARTZ), conditionsFromItem(Items.QUARTZ)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITBlocks.WAYPOINT_BANK, 1)
                    .pattern("RTR").pattern("BWB").pattern("IEI").input('R', Items.REDSTONE)
                    .input('T', Blocks.TINTED_GLASS).input('B', Items.IRON_BARS).input('W', AITItems.WAYPOINT_CARTRIDGE)
                    .input('I', Blocks.IRON_BLOCK).input('E', Blocks.ENDER_CHEST)
                    .criterion(hasItem(Items.REDSTONE), conditionsFromItem(Items.REDSTONE))
                    .criterion(hasItem(Items.TINTED_GLASS), conditionsFromItem(Items.TINTED_GLASS))
                    .criterion(hasItem(Items.IRON_BARS), conditionsFromItem(Items.IRON_BARS))
                    .criterion(hasItem(AITItems.WAYPOINT_CARTRIDGE), conditionsFromItem(AITItems.WAYPOINT_CARTRIDGE))
                    .criterion(hasItem(Items.IRON_BLOCK), conditionsFromItem(Items.IRON_BLOCK))
                    .criterion(hasItem(Blocks.ENDER_CHEST), conditionsFromItem(Blocks.ENDER_CHEST)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITBlocks.POWER_CONVERTER, 1)
                    .pattern("RFR")
                    .pattern("BGB")
                    .pattern("IFI")
                    .input('R', Items.REDSTONE)
                    .input('B', Items.IRON_BARS)
                    .input('F', AITBlocks.CABLE_BLOCK)
                    .input('I', Blocks.IRON_BLOCK)
                    .input('G', AITBlocks.GENERIC_SUBSYSTEM)
                    .criterion(hasItem(Items.REDSTONE), conditionsFromItem(Items.REDSTONE))
                    .criterion(hasItem(Items.IRON_BARS), conditionsFromItem(Items.IRON_BARS))
                    .criterion(hasItem(AITBlocks.CABLE_BLOCK), conditionsFromItem(AITBlocks.CABLE_BLOCK))
                    .criterion(hasItem(Items.IRON_BLOCK), conditionsFromItem(Items.IRON_BLOCK))
                    .criterion(hasItem(AITBlocks.GENERIC_SUBSYSTEM), conditionsFromItem(AITBlocks.GENERIC_SUBSYSTEM)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITBlocks.MATRIX_ENERGIZER, 1)
                    .pattern("IRI")
                    .pattern("SCS")
                    .pattern("IZI")
                    .input('I', Items.IRON_INGOT)
                    .input('R', Items.REDSTONE)
                    .input('S', Items.SCULK)
                    .input('C', AITBlocks.ARTRON_COLLECTOR_BLOCK)
                    .input('Z', AITItems.SUPERHEATED_ZEITON)
                    .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                    .criterion(hasItem(Items.REDSTONE), conditionsFromItem(Items.REDSTONE))
                    .criterion(hasItem(Items.SCULK), conditionsFromItem(Items.SCULK))
                    .criterion(hasItem(AITBlocks.ARTRON_COLLECTOR_BLOCK), conditionsFromItem(AITBlocks.ARTRON_COLLECTOR_BLOCK))
                    .criterion(hasItem(AITItems.SUPERHEATED_ZEITON), conditionsFromItem(AITItems.SUPERHEATED_ZEITON)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITItems.BULB, 1)
                    .pattern("GGG")
                    .pattern("G G")
                    .pattern(" I ")
                    .input('I', Items.IRON_INGOT)
                    .input('G', Blocks.GLASS_PANE)
                    .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                    .criterion(hasItem(Blocks.GLASS_PANE), conditionsFromItem(Blocks.GLASS_PANE)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITItems.CONDENSER, 1)
                    .pattern("III")
                    .pattern("IBI")
                    .pattern("III")
                    .input('I', Items.IRON_INGOT)
                    .input('B', Blocks.IRON_BARS)
                    .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                    .criterion(hasItem(Blocks.IRON_BARS), conditionsFromItem(Blocks.IRON_BARS)));


            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITItems.IRON_GOAT_HORN, 1)
                    .pattern("III")
                    .pattern("IBI")
                    .pattern("III")
                    .input('I', Items.IRON_INGOT)
                    .input('B', Items.GOAT_HORN)
                    .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                    .criterion(hasItem(Items.GOAT_HORN), conditionsFromItem(Items.GOAT_HORN)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, PlanetItems.FABRIC, 1)
                    .pattern("SSS")
                    .pattern("SPS")
                    .pattern("SSS")
                    .input('S', Items.STRING)
                    .input('P', Items.PAPER)
                    .criterion(hasItem(Items.STRING), conditionsFromItem(Items.STRING))
                    .criterion(hasItem(Items.PAPER), conditionsFromItem(Items.PAPER)));


            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, PlanetBlocks.OXYGENATOR_BLOCK, 1)
                    .pattern("IWI")
                    .pattern("BTB")
                    .pattern("IWI")
                    .input('I', Items.BLACKSTONE)
                    .input('B', Items.IRON_BARS)
                    .input('T', Items.GHAST_TEAR)
                    .input('W', Items.WATER_BUCKET)
                    .criterion(hasItem(Items.BLACKSTONE), conditionsFromItem(Items.BLACKSTONE))
                    .criterion(hasItem(Items.IRON_BARS), conditionsFromItem(Items.IRON_BARS))
                    .criterion(hasItem(Items.WATER_BUCKET), conditionsFromItem(Items.WATER_BUCKET))
                    .criterion(hasItem(Items.GHAST_TEAR), conditionsFromItem(Items.GHAST_TEAR)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITBlocks.GENERIC_SUBSYSTEM, 1)
                    .pattern("IBI")
                    .pattern("BSB")
                    .pattern("IBI")
                    .input('I', Items.IRON_INGOT)
                    .input('B', Items.IRON_BARS)
                    .input('S', Items.GHAST_TEAR)
                    .criterion(hasItem(Items.IRON_BLOCK), conditionsFromItem(Items.IRON_BLOCK))
                    .criterion(hasItem(Items.IRON_BARS), conditionsFromItem(Items.IRON_BARS))
                    .criterion(hasItem(Items.GHAST_TEAR), conditionsFromItem(Items.GHAST_TEAR)));

            provider.addShapedRecipe(
                    ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, AITBlocks.PLAQUE_BLOCK, 1).pattern("GSG")
                            .pattern("SBS").pattern("GSG").input('G', Items.GOLD_NUGGET).input('S', Items.SPRUCE_SLAB)
                            .input('B', Items.BLACK_CONCRETE)
                            .criterion(hasItem(Items.GOLD_NUGGET), conditionsFromItem(Items.GOLD_NUGGET))
                            .criterion(hasItem(Items.SPRUCE_SLAB), conditionsFromItem(Items.SPRUCE_SLAB))
                            .criterion(hasItem(Items.BLACK_CONCRETE), conditionsFromItem(Items.BLACK_CONCRETE)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, AITBlocks.CONSOLE_GENERATOR, 1)
                    .pattern(" G ").pattern("CEC").pattern(" I ").input('G', Items.GLASS).input('C', Items.COMPARATOR)
                    .input('E', Items.END_CRYSTAL).input('I', Items.IRON_INGOT)
                    .criterion(hasItem(Items.GLASS), conditionsFromItem(Items.GLASS))
                    .criterion(hasItem(Items.COMPARATOR), conditionsFromItem(Items.COMPARATOR))
                    .criterion(hasItem(Items.END_CRYSTAL), conditionsFromItem(Items.END_CRYSTAL))
                    .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, AITBlocks.DETECTOR_BLOCK, 4)
                    .pattern(" D ").pattern("ICI").pattern(" R ").input('D', Items.DAYLIGHT_DETECTOR)
                    .input('I', Items.IRON_INGOT).input('C', Items.COMPARATOR).input('R', Items.REDSTONE)
                    .criterion(hasItem(Items.DAYLIGHT_DETECTOR), conditionsFromItem(Items.DAYLIGHT_DETECTOR))
                    .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                    .criterion(hasItem(Items.COMPARATOR), conditionsFromItem(Items.COMPARATOR))
                    .criterion(hasItem(Items.REDSTONE), conditionsFromItem(Items.REDSTONE)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, AITBlocks.DOOR_BLOCK, 1)
                    .pattern("GCG").pattern("CDC").pattern("CCC").input('D', Items.IRON_DOOR)
                    .input('G', Items.GLASS_PANE).input('C', Items.LIGHT_GRAY_CONCRETE)
                    .criterion(hasItem(Items.IRON_DOOR), conditionsFromItem(Items.IRON_DOOR))
                    .criterion(hasItem(Items.GLASS_PANE), conditionsFromItem(Items.GLASS_PANE))
                    .criterion(hasItem(Items.LIGHT_GRAY_CONCRETE), conditionsFromItem(Items.LIGHT_GRAY_CONCRETE)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, AITItems.CORAL_CAGE, 1)
                    .pattern("BPB")
                    .pattern("ICI")
                    .pattern("BPB")
                    .input('B', Items.IRON_BARS)
                    .input('P', AITItems.PLASMIC_MATERIAL)
                    .input('I', Items.IRON_INGOT)
                    .input('C', AITItems.CORAL_FRAGMENT)
                    .criterion(hasItem(Items.IRON_BARS), conditionsFromItem(Items.IRON_BARS))
                    .criterion(hasItem(AITItems.PLASMIC_MATERIAL), conditionsFromItem(AITItems.PLASMIC_MATERIAL))
                    .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                    .criterion(hasItem(AITItems.CORAL_FRAGMENT), conditionsFromItem(AITItems.CORAL_FRAGMENT)));

            provider.addShapedRecipe(
                    ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITItems.WAYPOINT_CARTRIDGE, 1).pattern("III")
                            .pattern("IBI").pattern("CGC").input('I', Items.IRON_INGOT).input('B', Items.REDSTONE_BLOCK)
                            .input('C', Items.GREEN_DYE).input('G', Items.GOLD_NUGGET)
                            .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                            .criterion(hasItem(Items.REDSTONE_BLOCK), conditionsFromItem(Items.REDSTONE_BLOCK))
                            .criterion(hasItem(Items.GREEN_DYE), conditionsFromItem(Items.GREEN_DYE))
                            .criterion(hasItem(Items.GOLD_NUGGET), conditionsFromItem(Items.GOLD_NUGGET)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITItems.HAMMER, 1)
                    .pattern("DSD").pattern(" A ").pattern(" T ").input('D', Items.DRIED_KELP).input('S', Items.STRING)
                    .input('A', Items.IRON_AXE).input('T', Items.STICK)
                    .criterion(hasItem(Items.DRIED_KELP), conditionsFromItem(Items.DRIED_KELP))
                    .criterion(hasItem(Items.STRING), conditionsFromItem(Items.STRING))
                    .criterion(hasItem(Items.IRON_AXE), conditionsFromItem(Items.IRON_AXE))
                    .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK)));

            provider.addShapedRecipe(
                    ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, AITBlocks.MONITOR_BLOCK, 1).pattern("III")
                            .pattern("IBI").pattern("III").input('I', Items.IRON_INGOT).input('B', Items.ENDER_EYE)
                            .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                            .criterion(hasItem(Items.ENDER_EYE), conditionsFromItem(Items.ENDER_EYE)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder
                    .create(RecipeCategory.REDSTONE, AITBlocks.WALL_MONITOR_BLOCK, 1).pattern("QIQ").pattern("IBI")
                    .pattern("QIQ").input('Q', Items.QUARTZ).input('I', Items.IRON_INGOT).input('B', Items.ENDER_EYE)
                    .criterion(hasItem(Items.QUARTZ), conditionsFromItem(Items.QUARTZ))
                    .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                    .criterion(hasItem(Items.ENDER_EYE), conditionsFromItem(Items.ENDER_EYE)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITItems.RESPIRATOR, 1)
                    .pattern("NNN").pattern("SPS").pattern("WNW").input('N', Items.IRON_NUGGET).input('S', Items.STRING)
                    .input('P', Items.GLASS_PANE).input('W', Items.PINK_WOOL)
                    .criterion(hasItem(Items.IRON_NUGGET), conditionsFromItem(Items.IRON_NUGGET))
                    .criterion(hasItem(Items.STRING), conditionsFromItem(Items.STRING))
                    .criterion(hasItem(Items.GLASS_PANE), conditionsFromItem(Items.GLASS_PANE))
                    .criterion(hasItem(Items.PINK_WOOL), conditionsFromItem(Items.PINK_WOOL)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder
                    .create(RecipeCategory.TOOLS, AITItems.FACELESS_RESPIRATOR, 1).pattern("   ").pattern(" R ")
                    .pattern("NWN").input('R', Items.REDSTONE).input('N', Items.IRON_NUGGET)
                    .input('W', Items.BLACK_WOOL).criterion(hasItem(Items.REDSTONE), conditionsFromItem(Items.REDSTONE))
                    .criterion(hasItem(Items.IRON_NUGGET), conditionsFromItem(Items.IRON_NUGGET))
                    .criterion(hasItem(Items.BLACK_WOOL), conditionsFromItem(Items.BLACK_WOOL)));

            provider.addShapedRecipe(
                    ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, AITBlocks.ENVIRONMENT_PROJECTOR, 1)
                            .pattern("IGI").pattern("GPG").pattern("ISI").input('I', Items.IRON_INGOT)
                            .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_NUGGET))
                            .input('G', Blocks.GLASS_PANE)
                            .criterion(hasItem(Blocks.GLASS_PANE), conditionsFromItem(Blocks.GLASS_PANE))
                            .input('P', Items.ENDER_PEARL)
                            .criterion(hasItem(Items.ENDER_PEARL), conditionsFromItem(Items.ENDER_PEARL))
                            .input('S', Blocks.SEA_LANTERN)
                            .criterion(hasItem(Blocks.SEA_LANTERN), conditionsFromItem(Blocks.SEA_LANTERN)));

            provider.addShapedRecipe(
                    ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITBlocks.LANDING_PAD, 1)
                            .pattern(" E ")
                            .pattern("ZCZ")
                            .pattern("ZDZ")
                            .input('E', Items.ENDER_EYE).input('Z', AITItems.ZEITON_SHARD).input('D', Items.DIAMOND).input('C', Items.COMPASS)
                            .criterion(hasItem(Items.ENDER_EYE), conditionsFromItem(Items.ENDER_EYE))
                            .criterion(hasItem(AITItems.ZEITON_SHARD), conditionsFromItem(AITItems.ZEITON_SHARD))
                            .criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
                            .criterion(hasItem(Items.COMPASS), conditionsFromItem(Items.COMPASS)));

            /*if (isUnlockedOnThisDay(Calendar.DECEMBER, 29)) {
                provider.addShapelessRecipe(ShapelessRecipeJsonBuilder.create(RecipeCategory.COMBAT, AITItems.COBBLED_SNOWBALL)
                        .input(Blocks.COBBLESTONE).criterion(hasItem(Blocks.COBBLESTONE), conditionsFromItem(Blocks.COBBLESTONE))
                        .input(Items.SNOWBALL).criterion(hasItem(Items.SNOWBALL), conditionsFromItem(Items.SNOWBALL)));
            }*/

            if (isUnlockedOnThisDay(Calendar.JANUARY, 2)) {
                provider.addShapedRecipe(
                        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, AITItems.MUG, 1)
                                .pattern(" CC")
                                .pattern("P C")
                                .pattern(" CC")
                                .input('P', Items.FLOWER_POT)
                                .input('C', Items.CLAY_BALL)
                                .criterion(hasItem(Items.FLOWER_POT), conditionsFromItem(Items.FLOWER_POT))
                                .criterion(hasItem(Items.CLAY_BALL), conditionsFromItem(Items.CLAY_BALL)));
                /*provider.addShapelessRecipe(ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, AITItems.HOT_CHOCOLATE_POWDER)
                        .input(Items.COCOA_BEANS).criterion(hasItem(Items.COCOA_BEANS), conditionsFromItem(Items.COCOA_BEANS))
                        .input(Blocks.COBBLESTONE).criterion(hasItem(Blocks.COBBLESTONE), conditionsFromItem(Blocks.COBBLESTONE))
                        .input(AITItems.ZEITON_DUST).criterion(hasItem(AITItems.ZEITON_DUST), conditionsFromItem(AITItems.ZEITON_DUST)));
                provider.addShapelessRecipe(ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, AITItems.HOT_CHOCOLATE)
                        .input(AITItems.HOT_CHOCOLATE_POWDER).criterion(hasItem(AITItems.HOT_CHOCOLATE_POWDER), conditionsFromItem(AITItems.HOT_CHOCOLATE_POWDER))
                        .input(AITItems.MUG).criterion(hasItem(AITItems.MUG), conditionsFromItem(AITItems.MUG))
                        .input(Items.MILK_BUCKET).criterion(hasItem(Items.MILK_BUCKET), conditionsFromItem(Items.MILK_BUCKET)));*/
            }

            if (isUnlockedOnThisDay(Calendar.DECEMBER, 30)) {
                provider.addShapedRecipe(
                        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, AITBlocks.SNOW_GLOBE, 1)
                                .pattern("GGG")
                                .pattern("GSG")
                                .pattern("CCC")
                                .input('G', Items.GLASS_PANE)
                                .input('C', Items.BLUE_CONCRETE)
                                .input('S', Items.SNOWBALL)
                                .criterion(hasItem(Items.GLASS_PANE), conditionsFromItem(Items.GLASS_PANE))
                                .criterion(hasItem(Items.BLUE_CONCRETE), conditionsFromItem(Items.BLUE_CONCRETE))
                                .criterion(hasItem(Items.SNOWBALL), conditionsFromItem(Items.SNOWBALL)));
            }


            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITItems.HYPERCUBE)
                    .pattern("BBB").pattern("BEB").pattern("BBB").input('B', AITItems.ZEITON_SHARD)
                    .criterion(hasItem(AITItems.ZEITON_SHARD), conditionsFromItem(AITItems.ZEITON_SHARD))
                    .input('E', Items.END_CRYSTAL)
                    .criterion(hasItem(Items.END_CRYSTAL), conditionsFromItem(Items.END_CRYSTAL)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, AITItems.REDSTONE_CONTROL)
                    .pattern("OEO").pattern("ZRZ").pattern("OOO")
                    .input('O', Blocks.OBSIDIAN).criterion(hasItem(Blocks.OBSIDIAN), conditionsFromItem(Blocks.OBSIDIAN))
                    .input('E', Items.ENDER_EYE).criterion(hasItem(Items.ENDER_EYE), conditionsFromItem(Items.ENDER_EYE))
                    .input('Z', AITItems.ZEITON_SHARD).criterion(hasItem(AITItems.ZEITON_SHARD), conditionsFromItem(AITItems.ZEITON_SHARD))
                    .input('R', Items.REDSTONE).criterion(hasItem(Items.REDSTONE), conditionsFromItem(Items.REDSTONE)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, AITItems.HAZANDRA)
                    .pattern("CEC").pattern("ZRZ").pattern("CAC")
                    .input('A', Items.AMETHYST_SHARD).criterion(hasItem(Items.AMETHYST_SHARD), conditionsFromItem(Items.AMETHYST_SHARD))
                    .input('C', Items.COPPER_INGOT).criterion(hasItem(Items.COPPER_INGOT), conditionsFromItem(Items.COPPER_INGOT))
                    .input('E', Items.ENDER_EYE).criterion(hasItem(Items.ENDER_EYE), conditionsFromItem(Items.ENDER_EYE))
                    .input('Z', AITItems.ZEITON_SHARD).criterion(hasItem(AITItems.ZEITON_SHARD), conditionsFromItem(AITItems.ZEITON_SHARD))
                    .input('R', Items.END_CRYSTAL).criterion(hasItem(Items.END_CRYSTAL), conditionsFromItem(Items.END_CRYSTAL)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, AITBlocks.CABLE_BLOCK, 8)
                    .pattern("DDD").pattern("CZC").pattern("DDD")
                    .input('D', Blocks.POLISHED_DEEPSLATE).criterion(hasItem(Blocks.POLISHED_DEEPSLATE), conditionsFromItem(Blocks.POLISHED_DEEPSLATE))
                    .input('C', Items.COPPER_INGOT).criterion(hasItem(Items.COPPER_INGOT), conditionsFromItem(Items.COPPER_INGOT))
                    .input('Z', AITItems.ZEITON_SHARD).criterion(hasItem(AITItems.ZEITON_SHARD), conditionsFromItem(AITItems.ZEITON_SHARD)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, AITItems.INDUCTOR)
                    .pattern("BCB")
                    .input('B', Items.BLACKSTONE).criterion(hasItem(Items.BLACKSTONE), conditionsFromItem(Items.BLACKSTONE))
                    .input('C', Items.COPPER_INGOT).criterion(hasItem(Items.COPPER_INGOT), conditionsFromItem(Items.COPPER_INGOT)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, AITItems.MANIPULATOR)
                    .pattern("III").pattern("IZI").pattern("III")
                    .input('I', Items.IRON_INGOT).criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                    .input('Z', AITItems.ZEITON_SHARD).criterion(hasItem(AITItems.ZEITON_SHARD), conditionsFromItem(AITItems.ZEITON_SHARD)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE,AITBlocks.FABRICATOR)
                    .pattern(" O ").pattern("OZO").pattern("OZO")
                    .input('O', Blocks.OBSIDIAN).criterion(hasItem(Blocks.OBSIDIAN), conditionsFromItem(Blocks.OBSIDIAN))
                    .input('Z', AITItems.ZEITON_SHARD).criterion(hasItem(AITItems.ZEITON_SHARD), conditionsFromItem(AITItems.ZEITON_SHARD)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, AITItems.GALLIFREY_FALLS_PAINTING)
                    .pattern("OBO")
                    .pattern("BSB")
                    .pattern("OPO")
                    .input('O', Blocks.ORANGE_WOOL)
                    .criterion(hasItem(Blocks.ORANGE_WOOL), conditionsFromItem(Blocks.ORANGE_WOOL))
                    .input('S', Items.NETHER_STAR)
                    .criterion(hasItem(Items.NETHER_STAR), conditionsFromItem(Items.NETHER_STAR))
                    .input('P', Items.PAINTING)
                    .criterion(hasItem(Items.PAINTING), conditionsFromItem(Items.PAINTING))
                    .input('B', Blocks.BLACK_WOOL)
                    .criterion(hasItem(Blocks.BLACK_WOOL), conditionsFromItem(Blocks.BLACK_WOOL)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITItems.PSYCHPAPER)
                    .pattern("SOS")
                    .pattern("BPB")
                    .pattern("SOS")
                    .input('O', Items.BLACK_DYE)
                    .criterion(hasItem(Items.BLACK_DYE), conditionsFromItem(Items.BLACK_DYE))
                    .input('S', Items.LEATHER)
                    .criterion(hasItem(Items.LEATHER), conditionsFromItem(Items.LEATHER))
                    .input('P', Items.PAPER)
                    .criterion(hasItem(Items.PAPER), conditionsFromItem(Items.PAPER))
                    .input('B', Items.ENDER_EYE)
                    .criterion(hasItem(Items.ENDER_EYE), conditionsFromItem(Items.ENDER_EYE)));


            provider.addShapelessRecipe(ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, AITBlocks.ZEITON_COBBLE)
                    .input(Blocks.COBBLESTONE).criterion(hasItem(Blocks.COBBLESTONE), conditionsFromItem(Blocks.COBBLESTONE))
                    .input(AITItems.ZEITON_SHARD).criterion(hasItem(AITItems.ZEITON_SHARD), conditionsFromItem(AITItems.ZEITON_SHARD)));


            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, AITBlocks.COMPACT_ZEITON)
                    .pattern("OBO")
                    .pattern("BSB")
                    .pattern("OPO")
                    .input('O', AITItems.ZEITON_SHARD)
                    .criterion(hasItem(AITItems.ZEITON_SHARD), conditionsFromItem(AITItems.ZEITON_SHARD))
                    .input('S', Blocks.COBBLESTONE)
                    .criterion(hasItem(Blocks.COBBLESTONE), conditionsFromItem(Blocks.COBBLESTONE))
                    .input('P', AITBlocks.ZEITON_COBBLE)
                    .criterion(hasItem(AITBlocks.ZEITON_COBBLE), conditionsFromItem(AITBlocks.ZEITON_COBBLE))
                    .input('B', AITBlocks.ZEITON_BLOCK)
                    .criterion(hasItem(AITBlocks.ZEITON_BLOCK), conditionsFromItem(AITBlocks.ZEITON_BLOCK)));

            createStairsRecipe(PlanetBlocks.MARTIAN_STONE_STAIRS, Ingredient.ofItems(PlanetBlocks.MARTIAN_STONE));
            createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MARTIAN_STONE_SLAB, Ingredient.ofItems(PlanetBlocks.MARTIAN_STONE));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITItems.REMOTE_ITEM)
                    .pattern("OPO")
                    .pattern("BSB")
                    .pattern("OKO")
                    .input('O', Items.NETHERITE_SCRAP)
                    .criterion(hasItem(Items.NETHERITE_SCRAP), conditionsFromItem(Items.NETHERITE_SCRAP))
                    .input('S', Blocks.STONE_BUTTON)
                    .criterion(hasItem(Blocks.STONE_BUTTON), conditionsFromItem(Blocks.STONE_BUTTON))
                    .input('P', Items.NETHER_STAR)
                    .criterion(hasItem(Items.NETHER_STAR), conditionsFromItem(Items.NETHER_STAR))
                    .input('K', Blocks.REDSTONE_BLOCK)
                    .criterion(hasItem(Blocks.REDSTONE_BLOCK), conditionsFromItem(Blocks.REDSTONE_BLOCK))
                    .input('B', Items.NETHERITE_INGOT)
                    .criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromItem(Items.NETHERITE_INGOT)));


            generateSmithingRecipes(provider);
            return provider;
        })));
    }

    public void generateSmithingRecipes(AITRecipeProvider provider) {
        // Key Crafting
        provider.addSmithingTransformRecipe(
                SmithingTransformRecipeJsonBuilder
                        .create(Ingredient.ofItems(AITItems.GOLD_KEY_UPGRADE_SMITHING_TEMPLATE),
                                Ingredient.ofItems(AITItems.IRON_KEY), Ingredient.ofItems(Items.GOLD_NUGGET),
                                RecipeCategory.TOOLS, AITItems.GOLD_KEY)
                        .criterion(hasItem(AITItems.GOLD_KEY_UPGRADE_SMITHING_TEMPLATE),
                                conditionsFromItem(AITItems.GOLD_KEY_UPGRADE_SMITHING_TEMPLATE))
                        .criterion(hasItem(AITItems.IRON_KEY), conditionsFromItem(AITItems.IRON_KEY))
                        .criterion(hasItem(Items.GOLD_NUGGET), conditionsFromItem(Items.GOLD_NUGGET))
                        .criterion(hasItem(AITItems.GOLD_KEY), conditionsFromItem(AITItems.GOLD_KEY)),
                AITMod.id("gold_key_smithing"));
        provider.addSmithingTransformRecipe(
                SmithingTransformRecipeJsonBuilder
                        .create(Ingredient.ofItems(AITItems.NETHERITE_KEY_UPGRADE_SMITHING_TEMPLATE),
                                Ingredient.ofItems(AITItems.GOLD_KEY), Ingredient.ofItems(Items.NETHERITE_SCRAP),
                                RecipeCategory.TOOLS, AITItems.NETHERITE_KEY)
                        .criterion(hasItem(AITItems.NETHERITE_KEY_UPGRADE_SMITHING_TEMPLATE),
                                conditionsFromItem(AITItems.NETHERITE_KEY_UPGRADE_SMITHING_TEMPLATE))
                        .criterion(hasItem(AITItems.GOLD_KEY), conditionsFromItem(AITItems.GOLD_KEY))
                        .criterion(hasItem(Items.NETHERITE_SCRAP), conditionsFromItem(Items.NETHERITE_SCRAP))
                        .criterion(hasItem(AITItems.NETHERITE_KEY), conditionsFromItem(AITItems.NETHERITE_KEY)),
                AITMod.id("netherite_key_smithing"));
        provider.addSmithingTransformRecipe(
                SmithingTransformRecipeJsonBuilder
                        .create(Ingredient.ofItems(AITItems.CLASSIC_KEY_UPGRADE_SMITHING_TEMPLATE),
                                Ingredient.ofItems(AITItems.NETHERITE_KEY), Ingredient.ofItems(Items.AMETHYST_SHARD),
                                RecipeCategory.TOOLS, AITItems.CLASSIC_KEY)
                        .criterion(hasItem(AITItems.CLASSIC_KEY_UPGRADE_SMITHING_TEMPLATE),
                                conditionsFromItem(AITItems.CLASSIC_KEY_UPGRADE_SMITHING_TEMPLATE))
                        .criterion(hasItem(AITItems.NETHERITE_KEY), conditionsFromItem(AITItems.NETHERITE_KEY))
                        .criterion(hasItem(Items.AMETHYST_SHARD), conditionsFromItem(Items.AMETHYST_SHARD))
                        .criterion(hasItem(AITItems.CLASSIC_KEY), conditionsFromItem(AITItems.CLASSIC_KEY)),
                AITMod.id("classic_key_smithing"));

        // Horn Crafting
        provider.addSmithingTransformRecipe(
                SmithingTransformRecipeJsonBuilder
                        .create(Ingredient.ofItems(AITItems.GOLD_KEY_UPGRADE_SMITHING_TEMPLATE),
                                Ingredient.ofItems(AITItems.IRON_GOAT_HORN), Ingredient.ofItems(Items.GOLD_NUGGET),
                                RecipeCategory.TOOLS, AITItems.GOLD_GOAT_HORN)
                        .criterion(hasItem(AITItems.GOLD_KEY_UPGRADE_SMITHING_TEMPLATE),
                                conditionsFromItem(AITItems.GOLD_KEY_UPGRADE_SMITHING_TEMPLATE))
                        .criterion(hasItem(AITItems.IRON_GOAT_HORN), conditionsFromItem(AITItems.IRON_GOAT_HORN))
                        .criterion(hasItem(Items.GOLD_NUGGET), conditionsFromItem(Items.GOLD_NUGGET))
                        .criterion(hasItem(AITItems.GOLD_KEY), conditionsFromItem(AITItems.GOLD_KEY)),
                AITMod.id("gold_goat_horn_smithing"));
        provider.addSmithingTransformRecipe(
                SmithingTransformRecipeJsonBuilder
                        .create(Ingredient.ofItems(AITItems.NETHERITE_KEY_UPGRADE_SMITHING_TEMPLATE),
                                Ingredient.ofItems(AITItems.GOLD_GOAT_HORN), Ingredient.ofItems(Items.NETHERITE_SCRAP),
                                RecipeCategory.TOOLS, AITItems.NETHERITE_GOAT_HORN)
                        .criterion(hasItem(AITItems.NETHERITE_KEY_UPGRADE_SMITHING_TEMPLATE),
                                conditionsFromItem(AITItems.NETHERITE_KEY_UPGRADE_SMITHING_TEMPLATE))
                        .criterion(hasItem(AITItems.GOLD_GOAT_HORN), conditionsFromItem(AITItems.GOLD_GOAT_HORN))
                        .criterion(hasItem(Items.NETHERITE_SCRAP), conditionsFromItem(Items.NETHERITE_SCRAP))
                        .criterion(hasItem(AITItems.NETHERITE_KEY), conditionsFromItem(AITItems.NETHERITE_KEY)),
                AITMod.id("netherite_goat_horn_smithing"));
        provider.addSmithingTransformRecipe(
                SmithingTransformRecipeJsonBuilder
                        .create(Ingredient.ofItems(AITItems.CLASSIC_KEY_UPGRADE_SMITHING_TEMPLATE),
                                Ingredient.ofItems(AITItems.NETHERITE_GOAT_HORN), Ingredient.ofItems(Items.AMETHYST_SHARD),
                                RecipeCategory.TOOLS, AITItems.CLASSIC_GOAT_HORN)
                        .criterion(hasItem(AITItems.CLASSIC_KEY_UPGRADE_SMITHING_TEMPLATE),
                                conditionsFromItem(AITItems.CLASSIC_KEY_UPGRADE_SMITHING_TEMPLATE))
                        .criterion(hasItem(AITItems.NETHERITE_GOAT_HORN), conditionsFromItem(AITItems.NETHERITE_GOAT_HORN))
                        .criterion(hasItem(Items.AMETHYST_SHARD), conditionsFromItem(Items.AMETHYST_SHARD))
                        .criterion(hasItem(AITItems.CLASSIC_KEY), conditionsFromItem(AITItems.CLASSIC_KEY)),
                AITMod.id("classic_goat_horn_smithing"));
    }

    public void generateSoundData(FabricDataGenerator.Pack pack) {
        pack.addProvider((((output, registriesFuture) -> new AmbleSoundProvider(output))));
    }

    public void generateItemTags(FabricDataGenerator.Pack pack) {
        pack.addProvider(AITItemTagProvider::new);
    }

    public void generateBlockTags(FabricDataGenerator.Pack pack) {
        pack.addProvider(AITBlockTagProvider::new);
    }

    public void generateGameEventTags(FabricDataGenerator.Pack pack) {
        pack.addProvider(AITGameEventTagProvider::new);
    }

    public void generatePaintingTags(FabricDataGenerator.Pack pack) {
        pack.addProvider(AITPaintingVariantTagProvider::new);
    }

    public void generateEntityTypeTags(FabricDataGenerator.Pack pack) {
        pack.addProvider(AITEntityTypeTagProvider::new);
    }

    public void generateBlockModels(FabricDataGenerator.Pack pack) {
        pack.addProvider(((output, registriesFuture) -> {
            AITModelProvider provider = new AITModelProvider(output);
            provider.registerDirectionalBlock(AITBlocks.CONSOLE);
            provider.registerSimpleBlock(AITBlocks.EXTERIOR_BLOCK);
            provider.registerDirectionalBlock(AITBlocks.FABRICATOR);
            provider.registerDirectionalBlock(AITBlocks.DOOR_BLOCK);
            provider.registerCoralFanBlock(AITBlocks.TARDIS_CORAL_FAN, AITBlocks.TARDIS_CORAL_WALL_FAN);

            return provider;
        }));
    }

    public void generateLanguages(FabricDataGenerator.Pack pack) {
        generate_EN_US_Language(pack); // en_us (English US)
        generate_EN_UK_Language(pack); // en_uk (English UK)
        generate_FR_CA_Language(pack); // fr_ca (French Canadian)
        generate_FR_FR_Language(pack); // fr_fr (French France)
        generate_ES_AR_Language(pack); // es_ar (Spanish Argentina)
        generate_ES_CL_Language(pack); // es_cl (Spanish Chile)
        generate_ES_EC_Language(pack); // es_ec (Spanish Ecuador)
        generate_ES_ES_Language(pack); // es_es (Spanish Spain)
        generate_ES_MX_Language(pack); // es_mx (Spanish Mexico)
        generate_ES_UY_Language(pack); // es_uy (Spanish Uruguay)
        generate_ES_VE_Language(pack); // es_ve (Spanish Venezuela)
        generate_EN_AU_Language(pack); // en_au (English Australia)
        generate_EN_CA_Language(pack); // en_ca (English Canada)
        generate_EN_GB_Language(pack); // en_gb (English Great Britain)
        generate_EN_NZ_Language(pack); // en_nz (English New Zealand)
        generate_DE_DE_Language(pack); // de_de (German Germany)
        generate_DE_AT_Language(pack); // de_at (German Austria)
        generate_DE_CH_Language(pack); // de_ch (German Switzerland)
        generate_NDS_DE_Language(pack); // nds_de (Nordic German)
        generate_PT_BR_Language(pack); // pt_br (Portuguese Brazil)
        generate_RU_RU_Language(pack); // ru_ru (Russian Russia)
        generate_UK_UA_Language(pack); // uk_ua (Ukrainian Ukraine)
    }

    /**
     * Adds English translations to the language file.
     *
     * @param output           The data generator output.
     * @param registriesFuture The registries future.
     * @param languageType     The language type.
     * @return The AmbleLanguageProvider.
     */

    public AmbleLanguageProvider addEnglishTranslations(FabricDataOutput output,
                                                          CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture, LanguageType languageType) {
        AmbleLanguageProvider provider = new AmbleLanguageProvider(output, languageType);

        provider.translateBlocks(AITBlocks.class);
        provider.translateItems(AITItems.class);

        ModuleRegistry.instance().iterator().forEachRemaining(module -> {
            module.getDataGenerator().ifPresent(data -> data.lang(provider));
            module.getBlockRegistry().ifPresent(provider::translateBlocks);
            module.getItemRegistry().ifPresent(provider::translateItems);
        });

        // Control entities
        provider.addTranslation("control.ait.antigravs", "Antigravs");
        provider.addTranslation("control.ait.light_manipulator", "Refreshment Selector");
        provider.addTranslation("control.ait.food_creation", "Refreshment Dispenser");
        provider.addTranslation("control.ait.protocol_116", "Protocol 116");
        provider.addTranslation("control.ait.protocol_3", "Protocol 3");
        provider.addTranslation("control.ait.dimension", "Dimension");
        provider.addTranslation("control.ait.direction", "Direction");
        provider.addTranslation("control.ait.door_control", "Door Control");
        provider.addTranslation("control.ait.door_lock", "Door Lock");
        provider.addTranslation("control.ait.fast_return", "Fast Return");
        provider.addTranslation("control.ait.alarms", "Alarms");
        provider.addTranslation("control.ait.protocol_813", "Protocol 813");
        provider.addTranslation("control.ait.handbrake", "Handbrake");
        provider.addTranslation("control.ait.land_type", "Land Type");
        provider.addTranslation("control.ait.monitor", "Monitor");
        provider.addTranslation("control.ait.power", "Power");
        provider.addTranslation("control.ait.randomiser", "Randomiser");
        provider.addTranslation("control.ait.refueler", "Refueler");
        provider.addTranslation("control.ait.protocol_19", "Protocol 19");
        provider.addTranslation("control.ait.protocol_1913", "Protocol 1913");
        provider.addTranslation("control.ait.sonic_port", "Sonic Port");
        provider.addTranslation("control.ait.telepathic_circuit", "Telepathic Circuit");
        provider.addTranslation("control.ait.throttle", "Throttle");
        provider.addTranslation("control.ait.visualiser", "Manual Override");
        provider.addTranslation("control.ait.eject_waypoint", "Eject Waypoint");
        provider.addTranslation("control.ait.goto_waypoint", "Goto Waypoint");
        provider.addTranslation("control.ait.console_port", "Console Port");
        provider.addTranslation("control.ait.mark_waypoint", "Mark Waypoint");
        provider.addTranslation("control.ait.set_waypoint", "Set Waypoint");
        provider.addTranslation("control.ait.increment", "Increment");
        provider.addTranslation("control.ait.x", "X");
        provider.addTranslation("control.ait.y", "Y");
        provider.addTranslation("control.ait.z", "Z");
        provider.addTranslation("control.ait.shields", "Shields");
        provider.addTranslation("control.ait.engine_overload", "Protocol 515");
        provider.addTranslation("control.ait.electrical_discharge", "Protocol 54");

        // Tabs
        provider.addTranslation(AITItemGroups.MAIN, "Adventures In Time");
        provider.addTranslation(AITItemGroups.FABRICATOR, "AIT: Fabrication");

        // Config
        provider.addTranslation("text.autoconfig.aitconfig.category.server", "Server");
        provider.addTranslation("text.autoconfig.aitconfig.option.SERVER.MINIFY_JSON", "Minify JSON Output");
        provider.addTranslation("text.autoconfig.aitconfig.option.SERVER.GHOST_MONUMENT", "Allow Ghost Monument");
        provider.addTranslation("text.autoconfig.aitconfig.option.SERVER.LOCK_DIMENSIONS", "Enable Locked Dimensions");
        provider.addTranslation("text.autoconfig.aitconfig.option.SERVER.WORLDS_BLACKLIST", "Blacklist Dimensions");
        provider.addTranslation("text.autoconfig.aitconfig.option.SERVER.TRAVEL_PER_TICK", "TARDIS Travel Speed (per tick)");
        provider.addTranslation("text.autoconfig.aitconfig.option.SERVER.SEND_BULK", "Send Bulk");
        provider.addTranslation("text.autoconfig.aitconfig.option.SERVER.MAX_TARDISES", "Max TARDISES (-1 = Infinite)");

        provider.addTranslation("text.autoconfig.aitconfig.category.client", "Client");
        provider.addTranslation("text.autoconfig.aitconfig.option.CLIENT.SHOW_EXPERIMENTAL_WARNING", "Show Experimental Warning");
        provider.addTranslation("text.autoconfig.aitconfig.option.CLIENT.ENVIRONMENT_PROJECTOR", "Enable Environment Projector Skybox");
        provider.addTranslation("text.autoconfig.aitconfig.option.CLIENT.DISABLE_LOYALTY_FOG", "Disable Loyalty Fog");
        provider.addTranslation("text.autoconfig.aitconfig.option.CLIENT.DISABLE_LOYALTY_SLEEPING_ACTIONBAR", "Disable Loyalty Sleeping Actionbar Message");
        provider.addTranslation("text.autoconfig.aitconfig.option.CLIENT.TEMPERATURE_TYPE", "Temperature Type");
        provider.addTranslation("text.autoconfig.aitconfig.option.CLIENT.ENABLE_TARDIS_BOTI", "Enable TATDIS BOTI");
        provider.addTranslation("text.autoconfig.aitconfig.option.CLIENT.ANIMATE_CONSOLE", "Enable Animated Console");
        provider.addTranslation("text.autoconfig.aitconfig.option.CLIENT.ANIMATE_DOORS", "Enable Animated Doors");
        provider.addTranslation("text.autoconfig.aitconfig.option.CLIENT.DOOR_ANIMATION_SPEED", "Door Animation Speed");
        provider.addTranslation("text.autoconfig.aitconfig.option.CLIENT.SHOW_CONTROL_HITBOXES", "Show Control Hitboxes");
        provider.addTranslation("text.autoconfig.aitconfig.option.CLIENT.RENDER_DEMAT_PARTICLES", "Enable Dematerialization Particles");
        provider.addTranslation("text.autoconfig.aitconfig.option.CLIENT.INTERIOR_HUM_VOLUME", "Interior Hum Volume");
        provider.addTranslation("text.autoconfig.aitconfig.option.CLIENT.CUSTOM_MENU", "Enable Main Custom Menu");

        provider.addTranslation(AITMod.TARDIS_GRIEFING.getTranslationKey(), "TARDIS Griefing");
        provider.addTranslation("entity.minecraft.villager.fabricator_engineer", "Fabricator Engineer");

        // Entitys
        provider.addTranslation(AITEntityTypes.RIFT_ENTITY.getTranslationKey(), "Space-Time Rift");
        provider.addTranslation(AITEntityTypes.CONTROL_ENTITY_TYPE.getTranslationKey(), "Control Entity");
        provider.addTranslation(AITEntityTypes.FALLING_TARDIS_TYPE.getTranslationKey(), "Falling TARDIS");
        provider.addTranslation(AITEntityTypes.FLIGHT_TARDIS_TYPE.getTranslationKey(), "RWF TARDIS");
        provider.addTranslation(AITEntityTypes.GALLIFREY_FALLS_PAINTING_TYPE.getTranslationKey(), "Galifray Falls Painting");
        provider.addTranslation(GunEntityTypes.STASER_BOLT_ENTITY_TYPE.getTranslationKey(), "Stazer Bolt Projectile");

        // Items
        provider.addTranslation(AITItems.TARDIS_ITEM, "TARDIS");
        provider.addTranslation(AITItems.REMOTE_ITEM, "Stattenheim Remote");
        provider.addTranslation(AITItems.ARTRON_COLLECTOR, "Artron Collector Unit");
        provider.addTranslation(AITItems.SIEGE_ITEM, "TARDIS");
        provider.addTranslation(AITItems.DRIFTING_MUSIC_DISC, "Music Disc");
        provider.addTranslation(AITItems.WONDERFUL_TIME_IN_SPACE_MUSIC_DISC, "Music Disc");
        provider.addTranslation(AITItems.DRIFTING_MUSIC_DISC.getTranslationKey() + ".desc", "Radio - Drifting");
        provider.addTranslation(AITItems.WONDERFUL_TIME_IN_SPACE_MUSIC_DISC.getTranslationKey() + ".desc", "Dian - Wonderful Time in Space");
        provider.addTranslation(AITItems.MERCURY_MUSIC_DISC, "Music Disc");
        provider.addTranslation(AITItems.MERCURY_MUSIC_DISC.getTranslationKey() + ".desc", "Nitrogenez - Mercury");
        provider.addTranslation(AITItems.EARTH_MUSIC_DISC, "Music Disc");
        provider.addTranslation(AITItems.EARTH_MUSIC_DISC.getTranslationKey() + ".desc", "Nitrogenez - Earth");
        provider.addTranslation(AITItems.VENUS_MUSIC_DISC, "Music Disc");
        provider.addTranslation(AITItems.VENUS_MUSIC_DISC.getTranslationKey() + ".desc", "Nitrogenez - Venus");
        provider.addTranslation(AITItems.GOLD_KEY_UPGRADE_SMITHING_TEMPLATE, "Smithing Template");
        provider.addTranslation(AITItems.NETHERITE_KEY_UPGRADE_SMITHING_TEMPLATE, "Smithing Template");
        provider.addTranslation(AITItems.CLASSIC_KEY_UPGRADE_SMITHING_TEMPLATE, "Smithing Template");
        provider.addTranslation(AITItems.PSYCHPAPER, "Psychic Paper");
        provider.addTranslation(AITItems.GALLIFREY_FALLS_PAINTING, "Painting");
        provider.addTranslation("ait.item.drink.mug_empty", "Empty Mug");
        provider.addTranslation("ait.item.drink.hot_cocoa", "Hot Cocoa");
        provider.addTranslation("ait.item.drink.tea", "Tea");
        provider.addTranslation("ait.item.drink.latte", "Latte");
        provider.addTranslation("ait.item.drink.milk", "Milk");
        provider.addTranslation("ait.item.drink.water", "Water");
        provider.addTranslation("ait.item.drink.iced_coffee", "Iced Coffee");
        provider.addTranslation("ait.item.drink.coffee", "Coffee");
        provider.addTranslation("ait.item.drink.vodka", "Vodka");
        provider.addTranslation("ait.item.drink.chocolate_milk", "Chocolate Milk");

        // Exteriors
        provider.addTranslation("exterior.ait.capsule", "Capsule");
        provider.addTranslation("exterior.ait.police_box", "Police Box");
        provider.addTranslation("exterior.ait.booth", "Booth");
        provider.addTranslation("exterior.ait.renegade", "Renegade");
        provider.addTranslation("exterior.ait.doom", "DOOM");
        provider.addTranslation("exterior.ait.geometric", "Geometric");
        provider.addTranslation("exterior.ait.tardim", "TARDIM");
        provider.addTranslation("exterior.ait.easter_head", "Moyai");
        provider.addTranslation("exterior.ait.plinth", "Plinth");
        provider.addTranslation("exterior.ait.bookshelf", "Bookshelf");
        provider.addTranslation("exterior.ait.classic", "Classic");
        provider.addTranslation("exterior.ait.stallion", "Stallion");
        provider.addTranslation("exterior.ait.jake", "Jake");
        provider.addTranslation("exterior.ait.dalek_mod", "Dalek Mod");
        provider.addTranslation("exterior.ait.pipe", "Pipe");
        provider.addTranslation("exterior.ait.present", "Present");

        // Desktops
        provider.addTranslation("desktop.ait.coral", "Coral");
        provider.addTranslation("desktop.ait.war", "War");
        provider.addTranslation("desktop.ait.office", "Office");
        provider.addTranslation("desktop.ait.meridian", "Meridian");
        provider.addTranslation("desktop.ait.botanist", "Botanist");
        provider.addTranslation("desktop.ait.default_cave", "Default Cave");
        provider.addTranslation("desktop.ait.timeless", "Timeless");
        provider.addTranslation("desktop.ait.cave", "Cave");
        provider.addTranslation("desktop.ait.deco", "Deco");
        provider.addTranslation("desktop.ait.alnico", "Alnico");
        provider.addTranslation("desktop.ait.newbury", "Newbury");
        provider.addTranslation("desktop.ait.type_40", "Type 40");
        provider.addTranslation("desktop.ait.copper", "Copper");
        provider.addTranslation("desktop.ait.pristine", "Pristine");
        provider.addTranslation("desktop.ait.victorian", "Victorian");
        provider.addTranslation("desktop.ait.vintage", "Vintage");
        provider.addTranslation("desktop.ait.dev", "Dev");
        provider.addTranslation("desktop.ait.renaissance", "Renaissance");
        provider.addTranslation("desktop.ait.toyota", "Toyota");
        provider.addTranslation("desktop.ait.crystalline", "Crystalline");
        provider.addTranslation("desktop.ait.hourglass", "Hourglass");
        provider.addTranslation("desktop.ait.regal", "Regal");
        provider.addTranslation("desktop.ait.accursed", "Accursed");
        provider.addTranslation("desktop.ait.celery", "Celery");
        provider.addTranslation("desktop.ait.golden", "Golden");
        provider.addTranslation("desktop.ait.observatory", "Observatory");
        provider.addTranslation("desktop.ait.shalka", "Shalka");
        provider.addTranslation("desktop.ait.progress", "Progress");
        provider.addTranslation("desktop.ait.fountain", "Fountain");
        provider.addTranslation("desktop.ait.conquista", "Conquista");
        provider.addTranslation("desktop.ait.mortis", "Mortis");
        provider.addTranslation("desktop.ait.industrial", "Industrial");
        provider.addTranslation("desktop.ait.hybern", "Hybern");
        provider.addTranslation("desktop.ait.missy", "Missy");
        provider.addTranslation("desktop.ait.tron", "Tron");
        provider.addTranslation("desktop.ait.planetarium", "Planetarium");
        provider.addTranslation("desktop.ait.modest", "Modest");
        provider.addTranslation("desktop.ait.war_games", "War Games");
        provider.addTranslation("desktop.ait.renewed", "Renewed");
        provider.addTranslation("desktop.ait.definitive", "Definitive");
        provider.addTranslation("desktop.ait.exile", "Exile");
        provider.addTranslation("desktop.ait.axos", "Axos");
        provider.addTranslation("desktop.ait.cherryblossom", "Cherryblossom");

        // Sonic Screwdrivers
        provider.addTranslation("sonic.ait.prime", "Prime");
        provider.addTranslation("sonic.ait.crystalline", "Crystalline");
        provider.addTranslation("sonic.ait.renaissance", "Renaissance");
        provider.addTranslation("sonic.ait.coral", "Coral");
        provider.addTranslation("sonic.ait.fob", "Fob");
        provider.addTranslation("sonic.ait.copper", "Copper");
        provider.addTranslation("sonic.ait.mechanical", "Mechanical");
        provider.addTranslation("sonic.ait.song", "Song");
        provider.addTranslation("sonic.ait.singularity", "Singularity");
        provider.addTranslation("sonic.ait.candy_cane", "Candy Cane");
        provider.addTranslation("sonic.ait.type_100", "Type 100");

        // Blocks
        provider.addTranslation(AITBlocks.LANDING_PAD, "Landing Marker");
        provider.addTranslation(AITBlocks.DETECTOR_BLOCK, "Interior Detector Block");
        provider.addTranslation(AITBlocks.EXTERIOR_BLOCK, "Exterior");
        provider.addTranslation(AITBlocks.CORAL_PLANT, "TARDIS Coral");
        provider.addTranslation(AITBlocks.MONITOR_BLOCK, "Monitor");
        provider.addTranslation(AITBlocks.ARTRON_COLLECTOR_BLOCK, "Artron Collector");
        provider.addTranslation(AITBlocks.ZEITON_BLOCK, "Block of Zeiton");
        provider.addTranslation(AITBlocks.PLAQUE_BLOCK, "TARDIS Plaque");
        provider.addTranslation(AITBlocks.WALL_MONITOR_BLOCK, "Wall Monitor");
        provider.addTranslation(AITBlocks.DOOR_BLOCK, "Door");
        provider.addTranslation(AITBlocks.CONSOLE, "Console");
        provider.addTranslation(AITBlocks.REDSTONE_CONTROL_BLOCK, "Redstone Control");
        provider.addTranslation(AITBlocks.ENGINE_BLOCK, "Engine");
        provider.addTranslation(AITBlocks.CABLE_BLOCK, "Artron Cable");
        provider.addTranslation(AITBlocks.GENERIC_SUBSYSTEM, "Generalized Subsystem Core");

        // Block Tooltips
        provider.addTranslation("block.ait.fabricator.tooltip.use", "(Place on top of a Smithing Table)");
        provider.addTranslation("tooltip.ait.use_in_tardis", "(Place inside a TARDIS)");
        provider.addTranslation("block.ait.artron_collector_block.tooltip.use", "(Charges inside of Rift Chunks)");
        provider.addTranslation("tooltip.ait.power_converter", "(Convert Zeiton,Lava,Coal and Wood into Artron)");
        provider.addTranslation("tooltip.ait.singularity", "(Give the TARDIS Coral this to allow the generation of the interior)");
        provider.addTranslation("tooltip.ait.tardis_coral", "(Place the CORAL on soul sand in a rift chunk and give it a SMS to generate your interior)");
        provider.addTranslation("tooltip.ait.matrix_energizer", "(Place this on a naturally generating shriker and give it a nether star)");

        // Painting
        provider.addTranslation("painting.ait.crab_thrower.title", "Crab Thrower");
        provider.addTranslation("painting.ait.crab_thrower.author", "???");

        provider.addTranslation("painting.ait.gallifrey_falls.title", "Gallifrey Falls");
        provider.addTranslation("painting.ait.gallifrey_falls.author", "???");

        provider.addTranslation("painting.ait.peanut.title", "Peanut");
        provider.addTranslation("painting.ait.peanut.author", "???");

        // Death
        provider.addTranslation("death.attack.tardis_squash", "%1$s got squashed by a TARDIS!");
        provider.addTranslation("death.attack.space_suffocation", "%1$s got blown up due to lack of Oxygen!");

        // TARDIS Control Actionbar Title
        provider.addTranslation("tardis.message.protocol_813.travel", "Hail Mary: Protocol 813 is active, please prepare for departure.");
        provider.addTranslation("tardis.message.control.protocol_116.active", "Protocol 116: ENGAGED");
        provider.addTranslation("tardis.message.control.hail_mary.engaged", "Protocol 813: ENGAGED");
        provider.addTranslation("tardis.message.control.hail_mary.disengaged", "Protocol 813: DISENGAGED");
        provider.addTranslation("tardis.message.control.protocol_116.inactive", "Protocol 116: DISENGAGED");
        provider.addTranslation("tardis.message.console.has_sonic_in_port", "Cannot cache console with sonic in port");
        provider.addTranslation("tardis.message.control.antigravs.active", "Antigravs: ENGAGED");
        provider.addTranslation("tardis.message.control.antigravs.inactive", "Antigravs: DISENGAGED");
        provider.addTranslation("tardis.message.control.rwf_disabled", "RWF is disabled in SERVER config.");
        provider.addTranslation("tardis.message.control.fast_return.destination_nonexistent",
                "Fast Return: Last Position Nonexistent!");
        provider.addTranslation("tardis.message.control.fast_return.last_position", "Fast Return: LAST POSITION SET");
        provider.addTranslation("tardis.message.control.fast_return.current_position",
                "Fast Return: CURRENT POSITION SET");
        provider.addTranslation("tardis.message.control.protocol_813.active", "Protocol 813: ENGAGED");
        provider.addTranslation("tardis.message.control.protocol_813.inactive", "Protocol 813: DISENGAGED");
        provider.addTranslation("tardis.message.control.handbrake.on", "Handbrake: ON");
        provider.addTranslation("tardis.message.control.handbrake.off", "Handbrake: OFF");
        provider.addTranslation("tardis.message.control.randomiser.destination", "Destination: ");
        provider.addTranslation("tardis.message.control.refueler.enabled", "Refueling: ENGAGED");
        provider.addTranslation("tardis.message.control.refueler.disabled", "Refueling: DISENGAGED");
        provider.addTranslation("tardis.message.destination_biome", "Destination Biome: ");
        provider.addTranslation("tardis.message.control.increment.info", "Increment: ");
        provider.addTranslation("tardis.message.control.randomiser.poscontrol", "Destination: ");
        provider.addTranslation("tardis.exterior.sonic.repairing", "Repairing");
        provider.addTranslation("tardis.tool.cannot_repair", "Unable to repair TARDIS with current tool");
        provider.addTranslation("tardis.key.identity_error", "TARDIS does not identify with key");
        provider.addTranslation("tardis.message.control.hads.alarm_enabled", "Alarms: ENGAGED");
        provider.addTranslation("tardis.message.control.hads.alarms_disabled", "Alarms: DISENGAGED");
        provider.addTranslation("tardis.message.control.siege.enabled", "Siege Mode: ENGAGED");
        provider.addTranslation("tardis.message.control.siege.disabled", "Siege Mode: DISENGAGED");
        provider.addTranslation("tardis.message.control.telepathic.success", "Destination Found");
        provider.addTranslation("tardis.message.control.telepathic.failed", "Destination Not Found");
        provider.addTranslation("tardis.message.control.telepathic.choosing", "The TARDIS is choosing...");
        provider.addTranslation("tardis.message.interiorchange.success", "%s has grown to %d");
        provider.addTranslation("tardis.message.landingpad.adjust", "Adjusting to landing pad..");
        provider.addTranslation("tardis.message.self_destruct.warning", "SELF DESTRUCT INITIATED | ABORT SHIP");
        provider.addTranslation("warning.ait.needs_subsystem", "ERROR, REQUIRES ACTIVE SUBSYSTEM: %s");
        provider.addTranslation("tardis.message.growth.hint", "Throw the Personality Matrix into the water to give it life...");
        provider.addTranslation("tardis.message.growth.no_cage", "Cage the TARDIS Coral to begin Plasmic coating process!");
        provider.addTranslation("tardis.message.interiorchange.not_enough_plasmic_material", "Not enough Plasmic Material for shell: %s / 8");
        provider.addTranslation("message.ait.control.ylandtype", "Vertical Search Mode: %s");
        provider.addTranslation("message.ait.loyalty_amount", "Loyalty Level: %s");
        provider.addTranslation("message.ait.landing_code", "Landing Code...");
        provider.addTranslation("message.ait.enter_landing_code", "Enter Landing Code...");
        provider.addTranslation("message.ait.date_created", "Date Created:");
        provider.addTranslation("message.ait.unlocked", "Congratulations! You unlocked a TARDIS Exterior Shell: ");
        provider.addTranslation("message.ait.unlocked_sonic", "Congratulations! You unlocked a Sonic Screwdriver Casing Option: ");
        provider.addTranslation("message.ait.unlocked_console", "Congratulations! You unlocked a TARDIS Console Interface: ");
        provider.addTranslation("message.ait.unlocked_interior", "Congratulations! You unlocked a TARDIS Desktop Option: ");
        provider.addTranslation("message.ait.unlocked_all", "Unlocked all: ");
        provider.addTranslation("message.ait.all_types", "TARDIS Consoles, TARDIS Exteriors & Sonic Casings");
        provider.addTranslation("screen.ait.sonic_casing", "Sonic Casing");
        provider.addTranslation("screen.ait.current_au", "Current AU");
        provider.addTranslation("screen.ait.linked_tardis", "Linked TARDIS");
        provider.addTranslation("message.ait.control.xlandtype.on", "Horizontal Search: ENGAGED");
        provider.addTranslation("message.ait.control.xlandtype.off", "Horizontal Search: DISENGAGED");
        provider.addTranslation("tardis.message.engine.phasing", "ENGINES PHASING");
        provider.addTranslation("message.ait.cage.full", "It calls for the void..");
        provider.addTranslation("message.ait.cage.void_hint", "(Throw this into the END void)");
        provider.addTranslation("message.ait.cage.empty", "(Place this in a rift chunk)");

        // Achievements
        provider.addTranslation("achievement.ait.title.root", "Adventures in Time");
        provider.addTranslation("achievement.ait.description.root", "Discover the wonders of time and space.");
        provider.addTranslation("achievement.ait.title.place_coral", "Gardening Guru");
        provider.addTranslation("achievement.ait.description.place_coral", "Plant the TARDIS Coral, the seed of time itself.");
        provider.addTranslation("achievement.ait.title.enter_tardis", "How Does It Fit?");
        provider.addTranslation("achievement.ait.description.enter_tardis", "Enter the TARDIS for the first time.");
        provider.addTranslation("achievement.ait.title.iron_key", "More than Just a Piece of Metal");
        provider.addTranslation("achievement.ait.description.iron_key", "Gain an Iron Key.");
        provider.addTranslation("achievement.ait.title.gold_key", "Golden Gatekeeper");
        provider.addTranslation("achievement.ait.description.gold_key", "Gain an Golden Key.");
        provider.addTranslation("achievement.ait.title.netherite_key", "Forged in Fire");
        provider.addTranslation("achievement.ait.description.netherite_key", "Gain a Netherite Key.");
        provider.addTranslation("achievement.ait.title.classic_key", "Time Traveler's Apprentice");
        provider.addTranslation("achievement.ait.description.classic_key", "Gain a Classic Key.");
        provider.addTranslation("achievement.ait.title.first_demat", "Maiden Voyage");
        provider.addTranslation("achievement.ait.description.first_demat", "Successfully initiate the takeoff sequence and experience your first journey through time and space with your TARDIS.");
        provider.addTranslation("achievement.ait.title.first_crash", "Temporal Turbulence");
        provider.addTranslation("achievement.ait.description.first_crash", "Embrace the chaos of time and space by unintentionally crashing your TARDIS for the first time.");
        provider.addTranslation("achievement.ait.title.break_growth", "Temporal Gardener");
        provider.addTranslation("achievement.ait.description.break_growth", "Tend to the temporal vines and foliage clinging to your TARDIS by breaking off vegetation.");
        provider.addTranslation("achievement.ait.title.redecorate", "I Don't Like It.");
        provider.addTranslation("achievement.ait.description.redecorate", "Redecorate your TARDIS desktop.");
        provider.addTranslation("achievement.ait.title.ultimate_counter", "It Doesn't Do Wood!");
        provider.addTranslation("achievement.ait.description.ultimate_counter", "Attempt to use the sonic screwdriver on wood.");
        provider.addTranslation("achievement.ait.title.forced_entry", "That Won't Have Consequences...");
        provider.addTranslation("achievement.ait.description.forced_entry", "Forcefully enter a TARDIS.");
        provider.addTranslation("achievement.ait.title.pui", "Piloting under the influence");
        provider.addTranslation("achievement.ait.description.pui", "Consume Zeiton Dust while the TARDIS is in flight.");
        provider.addTranslation("achievement.ait.title.bonding", "I think it's starting to trust you.");
        provider.addTranslation("achievement.ait.description.bonding", "Reach 'Pilot' loyalty for the first time.");
        provider.addTranslation("achievement.ait.title.owner_ship", "It trusts you now worth it right?");
        provider.addTranslation("achievement.ait.description.owner_ship", "Reach 'Owner' loyalty for the first time.");
        provider.addTranslation("achievement.ait.title.enable_subsystem", "Time-Space Engineer");
        provider.addTranslation("achievement.ait.description.enable_subsystem", "Enable a subsystem.");
        provider.addTranslation("achievement.ait.title.repair_subsystem", "Handyman!");
        provider.addTranslation("achievement.ait.description.repair_subsystem", "Repair a broken subsystem");
        provider.addTranslation("achievement.ait.title.engines_phase", "Technical Problems");
        provider.addTranslation("achievement.ait.description.engines_phase", "ENGINES PHASING! That doesn't sound good.");
        provider.addTranslation("achievement.ait.title.place_energizer", "Procrastination 3000");
        provider.addTranslation("achievement.ait.description.place_energizer", "Place a Matrix Energizer ontop of a naturaly generated Shrieker.");


        // Commands
        // Fuel
        provider.addTranslation("message.ait.fuel.add", "Added %s fuel for %s; total: [%sau]");
        provider.addTranslation("message.ait.fuel.remove", "Removed %s fuel for %s; total: [%sau]");
        provider.addTranslation("message.ait.fuel.set", "Set fuel for %s; total: [%sau]");
        provider.addTranslation("message.ait.fuel.get", "Fuel of %s is: [%sau]");
        provider.addTranslation("message.ait.fuel.max", "TARDIS fuel is at max!");

        // Get TARDIS ID
        provider.addTranslation("message.ait.id", "TARDIS id: ");
        provider.addTranslation("message.ait.click_to_copy", "Click to copy");

        // Sonic (TARDIS Mode)
        provider.addTranslation("message.ait.sonic.riftfound", "RIFT CHUNK FOUND");
        provider.addTranslation("message.ait.sonic.riftnotfound", "RIFT CHUNK NOT FOUND");
        provider.addTranslation("message.ait.sonic.handbrakedisengaged",
                "Handbrake disengaged, destination set to current position");
        provider.addTranslation("message.sonic.not_damaged", "TARDIS is not damaged");
        provider.addTranslation("message.ait.sonic.mode", "Mode: ");
        provider.addTranslation("message.ait.sonic.none", "None");
        provider.addTranslation("message.ait.sonic.currenttype", "Current Casing: ");
        provider.addTranslation("message.ait.remoteitem.warning4",
                "Target has been reset and updated, the device is now pointing towards your new target");

        // Sonic Modes
        provider.addTranslation("sonic.ait.mode.inactive", "INACTIVE");
        provider.addTranslation("sonic.ait.mode.tardis", "TARDIS");
        provider.addTranslation("sonic.ait.mode.interaction", "INTERACTION");
        provider.addTranslation("sonic.ait.mode.overload", "OVERLOAD");
        provider.addTranslation("sonic.ait.mode.scanning", "SCANNING");

        // Effects
        provider.addTranslation("effect.air.lunar_sickness", "Lunar Sickness");

        // Key tooltips
        provider.addTranslation("message.ait.keysmithing.upgrade", "Upgrade");
        provider.addTranslation("message.ait.keysmithing.key", "Key Type: ");
        provider.addTranslation("message.ait.keysmithing.ingredient", "Material: ");
        provider.addTranslation("tooltip.ait.skeleton_key", "CREATIVE ONLY ITEM: Unlock any TARDIS Exteriors with it.");
        provider.addTranslation("tooltip.ait.subsystem_item", "(Use this on the Generalised Subsytem Core to set it to this type)");

        // Item tooltips
        provider.addTranslation("message.ait.artron_units", "Artron Units: %s");
        provider.addTranslation("message.ait.tooltips.artron_units", "Artron Units: ");
        provider.addTranslation("message.ait.ammo", "Ammo: %s");
        provider.addTranslation("tooltip.ait.position", "Position: ");
        provider.addTranslation("message.ait.artron_units2", " AU");


        // Environment Projector
        provider.addTranslation("message.ait.projector.skybox", "Now projecting: %s");

        // Rift Scanner
        provider.addTranslation("message.ait.riftscanner.info1", "Artron Chunk Info: ");
        provider.addTranslation("message.ait.riftscanner.info2", "Artron left in chunk: ");
        provider.addTranslation("message.ait.riftscanner.info3", "This is not a rift chunk");
        provider.addTranslation("message.ait.remoteitem.warning1",
                "The TARDIS is out of fuel and cannot dematerialise");
        provider.addTranslation("message.ait.remoteitem.warning2",
                "The TARDIS is refueling and is unable to dematerialise");
        provider.addTranslation("message.ait.remoteitem.cancel.refuel",
                "Refueling process halted, TARDIS dematerializing");
        provider.addTranslation("message.ait.remoteitem.warning3", "Cannot translocate exterior to interior dimension");
        provider.addTranslation("message.ait.remoteitem.success1", "Dematerialized TARDIS");
        provider.addTranslation("message.ait.remoteitem.success2", "Activated refueler and handbrake");
        provider.addTranslation("message.ait.tardis.control.dimension.info", "Dimension: ");
        provider.addTranslation("message.ait.version", "ᴠᴇʀꜱɪᴏɴ");
        provider.addTranslation("message.ait.max_tardises", "SERVER has reached the maximum amount of TARDISes");

        provider.addTranslation("tooltip.ait.key.notardis", "Key does not identify with any TARDIS");
        provider.addTranslation("tooltip.ait.remoteitem.holdformoreinfo", "Hold shift for more info");
        provider.addTranslation("tooltip.ait.remoteitem.notardis", "Remote does not identify with any TARDIS");
        provider.addTranslation("tooltip.ait.distresscall.source", "SOURCE");

        //Monitor
        provider.addTranslation("screen.ait.monitor.on", "ON");
        provider.addTranslation("screen.ait.monitor.off", "OFF");
        provider.addTranslation("screen.ait.monitor.apply", "Apply");
        provider.addTranslation("screen.ait.monitor.gear_icon", "⚙");
        provider.addTranslation("screen.ait.monitor.fuel", "Fuel");
        provider.addTranslation("screen.ait.monitor.traveltime", "Travel Time");
        provider.addTranslation("screen.ait.interiorsettings.title", "Interior Settings");
        provider.addTranslation("screen.ait.interiorsettings.back", "> Back");
        provider.addTranslation("screen.ait.security.button", "> Security Options");
        provider.addTranslation("screen.ait.interiorsettings.changeinterior", "> Change Interior");
        provider.addTranslation("screen.ait.interiorsettings.cacheconsole", "> Cache Console");

        //TARDIS Flight Sequences
        provider.addTranslation("sequence.ait.avoid_debris", "Debris incoming!");
        provider.addTranslation("sequence.ait.dimensional_breach", "DIMENSION BREACH: SECURE DOORS");
        provider.addTranslation("sequence.ait.energy_drain", "Artron drain detected!");
        provider.addTranslation("sequence.ait.power_drain_imminent", "Power drain imminent!");
        provider.addTranslation("sequence.ait.ship_computer_offline", "Ship requires re-stabilization!");
        provider.addTranslation("sequence.ait.anti_gravity_error", "Gravity miscalculation!");
        provider.addTranslation("sequence.ait.dimensional_drift_x", "Drifting off course X!");
        provider.addTranslation("sequence.ait.dimensional_drift_y", "Drifting off course Y!");
        provider.addTranslation("sequence.ait.dimensional_drift_z", "Drifting off course Z!");
        provider.addTranslation("sequence.ait.cloak_to_avoid_vortex_trapped_mobs", "Immediate cloaking necessary!");
        provider.addTranslation("sequence.ait.directional_error", "Directional error!");
        provider.addTranslation("sequence.ait.speed_up_to_avoid_drifting_out_of_vortex", "Vortex drift: acceleration necessary!");
        provider.addTranslation("sequence.ait.course_correct", "TARDIS off course!");
        provider.addTranslation("sequence.ait.ground_unstable", "Unstable landing position!");
        provider.addTranslation("sequence.ait.increment_scale_recalculation_necessary", "Increment scale error! Recalculation necessary!");
        provider.addTranslation("sequence.ait.small_debris_field", "Small debris field!");
        provider.addTranslation("sequence.ait.slow_down_to_avoid_flying_out_of_vortex", "Vortex drift: de-acceleration necessary!");


        // Hums
        provider.addTranslation("screen.ait.interior.settings.hum", "HUMS");
        provider.addTranslation("screen.ait.interior.settings.coral", "Coral");
        provider.addTranslation("screen.ait.interior.settings.toyota", "Toyota");
        provider.addTranslation("screen.ait.interior.settings.eight", "Eighth");
        provider.addTranslation("screen.ait.interior.settings.beacon", "Beacon");
        provider.addTranslation("screen.ait.interior.settings.copper", "Copper");
        provider.addTranslation("screen.ait.interior.settings.prime", "Prime");
        provider.addTranslation("screen.ait.interior.settings.renaissance", "Renaissance");
        provider.addTranslation("screen.ait.interior.settings.exile", "Exile");

        // Handles
        provider.addTranslation("message.ait.handles.take_off","<Handles> Taking Off.");
        provider.addTranslation("message.ait.handles.land","<Handles> Landing.");
        provider.addTranslation("message.ait.handles.toggle_shields","<Handles> Toggling Shields.");
        provider.addTranslation("message.ait.handles.toggle_alarms","<Handles> Toggling Alarms.");
        provider.addTranslation("message.ait.handles.toggle_antigravs","<Handles> Toggling Antigravs.");
        provider.addTranslation("message.ait.handles.toggle_cloaked","<Handles> Toggling Cloak.");
        provider.addTranslation("message.ait.handles.open_doors","<Handles> Opening Doors.");
        provider.addTranslation("message.ait.handles.close_doors","<Handles> Closing Doors.");
        provider.addTranslation("message.ait.handles.toggle_lock","<Handles> Toggling Lock.");
        provider.addTranslation("message.ait.handles.activate_handbrake","<Handles> Handbrake Activated.");
        provider.addTranslation("message.ait.handles.disable_handbrake","<Handles> Handbrake Disabled.");
        provider.addTranslation("message.ait.handles.default","<Handles> Couldn't recognise command.");
        provider.addTranslation("message.ait.handles.displace","<Handles> Displacing Coordinates.");
        provider.addTranslation("message.ait.handles.activate_refuel","<Handles> Refuel Activated.");
        provider.addTranslation("message.ait.handles.disable_refuel","<Handles> Refuel Disabled.");
        provider.addTranslation("message.ait.handles.when","<Handles> When.");
        provider.addTranslation("message.ait.handles.when","<Handles> Affirmative.");
        provider.addTranslation("message.ait.handles.play_wtis","<Handles> Now Playing Wonderful Time In Space.");
        provider.addTranslation("message.ait.handles.play_drifting","<Handles> Now Playing Drifting.");
        provider.addTranslation("message.ait.handles.play_mercury","<Handles> Now Playing Mercury.");
        provider.addTranslation("message.ait.handles.play_13","<Handles> Now Playing 13.");
        provider.addTranslation("message.ait.handles.play_cat","<Handles> Now Playing Cat.");
        provider.addTranslation("message.ait.handles.play_blocks","<Handles> Now Playing Blocks.");
        provider.addTranslation("message.ait.handles.play_chirp","<Handles> Now Playing Chirp.");
        provider.addTranslation("message.ait.handles.play_far","<Handles> Now Playing Far.");
        provider.addTranslation("message.ait.handles.play_hall","<Handles> Now Playing Hall.");
        provider.addTranslation("message.ait.handles.play_mellohi","<Handles> Now Playing Mellohi.");
        provider.addTranslation("message.ait.handles.play_stal","<Handles> Now Playing Stal.");
        provider.addTranslation("message.ait.handles.play_strad","<Handles> Now Playing Strad.");
        provider.addTranslation("message.ait.handles.play_ward","<Handles> Now Playing Ward.");
        provider.addTranslation("message.ait.handles.play_11","<Handles> Now Playing 11.");
        provider.addTranslation("message.ait.handles.play_wait","<Handles> Now Playing Wait.");
        provider.addTranslation("message.ait.handles.play_pigstep","<Handles> Now Playing Pigstep.");
        provider.addTranslation("message.ait.handles.play_otherside","<Handles> Now Playing Otherside.");
        provider.addTranslation("message.ait.handles.play_relic","<Handles> Now Playing Relic.");
        provider.addTranslation("message.ait.handles.play_5","<Handles> Now Playing 5.");


        // Exterior Variant translations

        // All
        provider.addTranslation("exterior.ait.adaptive", "Adaptive");
        provider.addTranslation("exterior.ait.default", "Default");
        provider.addTranslation("exterior.ait.fire", "Fire");
        provider.addTranslation("exterior.ait.soul", "Soul");
        provider.addTranslation("exterior.ait.gilded", "Gilded");
        provider.addTranslation("exterior.ait.steel", "Steel");
        provider.addTranslation("exterior.ait.geometric_blue", "Biscay");
        provider.addTranslation("exterior.ait.geometric_green", "War Games");
        provider.addTranslation("exterior.ait.green", "Green");
        provider.addTranslation("exterior.ait.red", "Red");
        provider.addTranslation("exterior.ait.yellow", "Yellow");

        // Police box specific
        provider.addTranslation("exterior.ait.renaissance", "Renaissance");
        provider.addTranslation("exterior.ait.coral", "Coral");
        provider.addTranslation("exterior.ait.futuristic", "Futuristic");
        provider.addTranslation("exterior.ait.cherrywood", "Cherrywood");
        provider.addTranslation("exterior.ait.yard", "73 Yards");
        provider.addTranslation("exterior.ait.tokamak", "Tokamak");
        provider.addTranslation("exterior.ait.black_mesa", "Black Mesa");
        provider.addTranslation("exterior.ait.aperture_science", "Aperture Science");

        // Classic specific
        provider.addTranslation("exterior.ait.definitive", "Definitive");
        provider.addTranslation("exterior.ait.hudolin", "Hudolin");
        provider.addTranslation("exterior.ait.exile", "Exile");
        provider.addTranslation("exterior.ait.prime", "Prime");
        provider.addTranslation("exterior.ait.ptored", "PTORed");
        provider.addTranslation("exterior.ait.shalka", "Shalka");
        provider.addTranslation("exterior.ait.mint", "Mint");
        provider.addTranslation("exterior.ait.yeti", "Yeti");
        provider.addTranslation("exterior.ait.tango", "Tango");
        provider.addTranslation("exterior.ait.falloutwho", "Fallout Who");

        // Renegade specific
        provider.addTranslation("exterior.ait.cabinet", "Cabinet");
        provider.addTranslation("exterior.ait.tron", "Tron");
        provider.addTranslation("exterior.ait.rotestor", "Rotestor");

        // Booth specific
        provider.addTranslation("exterior.ait.blue", "Blue");
        provider.addTranslation("exterior.ait.vintage", "Vintage");
        provider.addTranslation("exterior.ait.white", "White");

        // Stallion
        provider.addTranslation("exterior.ait.bt", "BT");
        provider.addTranslation("exterior.ait.stallion_pristine", "Pristine");
        provider.addTranslation("exterior.ait.stallion_green", "Green");

        // Frooploof Police Box
        provider.addTranslation("exterior.frooploof.copper", "Copper");
        provider.addTranslation("exterior.frooploof.eleven_toyota", "Toyota (1)");
        provider.addTranslation("exterior.frooploof.eleven_toyota_alternate", "Toyota (2)");
        provider.addTranslation("exterior.frooploof.toyota_alternate", "Toyota (3)");
        provider.addTranslation("exterior.frooploof.toyota_memorial", "Toyota (Memorial)");
        provider.addTranslation("exterior.frooploof.coral_alternate", "Coral (Alt)");
        provider.addTranslation("exterior.frooploof.coral_bad_wolf", "Coral (Bad Wolf)");
        provider.addTranslation("exterior.frooploof.coral_war", "War");
        provider.addTranslation("exterior.frooploof.tokamak_eotd", "Tokamak (EOTD)");

        // Dalek Mod

        provider.addTranslation("exterior.ait.1963", "1963");
        provider.addTranslation("exterior.ait.1967", "1967");
        provider.addTranslation("exterior.ait.1970", "1970");
        provider.addTranslation("exterior.ait.1976", "1976");
        provider.addTranslation("exterior.ait.1980", "1980");



        // Security Settings Menu
        provider.addTranslation("screen.ait.sonic.button", "> Sonic Settings");
        provider.addTranslation("screen.ait.sonicsettings.back", "Back");
        provider.addTranslation("screen.ait.gravity", "> Gravity: %s");
        provider.addTranslation("screen.ait.interor_select.title", "Interior Select");
        provider.addTranslation("screen.ait.security.leave_behind", "> Leave Behind");
        provider.addTranslation("screen.ait.security.hostile_alarms", "> Hostile Alarms");
        provider.addTranslation("screen.ait.security.minimum_loyalty", "> Isomorphic LVL");
        provider.addTranslation("screen.ait.security.receive_distress_calls", "> Receive Distress Calls");
        provider.addTranslation("tardis.message.interiorchange.not_enough_fuel",
                "The TARDIS does not have enough fuel to change it's interior");
        provider.addTranslation("tardis.message.interiorchange.warning",
                "ARS initiated, console room is being reconfigured, please vacate the interior!");
        provider.addTranslation("tardis.message.interiorchange.subsystems_enabled",
                "TARDIS has %s subsystems enabled. Are you sure you want to do this?");

        // Landing Pad
        provider.addTranslation("message.ait.landingpad.adjust", "Your landing position has been adjusted");

        // Commands
        provider.addTranslation("command.ait.realworld.responses", "Spawned a real world TARDIS at: ");
        provider.addTranslation("command.ait.riftchunk.cannotsetlevel",
                "This chunk is not a rift chunk, so you can't set the artron levels of it");
        provider.addTranslation("command.ait.riftchunk.setlevel", "Set artron levels in rift chunk to: %s");
        provider.addTranslation("command.ait.riftchunk.cannotgetlevel",
                "This chunk is not a rift chunk, so you can't get the artron levels of it");
        provider.addTranslation("command.ait.riftchunk.getlevel", "AU in rift chunk: %s");
        provider.addTranslation("command.ait.data.get", "Value %s is set to '%s'");
        provider.addTranslation("command.ait.data.set", "Set value %s to '%s'");
        provider.addTranslation("command.ait.data.fail",
                "Can't get value of a property named %s, because component %s is not keyed!");

        // Rift Chunk Tracking
        provider.addTranslation("riftchunk.ait.tracking", "Rift Tracking");
        provider.addTranslation("riftchunk.ait.cooldown", "Rift tracking is on cooldown");
        provider.addTranslation("waypoint.position.tooltip", "Position");
        provider.addTranslation("waypoint.dimension.tooltip", "Dimension");
        provider.addTranslation("waypoint.direction.tooltip", "Direction");

        // Blueprint Item
        provider.addTranslation("ait.blueprint.tooltip", "Blueprint: ");

        // Directions
        provider.addTranslation("direction.north", "North");
        provider.addTranslation("direction.north_east", "North East");
        provider.addTranslation("direction.north_west", "North West");
        provider.addTranslation("direction.south", "South");
        provider.addTranslation("direction.south_east", "South East");
        provider.addTranslation("direction.south_west", "South West");
        provider.addTranslation("direction.east", "East");
        provider.addTranslation("direction.west", "West");

        // keybinds
        provider.addTranslation("category.ait.main", "Adventures in Time");
        provider.addTranslation("key.ait.snap", "Open/Close/Lock Doors (KEY/RWF)");
        provider.addTranslation("key.ait.increase_speed", "Increase Speed (RWF)");
        provider.addTranslation("key.ait.decrease_speed", "Decrease Speed (RWF)");
        provider.addTranslation("key.ait.toggle_antigravs", "Toggle Antigravs (RWF)");

        // effects
        provider.addTranslation("effect.ait.zeiton_high", "Zeiton High");
        provider.addTranslation("effect.ait.lunar_regolith", "Lunar Regolith Poisoned");
        provider.addTranslation("effect.ait.oxygenated", "Oxygenator Field");

        return provider;
    }

    /**
     * Adds French translations to the language file.
     *
     * @param output           The data generator output.
     * @param registriesFuture The registries future.
     * @param languageType     The language type.
     * @return The AmbleLanguageProvider.
     */
    public AmbleLanguageProvider addFrenchTranslations(FabricDataOutput output,
                                                     CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture, LanguageType languageType) {
        AmbleLanguageProvider provider = new AmbleLanguageProvider(output, languageType);

        provider.addTranslation(AITItemGroups.MAIN, "Adventures In Time");
        provider.addTranslation(AITItems.TARDIS_ITEM, "TARDIS");
        provider.addTranslation(AITBlocks.DOOR_BLOCK, "Porte");
        provider.addTranslation(AITBlocks.CONSOLE, "Console");
        provider.addTranslation(AITItems.IRON_KEY, "Clé en Fer");
        provider.addTranslation(AITItems.GOLD_KEY, "Clé en Or");
        provider.addTranslation(AITItems.NETHERITE_KEY, "Clé en Netherite");
        provider.addTranslation(AITItems.CLASSIC_KEY, "Clé Classique");
        provider.addTranslation(AITItems.REMOTE_ITEM, "Télécommande Stattenheim");
        provider.addTranslation(AITItems.ARTRON_COLLECTOR, "Collecteur d’Artron ");
        provider.addTranslation(AITItems.RIFT_SCANNER, "Scanneur de Faille");
        provider.addTranslation(AITItems.SONIC_SCREWDRIVER, "Tournevis Sonique");
        // provider.addTranslation(AITItems.RENAISSANCE_SONIC_SCREWDRIVER,
        // "Tournevis
        // Sonique
        // Renaissance");
        // provider.addTranslation(AITItems.CORAL_SONIC_SCREWDRIVER,
        // "Tournevis Sonique
        // Coral");
        provider.addTranslation(AITItems.GOLD_KEY_UPGRADE_SMITHING_TEMPLATE, "Modèle de forge");
        provider.addTranslation(AITItems.NETHERITE_KEY_UPGRADE_SMITHING_TEMPLATE, "Modèle de forge");
        provider.addTranslation(AITItems.CLASSIC_KEY_UPGRADE_SMITHING_TEMPLATE, "Modèle de forge");
        provider.addTranslation(AITBlocks.EXTERIOR_BLOCK, "Exterieur");
        provider.addTranslation(AITBlocks.CORAL_PLANT, "Corail TARDIS");
        provider.addTranslation("death.attack.tardis_squash", "%1$s a été écrasé(e) par un TARDIS!");
        provider.addTranslation("message.ait.riftscanner.info1", "Information sur le Chunk à Artron: ");
        provider.addTranslation("message.ait.riftscanner.info2", "Artron laissé dans le chunk: ");
        provider.addTranslation("message.ait.riftscanner.info3", "Ceci n'est pas un chunk à faille");
        provider.addTranslation("tooltip.ait.remoteitem.holdformoreinfo",
                "Maintenez la touche shift pour plus d'informations");
        provider.addTranslation("tardis.message.control.protocol_116.active", "Protocole 116: ACTIF");
        provider.addTranslation("tardis.message.control.protocol_116.inactive", "Protocole 116: INACTIF");
        provider.addTranslation("message.ait.remoteitem.warning1",
                "Le TARDIS n’a plus de carburant et ne peux plus se dématérialiser");
        provider.addTranslation("message.ait.remoteitem.warning2",
                "Le TARDIS est en train de se recharger et est incapable de se dématérialiser");
        provider.addTranslation("message.ait.remoteitem.warning3",
                "Impossible de passer de la dimension extérieure à la dimension intérieure");
        provider.addTranslation("tooltip.ait.remoteitem.notardis",
                "La télécommande n’est pas connecté avec le TARDIS");
        provider.addTranslation("tardis.message.control.antigravs.active", "Antigravs: ACTIF");
        provider.addTranslation("tardis.message.control.antigravs.inactive", "Antigravs: INACTIF");
        provider.addTranslation("message.ait.tardis.control.dimension.info", "Dimension: ");
        provider.addTranslation("tardis.message.control.fast_return.destination_nonexistent",
                "Retour Rapide: Dernière position inexistante!");
        provider.addTranslation("tardis.message.control.fast_return.last_position",
                " Retour Rapide: DERNIÈRE POSITION DÉFINIE");
        provider.addTranslation("tardis.message.control.fast_return.current_position",
                "Fast Return: POSITION ACTUELLE DÉFINIE");
        provider.addTranslation("tardis.message.control.protocol_813.active", "Protocole 813: ACTIF");
        provider.addTranslation("tardis.message.control.protocol_813.inactive", "Protocole 813: INACTF");
        provider.addTranslation("tardis.message.control.handbrake.on", "Frein à main: ON");
        provider.addTranslation("tardis.message.control.handbrake.off", "Frein à main: OFF");
        provider.addTranslation("tardis.message.control.randomiser.destination", "Destination: ");
        provider.addTranslation("tardis.message.control.siege.enabled", "Siége: Activé");
        provider.addTranslation("tardis.message.control.siege.enabled", "Siége: Désactivé");
        provider.addTranslation("tardis.message.control.refueler.enabled", "Rechargement: Activé");
        provider.addTranslation("tardis.message.control.refueler.disabled", "Rechargement: Désactivé");
        provider.addTranslation("tardis.message.destination_biome", "Biome de Destination: ");
        provider.addTranslation("tardis.message.control.increment.info", "Incrément: ");
        provider.addTranslation("tardis.message.control.randomiser.poscontrol", "Destination: ");
        provider.addTranslation("command.ait.riftchunk.isariftchunk", "Ceci est un chunk à faille");
        provider.addTranslation("command.ait.riftchunk.isnotariftchunk", "Ceci n’est pas un chunk à faille");
        provider.addTranslation("message.ait.sonic.riftfound", "CHUNK À FAILLE TROUVÉ");
        provider.addTranslation("message.ait.sonic.riftnotfound", "CHUNK À FAILLE NON TROUVÉ");
        provider.addTranslation("message.ait.sonic.handbrakedisengaged",
                "Frein à main desserré, destination définie à la position actuelle");
        provider.addTranslation("message.ait.sonic.mode", "Mode: ");
        provider.addTranslation("message.ait.sonic.none", "Aucun");
        provider.addTranslation("message.ait.remoteitem.warning4",
                "La cible a été réinitialisée et mise à jour, l'appareil est maintenant orienté vers votre nouvelle cible");
        provider.addTranslation("message.ait.keysmithing.upgrade", "Amélioration");
        provider.addTranslation("message.ait.keysmithing.key", "Type de Clé: ");
        provider.addTranslation("message.ait.keysmithing.ingredient", "Matériau: ");
        provider.addTranslation("message.ait.control.ylandtype", "Recherche Surface Mode: %s");
        provider.addTranslation("message.ait.control.xlandtype.on", "Horizontal Surface Search: ON");
        provider.addTranslation("message.ait.control.xlandtype.off", "Horizontal Surface Search: OFF");

        provider.addTranslation("tooltip.ait.key.notardis", "La clé ne s’identifie avec aucun TARDIS");
        //
        provider.addTranslation("tardis.message.control.hads.alarm_enabled", "Alarms: Enabled");
        provider.addTranslation("tardis.message.control.hads.alarms_disabled", "Alarms: Disabled");
        provider.addTranslation("screen.ait.monitor.desktop_settings", "Desktop Settings");
        provider.addTranslation("screen.ait.monitor.apply", "Apply");
        provider.addTranslation("screen.ait.monitor.fuel", "Fuel: ");
        provider.addTranslation("screen.ait.interiorsettings.title", "Interior Settings");
        provider.addTranslation("screen.ait.interiorsettings.back", "> Back");
        provider.addTranslation("screen.ait.interiorsettings.changeinterior", "> Change Interior");
        provider.addTranslation("screen.ait.interior.settings.hum", "HUMS");
        provider.addTranslation("screen.ait.interior.settings.coral", "Coral");
        provider.addTranslation("screen.ait.interior.settings.toyota", "Toyota");
        provider.addTranslation("screen.ait.interor_select.title", "Interior Select");
        provider.addTranslation("tardis.message.interiorchange.not_enough_fuel",
                "The TARDIS does not have enough fuel to change it's interior");
        provider.addTranslation("tardis.message.interiorchange.warning",
                "Interior reconfiguration started! Please leave the interior.");
        provider.addTranslation("command.ait.realworld.responses", "Spawned a real world TARDIS at: ");

        return provider;
    }

    /**
     * Adds Spanish translations to the language file.
     *
     * @param output           The data generator output.
     * @param registriesFuture The registries future.
     * @param languageType     The language type.
     * @return The AmbleLanguageProvider.
     */
    public AmbleLanguageProvider addSpanishTranslations(FabricDataOutput output,
                                                      CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture, LanguageType languageType) {
        AmbleLanguageProvider provider = new AmbleLanguageProvider(output, languageType);

        provider.addTranslation(AITItemGroups.MAIN, "Adventures In Time");
        provider.addTranslation(AITItems.TARDIS_ITEM, "TARDIS");
        provider.addTranslation(AITBlocks.DOOR_BLOCK, "Door");
        provider.addTranslation(AITBlocks.CONSOLE, "Console");
        provider.addTranslation(AITItems.IRON_KEY, "Iron Key");
        provider.addTranslation(AITItems.GOLD_KEY, "Gold Key");
        provider.addTranslation(AITItems.NETHERITE_KEY, "Netherite Key");
        provider.addTranslation(AITItems.CLASSIC_KEY, "Classic Key");
        provider.addTranslation(AITItems.REMOTE_ITEM, "Stattenheim Remote");
        provider.addTranslation(AITItems.ARTRON_COLLECTOR, "Artron Collector");
        provider.addTranslation(AITItems.RIFT_SCANNER, "escáner de Rift");
        provider.addTranslation(AITItems.SONIC_SCREWDRIVER, "Sonic Screwdriver");
        provider.addTranslation(AITItems.GOLD_KEY_UPGRADE_SMITHING_TEMPLATE, "Smithing Template");
        provider.addTranslation(AITItems.NETHERITE_KEY_UPGRADE_SMITHING_TEMPLATE, "Smithing Template");
        provider.addTranslation(AITItems.CLASSIC_KEY_UPGRADE_SMITHING_TEMPLATE, "Smithing Template");
        provider.addTranslation(AITBlocks.EXTERIOR_BLOCK, "Exterior");
        provider.addTranslation(AITBlocks.CORAL_PLANT, "TARDIS Coral");
        provider.addTranslation("death.attack.tardis_squash", "%1$s got squashed by a TARDIS!");
        provider.addTranslation("message.ait.riftscanner.info1", "Artron Chunk Info: ");
        provider.addTranslation("message.ait.riftscanner.info2", "Artron left in chunk: ");
        provider.addTranslation("message.ait.riftscanner.info3", "This is not a rift chunk");
        provider.addTranslation("tooltip.ait.remoteitem.holdformoreinfo", "Hold shift for more info");
        provider.addTranslation("tardis.message.control.protocol_116.active", "Protocol 116: ACTIVE");
        provider.addTranslation("tardis.message.control.protocol_116.inactive", "Protocol 116: INACTIVE");
        provider.addTranslation("message.ait.remoteitem.warning1",
                "The TARDIS is out of fuel and cannot dematerialise");
        provider.addTranslation("message.ait.remoteitem.warning2",
                "The TARDIS is refueling and is unable to dematerialise");
        provider.addTranslation("message.ait.remoteitem.warning3",
                "Cannot translocate exterior to interior dimension");
        provider.addTranslation("tooltip.ait.remoteitem.notardis",
                "Remote does not identify with any TARDIS");
        provider.addTranslation("tardis.message.control.antigravs.active", "Antigravs: ACTIVE");
        provider.addTranslation("tardis.message.control.antigravs.inactive", "Antigravs: INACTIVE");
        provider.addTranslation("message.ait.tardis.control.dimension.info", "Dimension: ");
        provider.addTranslation("tardis.message.control.fast_return.destination_nonexistent",
                "Fast Return: Last Position Nonexistent!");
        provider.addTranslation("tardis.message.control.fast_return.last_position",
                "Fast Return: LAST POSITION SET");
        provider.addTranslation("tardis.message.control.fast_return.current_position",
                "Fast Return: CURRENT POSITION SET");
        provider.addTranslation("tardis.message.control.protocol_813.active", "Protocol 813: ACTIVE");
        provider.addTranslation("tardis.message.control.protocol_813.inactive", "Protocol 813: INACTIVE");
        provider.addTranslation("tardis.message.control.handbrake.on", "handbrake: ON");
        provider.addTranslation("tardis.message.control.handbrake.off", "handbrake: OFF");
        provider.addTranslation("tardis.message.control.landtype.on", "Ground Searching: ON");
        provider.addTranslation("tardis.message.control.landtype.off", "Ground Searching: OFF");
        provider.addTranslation("tardis.message.control.randomiser.destination", "Destination: ");
        provider.addTranslation("tardis.message.control.refueler.enabled", "Refueling: Enabled");
        provider.addTranslation("tardis.message.control.refueler.disabled", "Refueling: Disabled");
        provider.addTranslation("tardis.message.destination_biome", "Destination Biome: ");
        provider.addTranslation("tardis.message.control.increment.info", "Increment: ");
        provider.addTranslation("tardis.message.control.randomiser.poscontrol", "Destination: ");
        provider.addTranslation("message.ait.sonic.riftfound", "RIFT CHUNK FOUND");
        provider.addTranslation("message.ait.sonic.riftnotfound", "RIFT CHUNK NOT FOUND");
        provider.addTranslation("message.ait.sonic.handbrakedisengaged",
                "Handbrake disengaged, destination set to current position");
        provider.addTranslation("message.ait.sonic.mode", "Mode: ");
        provider.addTranslation("message.ait.sonic.none", "None");
        provider.addTranslation("message.ait.sonic.currenttype", "Current Casing: ");
        provider.addTranslation("message.ait.remoteitem.warning4",
                "Target has been reset and updated, the device is now pointing towards your new target");
        provider.addTranslation("message.ait.keysmithing.upgrade", "Upgrade");
        provider.addTranslation("message.ait.keysmithing.key", "Key Type: ");
        provider.addTranslation("message.ait.keysmithing.ingredient", "Material: ");
        provider.addTranslation("tooltip.ait.key.notardis", "Key does not identify with any TARDIS");
        provider.addTranslation("tardis.message.control.hads.alarm_enabled", "Alarms: Enabled");
        provider.addTranslation("tardis.message.control.hads.alarms_disabled", "Alarms: Disabled");
        provider.addTranslation("screen.ait.monitor.desktop_settings", "Desktop Settings");
        provider.addTranslation("screen.ait.monitor.apply", "Apply");
        provider.addTranslation("screen.ait.monitor.fuel", "Fuel: ");
        provider.addTranslation("screen.ait.interiorsettings.title", "Interior Settings");
        provider.addTranslation("screen.ait.interiorsettings.back", "> Back");
        provider.addTranslation("screen.ait.interiorsettings.changeinterior", "> Change Interior");
        provider.addTranslation("screen.ait.interior.settings.hum", "HUMS");
        provider.addTranslation("screen.ait.interior.settings.coral", "Coral");
        provider.addTranslation("screen.ait.interior.settings.toyota", "Toyota");
        provider.addTranslation("screen.ait.interor_select.title", "Interior Select");
        provider.addTranslation("tardis.message.interiorchange.not_enough_fuel",
                "The TARDIS does not have enough fuel to change it's interior");
        provider.addTranslation("tardis.message.interiorchange.warning",
                "Interior reconfiguration started! Please leave the interior.");
        provider.addTranslation("command.ait.realworld.responses", "Spawned a real world TARDIS at: ");

        return provider;
    }

    public AmbleLanguageProvider addGermanTranslations(FabricDataOutput output,
                                                     CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture, LanguageType languageType) {
        AmbleLanguageProvider provider = new AmbleLanguageProvider(output, languageType);

        provider.addTranslation(AITItemGroups.MAIN, "Abenteuer in der Zeit");
        provider.addTranslation(AITItems.TARDIS_ITEM, "TARDIS");
        provider.addTranslation(AITBlocks.DOOR_BLOCK, "Tür");
        provider.addTranslation(AITBlocks.CONSOLE, "Konsole");
        provider.addTranslation(AITItems.IRON_KEY, "Eiserner Schlüssel");
        provider.addTranslation(AITItems.GOLD_KEY, "Goldener Schlüssel");
        provider.addTranslation(AITItems.NETHERITE_KEY, "Netherite Schlüssel");
        provider.addTranslation(AITItems.CLASSIC_KEY, "Klassischer Schlüssel");
        provider.addTranslation(AITItems.REMOTE_ITEM, "Stattenheim Fernbedienung");
        provider.addTranslation(AITItems.ARTRON_COLLECTOR, "Artronsammler");
        provider.addTranslation(AITItems.RIFT_SCANNER, "Riss-Scanner");
        provider.addTranslation(AITItems.SONIC_SCREWDRIVER, "Schallschraubenzieher");
        // provider.addTranslation(AITItems.RENAISSANCE_SONIC_SCREWDRIVER,
        // "Renaissance
        // Schallschraubenzieher");
        // provider.addTranslation(AITItems.CORAL_SONIC_SCREWDRIVER,
        // "Korallen
        // Schallschraubenzieher");
        provider.addTranslation(AITItems.GOLD_KEY_UPGRADE_SMITHING_TEMPLATE, "Schmiedevorlage");
        provider.addTranslation(AITItems.NETHERITE_KEY_UPGRADE_SMITHING_TEMPLATE, "Schmiedevorlage");
        provider.addTranslation(AITItems.CLASSIC_KEY_UPGRADE_SMITHING_TEMPLATE, "Schmiedevorlage");
        provider.addTranslation(AITBlocks.EXTERIOR_BLOCK, "Äußere Hülle");
        provider.addTranslation(AITBlocks.CORAL_PLANT, "TARDIS Koralle");
        provider.addTranslation("death.attack.tardis_squash", "%1$s wurde von einer TARDIS zerquetscht!");
        provider.addTranslation("message.ait.riftscanner.info1", "Artron Chunk Info: ");
        provider.addTranslation("message.ait.riftscanner.info2", "Artron noch im Chunk: ");
        provider.addTranslation("message.ait.riftscanner.info3", "Dies ist kein Riss-Chunk");
        provider.addTranslation("tooltip.ait.remoteitem.holdformoreinfo",
                "Shift halten für weitere Informationen");
        provider.addTranslation("tardis.message.control.protocol_116.active", "Protokoll 116: AKTIV");
        provider.addTranslation("tardis.message.control.protocol_116.inactive", "Protocol 116: INACTIVE");
        provider.addTranslation("message.ait.remoteitem.warning1",
                "Die TARDIS benötigt Treibstoff und kann sich nicht dematerialisieren");
        provider.addTranslation("message.ait.remoteitem.warning2",
                "Die TARDIS tankt und kann sich nicht dematerialisieren");
        provider.addTranslation("message.ait.remoteitem.warning3",
                "Äußere Hülle kann nicht in die innere Dimension verschoben werden");
        provider.addTranslation("tooltip.ait.remoteitem.notardis",
                "Fernbedienung identifiziert sich mit keiner TARDIS");
        provider.addTranslation("tardis.message.control.antigravs.active", "Antigravitation: AKTIVIERT");
        provider.addTranslation("tardis.message.control.antigravs.inactive", "Antigravitation: DEAKTIVIERT");
        provider.addTranslation("message.ait.tardis.control.dimension.info", "Dimension: ");
        provider.addTranslation("tardis.message.control.fast_return.destination_nonexistent",
                "Rückreise: Letzte Position existiert nicht!");
        provider.addTranslation("tardis.message.control.fast_return.last_position",
                "Rückreise: LETZTE POSITION GESETZT");
        provider.addTranslation("tardis.message.control.fast_return.current_position",
                "Rückreise: JETZIGE POSITION GESETZT");
        provider.addTranslation("tardis.message.control.protocol_813.active", "Protokoll 813: AKTIV");
        provider.addTranslation("tardis.message.control.protocol_813.inactive", "Protocol 813: INACTIVE");
        provider.addTranslation("tardis.message.control.handbrake.on", "Handbremse: AN");
        provider.addTranslation("tardis.message.control.handbrake.off", "Handbremse: AUS");
        provider.addTranslation("tardis.message.control.landtype.on", "Bodensuche: AN");
        provider.addTranslation("tardis.message.control.landtype.off", "Bodensuche: AUS");
        provider.addTranslation("tardis.message.control.randomiser.destination", "Zielort: ");
        provider.addTranslation("tardis.message.control.refueler.enabled", "Tanken: Aktiviert");
        provider.addTranslation("tardis.message.control.refueler.disabled", "Tanken: Deaktiviert");
        provider.addTranslation("tardis.message.destination_biome", "Zielbiom: ");
        provider.addTranslation("tardis.message.control.increment.info", "Steigerung: ");
        provider.addTranslation("tardis.message.control.randomiser.poscontrol", "Zielort: ");
        provider.addTranslation("message.ait.sonic.riftfound", "RIFT-CHUNK GEFUNDEN");
        provider.addTranslation("message.ait.sonic.riftnotfound", "KEIN RIFT-CHUNK GEFUNDEN");
        provider.addTranslation("message.ait.sonic.handbrakedisengaged",
                "Handbremse deaktiviert, Koordinaten auf jetzige Position gesetzt");
        provider.addTranslation("message.ait.sonic.mode", "Modus: ");
        provider.addTranslation("message.ait.sonic.none", "Keiner");
        provider.addTranslation("message.ait.remoteitem.warning4",
                "Ziel wurde zurückgesetzt und aktualisiert, das Gerät zeigt nun in Richtung des neuen Ziels");
        provider.addTranslation("message.ait.keysmithing.upgrade", "Upgrade");
        provider.addTranslation("message.ait.keysmithing.key", "Schlüsseltyp: ");
        provider.addTranslation("message.ait.keysmithing.ingredient", "Material: ");
        provider.addTranslation("tooltip.ait.key.notardis",
                "Schlüssel identifiziert sich mit keiner TARDIS");
        //
        provider.addTranslation("tardis.message.control.hads.alarm_enabled", "Alarms: Enabled");
        provider.addTranslation("tardis.message.control.hads.alarms_disabled", "Alarms: Disabled");
        provider.addTranslation("screen.ait.monitor.desktop_settings", "Desktop Settings");
        provider.addTranslation("screen.ait.monitor.apply", "Apply");
        provider.addTranslation("screen.ait.monitor.fuel", "Fuel: ");
        provider.addTranslation("screen.ait.interiorsettings.title", "Interior Settings");
        provider.addTranslation("screen.ait.interiorsettings.back", "> Back");
        provider.addTranslation("screen.ait.interiorsettings.changeinterior", "> Change Interior");
        provider.addTranslation("screen.ait.interior.settings.hum", "HUMS");
        provider.addTranslation("screen.ait.interior.settings.coral", "Coral");
        provider.addTranslation("screen.ait.interior.settings.toyota", "Toyota");
        provider.addTranslation("screen.ait.interor_select.title", "Interior Select");
        provider.addTranslation("tardis.message.interiorchange.not_enough_fuel",
                "The TARDIS does not have enough fuel to change it's interior");
        provider.addTranslation("tardis.message.interiorchange.warning",
                "Interior reconfiguration started! Please leave the interior.");
        provider.addTranslation("command.ait.realworld.responses", "Spawned a real world TARDIS at:");

        return provider;
    }

    public AmbleLanguageProvider addPortugueseTranslations(FabricDataOutput output,
                                                         CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture, LanguageType languageType) {
        AmbleLanguageProvider provider = new AmbleLanguageProvider(output, languageType);
        return provider;
    }

    public void generate_DE_AT_Language(FabricDataGenerator.Pack pack) {
        pack.addProvider(
                ((output, registriesFuture) -> addGermanTranslations(output, registriesFuture, LanguageType.DE_AT))); // de_at
        // (German
        // Austria)
    }

    public void generate_DE_CH_Language(FabricDataGenerator.Pack pack) {
        pack.addProvider(
                ((output, registriesFuture) -> addGermanTranslations(output, registriesFuture, LanguageType.DE_CH))); // de_ch
        // (German
        // Switzerland)
    }

    public void generate_DE_DE_Language(FabricDataGenerator.Pack pack) {
        pack.addProvider(
                ((output, registriesFuture) -> addGermanTranslations(output, registriesFuture, LanguageType.DE_DE))); // de_de
        // (German
        // Germany)
    }

    public void generate_NDS_DE_Language(FabricDataGenerator.Pack pack) {
        pack.addProvider(
                ((output, registriesFuture) -> addGermanTranslations(output, registriesFuture, LanguageType.NDS_DE))); // nds_de
        // (Nordic
        // German)
    }

    public void generate_EN_US_Language(FabricDataGenerator.Pack pack) {
        pack.addProvider(
                ((output, registriesFuture) -> addEnglishTranslations(output, registriesFuture, LanguageType.EN_US))); // en_us
        // (English
        // US)
    }

    public void generate_EN_UK_Language(FabricDataGenerator.Pack pack) {
        pack.addProvider(
                ((output, registriesFuture) -> addEnglishTranslations(output, registriesFuture, LanguageType.EN_UK))); // en_uk
        // (English
        // UK)
    }

    public void generate_FR_CA_Language(FabricDataGenerator.Pack pack) {
        pack.addProvider(
                (((output, registriesFuture) -> addFrenchTranslations(output, registriesFuture, LanguageType.FR_CA)))); // fr_ca
        // (French
        // Canadian)
    }

    public void generate_FR_FR_Language(FabricDataGenerator.Pack pack) {
        pack.addProvider(
                (((output, registriesFuture) -> addFrenchTranslations(output, registriesFuture, LanguageType.FR_FR)))); // fr_fr
        // (French
        // France)
    }

    public void generate_ES_AR_Language(FabricDataGenerator.Pack pack) {
        pack.addProvider(
                (((output, registriesFuture) -> addSpanishTranslations(output, registriesFuture, LanguageType.ES_AR)))); // es_ar
        // (Spanish
        // Argentina)
    }

    public void generate_ES_CL_Language(FabricDataGenerator.Pack pack) {
        pack.addProvider(
                (((output, registriesFuture) -> addSpanishTranslations(output, registriesFuture, LanguageType.ES_CL)))); // es_cl
        // (Spanish
        // Chile)
    }

    public void generate_ES_EC_Language(FabricDataGenerator.Pack pack) {
        pack.addProvider(
                (((output, registriesFuture) -> addSpanishTranslations(output, registriesFuture, LanguageType.ES_EC)))); // es_ec
        // (Spanish
        // Ecuador)
    }

    public void generate_ES_ES_Language(FabricDataGenerator.Pack pack) {
        pack.addProvider(
                (((output, registriesFuture) -> addSpanishTranslations(output, registriesFuture, LanguageType.ES_ES)))); // es_es
        // (Spanish
        // Spain)
    }

    public void generate_ES_MX_Language(FabricDataGenerator.Pack pack) {
        pack.addProvider(
                (((output, registriesFuture) -> addSpanishTranslations(output, registriesFuture, LanguageType.ES_MX)))); // es_mx
        // (Spanish
        // Mexico)
    }

    public void generate_ES_UY_Language(FabricDataGenerator.Pack pack) {
        pack.addProvider(
                (((output, registriesFuture) -> addSpanishTranslations(output, registriesFuture, LanguageType.ES_UY)))); // es_uy
        // (Spanish
        // Uruguay)
    }

    public void generate_ES_VE_Language(FabricDataGenerator.Pack pack) {
        pack.addProvider(
                (((output, registriesFuture) -> addSpanishTranslations(output, registriesFuture, LanguageType.ES_VE)))); // es_ve
        // (Spanish
        // Venezuela)
    }

    public void generate_EN_AU_Language(FabricDataGenerator.Pack pack) {
        pack.addProvider(
                ((output, registriesFuture) -> addEnglishTranslations(output, registriesFuture, LanguageType.EN_AU))); // en_au
        // (English
        // Australia)
    }

    public void generate_EN_CA_Language(FabricDataGenerator.Pack pack) {
        pack.addProvider(
                ((output, registriesFuture) -> addEnglishTranslations(output, registriesFuture, LanguageType.EN_CA))); // en_ca
        // (English
        // Canada)
    }

    public void generate_EN_GB_Language(FabricDataGenerator.Pack pack) {
        pack.addProvider(
                ((output, registriesFuture) -> addEnglishTranslations(output, registriesFuture, LanguageType.EN_GB))); // en_gb
        // (English
        // Great
        // Britain)
    }

    public void generate_EN_NZ_Language(FabricDataGenerator.Pack pack) {
        pack.addProvider(
                ((output, registriesFuture) -> addEnglishTranslations(output, registriesFuture, LanguageType.EN_NZ))); // en_nz
        // (English
        // New
        // Zealand)
    }

    public void generate_PT_BR_Language(FabricDataGenerator.Pack pack) {
        pack.addProvider(((output, registriesFuture) -> addPortugueseTranslations(output, registriesFuture,
                LanguageType.PT_BR))); // pt_br (Portuguese Brazil)
    }

    public void generate_RU_RU_Language(FabricDataGenerator.Pack pack) {
        pack.addProvider(((output, registriesFuture) -> new AmbleLanguageProvider(output, LanguageType.RU_RU))); // ru_ru
        // (Russian
        // Russia)
    }

    public void generate_UK_UA_Language(FabricDataGenerator.Pack pack) {
        pack.addProvider(((output, registriesFuture) -> new AmbleLanguageProvider(output, LanguageType.UK_UA))); // uk_ua
        // (Ukrainian
        // Ukraine)
    }
}
