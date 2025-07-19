package dev.amble.ait.module.planet;

import static net.minecraft.data.server.recipe.RecipeProvider.*;

import java.util.Optional;
import java.util.function.Consumer;

import dev.amble.lib.container.RegistryContainer;
import dev.amble.lib.container.impl.BlockContainer;
import dev.amble.lib.container.impl.ItemContainer;
import dev.amble.lib.datagen.lang.AmbleLanguageProvider;
import dev.amble.lib.datagen.model.AmbleModelProvider;
import dev.amble.lib.itemgroup.AItemGroup;
import dev.amble.lib.register.AmbleRegistries;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.ChangedDimensionCriterion;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.advancement.TardisCriterions;
import dev.amble.ait.datagen.datagen_providers.AITBlockTagProvider;
import dev.amble.ait.datagen.datagen_providers.AITItemTagProvider;
import dev.amble.ait.datagen.datagen_providers.AITRecipeProvider;
import dev.amble.ait.module.Module;
import dev.amble.ait.module.planet.client.SpaceSuitOverlay;
import dev.amble.ait.module.planet.core.PlanetBlockEntities;
import dev.amble.ait.module.planet.core.PlanetBlocks;
import dev.amble.ait.module.planet.core.PlanetItems;
import dev.amble.ait.module.planet.core.space.planet.PlanetRegistry;
import dev.amble.ait.module.planet.core.util.PlanetCustomTrades;

public class PlanetModule extends Module {
    private static final PlanetModule INSTANCE = new PlanetModule();

    public static final Identifier ID = AITMod.id("planet");

    @Override
    public void init() {
        PlanetCustomTrades.registerCustomTrades();
        AmbleRegistries.getInstance().register(PlanetRegistry.getInstance());

        RegistryContainer.register(PlanetItems.class, AITMod.MOD_ID);
        RegistryContainer.register(PlanetBlocks.class, AITMod.MOD_ID);
        RegistryContainer.register(PlanetBlockEntities.class, AITMod.MOD_ID);
    }

