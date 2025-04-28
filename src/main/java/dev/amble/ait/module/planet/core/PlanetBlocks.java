package dev.amble.ait.module.planet.core;

import dev.amble.lib.container.impl.BlockContainer;
import dev.amble.lib.datagen.util.AutomaticModel;
import dev.amble.lib.datagen.util.NoBlockDrop;
import dev.amble.lib.datagen.util.PickaxeMineable;
import dev.amble.lib.item.AItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.Item;

import dev.amble.ait.core.blocks.FlagBlock;
import dev.amble.ait.module.planet.PlanetModule;
import dev.amble.ait.module.planet.core.block.OxygenatorBlock;

public class PlanetBlocks extends BlockContainer {

    public static final Block FLAG = new FlagBlock(
            FabricBlockSettings.create().nonOpaque().strength(0.01F, 0.01F).pistonBehavior(PistonBehavior.DESTROY));

    // Tech

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block OXYGENATOR_BLOCK = new OxygenatorBlock(
            FabricBlockSettings.copy(Blocks.IRON_BLOCK));

    // Mars

        // Stone

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_STONE = new Block(
            AbstractBlock.Settings.copy(Blocks.STONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_STONE_WALL = new WallBlock(
            AbstractBlock.Settings.copy(Blocks.STONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_STONE_SLAB = new SlabBlock(
            AbstractBlock.Settings.copy(Blocks.STONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_STONE_STAIRS = new StairsBlock(
            MARTIAN_STONE.getDefaultState(), AbstractBlock.Settings.copy(Blocks.STONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_STONE_BUTTON = new ButtonBlock(
            AbstractBlock.Settings.copy(Blocks.STONE_BUTTON), BlockSetType.STONE, 10, false);

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_STONE_PRESSURE_PLATE  = new PressurePlateBlock(
            PressurePlateBlock.ActivationRule.EVERYTHING, AbstractBlock.Settings.copy(Blocks.STONE_PRESSURE_PLATE),BlockSetType.STONE);

        // Ores

    @AutomaticModel
    @PickaxeMineable(tool = PickaxeMineable.Tool.NONE)
    public static final Block MARTIAN_COAL_ORE = new Block(
            AbstractBlock.Settings.copy(Blocks.COAL_ORE));

    @AutomaticModel
    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_COPPER_ORE = new Block(
            AbstractBlock.Settings.copy(Blocks.COPPER_ORE));

    @AutomaticModel
    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_IRON_ORE = new Block(
            AbstractBlock.Settings.copy(Blocks.IRON_ORE));

    @AutomaticModel
    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_GOLD_ORE = new Block(
            AbstractBlock.Settings.copy(Blocks.GOLD_ORE));

    @AutomaticModel
    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_LAPIS_ORE = new Block(
            AbstractBlock.Settings.copy(Blocks.LAPIS_ORE));

    @AutomaticModel
    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_REDSTONE_ORE = new RedstoneOreBlock(
            AbstractBlock.Settings.copy(Blocks.REDSTONE_ORE));

    @AutomaticModel
    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_DIAMOND_ORE = new Block(
            AbstractBlock.Settings.copy(Blocks.DIAMOND_ORE));

    @AutomaticModel
    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_EMERALD_ORE = new Block(
            AbstractBlock.Settings.copy(Blocks.EMERALD_ORE));

        // Cobblestone

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_COBBLESTONE = new Block(
            AbstractBlock.Settings.copy(Blocks.COBBLESTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_COBBLESTONE_WALL = new WallBlock(
            AbstractBlock.Settings.copy(Blocks.COBBLESTONE_WALL));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    @NoBlockDrop
    public static final Block MARTIAN_COBBLESTONE_SLAB = new SlabBlock(
            AbstractBlock.Settings.copy(Blocks.COBBLESTONE_SLAB));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_COBBLESTONE_STAIRS = new StairsBlock(
            MARTIAN_COBBLESTONE.getDefaultState(), AbstractBlock.Settings.copy(Blocks.COBBLESTONE_STAIRS));

        // Mossy Cobblestone

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MOSSY_MARTIAN_COBBLESTONE = new Block(
            AbstractBlock.Settings.copy(Blocks.MOSSY_COBBLESTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MOSSY_MARTIAN_COBBLESTONE_WALL = new WallBlock(
            AbstractBlock.Settings.copy(Blocks.MOSSY_COBBLESTONE_WALL));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MOSSY_MARTIAN_COBBLESTONE_SLAB = new SlabBlock(
            AbstractBlock.Settings.copy(Blocks.MOSSY_COBBLESTONE_SLAB));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MOSSY_MARTIAN_COBBLESTONE_STAIRS = new StairsBlock(
            MOSSY_MARTIAN_COBBLESTONE.getDefaultState(), AbstractBlock.Settings.copy(Blocks.MOSSY_COBBLESTONE_STAIRS));

        // Polished Stone

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block POLISHED_MARTIAN_STONE = new Block(
            AbstractBlock.Settings.copy(Blocks.POLISHED_ANDESITE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block POLISHED_MARTIAN_STONE_SLAB = new SlabBlock(
            AbstractBlock.Settings.copy(Blocks.POLISHED_ANDESITE_SLAB));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block POLISHED_MARTIAN_STONE_STAIRS = new StairsBlock(
            POLISHED_MARTIAN_STONE.getDefaultState(), AbstractBlock.Settings.copy(Blocks.POLISHED_ANDESITE_STAIRS));


        // Smooth Stone

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block SMOOTH_MARTIAN_STONE = new Block(
            AbstractBlock.Settings.copy(Blocks.SMOOTH_STONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    @NoBlockDrop
    public static final Block SMOOTH_MARTIAN_STONE_SLAB = new SlabBlock(
            AbstractBlock.Settings.copy(Blocks.SMOOTH_STONE_SLAB));

        // Sand

    public static final Block MARTIAN_SAND = new FallingBlock(
            AbstractBlock.Settings.copy(Blocks.SAND));

        // Martian Sandstone

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_SANDSTONE = new Block(
            AbstractBlock.Settings.copy(Blocks.SANDSTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_SANDSTONE_WALL = new WallBlock(
            AbstractBlock.Settings.copy(Blocks.SANDSTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_SANDSTONE_SLAB = new SlabBlock(
            AbstractBlock.Settings.copy(Blocks.SANDSTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_SANDSTONE_BRICK_WALL = new WallBlock(
            AbstractBlock.Settings.copy(Blocks.SANDSTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_SANDSTONE_BRICK_SLAB = new SlabBlock(
            AbstractBlock.Settings.copy(Blocks.SANDSTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_SANDSTONE_BRICK_STAIRS = new StairsBlock(
            POLISHED_MARTIAN_STONE.getDefaultState(), AbstractBlock.Settings.copy(Blocks.SANDSTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_SANDSTONE_STAIRS = new StairsBlock(
            POLISHED_MARTIAN_STONE.getDefaultState(), AbstractBlock.Settings.copy(Blocks.SANDSTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block CRACKED_MARTIAN_SANDSTONE = new Block(
            AbstractBlock.Settings.copy(Blocks.SANDSTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block POLISHED_MARTIAN_SANDSTONE = new Block(
            AbstractBlock.Settings.copy(Blocks.SANDSTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_SANDSTONE_PILLAR = new PillarBlock(
            AbstractBlock.Settings.copy(Blocks.SANDSTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_SANDSTONE_BRICKS = new Block(
            AbstractBlock.Settings.copy(Blocks.SANDSTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block CRACKED_MARTIAN_SANDSTONE_BRICKS = new Block(
            AbstractBlock.Settings.copy(Blocks.SANDSTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block CHISELED_MARTIAN_SANDSTONE = new Block(
            AbstractBlock.Settings.copy(Blocks.SANDSTONE));


        // Bricks

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_BRICKS = new Block(
            AbstractBlock.Settings.copy(Blocks.STONE_BRICKS));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_BRICK_SLAB = new SlabBlock(
            AbstractBlock.Settings.copy(Blocks.STONE_BRICK_SLAB));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_BRICK_STAIRS = new StairsBlock(
            MARTIAN_BRICKS.getDefaultState(), AbstractBlock.Settings.copy(Blocks.STONE_BRICK_STAIRS));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_BRICK_WALL = new WallBlock(
            AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL));

        // Other

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MARTIAN_PILLAR = new PillarBlock(
            AbstractBlock.Settings.copy(Blocks.QUARTZ_PILLAR));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block CHISELED_MARTIAN_STONE = new Block(
            AbstractBlock.Settings.copy(Blocks.CHISELED_STONE_BRICKS));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block CRACKED_MARTIAN_BRICKS = new Block(
            AbstractBlock.Settings.copy(Blocks.CRACKED_STONE_BRICKS));

        // Infested Blocks

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block INFESTED_MARTIAN_STONE = new InfestedBlock(
            Blocks.INFESTED_STONE, AbstractBlock.Settings.copy(Blocks.INFESTED_STONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block INFESTED_MARTIAN_COBBLESTONE = new InfestedBlock(
            Blocks.INFESTED_COBBLESTONE, AbstractBlock.Settings.copy(Blocks.INFESTED_COBBLESTONE));



    // Moon

        // Anorthosite

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block ANORTHOSITE = new Block(
            AbstractBlock.Settings.copy(Blocks.STONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block ANORTHOSITE_WALL = new WallBlock(
            AbstractBlock.Settings.copy(Blocks.STONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block ANORTHOSITE_SLAB = new SlabBlock(
            AbstractBlock.Settings.copy(Blocks.STONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block ANORTHOSITE_STAIRS = new StairsBlock(
            ANORTHOSITE.getDefaultState(), AbstractBlock.Settings.copy(Blocks.STONE));

        // Ores

    @AutomaticModel
    @PickaxeMineable(tool = PickaxeMineable.Tool.NONE)
    public static final Block ANORTHOSITE_COAL_ORE = new Block(
            AbstractBlock.Settings.copy(Blocks.COAL_ORE));

    @AutomaticModel
    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block ANORTHOSITE_COPPER_ORE = new Block(
            AbstractBlock.Settings.copy(Blocks.COPPER_ORE));

    @AutomaticModel
    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block ANORTHOSITE_IRON_ORE = new Block(
            AbstractBlock.Settings.copy(Blocks.IRON_ORE));

    @AutomaticModel
    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block ANORTHOSITE_GOLD_ORE = new Block(
            AbstractBlock.Settings.copy(Blocks.GOLD_ORE));

    @AutomaticModel
    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block ANORTHOSITE_LAPIS_ORE = new Block(
            AbstractBlock.Settings.copy(Blocks.LAPIS_ORE));

    @AutomaticModel
    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block ANORTHOSITE_REDSTONE_ORE = new RedstoneOreBlock(
            AbstractBlock.Settings.copy(Blocks.REDSTONE_ORE));

    @AutomaticModel
    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block ANORTHOSITE_DIAMOND_ORE = new Block(
            AbstractBlock.Settings.copy(Blocks.DIAMOND_ORE));

    @AutomaticModel
    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block ANORTHOSITE_EMERALD_ORE = new Block(
            AbstractBlock.Settings.copy(Blocks.EMERALD_ORE));

        // Polished Stone

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block POLISHED_ANORTHOSITE = new Block(
            AbstractBlock.Settings.copy(Blocks.POLISHED_ANDESITE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block POLISHED_ANORTHOSITE_SLAB = new SlabBlock(
            AbstractBlock.Settings.copy(Blocks.POLISHED_ANDESITE_SLAB));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block POLISHED_ANORTHOSITE_STAIRS = new StairsBlock(
            POLISHED_ANORTHOSITE.getDefaultState(), AbstractBlock.Settings.copy(Blocks.POLISHED_ANDESITE_STAIRS));

        // Smooth Stone

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block SMOOTH_ANORTHOSITE = new Block(
            AbstractBlock.Settings.copy(Blocks.SMOOTH_STONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block SMOOTH_ANORTHOSITE_SLAB = new SlabBlock(
            AbstractBlock.Settings.copy(Blocks.SMOOTH_STONE_SLAB));

        // Sand (Regolith)

    public static final Block REGOLITH = new FallingBlock(
            AbstractBlock.Settings.copy(Blocks.SAND));

        // Sandstone

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MOON_SANDSTONE = new Block(
            AbstractBlock.Settings.copy(Blocks.SANDSTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MOON_SANDSTONE_WALL = new WallBlock(
            AbstractBlock.Settings.copy(Blocks.SANDSTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MOON_SANDSTONE_SLAB = new SlabBlock(
            AbstractBlock.Settings.copy(Blocks.SANDSTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MOON_SANDSTONE_STAIRS = new StairsBlock(
            POLISHED_MARTIAN_STONE.getDefaultState(), AbstractBlock.Settings.copy(Blocks.SANDSTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block CRACKED_MOON_SANDSTONE = new Block(
            AbstractBlock.Settings.copy(Blocks.SANDSTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block POLISHED_MOON_SANDSTONE = new Block(
            AbstractBlock.Settings.copy(Blocks.SANDSTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MOON_SANDSTONE_PILLAR = new PillarBlock(
            AbstractBlock.Settings.copy(Blocks.SANDSTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MOON_SANDSTONE_BRICKS = new Block(
            AbstractBlock.Settings.copy(Blocks.SANDSTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MOON_SANDSTONE_BRICK_WALL = new WallBlock(
            AbstractBlock.Settings.copy(Blocks.SANDSTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MOON_SANDSTONE_BRICK_SLAB = new SlabBlock(
            AbstractBlock.Settings.copy(Blocks.SANDSTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block MOON_SANDSTONE_BRICK_STAIRS = new StairsBlock(
            POLISHED_MARTIAN_STONE.getDefaultState(), AbstractBlock.Settings.copy(Blocks.SANDSTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block CRACKED_MOON_SANDSTONE_BRICKS = new Block(
            AbstractBlock.Settings.copy(Blocks.SANDSTONE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block CHISELED_MOON_SANDSTONE = new Block(
            AbstractBlock.Settings.copy(Blocks.SANDSTONE));

        // Bricks

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block ANORTHOSITE_BRICKS = new Block(
            AbstractBlock.Settings.copy(Blocks.STONE_BRICKS));


    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block ANORTHOSITE_BRICK_SLAB = new SlabBlock(
            AbstractBlock.Settings.copy(Blocks.STONE_BRICK_SLAB));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block ANORTHOSITE_BRICK_STAIRS = new StairsBlock(
            ANORTHOSITE_BRICKS.getDefaultState(), AbstractBlock.Settings.copy(Blocks.STONE_BRICK_STAIRS));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block ANORTHOSITE_BRICK_WALL = new WallBlock(
            AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL));

        // Other

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block ANORTHOSITE_PILLAR = new PillarBlock(
            AbstractBlock.Settings.copy(Blocks.QUARTZ_PILLAR));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block CHISELED_ANORTHOSITE = new Block(
            AbstractBlock.Settings.copy(Blocks.CHISELED_STONE_BRICKS));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block CRACKED_ANORTHOSITE_BRICKS = new Block(
            AbstractBlock.Settings.copy(Blocks.CRACKED_STONE_BRICKS));

    @Override
    public Item.Settings createBlockItemSettings(Block block) {
        return new AItemSettings().group(PlanetModule.instance().getItemGroup());
    }
}
