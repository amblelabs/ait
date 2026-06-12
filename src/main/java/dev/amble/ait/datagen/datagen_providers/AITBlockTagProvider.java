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
                .add(Blocks.TNT)
                .forceAddTag(BlockTags.CANDLES).forceAddTag(BlockTags.CANDLE_CAKES)
                .add(Blocks.REDSTONE_LAMP).add(AITBlocks.EXTERIOR_BLOCK).add(AITBlocks.CONSOLE_GENERATOR)
                .forceAddTag(TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "glass_panes")))
                .forceAddTag(TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "glass_blocks")))
                .add(AITBlocks.CONSOLE)
                .add(Blocks.BRICKS)
                .add(Blocks.REDSTONE_WIRE, Blocks.COMPARATOR, Blocks.REPEATER, Blocks.LEVER)
                .forceAddTag(BlockTags.BUTTONS)
                .add(Blocks.DAYLIGHT_DETECTOR)
                .add(Blocks.OBSIDIAN);

        getOrCreateTagBuilder(AITTags.Blocks.SONIC_CAN_LOCATE).add(AITBlocks.ZEITON_BLOCK).add(AITBlocks.ZEITON_COBBLE)
                .add(AITBlocks.BUDDING_ZEITON).add(AITBlocks.COMPACT_ZEITON).add(AITBlocks.SMALL_ZEITON_BUD)
                .add(AITBlocks.MEDIUM_ZEITON_BUD).add(AITBlocks.LARGE_ZEITON_BUD).add(AITBlocks.ZEITON_CLUSTER)
                .add(Blocks.BELL);

        getOrCreateTagBuilder(AITTags.Blocks.WOODEN_BLOCKS)
                .forceAddTag(BlockTags.LOGS)
                .forceAddTag(BlockTags.PLANKS)
                .forceAddTag(BlockTags.WOODEN_BUTTONS)
                .forceAddTag(BlockTags.WOODEN_PRESSURE_PLATES)
                .forceAddTag(BlockTags.WOODEN_DOORS)
                .forceAddTag(BlockTags.WOODEN_TRAPDOORS)
                .forceAddTag(BlockTags.WOODEN_FENCES)
                .forceAddTag(BlockTags.WOODEN_SLABS)
                .forceAddTag(BlockTags.WOODEN_STAIRS);



        getOrCreateTagBuilder(BlockTags.WALLS)
                .add(AITBlocks.TARDIS_CORAL_WALL);
        getOrCreateTagBuilder(BlockTags.FENCES)
                .add(AITBlocks.TARDIS_CORAL_FENCE);

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