    @Override
    protected AItemGroup.Builder buildItemGroup() {
        return AItemGroup.builder(id()).icon(() -> new ItemStack(PlanetItems.SPACESUIT_HELMET));
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void initClient() {
        HudRenderCallback.EVENT.register(new SpaceSuitOverlay());
    }

    @Override
    public Identifier id() {
        return ID;
    }


    @Override
    public Optional<Class<? extends BlockContainer>> getBlockRegistry() {
        return Optional.of(PlanetBlocks.class);
    }

    @Override
    public Optional<Class<? extends ItemContainer>> getItemRegistry() {
        return Optional.of(PlanetItems.class);
    }

    @Override
    public Optional<DataGenerator> getDataGenerator() {
        return Optional.of(new DataGenerator() {
            @Override
            public void lang(AmbleLanguageProvider provider) {
                provider.addTranslation(getItemGroup(), "AIT: Planetary Exploration");
                provider.addTranslation("itemGroup.ait.planet", "AIT: Planetary Exploration");
                provider.addTranslation("message.ait.oxygen", "Stored Oxygen: %s");
                provider.addTranslation("achievements.ait.title.planet_root", "Planetary Exploration");
                provider.addTranslation("achievements.ait.description.planet_root", "Explore the planets of the universe");
                provider.addTranslation("achievements.ait.title.enter_mars", "You were not the first");
                provider.addTranslation("achievements.ait.description.enter_mars", "Landed on Mars for the first time");
                provider.addTranslation("achievements.ait.title.enter_moon", "One small step for Time Lords");
                provider.addTranslation("achievements.ait.description.enter_moon", "Landed on the Moon for the first time");
                provider.addTranslation("achievements.ait.find_planet_structure.title", "Veneration.");
                provider.addTranslation("achievements.ait.find_planet_structure.description", "Dread.");
            }

            @Override
            public void recipes(AITRecipeProvider provider) {

                // Martian
                provider.addStonecutting(PlanetBlocks.MARTIAN_STONE, PlanetBlocks.MARTIAN_BRICKS);
                provider.addStonecutting(PlanetBlocks.MARTIAN_STONE, PlanetBlocks.MARTIAN_BRICK_WALL);
                provider.addStonecutting(PlanetBlocks.MARTIAN_STONE, PlanetBlocks.MARTIAN_BRICK_STAIRS);
                provider.addStonecutting(PlanetBlocks.MARTIAN_STONE, PlanetBlocks.MARTIAN_BRICK_SLAB);
                provider.addStonecutting(PlanetBlocks.MARTIAN_STONE, PlanetBlocks.CHISELED_MARTIAN_STONE);
                provider.addStonecutting(PlanetBlocks.MARTIAN_STONE, PlanetBlocks.MARTIAN_STONE_SLAB);
                provider.addStonecutting(PlanetBlocks.MARTIAN_STONE, PlanetBlocks.MARTIAN_STONE_STAIRS);

                provider.addStonecutting(PlanetBlocks.MARTIAN_COBBLESTONE, PlanetBlocks.MARTIAN_COBBLESTONE_SLAB);
                provider.addStonecutting(PlanetBlocks.MARTIAN_COBBLESTONE, PlanetBlocks.MARTIAN_COBBLESTONE_STAIRS);
                provider.addStonecutting(PlanetBlocks.MARTIAN_COBBLESTONE, PlanetBlocks.MARTIAN_COBBLESTONE_WALL);

                provider.addStonecutting(PlanetBlocks.MARTIAN_SANDSTONE, PlanetBlocks.CHISELED_MARTIAN_SANDSTONE);
                provider.addStonecutting(PlanetBlocks.MARTIAN_SANDSTONE, PlanetBlocks.MARTIAN_SANDSTONE_PILLAR);
                provider.addStonecutting(PlanetBlocks.MARTIAN_SANDSTONE, PlanetBlocks.MARTIAN_SANDSTONE_BRICKS);
                provider.addStonecutting(PlanetBlocks.MARTIAN_SANDSTONE, PlanetBlocks.MARTIAN_SANDSTONE_SLAB);
                provider.addStonecutting(PlanetBlocks.MARTIAN_SANDSTONE, PlanetBlocks.MARTIAN_SANDSTONE_STAIRS);
                provider.addStonecutting(PlanetBlocks.MARTIAN_SANDSTONE, PlanetBlocks.MARTIAN_SANDSTONE_WALL);

                provider.addStonecutting(PlanetBlocks.MARTIAN_SANDSTONE_BRICKS, PlanetBlocks.CHISELED_MARTIAN_SANDSTONE);
                provider.addStonecutting(PlanetBlocks.MARTIAN_SANDSTONE_BRICKS, PlanetBlocks.MARTIAN_SANDSTONE_BRICK_SLAB);
                provider.addStonecutting(PlanetBlocks.MARTIAN_SANDSTONE_BRICKS, PlanetBlocks.MARTIAN_SANDSTONE_BRICK_STAIRS);
                provider.addStonecutting(PlanetBlocks.MARTIAN_SANDSTONE_BRICKS, PlanetBlocks.MARTIAN_SANDSTONE_BRICK_WALL);

                provider.addStonecutting(PlanetBlocks.MARTIAN_BRICKS, PlanetBlocks.MARTIAN_BRICK_WALL);
                provider.addStonecutting(PlanetBlocks.MARTIAN_BRICKS, PlanetBlocks.MARTIAN_BRICK_STAIRS);
                provider.addStonecutting(PlanetBlocks.MARTIAN_BRICKS, PlanetBlocks.MARTIAN_BRICK_SLAB);

                provider.addStonecutting(PlanetBlocks.SMOOTH_MARTIAN_STONE, PlanetBlocks.SMOOTH_MARTIAN_STONE_SLAB);

                provider.addStonecutting(PlanetBlocks.POLISHED_MARTIAN_STONE, PlanetBlocks.POLISHED_MARTIAN_STONE_SLAB);
                provider.addStonecutting(PlanetBlocks.POLISHED_MARTIAN_STONE, PlanetBlocks.POLISHED_MARTIAN_STONE_STAIRS);

                provider.addStonecutting(PlanetBlocks.MOSSY_MARTIAN_COBBLESTONE, PlanetBlocks.MOSSY_MARTIAN_COBBLESTONE_SLAB);
                provider.addStonecutting(PlanetBlocks.MOSSY_MARTIAN_COBBLESTONE, PlanetBlocks.MOSSY_MARTIAN_COBBLESTONE_STAIRS);

                // Anorthosite

                provider.addStonecutting(PlanetBlocks.ANORTHOSITE, PlanetBlocks.ANORTHOSITE_BRICKS);
                provider.addStonecutting(PlanetBlocks.ANORTHOSITE, PlanetBlocks.ANORTHOSITE_BRICK_WALL);
                provider.addStonecutting(PlanetBlocks.ANORTHOSITE, PlanetBlocks.ANORTHOSITE_BRICK_STAIRS);
                provider.addStonecutting(PlanetBlocks.ANORTHOSITE, PlanetBlocks.ANORTHOSITE_BRICK_SLAB);
                provider.addStonecutting(PlanetBlocks.ANORTHOSITE, PlanetBlocks.CHISELED_ANORTHOSITE);
                provider.addStonecutting(PlanetBlocks.ANORTHOSITE, PlanetBlocks.ANORTHOSITE_SLAB);
                provider.addStonecutting(PlanetBlocks.ANORTHOSITE, PlanetBlocks.ANORTHOSITE_STAIRS);
                provider.addStonecutting(PlanetBlocks.ANORTHOSITE, PlanetBlocks.ANORTHOSITE_WALL);

                provider.addStonecutting(PlanetBlocks.MOON_SANDSTONE, PlanetBlocks.CHISELED_MOON_SANDSTONE);
                provider.addStonecutting(PlanetBlocks.MOON_SANDSTONE, PlanetBlocks.MOON_SANDSTONE_PILLAR);
                provider.addStonecutting(PlanetBlocks.MOON_SANDSTONE, PlanetBlocks.MOON_SANDSTONE_BRICKS);
                provider.addStonecutting(PlanetBlocks.MOON_SANDSTONE, PlanetBlocks.MOON_SANDSTONE_SLAB);
                provider.addStonecutting(PlanetBlocks.MOON_SANDSTONE, PlanetBlocks.MOON_SANDSTONE_STAIRS);
                provider.addStonecutting(PlanetBlocks.MOON_SANDSTONE, PlanetBlocks.MOON_SANDSTONE_WALL);

                provider.addStonecutting(PlanetBlocks.MOON_SANDSTONE_BRICKS, PlanetBlocks.CHISELED_MOON_SANDSTONE);
                provider.addStonecutting(PlanetBlocks.MOON_SANDSTONE_BRICKS, PlanetBlocks.MOON_SANDSTONE_BRICK_SLAB);
                provider.addStonecutting(PlanetBlocks.MOON_SANDSTONE_BRICKS, PlanetBlocks.MOON_SANDSTONE_BRICK_STAIRS);
                provider.addStonecutting(PlanetBlocks.MOON_SANDSTONE_BRICKS, PlanetBlocks.MOON_SANDSTONE_BRICK_WALL);

                provider.addStonecutting(PlanetBlocks.ANORTHOSITE_BRICKS, PlanetBlocks.ANORTHOSITE_BRICK_WALL);
                provider.addStonecutting(PlanetBlocks.ANORTHOSITE_BRICKS, PlanetBlocks.ANORTHOSITE_BRICK_STAIRS);
                provider.addStonecutting(PlanetBlocks.ANORTHOSITE_BRICKS, PlanetBlocks.ANORTHOSITE_BRICK_SLAB);

                provider.addStonecutting(PlanetBlocks.SMOOTH_ANORTHOSITE, PlanetBlocks.SMOOTH_ANORTHOSITE_SLAB);

                provider.addStonecutting(PlanetBlocks.POLISHED_ANORTHOSITE, PlanetBlocks.POLISHED_ANORTHOSITE_SLAB);
                provider.addStonecutting(PlanetBlocks.POLISHED_ANORTHOSITE, PlanetBlocks.POLISHED_ANORTHOSITE_STAIRS);

                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, PlanetBlocks.FLAG, 1)
                        .pattern("GBR")
                        .pattern("IWW")
                        .pattern("I  ")
                        .input('G', Items.GOLD_INGOT)
                        .input('I', Items.IRON_INGOT)
                        .input('B', Items.BLUE_WOOL)
                        .input('R', Items.RED_WOOL)
                        .input('W', Items.WHITE_WOOL)
                        .criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(Items.GOLD_INGOT))
                        .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                        .criterion(hasItem(Items.RED_WOOL), conditionsFromItem(Items.RED_WOOL))
                        .criterion(hasItem(Items.BLUE_WOOL), conditionsFromItem(Items.BLUE_WOOL))
                        .criterion(hasItem(Items.WHITE_WOOL), conditionsFromItem(Items.WHITE_WOOL)));

                // tendo count your fucking days

                // anorthosite section
                // polished anorthosite
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.POLISHED_ANORTHOSITE, 4)
                        .pattern("##")
                        .pattern("##")
                        .input('#', PlanetBlocks.ANORTHOSITE)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE), conditionsFromItem(PlanetBlocks.ANORTHOSITE)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.POLISHED_ANORTHOSITE_SLAB, 6)
                        .pattern("###")
                        .input('#', PlanetBlocks.POLISHED_ANORTHOSITE)
                        .criterion(hasItem(PlanetBlocks.POLISHED_ANORTHOSITE), conditionsFromItem(PlanetBlocks.POLISHED_ANORTHOSITE)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.POLISHED_ANORTHOSITE_STAIRS, 4)
                        .pattern("#  ")
                        .pattern("## ")
                        .pattern("###")
                        .input('#', PlanetBlocks.POLISHED_ANORTHOSITE)
                        .criterion(hasItem(PlanetBlocks.POLISHED_ANORTHOSITE), conditionsFromItem(PlanetBlocks.POLISHED_ANORTHOSITE)));
                // smooth anorthosite
                provider.addFurnaceRecipe(CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(PlanetBlocks.ANORTHOSITE),
                                RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.SMOOTH_ANORTHOSITE, 0.3f, 200)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE), conditionsFromItem(PlanetBlocks.ANORTHOSITE)),
                new Identifier("ait", "smooth_anorthosite_from_anorthosite_smelted"));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.SMOOTH_ANORTHOSITE_SLAB, 6)
                        .pattern("###")
                        .input('#', PlanetBlocks.SMOOTH_ANORTHOSITE)
                        .criterion(hasItem(PlanetBlocks.SMOOTH_ANORTHOSITE), conditionsFromItem(PlanetBlocks.SMOOTH_ANORTHOSITE)));
                //normal anorthosite
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.ANORTHOSITE_WALL, 6)
                        .pattern("###")
                        .pattern("###")
                        .input('#', PlanetBlocks.ANORTHOSITE)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE), conditionsFromItem(PlanetBlocks.ANORTHOSITE)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.ANORTHOSITE_SLAB, 6)
                        .pattern("###")
                        .input('#', PlanetBlocks.ANORTHOSITE)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE), conditionsFromItem(PlanetBlocks.ANORTHOSITE)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.ANORTHOSITE_STAIRS, 4)
                        .pattern("#  ")
                        .pattern("## ")
                        .pattern("###")
                        .input('#', PlanetBlocks.ANORTHOSITE)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE), conditionsFromItem(PlanetBlocks.ANORTHOSITE)));
                // anorthosite bricks
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.ANORTHOSITE_BRICKS, 4)
                        .pattern("##")
                        .pattern("##")
                        .input('#', PlanetBlocks.SMOOTH_ANORTHOSITE)
                        .criterion(hasItem(PlanetBlocks.SMOOTH_ANORTHOSITE), conditionsFromItem(PlanetBlocks.SMOOTH_ANORTHOSITE)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.ANORTHOSITE_BRICK_SLAB, 6)
                        .pattern("###")
                        .input('#', PlanetBlocks.ANORTHOSITE_BRICKS)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE_BRICKS), conditionsFromItem(PlanetBlocks.ANORTHOSITE_BRICKS)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.ANORTHOSITE_BRICK_STAIRS, 4)
                        .pattern("#  ")
                        .pattern("## ")
                        .pattern("###")
                        .input('#', PlanetBlocks.ANORTHOSITE_BRICKS)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE_BRICKS), conditionsFromItem(PlanetBlocks.ANORTHOSITE_BRICKS)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.ANORTHOSITE_BRICK_WALL, 6)
                        .pattern("###")
                        .pattern("###")
                        .input('#', PlanetBlocks.ANORTHOSITE_BRICKS)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE_BRICKS), conditionsFromItem(PlanetBlocks.ANORTHOSITE_BRICKS)));
                //chiseled anorthosite, anorthosite pillar, cracked anorthosite brick
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.CHISELED_ANORTHOSITE, 1)
                        .pattern("#")
                        .pattern("#")
                        .input('#', PlanetBlocks.ANORTHOSITE_SLAB)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE_SLAB), conditionsFromItem(PlanetBlocks.ANORTHOSITE_SLAB)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.ANORTHOSITE_PILLAR, 1)
                        .pattern("#")
                        .pattern("#")
                        .input('#', PlanetBlocks.SMOOTH_ANORTHOSITE)
                        .criterion(hasItem(PlanetBlocks.SMOOTH_ANORTHOSITE), conditionsFromItem(PlanetBlocks.SMOOTH_ANORTHOSITE)));
                provider.addFurnaceRecipe(CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(PlanetBlocks.ANORTHOSITE_BRICKS),
                                        RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.CRACKED_ANORTHOSITE_BRICKS, 0.7f, 200)
                                .criterion(hasItem(PlanetBlocks.ANORTHOSITE_BRICKS), conditionsFromItem(PlanetBlocks.ANORTHOSITE_BRICKS)),
                        new Identifier("ait", "cracked_anorthosite_bricks_from_anorthosite_bricks_smelted"));

                // anorthosite ores
                //coal
                provider.addFurnaceRecipe(CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(PlanetBlocks.ANORTHOSITE_COAL_ORE),
                                RecipeCategory.MISC, Items.COAL, 0.7f, 200)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE_COAL_ORE), conditionsFromItem(PlanetBlocks.ANORTHOSITE_COAL_ORE)),
                new Identifier("ait", "coal_from_anorthosite_smelted"));

                provider.addBlastFurnaceRecipe(CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(PlanetBlocks.ANORTHOSITE_COAL_ORE),
                                RecipeCategory.MISC, Items.COAL, 0.7f, 100)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE_COAL_ORE), conditionsFromItem(PlanetBlocks.ANORTHOSITE_COAL_ORE)),
                new Identifier("ait", "coal_from_anorthosite_blasted"));
                //copper
                provider.addFurnaceRecipe(CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(PlanetBlocks.ANORTHOSITE_COPPER_ORE),
                                RecipeCategory.MISC, Items.COPPER_INGOT, 0.7f, 200)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE_COPPER_ORE), conditionsFromItem(PlanetBlocks.ANORTHOSITE_COPPER_ORE)),
                new Identifier("ait", "copper_from_anorthosite_smelted"));
                provider.addBlastFurnaceRecipe(CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(PlanetBlocks.ANORTHOSITE_COPPER_ORE),
                                RecipeCategory.MISC, Items.COPPER_INGOT, 0.7f, 100)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE_COPPER_ORE), conditionsFromItem(PlanetBlocks.ANORTHOSITE_COPPER_ORE)),
                new Identifier("ait", "copper_from_anorthosite_blasted"));
                //iron
                provider.addFurnaceRecipe(CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(PlanetBlocks.ANORTHOSITE_IRON_ORE),
                                RecipeCategory.MISC, Items.IRON_INGOT, 0.7f, 200)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE_IRON_ORE), conditionsFromItem(PlanetBlocks.ANORTHOSITE_IRON_ORE)),
                new Identifier("ait", "iron_from_anorthosite_smelted"));
                provider.addBlastFurnaceRecipe(CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(PlanetBlocks.ANORTHOSITE_IRON_ORE),
                                RecipeCategory.MISC, Items.IRON_INGOT, 0.7f, 100)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE_IRON_ORE), conditionsFromItem(PlanetBlocks.ANORTHOSITE_IRON_ORE)),
                new Identifier("ait", "iron_from_anorthosite_blasted"));
                //gold
                provider.addFurnaceRecipe(CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(PlanetBlocks.ANORTHOSITE_GOLD_ORE),
                                RecipeCategory.MISC, Items.GOLD_INGOT, 0.7f, 200)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE_GOLD_ORE), conditionsFromItem(PlanetBlocks.ANORTHOSITE_GOLD_ORE)),
                new Identifier("ait", "gold_from_anorthosite_smelted"));
                provider.addBlastFurnaceRecipe(CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(PlanetBlocks.ANORTHOSITE_GOLD_ORE),
                                RecipeCategory.MISC, Items.GOLD_INGOT, 0.7f, 100)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE_GOLD_ORE), conditionsFromItem(PlanetBlocks.ANORTHOSITE_GOLD_ORE)),
                new Identifier("ait", "gold_from_anorthosite_blasted"));
                //redstone
                provider.addFurnaceRecipe(CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(PlanetBlocks.ANORTHOSITE_REDSTONE_ORE),
                                RecipeCategory.MISC, Items.REDSTONE, 0.7f, 200)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE_REDSTONE_ORE), conditionsFromItem(PlanetBlocks.ANORTHOSITE_REDSTONE_ORE)),
                new Identifier("ait", "redstone_from_anorthosite_smelted"));
                provider.addBlastFurnaceRecipe(CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(PlanetBlocks.ANORTHOSITE_REDSTONE_ORE),
                                RecipeCategory.MISC, Items.REDSTONE, 0.7f, 100)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE_REDSTONE_ORE), conditionsFromItem(PlanetBlocks.ANORTHOSITE_REDSTONE_ORE)),
                new Identifier("ait", "redstone_from_anorthosite_blasted"));
                //lapis
                provider.addFurnaceRecipe(CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(PlanetBlocks.ANORTHOSITE_LAPIS_ORE),
                                RecipeCategory.MISC, Items.LAPIS_LAZULI, 0.7f, 200)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE_LAPIS_ORE), conditionsFromItem(PlanetBlocks.ANORTHOSITE_LAPIS_ORE)),
                new Identifier("ait", "lapis_from_anorthosite_smelted"));
                provider.addBlastFurnaceRecipe(CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(PlanetBlocks.ANORTHOSITE_LAPIS_ORE),
                                RecipeCategory.MISC, Items.LAPIS_LAZULI, 0.7f, 100)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE_LAPIS_ORE), conditionsFromItem(PlanetBlocks.ANORTHOSITE_LAPIS_ORE)),
                new Identifier("ait", "lapis_from_anorthosite_blasted"));
                //diamond
                provider.addFurnaceRecipe(CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(PlanetBlocks.ANORTHOSITE_DIAMOND_ORE),
                                RecipeCategory.MISC, Items.DIAMOND, 0.7f, 200)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE_DIAMOND_ORE), conditionsFromItem(PlanetBlocks.ANORTHOSITE_DIAMOND_ORE)),
                new Identifier("ait", "diamond_from_anorthosite_smelted"));
                provider.addBlastFurnaceRecipe(CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(PlanetBlocks.ANORTHOSITE_DIAMOND_ORE),
                                RecipeCategory.MISC, Items.DIAMOND, 0.7f, 100)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE_DIAMOND_ORE), conditionsFromItem(PlanetBlocks.ANORTHOSITE_DIAMOND_ORE)),
                new Identifier("ait", "diamond_from_anorthosite_blasted"));
                //emerald
                provider.addFurnaceRecipe(CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(PlanetBlocks.ANORTHOSITE_EMERALD_ORE),
                                RecipeCategory.MISC, Items.EMERALD, 0.7f, 200)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE_EMERALD_ORE), conditionsFromItem(PlanetBlocks.ANORTHOSITE_EMERALD_ORE)),
                new Identifier("ait", "emerald_from_anorthosite_smelted"));
                provider.addBlastFurnaceRecipe(CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(PlanetBlocks.ANORTHOSITE_EMERALD_ORE),
                                RecipeCategory.MISC, Items.EMERALD, 0.7f, 100)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE_EMERALD_ORE), conditionsFromItem(PlanetBlocks.ANORTHOSITE_EMERALD_ORE)),
                new Identifier("ait", "emerald_from_anorthosite_blasted"));

                // moon sandstone section
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MOON_SANDSTONE_WALL, 6)
                        .pattern("###")
                        .pattern("###")
                        .input('#', PlanetBlocks.MOON_SANDSTONE)
                        .criterion(hasItem(PlanetBlocks.MOON_SANDSTONE), conditionsFromItem(PlanetBlocks.MOON_SANDSTONE)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MOON_SANDSTONE_SLAB, 6)
                        .pattern("###")
                        .input('#', PlanetBlocks.MOON_SANDSTONE)
                        .criterion(hasItem(PlanetBlocks.MOON_SANDSTONE), conditionsFromItem(PlanetBlocks.MOON_SANDSTONE)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MOON_SANDSTONE_STAIRS, 4)
                        .pattern("#  ")
                        .pattern("## ")
                        .pattern("###")
                        .input('#', PlanetBlocks.MOON_SANDSTONE)
                        .criterion(hasItem(PlanetBlocks.MOON_SANDSTONE), conditionsFromItem(PlanetBlocks.MOON_SANDSTONE)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.POLISHED_MOON_SANDSTONE, 4)
                        .pattern("##")
                        .pattern("##")
                        .input('#', PlanetBlocks.MOON_SANDSTONE)
                        .criterion(hasItem(PlanetBlocks.MOON_SANDSTONE), conditionsFromItem(PlanetBlocks.MOON_SANDSTONE)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MOON_SANDSTONE_BRICKS, 4)
                        .pattern("##")
                        .pattern("##")
                        .input('#', PlanetBlocks.POLISHED_MOON_SANDSTONE)
                        .criterion(hasItem(PlanetBlocks.POLISHED_MOON_SANDSTONE), conditionsFromItem(PlanetBlocks.POLISHED_MOON_SANDSTONE)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MOON_SANDSTONE_BRICK_WALL, 6)
                        .pattern("###")
                        .pattern("###")
                        .input('#', PlanetBlocks.MOON_SANDSTONE_BRICKS)
                        .criterion(hasItem(PlanetBlocks.MOON_SANDSTONE_BRICKS), conditionsFromItem(PlanetBlocks.MOON_SANDSTONE_BRICKS)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MOON_SANDSTONE_BRICK_SLAB, 6)
                        .pattern("###")
                        .input('#', PlanetBlocks.MOON_SANDSTONE_BRICKS)
                        .criterion(hasItem(PlanetBlocks.MOON_SANDSTONE_BRICKS), conditionsFromItem(PlanetBlocks.MOON_SANDSTONE_BRICKS)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MOON_SANDSTONE_BRICK_STAIRS, 4)
                        .pattern("#  ")
                        .pattern("## ")
                        .pattern("###")
                        .input('#', PlanetBlocks.MOON_SANDSTONE_BRICKS)
                        .criterion(hasItem(PlanetBlocks.MOON_SANDSTONE_BRICKS), conditionsFromItem(PlanetBlocks.MOON_SANDSTONE_BRICKS)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.CHISELED_MOON_SANDSTONE, 1)
                        .pattern("#")
                        .pattern("#")
                        .input('#', PlanetBlocks.MOON_SANDSTONE_SLAB)
                        .criterion(hasItem(PlanetBlocks.MOON_SANDSTONE_SLAB), conditionsFromItem(PlanetBlocks.MOON_SANDSTONE_SLAB)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MOON_SANDSTONE_PILLAR, 1)
                        .pattern("#")
                        .pattern("#")
                        .input('#', PlanetBlocks.POLISHED_MOON_SANDSTONE)
                        .criterion(hasItem(PlanetBlocks.POLISHED_MOON_SANDSTONE), conditionsFromItem(PlanetBlocks.POLISHED_MOON_SANDSTONE)));
                provider.addFurnaceRecipe(CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(PlanetBlocks.MOON_SANDSTONE_BRICKS),
                                RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.CRACKED_MOON_SANDSTONE_BRICKS, 0.7f, 200)
                        .criterion(hasItem(PlanetBlocks.MOON_SANDSTONE_BRICKS), conditionsFromItem(PlanetBlocks.MOON_SANDSTONE_BRICKS)),
                new Identifier("ait", "cracked_moon_sandstone_bricks_from_moon_sandstone_bricks_smelted"));
                provider.addFurnaceRecipe(CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(PlanetBlocks.MOON_SANDSTONE),
                                RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.CRACKED_MOON_SANDSTONE, 0.7f, 200)
                        .criterion(hasItem(PlanetBlocks.MOON_SANDSTONE), conditionsFromItem(PlanetBlocks.MOON_SANDSTONE)),
                new Identifier("ait", "cracked_moon_sandstone_from_moon_sandstone_smelted"));

                // martian section

                // martian ores
                //coal
                provider.addFurnaceRecipe(CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(PlanetBlocks.MARTIAN_COAL_ORE),
                                RecipeCategory.MISC, Items.COAL, 0.7f, 200)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_COAL_ORE), conditionsFromItem(PlanetBlocks.MARTIAN_COAL_ORE)),
                new Identifier("ait", "coal_from_martian_smelted"));
                provider.addBlastFurnaceRecipe(CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(PlanetBlocks.MARTIAN_COAL_ORE),
                                RecipeCategory.MISC, Items.COAL, 0.7f, 100)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_COAL_ORE), conditionsFromItem(PlanetBlocks.MARTIAN_COAL_ORE)),
                new Identifier("ait", "coal_from_martian_blasted"));
                //copper
                provider.addFurnaceRecipe(CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(PlanetBlocks.MARTIAN_COPPER_ORE),
                                RecipeCategory.MISC, Items.COPPER_INGOT, 0.7f, 200)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_COPPER_ORE), conditionsFromItem(PlanetBlocks.MARTIAN_COPPER_ORE)),
                new Identifier("ait", "copper_from_martian_smelted"));
                provider.addBlastFurnaceRecipe(CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(PlanetBlocks.MARTIAN_COPPER_ORE),
                                RecipeCategory.MISC, Items.COPPER_INGOT, 0.7f, 100)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_COPPER_ORE), conditionsFromItem(PlanetBlocks.MARTIAN_COPPER_ORE)),
                new Identifier("ait", "copper_from_martian_blasted"));
                //iron
                provider.addFurnaceRecipe(CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(PlanetBlocks.MARTIAN_IRON_ORE),
                                RecipeCategory.MISC, Items.IRON_INGOT, 0.7f, 200)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_IRON_ORE), conditionsFromItem(PlanetBlocks.MARTIAN_IRON_ORE)),
                new Identifier("ait", "iron_from_martian_smelted"));
                provider.addBlastFurnaceRecipe(CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(PlanetBlocks.MARTIAN_IRON_ORE),
                                RecipeCategory.MISC, Items.IRON_INGOT, 0.7f, 100)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_IRON_ORE), conditionsFromItem(PlanetBlocks.MARTIAN_IRON_ORE)),
                new Identifier("ait", "iron_from_martian_blasted"));
                //gold
                provider.addFurnaceRecipe(CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(PlanetBlocks.MARTIAN_GOLD_ORE),
                                RecipeCategory.MISC, Items.GOLD_INGOT, 0.7f, 200)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_GOLD_ORE), conditionsFromItem(PlanetBlocks.MARTIAN_GOLD_ORE)),
                new Identifier("ait", "gold_from_martian_smelted"));
                provider.addBlastFurnaceRecipe(CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(PlanetBlocks.MARTIAN_GOLD_ORE),
                                RecipeCategory.MISC, Items.GOLD_INGOT, 0.7f, 100)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_GOLD_ORE), conditionsFromItem(PlanetBlocks.MARTIAN_GOLD_ORE)),
                new Identifier("ait", "gold_from_martian_blasted"));
                //redstone
                provider.addFurnaceRecipe(CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(PlanetBlocks.MARTIAN_REDSTONE_ORE),
                                RecipeCategory.MISC, Items.REDSTONE, 0.7f, 200)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_REDSTONE_ORE), conditionsFromItem(PlanetBlocks.MARTIAN_REDSTONE_ORE)),
                new Identifier("ait", "redstone_from_martian_smelted"));
                provider.addBlastFurnaceRecipe(CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(PlanetBlocks.MARTIAN_REDSTONE_ORE),
                                RecipeCategory.MISC, Items.REDSTONE, 0.7f, 100)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_REDSTONE_ORE), conditionsFromItem(PlanetBlocks.MARTIAN_REDSTONE_ORE)),
                new Identifier("ait", "redstone_from_martian_blasted"));
                //lapis
                provider.addFurnaceRecipe(CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(PlanetBlocks.MARTIAN_LAPIS_ORE),
                                RecipeCategory.MISC, Items.LAPIS_LAZULI, 0.7f, 200)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_LAPIS_ORE), conditionsFromItem(PlanetBlocks.MARTIAN_LAPIS_ORE)),
                new Identifier("ait", "lapis_from_martian_smelted"));
                provider.addBlastFurnaceRecipe(CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(PlanetBlocks.MARTIAN_LAPIS_ORE),
                                RecipeCategory.MISC, Items.LAPIS_LAZULI, 0.7f, 100)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_LAPIS_ORE), conditionsFromItem(PlanetBlocks.MARTIAN_LAPIS_ORE)),
                new Identifier("ait", "lapis_from_martian_blasted"));
                //diamond
                provider.addFurnaceRecipe(CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(PlanetBlocks.MARTIAN_DIAMOND_ORE),
                                RecipeCategory.MISC, Items.DIAMOND, 0.7f, 200)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_DIAMOND_ORE), conditionsFromItem(PlanetBlocks.MARTIAN_DIAMOND_ORE)),
                new Identifier("ait", "diamond_from_martian_smelted"));
                provider.addBlastFurnaceRecipe(CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(PlanetBlocks.MARTIAN_DIAMOND_ORE),
                                RecipeCategory.MISC, Items.DIAMOND, 0.7f, 100)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_DIAMOND_ORE), conditionsFromItem(PlanetBlocks.MARTIAN_DIAMOND_ORE)),
                new Identifier("ait", "diamond_from_martian_blasted"));
                //emerald
                provider.addFurnaceRecipe(CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(PlanetBlocks.MARTIAN_EMERALD_ORE),
                                RecipeCategory.MISC, Items.EMERALD, 0.7f, 200)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_EMERALD_ORE), conditionsFromItem(PlanetBlocks.MARTIAN_EMERALD_ORE)),
                new Identifier("ait", "emerald_from_martian_smelted"));
                provider.addBlastFurnaceRecipe(CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(PlanetBlocks.MARTIAN_EMERALD_ORE),
                                RecipeCategory.MISC, Items.EMERALD, 0.7f, 100)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_EMERALD_ORE), conditionsFromItem(PlanetBlocks.MARTIAN_EMERALD_ORE)),
                new Identifier("ait", "emerald_from_martian_blasted"));

                // martian stones
                provider.addShapelessRecipe(ShapelessRecipeJsonBuilder.create(RecipeCategory.REDSTONE, PlanetBlocks.MARTIAN_STONE_BUTTON, 1)
                        .input(PlanetBlocks.MARTIAN_STONE)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_STONE), conditionsFromItem(PlanetBlocks.MARTIAN_STONE)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MARTIAN_STONE_PRESSURE_PLATE, 1)
                        .pattern("##")
                        .input('#', PlanetBlocks.MARTIAN_STONE)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_STONE), conditionsFromItem(PlanetBlocks.MARTIAN_STONE)));
                provider.addFurnaceRecipe(CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(PlanetBlocks.MARTIAN_COBBLESTONE),
                                RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MARTIAN_STONE, 0.7f, 200)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_COBBLESTONE), conditionsFromItem(PlanetBlocks.MARTIAN_COBBLESTONE)),
                new Identifier("ait", "martian_stone_from_martian_cobblestone_smelted"));
                // martian cobblestones
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MARTIAN_COBBLESTONE_WALL, 6)
                        .pattern("###")
                        .pattern("###")
                        .input('#', PlanetBlocks.MARTIAN_COBBLESTONE)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_COBBLESTONE), conditionsFromItem(PlanetBlocks.MARTIAN_COBBLESTONE)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MARTIAN_COBBLESTONE_SLAB, 6)
                        .pattern("###")
                        .input('#', PlanetBlocks.MARTIAN_COBBLESTONE)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_COBBLESTONE), conditionsFromItem(PlanetBlocks.MARTIAN_COBBLESTONE)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MARTIAN_COBBLESTONE_STAIRS, 4)
                        .pattern("#  ")
                        .pattern("## ")
                        .pattern("###")
                        .input('#', PlanetBlocks.MARTIAN_COBBLESTONE)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_COBBLESTONE), conditionsFromItem(PlanetBlocks.MARTIAN_COBBLESTONE)));
                // polished martian stone
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.POLISHED_MARTIAN_STONE, 4)
                        .pattern("##")
                        .pattern("##")
                        .input('#', PlanetBlocks.MARTIAN_STONE)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_STONE), conditionsFromItem(PlanetBlocks.MARTIAN_STONE)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.POLISHED_MARTIAN_STONE_SLAB, 6)
                        .pattern("###")
                        .input('#', PlanetBlocks.POLISHED_MARTIAN_STONE)
                        .criterion(hasItem(PlanetBlocks.POLISHED_MARTIAN_STONE), conditionsFromItem(PlanetBlocks.POLISHED_MARTIAN_STONE)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.POLISHED_MARTIAN_STONE_STAIRS, 4)
                        .pattern("#  ")
                        .pattern("## ")
                        .pattern("###")
                        .input('#', PlanetBlocks.POLISHED_MARTIAN_STONE)
                        .criterion(hasItem(PlanetBlocks.POLISHED_MARTIAN_STONE), conditionsFromItem(PlanetBlocks.POLISHED_MARTIAN_STONE)));
                // smooth martian stone
                provider.addFurnaceRecipe(CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(PlanetBlocks.MARTIAN_STONE),
                                RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.SMOOTH_MARTIAN_STONE, 0.7f, 200)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_STONE), conditionsFromItem(PlanetBlocks.MARTIAN_STONE)),
                new Identifier("ait", "smooth_martian_stone_from_martian_stone_smelted"));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.SMOOTH_MARTIAN_STONE_SLAB, 6)
                        .pattern("###")
                        .input('#', PlanetBlocks.SMOOTH_MARTIAN_STONE)
                        .criterion(hasItem(PlanetBlocks.SMOOTH_MARTIAN_STONE), conditionsFromItem(PlanetBlocks.SMOOTH_MARTIAN_STONE)));
                // martian sandstone
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MARTIAN_SANDSTONE, 1)
                        .pattern("##")
                        .pattern("##")
                        .input('#', PlanetBlocks.MARTIAN_SAND)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_SAND), conditionsFromItem(PlanetBlocks.MARTIAN_SAND)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.POLISHED_MARTIAN_SANDSTONE, 4)
                        .pattern("##")
                        .pattern("##")
                        .input('#', PlanetBlocks.MARTIAN_SANDSTONE)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_SANDSTONE), conditionsFromItem(PlanetBlocks.MARTIAN_SANDSTONE)));
                provider.addFurnaceRecipe(CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(PlanetBlocks.MARTIAN_SANDSTONE),
                                RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.CRACKED_MARTIAN_SANDSTONE, 0.7f, 200)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_SANDSTONE), conditionsFromItem(PlanetBlocks.MARTIAN_SANDSTONE)),
                new Identifier("ait", "cracked_martian_sandstone_from_martian_sandstone_smelted"));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MARTIAN_SANDSTONE_WALL, 6)
                        .pattern("###")
                        .pattern("###")
                        .input('#', PlanetBlocks.MARTIAN_SANDSTONE)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_SANDSTONE), conditionsFromItem(PlanetBlocks.MARTIAN_SANDSTONE)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MARTIAN_SANDSTONE_SLAB, 6)
                        .pattern("###")
                        .input('#', PlanetBlocks.MARTIAN_SANDSTONE)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_SANDSTONE), conditionsFromItem(PlanetBlocks.MARTIAN_SANDSTONE)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MARTIAN_SANDSTONE_STAIRS, 4)
                        .pattern("#  ")
                        .pattern("## ")
                        .pattern("###")
                        .input('#', PlanetBlocks.MARTIAN_SANDSTONE)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_SANDSTONE), conditionsFromItem(PlanetBlocks.MARTIAN_SANDSTONE)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MARTIAN_SANDSTONE_PILLAR, 1)
                        .pattern("#")
                        .pattern("#")
                        .input('#', PlanetBlocks.POLISHED_MARTIAN_SANDSTONE)
                        .criterion(hasItem(PlanetBlocks.POLISHED_MARTIAN_SANDSTONE), conditionsFromItem(PlanetBlocks.POLISHED_MARTIAN_SANDSTONE)));
                // martian sandstone bricks, martian sandstone brick wall, slab, stairs, cracked, chiseled
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MARTIAN_SANDSTONE_BRICKS, 4)
                        .pattern("##")
                        .pattern("##")
                        .input('#', PlanetBlocks.POLISHED_MARTIAN_SANDSTONE)
                        .criterion(hasItem(PlanetBlocks.POLISHED_MARTIAN_SANDSTONE), conditionsFromItem(PlanetBlocks.POLISHED_MARTIAN_SANDSTONE)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MARTIAN_SANDSTONE_BRICK_WALL, 6)
                        .pattern("###")
                        .pattern("###")
                        .input('#', PlanetBlocks.MARTIAN_SANDSTONE_BRICKS)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_SANDSTONE_BRICKS), conditionsFromItem(PlanetBlocks.MARTIAN_SANDSTONE_BRICKS)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MARTIAN_SANDSTONE_BRICK_SLAB, 6)
                        .pattern("###")
                        .input('#', PlanetBlocks.MARTIAN_SANDSTONE_BRICKS)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_SANDSTONE_BRICKS), conditionsFromItem(PlanetBlocks.MARTIAN_SANDSTONE_BRICKS)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MARTIAN_SANDSTONE_BRICK_STAIRS, 4)
                        .pattern("#  ")
                        .pattern("## ")
                        .pattern("###")
                        .input('#', PlanetBlocks.MARTIAN_SANDSTONE_BRICKS)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_SANDSTONE_BRICKS), conditionsFromItem(PlanetBlocks.MARTIAN_SANDSTONE_BRICKS)));
                provider.addFurnaceRecipe(CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(PlanetBlocks.MARTIAN_SANDSTONE_BRICKS),
                                RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.CRACKED_MARTIAN_SANDSTONE_BRICKS, 0.7f, 200)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_SANDSTONE_BRICKS), conditionsFromItem(PlanetBlocks.MARTIAN_SANDSTONE_BRICKS)),
                new Identifier("ait", "cracked_martian_sandstone_bricks_from_martian_sandstone_bricks_smelted"));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.CHISELED_MARTIAN_SANDSTONE, 1)
                        .pattern("#")
                        .pattern("#")
                        .input('#', PlanetBlocks.MARTIAN_SANDSTONE_SLAB)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_SANDSTONE_SLAB), conditionsFromItem(PlanetBlocks.MARTIAN_SANDSTONE_SLAB)));
                // martian bricks
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MARTIAN_BRICKS, 4)
                        .pattern("##")
                        .pattern("##")
                        .input('#', PlanetBlocks.SMOOTH_MARTIAN_STONE)
                        .criterion(hasItem(PlanetBlocks.SMOOTH_MARTIAN_STONE), conditionsFromItem(PlanetBlocks.SMOOTH_MARTIAN_STONE)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MARTIAN_BRICK_WALL, 6)
                        .pattern("###")
                        .pattern("###")
                        .input('#', PlanetBlocks.MARTIAN_BRICKS)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_BRICKS), conditionsFromItem(PlanetBlocks.MARTIAN_BRICKS)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MARTIAN_BRICK_SLAB, 6)
                        .pattern("###")
                        .input('#', PlanetBlocks.MARTIAN_BRICKS)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_BRICKS), conditionsFromItem(PlanetBlocks.MARTIAN_BRICKS)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MARTIAN_BRICK_STAIRS, 4)
                        .pattern("#  ")
                        .pattern("## ")
                        .pattern("###")
                        .input('#', PlanetBlocks.MARTIAN_BRICKS)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_BRICKS), conditionsFromItem(PlanetBlocks.MARTIAN_BRICKS)));
                provider.addFurnaceRecipe(CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(PlanetBlocks.MARTIAN_BRICKS),
                                RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.CRACKED_MARTIAN_BRICKS, 0.7f, 200)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_BRICKS), conditionsFromItem(PlanetBlocks.MARTIAN_BRICKS)),
                new Identifier("ait", "cracked_martian_bricks_from_martian_bricks_smelted"));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MARTIAN_PILLAR, 1)
                        .pattern("#")
                        .pattern("#")
                        .input('#', PlanetBlocks.POLISHED_MARTIAN_STONE)
                        .criterion(hasItem(PlanetBlocks.POLISHED_MARTIAN_STONE), conditionsFromItem(PlanetBlocks.POLISHED_MARTIAN_STONE)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.CHISELED_MARTIAN_STONE, 1)
                        .pattern("#")
                        .pattern("#")
                        .input('#', PlanetBlocks.MARTIAN_BRICK_SLAB)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_BRICK_SLAB), conditionsFromItem(PlanetBlocks.MARTIAN_BRICK_SLAB)));
                // mossy martian cobblestone
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MOSSY_MARTIAN_COBBLESTONE, 1)
                        .pattern("#M")
                        .input('#', PlanetBlocks.MARTIAN_COBBLESTONE)
                        .input('M', Ingredient.ofItems(Items.MOSS_BLOCK, Items.VINE))
                        .criterion(hasItem(PlanetBlocks.MARTIAN_COBBLESTONE), conditionsFromItem(PlanetBlocks.MARTIAN_COBBLESTONE))
                        .criterion(hasItem(Items.MOSS_BLOCK), conditionsFromItem(Items.MOSS_BLOCK))
                        .criterion(hasItem(Items.VINE), conditionsFromItem(Items.VINE)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MOSSY_MARTIAN_COBBLESTONE_WALL, 6)
                        .pattern("###")
                        .pattern("###")
                        .input('#', PlanetBlocks.MOSSY_MARTIAN_COBBLESTONE)
                        .criterion(hasItem(PlanetBlocks.MOSSY_MARTIAN_COBBLESTONE), conditionsFromItem(PlanetBlocks.MOSSY_MARTIAN_COBBLESTONE)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MOSSY_MARTIAN_COBBLESTONE_SLAB, 6)
                        .pattern("###")
                        .input('#', PlanetBlocks.MOSSY_MARTIAN_COBBLESTONE)
                        .criterion(hasItem(PlanetBlocks.MOSSY_MARTIAN_COBBLESTONE), conditionsFromItem(PlanetBlocks.MOSSY_MARTIAN_COBBLESTONE)));
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MOSSY_MARTIAN_COBBLESTONE_STAIRS, 4)
                        .pattern("#  ")
                        .pattern("## ")
                        .pattern("###")
                        .input('#', PlanetBlocks.MOSSY_MARTIAN_COBBLESTONE)
                        .criterion(hasItem(PlanetBlocks.MOSSY_MARTIAN_COBBLESTONE), conditionsFromItem(PlanetBlocks.MOSSY_MARTIAN_COBBLESTONE)));


                // Spacesuits
                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, PlanetItems.SPACESUIT_BOOTS, 1)
                        .pattern("   ")
                        .pattern("F F")
                        .pattern("FBF")
                        .input('F', PlanetItems.FABRIC)
                        .input('B', Items.IRON_BOOTS)
                        .criterion(hasItem(PlanetItems.FABRIC), conditionsFromItem(PlanetItems.FABRIC))
                        .criterion(hasItem(Items.IRON_BOOTS), conditionsFromItem(Items.IRON_BOOTS)));

                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, PlanetItems.SPACESUIT_LEGGINGS, 1)
                        .pattern("FLF")
                        .pattern("F F")
                        .pattern("F F")
                        .input('F', PlanetItems.FABRIC)
                        .input('L', Items.IRON_LEGGINGS)
                        .criterion(hasItem(PlanetItems.FABRIC), conditionsFromItem(PlanetItems.FABRIC))
                        .criterion(hasItem(Items.IRON_LEGGINGS), conditionsFromItem(Items.IRON_LEGGINGS)));

                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, PlanetItems.SPACESUIT_CHESTPLATE, 1)
                        .pattern("F F")
                        .pattern("FCF")
                        .pattern("FBF")
                        .input('F', PlanetItems.FABRIC)
                        .input('B', Items.BUCKET)
                        .input('C', Items.IRON_CHESTPLATE)
                        .criterion(hasItem(PlanetItems.FABRIC), conditionsFromItem(PlanetItems.FABRIC))
                        .criterion(hasItem(Items.BUCKET), conditionsFromItem(Items.BUCKET))
                        .criterion(hasItem(Items.IRON_CHESTPLATE), conditionsFromItem(Items.IRON_CHESTPLATE)));

                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, PlanetItems.SPACESUIT_HELMET, 1)
                        .pattern("FHF")
                        .pattern("FGF")
                        .pattern("FFF")
                        .input('F', PlanetItems.FABRIC)
                        .input('H', Items.IRON_HELMET)
                        .input('G', Items.YELLOW_STAINED_GLASS_PANE)
                        .criterion(hasItem(PlanetItems.FABRIC), conditionsFromItem(PlanetItems.FABRIC))
                        .criterion(hasItem(Items.IRON_HELMET), conditionsFromItem(Items.IRON_HELMET))
                        .criterion(hasItem(Items.YELLOW_STAINED_GLASS_PANE), conditionsFromItem(Items.YELLOW_STAINED_GLASS_PANE)));

                // Martian Tools

                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, PlanetItems.MARTIAN_STONE_PICKAXE, 1)
                        .pattern("MMM")
                        .pattern(" S ")
                        .pattern(" S ")
                        .input('M', PlanetBlocks.MARTIAN_COBBLESTONE)
                        .input('S', Items.STICK)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_COBBLESTONE), conditionsFromItem(PlanetBlocks.MARTIAN_COBBLESTONE))
                        .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK)));

                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, PlanetItems.MARTIAN_STONE_SWORD, 1)
                        .pattern(" M ")
                        .pattern(" M ")
                        .pattern(" S ")
                        .input('M', PlanetBlocks.MARTIAN_COBBLESTONE)
                        .input('S', Items.STICK)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_COBBLESTONE), conditionsFromItem(PlanetBlocks.MARTIAN_COBBLESTONE))
                        .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK)));

                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, PlanetItems.MARTIAN_STONE_SHOVEL, 1)
                        .pattern(" M ")
                        .pattern(" S ")
                        .pattern(" S ")
                        .input('M', PlanetBlocks.MARTIAN_COBBLESTONE)
                        .input('S', Items.STICK)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_COBBLESTONE), conditionsFromItem(PlanetBlocks.MARTIAN_COBBLESTONE))
                        .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK)));

                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, PlanetItems.MARTIAN_STONE_HOE, 1)
                        .pattern(" MM")
                        .pattern(" S ")
                        .pattern(" S ")
                        .input('M', PlanetBlocks.MARTIAN_COBBLESTONE)
                        .input('S', Items.STICK)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_COBBLESTONE), conditionsFromItem(PlanetBlocks.MARTIAN_COBBLESTONE))
                        .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK)));

                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, PlanetItems.MARTIAN_STONE_AXE, 1)
                        .pattern(" MM")
                        .pattern(" SM")
                        .pattern(" S ")
                        .input('M', PlanetBlocks.MARTIAN_COBBLESTONE)
                        .input('S', Items.STICK)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_COBBLESTONE), conditionsFromItem(PlanetBlocks.MARTIAN_COBBLESTONE))
                        .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK)));

                // Anorthosite Tools

                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, PlanetItems.ANORTHOSITE_PICKAXE, 1)
                        .pattern("MMM")
                        .pattern(" S ")
                        .pattern(" S ")
                        .input('M', PlanetBlocks.ANORTHOSITE)
                        .input('S', Items.STICK)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE), conditionsFromItem(PlanetBlocks.ANORTHOSITE))
                        .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK)));

                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, PlanetItems.ANORTHOSITE_SWORD, 1)
                        .pattern(" M ")
                        .pattern(" M ")
                        .pattern(" S ")
                        .input('M', PlanetBlocks.ANORTHOSITE)
                        .input('S', Items.STICK)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE), conditionsFromItem(PlanetBlocks.ANORTHOSITE))
                        .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK)));

                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, PlanetItems.ANORTHOSITE_SHOVEL, 1)
                        .pattern(" M ")
                        .pattern(" S ")
                        .pattern(" S ")
                        .input('M', PlanetBlocks.ANORTHOSITE)
                        .input('S', Items.STICK)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE), conditionsFromItem(PlanetBlocks.ANORTHOSITE))
                        .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK)));

                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, PlanetItems.ANORTHOSITE_HOE, 1)
                        .pattern(" MM")
                        .pattern(" S ")
                        .pattern(" S ")
                        .input('M', PlanetBlocks.ANORTHOSITE)
                        .input('S', Items.STICK)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE), conditionsFromItem(PlanetBlocks.ANORTHOSITE))
                        .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK)));

                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, PlanetItems.ANORTHOSITE_AXE, 1)
                        .pattern(" MM")
                        .pattern(" SM")
                        .pattern(" S ")
                        .input('M', PlanetBlocks.ANORTHOSITE)
                        .input('S', Items.STICK)
                        .criterion(hasItem(PlanetBlocks.ANORTHOSITE), conditionsFromItem(PlanetBlocks.ANORTHOSITE))
                        .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK)));

                // Martian Stone

                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MARTIAN_STONE_WALL, 1)
                        .pattern("   ")
                        .pattern("SSS")
                        .pattern("SSS")
                        .input('S', PlanetBlocks.MARTIAN_STONE)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_STONE), conditionsFromItem(PlanetBlocks.MARTIAN_STONE)));

                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MARTIAN_STONE_STAIRS, 1)
                        .pattern("S  ")
                        .pattern("SS ")
                        .pattern("SSS")
                        .input('S', PlanetBlocks.MARTIAN_STONE)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_STONE), conditionsFromItem(PlanetBlocks.MARTIAN_STONE)));

                provider.addShapedRecipe(ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PlanetBlocks.MARTIAN_STONE_SLAB, 1)
                        .pattern("   ")
                        .pattern("   ")
                        .pattern("SSS")
                        .input('S', PlanetBlocks.MARTIAN_STONE)
                        .criterion(hasItem(PlanetBlocks.MARTIAN_STONE), conditionsFromItem(PlanetBlocks.MARTIAN_STONE)));



            }


            @Override
            public void blockTags(AITBlockTagProvider provider) {
                // Martian Blocks
                provider.getOrCreateTagBuilder(BlockTags.WALLS)
                        .add(PlanetBlocks.MARTIAN_BRICK_WALL).add(PlanetBlocks.MARTIAN_COBBLESTONE_WALL).add(PlanetBlocks.MARTIAN_SANDSTONE_WALL).add(PlanetBlocks.MARTIAN_STONE_WALL).add(PlanetBlocks.MOSSY_MARTIAN_COBBLESTONE_WALL).add(PlanetBlocks.MARTIAN_BRICK_WALL).add(PlanetBlocks.MARTIAN_SANDSTONE_BRICK_WALL);

                // Anorthosite Blocks
                provider.getOrCreateTagBuilder(BlockTags.WALLS)
                        .add(PlanetBlocks.ANORTHOSITE_BRICK_WALL).add(PlanetBlocks.ANORTHOSITE_WALL).add(PlanetBlocks.MOON_SANDSTONE_BRICK_WALL).add(PlanetBlocks.MOON_SANDSTONE_WALL);

            }

            @Override
            public void itemTags(AITItemTagProvider provider) {
                provider.getOrCreateTagBuilder(ItemTags.TRIMMABLE_ARMOR).add(PlanetItems.SPACESUIT_BOOTS).add(PlanetItems.SPACESUIT_LEGGINGS).add(PlanetItems.SPACESUIT_CHESTPLATE).add(PlanetItems.SPACESUIT_HELMET);
            }


            @Override
            public void generateItemModels(AmbleModelProvider provider, ItemModelGenerator generator) {
                generator.registerArmor((ArmorItem) PlanetItems.SPACESUIT_BOOTS);
                generator.registerArmor((ArmorItem) PlanetItems.SPACESUIT_CHESTPLATE);
                generator.registerArmor((ArmorItem) PlanetItems.SPACESUIT_LEGGINGS);
                generator.registerArmor((ArmorItem) PlanetItems.SPACESUIT_HELMET);

                generator.register(PlanetItems.MARTIAN_STONE_SWORD, Models.HANDHELD);
                generator.register(PlanetItems.MARTIAN_STONE_SHOVEL, Models.HANDHELD);
                generator.register(PlanetItems.MARTIAN_STONE_PICKAXE, Models.HANDHELD);
                generator.register(PlanetItems.MARTIAN_STONE_HOE, Models.HANDHELD);
                generator.register(PlanetItems.MARTIAN_STONE_AXE, Models.HANDHELD);

                generator.register(PlanetItems.ANORTHOSITE_SWORD, Models.HANDHELD);
                generator.register(PlanetItems.ANORTHOSITE_SHOVEL, Models.HANDHELD);
                generator.register(PlanetItems.ANORTHOSITE_PICKAXE, Models.HANDHELD);
                generator.register(PlanetItems.ANORTHOSITE_HOE, Models.HANDHELD);
                generator.register(PlanetItems.ANORTHOSITE_AXE, Models.HANDHELD);
            }

            @Override
            public void models(AmbleModelProvider provider, BlockStateModelGenerator generator) {
                //Martian (Slabs, Walls, etc.)
                BlockStateModelGenerator.BlockTexturePool martian_stone_pool = generator.registerCubeAllModelTexturePool(PlanetBlocks.MARTIAN_STONE);
                martian_stone_pool.stairs(PlanetBlocks.MARTIAN_STONE_STAIRS);
                martian_stone_pool.wall(PlanetBlocks.MARTIAN_STONE_WALL);
                martian_stone_pool.slab(PlanetBlocks.MARTIAN_STONE_SLAB);
                martian_stone_pool.button(PlanetBlocks.MARTIAN_STONE_BUTTON);
                martian_stone_pool.pressurePlate(PlanetBlocks.MARTIAN_STONE_PRESSURE_PLATE);

                BlockStateModelGenerator.BlockTexturePool martian_bricks_pool = generator.registerCubeAllModelTexturePool(PlanetBlocks.MARTIAN_BRICKS);
                martian_bricks_pool.stairs(PlanetBlocks.MARTIAN_BRICK_STAIRS);
                martian_bricks_pool.wall(PlanetBlocks.MARTIAN_BRICK_WALL);
                martian_bricks_pool.slab(PlanetBlocks.MARTIAN_BRICK_SLAB);

                BlockStateModelGenerator.BlockTexturePool martian_cobblestone_pool = generator.registerCubeAllModelTexturePool(PlanetBlocks.MARTIAN_COBBLESTONE);
                martian_cobblestone_pool.stairs(PlanetBlocks.MARTIAN_COBBLESTONE_STAIRS);
                martian_cobblestone_pool.wall(PlanetBlocks.MARTIAN_COBBLESTONE_WALL);
                martian_cobblestone_pool.slab(PlanetBlocks.MARTIAN_COBBLESTONE_SLAB);


                BlockStateModelGenerator.BlockTexturePool mossy_martian_cobblestone_pool = generator.registerCubeAllModelTexturePool(PlanetBlocks.MOSSY_MARTIAN_COBBLESTONE);
                mossy_martian_cobblestone_pool.stairs(PlanetBlocks.MOSSY_MARTIAN_COBBLESTONE_STAIRS);
                mossy_martian_cobblestone_pool.wall(PlanetBlocks.MOSSY_MARTIAN_COBBLESTONE_WALL);
                mossy_martian_cobblestone_pool.slab(PlanetBlocks.MOSSY_MARTIAN_COBBLESTONE_SLAB);

                BlockStateModelGenerator.BlockTexturePool martian_sandstone_pool = generator.registerCubeAllModelTexturePool(PlanetBlocks.MARTIAN_SANDSTONE);
                martian_sandstone_pool.stairs(PlanetBlocks.MARTIAN_SANDSTONE_STAIRS);
                martian_sandstone_pool.wall(PlanetBlocks.MARTIAN_SANDSTONE_WALL);
                martian_sandstone_pool.slab(PlanetBlocks.MARTIAN_SANDSTONE_SLAB);

                BlockStateModelGenerator.BlockTexturePool martian_sandstone_bricks_pool = generator.registerCubeAllModelTexturePool(PlanetBlocks.MARTIAN_SANDSTONE_BRICKS);
                martian_sandstone_bricks_pool.stairs(PlanetBlocks.MARTIAN_SANDSTONE_BRICK_STAIRS);
                martian_sandstone_bricks_pool.wall(PlanetBlocks.MARTIAN_SANDSTONE_BRICK_WALL);
                martian_sandstone_bricks_pool.slab(PlanetBlocks.MARTIAN_SANDSTONE_BRICK_SLAB);

                BlockStateModelGenerator.BlockTexturePool smooth_martian_stone_pool = generator.registerCubeAllModelTexturePool(PlanetBlocks.SMOOTH_MARTIAN_STONE);
                smooth_martian_stone_pool.slab(PlanetBlocks.SMOOTH_MARTIAN_STONE_SLAB);

                BlockStateModelGenerator.BlockTexturePool polished_martian_stone_pool = generator.registerCubeAllModelTexturePool(PlanetBlocks.POLISHED_MARTIAN_STONE);
                polished_martian_stone_pool.stairs(PlanetBlocks.POLISHED_MARTIAN_STONE_STAIRS);
                polished_martian_stone_pool.slab(PlanetBlocks.POLISHED_MARTIAN_STONE_SLAB);


                //Anorthosite (Slabs, Walls, etc.)

                BlockStateModelGenerator.BlockTexturePool anorthosite_pool = generator.registerCubeAllModelTexturePool(PlanetBlocks.ANORTHOSITE);
                anorthosite_pool.stairs(PlanetBlocks.ANORTHOSITE_STAIRS);
                anorthosite_pool.wall(PlanetBlocks.ANORTHOSITE_WALL);
                anorthosite_pool.slab(PlanetBlocks.ANORTHOSITE_SLAB);

                BlockStateModelGenerator.BlockTexturePool anorthosite_bricks_pool = generator.registerCubeAllModelTexturePool(PlanetBlocks.ANORTHOSITE_BRICKS);
                anorthosite_bricks_pool.stairs(PlanetBlocks.ANORTHOSITE_BRICK_STAIRS);
                anorthosite_bricks_pool.wall(PlanetBlocks.ANORTHOSITE_BRICK_WALL);
                anorthosite_bricks_pool.slab(PlanetBlocks.ANORTHOSITE_BRICK_SLAB);

                BlockStateModelGenerator.BlockTexturePool smooth_anorthosite_stone_pool = generator.registerCubeAllModelTexturePool(PlanetBlocks.SMOOTH_ANORTHOSITE);
                smooth_anorthosite_stone_pool.slab(PlanetBlocks.SMOOTH_ANORTHOSITE_SLAB);

                BlockStateModelGenerator.BlockTexturePool polished_anorthosite_stone_pool = generator.registerCubeAllModelTexturePool(PlanetBlocks.POLISHED_ANORTHOSITE);
                polished_anorthosite_stone_pool.stairs(PlanetBlocks.POLISHED_ANORTHOSITE_STAIRS);
                polished_anorthosite_stone_pool.slab(PlanetBlocks.POLISHED_ANORTHOSITE_SLAB);

                BlockStateModelGenerator.BlockTexturePool moon_sandstone_pool = generator.registerCubeAllModelTexturePool(PlanetBlocks.MOON_SANDSTONE);
                moon_sandstone_pool.stairs(PlanetBlocks.MOON_SANDSTONE_STAIRS);
                moon_sandstone_pool.wall(PlanetBlocks.MOON_SANDSTONE_WALL);
                moon_sandstone_pool.slab(PlanetBlocks.MOON_SANDSTONE_SLAB);

                BlockStateModelGenerator.BlockTexturePool moon_sandstone_bricks_pool = generator.registerCubeAllModelTexturePool(PlanetBlocks.MOON_SANDSTONE_BRICKS);
                moon_sandstone_bricks_pool.stairs(PlanetBlocks.MOON_SANDSTONE_BRICK_STAIRS);
                moon_sandstone_bricks_pool.wall(PlanetBlocks.MOON_SANDSTONE_BRICK_WALL);
                moon_sandstone_bricks_pool.slab(PlanetBlocks.MOON_SANDSTONE_BRICK_SLAB);
            }

            @Override
            public void advancements(Consumer<Advancement> consumer) {
                Advancement root = Advancement.Builder.create()
                        .display(
                                PlanetItems.SPACESUIT_HELMET,
                                Text.translatable("achievements.ait.title.planet_root"),
                                Text.translatable("achievements.ait.description.planet_root"),
                                AITMod.id("textures/block/martian_stone.png"),
                                AdvancementFrame.TASK,
                                false,
                                false,
                                false
                        )
                        .criterion("enter_tardis", TardisCriterions.ENTER_TARDIS.conditions())
                        .build(consumer, AITMod.MOD_ID + "/planet_root");
                Advancement landOnMars = Advancement.Builder.create()
                        .parent(root)
                        .display(
                                PlanetBlocks.MARTIAN_STONE,
                                Text.translatable("achievements.ait.title.enter_mars"),
                                Text.translatable("achievements.ait.description.enter_mars"),
                                null,
                                AdvancementFrame.TASK,
                                true,
                                true,
                                true
                        )
                        .criterion(
                                "enter_mars",
                                ChangedDimensionCriterion.Conditions.to(
                                        RegistryKey.of(
                                                RegistryKeys.WORLD,
                                                AITMod.id("mars")
                                        )
                                )
                        )
                        .build(consumer, AITMod.MOD_ID + "/enter_mars");
                Advancement landOnMoon = Advancement.Builder.create()
                        .parent(root)
                        .display(
                                PlanetBlocks.ANORTHOSITE,
                                Text.translatable("achievements.ait.title.enter_moon"),
                                Text.translatable("achievements.ait.description.enter_moon"),
                                null,
                                AdvancementFrame.TASK,
                                true,
                                true,
                                true
                        )
                        .criterion(
                                "enter_moon",
                                ChangedDimensionCriterion.Conditions.to(
                                        RegistryKey.of(
                                                RegistryKeys.WORLD,
                                                AITMod.id("moon")
                                        )
                                )
                        )
                        .build(consumer, AITMod.MOD_ID + "/enter_moon");

                // todo - idk how to do this
                // Advancement findStructure = Advancement.Builder.create().parent(root).display(Blocks.REDSTONE_BLOCK, Text.translatable("advancements.ait.find_planet_structure.title"), Text.translatable("advancements.ait.find_planet_structure.description"), null, AdvancementFrame.CHALLENGE, true, true, true).criterion("planet_structure", TickCriterion.Conditions.createLocation(LocationPredicate.feature(RegistryKey.of(RegistryKeys.STRUCTURE, AITMod.id("cult_structures_overworld"))))).build(consumer, AITMod.MOD_ID + "/find_planet_structure");
            }
        });
    }

    public static PlanetModule instance() {
        return INSTANCE;
    }
}
