package dev.amble.ait.core;


import static dev.amble.ait.core.AITItems.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dev.amble.lib.block.ABlockSettings;
import dev.amble.lib.container.impl.BlockContainer;
import dev.amble.lib.container.impl.NoBlockItem;
import dev.amble.lib.datagen.util.AutomaticModel;
import dev.amble.lib.datagen.util.NoBlockDrop;
import dev.amble.lib.datagen.util.NoEnglish;
import dev.amble.lib.datagen.util.PickaxeMineable;
import dev.amble.lib.item.AItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import net.minecraft.block.*;
import net.minecraft.block.enums.Instrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.sound.BlockSoundGroup;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.blocks.*;
import dev.amble.ait.core.blocks.DoorBlock;
import dev.amble.ait.core.blocks.control.RedstoneControlBlock;
import dev.amble.ait.core.engine.block.generic.GenericSubSystemBlock;


public class AITBlocks extends BlockContainer {
    public static Block SNOW_GLOBE;
    @NoBlockItem
    @NoBlockDrop
    @NoEnglish
    public static final Block EXTERIOR_BLOCK = new ExteriorBlock(
            FabricBlockSettings.create().nonOpaque().noBlockBreakParticles().strength(-1.0f, 3600000.0f).dropsNothing()
                    .pistonBehavior(PistonBehavior.IGNORE).luminance(ExteriorBlock.STATE_TO_LUMINANCE));

    @PickaxeMineable
    @NoEnglish
    public static final Block DOOR_BLOCK = new DoorBlock(FabricBlockSettings.create().nonOpaque().noCollision()
            .instrument(Instrument.BASEDRUM).requiresTool().strength(0.5F, 6.0F).pistonBehavior(PistonBehavior.IGNORE));
    @NoBlockDrop
    @NoEnglish
    public static final Block CONSOLE = new ConsoleBlock(
            FabricBlockSettings.create().nonOpaque().noBlockBreakParticles().strength(-1.0f, 3600000.0f).dropsNothing()
                    .instrument(Instrument.COW_BELL).pistonBehavior(PistonBehavior.IGNORE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    @NoBlockDrop
    public static final Block WAYPOINT_BANK = new WaypointBankBlock(
            FabricBlockSettings.create().nonOpaque().requiresTool().instrument(Instrument.BASEDRUM).strength(0.5F, 6.0F)
                    .pistonBehavior(PistonBehavior.IGNORE).luminance(light -> 3));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    @NoEnglish
    public static final Block LANDING_PAD = new LandingPadBlock(FabricBlockSettings.create().nonOpaque().requiresTool()
            .instrument(Instrument.BASEDRUM).strength(0.5F, 6.0F).pistonBehavior(PistonBehavior.IGNORE));

    @NoEnglish
    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block ENGINE_BLOCK = new EngineBlock(ABlockSettings.create()
            .itemSettings(new AItemSettings().group(AITItemGroups.FABRICATOR)).requiresTool()
            .instrument(Instrument.BASEDRUM).strength(1.5F, 6.0F).pistonBehavior(PistonBehavior.IGNORE));
    @PickaxeMineable
    public static final Block CONSOLE_GENERATOR = new ConsoleGeneratorBlock(
            FabricBlockSettings.create().nonOpaque().noBlockBreakParticles().requiresTool().strength(1.5F)
                    .instrument(Instrument.COW_BELL).pistonBehavior(PistonBehavior.DESTROY));
    @PickaxeMineable
    @NoEnglish
    public static final Block ARTRON_COLLECTOR_BLOCK = new ArtronCollectorBlock(
            FabricBlockSettings.create().nonOpaque().noBlockBreakParticles().requiresTool().strength(1F)
                    .instrument(Instrument.BANJO).pistonBehavior(PistonBehavior.IGNORE));

    // Coral Blocks
    @NoEnglish
    public static final Block CORAL_PLANT = new CoralPlantBlock(FabricBlockSettings.create().ticksRandomly().nonOpaque()
            .noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).pistonBehavior(PistonBehavior.DESTROY));
    @PickaxeMineable(tool = PickaxeMineable.Tool.STONE)
    public static final Block MATRIX_ENERGIZER = new MatrixEnergizerBlock(FabricBlockSettings.create().ticksRandomly().nonOpaque()
            .luminance(light -> 0).instrument(Instrument.COW_BELL)
            .strength(1.5F, 6.0F).requiresTool().sounds(BlockSoundGroup.ANVIL).pistonBehavior(PistonBehavior.IGNORE));

    @PickaxeMineable(tool = PickaxeMineable.Tool.DIAMOND)
    public static final Block TARDIS_CORAL_BLOCK = new Block(FabricBlockSettings.create().mapColor(MapColor.GOLD).solid().instrument(Instrument.BASEDRUM).requiresTool().strength(7f, 6.0f));

    @PickaxeMineable(tool = PickaxeMineable.Tool.DIAMOND)
    public static final Block TARDIS_CORAL_STAIRS = new StairsBlock(TARDIS_CORAL_BLOCK.getDefaultState(), FabricBlockSettings.create().mapColor(MapColor.GOLD).solid().instrument(Instrument.BASEDRUM).requiresTool().strength(7f, 6.0f));

    @PickaxeMineable(tool = PickaxeMineable.Tool.DIAMOND)
    public static final Block TARDIS_CORAL_SLAB = new SlabBlock(FabricBlockSettings.create().mapColor(MapColor.GOLD).solid().instrument(Instrument.BASEDRUM).requiresTool().strength(7f, 6.0f));

    @PickaxeMineable(tool = PickaxeMineable.Tool.DIAMOND)
    public static final Block TARDIS_CORAL_FAN = new TardisCoralFanBlock(FabricBlockSettings.create().mapColor(MapColor.GOLD).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS).pistonBehavior(PistonBehavior.DESTROY).requiresTool().strength(7f, 6.0f));

