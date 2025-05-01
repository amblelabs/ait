package dev.amble.ait.datagen.datagen_providers;

import java.util.concurrent.CompletableFuture;

import dev.amble.lib.datagen.tag.AmbleBlockTagProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITTags;
import dev.amble.ait.module.ModuleRegistry;
import dev.amble.ait.module.planet.core.PlanetBlocks;


public class AITBlockTagProvider extends AmbleBlockTagProvider {
    public AITBlockTagProvider(FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
//TODO: Make the glass tag work on this and the leafs, for now theres just glass and glass pane and the birch leafs as a temporarly thing.
        getOrCreateTagBuilder(AITTags.Blocks.SONIC_INTERACTABLE).add(Blocks.IRON_DOOR).add(Blocks.IRON_TRAPDOOR)
                .add(Blocks.TNT).add(Blocks.CAMPFIRE).add(Blocks.CANDLE).add(Blocks.CANDLE_CAKE)
                .add(Blocks.WHITE_CANDLE).add(Blocks.ORANGE_CANDLE).add(Blocks.MAGENTA_CANDLE)
                .add(Blocks.LIGHT_BLUE_CANDLE).add(Blocks.YELLOW_CANDLE).add(Blocks.LIME_CANDLE).add(Blocks.PINK_CANDLE)
                .add(Blocks.GRAY_CANDLE).add(Blocks.LIGHT_GRAY_CANDLE).add(Blocks.CYAN_CANDLE).add(Blocks.PURPLE_CANDLE)
                .add(Blocks.BLUE_CANDLE).add(Blocks.BROWN_CANDLE).add(Blocks.GREEN_CANDLE).add(Blocks.RED_CANDLE)
                .add(Blocks.BLACK_CANDLE).add(Blocks.CANDLE_CAKE).add(Blocks.WHITE_CANDLE_CAKE)
                .add(Blocks.ORANGE_CANDLE_CAKE).add(Blocks.MAGENTA_CANDLE_CAKE).add(Blocks.LIGHT_BLUE_CANDLE_CAKE)
                .add(Blocks.YELLOW_CANDLE_CAKE).add(Blocks.LIME_CANDLE_CAKE).add(Blocks.PINK_CANDLE_CAKE)
                .add(Blocks.GRAY_CANDLE_CAKE).add(Blocks.LIGHT_GRAY_CANDLE_CAKE).add(Blocks.CYAN_CANDLE_CAKE)
                .add(Blocks.PURPLE_CANDLE_CAKE).add(Blocks.BLUE_CANDLE_CAKE).add(Blocks.BROWN_CANDLE_CAKE)
                .add(Blocks.GREEN_CANDLE_CAKE).add(Blocks.RED_CANDLE_CAKE).add(Blocks.BLACK_CANDLE_CAKE)
                .add(Blocks.REDSTONE_LAMP).add(AITBlocks.EXTERIOR_BLOCK).add(AITBlocks.CONSOLE_GENERATOR)
                .add(Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE).add(Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.NETHER_GOLD_ORE, Blocks.LAPIS_ORE,Blocks.DEEPSLATE_LAPIS_ORE, Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DEEPSLATE_COAL_ORE, Blocks.COAL_ORE, Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE)
                .forceAddTag(TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "ores")))
                .forceAddTag(TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "glass_panes")))
                .forceAddTag(TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "glass_blocks")))
                .forceAddTag(BlockTags.ICE).forceAddTag(BlockTags.SNOW).forceAddTag(BlockTags.SAND).forceAddTag(BlockTags.LEAVES).forceAddTag(BlockTags.DOORS)
                .add(Blocks.NETHER_BRICKS, Blocks.RED_NETHER_BRICKS, Blocks.NETHER_BRICK_WALL, Blocks.RED_NETHER_BRICK_WALL)
                .add(PlanetBlocks.ANORTHOSITE_IRON_ORE, PlanetBlocks.MARTIAN_IRON_ORE, PlanetBlocks.ANORTHOSITE_GOLD_ORE, PlanetBlocks.MARTIAN_GOLD_ORE, PlanetBlocks.ANORTHOSITE_COAL_ORE, PlanetBlocks.MARTIAN_COAL_ORE, PlanetBlocks.ANORTHOSITE_LAPIS_ORE, PlanetBlocks.MARTIAN_LAPIS_ORE, PlanetBlocks.ANORTHOSITE_DIAMOND_ORE, PlanetBlocks.MARTIAN_DIAMOND_ORE, PlanetBlocks.ANORTHOSITE_COPPER_ORE, PlanetBlocks.MARTIAN_COPPER_ORE)
                .add(AITBlocks.MACHINE_CASING, AITBlocks.CONSOLE).forceAddTag(BlockTags.STONE_BRICKS)
                .add(Blocks.BLACK_CONCRETE,Blocks.CYAN_CONCRETE,Blocks.BLUE_CONCRETE,Blocks.BROWN_CONCRETE,Blocks.GRAY_CONCRETE,Blocks.GREEN_CONCRETE,Blocks.MAGENTA_CONCRETE,Blocks.ORANGE_CONCRETE,Blocks.PINK_CONCRETE,Blocks.RED_CONCRETE,Blocks.WHITE_CONCRETE,Blocks.PURPLE_CONCRETE,Blocks.LIGHT_GRAY_CONCRETE,Blocks.LIGHT_BLUE_CONCRETE,Blocks.LIME_CONCRETE)
                .add(Blocks.BRICKS, Blocks.BRICK_WALL, Blocks.REDSTONE_TORCH, Blocks.DEEPSLATE_BRICKS)
                .add(Blocks.BARREL)
                .add(Blocks.REDSTONE_WIRE, Blocks.COMPARATOR, Blocks.REPEATER)
                .add(Blocks.BELL, Blocks.JUKEBOX, Blocks.TRAPPED_CHEST)
                .add(Blocks.DAYLIGHT_DETECTOR)
                .add(Blocks.OBSIDIAN);

        getOrCreateTagBuilder(AITTags.Blocks.SONIC_CAN_LOCATE).add(AITBlocks.ZEITON_BLOCK).add(AITBlocks.ZEITON_COBBLE)
                .add(AITBlocks.BUDDING_ZEITON).add(AITBlocks.COMPACT_ZEITON).add(AITBlocks.SMALL_ZEITON_BUD)
                .add(AITBlocks.MEDIUM_ZEITON_BUD).add(AITBlocks.LARGE_ZEITON_BUD).add(AITBlocks.ZEITON_CLUSTER)
                .add(Blocks.BELL);

        getOrCreateTagBuilder(BlockTags.COAL_ORES).add(PlanetBlocks.ANORTHOSITE_COAL_ORE, PlanetBlocks.MARTIAN_COAL_ORE);
        getOrCreateTagBuilder(BlockTags.COPPER_ORES).add(PlanetBlocks.ANORTHOSITE_COPPER_ORE, PlanetBlocks.MARTIAN_COPPER_ORE);
        getOrCreateTagBuilder(BlockTags.IRON_ORES).add(PlanetBlocks.ANORTHOSITE_COPPER_ORE, PlanetBlocks.MARTIAN_COPPER_ORE);
        getOrCreateTagBuilder(BlockTags.GOLD_ORES).add(PlanetBlocks.ANORTHOSITE_COPPER_ORE, PlanetBlocks.MARTIAN_COPPER_ORE);
        getOrCreateTagBuilder(BlockTags.DIAMOND_ORES).add(PlanetBlocks.ANORTHOSITE_DIAMOND_ORE, PlanetBlocks.MARTIAN_DIAMOND_ORE);
        getOrCreateTagBuilder(BlockTags.EMERALD_ORES).add(PlanetBlocks.ANORTHOSITE_EMERALD_ORE, PlanetBlocks.MARTIAN_EMERALD_ORE);
        getOrCreateTagBuilder(BlockTags.LAPIS_ORES).add(PlanetBlocks.ANORTHOSITE_LAPIS_ORE, PlanetBlocks.MARTIAN_LAPIS_ORE);
        getOrCreateTagBuilder(BlockTags.REDSTONE_ORES).add(PlanetBlocks.ANORTHOSITE_REDSTONE_ORE, PlanetBlocks.MARTIAN_REDSTONE_ORE);

        getOrCreateTagBuilder(BlockTags.DIRT).add(PlanetBlocks.MARTIAN_SAND).add(PlanetBlocks.REGOLITH);

        getOrCreateTagBuilder(BlockTags.DRAGON_IMMUNE).add(AITBlocks.EXTERIOR_BLOCK, AITBlocks.CONSOLE);
        getOrCreateTagBuilder(BlockTags.WITHER_IMMUNE).add(AITBlocks.EXTERIOR_BLOCK, AITBlocks.CONSOLE);

        getOrCreateTagBuilder(AITTags.Blocks.FLUID_LINK_CAN_CONNECT).add(Blocks.JUKEBOX);

        ModuleRegistry.instance().iterator().forEachRemaining(module -> {
            module.getDataGenerator().ifPresent(generator -> {
                generator.blockTags(this);
            });
            module.getBlockRegistry().ifPresent(this::withBlocks);
        });

        this.withBlocks(AITBlocks.class);
        super.configure(arg);
    }

    @Override
    public FabricTagProvider<Block>.FabricTagBuilder getOrCreateTagBuilder(TagKey<Block> tag) {
        return super.getOrCreateTagBuilder(tag);
    }
}
