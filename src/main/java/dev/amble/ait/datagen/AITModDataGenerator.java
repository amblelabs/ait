package dev.amble.ait.datagen;

import static dev.amble.ait.core.AITItems.isUnlockedOnThisDay;
import static net.minecraft.data.server.recipe.RecipeProvider.*;

import java.util.Calendar;
import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.data.server.recipe.SmithingTransformRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITEntityTypes;
import dev.amble.ait.core.AITItemGroups;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.datagen.datagen_providers.*;
import dev.amble.ait.datagen.datagen_providers.loot.AITBlockLootTables;
import dev.amble.ait.module.ModuleRegistry;
import dev.amble.ait.module.gun.core.entity.GunEntityTypes;
import dev.amble.ait.module.planet.core.PlanetBlocks;
import dev.amble.ait.module.planet.core.PlanetItems;
import dev.amble.ait.module.planet.core.world.PlanetConfiguredFeatures;
import dev.amble.ait.module.planet.core.world.PlanetPlacedFeatures;
import dev.amble.lib.datagen.lang.AmbleLanguageProvider;
import dev.amble.lib.datagen.lang.LanguageType;
import dev.amble.lib.datagen.sound.AmbleSoundProvider;

public class AITModDataGenerator implements DataGeneratorEntrypoint {

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

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITBlocks.TARDIS_CORAL_BLOCK, 1)
                    .pattern("##")
                    .pattern("##")
                    .input('#', AITItems.CORAL_FRAGMENT)
                    .criterion(hasItem(AITItems.CORAL_FRAGMENT), conditionsFromItem(AITItems.CORAL_FRAGMENT)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITBlocks.TARDIS_CORAL_SLAB, 6)
                    .pattern("###")
                    .input('#', AITBlocks.TARDIS_CORAL_BLOCK)
                    .criterion(hasItem(AITBlocks.TARDIS_CORAL_BLOCK), conditionsFromItem(AITBlocks.TARDIS_CORAL_BLOCK)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITBlocks.TARDIS_CORAL_STAIRS, 4)
                    .pattern("#  ")
                    .pattern("## ")
                    .pattern("###")
                    .input('#', AITBlocks.TARDIS_CORAL_BLOCK)
                    .criterion(hasItem(AITBlocks.TARDIS_CORAL_BLOCK), conditionsFromItem(AITBlocks.TARDIS_CORAL_BLOCK)));
            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITBlocks.TARDIS_CORAL_FAN, 3)
                    .pattern(" # ")
                    .pattern("###")
                    .pattern(" # ")
                    .input('#', AITItems.CORAL_FRAGMENT)
                    .criterion(hasItem(AITItems.CORAL_FRAGMENT), conditionsFromItem(AITItems.CORAL_FRAGMENT)));

            provider.addBlastFurnaceRecipe(CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(AITItems.ZEITON_SHARD),
                            RecipeCategory.MISC, AITItems.SUPERHEATED_ZEITON, 0.2f, 500)
                    .criterion(hasItem(AITItems.ZEITON_SHARD), conditionsFromItem(AITItems.ZEITON_SHARD)),
            new Identifier("ait", "superheated_zeiton_from_zeiton_shard_blasting"));

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

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITItems.PHOTON_ACCELERATOR, 1)
                    .pattern(" I ")
                    .pattern("IXI")
                    .pattern(" I ")
                    .input('I', Items.IRON_INGOT)
                    .input('X', Items.BLAZE_POWDER)
                    .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                    .criterion(hasItem(Items.BLAZE_POWDER), conditionsFromItem(Items.BLAZE_POWDER)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITItems.ORTHOGONAL_ENGINE_FILTER, 1)
                    .pattern("III")
                    .pattern("IBI")
                    .pattern("IXI")
                    .input('I', Items.IRON_INGOT)
                    .input('X', AITItems.SUPERHEATED_ZEITON)
                    .input('B', AITItems.ARTRON_COLLECTOR)
                    .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                    .criterion(hasItem(AITItems.SUPERHEATED_ZEITON), conditionsFromItem(AITItems.SUPERHEATED_ZEITON))
                    .criterion(hasItem(AITItems.ARTRON_COLLECTOR), conditionsFromItem(AITItems.ARTRON_COLLECTOR)));


            /*provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITItems.IRON_GOAT_HORN, 1)
                    .pattern("III")
                    .pattern("IBI")
                    .pattern("III")
                    .input('I', Items.IRON_INGOT)
                    .input('B', Items.GOAT_HORN)
                    .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                    .criterion(hasItem(Items.GOAT_HORN), conditionsFromItem(Items.GOAT_HORN)));*/

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
                    .input('T', AITItems.SUPERHEATED_ZEITON)
                    .input('W', Items.WATER_BUCKET)
                    .criterion(hasItem(Items.BLACKSTONE), conditionsFromItem(Items.BLACKSTONE))
                    .criterion(hasItem(Items.IRON_BARS), conditionsFromItem(Items.IRON_BARS))
                    .criterion(hasItem(Items.WATER_BUCKET), conditionsFromItem(Items.WATER_BUCKET))
                    .criterion(hasItem(AITItems.SUPERHEATED_ZEITON), conditionsFromItem(AITItems.SUPERHEATED_ZEITON)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITBlocks.GENERIC_SUBSYSTEM, 1)
                    .pattern("IBI")
                    .pattern("BSB")
                    .pattern("IBI")
                    .input('I', Items.IRON_INGOT)
                    .input('B', AITItems.SUPERHEATED_ZEITON)
                    .input('S', AITItems.ORTHOGONAL_ENGINE_FILTER)
                    .criterion(hasItem(Items.IRON_BLOCK), conditionsFromItem(Items.IRON_BLOCK))
                    .criterion(hasItem(AITItems.SUPERHEATED_ZEITON), conditionsFromItem(AITItems.SUPERHEATED_ZEITON))
                    .criterion(hasItem(AITItems.ORTHOGONAL_ENGINE_FILTER), conditionsFromItem(AITItems.ORTHOGONAL_ENGINE_FILTER)));

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

            provider.addShapedRecipe(
                    ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, AITItems.CONTROL_DISC, 1)
                            .pattern(" I ")
                            .pattern("IZI")
                            .pattern(" I ")
                            .input('I', Items.IRON_INGOT)
                            .input('Z', AITItems.CHARGED_ZEITON_CRYSTAL)
                            .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                            .criterion(hasItem(AITItems.CHARGED_ZEITON_CRYSTAL), conditionsFromItem(AITItems.CHARGED_ZEITON_CRYSTAL)));

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

            provider.addShapelessRecipe(ShapelessRecipeJsonBuilder.create(RecipeCategory.REDSTONE, AITBlocks.CABLE_CONNECTOR_BLOCK , 2)
                    .input(AITBlocks.CABLE_BLOCK, 4).criterion(hasItem(AITBlocks.CABLE_BLOCK), conditionsFromItem(AITBlocks.CABLE_BLOCK)));

            //provider.addShapelessRecipe(ShapelessRecipeJsonBuilder.create(RecipeCategory.REDSTONE, AITBlocks.CABLE_BLOCK, 2)
            //        .input(AITBlocks.CABLE_CONNECTOR_BLOCK ).criterion(hasItem(AITBlocks.CABLE_CONNECTOR_BLOCK ), conditionsFromItem(AITBlocks.CABLE_CONNECTOR_BLOCK )));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, AITItems.HYPERION_CORE_SHAFT)
                    .pattern("ACB")
                    .input('B', AITItems.SUPERHEATED_ZEITON).criterion(hasItem(AITItems.SUPERHEATED_ZEITON), conditionsFromItem(AITItems.SUPERHEATED_ZEITON))
                    .input('A', AITItems.PHOTON_ACCELERATOR).criterion(hasItem(AITItems.PHOTON_ACCELERATOR), conditionsFromItem(AITItems.PHOTON_ACCELERATOR))
                    .input('C', Items.IRON_INGOT).criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, AITItems.TRANSWARP_RESONATOR)
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
                    .input('S', AITItems.CHARGED_ZEITON_CRYSTAL)
                    .criterion(hasItem(AITItems.CHARGED_ZEITON_CRYSTAL), conditionsFromItem(AITItems.CHARGED_ZEITON_CRYSTAL))
                    .input('P', Items.PAINTING)
                    .criterion(hasItem(Items.PAINTING), conditionsFromItem(Items.PAINTING))
                    .input('B', Blocks.BLACK_WOOL)
                    .criterion(hasItem(Blocks.BLACK_WOOL), conditionsFromItem(Blocks.BLACK_WOOL)));

            provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, AITItems.TRENZALORE_PAINTING)
                    .pattern("OBO")
                    .pattern("BSB")
                    .pattern("OPO")
                    .input('O', Blocks.PURPLE_WOOL)
                    .criterion(hasItem(Blocks.PURPLE_WOOL), conditionsFromItem(Blocks.PURPLE_WOOL))
                    .input('S', AITItems.CHARGED_ZEITON_CRYSTAL)
                    .criterion(hasItem(AITItems.CHARGED_ZEITON_CRYSTAL), conditionsFromItem(AITItems.CHARGED_ZEITON_CRYSTAL))
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
        /*provider.addSmithingTransformRecipe(
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
                AITMod.id("classic_goat_horn_smithing"));*/
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

        provider.addTranslation("block.ait.matrix_energizer.needs_nether_star", "The energizer needs the power of the Wither...");
        provider.addTranslation("ait.tardis.likes_item", "The TARDIS may like this item...");
        provider.addTranslation("ait.charged_zeiton_crystal.not_max_fuel", "Crystal doesn't have enough fuel!");
        provider.addTranslation("tooltip.ait.remoteitem.holdformoreinfo", "Hold shift for more info");

        provider.addTranslation("ait.control_disc.set_position", "Write to DVD-ROM");
        provider.addTranslation("ait.control_disc.can_contain_players.toggle", "Can Contain Players: %s");
        provider.addTranslation("ait.control_disc.unusable_in_tardis_world", "Failed to write DVD-ROM contents: invalid dimension!");

        provider.addTranslation("ait.text.chat.clicked", "here");
        provider.addTranslation("ait.text.chat.readwiki", "To learn how to play Adventures in Time, we suggest you read the wiki, which is ");
        provider.addTranslation("ait.text.chat.hover", "Click here to open the wiki in your browser");

        provider.addTranslation("text.ait.beta.play.tooltip", "You're not a beta tester!");
        provider.addTranslation("text.ait.beta.play", "Authorize to play AIT Beta");
        provider.addTranslation("text.ait.beta.play.browser", "Check your browser!");

        // Visible text owned by screens, renderers, commands, and actionbar messages
        provider.addTranslation("block.ait.detector.type.alarms", "ALARMS");
        provider.addTranslation("block.ait.detector.type.crashed", "CRASHED");
        provider.addTranslation("block.ait.detector.type.door_locked", "DOOR LOCKED");
        provider.addTranslation("block.ait.detector.type.door_open", "DOOR OPEN");
        provider.addTranslation("block.ait.detector.type.flight", "FLIGHT");
        provider.addTranslation("block.ait.detector.type.power", "POWER");
        provider.addTranslation("block.ait.detector.type.sonic", "SONIC");
        provider.addTranslation("block.ait.fabricator.status.collect_output", "COLLECT OUTPUT");
        provider.addTranslation("block.ait.fabricator.status.insert_blueprint", "INSERT BLUEPRINT");
        provider.addTranslation("block.ait.fabricator.status.insert_material", "INSERT %s %s");
        provider.addTranslation("block.ait.plaque.default_text", "Type 50 TT Capsule");
        provider.addTranslation("block.ait.plaque.tt_capsule_type", "Type %s TT Capsule");
        provider.addTranslation("command.ait.door_particle.done", "Particle of [%s] set to [%s]");
        provider.addTranslation("command.ait.list.header", "TARDISes:");
        provider.addTranslation("command.ait.load.loaded", "Loaded: %s");
        provider.addTranslation("command.ait.load.not_found", "No TARDIS found with that UUID.");
        provider.addTranslation("command.ait.scale.not_found", "TARDIS not found.");
        provider.addTranslation("command.ait.unlock.all", "Granted [%s] every %s");
        provider.addTranslation("command.ait.unlock.some", "Granted [%s] %s %s");
        provider.addTranslation("command.ait.unlock.type.console", "console");
        provider.addTranslation("command.ait.unlock.type.desktop", "desktop");
        provider.addTranslation("command.ait.unlock.type.exterior_variant", "exterior variant");
        provider.addTranslation("command.tardis.ait.name", "TARDIS name: %s");
        provider.addTranslation("console.ait.generator.requirement.none", "None");
        provider.addTranslation("console.ait.generator.requires_loyalty", "Requires Loyalty Level: %s");
        provider.addTranslation("text.ait.config.title", "Adventures in Time");
        provider.addTranslation("message.ait.boti.indium_required.amd", "You appear to have an AMD GPU. Indium is required, but is not found. This may cause issues with the mod - BOTI has been disabled!");
        provider.addTranslation("message.ait.boti.indium_required.mac", "You appear to be playing on a Mac. Indium is required, but is not found. This may cause issues with the mod - BOTI has been disabled!");
        provider.addTranslation("message.ait.console_generator.not_unlocked", "This console is not unlocked yet!");
        provider.addTranslation("message.ait.console_control.json_logged", "JSON data logged to Java console!");
        provider.addTranslation("message.ait.control.direction.rotation", "Rotation Direction: %s | %s");
        provider.addTranslation("message.ait.control.monitor.status", "X: %s Y: %s Z: %s Dim: %s Fuel: %s/50000");
        provider.addTranslation("message.ait.dimension.unlocked", "%s unlocked!");
        provider.addTranslation("message.ait.handles.already_in_flight", "The TARDIS is already in flight...");
        provider.addTranslation("message.ait.handles.antigravs_toggled", "Anti-Gravs Toggled.");
        provider.addTranslation("message.ait.handles.available_commands", "Available Commands: %s");
        provider.addTranslation("message.ait.handles.closing_doors", "Closing TARDIS doors.");
        provider.addTranslation("message.ait.handles.dematerializing", "Initiating dematerialization sequence.");
        provider.addTranslation("message.ait.handles.disabling_refueling", "Disabling Refueling.");
        provider.addTranslation("message.ait.handles.doors_already_closed", "Doors are already closed");
        provider.addTranslation("message.ait.handles.doors_already_locked", "Doors already locked");
        provider.addTranslation("message.ait.handles.doors_already_open", "Doors are already open");
        provider.addTranslation("message.ait.handles.doors_already_unlocked", "Doors already unlocked");
        provider.addTranslation("message.ait.handles.enabling_refueling", "Enabling Refueling.");
        provider.addTranslation("message.ait.handles.flight_complete", "Flight is %s%% complete.");
        provider.addTranslation("message.ait.handles.fun_fact.gallifrey", "Gallifrey has two suns and an orange sky!");
        provider.addTranslation("message.ait.handles.fun_fact.green_tardis", "The first TARDIS was actually painted green!");
        provider.addTranslation("message.ait.handles.fun_fact.handles", "Handles once saved the Doctor's life by solving a centuries-old riddle.");
        provider.addTranslation("message.ait.handles.joke.calm", "Why was the TARDIS always calm? Because it's bigger on the inside.");
        provider.addTranslation("message.ait.handles.joke.dalek", "Why did the Dalek apply for a job? It wanted to EX-TER-MINATE its competition!");
        provider.addTranslation("message.ait.handles.joke.hide_and_seek", "Why does the TARDIS always win hide-and-seek? Because it's in another dimension!");
        provider.addTranslation("message.ait.handles.joke.no_time", "What do you call a Time Lord with no time? A Lord!");
        provider.addTranslation("message.ait.handles.joke.time_lords", "How many Time Lords does it take to change a light bulb? None, they just change the timeline.");
        provider.addTranslation("message.ait.handles.locking_doors", "Locking door.");
        provider.addTranslation("message.ait.handles.no_waypoint", "There is no waypoint set.");
        provider.addTranslation("message.ait.handles.not_in_flight", "The TARDIS is not in flight.");
        provider.addTranslation("message.ait.handles.opening_doors", "Opening TARDIS doors.");
        provider.addTranslation("message.ait.handles.protocol_3_toggled", "Protocol 3 Toggled.");
        provider.addTranslation("message.ait.handles.refueling_already_disabled", "Refueling is already disabled.");
        provider.addTranslation("message.ait.handles.refueling_already_enabled", "Refueling is already enabled.");
        provider.addTranslation("message.ait.handles.rematerializing", "Rematerializing.");
        provider.addTranslation("message.ait.handles.setting_course_waypoint", "Setting course for waypoint.");
        provider.addTranslation("message.ait.handles.tardis_state", "TARDIS State: %s");
        provider.addTranslation("message.ait.handles.toggled_shields", "Toggled Shields.");
        provider.addTranslation("message.ait.handles.unlocking_doors", "Unlocking door.");
        provider.addTranslation("message.ait.radio.changing_frequency", "Changing Frequency...");
        provider.addTranslation("message.ait.radio.off", "Radio Off");
        provider.addTranslation("message.ait.radio.on", "Radio On");
        provider.addTranslation("message.ait.remoteitem.power_switch_disabled", "TARDIS in flight. Power Switch Disabled.");
        provider.addTranslation("message.ait.remoteitem.powering_down", "TARDIS Powering Down...");
        provider.addTranslation("message.ait.remoteitem.powering_up", "TARDIS Powering Up...");
        provider.addTranslation("message.ait.remoteitem.takeoff_failed_powered_off", "Takeoff Failed, TARDIS powered off...");
        provider.addTranslation("message.ait.tardis_goat_horn.destination", "X: %s Y: %s Z: %s Dim: %s");
        provider.addTranslation("screen.ait.blueprint_fabricator", "Blueprint Fabricator");
        provider.addTranslation("screen.ait.environment_projector", "Environment Projector");
        provider.addTranslation("screen.ait.environment_projector.current", "CURRENT: ");
        provider.addTranslation("screen.ait.environment_projector.direction.down", "DOWN");
        provider.addTranslation("screen.ait.environment_projector.direction.east", "EAST");
        provider.addTranslation("screen.ait.environment_projector.direction.north", "NORTH");
        provider.addTranslation("screen.ait.environment_projector.direction.south", "SOUTH");
        provider.addTranslation("screen.ait.environment_projector.direction.up", "UP");
        provider.addTranslation("screen.ait.environment_projector.direction.west", "WEST");
        provider.addTranslation("screen.ait.environment_projector.enabled.off", "OFF");
        provider.addTranslation("screen.ait.environment_projector.enabled.on", "ON");
        provider.addTranslation("screen.ait.environment_projector.pitch", "Pitch");
        provider.addTranslation("screen.ait.environment_projector.tab.direction", "Direction");
        provider.addTranslation("screen.ait.environment_projector.tab.sky", "Sky");
        provider.addTranslation("screen.ait.environment_projector.yaw", "Yaw");
        provider.addTranslation("screen.ait.interior_settings.mode.demat", "DEMAT");
        provider.addTranslation("screen.ait.interior_settings.mode.flight", "FLIGHT");
        provider.addTranslation("screen.ait.interior_settings.mode.hum", "HUM");
        provider.addTranslation("screen.ait.interior_settings.mode.mat", "MAT");
        provider.addTranslation("screen.ait.interior_settings.mode.vortex", "VORTEX");
        provider.addTranslation("screen.ait.landing_pad", "Landing Marker");
        provider.addTranslation("screen.ait.visualizer.title", "Portal Visualizer");
        provider.addTranslation("tardis.remove.done", "TARDIS [%s] removed");
        provider.addTranslation("tardis.remove.progress", "Removing TARDIS with id [%s]...");
        provider.addTranslation("tardis.repair.max", "TARDIS repair ticks are at max!");
        provider.addTranslation("tardis.repair.set", "Set repair ticks for [%s] to: [%s]");
        provider.addTranslation("tardis.summon", "TARDIS [%s] is on the way!");
        provider.addTranslation("tardis.teleport.exterior.success", "Successful teleport - exterior of %s");
        provider.addTranslation("tardis.teleport.interior.success", "Successful teleport - interior of %s");
        provider.addTranslation("tooltip.ait.linked_tardis", "TARDIS:");
        provider.addTranslation("tooltip.ait.roundel_type", "Roundel Type");
        provider.addTranslation("tooltip.ait.tardisdeco_type", "TARDIS Deco Type");
        provider.addTranslation("tooltip.ait.tardis_matrix.name", "Name: %s");

        // Control entities
        provider.addTranslation("control.ait.antigravs", "Antigravs");
        provider.addTranslation("control.ait.refreshment_control", "Refreshment Selector");
        provider.addTranslation("control.ait.food_creation", "Refreshment Dispenser");
        provider.addTranslation("control.ait.protocol_116", "Stabiliser");
        provider.addTranslation("control.ait.protocol_3", "Shell Cloaking");
        provider.addTranslation("control.ait.protocol_3_silent_activated", "Silent Mode for Shell Cloaking has been activated");
        provider.addTranslation("control.ait.protocol_3_silent_deactivated", "Silent Mode for Shell Cloaking has been deactivated");
        provider.addTranslation("control.ait.protocol_3_silent_active", "Shell Cloaking (Silent Activated)");
        provider.addTranslation("control.ait.protocol_3_silent_inactive", "Shell Cloaking (Silent Deactivated)");
        provider.addTranslation("control.ait.dimension", "Dimension");
        provider.addTranslation("control.ait.direction", "Direction");
        provider.addTranslation("control.ait.door_control", "Door Control");
        provider.addTranslation("control.ait.door_lock", "Door Lock");
        provider.addTranslation("control.ait.fast_return", "Fast Return");
        provider.addTranslation("control.ait.alarms", "Alarms");
        provider.addTranslation("control.ait.protocol_813", "Hail Mary");
        provider.addTranslation("control.ait.handbrake", "Handbrake");
        provider.addTranslation("control.ait.land_type", "Land Type");
        provider.addTranslation("control.ait.monitor", "Monitor");
        provider.addTranslation("control.ait.power", "Power");
        provider.addTranslation("control.ait.randomiser", "Randomiser");
        provider.addTranslation("control.ait.refueler", "Refueler");
        provider.addTranslation("control.ait.protocol_19", "Isomorphic Security");
        provider.addTranslation("control.ait.protocol_1913", "Siege Mode");
        provider.addTranslation("control.ait.sonic_port", "Sonic Port");
        provider.addTranslation("control.ait.telepathic_circuit", "Telepathic Circuit");
        provider.addTranslation("control.ait.throttle", "Throttle");
        provider.addTranslation("control.ait.visualiser.normal", "Scanner");
        provider.addTranslation("control.ait.visualiser.rwf", "Manual Override");
        provider.addTranslation("control.ait.visualiser.none", "Cool Button");
        provider.addTranslation("control.ait.eject_waypoint", "Eject Waypoint");
        provider.addTranslation("control.ait.goto_waypoint", "Goto Waypoint");
        provider.addTranslation("control.ait.console_port", "Console Port");
        provider.addTranslation("control.ait.save_waypoint", "Save Waypoint");
        provider.addTranslation("control.ait.load_waypoint", "Load Waypoint");
        provider.addTranslation("control.ait.load_waypoint.error", "Cartridge contains no waypoint");
        provider.addTranslation("control.ait.load_control_disc.loaded", "Control disc loaded. Ready for takeoff.");
        provider.addTranslation("control.ait.load_waypoint.no_cartridge", "No cartridge in port");
        provider.addTranslation("control.ait.increment", "Increment");
        provider.addTranslation("control.ait.x", "X");
        provider.addTranslation("control.ait.y", "Y");
        provider.addTranslation("control.ait.z", "Z");
        provider.addTranslation("control.ait.shields", "Shields");
        provider.addTranslation("control.ait.engine_overload", "Engine Overload");
        provider.addTranslation("control.ait.electrical_discharge", "Shell Repellent");
        provider.addTranslation("control.ait.hammer_hanger", "Hammer Hanger");

        // Tabs
        provider.addTranslation(AITItemGroups.MAIN, "Adventures In Time");
        provider.addTranslation(AITItemGroups.FABRICATOR, "AIT: Fabrication");

        // Config
        provider.addTranslation("category.ait.config.client", "Client Options");
        provider.addTranslation("category.ait.config.server", "Server Options");

        // Client config
        provider.addTranslation("yacl3.config.ait:client.interiorHumVolume", "Interior Hum Volume");
        provider.addTranslation("yacl3.config.ait:client.engineLoopVolume", "Engine Loop Volume");
        provider.addTranslation("yacl3.config.ait:client.flightMusicVolume", "Flight Music Volume");
        provider.addTranslation("yacl3.config.ait:client.customMenu", "Toggle Custom Menu");
        provider.addTranslation("yacl3.config.ait:client.showExperimentalWarning", "Toggle Experimental Warning");
        provider.addTranslation("yacl3.config.ait:client.environmentProjector", "Toggle Environment Projector");
        provider.addTranslation("yacl3.config.ait:client.screenShake", "Screen Shake Amount");
        provider.addTranslation("yacl3.config.ait:client.enableTardisBOTI", "Toggle TARDIS BOTI");
        provider.addTranslation("yacl3.config.ait:client.greenScreenBOTI", "Toggle Green-Screen BOTI");
        provider.addTranslation("yacl3.config.ait:client.allowPortalsBoti", "Toggle Immersive Portals BOTI");
        provider.addTranslation("yacl3.config.ait:client.showControlHitboxes", "Toggle Console Control Hitboxes");
        provider.addTranslation("yacl3.config.ait:client.showConsoleMonitorText", "Toggle Text On Console Monitors");
        provider.addTranslation("yacl3.config.ait:client.showCRTMonitorText", "Toggle Text On CRT Monitors");
        provider.addTranslation("yacl3.config.ait:client.renderDematParticles", "Toggle Demat Particle Rendering");
        provider.addTranslation("yacl3.config.ait:client.powerOffDarkness", "Toggle Darkness Fog When Powered Off");
        provider.addTranslation("yacl3.config.ait:client.animateConsole", "Toggle Animated Consoles");
        provider.addTranslation("yacl3.config.ait:client.animateControls", "Toggle Smoothly Animated Controls");
        provider.addTranslation("yacl3.config.ait:client.animateDoors", "Toggle Smoothly Animated Doors");
        provider.addTranslation("yacl3.config.ait:client.handlesLevenshteinDistance", "Levenshtein Distance For Handles");
        provider.addTranslation("yacl3.config.ait:client.temperatureType", "Temperature Type");
        provider.addTranslation("yacl3.config.ait:client.temperatureType.unit.celsius", "Celsius (°C)");
        provider.addTranslation("yacl3.config.ait:client.temperatureType.unit.fahrenheit", "Fahrenheit (°F)");
        provider.addTranslation("yacl3.config.ait:client.temperatureType.unit.kelvin", "Kelvin (K)");

        // Server config
        provider.addTranslation("yacl3.config.ait:server.minifyJson", "Minify Json");
        provider.addTranslation("yacl3.config.ait:server.ghostMonument", "Toggle Ghost Monument");
        provider.addTranslation("yacl3.config.ait:server.lockDimensions", "Toggle Lockable Dimensions");
        provider.addTranslation("yacl3.config.ait:server.rwfEnabled", "[EXPERIMENTAL] Toggle RWF");
        provider.addTranslation("yacl3.config.ait:server.allowPortalsBoti", "Toggle Immersive Portals BOTI");
        provider.addTranslation("yacl3.config.ait:server.tntCanTeleportThroughDoors", "Toggle TNT Door Teleporting");
        provider.addTranslation("yacl3.config.ait:server.hypercubesEnabled", "Toggle Hypercubes");
        provider.addTranslation("yacl3.config.ait:server.travelPerTick", "Travel Per-Tick");
        provider.addTranslation("yacl3.config.ait:server.astralMapBiomeLocatorRange", "Astral Map Biome Locator Range");
        provider.addTranslation("yacl3.config.ait:server.sendBulk", "Toggle Send Bulk TARDIS Data");
        provider.addTranslation("yacl3.config.ait:server.maxTardises", "Max TARDISes");
        provider.addTranslation("yacl3.config.ait:server.disableSafeguards", "Disable In-Built Safeguards");
        provider.addTranslation("yacl3.config.ait:server.crashSoundVolume", "Crash Sound Volume");
        provider.addTranslation("yacl3.config.ait:server.flightSoundVolume", "Flight Sound Volume");
        provider.addTranslation("yacl3.config.ait:server.maxStabilizedSpeed", "Max Stabilized Speed");
        provider.addTranslation("yacl3.config.ait:server.projectorBlacklist", "Environment Projector Blacklist");
        provider.addTranslation("yacl3.config.ait:server.projectorWhitelist", "Environment Projector Whitelist");
        provider.addTranslation("yacl3.config.ait:server.travelBlacklist", "TARDIS Travel Blacklist");
        provider.addTranslation("yacl3.config.ait:server.travelWhitelist", "TARDIS Travel Whitelist");

        provider.addTranslation(AITMod.TARDIS_GRIEFING.getTranslationKey(), "TARDIS Griefing");
        provider.addTranslation(AITMod.TARDIS_FIRE_GRIEFING.getTranslationKey(), "TARDIS Fire Griefing");
        provider.addTranslation("entity.minecraft.villager.fabricator_engineer", "Fabricator Engineer");

        // Entitys
        provider.addTranslation(AITEntityTypes.RIFT_ENTITY.getTranslationKey(), "Space-Time Rift");
        provider.addTranslation(AITEntityTypes.CONTROL_ENTITY_TYPE.getTranslationKey(), "Control Entity");
        provider.addTranslation(AITEntityTypes.FALLING_TARDIS_TYPE.getTranslationKey(), "Falling TARDIS");
        provider.addTranslation(AITEntityTypes.FLIGHT_TARDIS_TYPE.getTranslationKey(), "RWF TARDIS");
        provider.addTranslation(AITEntityTypes.GALLIFREY_FALLS_PAINTING_ENTITY_TYPE.getTranslationKey(), "Gallifrey Falls Painting");
        provider.addTranslation(AITEntityTypes.TRENZALORE_PAINTING_ENTITY_TYPE.getTranslationKey(), "Trenzalore Painting");
        provider.addTranslation(GunEntityTypes.STASER_BOLT_ENTITY_TYPE.getTranslationKey(), "Stazer Bolt Projectile");

        // Items
        provider.addTranslation(AITItems.TARDIS_ITEM, "TARDIS");
        provider.addTranslation(AITItems.REMOTE_ITEM, "Stattenheim Remote");
        provider.addTranslation(AITItems.ARTRON_COLLECTOR, "Artron Collector Unit");
        provider.addTranslation(AITItems.SIEGE_ITEM, "TARDIS");
        provider.addTranslation(AITItems.TWO_THOUSAND_MUSIC_DISC, "Music Disc");
        provider.addTranslation(AITItems.WONDERFUL_TIME_IN_SPACE_MUSIC_DISC, "Music Disc");
        provider.addTranslation(AITItems.TWO_THOUSAND_MUSIC_DISC.getTranslationKey() + ".desc", "lucien - Two Thousand");
        provider.addTranslation(AITItems.WONDERFUL_TIME_IN_SPACE_MUSIC_DISC.getTranslationKey() + ".desc", "Dian - Wonderful Time in Space");
        provider.addTranslation(AITItems.EARTH_MUSIC_DISC, "Music Disc");
        provider.addTranslation(AITItems.EARTH_MUSIC_DISC.getTranslationKey() + ".desc", "Nitrogenez - Earth");
        provider.addTranslation(AITItems.VENUS_MUSIC_DISC, "Music Disc");
        provider.addTranslation(AITItems.VENUS_MUSIC_DISC.getTranslationKey() + ".desc", "Nitrogenez - Venus");
        provider.addTranslation(AITItems.STAGE_4_MUSIC_DISC, "Music Disc");
        provider.addTranslation(AITItems.STAGE_4_MUSIC_DISC.getTranslationKey() + ".desc", "??? - [stage 4]");
        provider.addTranslation(AITItems.GOOD_MAN_MUSIC_DISC, "Music Disc");
        provider.addTranslation(AITItems.GOOD_MAN_MUSIC_DISC.getTranslationKey() + ".desc", "Dian - Good Man? [CUT EDITION]");
        provider.addTranslation(AITItems.AIT_THEME_MUSIC_DISC, "Music Disc");
        provider.addTranslation(AITItems.AIT_THEME_MUSIC_DISC.getTranslationKey() + ".desc", "RatZoomie - Adventures In Time [MAIN THEME]");
        provider.addTranslation(AITItems.CRASH_MUSIC_DISC, "Music Disc");
        provider.addTranslation(AITItems.CRASH_MUSIC_DISC.getTranslationKey() + ".desc", "lucien - Crashing TARDIS");
        provider.addTranslation(AITItems.GOLD_KEY_UPGRADE_SMITHING_TEMPLATE, "Smithing Template");
        provider.addTranslation(AITItems.NETHERITE_KEY_UPGRADE_SMITHING_TEMPLATE, "Smithing Template");
        provider.addTranslation(AITItems.CLASSIC_KEY_UPGRADE_SMITHING_TEMPLATE, "Smithing Template");
        provider.addTranslation(AITItems.PSYCHPAPER, "Psychic Paper");
        provider.addTranslation(AITItems.GALLIFREY_FALLS_PAINTING, "Painting");
        provider.addTranslation(AITItems.TRENZALORE_PAINTING, "Painting");
        provider.addTranslation(AITItems.HAMMER, "Mallet");
        provider.addTranslation(AITItems.FOOD_CUBE, "Food Cube");
        provider.addTranslation(AITItems.OVERCHARGED_FOOD_CUBE, "Overcharged Food Cube");
        provider.addTranslation("ait.foodmachine.mode.food_cubes", "Food Cubes");
        provider.addTranslation("ait.foodmachine.mode.drinks", "Drinks");
        provider.addTranslation("ait.foodmachine.mode.overcharged_food_cubes", "Overcharged Food Cubes");
        provider.addTranslation("ait.foodmachine.mode.refreshement_set_to", "Refreshment set to: %s!");
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
        provider.addTranslation("ait.tooltip.siege_item.enter", "Temporal machine detected, cannot seal real-time envelope!");

        // Exteriors
        provider.addTranslation("exterior.ait.capsule", "Capsule");
        provider.addTranslation("exterior.ait.police_box", "Police Box");
        provider.addTranslation("exterior.ait.booth", "Booth");
        provider.addTranslation("exterior.ait.renegade", "Renegade");
        provider.addTranslation("exterior.ait.doom", "DOOM");
        provider.addTranslation("exterior.ait.wanderer", "Wanderer");
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
        provider.addTranslation("desktop.ait.corpoyta", "Corpoyta");
        provider.addTranslation("desktop.ait.legacy", "Legacy");

        provider.addTranslation("desktop.ait.cathedral", "Cathedral");
        provider.addTranslation("desktop.ait.cyberpunk", "CyberPunk");
        provider.addTranslation("desktop.ait.egyptian", "Egyptian");
        provider.addTranslation("desktop.ait.forest", "Forest");
        provider.addTranslation("desktop.ait.gothic", "Gothic");
        provider.addTranslation("desktop.ait.historic", "Historic");
        provider.addTranslation("desktop.ait.laboratory", "Laboratory");
        provider.addTranslation("desktop.ait.steampunk", "Steampunk");
        provider.addTranslation("desktop.ait.trek", "Trek");
        // Sonic Screwdrivers
        provider.addTranslation("sonic.ait.prime", "Prime");
        provider.addTranslation("sonic.ait.crystalline", "Crystalline");
        provider.addTranslation("sonic.ait.renaissance", "Renaissance");
        provider.addTranslation("sonic.ait.coral", "Coral");
        provider.addTranslation("sonic.ait.charon", "Charon");
        provider.addTranslation("sonic.ait.fob", "Fob");
        provider.addTranslation("sonic.ait.copper", "Copper");
        provider.addTranslation("sonic.ait.mechanical", "Mechanical");
        provider.addTranslation("sonic.ait.song", "Song");
        provider.addTranslation("sonic.ait.singularity", "Singularity");
        provider.addTranslation("sonic.ait.candy_cane", "Candy Cane");
        provider.addTranslation("sonic.ait.type_100", "Frontier");
        provider.addTranslation("sonic.ait.portal_gun", "Portal Gun");

        // Consoles
        provider.addTranslation("console.ait.variant_label", "Console Type: ");
        provider.addTranslation("console.ait.alnico", "Alnico");
        provider.addTranslation("console.ait.borealis", "Borealis");
        provider.addTranslation("console.ait.steam_steel", "Steel Steam");
        provider.addTranslation("console.ait.toyota", "Toyota");
        provider.addTranslation("console.ait.hartnell", "Hartnell");
        provider.addTranslation("console.ait.hartnell_wooden", "Wooden Hartnell");
        provider.addTranslation("console.ait.coral", "Coral");
        provider.addTranslation("console.ait.coral_blue", "Blue Coral");
        provider.addTranslation("console.ait.coral_decayed", "Decayed Coral");
        provider.addTranslation("console.ait.steam", "Steam");
        provider.addTranslation("console.ait.hartnell_kelt", "Kelt Hartnell");
        provider.addTranslation("console.ait.alnico_blue", "Blue Alnico");
        provider.addTranslation("console.ait.renaissance_fire", "Fire Renaissance");
        provider.addTranslation("console.ait.steam_gilded", "Gilded Steam");
        provider.addTranslation("console.ait.coral_sith", "Sith Coral");
        provider.addTranslation("console.ait.hudolin", "Hudolin");
        provider.addTranslation("console.ait.hudolin_nature", "Human Nature");
        provider.addTranslation("console.ait.crystalline", "Crystalline");
        provider.addTranslation("console.ait.steam_playpal", "Playpal Steam");
        provider.addTranslation("console.ait.renaissance_tokamak", "Tokamak Renaissance");
        provider.addTranslation("console.ait.copper", "Copper");
        provider.addTranslation("console.ait.renaissance_identity", "Identity Renaissance");
        provider.addTranslation("console.ait.steam_cherry", "Cherry Steam");
        provider.addTranslation("console.ait.renaissance_industrious", "Industrious Renaissance");
        provider.addTranslation("console.ait.hartnell_mint", "Mint Hartnell");
        provider.addTranslation("console.ait.hartnell_mint_green_console", "Mint Green Hartnell");
        provider.addTranslation("console.ait.exile", "Exile");
        provider.addTranslation("console.ait.crystalline_zeiton", "Zeiton Crystalline");
        provider.addTranslation("console.ait.steam_copper", "Copper Steam");
        provider.addTranslation("console.ait.toyota_blue", "Blue Toyota");
        provider.addTranslation("console.ait.toyota_legacy", "Legacy Toyota");
        provider.addTranslation("console.ait.renaissance", "Renaissance");
        provider.addTranslation("console.ait.coral_white", "White Coral");
        provider.addTranslation("console.ait.hudolin_shalka", "Shalka");
        provider.addTranslation("console.ait.hudolin_short", "Hudolin Short");
        provider.addTranslation("console.ait.hudolin_tall", "Human Nature Tall");

        // Blocks
        provider.addTranslation(AITBlocks.LANDING_PAD, "Landing Marker");
        provider.addTranslation(AITBlocks.DETECTOR_BLOCK, "Interior Detector Block");
        provider.addTranslation(AITBlocks.EXTERIOR_BLOCK, "Exterior");
        provider.addTranslation(AITBlocks.CORAL_PLANT, "Coral Growth");
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
        provider.addTranslation(AITBlocks.CABLE_CONNECTOR_BLOCK , "Artron Cable Connector");
        provider.addTranslation(AITBlocks.GENERIC_SUBSYSTEM, "Generalized Subsystem Core");
        provider.addTranslation(AITBlocks.FOOD_MACHINE, "Food Machine");

        // Block Tooltips
        provider.addTranslation("block.ait.fabricator.tooltip.use", "(Place on top of a Smithing Table)");
        provider.addTranslation("tooltip.ait.use_in_tardis", "(Place inside a TARDIS)");
        provider.addTranslation("block.ait.artron_collector_block.tooltip.use", "(Charges inside of Rift Chunks)");
        provider.addTranslation("tooltip.ait.power_converter", "(Convert zeiton, lava, coal and wood into Artron)");
        provider.addTranslation("tooltip.ait.singularity", "(Give the TARDIS Coral this to allow the generation of the interior)");
        provider.addTranslation("tooltip.ait.tardis_coral", "(Grow this on top of soul sand)");
        provider.addTranslation("tooltip.ait.matrix_energizer", "(Place on a naturally-occurring shrieker to produce a TARDIS Matrix)");

        // Painting
        provider.addTranslation("painting.ait.crab_thrower.title", "Crab Thrower");
        provider.addTranslation("painting.ait.crab_thrower.author", "???");

        provider.addTranslation("painting.ait.gallifrey_falls.title", "Gallifrey Falls");
        provider.addTranslation("painting.ait.gallifrey_falls.author", "???");

        provider.addTranslation("painting.ait.trenzalore.title", "Trenzalore");
        provider.addTranslation("painting.ait.trenzalore.author", "???");

        provider.addTranslation("painting.ait.peanut.title", "Peanut");
        provider.addTranslation("painting.ait.peanut.author", "???");

        // Astral Map
        provider.addTranslation("screen.ait.astral_map.structures.button", "STRUCTURES");
        provider.addTranslation("screen.ait.astral_map.biomes.button", "BIOMES");
        provider.addTranslation("screen.ait.astral_map.search.button", "SEARCH");
        provider.addTranslation("screen.ait.astral_map.loading", "LOADING");
        provider.addTranslation("screen.ait.astral_map.switcher.left_arrow", "<");
        provider.addTranslation("screen.ait.astral_map.switcher.right_arrow", ">");
        provider.addTranslation("block.ait.astral_map.finder.structure_not_found", "404: STRUCTURE NOT FOUND");
        provider.addTranslation("block.ait.astral_map.finder.biome_not_found", "404: BIOME NOT FOUND");
        provider.addTranslation("block.ait.astral_map.finder.searching_for_biome", "SEARCHING FOR BIOME...");
        provider.addTranslation("block.ait.astral_map.finder.searching_for_structure", "SEARCHING FOR STRUCTURE...");
        provider.addTranslation("block.ait.astral_map.finder.found", "SUCCESS! FOUND AT %s, %s, %s ( %s blocks away )");

        // Death
        provider.addTranslation("death.attack.tardis_squash", "%1$s got squashed by a TARDIS!");
        provider.addTranslation("death.attack.space_suffocation", "%1$s got blown up due to lack of Oxygen!");

        // Sonic Scanning Mode
        provider.addTranslation("item.sonic.scanning.any_tool", "Any Tool");
        provider.addTranslation("item.sonic.scanning.diamond_tool", "Diamond Tool");
        provider.addTranslation("item.sonic.scanning.iron_tool", "Iron Tool");
        provider.addTranslation("item.sonic.scanning.stone_tool", "Stone Tool");
        provider.addTranslation("item.sonic.scanning.no_tool", "Hand (No Tool)");
        provider.addTranslation("item.sonic.scanning.cant_break", "Can't Break Block!");
        provider.addTranslation("item.sonic.scanning.locator_message.title", "TARDIS location: %s");
        provider.addTranslation("item.sonic.scanning.locator_message.coordinates", "Coordinates: %s %s %s");

        provider.addTranslation("tardis.loyalty.name.reject","REJECT");
        provider.addTranslation("tardis.loyalty.name.neutral","NEUTRAL");
        provider.addTranslation("tardis.loyalty.name.companion","COMPANION");
        provider.addTranslation("tardis.loyalty.name.pilot","PILOT");
        provider.addTranslation("tardis.loyalty.name.owner","OWNER");

        // Loyalty Messages In Bed
        provider.addTranslation("tardis.loyalty.message.reject","You hear whispers all around you, you are not welcome. [REJECT]");
        provider.addTranslation("tardis.loyalty.message.neutral", "The TARDIS hums, neither welcoming nor dismissing your presence. [NEUTRAL]");
        provider.addTranslation("tardis.loyalty.message.companion", "The TARDIS hums you a tune, as if glad to have you on board. [COMPANION]");
        provider.addTranslation("tardis.loyalty.message.pilot", "The TARDIS hums gently, as if to show its trust. [PILOT]");
        provider.addTranslation("tardis.loyalty.message.owner", "The TARDIS hums you a song, as if to show it will always be here for you. [OWNER]");

        // TARDIS Control Actionbar Title
        provider.addTranslation("tardis.message.protocol_813.travel", "Hail Mary is active, please prepare for departure.");
        provider.addTranslation("tardis.message.control.protocol_116.active", "Stabiliser: ENGAGED");
        provider.addTranslation("tardis.message.control.hail_mary.engaged", "Hail Mary: ENGAGED");
        provider.addTranslation("tardis.message.control.hail_mary.disengaged", "Hail Mary: DISENGAGED");
        provider.addTranslation("tardis.message.control.protocol_116.inactive", "Stabilisers: DISENGAGED");
        provider.addTranslation("tardis.message.console.has_sonic_in_port", "Cannot cache console with sonic in port");
        provider.addTranslation("ait.tardis.control.throttle.stabilisers_disabled", "Stabilisers not connected to engine, speed limited!");
        provider.addTranslation("tardis.message.control.antigravs.active", "Antigravs: ENGAGED");
        provider.addTranslation("tardis.message.control.antigravs.inactive", "Antigravs: DISENGAGED");
        provider.addTranslation("tardis.message.control.electric.fail", "System Error: Not enough fuel present! Requires %sAU");
        provider.addTranslation("tardis.message.control.rwf_disabled", "RWF is disabled in SERVER config.");
        provider.addTranslation("tardis.message.control.rwf_creative_only", "RWF is CREATIVE only");
        provider.addTranslation("tardis.message.control.fast_return.destination_nonexistent",
                "Fast Return: Last Position Nonexistent!");
        provider.addTranslation("tardis.message.control.fast_return.last_position", "Fast Return: LAST POSITION SET");
        provider.addTranslation("tardis.message.control.fast_return.current_position",
                "Fast Return: CURRENT POSITION SET");
        provider.addTranslation("tardis.message.control.protocol_813.active", "Hail Mary: ENGAGED");
        provider.addTranslation("tardis.message.control.protocol_813.inactive", "Hail Mary: DISENGAGED");
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
        provider.addTranslation("tardis.message.control.telepathic.home_updated", "TARDIS home location changed.");
        provider.addTranslation("tardis.message.control.telepathic.home_denied", "The TARDIS refuses to change its home for you. Loyalty level PILOT required.");
        provider.addTranslation("tardis.message.control.telepathic.home_denied_nether", "The TARDIS rejects Nether as home. Loyalty level OWNER required.");
        provider.addTranslation("tardis.message.control.engine_overdrive.primed", "Dump Artron? Press again to confirm.");
        provider.addTranslation("tardis.message.control.engine_overdrive.insufficient_fuel", "ERROR, TARDIS REQUIRES AT LEAST 25K ARTRON TO EXECUTE THIS ACTION.");
        provider.addTranslation("tardis.message.control.engine_overdrive.dumping_artron", "DUMPING ARTRON");
        provider.addTranslation("tardis.message.control.engine_overdrive.engines_overloaded", "ARTRON DUMPED, ENGINES OVERLOADED, TRIGGERING EMERGENCY ARTRON RELEASE");
        provider.addTranslation("tardis.message.interiorchange.success", "%s has grown to %d");
        provider.addTranslation("tardis.message.landingpad.adjust", "Adjusting to landing pad..");
        provider.addTranslation("tardis.message.self_destruct.warning", "SELF DESTRUCT INITIATED | ABANDON SHIP");
        provider.addTranslation("tardis.message.chameleon.failed", "Failed to find a suitable disguise!");
        provider.addTranslation("warning.ait.needs_subsystem", "ERROR, REQUIRES ACTIVE SUBSYSTEM: %s");
        provider.addTranslation("tardis.message.growth.hint", "Throw the TARDIS Matrix into the water to give it life...");
        provider.addTranslation("tardis.message.growth.no_cage", "Cage the TARDIS Coral to begin Plasmic coating process!");
        provider.addTranslation("tardis.message.growth.in_progress", "Coral growth still in progress...");
        provider.addTranslation("message.ait.hypercubes.disabled", "Hypercubes are disabled in SERVER config.");

        provider.addTranslation("ait.monitor.fuel", "AU");
        provider.addTranslation("ait.monitor.fuel_with_text", "AU: %s");

        provider.addTranslation("message.ait.control.ylandtype", "Vertical Search Mode: %s");
        provider.addTranslation("message.ait.control.ylandtype.floor", "FLOOR");
        provider.addTranslation("message.ait.control.ylandtype.ceiling", "CEILING");
        provider.addTranslation("message.ait.control.ylandtype.median", "MEDIAN");
        provider.addTranslation("message.ait.control.ylandtype.none", "NONE");
        provider.addTranslation("message.ait.control.xlandtype.on", "Horizontal Search: ENGAGED");
        provider.addTranslation("message.ait.control.xlandtype.off", "Horizontal Search: DISENGAGED");
        provider.addTranslation("message.ait.loyalty_amount", "Loyalty Level: %s");
        provider.addTranslation("message.ait.landing_code", "Landing Code...");
        provider.addTranslation("message.ait.enter_landing_code", "Enter Landing Code...");
        provider.addTranslation("message.ait.date_created", "Date Created:");
        provider.addTranslation("message.ait.unlocked", "'%s' unlocked!");
        provider.addTranslation("message.ait.unlocked_exterior", "Exterior Shell '%s' unlocked!");
        provider.addTranslation("message.ait.unlocked_sonic", "Sonic Screwdriver Casing '%s' unlocked!");
        provider.addTranslation("message.ait.unlocked_console", "Console Interface '%s' unlocked!");
        provider.addTranslation("message.ait.unlocked_interior", "Desktop Theme '%s' unlocked!");
        provider.addTranslation("message.ait.unlocked_all", "Unlocked all %s");
        provider.addTranslation("message.ait.all_types", "Consoles, Exteriors, Desktops & Sonic Casings");
        provider.addTranslation("screen.ait.sonic_casing", "Sonic Casing");
        provider.addTranslation("sonic.ait.mode.tardis.location_summon", "Summoned TARDIS To Your Location, Please Wait...");
        provider.addTranslation("sonic.ait.mode.tardis.is_not_in_range",  "TARDIS is out of range!");
        provider.addTranslation("sonic.ait.mode.tardis.insufficient_fuel",  "TARDIS doesn't have enough fuel!");
        provider.addTranslation("sonic.ait.mode.tardis.does_not_have_power",  "TARDIS is powered off!");
        provider.addTranslation("sonic.ait.mode.tardis.does_not_have_stabilisers",  "Remote Summoning Requires Stabilisers!");
        provider.addTranslation("sonic.ait.mode.tardis.refuel", "Engaged Handbrake, TARDIS Refueling...");
        provider.addTranslation("sonic.ait.mode.tardis.flight", "Disengaged Handbrake, TARDIS Dematerialising...");
        provider.addTranslation("screen.ait.current_au", "Current AU");
        provider.addTranslation("screen.ait.linked_tardis", "Linked TARDIS");
        provider.addTranslation("tardis.message.engine.phasing", "ENGINES PHASING");
        provider.addTranslation("message.ait.cage.full", "It calls for the void..");
        provider.addTranslation("message.ait.cage.void_hint", "(Throw this into the END void)");
        provider.addTranslation("message.ait.cage.empty", "(Place this in a rift chunk)");
        provider.addTranslation("tardis.message.engine.system_is_weakened", "This System Is Showing Signs Of Weakness, But Is Still Functional!");
        provider.addTranslation("tardis.message.subsystem.requires_link", "LINK TO ENGINE VIA ARTRON CABLES");
        provider.addTranslation("tardis.message.engine.no_space", "Engine requires a 3x3 space to function!");

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
        provider.addTranslation("achievement.ait.title.feed_power_converter", "What are you doing, Doc?");
        provider.addTranslation("achievement.ait.description.feed_power_converter", "Use food on the power converter.");
        provider.addTranslation("achievement.ait.title.attack_eyebrows", "Attack Eyebrows");
        provider.addTranslation("achievement.ait.description.attack_eyebrows", "It's an elevator!");
        provider.addTranslation("achievement.ait.title.pui", "Piloting under the influence");
        provider.addTranslation("achievement.ait.description.pui", "Consume Zeiton Dust while the TARDIS is in flight.");
        provider.addTranslation("achievement.ait.title.bonding", "I think it's starting to trust you.");
        provider.addTranslation("achievement.ait.description.bonding", "Reach 'Pilot' loyalty for the first time.");
        provider.addTranslation("achievement.ait.title.owner_ship", "Established complete telepathic connection.");
        provider.addTranslation("achievement.ait.description.owner_ship", "Reach 'Owner' loyalty for the first time.");
        provider.addTranslation("achievement.ait.title.enable_subsystem", "Time-Space Engineer");
        provider.addTranslation("achievement.ait.description.enable_subsystem", "Enable a subsystem.");
        provider.addTranslation("achievement.ait.title.repair_subsystem", "Handyman!");
        provider.addTranslation("achievement.ait.description.repair_subsystem", "Repair a broken subsystem");
        provider.addTranslation("achievement.ait.title.engines_phase", "Technical Problems");
        provider.addTranslation("achievement.ait.description.engines_phase", "ENGINES PHASING! That doesn't sound good.");
        provider.addTranslation("achievement.ait.title.place_energizer", "Procrastination 3000");
        provider.addTranslation("achievement.ait.description.place_energizer", "Place a Matrix Energizer ontop of a naturaly generated Shrieker.");
        provider.addTranslation("achievement." + AITMod.MOD_ID + ".title.brand_new", "Having a Coffee!");
        provider.addTranslation("achievement." + AITMod.MOD_ID + ".description.brand_new", "OH MY GOD! IVE DONE IT AGAIN!");
        provider.addTranslation("achievement.ait.title.remote", "Grand Design");
        provider.addTranslation("achievement.ait.description.remote", "The Stattenheim Remote is yours. Fascinating. Now we shall observe precisely how you manipulate causality… and fracture under pressure.");
        provider.addTranslation("achievement.ait.title.first_rift", "The Sound of Drums");
        provider.addTranslation("achievement.ait.description.first_rift", "The beating of the drums never stops...");

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
        provider.addTranslation("message.ait.sonic.repairtime", "Repair Time: %s");
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
        provider.addTranslation("tooltip.ait.subsystem_item", "(Use this on the Generalized Subsystem Core to set it to this type)");
        provider.addTranslation("tooltip.ait.repair_tool", "Use this while holding on a subsystem, engine, or broken controls to repair them.");

        // Item tooltips
        provider.addTranslation("message.ait.artron_units", "Artron Units: %s");
        provider.addTranslation("message.ait.tooltips.artron_units", "Artron Units: ");
        provider.addTranslation("message.ait.ammo", "Ammo: %s");
        provider.addTranslation("tooltip.ait.position", "Position: ");
        provider.addTranslation("message.ait.artron_units2", " AU");
        provider.addTranslation("overlay.ait.untempered_schism.au", "AU: %s/%s");


        // Environment Projector
        provider.addTranslation("message.ait.projector.skybox", "Now projecting: %s");
        provider.addTranslation("message.ait.projector.world", "World: ");
        provider.addTranslation("message.ait.projector.dimension_skys", "Dimension skys");

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
        provider.addTranslation("ait.console.inventory", "Console");

        provider.addTranslation("tooltip.ait.key.notardis", "Key does not identify with any TARDIS");
        provider.addTranslation("tooltip.ait.items.holdformoreinfo", "Hold shift for more info");
        provider.addTranslation("tooltip.ait.remoteitem.notardis", "Remote does not identify with any TARDIS");
        provider.addTranslation("tooltip.ait.distresscall.source", "SOURCE");

        //Monitor
        provider.addTranslation("screen.ait.monitor.on", "ON");
        provider.addTranslation("screen.ait.monitor.off", "OFF");
        provider.addTranslation("screen.ait.monitor.apply", "Apply");
        provider.addTranslation("screen.ait.monitor.fuel", "Fuel");
        provider.addTranslation("screen.ait.monitor.traveltime", "Travel Time");
        provider.addTranslation("screen.ait.interiorsettings.title", "Interior Settings");
        provider.addTranslation("screen.ait.interiorsettings.back", "> Back");
        provider.addTranslation("screen.ait.security.button", "> Security Options");
        provider.addTranslation("screen.ait.interiorsettings.changeinterior", "> Change Interior");
        provider.addTranslation("screen.ait.interiorsettings.cacheconsole", "> Cache Console");
        provider.addTranslation("screen.ait.loadsaveinterior.button", "> Save Interior");
        provider.addTranslation("screen.ait.widget.timeline", "Timeline");
        provider.addTranslation("screen.ait.monitor.shell_cloaking_activated_message", "Silent shell cloaking is activated");

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
        provider.addTranslation("hum.ait.beacon", "Beacon");
        provider.addTranslation("hum.ait.copper", "Copper");
        provider.addTranslation("hum.ait.eight", "Eight");
        provider.addTranslation("hum.ait.exile", "Exile");
        provider.addTranslation("hum.ait.prime", "Prime");
        provider.addTranslation("hum.ait.renaissance", "Renaissance");
        provider.addTranslation("hum.ait.toyota", "Toyota");
        provider.addTranslation("hum.ait.coral", "Coral");
        provider.addTranslation("hum.ait.christmas", "Christmas");
        provider.addTranslation("hum.ait.off", "Off");

        // Flight Sounds
        provider.addTranslation("flight.ait.default", "Default");
        provider.addTranslation("flight.ait.eight", "Eighth");
        provider.addTranslation("flight.ait.proton", "Proton");
        provider.addTranslation("flight.ait.stabilize", "Stabilize");
        provider.addTranslation("flight.ait.unstable", "Unstable");

        // Vortices
        provider.addTranslation("vortex.ait.80s", "80s");
        provider.addTranslation("vortex.ait.accursed", "Accursed");
        provider.addTranslation("vortex.ait.capaldi", "Capaldi");
        provider.addTranslation("vortex.ait.chronos", "Chronos");
        provider.addTranslation("vortex.ait.classic", "Classic");
        provider.addTranslation("vortex.ait.classic_white", "Classic White");
        provider.addTranslation("vortex.ait.copper", "Copper");
        provider.addTranslation("vortex.ait.crystal", "Crystal");
        provider.addTranslation("vortex.ait.dalekmod", "Dalek Mod");
        provider.addTranslation("vortex.ait.darkness", "Darkness");
        provider.addTranslation("vortex.ait.galactic", "Galactic");
        provider.addTranslation("vortex.ait.house", "House");
        provider.addTranslation("vortex.ait.lego", "LEGO");
        provider.addTranslation("vortex.ait.mccoy", "McCoy");
        provider.addTranslation("vortex.ait.movie", "Movie");
        provider.addTranslation("vortex.ait.outergalaxy", "Outer Galaxy");
        provider.addTranslation("vortex.ait.outerspace", "Outer Space");
        provider.addTranslation("vortex.ait.peanut", "Peanut");
        provider.addTranslation("vortex.ait.pixelator", "Pixelator");
        provider.addTranslation("vortex.ait.renaissance", "Renaissance");
        provider.addTranslation("vortex.ait.rulebreaker", "Rulebreaker");
        provider.addTranslation("vortex.ait.space", "Space");
        provider.addTranslation("vortex.ait.stargate", "Stargate");
        provider.addTranslation("vortex.ait.starlight", "Starlight");
        provider.addTranslation("vortex.ait.synthwave", "Synthwave");
        provider.addTranslation("vortex.ait.tennantblue", "Tennant Blue");
        provider.addTranslation("vortex.ait.tennantred", "Tennant Red");
        provider.addTranslation("vortex.ait.timehole", "Time Hole");
        provider.addTranslation("vortex.ait.toyota", "Toyota");
        provider.addTranslation("vortex.ait.tuat", "TUAT");
        provider.addTranslation("vortex.ait.war", "War");

        // Astral Map
        provider.addTranslation("screen.ait.astral_map.show_structures", "Structures");
        provider.addTranslation("screen.ait.astral_map.show_biomes", "Biomes");

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
        provider.addTranslation("message.ait.handles.when","<Handles> Affirmative.");


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


        // Alarms
        provider.addTranslation("tardis.message.alarm.crashing", "System Alert: TARDIS is experiencing a critical failure.");
        provider.addTranslation("tardis.message.alarm.hostile_presence", "System Alert: Hostile presence detected.");

        // Security Settings Menu
        provider.addTranslation("screen.ait.sonic.button", "> Sonic Settings");
        provider.addTranslation("screen.ait.sonicsettings.back", "Back");
        provider.addTranslation("screen.ait.gravity", "> Gravity: %s");
        provider.addTranslation("screen.ait.interor_select.title", "Interior Select");
        provider.addTranslation("screen.ait.security.leave_behind", "> Leave Behind");
        provider.addTranslation("screen.ait.security.hostile_alarms", "> Hostile Alarms");
        provider.addTranslation("screen.ait.security.minimum_loyalty", "> Isomorphic LVL");
        provider.addTranslation("screen.ait.security.receive_distress_calls", "> Receive Distress Calls");

        // Interior changing
        provider.addTranslation("tardis.message.interiorchange.not_enough_fuel",
                "The TARDIS does not have enough fuel to change it's interior");
        provider.addTranslation("tardis.message.interiorchange.warning",
                "ARS initiated, console room is being reconfigured, please vacate the interior!");
        provider.addTranslation("tardis.message.interiorchange.subsystems_enabled",
                "TARDIS has %s subsystems enabled. Are you sure you want to do this?");
        provider.addTranslation("tardis.message.interiorchange.not_enough_plasmic_material", "Not enough Plasmic Material for shell: %s / 8");
        provider.addTranslation("tardis.message.interiorchange.regenerating", "Interior Reconfiguration in %s seconds");
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
        provider.addTranslation("command.ait.list.tardises", "TARDISes");
        provider.addTranslation("command.ait.list.pattern.error", "Bad pattern '%s'!");
        provider.addTranslation("command.ait.this.not_found", "Not in TARDIS interior, or no linked item.");
        provider.addTranslation("command.ait.home.dimension_locked", "Cannot set home in a dimension locked for this TARDIS.");

        // Rift Chunk Tracking
        provider.addTranslation("riftchunk.ait.tracking", "Rift Tracking");
        provider.addTranslation("riftchunk.ait.cooldown", "Rift tracking is on cooldown");
        provider.addTranslation("riftchunk.ait.found", "Rift located at this position!");
        provider.addTranslation("waypoint.position.tooltip", "Position");
        provider.addTranslation("waypoint.dimension.tooltip", "Dimension");
        provider.addTranslation("waypoint.direction.tooltip", "Direction");
        provider.addTranslation("ait.tooltip.coral_riftchunk", "Must be placed in Rift Chunk");
        provider.addTranslation("ait.tooltip.coral_soulsand", "Must be placed on Soul Sand Block");

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

        // subsystems
        provider.addTranslation(SubSystem.Id.DEMAT.toTranslationKey(), "Dematerialization Circuit");
        provider.addTranslation(SubSystem.Id.CHAMELEON.toTranslationKey(), "Chameleon Circuit");
        provider.addTranslation(SubSystem.Id.SHIELDS.toTranslationKey(), "Shield System");
        provider.addTranslation(SubSystem.Id.DESPERATION.toTranslationKey(), "Desperation");
        provider.addTranslation(SubSystem.Id.GRAVITATIONAL.toTranslationKey(), "Gravitational Modulator");
        provider.addTranslation(SubSystem.Id.LIFE_SUPPORT.toTranslationKey(), "Life Support");
        provider.addTranslation(SubSystem.Id.ENGINE.toTranslationKey(), "Engine");
        provider.addTranslation(SubSystem.Id.STABILISERS.toTranslationKey(), "Blue Stabilisers");
        provider.addTranslation(SubSystem.Id.EMERGENCY_POWER.toTranslationKey(), "Emergency Backup Power");

        // Exterior Animations
        provider.addTranslation("animation." + AITMod.MOD_ID + ".bnt_demat", "Bill & Ted");
        provider.addTranslation("animation." + AITMod.MOD_ID + ".bnt_mat", "Bill & Ted");
        provider.addTranslation("animation." + AITMod.MOD_ID + ".classic_demat", "Classic");
        provider.addTranslation("animation." + AITMod.MOD_ID + ".classic_mat", "Classic");
        provider.addTranslation("animation." + AITMod.MOD_ID + ".crumple", "Crumple");
        provider.addTranslation("animation." + AITMod.MOD_ID + ".drill_demat", "Drill");
        provider.addTranslation("animation." + AITMod.MOD_ID + ".drill_mat", "Drill");
        provider.addTranslation("animation." + AITMod.MOD_ID + ".ghost_mat", "Ghosting");
        provider.addTranslation("animation." + AITMod.MOD_ID + ".pulsating_demat", "Pulsating");
        provider.addTranslation("animation." + AITMod.MOD_ID + ".pulsating_mat", "Pulsating");
        provider.addTranslation("animation." + AITMod.MOD_ID + ".self_destruct", "Self Destruct");
        provider.addTranslation("animation." + AITMod.MOD_ID + ".zwip_demat", "Zwip");
        provider.addTranslation("animation." + AITMod.MOD_ID + ".zwip_mat", "Zwip");
        provider.addTranslation("animation." + AITMod.MOD_ID + ".eighth_demat", "Eighth");
        provider.addTranslation("animation." + AITMod.MOD_ID + ".eighth_mat", "Eighth");
        provider.addTranslation("animation." + AITMod.MOD_ID + ".proton_mat", "Proton");
        provider.addTranslation("animation." + AITMod.MOD_ID + ".proton_demat", "Proton");


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
        provider.addTranslation("tardis.message.control.siege.disabled", "Siége: Désactivé");
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
        provider.addTranslation("tardis.message.control.telepathic.home_updated", "Emplacement de base de la TARDIS modifié.");
        provider.addTranslation("tardis.message.control.telepathic.home_denied", "La TARDIS refuse de changer sa base pour vous. Niveau de loyauté PILOT requis.");
        provider.addTranslation("tardis.message.control.telepathic.home_denied_nether", "La TARDIS rejette le Nether comme base. Niveau de loyauté OWNER requis.");
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
        provider.addTranslation("command.ait.home.dimension_locked",
                "Impossible de définir la base dans une dimension verrouillée pour cette TARDIS.");

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

        provider.addTranslation("achievement.ait.description.attack_eyebrows", "¡Es un ascensor!");
        provider.addTranslation("achievement.ait.description.bonding", "Alcanza la lealtad 'Piloto' por primera vez.");
        provider.addTranslation("achievement.ait.description.brand_new", "¡DIOS MÍO! ¡LO HE VUELTO A HACER!");
        provider.addTranslation("achievement.ait.description.break_growth", "Cuida las enredaderas y la vegetación temporal que se aferran a tu TARDIS retirando la maleza.");
        provider.addTranslation("achievement.ait.description.classic_key", "Consigue una llave clásica.");
        provider.addTranslation("achievement.ait.description.enable_subsystem", "Activa un subsistema.");
        provider.addTranslation("achievement.ait.description.engines_phase", "¡LOS MOTORES ESTÁN DESFASÁNDOSE! Eso no suena nada bien.");
        provider.addTranslation("achievement.ait.description.enter_tardis", "Entra en la TARDIS por primera vez.");
        provider.addTranslation("achievement.ait.description.feed_power_converter", "Usa comida en el convertidor de energía.");
        provider.addTranslation("achievement.ait.description.first_crash", "Abraza el caos del tiempo y el espacio estrellando tu TARDIS sin querer por primera vez.");
        provider.addTranslation("achievement.ait.description.first_demat", "Inicia correctamente la secuencia de despegue y vive tu primer viaje por el tiempo y el espacio con tu TARDIS.");
        provider.addTranslation("achievement.ait.description.first_rift", "Cierra tu primera grieta espacio-temporal.");
        provider.addTranslation("achievement.ait.description.forced_entry", "Entra por la fuerza en una TARDIS.");
        provider.addTranslation("achievement.ait.description.gold_key", "Consigue una llave de oro.");
        provider.addTranslation("achievement.ait.description.iron_key", "Consigue una llave de hierro.");
        provider.addTranslation("achievement.ait.description.netherite_key", "Consigue una llave de netherita.");
        provider.addTranslation("achievement.ait.description.owner_ship", "Alcanza la lealtad 'Propietario' por primera vez.");
        provider.addTranslation("achievement.ait.description.place_coral", "Planta el coral de TARDIS, la semilla del propio tiempo.");
        provider.addTranslation("achievement.ait.description.place_energizer", "Coloca un energizador de matriz sobre un chillador de sculk generado de forma natural.");
        provider.addTranslation("achievement.ait.description.pui", "Consume polvo de Zeiton mientras la TARDIS está en vuelo.");
        provider.addTranslation("achievement.ait.description.redecorate", "Redecora el interior de tu TARDIS.");
        provider.addTranslation("achievement.ait.description.remote", "El mando Stattenheim es tuyo. Fascinante. Ahora observaremos con precisión cómo manipulas la causalidad... y cómo te fracturas bajo presión.");
        provider.addTranslation("achievement.ait.description.repair_subsystem", "Repara un subsistema averiado");
        provider.addTranslation("achievement.ait.description.root", "Descubre las maravillas del tiempo y el espacio.");
        provider.addTranslation("achievement.ait.description.ultimate_counter", "Intenta usar el destornillador sónico sobre madera.");
        provider.addTranslation("achievement.ait.title.attack_eyebrows", "Cejas de ataque");
        provider.addTranslation("achievement.ait.title.bonding", "Creo que empieza a confiar en ti.");
        provider.addTranslation("achievement.ait.title.brand_new", "¡Tomando un café!");
        provider.addTranslation("achievement.ait.title.break_growth", "Jardinero temporal");
        provider.addTranslation("achievement.ait.title.classic_key", "Aprendiz de viajero del tiempo");
        provider.addTranslation("achievement.ait.title.enable_subsystem", "Ingeniero espacio-temporal");
        provider.addTranslation("achievement.ait.title.engines_phase", "Problemas técnicos");
        provider.addTranslation("achievement.ait.title.enter_tardis", "¿Cómo cabe ahí dentro?");
        provider.addTranslation("achievement.ait.title.feed_power_converter", "¿Qué haces, Doctor?");
        provider.addTranslation("achievement.ait.title.first_crash", "Turbulencia temporal");
        provider.addTranslation("achievement.ait.title.first_demat", "Viaje inaugural");
        provider.addTranslation("achievement.ait.title.first_rift", "Es una grieta");
        provider.addTranslation("achievement.ait.title.forced_entry", "Esto no tendrá consecuencias...");
        provider.addTranslation("achievement.ait.title.gold_key", "Guardián dorado");
        provider.addTranslation("achievement.ait.title.iron_key", "Más que un trozo de metal");
        provider.addTranslation("achievement.ait.title.netherite_key", "Forjada en fuego");
        provider.addTranslation("achievement.ait.title.owner_ship", "Ahora confía en ti. Ha merecido la pena, ¿no?");
        provider.addTranslation("achievement.ait.title.place_coral", "Gurú de la jardinería");
        provider.addTranslation("achievement.ait.title.place_energizer", "Procrastinación 3000");
        provider.addTranslation("achievement.ait.title.pui", "Pilotaje bajo los efectos");
        provider.addTranslation("achievement.ait.title.redecorate", "No me gusta.");
        provider.addTranslation("achievement.ait.title.remote", "Gran diseño");
        provider.addTranslation("achievement.ait.title.repair_subsystem", "¡Manitas!");
        provider.addTranslation("achievement.ait.title.root", "Adventures in Time");
        provider.addTranslation("achievement.ait.title.ultimate_counter", "¡No sirve para madera!");
        provider.addTranslation("achievements.ait.description.enter_mars", "Aterriza en Marte por primera vez");
        provider.addTranslation("achievements.ait.description.enter_moon", "Aterriza en la Luna por primera vez");
        provider.addTranslation("achievements.ait.description.planet_root", "Explora los planetas del universo");
        provider.addTranslation("achievements.ait.find_planet_structure.description", "Pavor.");
        provider.addTranslation("achievements.ait.find_planet_structure.title", "Veneración.");
        provider.addTranslation("achievements.ait.title.enter_mars", "No fuiste el primero");
        provider.addTranslation("achievements.ait.title.enter_moon", "Un pequeño paso para los Señores del Tiempo");
        provider.addTranslation("achievements.ait.title.planet_root", "Exploración planetaria");
        provider.addTranslation("ait.blueprint.tooltip", "Plano: ");
        provider.addTranslation("ait.charged_zeiton_crystal.not_max_fuel", "¡El cristal no tiene suficiente combustible!");
        provider.addTranslation("ait.console.inventory", "Consola");
        provider.addTranslation("ait.control_disc.can_contain_players.toggle", "Puede contener jugadores: %s");
        provider.addTranslation("ait.control_disc.set_position", "Escribir en DVD-ROM");
        provider.addTranslation("ait.control_disc.unusable_in_tardis_world", "No se pudo escribir el contenido del DVD-ROM: ¡dimensión no válida!");
        provider.addTranslation("ait.foodmachine.mode.drinks", "Bebidas");
        provider.addTranslation("ait.foodmachine.mode.food_cubes", "Cubos de comida");
        provider.addTranslation("ait.foodmachine.mode.overcharged_food_cubes", "Cubos de comida sobrecargados");
        provider.addTranslation("ait.foodmachine.mode.refreshement_set_to", "Refrigerio seleccionado: %s!");
        provider.addTranslation("ait.item.drink.chocolate_milk", "Leche con chocolate");
        provider.addTranslation("ait.item.drink.coffee", "Café");
        provider.addTranslation("ait.item.drink.hot_cocoa", "Chocolate caliente");
        provider.addTranslation("ait.item.drink.iced_coffee", "Café con hielo");
        provider.addTranslation("ait.item.drink.latte", "Café con leche");
        provider.addTranslation("ait.item.drink.milk", "Leche");
        provider.addTranslation("ait.item.drink.mug_empty", "Taza vacía");
        provider.addTranslation("ait.item.drink.tea", "Té");
        provider.addTranslation("ait.item.drink.vodka", "Vodka");
        provider.addTranslation("ait.item.drink.water", "Agua");
        provider.addTranslation("ait.monitor.fuel", "AU");
        provider.addTranslation("ait.monitor.fuel_with_text", "AU: %s");
        provider.addTranslation("ait.tardis.control.throttle.stabilisers_disabled", "¡Los estabilizadores no están conectados al motor, velocidad limitada!");
        provider.addTranslation("ait.tardis.likes_item", "Puede que a la TARDIS le guste este objeto...");
        provider.addTranslation("ait.text.chat.clicked", "aquí");
        provider.addTranslation("ait.text.chat.hover", "Haz clic aquí para abrir la wiki en tu navegador");
        provider.addTranslation("ait.text.chat.readwiki", "Para aprender a jugar a Adventures in Time, te recomendamos leer la wiki, que está ");
        provider.addTranslation("ait.tooltip.coral_riftchunk", "Debe colocarse en un chunk con grieta");
        provider.addTranslation("ait.tooltip.coral_soulsand", "Debe colocarse sobre un bloque de arena de almas");
        provider.addTranslation("ait.tooltip.siege_item.enter", "Máquina temporal detectada, ¡no se puede sellar la envoltura en tiempo real!");
        provider.addTranslation("animation.ait.bnt_demat", "Bill y Ted");
        provider.addTranslation("animation.ait.bnt_mat", "Bill y Ted");
        provider.addTranslation("animation.ait.classic_demat", "Clásico");
        provider.addTranslation("animation.ait.classic_mat", "Clásico");
        provider.addTranslation("animation.ait.crumple", "Plegado");
        provider.addTranslation("animation.ait.drill_demat", "Taladro");
        provider.addTranslation("animation.ait.drill_mat", "Taladro");
        provider.addTranslation("animation.ait.eighth_demat", "Octavo");
        provider.addTranslation("animation.ait.eighth_mat", "Octavo");
        provider.addTranslation("animation.ait.ghost_mat", "Fantasmal");
        provider.addTranslation("animation.ait.proton_demat", "Protón");
        provider.addTranslation("animation.ait.proton_mat", "Protón");
        provider.addTranslation("animation.ait.pulsating_demat", "Pulsante");
        provider.addTranslation("animation.ait.pulsating_mat", "Pulsante");
        provider.addTranslation("animation.ait.self_destruct", "Autodestrucción");
        provider.addTranslation("animation.ait.zwip_demat", "Zwip");
        provider.addTranslation("animation.ait.zwip_mat", "Zwip");
        provider.addTranslation("block.ait.anorthosite", "Anortosita");
        provider.addTranslation("block.ait.anorthosite_brick_slab", "Losa de ladrillos de anortosita");
        provider.addTranslation("block.ait.anorthosite_brick_stairs", "Escaleras de ladrillos de anortosita");
        provider.addTranslation("block.ait.anorthosite_brick_wall", "Muro de ladrillos de anortosita");
        provider.addTranslation("block.ait.anorthosite_bricks", "Ladrillos de anortosita");
        provider.addTranslation("block.ait.anorthosite_coal_ore", "Mena de carbón de anortosita");
        provider.addTranslation("block.ait.anorthosite_copper_ore", "Mena de cobre de anortosita");
        provider.addTranslation("block.ait.anorthosite_diamond_ore", "Mena de diamante de anortosita");
        provider.addTranslation("block.ait.anorthosite_emerald_ore", "Mena de esmeralda de anortosita");
        provider.addTranslation("block.ait.anorthosite_gold_ore", "Mena de oro de anortosita");
        provider.addTranslation("block.ait.anorthosite_iron_ore", "Mena de hierro de anortosita");
        provider.addTranslation("block.ait.anorthosite_lapis_ore", "Mena de lapislázuli de anortosita");
        provider.addTranslation("block.ait.anorthosite_pillar", "Pilar de anortosita");
        provider.addTranslation("block.ait.anorthosite_redstone_ore", "Mena de redstone de anortosita");
        provider.addTranslation("block.ait.anorthosite_slab", "Losa de anortosita");
        provider.addTranslation("block.ait.anorthosite_stairs", "Escaleras de anortosita");
        provider.addTranslation("block.ait.anorthosite_wall", "Muro de anortosita");
        provider.addTranslation("block.ait.artron_collector_block", "Colector de artron");
        provider.addTranslation("block.ait.artron_collector_block.tooltip.use", "(Se carga dentro de chunks con grieta)");
        provider.addTranslation("block.ait.astral_map", "Mapa astral");
        provider.addTranslation("block.ait.astral_map.finder.biome_not_found", "404: BIOMA NO ENCONTRADO");
        provider.addTranslation("block.ait.astral_map.finder.found", "¡ÉXITO! ENCONTRADO EN %s, %s, %s (a %s bloques)");
        provider.addTranslation("block.ait.astral_map.finder.searching_for_biome", "BUSCANDO BIOMA...");
        provider.addTranslation("block.ait.astral_map.finder.searching_for_structure", "BUSCANDO ESTRUCTURA...");
        provider.addTranslation("block.ait.astral_map.finder.structure_not_found", "404: ESTRUCTURA NO ENCONTRADA");
        provider.addTranslation("block.ait.budding_zeiton", "Brote de Zeiton");
        provider.addTranslation("block.ait.cable_block", "Cable de artron");
        provider.addTranslation("block.ait.cable_connector_block", "Conector de cable de artron");
        provider.addTranslation("block.ait.chiseled_anorthosite", "Anortosita cincelada");
        provider.addTranslation("block.ait.chiseled_martian_sandstone", "Arenisca marciana cincelada");
        provider.addTranslation("block.ait.chiseled_martian_stone", "Piedra marciana cincelada");
        provider.addTranslation("block.ait.chiseled_moon_sandstone", "Arenisca lunar cincelada");
        provider.addTranslation("block.ait.compact_zeiton", "Zeiton compacto");
        provider.addTranslation("block.ait.console", "Consola");
        provider.addTranslation("block.ait.console_generator", "Generador de consola");
        provider.addTranslation("block.ait.coral_plant", "Crecimiento de coral");
        provider.addTranslation("block.ait.cracked_anorthosite_bricks", "Ladrillos de anortosita agrietados");
        provider.addTranslation("block.ait.cracked_martian_bricks", "Ladrillos marcianos agrietados");
        provider.addTranslation("block.ait.cracked_martian_sandstone", "Arenisca marciana agrietada");
        provider.addTranslation("block.ait.cracked_martian_sandstone_bricks", "Ladrillos de arenisca marciana agrietados");
        provider.addTranslation("block.ait.cracked_moon_sandstone", "Arenisca lunar agrietada");
        provider.addTranslation("block.ait.cracked_moon_sandstone_bricks", "Ladrillos de arenisca lunar agrietados");
        provider.addTranslation("block.ait.detector.type.alarms", "ALARMAS");
        provider.addTranslation("block.ait.detector.type.crashed", "ESTRELLADA");
        provider.addTranslation("block.ait.detector.type.door_locked", "PUERTA BLOQUEADA");
        provider.addTranslation("block.ait.detector.type.door_open", "PUERTA ABIERTA");
        provider.addTranslation("block.ait.detector.type.flight", "VUELO");
        provider.addTranslation("block.ait.detector.type.power", "ENERGÍA");
        provider.addTranslation("block.ait.detector.type.sonic", "SÓNICO");
        provider.addTranslation("block.ait.detector_block", "Bloque detector de interior");
        provider.addTranslation("block.ait.door_block", "Puerta");
        provider.addTranslation("block.ait.engine_block", "Motor");
        provider.addTranslation("block.ait.environment_projector", "Proyector ambiental");
        provider.addTranslation("block.ait.exterior_block", "Exterior");
        provider.addTranslation("block.ait.fabricator", "Fabricador");
        provider.addTranslation("block.ait.fabricator.status.collect_output", "RECOGER RESULTADO");
        provider.addTranslation("block.ait.fabricator.status.insert_blueprint", "INSERTAR PLANO");
        provider.addTranslation("block.ait.fabricator.status.insert_material", "INSERTAR %s x %s");
        provider.addTranslation("block.ait.fabricator.tooltip.use", "(Colócalo encima de una mesa de herrería)");
        provider.addTranslation("block.ait.flag", "Bandera");
        provider.addTranslation("block.ait.food_machine", "Máquina de comida");
        provider.addTranslation("block.ait.generic_subsystem", "Núcleo de subsistema generalizado");
        provider.addTranslation("block.ait.infested_martian_cobblestone", "Adoquín marciano infestado");
        provider.addTranslation("block.ait.infested_martian_stone", "Piedra marciana infestada");
        provider.addTranslation("block.ait.landing_pad", "Marcador de aterrizaje");
        provider.addTranslation("block.ait.large_zeiton_bud", "Brote de Zeiton grande");
        provider.addTranslation("block.ait.machine_casing", "Carcasa de máquina");
        provider.addTranslation("block.ait.martian_brick_slab", "Losa de ladrillos marcianos");
        provider.addTranslation("block.ait.martian_brick_stairs", "Escaleras de ladrillos marcianos");
        provider.addTranslation("block.ait.martian_brick_wall", "Muro de ladrillos marcianos");
        provider.addTranslation("block.ait.martian_bricks", "Ladrillos marcianos");
        provider.addTranslation("block.ait.martian_coal_ore", "Mena de carbón marciana");
        provider.addTranslation("block.ait.martian_cobblestone", "Adoquín marciano");
        provider.addTranslation("block.ait.martian_cobblestone_slab", "Losa de adoquín marciano");
        provider.addTranslation("block.ait.martian_cobblestone_stairs", "Escaleras de adoquín marciano");
        provider.addTranslation("block.ait.martian_cobblestone_wall", "Muro de adoquín marciano");
        provider.addTranslation("block.ait.martian_copper_ore", "Mena de cobre marciana");
        provider.addTranslation("block.ait.martian_diamond_ore", "Mena de diamante marciana");
        provider.addTranslation("block.ait.martian_emerald_ore", "Mena de esmeralda marciana");
        provider.addTranslation("block.ait.martian_gold_ore", "Mena de oro marciana");
        provider.addTranslation("block.ait.martian_iron_ore", "Mena de hierro marciana");
        provider.addTranslation("block.ait.martian_lapis_ore", "Mena de lapislázuli marciana");
        provider.addTranslation("block.ait.martian_pillar", "Pilar marciano");
        provider.addTranslation("block.ait.martian_redstone_ore", "Mena de redstone marciana");
        provider.addTranslation("block.ait.martian_sand", "Arena marciana");
        provider.addTranslation("block.ait.martian_sandstone", "Arenisca marciana");
        provider.addTranslation("block.ait.martian_sandstone_brick_slab", "Losa de ladrillos de arenisca marciana");
        provider.addTranslation("block.ait.martian_sandstone_brick_stairs", "Escaleras de ladrillos de arenisca marciana");
        provider.addTranslation("block.ait.martian_sandstone_brick_wall", "Muro de ladrillos de arenisca marciana");
        provider.addTranslation("block.ait.martian_sandstone_bricks", "Ladrillos de arenisca marciana");
        provider.addTranslation("block.ait.martian_sandstone_pillar", "Pilar de arenisca marciana");
        provider.addTranslation("block.ait.martian_sandstone_slab", "Losa de arenisca marciana");
        provider.addTranslation("block.ait.martian_sandstone_stairs", "Escaleras de arenisca marciana");
        provider.addTranslation("block.ait.martian_sandstone_wall", "Muro de arenisca marciana");
        provider.addTranslation("block.ait.martian_stone", "Piedra marciana");
        provider.addTranslation("block.ait.martian_stone_button", "Botón de piedra marciana");
        provider.addTranslation("block.ait.martian_stone_pressure_plate", "Placa de presión de piedra marciana");
        provider.addTranslation("block.ait.martian_stone_slab", "Losa de piedra marciana");
        provider.addTranslation("block.ait.martian_stone_stairs", "Escaleras de piedra marciana");
        provider.addTranslation("block.ait.martian_stone_wall", "Muro de piedra marciana");
        provider.addTranslation("block.ait.matrix_energizer", "Energizador de matriz");
        provider.addTranslation("block.ait.matrix_energizer.needs_nether_star", "El energizador necesita el poder del Wither...");
        provider.addTranslation("block.ait.medium_zeiton_bud", "Brote de Zeiton mediano");
        provider.addTranslation("block.ait.monitor_block", "Monitor");
        provider.addTranslation("block.ait.moon_sandstone", "Arenisca lunar");
        provider.addTranslation("block.ait.moon_sandstone_brick_slab", "Losa de ladrillos de arenisca lunar");
        provider.addTranslation("block.ait.moon_sandstone_brick_stairs", "Escaleras de ladrillos de arenisca lunar");
        provider.addTranslation("block.ait.moon_sandstone_brick_wall", "Muro de ladrillos de arenisca lunar");
        provider.addTranslation("block.ait.moon_sandstone_bricks", "Ladrillos de arenisca lunar");
        provider.addTranslation("block.ait.moon_sandstone_pillar", "Pilar de arenisca lunar");
        provider.addTranslation("block.ait.moon_sandstone_slab", "Losa de arenisca lunar");
        provider.addTranslation("block.ait.moon_sandstone_stairs", "Escaleras de arenisca lunar");
        provider.addTranslation("block.ait.moon_sandstone_wall", "Muro de arenisca lunar");
        provider.addTranslation("block.ait.mossy_martian_cobblestone", "Adoquín marciano musgoso");
        provider.addTranslation("block.ait.mossy_martian_cobblestone_slab", "Losa de adoquín marciano musgoso");
        provider.addTranslation("block.ait.mossy_martian_cobblestone_stairs", "Escaleras de adoquín marciano musgoso");
        provider.addTranslation("block.ait.mossy_martian_cobblestone_wall", "Muro de adoquín marciano musgoso");
        provider.addTranslation("block.ait.oxygenator_block", "Bloque oxigenador");
        provider.addTranslation("block.ait.plaque.default_text", "Cápsula TT Tipo 50");
        provider.addTranslation("block.ait.plaque.tt_capsule_type", "Cápsula TT Tipo %s");
        provider.addTranslation("block.ait.plaque_block", "Placa de TARDIS");
        provider.addTranslation("block.ait.polished_anorthosite", "Anortosita pulida");
        provider.addTranslation("block.ait.polished_anorthosite_slab", "Losa de anortosita pulida");
        provider.addTranslation("block.ait.polished_anorthosite_stairs", "Escaleras de anortosita pulida");
        provider.addTranslation("block.ait.polished_martian_sandstone", "Arenisca marciana pulida");
        provider.addTranslation("block.ait.polished_martian_stone", "Piedra marciana pulida");
        provider.addTranslation("block.ait.polished_martian_stone_slab", "Losa de piedra marciana pulida");
        provider.addTranslation("block.ait.polished_martian_stone_stairs", "Escaleras de piedra marciana pulida");
        provider.addTranslation("block.ait.polished_moon_sandstone", "Arenisca lunar pulida");
        provider.addTranslation("block.ait.power_converter", "Convertidor de energía");
        provider.addTranslation("block.ait.radio", "Radio");
        provider.addTranslation("block.ait.redstone_control_block", "Control de redstone");
        provider.addTranslation("block.ait.regolith", "Regolito");
        provider.addTranslation("block.ait.small_zeiton_bud", "Brote de Zeiton pequeño");
        provider.addTranslation("block.ait.smooth_anorthosite", "Anortosita lisa");
        provider.addTranslation("block.ait.smooth_anorthosite_slab", "Losa de anortosita lisa");
        provider.addTranslation("block.ait.smooth_martian_stone", "Piedra marciana lisa");
        provider.addTranslation("block.ait.smooth_martian_stone_slab", "Losa de piedra marciana lisa");
        provider.addTranslation("block.ait.tardis_coral_block", "Bloque de coral de TARDIS");
        provider.addTranslation("block.ait.tardis_coral_fan", "Abanico de coral de TARDIS");
        provider.addTranslation("block.ait.tardis_coral_fence", "Valla de coral de TARDIS");
        provider.addTranslation("block.ait.tardis_coral_leaves", "Hojas de coral de TARDIS");
        provider.addTranslation("block.ait.tardis_coral_slab", "Losa de coral de TARDIS");
        provider.addTranslation("block.ait.tardis_coral_stairs", "Escaleras de coral de TARDIS");
        provider.addTranslation("block.ait.tardis_coral_wall", "Muro de coral de TARDIS");
        provider.addTranslation("block.ait.untempered_schism", "Cisma intemperado");
        provider.addTranslation("block.ait.wall_monitor_block", "Monitor de pared");
        provider.addTranslation("block.ait.waypoint_bank", "Banco de destinos");
        provider.addTranslation("block.ait.zeiton_block", "Bloque de Zeiton");
        provider.addTranslation("block.ait.zeiton_cluster", "Cúmulo de Zeiton");
        provider.addTranslation("block.ait.zeiton_cobble", "Adoquín de Zeiton");
        provider.addTranslation("category.ait.config.client", "Cliente");
        provider.addTranslation("category.ait.config.server", "Servidor");
        provider.addTranslation("category.ait.main", "Adventures in Time");
        provider.addTranslation("command.ait.data.fail", "No se puede obtener el valor de una propiedad llamada %s porque el componente %s no tiene clave.");
        provider.addTranslation("command.ait.data.get", "El valor %s está establecido en '%s'");
        provider.addTranslation("command.ait.data.set", "Valor %s establecido en '%s'");
        provider.addTranslation("command.ait.door_particle.done", "Partícula de [%s] establecida en [%s]");
        provider.addTranslation("command.ait.home.dimension_locked", "No se puede establecer el hogar de esta TARDIS en una dimensión bloqueada.");
        provider.addTranslation("command.ait.list.header", "TARDIS:");
        provider.addTranslation("command.ait.list.pattern.error", "¡Patrón incorrecto '%s'!");
        provider.addTranslation("command.ait.list.tardises", "TARDIS");
        provider.addTranslation("command.ait.load.loaded", "Cargada: %s");
        provider.addTranslation("command.ait.load.not_found", "No se ha encontrado ninguna TARDIS con esa UUID.");
        provider.addTranslation("command.ait.realworld.responses", "TARDIS del mundo real generada en: ");
        provider.addTranslation("command.ait.riftchunk.cannotgetlevel", "Este chunk no es un chunk con grieta, así que no puedes obtener sus niveles de artron");
        provider.addTranslation("command.ait.riftchunk.cannotsetlevel", "Este chunk no es un chunk con grieta, así que no puedes establecer sus niveles de artron");
        provider.addTranslation("command.ait.riftchunk.getlevel", "AU en chunk con grieta: %s");
        provider.addTranslation("command.ait.riftchunk.setlevel", "Niveles de artron del chunk con grieta establecidos en: %s");
        provider.addTranslation("command.ait.scale.not_found", "No se ha encontrado la TARDIS.");
        provider.addTranslation("command.ait.this.not_found", "No estás en el interior de una TARDIS o no hay ningún objeto vinculado.");
        provider.addTranslation("command.ait.unlock.all", "Concedido a [%s]: todo desbloqueado (%s)");
        provider.addTranslation("command.ait.unlock.some", "Concedido a [%s]: %s (%s)");
        provider.addTranslation("command.ait.unlock.type.console", "consola");
        provider.addTranslation("command.ait.unlock.type.desktop", "interior");
        provider.addTranslation("command.ait.unlock.type.exterior_variant", "variante de exterior");
        provider.addTranslation("command.tardis.ait.name", "Nombre de la TARDIS: %s");
        provider.addTranslation("console.ait.alnico", "Alnico");
        provider.addTranslation("console.ait.alnico_blue", "Alnico azul");
        provider.addTranslation("console.ait.borealis", "Borealis");
        provider.addTranslation("console.ait.copper", "Cobre");
        provider.addTranslation("console.ait.coral", "Coral");
        provider.addTranslation("console.ait.coral_blue", "Coral azul");
        provider.addTranslation("console.ait.coral_decayed", "Coral deteriorado");
        provider.addTranslation("console.ait.coral_sith", "Coral Sith");
        provider.addTranslation("console.ait.coral_white", "Coral blanco");
        provider.addTranslation("console.ait.crystalline", "Cristalina");
        provider.addTranslation("console.ait.crystalline_zeiton", "Cristalina de Zeiton");
        provider.addTranslation("console.ait.exile", "Exilio");
        provider.addTranslation("console.ait.generator.requirement.none", "Ninguno");
        provider.addTranslation("console.ait.generator.requires_loyalty", "Requiere nivel de lealtad: %s");
        provider.addTranslation("console.ait.hartnell", "Hartnell");
        provider.addTranslation("console.ait.hartnell_kelt", "Hartnell Kelt");
        provider.addTranslation("console.ait.hartnell_mint", "Hartnell menta");
        provider.addTranslation("console.ait.hartnell_mint_green_console", "Hartnell verde menta");
        provider.addTranslation("console.ait.hartnell_wooden", "Hartnell de madera");
        provider.addTranslation("console.ait.hudolin", "Hudolin");
        provider.addTranslation("console.ait.hudolin_nature", "Naturaleza humana");
        provider.addTranslation("console.ait.hudolin_shalka", "Shalka");
        provider.addTranslation("console.ait.hudolin_short", "Hudolin corta");
        provider.addTranslation("console.ait.hudolin_tall", "Naturaleza humana alta");
        provider.addTranslation("console.ait.renaissance", "Renacentista");
        provider.addTranslation("console.ait.renaissance_fire", "Renacentista de fuego");
        provider.addTranslation("console.ait.renaissance_identity", "Renacentista identidad");
        provider.addTranslation("console.ait.renaissance_industrious", "Renacentista industriosa");
        provider.addTranslation("console.ait.renaissance_tokamak", "Renacentista Tokamak");
        provider.addTranslation("console.ait.steam", "Steam");
        provider.addTranslation("console.ait.steam_cherry", "Steam de cerezo");
        provider.addTranslation("console.ait.steam_copper", "Steam de cobre");
        provider.addTranslation("console.ait.steam_gilded", "Steam dorada");
        provider.addTranslation("console.ait.steam_playpal", "Steam Playpal");
        provider.addTranslation("console.ait.steam_steel", "Steam de acero");
        provider.addTranslation("console.ait.toyota", "Toyota");
        provider.addTranslation("console.ait.toyota_blue", "Toyota azul");
        provider.addTranslation("console.ait.toyota_legacy", "Toyota legado");
        provider.addTranslation("console.ait.variant_label", "Tipo de consola: ");
        provider.addTranslation("control.ait.alarms", "Alarmas");
        provider.addTranslation("control.ait.antigravs", "Antigravedad");
        provider.addTranslation("control.ait.console_port", "Puerto de consola");
        provider.addTranslation("control.ait.dimension", "Dimensión");
        provider.addTranslation("control.ait.direction", "Dirección");
        provider.addTranslation("control.ait.door_control", "Control de puerta");
        provider.addTranslation("control.ait.door_lock", "Bloqueo de puerta");
        provider.addTranslation("control.ait.eject_waypoint", "Expulsar cartucho de destino");
        provider.addTranslation("control.ait.electrical_discharge", "Repulsor exterior");
        provider.addTranslation("control.ait.engine_overload", "Sobrecarga del motor");
        provider.addTranslation("control.ait.fast_return", "Retorno rápido");
        provider.addTranslation("control.ait.food_creation", "Dispensador de refrigerios");
        provider.addTranslation("control.ait.goto_waypoint", "Ir al destino");
        provider.addTranslation("control.ait.hammer_hanger", "Colgador de maza");
        provider.addTranslation("control.ait.handbrake", "Freno de mano");
        provider.addTranslation("control.ait.increment", "Incremento");
        provider.addTranslation("control.ait.land_type", "Tipo de aterrizaje");
        provider.addTranslation("control.ait.load_control_disc.loaded", "Disco de control cargado. Listo para el despegue.");
        provider.addTranslation("control.ait.load_waypoint", "Cargar destino");
        provider.addTranslation("control.ait.load_waypoint.error", "El cartucho no contiene ningún destino");
        provider.addTranslation("control.ait.load_waypoint.no_cartridge", "No hay ningún cartucho en el puerto");
        provider.addTranslation("control.ait.monitor", "Monitor");
        provider.addTranslation("control.ait.power", "Energía");
        provider.addTranslation("control.ait.protocol_116", "Estabilizador");
        provider.addTranslation("control.ait.protocol_19", "Seguridad isomórfica");
        provider.addTranslation("control.ait.protocol_1913", "Modo asedio");
        provider.addTranslation("control.ait.protocol_3", "Camuflaje exterior");
        provider.addTranslation("control.ait.protocol_3_silent_activated", "Se ha activado el modo silencioso del camuflaje exterior");
        provider.addTranslation("control.ait.protocol_3_silent_active", "Camuflaje exterior (silencio activado)");
        provider.addTranslation("control.ait.protocol_3_silent_deactivated", "Se ha desactivado el modo silencioso del camuflaje exterior");
        provider.addTranslation("control.ait.protocol_3_silent_inactive", "Camuflaje exterior (silencio desactivado)");
        provider.addTranslation("control.ait.protocol_813", "Último recurso");
        provider.addTranslation("control.ait.randomiser", "Aleatorizador");
        provider.addTranslation("control.ait.refreshment_control", "Selector de refrigerios");
        provider.addTranslation("control.ait.refueler", "Repostador");
        provider.addTranslation("control.ait.save_waypoint", "Guardar destino");
        provider.addTranslation("control.ait.shields", "Escudos");
        provider.addTranslation("control.ait.sonic_port", "Puerto sónico");
        provider.addTranslation("control.ait.telepathic_circuit", "Circuito telepático");
        provider.addTranslation("control.ait.throttle", "Acelerador");
        provider.addTranslation("control.ait.visualiser.none", "Botón genial");
        provider.addTranslation("control.ait.visualiser.normal", "Escáner");
        provider.addTranslation("control.ait.visualiser.rwf", "Anulación manual");
        provider.addTranslation("control.ait.x", "X");
        provider.addTranslation("control.ait.y", "Y");
        provider.addTranslation("control.ait.z", "Z");
        provider.addTranslation("death.attack.space_suffocation", "¡%1$s explotó por falta de oxígeno!");
        provider.addTranslation("death.attack.tardis_squash", "¡%1$s fue aplastado por una TARDIS!");
        provider.addTranslation("desktop.ait.accursed", "Maldito");
        provider.addTranslation("desktop.ait.alnico", "Alnico");
        provider.addTranslation("desktop.ait.axos", "Axos");
        provider.addTranslation("desktop.ait.botanist", "Botánico");
        provider.addTranslation("desktop.ait.cathedral", "Catedral");
        provider.addTranslation("desktop.ait.cave", "Cueva");
        provider.addTranslation("desktop.ait.celery", "Apio");
        provider.addTranslation("desktop.ait.cherryblossom", "Flor de cerezo");
        provider.addTranslation("desktop.ait.conquista", "Conquista");
        provider.addTranslation("desktop.ait.copper", "Cobre");
        provider.addTranslation("desktop.ait.coral", "Coral");
        provider.addTranslation("desktop.ait.corpoyta", "Corpoyta");
        provider.addTranslation("desktop.ait.crystalline", "Cristalino");
        provider.addTranslation("desktop.ait.cyberpunk", "Ciberpunk");
        provider.addTranslation("desktop.ait.deco", "Deco");
        provider.addTranslation("desktop.ait.default_cave", "Cueva predeterminada");
        provider.addTranslation("desktop.ait.definitive", "Definitivo");
        provider.addTranslation("desktop.ait.dev", "Dev");
        provider.addTranslation("desktop.ait.egyptian", "Egipcio");
        provider.addTranslation("desktop.ait.exile", "Exilio");
        provider.addTranslation("desktop.ait.forest", "Bosque");
        provider.addTranslation("desktop.ait.fountain", "Fuente");
        provider.addTranslation("desktop.ait.golden", "Dorado");
        provider.addTranslation("desktop.ait.gothic", "Gótico");
        provider.addTranslation("desktop.ait.historic", "Histórico");
        provider.addTranslation("desktop.ait.hourglass", "Reloj de arena");
        provider.addTranslation("desktop.ait.hybern", "Hybern");
        provider.addTranslation("desktop.ait.industrial", "Industrial");
        provider.addTranslation("desktop.ait.laboratory", "Laboratorio");
        provider.addTranslation("desktop.ait.legacy", "Legado");
        provider.addTranslation("desktop.ait.meridian", "Meridiano");
        provider.addTranslation("desktop.ait.missy", "Missy");
        provider.addTranslation("desktop.ait.modest", "Modesto");
        provider.addTranslation("desktop.ait.mortis", "Mortis");
        provider.addTranslation("desktop.ait.newbury", "Newbury");
        provider.addTranslation("desktop.ait.observatory", "Observatorio");
        provider.addTranslation("desktop.ait.office", "Oficina");
        provider.addTranslation("desktop.ait.planetarium", "Planetario");
        provider.addTranslation("desktop.ait.pristine", "Impoluto");
        provider.addTranslation("desktop.ait.progress", "Progreso");
        provider.addTranslation("desktop.ait.regal", "Regio");
        provider.addTranslation("desktop.ait.renaissance", "Renacentista");
        provider.addTranslation("desktop.ait.renewed", "Renovado");
        provider.addTranslation("desktop.ait.shalka", "Shalka");
        provider.addTranslation("desktop.ait.steampunk", "Steampunk");
        provider.addTranslation("desktop.ait.timeless", "Intemporal");
        provider.addTranslation("desktop.ait.toyota", "Toyota");
        provider.addTranslation("desktop.ait.trek", "Trek");
        provider.addTranslation("desktop.ait.tron", "Tron");
        provider.addTranslation("desktop.ait.type_40", "Tipo 40");
        provider.addTranslation("desktop.ait.victorian", "Victoriano");
        provider.addTranslation("desktop.ait.vintage", "Vintage");
        provider.addTranslation("desktop.ait.war", "Guerra");
        provider.addTranslation("desktop.ait.war_games", "Juegos de Guerra");
        provider.addTranslation("direction.east", "Este");
        provider.addTranslation("direction.north", "Norte");
        provider.addTranslation("direction.north_east", "Nordeste");
        provider.addTranslation("direction.north_west", "Noroeste");
        provider.addTranslation("direction.south", "Sur");
        provider.addTranslation("direction.south_east", "Sudeste");
        provider.addTranslation("direction.south_west", "Sudoeste");
        provider.addTranslation("direction.west", "Oeste");
        provider.addTranslation("effect.air.lunar_sickness", "Mal lunar");
        provider.addTranslation("effect.ait.lunar_regolith", "Envenenamiento por regolito lunar");
        provider.addTranslation("effect.ait.oxygenated", "Campo oxigenador");
        provider.addTranslation("effect.ait.zeiton_high", "Subidón de Zeiton");
        provider.addTranslation("entity.ait.control_entity", "Entidad de control");
        provider.addTranslation("entity.ait.falling_tardis", "TARDIS cayendo");
        provider.addTranslation("entity.ait.flight_tardis", "TARDIS RWF");
        provider.addTranslation("entity.ait.gallifrey_falls_painting_entity_type", "Cuadro de Gallifrey Falls");
        provider.addTranslation("entity.ait.rift_entity", "Grieta espacio-temporal");
        provider.addTranslation("entity.ait.staser_bolt_entity_type", "Proyectil de rayo Staser");
        provider.addTranslation("entity.ait.trenzalore_painting_entity_type", "Cuadro de Trenzalore");
        provider.addTranslation("entity.minecraft.villager.fabricator_engineer", "Ingeniero fabricador");
        provider.addTranslation("exterior.ait.1963", "1963");
        provider.addTranslation("exterior.ait.1967", "1967");
        provider.addTranslation("exterior.ait.1970", "1970");
        provider.addTranslation("exterior.ait.1976", "1976");
        provider.addTranslation("exterior.ait.1980", "1980");
        provider.addTranslation("exterior.ait.adaptive", "Adaptativo");
        provider.addTranslation("exterior.ait.aperture_science", "Aperture Science");
        provider.addTranslation("exterior.ait.black_mesa", "Black Mesa");
        provider.addTranslation("exterior.ait.blue", "Azul");
        provider.addTranslation("exterior.ait.bookshelf", "Estantería");
        provider.addTranslation("exterior.ait.booth", "Cabina");
        provider.addTranslation("exterior.ait.bt", "BT");
        provider.addTranslation("exterior.ait.cabinet", "Armario");
        provider.addTranslation("exterior.ait.capsule", "Cápsula");
        provider.addTranslation("exterior.ait.cherrywood", "Madera de cerezo");
        provider.addTranslation("exterior.ait.classic", "Clásico");
        provider.addTranslation("exterior.ait.coral", "Coral");
        provider.addTranslation("exterior.ait.dalek_mod", "Dalek Mod");
        provider.addTranslation("exterior.ait.default", "Predeterminado");
        provider.addTranslation("exterior.ait.definitive", "Definitivo");
        provider.addTranslation("exterior.ait.doom", "DOOM");
        provider.addTranslation("exterior.ait.easter_head", "Moyai");
        provider.addTranslation("exterior.ait.exile", "Exilio");
        provider.addTranslation("exterior.ait.falloutwho", "Fallout Who");
        provider.addTranslation("exterior.ait.fire", "Fuego");
        provider.addTranslation("exterior.ait.futuristic", "Futurista");
        provider.addTranslation("exterior.ait.geometric", "Geométrico");
        provider.addTranslation("exterior.ait.geometric_blue", "Biscay");
        provider.addTranslation("exterior.ait.geometric_green", "Juegos de Guerra");
        provider.addTranslation("exterior.ait.gilded", "Dorado");
        provider.addTranslation("exterior.ait.green", "Verde");
        provider.addTranslation("exterior.ait.hudolin", "Hudolin");
        provider.addTranslation("exterior.ait.jake", "Jake");
        provider.addTranslation("exterior.ait.mint", "Menta");
        provider.addTranslation("exterior.ait.pipe", "Tubería");
        provider.addTranslation("exterior.ait.plinth", "Pedestal");
        provider.addTranslation("exterior.ait.police_box", "Cabina de policía");
        provider.addTranslation("exterior.ait.present", "Regalo");
        provider.addTranslation("exterior.ait.prime", "Prime");
        provider.addTranslation("exterior.ait.ptored", "PTORed");
        provider.addTranslation("exterior.ait.red", "Rojo");
        provider.addTranslation("exterior.ait.renaissance", "Renacentista");
        provider.addTranslation("exterior.ait.renegade", "Renegado");
        provider.addTranslation("exterior.ait.rotestor", "Rotestor");
        provider.addTranslation("exterior.ait.shalka", "Shalka");
        provider.addTranslation("exterior.ait.soul", "Alma");
        provider.addTranslation("exterior.ait.stallion", "Stallion");
        provider.addTranslation("exterior.ait.stallion_green", "Verde");
        provider.addTranslation("exterior.ait.stallion_pristine", "Impoluto");
        provider.addTranslation("exterior.ait.steel", "Acero");
        provider.addTranslation("exterior.ait.tango", "Tango");
        provider.addTranslation("exterior.ait.tardim", "TARDIM");
        provider.addTranslation("exterior.ait.tokamak", "Tokamak");
        provider.addTranslation("exterior.ait.tron", "Tron");
        provider.addTranslation("exterior.ait.vintage", "Vintage");
        provider.addTranslation("exterior.ait.wanderer", "Errante");
        provider.addTranslation("exterior.ait.white", "Blanco");
        provider.addTranslation("exterior.ait.yard", "73 yardas");
        provider.addTranslation("exterior.ait.yellow", "Amarillo");
        provider.addTranslation("exterior.ait.yeti", "Yeti");
        provider.addTranslation("exterior.frooploof.copper", "Cobre");
        provider.addTranslation("exterior.frooploof.coral_alternate", "Coral (alt.)");
        provider.addTranslation("exterior.frooploof.coral_bad_wolf", "Coral (Lobo Malo)");
        provider.addTranslation("exterior.frooploof.coral_war", "Guerra");
        provider.addTranslation("exterior.frooploof.eleven_toyota", "Toyota (1)");
        provider.addTranslation("exterior.frooploof.eleven_toyota_alternate", "Toyota (2)");
        provider.addTranslation("exterior.frooploof.tokamak_eotd", "Tokamak (EOTD)");
        provider.addTranslation("exterior.frooploof.toyota_alternate", "Toyota (3)");
        provider.addTranslation("exterior.frooploof.toyota_memorial", "Toyota (memorial)");
        provider.addTranslation("flight.ait.default", "Predeterminado");
        provider.addTranslation("flight.ait.eight", "Octavo");
        provider.addTranslation("flight.ait.proton", "Protón");
        provider.addTranslation("flight.ait.stabilize", "Estabilización");
        provider.addTranslation("flight.ait.unstable", "Inestable");
        provider.addTranslation("gamerule.tardisFireGriefing", "Daño al entorno por fuego de TARDIS");
        provider.addTranslation("gamerule.tardisGriefing", "Daño al entorno de TARDIS");
        provider.addTranslation("hum.ait.beacon", "Baliza");
        provider.addTranslation("hum.ait.christmas", "Navidad");
        provider.addTranslation("hum.ait.copper", "Cobre");
        provider.addTranslation("hum.ait.coral", "Coral");
        provider.addTranslation("hum.ait.eight", "Octavo");
        provider.addTranslation("hum.ait.exile", "Exilio");
        provider.addTranslation("hum.ait.off", "Apagado");
        provider.addTranslation("hum.ait.prime", "Prime");
        provider.addTranslation("hum.ait.renaissance", "Renacentista");
        provider.addTranslation("hum.ait.toyota", "Toyota");
        provider.addTranslation("item.ait.ait_theme_music_disc", "Disco de música");
        provider.addTranslation("item.ait.ait_theme_music_disc.desc", "RatZoomie - Adventures In Time [TEMA PRINCIPAL]");
        provider.addTranslation("item.ait.anorthosite_axe", "Hacha de anortosita");
        provider.addTranslation("item.ait.anorthosite_hoe", "Azada de anortosita");
        provider.addTranslation("item.ait.anorthosite_pickaxe", "Pico de anortosita");
        provider.addTranslation("item.ait.anorthosite_shovel", "Pala de anortosita");
        provider.addTranslation("item.ait.anorthosite_sword", "Espada de anortosita");
        provider.addTranslation("item.ait.artron_collector", "Unidad colectora de artron");
        provider.addTranslation("item.ait.artron_fluid_link", "Enlace de fluido artron");
        provider.addTranslation("item.ait.artron_mercurial_link", "Enlace mercurial artron");
        provider.addTranslation("item.ait.backup_circuit", "Circuito de respaldo");
        provider.addTranslation("item.ait.blueprint", "Plano");
        provider.addTranslation("item.ait.chameleon_circuit", "Circuito camaleónico");
        provider.addTranslation("item.ait.charged_zeiton_crystal", "Cristal de Zeiton cargado");
        provider.addTranslation("item.ait.classic_key", "Llave clásica");
        provider.addTranslation("item.ait.classic_key_upgrade_smithing_template", "Plantilla de herrería");
        provider.addTranslation("item.ait.control_disc", "Disco de control");
        provider.addTranslation("item.ait.coral_cage", "Jaula de coral");
        provider.addTranslation("item.ait.coral_fragment", "Fragmento de coral");
        provider.addTranslation("item.ait.crash_music_disc", "Disco de música");
        provider.addTranslation("item.ait.crash_music_disc.desc", "lucien - Crashing TARDIS");
        provider.addTranslation("item.ait.cult_staser", "Staser del Culto");
        provider.addTranslation("item.ait.cult_staser_rifle", "Rifle Staser del Culto");
        provider.addTranslation("item.ait.data_fluid_link", "Enlace de fluido de datos");
        provider.addTranslation("item.ait.data_mercurial_link", "Enlace mercurial de datos");
        provider.addTranslation("item.ait.dematerialization_circuit", "Circuito de desmaterialización");
        provider.addTranslation("item.ait.desperation_circuit", "Circuito de desesperación");
        provider.addTranslation("item.ait.earth_music_disc", "Disco de música");
        provider.addTranslation("item.ait.earth_music_disc.desc", "Nitrogenez - Earth");
        provider.addTranslation("item.ait.fabric", "Tela");
        provider.addTranslation("item.ait.faceless_respirator", "Respirador sin rostro");
        provider.addTranslation("item.ait.food_cube", "Cubo de comida");
        provider.addTranslation("item.ait.gallifrey_falls_painting", "Cuadro");
        provider.addTranslation("item.ait.gold_key", "Llave de oro");
        provider.addTranslation("item.ait.gold_key_upgrade_smithing_template", "Plantilla de herrería");
        provider.addTranslation("item.ait.good_man_music_disc", "Disco de música");
        provider.addTranslation("item.ait.good_man_music_disc.desc", "Dian - Good Man? [EDICIÓN RECORTADA]");
        provider.addTranslation("item.ait.gravitational_circuit", "Circuito gravitacional");
        provider.addTranslation("item.ait.hammer", "Maza");
        provider.addTranslation("item.ait.handles", "Handles");
        provider.addTranslation("item.ait.hazandra", "Hazandra");
        provider.addTranslation("item.ait.hypercube", "Hipercubo");
        provider.addTranslation("item.ait.hyperion_core_shaft", "Eje de núcleo Hyperion");
        provider.addTranslation("item.ait.iron_key", "Llave de hierro");
        provider.addTranslation("item.ait.life_support", "Soporte vital");
        provider.addTranslation("item.ait.martian_stone_axe", "Hacha de piedra marciana");
        provider.addTranslation("item.ait.martian_stone_hoe", "Azada de piedra marciana");
        provider.addTranslation("item.ait.martian_stone_pickaxe", "Pico de piedra marciana");
        provider.addTranslation("item.ait.martian_stone_shovel", "Pala de piedra marciana");
        provider.addTranslation("item.ait.martian_stone_sword", "Espada de piedra marciana");
        provider.addTranslation("item.ait.netherite_key", "Llave de netherita");
        provider.addTranslation("item.ait.netherite_key_upgrade_smithing_template", "Plantilla de herrería");
        provider.addTranslation("item.ait.orthogonal_engine_filter", "Filtro de motor ortogonal");
        provider.addTranslation("item.ait.overcharged_food_cube", "Cubo de comida sobrecargado");
        provider.addTranslation("item.ait.photon_accelerator", "Acelerador de fotones");
        provider.addTranslation("item.ait.plasmic_material", "Material plásmico");
        provider.addTranslation("item.ait.psychpaper", "Papel psíquico");
        provider.addTranslation("item.ait.remote_item", "Mando Stattenheim");
        provider.addTranslation("item.ait.repair_tool", "Herramienta de reparación");
        provider.addTranslation("item.ait.respirator", "Respirador");
        provider.addTranslation("item.ait.rift_scanner", "Escáner de grietas");
        provider.addTranslation("item.ait.shields_circuit", "Circuito de escudos");
        provider.addTranslation("item.ait.siege_item", "TARDIS");
        provider.addTranslation("item.ait.skeleton_key", "Llave maestra");
        provider.addTranslation("item.ait.sonic_screwdriver", "Destornillador sónico");
        provider.addTranslation("item.ait.spacesuit_boots", "Botas de traje espacial");
        provider.addTranslation("item.ait.spacesuit_chestplate", "Pechera de traje espacial");
        provider.addTranslation("item.ait.spacesuit_helmet", "Casco de traje espacial");
        provider.addTranslation("item.ait.spacesuit_leggings", "Pantalones de traje espacial");
        provider.addTranslation("item.ait.stabilisers", "Estabilizadores");
        provider.addTranslation("item.ait.stage_4_music_disc", "Disco de música");
        provider.addTranslation("item.ait.stage_4_music_disc.desc", "??? - [stage 4]");
        provider.addTranslation("item.ait.staser_bolt_magazine", "Cargador de rayos Staser");
        provider.addTranslation("item.ait.superheated_zeiton", "Zeiton sobrecalentado");
        provider.addTranslation("item.ait.tardis_item", "TARDIS");
        provider.addTranslation("item.ait.tardis_matrix", "Matriz de TARDIS");
        provider.addTranslation("item.ait.transwarp_resonator", "Resonador transwarp");
        provider.addTranslation("item.ait.trenzalore_painting", "Cuadro");
        provider.addTranslation("item.ait.two_thousand_music_disc", "Disco de música");
        provider.addTranslation("item.ait.two_thousand_music_disc.desc", "lucien - Two Thousand");
        provider.addTranslation("item.ait.venus_music_disc", "Disco de música");
        provider.addTranslation("item.ait.venus_music_disc.desc", "Nitrogenez - Venus");
        provider.addTranslation("item.ait.vortex_fluid_link", "Enlace de fluido del vórtice");
        provider.addTranslation("item.ait.vortex_mercurial_link", "Enlace mercurial del vórtice");
        provider.addTranslation("item.ait.waypoint_cartridge", "Cartucho de destino");
        provider.addTranslation("item.ait.wonderful_time_in_space_music_disc", "Disco de música");
        provider.addTranslation("item.ait.wonderful_time_in_space_music_disc.desc", "Dian - Wonderful Time in Space");
        provider.addTranslation("item.ait.zeiton_dust", "Polvo de Zeiton");
        provider.addTranslation("item.ait.zeiton_shard", "Fragmento de Zeiton");
        provider.addTranslation("item.sonic.scanning.any_tool", "Cualquier herramienta");
        provider.addTranslation("item.sonic.scanning.cant_break", "¡No puede romper el bloque!");
        provider.addTranslation("item.sonic.scanning.diamond_tool", "Herramienta de diamante");
        provider.addTranslation("item.sonic.scanning.iron_tool", "Herramienta de hierro");
        provider.addTranslation("item.sonic.scanning.locator_message.coordinates", "Coordenadas: %s %s %s");
        provider.addTranslation("item.sonic.scanning.locator_message.title", "Ubicación de la TARDIS: %s");
        provider.addTranslation("item.sonic.scanning.no_tool", "Mano (sin herramienta)");
        provider.addTranslation("item.sonic.scanning.stone_tool", "Herramienta de piedra");
        provider.addTranslation("itemGroup.ait.decoration", "AIT: Decoración");
        provider.addTranslation("itemGroup.ait.fabricator", "AIT: Fabricación");
        provider.addTranslation("itemGroup.ait.item_group", "Adventures In Time");
        provider.addTranslation("itemGroup.ait.planet", "AIT: Exploración planetaria");
        provider.addTranslation("key.ait.decrease_speed", "Reducir velocidad (RWF)");
        provider.addTranslation("key.ait.increase_speed", "Aumentar velocidad (RWF)");
        provider.addTranslation("key.ait.snap", "Abrir/cerrar/bloquear puertas (llave de TARDIS/RWF)");
        provider.addTranslation("key.ait.toggle_antigravs", "Alternar antigravedad (RWF)");
        provider.addTranslation("message.ait.all_types", "Consolas, exteriores, temas de interior y carcasas sónicas");
        provider.addTranslation("message.ait.ammo", "Munición: %s");
        provider.addTranslation("message.ait.artron_units", "Unidades de artron: %s");
        provider.addTranslation("message.ait.artron_units2", " AU");
        provider.addTranslation("message.ait.boti.indium_required.amd", "Parece que tienes una GPU AMD. Indium es necesario, pero no está instalado. Esto puede causar problemas con el mod: BOTI se ha desactivado.");
        provider.addTranslation("message.ait.boti.indium_required.mac", "Parece que estás jugando en un Mac. Indium es necesario, pero no está instalado. Esto puede causar problemas con el mod: BOTI se ha desactivado.");
        provider.addTranslation("message.ait.cage.empty", "(Coloca esto en un chunk con grieta)");
        provider.addTranslation("message.ait.cage.full", "Llama al vacío...");
        provider.addTranslation("message.ait.cage.void_hint", "(Lanza esto al vacío del End)");
        provider.addTranslation("message.ait.click_to_copy", "Haz clic para copiar");
        provider.addTranslation("message.ait.console_control.json_logged", "¡Datos JSON escritos en la consola Java!");
        provider.addTranslation("message.ait.console_generator.not_unlocked", "¡Esta consola aún no está desbloqueada!");
        provider.addTranslation("message.ait.control.direction.rotation", "Dirección de rotación: %s | %s");
        provider.addTranslation("message.ait.control.monitor.status", "X: %s Y: %s Z: %s Dim.: %s Combustible: %s/50000");
        provider.addTranslation("message.ait.control.xlandtype.off", "Búsqueda horizontal: DESACTIVADA");
        provider.addTranslation("message.ait.control.xlandtype.on", "Búsqueda horizontal: ACTIVADA");
        provider.addTranslation("message.ait.control.ylandtype", "Modo de búsqueda vertical: %s");
        provider.addTranslation("message.ait.control.ylandtype.ceiling", "TECHO");
        provider.addTranslation("message.ait.control.ylandtype.floor", "SUELO");
        provider.addTranslation("message.ait.control.ylandtype.median", "MEDIO");
        provider.addTranslation("message.ait.control.ylandtype.none", "NINGUNO");
        provider.addTranslation("message.ait.date_created", "Fecha de creación:");
        provider.addTranslation("message.ait.dimension.unlocked", "Desbloqueado: %s");
        provider.addTranslation("message.ait.enter_landing_code", "Código de aterrizaje");
        provider.addTranslation("message.ait.fuel.add", "Añadido %s de combustible para %s; total: [%sau]");
        provider.addTranslation("message.ait.fuel.get", "El combustible de %s es: [%sau]");
        provider.addTranslation("message.ait.fuel.max", "¡El combustible de la TARDIS está al máximo!");
        provider.addTranslation("message.ait.fuel.remove", "Retirado %s de combustible para %s; total: [%sau]");
        provider.addTranslation("message.ait.fuel.set", "Combustible establecido para %s; total: [%sau]");
        provider.addTranslation("message.ait.handles.activate_handbrake", "<Handles> Freno de mano activado.");
        provider.addTranslation("message.ait.handles.activate_refuel", "<Handles> Repostaje activado.");
        provider.addTranslation("message.ait.handles.already_in_flight", "La TARDIS ya está en vuelo...");
        provider.addTranslation("message.ait.handles.antigravs_toggled", "Antigravedad cambiada.");
        provider.addTranslation("message.ait.handles.available_commands", "Comandos disponibles: %s");
        provider.addTranslation("message.ait.handles.close_doors", "<Handles> Cerrando puertas.");
        provider.addTranslation("message.ait.handles.closing_doors", "Cerrando las puertas de la TARDIS.");
        provider.addTranslation("message.ait.handles.default", "<Handles> No he reconocido la orden.");
        provider.addTranslation("message.ait.handles.dematerializing", "Iniciando secuencia de desmaterialización.");
        provider.addTranslation("message.ait.handles.disable_handbrake", "<Handles> Freno de mano desactivado.");
        provider.addTranslation("message.ait.handles.disable_refuel", "<Handles> Repostaje desactivado.");
        provider.addTranslation("message.ait.handles.disabling_refueling", "Desactivando repostaje.");
        provider.addTranslation("message.ait.handles.displace", "<Handles> Desplazando coordenadas.");
        provider.addTranslation("message.ait.handles.doors_already_closed", "Las puertas ya están cerradas.");
        provider.addTranslation("message.ait.handles.doors_already_locked", "Las puertas ya están bloqueadas.");
        provider.addTranslation("message.ait.handles.doors_already_open", "Las puertas ya están abiertas.");
        provider.addTranslation("message.ait.handles.doors_already_unlocked", "Las puertas ya están desbloqueadas.");
        provider.addTranslation("message.ait.handles.enabling_refueling", "Activando repostaje.");
        provider.addTranslation("message.ait.handles.flight_complete", "Vuelo completado al %s%%.");
        provider.addTranslation("message.ait.handles.fun_fact.gallifrey", "Gallifrey tiene dos soles y un cielo naranja.");
        provider.addTranslation("message.ait.handles.fun_fact.green_tardis", "La primera TARDIS en realidad estaba pintada de verde.");
        provider.addTranslation("message.ait.handles.fun_fact.handles", "Handles salvó una vez la vida del Doctor resolviendo un acertijo de siglos de antigüedad.");
        provider.addTranslation("message.ait.handles.joke.calm", "¿Por qué la TARDIS siempre estaba tranquila? Porque es más grande por dentro.");
        provider.addTranslation("message.ait.handles.joke.dalek", "¿Por qué solicitó trabajo un Dalek? Porque quería EX-TER-MI-NAR a la competencia.");
        provider.addTranslation("message.ait.handles.joke.hide_and_seek", "¿Por qué la TARDIS siempre gana al escondite? Porque está en otra dimensión.");
        provider.addTranslation("message.ait.handles.joke.no_time", "¿Cómo llamas a un Señor del Tiempo sin tiempo? ¡Señor!");
        provider.addTranslation("message.ait.handles.joke.time_lords", "¿Cuántos Señores del Tiempo hacen falta para cambiar una bombilla? Ninguno, cambian la línea temporal.");
        provider.addTranslation("message.ait.handles.land", "<Handles> Aterrizando.");
        provider.addTranslation("message.ait.handles.locking_doors", "Bloqueando puertas.");
        provider.addTranslation("message.ait.handles.no_waypoint", "No hay ningún destino establecido.");
        provider.addTranslation("message.ait.handles.not_in_flight", "La TARDIS no está en vuelo.");
        provider.addTranslation("message.ait.handles.open_doors", "<Handles> Abriendo puertas.");
        provider.addTranslation("message.ait.handles.opening_doors", "Abriendo las puertas de la TARDIS.");
        provider.addTranslation("message.ait.handles.protocol_3_toggled", "Protocolo 3 cambiado.");
        provider.addTranslation("message.ait.handles.refueling_already_disabled", "El repostaje ya está desactivado.");
        provider.addTranslation("message.ait.handles.refueling_already_enabled", "El repostaje ya está activado.");
        provider.addTranslation("message.ait.handles.rematerializing", "Rematerializando.");
        provider.addTranslation("message.ait.handles.setting_course_waypoint", "Fijando rumbo al destino.");
        provider.addTranslation("message.ait.handles.take_off", "<Handles> Despegando.");
        provider.addTranslation("message.ait.handles.tardis_state", "Estado de la TARDIS: %s");
        provider.addTranslation("message.ait.handles.toggle_alarms", "<Handles> Cambiando estado de las alarmas.");
        provider.addTranslation("message.ait.handles.toggle_antigravs", "<Handles> Cambiando estado de la antigravedad.");
        provider.addTranslation("message.ait.handles.toggle_cloaked", "<Handles> Cambiando estado del camuflaje.");
        provider.addTranslation("message.ait.handles.toggle_lock", "<Handles> Cambiando estado del bloqueo.");
        provider.addTranslation("message.ait.handles.toggle_shields", "<Handles> Cambiando estado de los escudos.");
        provider.addTranslation("message.ait.handles.toggled_shields", "Escudos cambiados.");
        provider.addTranslation("message.ait.handles.unlocking_doors", "Desbloqueando puertas.");
        provider.addTranslation("message.ait.handles.when", "<Handles> Afirmativo.");
        provider.addTranslation("message.ait.hypercubes.disabled", "Los hipercubos están desactivados en la configuración del SERVIDOR.");
        provider.addTranslation("message.ait.id", "ID de TARDIS: ");
        provider.addTranslation("message.ait.keysmithing.ingredient", "Material: ");
        provider.addTranslation("message.ait.keysmithing.key", "Tipo de llave: ");
        provider.addTranslation("message.ait.keysmithing.upgrade", "Mejora");
        provider.addTranslation("message.ait.landing_code", "Código de aterrizaje...");
        provider.addTranslation("message.ait.landingpad.adjust", "Tu posición de aterrizaje ha sido ajustada");
        provider.addTranslation("message.ait.loyalty_amount", "Nivel de lealtad: %s");
        provider.addTranslation("message.ait.max_tardises", "El SERVIDOR ha alcanzado la cantidad máxima de TARDIS");
        provider.addTranslation("message.ait.oxygen", "Oxígeno almacenado: %s");
        provider.addTranslation("message.ait.projector.dimension_skys", "Cielos por dimensión");
        provider.addTranslation("message.ait.projector.no_worlds", "No hay mundos disponibles actualmente para el proyector ambiental.");
        provider.addTranslation("message.ait.projector.skybox", "Proyectando ahora: %s");
        provider.addTranslation("message.ait.projector.world", "Mundo: ");
        provider.addTranslation("message.ait.radio.changing_frequency", "Cambiando frecuencia...");
        provider.addTranslation("message.ait.radio.off", "Radio apagada");
        provider.addTranslation("message.ait.radio.on", "Radio encendida");
        provider.addTranslation("message.ait.remoteitem.cancel.refuel", "Proceso de repostaje detenido, TARDIS desmaterializándose");
        provider.addTranslation("message.ait.remoteitem.power_switch_disabled", "La TARDIS está en vuelo. Interruptor de energía desactivado.");
        provider.addTranslation("message.ait.remoteitem.powering_down", "TARDIS apagándose...");
        provider.addTranslation("message.ait.remoteitem.powering_up", "TARDIS encendiéndose...");
        provider.addTranslation("message.ait.remoteitem.success1", "TARDIS desmaterializada");
        provider.addTranslation("message.ait.remoteitem.success2", "Repostador y freno de mano activados");
        provider.addTranslation("message.ait.remoteitem.takeoff_failed_powered_off", "Despegue fallido: la TARDIS está apagada...");
        provider.addTranslation("message.ait.remoteitem.warning1", "La TARDIS no tiene combustible y no puede desmaterializarse");
        provider.addTranslation("message.ait.remoteitem.warning2", "La TARDIS está repostando y no puede desmaterializarse");
        provider.addTranslation("message.ait.remoteitem.warning3", "No se puede translocar el exterior a la dimensión interior");
        provider.addTranslation("message.ait.remoteitem.warning4", "El objetivo se ha restablecido y actualizado; ahora el dispositivo apunta hacia tu nuevo objetivo");
        provider.addTranslation("message.ait.riftscanner.info1", "Información del chunk de artron: ");
        provider.addTranslation("message.ait.riftscanner.info2", "Artron restante en el chunk: ");
        provider.addTranslation("message.ait.riftscanner.info3", "Esto no es un chunk con grieta");
        provider.addTranslation("message.ait.sonic.currenttype", "Carcasa actual: ");
        provider.addTranslation("message.ait.sonic.handbrakedisengaged", "Freno de mano desactivado, destino establecido en la posición actual");
        provider.addTranslation("message.ait.sonic.mode", "Modo: ");
        provider.addTranslation("message.ait.sonic.none", "Ninguno");
        provider.addTranslation("message.ait.sonic.repairtime", "Tiempo de reparación: %s");
        provider.addTranslation("message.ait.sonic.riftfound", "CHUNK CON GRIETA ENCONTRADO");
        provider.addTranslation("message.ait.sonic.riftnotfound", "CHUNK CON GRIETA NO ENCONTRADO");
        provider.addTranslation("message.ait.tardis.control.dimension.info", "Dimensión: ");
        provider.addTranslation("message.ait.tardis.control.dimension.none_available", "No hay dimensiones de viaje permitidas actualmente.");
        provider.addTranslation("message.ait.tardis_goat_horn.destination", "X: %s Y: %s Z: %s Dim.: %s");
        provider.addTranslation("message.ait.tooltips.artron_units", "Unidades de artron: ");
        provider.addTranslation("message.ait.unlocked", "¡'%s' desbloqueado!");
        provider.addTranslation("message.ait.unlocked_all", "Desbloqueado todo: %s");
        provider.addTranslation("message.ait.unlocked_console", "¡Interfaz de consola '%s' desbloqueada!");
        provider.addTranslation("message.ait.unlocked_exterior", "¡Exterior '%s' desbloqueado!");
        provider.addTranslation("message.ait.unlocked_interior", "¡Tema de interior '%s' desbloqueado!");
        provider.addTranslation("message.ait.unlocked_sonic", "¡Carcasa de destornillador sónico '%s' desbloqueada!");
        provider.addTranslation("message.ait.version", "ᴠᴇʀꜱɪᴏɴ");
        provider.addTranslation("message.sonic.not_damaged", "La TARDIS no está dañada");
        provider.addTranslation("overlay.ait.untempered_schism.au", "AU: %s/%s");
        provider.addTranslation("painting.ait.crab_thrower.author", "???");
        provider.addTranslation("painting.ait.crab_thrower.title", "Lanzador de cangrejos");
        provider.addTranslation("painting.ait.gallifrey_falls.author", "???");
        provider.addTranslation("painting.ait.gallifrey_falls.title", "Gallifrey Falls");
        provider.addTranslation("painting.ait.peanut.author", "???");
        provider.addTranslation("painting.ait.peanut.title", "Peanut");
        provider.addTranslation("painting.ait.trenzalore.author", "???");
        provider.addTranslation("painting.ait.trenzalore.title", "Trenzalore");
        provider.addTranslation("riftchunk.ait.cooldown", "El rastreo de grietas está en enfriamiento");
        provider.addTranslation("riftchunk.ait.found", "¡Grieta localizada en esta posición!");
        provider.addTranslation("riftchunk.ait.tracking", "Rastreo de grietas");
        provider.addTranslation("screen.ait.astral_map.biomes.button", "BIOMAS");
        provider.addTranslation("screen.ait.astral_map.loading", "CARGANDO");
        provider.addTranslation("screen.ait.astral_map.search.button", "BUSCAR");
        provider.addTranslation("screen.ait.astral_map.show_biomes", "Biomas");
        provider.addTranslation("screen.ait.astral_map.show_structures", "Estructuras");
        provider.addTranslation("screen.ait.astral_map.structures.button", "ESTRUCTURAS");
        provider.addTranslation("screen.ait.astral_map.switcher.left_arrow", "<");
        provider.addTranslation("screen.ait.astral_map.switcher.right_arrow", ">");
        provider.addTranslation("screen.ait.blueprint_fabricator", "Fabricador de planos");
        provider.addTranslation("screen.ait.current_au", "AU actuales");
        provider.addTranslation("screen.ait.environment_projector", "Proyector ambiental");
        provider.addTranslation("screen.ait.environment_projector.current", "ACTUAL: ");
        provider.addTranslation("screen.ait.environment_projector.direction.down", "ABAJO");
        provider.addTranslation("screen.ait.environment_projector.direction.east", "ESTE");
        provider.addTranslation("screen.ait.environment_projector.direction.north", "NORTE");
        provider.addTranslation("screen.ait.environment_projector.direction.south", "SUR");
        provider.addTranslation("screen.ait.environment_projector.direction.up", "ARRIBA");
        provider.addTranslation("screen.ait.environment_projector.direction.west", "OESTE");
        provider.addTranslation("screen.ait.environment_projector.enabled.off", "OFF");
        provider.addTranslation("screen.ait.environment_projector.enabled.on", "ON");
        provider.addTranslation("screen.ait.environment_projector.pitch", "Inclinación");
        provider.addTranslation("screen.ait.environment_projector.tab.direction", "Dirección");
        provider.addTranslation("screen.ait.environment_projector.tab.sky", "Cielo");
        provider.addTranslation("screen.ait.environment_projector.yaw", "Giro horizontal");
        provider.addTranslation("screen.ait.gravity", "> Gravedad: %s");
        provider.addTranslation("screen.ait.interior.settings.hum", "ZUMBIDOS");
        provider.addTranslation("screen.ait.interior_settings.mode.demat", "DESMAT.");
        provider.addTranslation("screen.ait.interior_settings.mode.flight", "VUELO");
        provider.addTranslation("screen.ait.interior_settings.mode.hum", "ZUMBIDO");
        provider.addTranslation("screen.ait.interior_settings.mode.mat", "MAT.");
        provider.addTranslation("screen.ait.interior_settings.mode.vortex", "VÓRTICE");
        provider.addTranslation("screen.ait.interiorsettings.back", "> Atrás");
        provider.addTranslation("screen.ait.interiorsettings.cacheconsole", "> Guardar consola en caché");
        provider.addTranslation("screen.ait.interiorsettings.changeinterior", "> Cambiar interior");
        provider.addTranslation("screen.ait.interiorsettings.title", "Ajustes del interior");
        provider.addTranslation("screen.ait.interor_select.title", "Selección de interior");
        provider.addTranslation("screen.ait.landing_pad", "Marcador de aterrizaje");
        provider.addTranslation("screen.ait.linked_tardis", "TARDIS vinculada");
        provider.addTranslation("screen.ait.loadsaveinterior.button", "> Guardar interior");
        provider.addTranslation("screen.ait.monitor.apply", "Aplicar");
        provider.addTranslation("screen.ait.monitor.fuel", "Combustible");
        provider.addTranslation("screen.ait.monitor.off", "OFF");
        provider.addTranslation("screen.ait.monitor.on", "ON");
        provider.addTranslation("screen.ait.monitor.shell_cloaking_activated_message", "El camuflaje exterior silencioso está activado");
        provider.addTranslation("screen.ait.monitor.traveltime", "Tiempo de viaje");
        provider.addTranslation("screen.ait.security.button", "> Opciones de seguridad");
        provider.addTranslation("screen.ait.security.hostile_alarms", "> Alarmas hostiles");
        provider.addTranslation("screen.ait.security.leave_behind", "> Dejar atrás");
        provider.addTranslation("screen.ait.security.minimum_loyalty", "> Nivel isomórfico");
        provider.addTranslation("screen.ait.security.receive_distress_calls", "> Llamadas de socorro");
        provider.addTranslation("screen.ait.security.title", "Seguridad");
        provider.addTranslation("screen.ait.sonic.button", "> Ajustes sónicos");
        provider.addTranslation("screen.ait.sonic_casing", "Carcasa sónica");
        provider.addTranslation("screen.ait.sonicsettings.back", "Atrás");
        provider.addTranslation("screen.ait.visualizer.title", "Visualizador de portal");
        provider.addTranslation("screen.ait.widget.timeline", "Línea temporal");
        provider.addTranslation("sequence.ait.anti_gravity_error", "¡Error de cálculo gravitatorio!");
        provider.addTranslation("sequence.ait.avoid_debris", "¡Escombros entrantes!");
        provider.addTranslation("sequence.ait.cloak_to_avoid_vortex_trapped_mobs", "¡Camuflaje inmediato necesario!");
        provider.addTranslation("sequence.ait.course_correct", "¡TARDIS fuera de rumbo!");
        provider.addTranslation("sequence.ait.dimensional_breach", "BRECHA DIMENSIONAL: ASEGURAD LAS PUERTAS");
        provider.addTranslation("sequence.ait.dimensional_drift_x", "¡Desviación de rumbo en X!");
        provider.addTranslation("sequence.ait.dimensional_drift_y", "¡Desviación de rumbo en Y!");
        provider.addTranslation("sequence.ait.dimensional_drift_z", "¡Desviación de rumbo en Z!");
        provider.addTranslation("sequence.ait.directional_error", "¡Error direccional!");
        provider.addTranslation("sequence.ait.energy_drain", "¡Drenaje de artron detectado!");
        provider.addTranslation("sequence.ait.ground_unstable", "¡Posición de aterrizaje inestable!");
        provider.addTranslation("sequence.ait.increment_scale_recalculation_necessary", "¡Error de escala de incremento! ¡Recalibración necesaria!");
        provider.addTranslation("sequence.ait.power_drain_imminent", "¡Drenaje de energía inminente!");
        provider.addTranslation("sequence.ait.ship_computer_offline", "¡La nave necesita reestabilización!");
        provider.addTranslation("sequence.ait.slow_down_to_avoid_flying_out_of_vortex", "Deriva del vórtice: ¡deceleración necesaria!");
        provider.addTranslation("sequence.ait.small_debris_field", "¡Pequeño campo de escombros!");
        provider.addTranslation("sequence.ait.speed_up_to_avoid_drifting_out_of_vortex", "Deriva del vórtice: ¡aceleración necesaria!");
        provider.addTranslation("sonic.ait.candy_cane", "Bastón de caramelo");
        provider.addTranslation("sonic.ait.charon", "Caronte");
        provider.addTranslation("sonic.ait.copper", "Cobre");
        provider.addTranslation("sonic.ait.coral", "Coral");
        provider.addTranslation("sonic.ait.crystalline", "Cristalino");
        provider.addTranslation("sonic.ait.fob", "Reloj de bolsillo");
        provider.addTranslation("sonic.ait.mechanical", "Mecánico");
        provider.addTranslation("sonic.ait.mode.inactive", "INACTIVO");
        provider.addTranslation("sonic.ait.mode.interaction", "INTERACCIÓN");
        provider.addTranslation("sonic.ait.mode.overload", "SOBRECARGA");
        provider.addTranslation("sonic.ait.mode.scanning", "ESCANEO");
        provider.addTranslation("sonic.ait.mode.tardis", "TARDIS");
        provider.addTranslation("sonic.ait.mode.tardis.does_not_have_power", "¡La TARDIS está apagada!");
        provider.addTranslation("sonic.ait.mode.tardis.does_not_have_stabilisers", "¡La invocación remota requiere estabilizadores!");
        provider.addTranslation("sonic.ait.mode.tardis.flight", "Freno de mano desactivado, TARDIS desmaterializándose...");
        provider.addTranslation("sonic.ait.mode.tardis.insufficient_fuel", "¡La TARDIS no tiene suficiente combustible!");
        provider.addTranslation("sonic.ait.mode.tardis.is_not_in_range", "¡La TARDIS está fuera de alcance!");
        provider.addTranslation("sonic.ait.mode.tardis.location_summon", "TARDIS invocada a tu ubicación, espera...");
        provider.addTranslation("sonic.ait.mode.tardis.refuel", "Freno de mano activado, TARDIS repostando...");
        provider.addTranslation("sonic.ait.portal_gun", "Pistola de portales");
        provider.addTranslation("sonic.ait.prime", "Prime");
        provider.addTranslation("sonic.ait.renaissance", "Renacentista");
        provider.addTranslation("sonic.ait.singularity", "Singularidad");
        provider.addTranslation("sonic.ait.song", "Song");
        provider.addTranslation("sonic.ait.type_100", "Frontera");
        provider.addTranslation("subsystem.ait.chameleon", "Circuito camaleónico");
        provider.addTranslation("subsystem.ait.demat", "Circuito de desmaterialización");
        provider.addTranslation("subsystem.ait.desperation", "Desesperación");
        provider.addTranslation("subsystem.ait.emergency_power", "Energía de emergencia de reserva");
        provider.addTranslation("subsystem.ait.engine", "Motor");
        provider.addTranslation("subsystem.ait.gravitational", "Modulador gravitacional");
        provider.addTranslation("subsystem.ait.life_support", "Soporte vital");
        provider.addTranslation("subsystem.ait.shields", "Sistema de escudos");
        provider.addTranslation("subsystem.ait.stabilisers", "Estabilizadores azules");
        provider.addTranslation("tardis.exterior.sonic.repairing", "Reparando");
        provider.addTranslation("tardis.key.identity_error", "La TARDIS no se identifica con la llave");
        provider.addTranslation("tardis.loyalty.message.companion", "La TARDIS te tararea una melodía, como si se alegrara de tenerte a bordo. [ACOMPAÑANTE]");
        provider.addTranslation("tardis.loyalty.message.neutral", "La TARDIS zumba, sin dar la bienvenida ni rechazar tu presencia. [NEUTRAL]");
        provider.addTranslation("tardis.loyalty.message.owner", "La TARDIS te tararea una canción, como para demostrar que siempre estará aquí para ti. [PROPIETARIO]");
        provider.addTranslation("tardis.loyalty.message.pilot", "La TARDIS zumba suavemente, como para mostrar su confianza. [PILOTO]");
        provider.addTranslation("tardis.loyalty.message.reject", "Oyes susurros a tu alrededor; no eres bienvenido. [RECHAZADO]");
        provider.addTranslation("tardis.loyalty.name.companion", "ACOMPAÑANTE");
        provider.addTranslation("tardis.loyalty.name.neutral", "NEUTRAL");
        provider.addTranslation("tardis.loyalty.name.owner", "PROPIETARIO");
        provider.addTranslation("tardis.loyalty.name.pilot", "PILOTO");
        provider.addTranslation("tardis.loyalty.name.reject", "RECHAZADO");
        provider.addTranslation("tardis.message.alarm.crashing", "Alerta del sistema: la TARDIS sufre un fallo crítico.");
        provider.addTranslation("tardis.message.alarm.hostile_presence", "Alerta del sistema: presencia hostil detectada.");
        provider.addTranslation("tardis.message.chameleon.failed", "¡No se pudo encontrar un disfraz adecuado!");
        provider.addTranslation("tardis.message.console.has_sonic_in_port", "No se puede almacenar la consola con el sónico en el puerto");
        provider.addTranslation("tardis.message.control.antigravs.active", "Antigravedad: ACTIVADA");
        provider.addTranslation("tardis.message.control.antigravs.inactive", "Antigravedad: DESACTIVADA");
        provider.addTranslation("tardis.message.control.electric.fail", "Error del sistema: ¡no hay suficiente combustible! Requiere %s AU");
        provider.addTranslation("tardis.message.control.engine_overdrive.dumping_artron", "PURGANDO ARTRON");
        provider.addTranslation("tardis.message.control.engine_overdrive.engines_overloaded", "ARTRON PURGADO, MOTORES SOBRECARGADOS, INICIANDO LIBERACIÓN DE ARTRON DE EMERGENCIA");
        provider.addTranslation("tardis.message.control.engine_overdrive.insufficient_fuel", "ERROR, LA TARDIS REQUIERE AL MENOS 25K DE ARTRON PARA EJECUTAR ESTA ACCIÓN.");
        provider.addTranslation("tardis.message.control.engine_overdrive.primed", "¿Purgar artron? Pulsa de nuevo para confirmar.");
        provider.addTranslation("tardis.message.control.fast_return.current_position", "Retorno rápido: POSICIÓN ACTUAL ESTABLECIDA");
        provider.addTranslation("tardis.message.control.fast_return.destination_nonexistent", "Retorno rápido: ¡la última posición no existe!");
        provider.addTranslation("tardis.message.control.fast_return.last_position", "Retorno rápido: ÚLTIMA POSICIÓN ESTABLECIDA");
        provider.addTranslation("tardis.message.control.hads.alarm_enabled", "Alarmas: ACTIVADAS");
        provider.addTranslation("tardis.message.control.hads.alarms_disabled", "Alarmas: DESACTIVADAS");
        provider.addTranslation("tardis.message.control.hail_mary.disengaged", "Último recurso: DESACTIVADO");
        provider.addTranslation("tardis.message.control.hail_mary.engaged", "Último recurso: ACTIVADO");
        provider.addTranslation("tardis.message.control.handbrake.off", "Freno de mano: DESACTIVADO");
        provider.addTranslation("tardis.message.control.handbrake.on", "Freno de mano: ACTIVADO");
        provider.addTranslation("tardis.message.control.increment.info", "Incremento: ");
        provider.addTranslation("tardis.message.control.protocol_116.active", "Estabilizador: ACTIVADO");
        provider.addTranslation("tardis.message.control.protocol_116.inactive", "Estabilizadores: DESACTIVADOS");
        provider.addTranslation("tardis.message.control.protocol_813.active", "Último recurso: ACTIVADO");
        provider.addTranslation("tardis.message.control.protocol_813.inactive", "Último recurso: DESACTIVADO");
        provider.addTranslation("tardis.message.control.randomiser.destination", "Destino: ");
        provider.addTranslation("tardis.message.control.randomiser.poscontrol", "Destino: ");
        provider.addTranslation("tardis.message.control.refueler.disabled", "Repostaje: DESACTIVADO");
        provider.addTranslation("tardis.message.control.refueler.enabled", "Repostaje: ACTIVADO");
        provider.addTranslation("tardis.message.control.rwf_creative_only", "RWF solo está disponible en CREATIVO");
        provider.addTranslation("tardis.message.control.rwf_disabled", "RWF está desactivado en la configuración del SERVIDOR.");
        provider.addTranslation("tardis.message.control.siege.disabled", "Modo asedio: DESACTIVADO");
        provider.addTranslation("tardis.message.control.siege.enabled", "Modo asedio: ACTIVADO");
        provider.addTranslation("tardis.message.control.telepathic.choosing", "La TARDIS está eligiendo...");
        provider.addTranslation("tardis.message.control.telepathic.failed", "Destino no encontrado");
        provider.addTranslation("tardis.message.control.telepathic.home_denied", "La TARDIS se niega a que cambies su hogar. Se requiere nivel de lealtad PILOTO.");
        provider.addTranslation("tardis.message.control.telepathic.home_denied_nether", "La TARDIS rechaza el Nether como hogar. Se requiere nivel de lealtad PROPIETARIO.");
        provider.addTranslation("tardis.message.control.telepathic.home_updated", "Hogar de la TARDIS actualizado.");
        provider.addTranslation("tardis.message.control.telepathic.success", "Destino encontrado");
        provider.addTranslation("tardis.message.destination_biome", "Bioma de destino: ");
        provider.addTranslation("tardis.message.engine.no_space", "¡El motor requiere un espacio de 3x3 para funcionar!");
        provider.addTranslation("tardis.message.engine.phasing", "MOTORES DESFASÁNDOSE");
        provider.addTranslation("tardis.message.engine.system_is_weakened", "¡Este sistema muestra señales de debilidad, pero sigue funcionando!");
        provider.addTranslation("tardis.message.growth.hint", "Lanza la matriz de TARDIS al agua para darle vida...");
        provider.addTranslation("tardis.message.growth.in_progress", "El crecimiento del coral sigue en progreso...");
        provider.addTranslation("tardis.message.growth.no_cage", "¡Coloca una jaula alrededor del coral de TARDIS para iniciar el recubrimiento plásmico!");
        provider.addTranslation("tardis.message.interiorchange.not_enough_fuel", "La TARDIS no tiene suficiente combustible para cambiar su interior");
        provider.addTranslation("tardis.message.interiorchange.not_enough_plasmic_material", "No hay suficiente material plásmico para la carcasa: %s / 8");
        provider.addTranslation("tardis.message.interiorchange.regenerating", "Reconfiguración interior en %s segundos");
        provider.addTranslation("tardis.message.interiorchange.subsystems_enabled", "La TARDIS tiene %s subsistemas activados. ¿Seguro que quieres hacer esto?");
        provider.addTranslation("tardis.message.interiorchange.success", "%s ha crecido hasta %d");
        provider.addTranslation("tardis.message.interiorchange.warning", "ARS iniciado, la sala de la consola se está reconfigurando; ¡evacua el interior!");
        provider.addTranslation("tardis.message.landingpad.adjust", "Ajustando a la plataforma de aterrizaje...");
        provider.addTranslation("tardis.message.protocol_813.travel", "Último recurso activo, prepárate para partir.");
        provider.addTranslation("tardis.message.self_destruct.warning", "AUTODESTRUCCIÓN INICIADA | ABANDONAD LA NAVE");
        provider.addTranslation("tardis.message.subsystem.requires_link", "VINCULAR AL MOTOR MEDIANTE CABLES ARTRON");
        provider.addTranslation("tardis.remove.done", "TARDIS [%s] eliminada");
        provider.addTranslation("tardis.remove.progress", "Eliminando TARDIS con id [%s]...");
        provider.addTranslation("tardis.repair.max", "¡Los ticks de reparación de la TARDIS están al máximo!");
        provider.addTranslation("tardis.repair.set", "Ticks de reparación de [%s] establecidos en: [%s]");
        provider.addTranslation("tardis.summon", "¡La TARDIS [%s] está en camino!");
        provider.addTranslation("tardis.teleport.exterior.success", "Teletransporte correcto: exterior de %s");
        provider.addTranslation("tardis.teleport.interior.success", "Teletransporte correcto: interior de %s");
        provider.addTranslation("tardis.tool.cannot_repair", "No se puede reparar la TARDIS con la herramienta actual");
        provider.addTranslation("text.ait.beta.play", "Autorizar para jugar a AIT Beta");
        provider.addTranslation("text.ait.beta.play.browser", "¡Revisa tu navegador!");
        provider.addTranslation("text.ait.beta.play.tooltip", "¡No eres beta tester!");
        provider.addTranslation("text.ait.config.categories", "Adventures in Time");
        provider.addTranslation("text.ait.config.title", "Adventures in Time");
        provider.addTranslation("tooltip.ait.distresscall.source", "ORIGEN");
        provider.addTranslation("tooltip.ait.items.holdformoreinfo", "Mantén Mayús para más información");
        provider.addTranslation("tooltip.ait.key.notardis", "La llave no se identifica con ninguna TARDIS");
        provider.addTranslation("tooltip.ait.linked_tardis", "TARDIS:");
        provider.addTranslation("tooltip.ait.matrix_energizer", "(Colócalo sobre un chillador de sculk\ngenerado de forma natural para producir\nuna matriz de TARDIS)");
        provider.addTranslation("tooltip.ait.position", "Posición: ");
        provider.addTranslation("tooltip.ait.power_converter", "(Convierte zeiton, lava, carbón y madera en artron)");
        provider.addTranslation("tooltip.ait.remoteitem.holdformoreinfo", "Mantén Mayús para más información");
        provider.addTranslation("tooltip.ait.remoteitem.notardis", "El mando no se identifica con ninguna TARDIS");
        provider.addTranslation("tooltip.ait.repair_tool", "Mantén pulsado el botón de uso\nmientras apuntas a un subsistema,\nun motor o unos controles averiados\npara repararlos.");
        provider.addTranslation("tooltip.ait.roundel_type", "Tipo de roundel");
        provider.addTranslation("tooltip.ait.singularity", "(Dáselo al coral de TARDIS para permitir la generación del interior)");
        provider.addTranslation("tooltip.ait.skeleton_key", "OBJETO SOLO CREATIVO: desbloquea cualquier exterior de TARDIS con él.");
        provider.addTranslation("tooltip.ait.subsystem_item", "(Úsalo en el núcleo de subsistema generalizado para establecerlo en este tipo)");
        provider.addTranslation("tooltip.ait.tardis_coral", "(Cultívalo sobre arena de almas)");
        provider.addTranslation("tooltip.ait.tardis_matrix.name", "Nombre: %s");
        provider.addTranslation("tooltip.ait.tardisdeco_type", "Tipo de decoración de TARDIS");
        provider.addTranslation("tooltip.ait.use_in_tardis", "(Colócalo dentro de una TARDIS)");
        provider.addTranslation("vortex.ait.80s", "Años 80");
        provider.addTranslation("vortex.ait.accursed", "Maldito");
        provider.addTranslation("vortex.ait.capaldi", "Capaldi");
        provider.addTranslation("vortex.ait.chronos", "Chronos");
        provider.addTranslation("vortex.ait.classic", "Clásico");
        provider.addTranslation("vortex.ait.classic_white", "Clásico blanco");
        provider.addTranslation("vortex.ait.copper", "Cobre");
        provider.addTranslation("vortex.ait.crystal", "Cristal");
        provider.addTranslation("vortex.ait.dalekmod", "Dalek Mod");
        provider.addTranslation("vortex.ait.darkness", "Oscuridad");
        provider.addTranslation("vortex.ait.galactic", "Galáctico");
        provider.addTranslation("vortex.ait.house", "House");
        provider.addTranslation("vortex.ait.lego", "LEGO");
        provider.addTranslation("vortex.ait.mccoy", "McCoy");
        provider.addTranslation("vortex.ait.movie", "Película");
        provider.addTranslation("vortex.ait.outergalaxy", "Galaxia exterior");
        provider.addTranslation("vortex.ait.outerspace", "Espacio exterior");
        provider.addTranslation("vortex.ait.peanut", "Peanut");
        provider.addTranslation("vortex.ait.pixelator", "Pixelator");
        provider.addTranslation("vortex.ait.renaissance", "Renacentista");
        provider.addTranslation("vortex.ait.rulebreaker", "Rompereglas");
        provider.addTranslation("vortex.ait.space", "Espacio");
        provider.addTranslation("vortex.ait.stargate", "Stargate");
        provider.addTranslation("vortex.ait.starlight", "Luz estelar");
        provider.addTranslation("vortex.ait.synthwave", "Synthwave");
        provider.addTranslation("vortex.ait.tennantblue", "Tennant azul");
        provider.addTranslation("vortex.ait.tennantred", "Tennant rojo");
        provider.addTranslation("vortex.ait.timehole", "Agujero temporal");
        provider.addTranslation("vortex.ait.toyota", "Toyota");
        provider.addTranslation("vortex.ait.tuat", "TUAT");
        provider.addTranslation("vortex.ait.war", "Guerra");
        provider.addTranslation("warning.ait.needs_subsystem", "ERROR, REQUIERE SUBSISTEMA ACTIVO: %s");
        provider.addTranslation("waypoint.dimension.tooltip", "Dimensión");
        provider.addTranslation("waypoint.direction.tooltip", "Dirección");
        provider.addTranslation("waypoint.position.tooltip", "Posición");
        provider.addTranslation("yacl3.config.ait:client.allowPortalsBoti", "[EXPERIMENTAL] ¿Renderizar IP BOTI?");
        provider.addTranslation("yacl3.config.ait:client.animateConsole", "¿Animar consola?");
        provider.addTranslation("yacl3.config.ait:client.animateControls", "¿Animar controles?");
        provider.addTranslation("yacl3.config.ait:client.animateDoors", "¿Animar puertas?");
        provider.addTranslation("yacl3.config.ait:client.category.client", "AIT (Cliente)");
        provider.addTranslation("yacl3.config.ait:client.customMenu", "¿Usar menú principal personalizado?");
        provider.addTranslation("yacl3.config.ait:client.enableTardisBOTI", "¿Activar BOTI de TARDIS?");
        provider.addTranslation("yacl3.config.ait:client.engineLoopVolume", "Volumen del bucle del motor");
        provider.addTranslation("yacl3.config.ait:client.environmentProjector", "¿Renderizar skybox local?");
        provider.addTranslation("yacl3.config.ait:client.flightMusicVolume", "Volumen de la música de vuelo");
        provider.addTranslation("yacl3.config.ait:client.greenScreenBOTI", "¿Pantalla verde BOTI?");
        provider.addTranslation("yacl3.config.ait:client.handlesLevenshteinDistance", "Distancia de Levenshtein para Handles");
        provider.addTranslation("yacl3.config.ait:client.interiorHumVolume", "Volumen del zumbido interior");
        provider.addTranslation("yacl3.config.ait:client.powerOffDarkness", "¿Efecto de oscuridad cuando la TARDIS se apaga?");
        provider.addTranslation("yacl3.config.ait:client.renderDematParticles", "¿Renderizar partículas de desmaterialización?");
        provider.addTranslation("yacl3.config.ait:client.screenShake", "Intensidad del temblor de pantalla");
        provider.addTranslation("yacl3.config.ait:client.showConsoleMonitorText", "¿Mostrar texto en monitores de consola?");
        provider.addTranslation("yacl3.config.ait:client.showControlHitboxes", "¿Mostrar hitboxes de controles?");
        provider.addTranslation("yacl3.config.ait:client.showCRTMonitorText", "¿Mostrar texto en monitores CRT?");
        provider.addTranslation("yacl3.config.ait:client.showExperimentalWarning", "¿Mostrar advertencia experimental?");
        provider.addTranslation("yacl3.config.ait:client.temperatureType", "Tipo de temperatura");
        provider.addTranslation("yacl3.config.ait:client.temperatureType.unit.celsius", "Celsius (°C)");
        provider.addTranslation("yacl3.config.ait:client.temperatureType.unit.fahrenheit", "Fahrenheit (°F)");
        provider.addTranslation("yacl3.config.ait:client.temperatureType.unit.kelvin", "Kelvin (K)");
        provider.addTranslation("yacl3.config.ait:server.allowPortalsBoti", "[EXPERIMENTAL] ¿Permitir IP BOTI? (requiere reiniciar)");
        provider.addTranslation("yacl3.config.ait:server.astralMapBiomeLocatorRange", "Radio en bloques del buscador de biomas del mapa astral");
        provider.addTranslation("yacl3.config.ait:server.category.server", "AIT (Servidor)");
        provider.addTranslation("yacl3.config.ait:server.crashSoundVolume", "Volumen del sonido de impacto de la TARDIS");
        provider.addTranslation("yacl3.config.ait:server.disableSafeguards", "Desactivar protecciones");
        provider.addTranslation("yacl3.config.ait:server.flightSoundVolume", "Volumen del sonido de vuelo");
        provider.addTranslation("yacl3.config.ait:server.ghostMonument", "Monumento fantasma");
        provider.addTranslation("yacl3.config.ait:server.hypercubesEnabled", "¿Activar hipercubos?");
        provider.addTranslation("yacl3.config.ait:server.lockDimensions", "¿Bloquear dimensiones?");
        provider.addTranslation("yacl3.config.ait:server.maxStabilizedSpeed", "Velocidad máxima estabilizada");
        provider.addTranslation("yacl3.config.ait:server.maxTardises", "Cantidad máxima de TARDIS");
        provider.addTranslation("yacl3.config.ait:server.minifyJson", "¿Minificar JSON?");
        provider.addTranslation("yacl3.config.ait:server.projectorBlacklist", "Dimensiones bloqueadas para el proyector ambiental");
        provider.addTranslation("yacl3.config.ait:server.projectorWhitelist", "Dimensiones permitidas para el proyector ambiental (sustituye a las bloqueadas)");
        provider.addTranslation("yacl3.config.ait:server.rwfEnabled", "¿Activar RWF?");
        provider.addTranslation("yacl3.config.ait:server.sendBulk", "¿Enviar en lote?");
        provider.addTranslation("yacl3.config.ait:server.tntCanTeleportThroughDoors", "¿Puede la TNT teletransportarse a través de puertas?");
        provider.addTranslation("yacl3.config.ait:server.travelBlacklist", "Dimensiones bloqueadas para viaje de TARDIS");
        provider.addTranslation("yacl3.config.ait:server.travelPerTick", "Viaje por tick");
        provider.addTranslation("yacl3.config.ait:server.travelWhitelist", "Dimensiones permitidas para viaje de TARDIS (sustituye a las bloqueadas)");

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
        provider.addTranslation("tardis.message.control.telepathic.home_updated", "Heimatposition der TARDIS wurde geändert.");
        provider.addTranslation("tardis.message.control.telepathic.home_denied", "Die TARDIS weigert sich, ihr Zuhause für dich zu ändern. Loyalitätsstufe PILOT erforderlich.");
        provider.addTranslation("tardis.message.control.telepathic.home_denied_nether", "Die TARDIS lehnt den Nether als Zuhause ab. Loyalitätsstufe OWNER erforderlich.");
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
        provider.addTranslation("command.ait.home.dimension_locked",
                "Heimatposition kann nicht in einer für diese TARDIS gesperrten Dimension festgelegt werden.");

        // Hums
        provider.addTranslation("hum.ait.beacon", "Beacon");
        provider.addTranslation("hum.ait.copper", "Copper");
        provider.addTranslation("hum.ait.eight", "Eight");
        provider.addTranslation("hum.ait.exile", "Exile");
        provider.addTranslation("hum.ait.prime", "Prime");
        provider.addTranslation("hum.ait.renaissance", "Renaissance");
        provider.addTranslation("hum.ait.toyota", "Toyota");
        provider.addTranslation("hum.ait.coral", "Coral");
        provider.addTranslation("hum.ait.christmas", "Christmas");
        provider.addTranslation("hum.ait.off", "Off");

        return provider;
    }

    public AmbleLanguageProvider addPortugueseTranslations(FabricDataOutput output,
                                                         CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture, LanguageType languageType) {
        AmbleLanguageProvider provider = new AmbleLanguageProvider(output, languageType);
        provider.addTranslation("tardis.message.control.telepathic.home_updated", "Local de origem da TARDIS alterado.");
        provider.addTranslation("tardis.message.control.telepathic.home_denied", "A TARDIS recusa-se a mudar sua casa para você. Nível de lealdade PILOT necessário.");
        provider.addTranslation("tardis.message.control.telepathic.home_denied_nether", "A TARDIS rejeita o Nether como casa. Nível de lealdade OWNER necessário.");
        provider.addTranslation("command.ait.home.dimension_locked", "Não é possível definir a casa em uma dimensão bloqueada para esta TARDIS.");
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