    @PickaxeMineable(tool = PickaxeMineable.Tool.DIAMOND)
    @NoBlockItem
    public static final Block TARDIS_CORAL_WALL_FAN = new TardisCoralWallBlock(FabricBlockSettings.create().mapColor(MapColor.GOLD).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS).pistonBehavior(PistonBehavior.DESTROY).requiresTool().strength(7f, 6.0f));


    // TARDIS Blocks

    @PickaxeMineable(tool = PickaxeMineable.Tool.STONE)
    @NoEnglish
    public static final Block MONITOR_BLOCK = new MonitorBlock(FabricBlockSettings.create().nonOpaque().requiresTool()
            .instrument(Instrument.COW_BELL).strength(1.5F, 6.0F).pistonBehavior(PistonBehavior.DESTROY));
    @NoEnglish
    public static final Block PLAQUE_BLOCK = new PlaqueBlock(
            FabricBlockSettings.create().nonOpaque().noBlockBreakParticles().instrument(Instrument.COW_BELL)
                    .strength(1.5F, 6.0F).pistonBehavior(PistonBehavior.DESTROY));
    @NoEnglish
    public static final Block WALL_MONITOR_BLOCK = new WallMonitorBlock(
            FabricBlockSettings.create().nonOpaque().noBlockBreakParticles().instrument(Instrument.COW_BELL)
                    .strength(1.5F, 6.0F).pistonBehavior(PistonBehavior.DESTROY));
    @NoEnglish
    public static final Block DETECTOR_BLOCK = new DetectorBlock(FabricBlockSettings.create().nonOpaque()
            .instrument(Instrument.COW_BELL).strength(1.5F, 6.0F).pistonBehavior(PistonBehavior.NORMAL));

    // Zeiton Blocks

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    @NoEnglish
    public static final Block ZEITON_BLOCK = new AmethystBlock(FabricBlockSettings.create().mapColor(MapColor.DARK_AQUA)
            .strength(1.5F, 6.0F).sounds(BlockSoundGroup.AMETHYST_BLOCK).requiresTool());

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block BUDDING_ZEITON = new BuddingZeitonBlock(
            FabricBlockSettings.create().mapColor(MapColor.DARK_AQUA).ticksRandomly().strength(1.5F, 6.0F)
                    .sounds(BlockSoundGroup.AMETHYST_BLOCK).requiresTool().pistonBehavior(PistonBehavior.DESTROY));
    @NoBlockDrop
    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block ZEITON_CLUSTER = new AmethystClusterBlock(7, 3,
            FabricBlockSettings.create().mapColor(MapColor.DARK_AQUA).solid().nonOpaque().ticksRandomly()
                    .sounds(BlockSoundGroup.AMETHYST_CLUSTER).strength(1.5F, 6.0F).luminance((state) -> 5)
                    .pistonBehavior(PistonBehavior.DESTROY));

    @PickaxeMineable(tool = PickaxeMineable.Tool.STONE)
    public static final Block LARGE_ZEITON_BUD = new AmethystClusterBlock(5, 3,
            FabricBlockSettings.copyOf(ZEITON_CLUSTER).sounds(BlockSoundGroup.MEDIUM_AMETHYST_BUD).solid()
                    .luminance((state) -> 4).pistonBehavior(PistonBehavior.DESTROY));

    @PickaxeMineable(tool = PickaxeMineable.Tool.STONE)
    public static final Block MEDIUM_ZEITON_BUD = new AmethystClusterBlock(4, 3,
            FabricBlockSettings.copyOf(ZEITON_CLUSTER).sounds(BlockSoundGroup.LARGE_AMETHYST_BUD).solid()
                    .luminance((state) -> 2).pistonBehavior(PistonBehavior.DESTROY));

    @PickaxeMineable(tool = PickaxeMineable.Tool.STONE)
    public static final Block SMALL_ZEITON_BUD = new AmethystClusterBlock(3, 4,
            FabricBlockSettings.copyOf(ZEITON_CLUSTER).sounds(BlockSoundGroup.SMALL_AMETHYST_BUD).solid()
                    .luminance((state) -> 1).pistonBehavior(PistonBehavior.DESTROY));

    @PickaxeMineable(tool = PickaxeMineable.Tool.STONE)
    @AutomaticModel
    public static final Block COMPACT_ZEITON = new Block(FabricBlockSettings.copyOf(ZEITON_BLOCK));

    @PickaxeMineable(tool = PickaxeMineable.Tool.STONE)
    @AutomaticModel
    public static final Block ZEITON_COBBLE = new Block(FabricBlockSettings.copyOf(ZEITON_BLOCK));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    @AutomaticModel()
    public static final Block POWER_CONVERTER = new PowerConverterBlock(ABlockSettings.create()
            .itemSettings(new AItemSettings().group(AITItemGroups.FABRICATOR)).nonOpaque()
            .requiresTool().instrument(Instrument.COW_BELL).strength(1.5F, 6.0F).pistonBehavior(PistonBehavior.DESTROY));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    @NoEnglish
    public static final Block GENERIC_SUBSYSTEM = new GenericSubSystemBlock(ABlockSettings.create()
            .itemSettings(new AItemSettings().group(AITItemGroups.FABRICATOR)).nonOpaque()
            .requiresTool().instrument(Instrument.COW_BELL).strength(1.5F, 6.0F).pistonBehavior(PistonBehavior.DESTROY));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    @NoEnglish
    public static final Block FOOD_MACHINE = new FoodMachineBlock(ABlockSettings.create()
            .itemSettings(new AItemSettings().group(AITItemGroups.MAIN)).nonOpaque()
            .requiresTool().instrument(Instrument.COW_BELL).strength(1.5F, 6.0F).pistonBehavior(PistonBehavior.DESTROY));

    @NoBlockItem
    public static final Block RADIO = new RadioBlock(FabricBlockSettings.create().nonOpaque());

    // Machines
    @NoBlockItem
    public static final Block MACHINE_CASING = new MachineCasingBlock(FabricBlockSettings.create().nonOpaque()
            .requiresTool().instrument(Instrument.COW_BELL).strength(1.5F, 6.0F));

    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block FABRICATOR = new FabricatorBlock(ABlockSettings.create()
            .itemSettings(new AItemSettings().group(AITItemGroups.FABRICATOR)).nonOpaque()
            .requiresTool().instrument(Instrument.COW_BELL).strength(1.5F, 6.0F));

    @AutomaticModel(justItem = true)
    @PickaxeMineable(tool = PickaxeMineable.Tool.IRON)
    public static final Block ASTRAL_MAP = new AstralMapBlock(FabricBlockSettings.create().nonOpaque().strength(1.5F, 6.0F));

    // Control Blocks
    @NoBlockItem
    @NoEnglish
    public static final Block REDSTONE_CONTROL_BLOCK = new RedstoneControlBlock(
            FabricBlockSettings.create().nonOpaque().strength(1.5F, 6.0F).pistonBehavior(PistonBehavior.DESTROY));

    public static final Block ENVIRONMENT_PROJECTOR = new EnvironmentProjectorBlock(FabricBlockSettings.create());

    // TODO ADVENT
    static {
        if (isUnlockedOnThisDay(Calendar.DECEMBER, 30)) {
            SNOW_GLOBE = new SnowGlobeBlock(FabricBlockSettings.create().nonOpaque().instrument(Instrument.GUITAR).strength(1.5F, 6.0F));
        }
    }

    static {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(entries -> {
            entries.addAfter(Items.AMETHYST_CLUSTER, ZEITON_BLOCK);
            entries.addAfter(ZEITON_BLOCK, BUDDING_ZEITON);
            entries.addAfter(BUDDING_ZEITON, SMALL_ZEITON_BUD);
            entries.addAfter(SMALL_ZEITON_BUD, MEDIUM_ZEITON_BUD);
            entries.addAfter(MEDIUM_ZEITON_BUD, LARGE_ZEITON_BUD);
            entries.addAfter(LARGE_ZEITON_BUD, ZEITON_CLUSTER);
            entries.addAfter(ZEITON_CLUSTER, CHARGED_ZEITON_CRYSTAL);

            entries.addAfter(Items.RAW_GOLD, COMPACT_ZEITON);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.addAfter(Items.AMETHYST_SHARD, ZEITON_SHARD);
            entries.addAfter(Items.SUGAR, ZEITON_DUST);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.addAfter(Items.COBBLESTONE, ZEITON_COBBLE);
        });

    }

    @NoEnglish
    public static final Block CABLE_BLOCK = new CableBlock(ABlockSettings.create()
            .itemSettings(new AItemSettings().group(AITItemGroups.FABRICATOR)).nonOpaque()
            .instrument(Instrument.GUITAR).strength(1.5F, 6.0F));

    @NoEnglish
    public static final Block FULL_CABLE_BLOCK = new FullCableBlock(ABlockSettings.create()
            .itemSettings(new AItemSettings().group(AITItemGroups.FABRICATOR)).nonOpaque()
            .instrument(Instrument.GUITAR).strength(1.5F, 6.0F));

    public static List<Block> get() {
        List<Block> list = new ArrayList<>();

        for (Block block : Registries.BLOCK) {
            if (Registries.BLOCK.getId(block).getNamespace().equalsIgnoreCase(AITMod.MOD_ID)) {
                list.add(block);
            }
        }

        return list;
    }

    @Override
    public Item.Settings createBlockItemSettings(Block block) {
        return new AItemSettings().group(AITItemGroups.MAIN);
    }
}
