package dev.amble.ait.core;


import static dev.amble.ait.core.AITItems.isUnlockedOnThisDay;

import java.util.Calendar;

import dev.amble.lib.container.impl.BlockEntityContainer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import net.minecraft.block.entity.BlockEntityType;

import dev.amble.ait.core.blockentities.*;
import dev.amble.ait.core.blockentities.control.RedstoneControlBlockEntity;
import dev.amble.ait.core.blocks.PowerConverterBlock;
import dev.amble.ait.core.engine.block.generic.GenericStructureSystemBlockEntity;
import dev.amble.ait.core.engine.link.block.CableBlockEntity;
import dev.amble.ait.core.engine.link.block.FullCableBlockEntity;
import dev.amble.ait.module.planet.core.PlanetBlocks;

public class AITBlockEntityTypes implements BlockEntityContainer {
    public static BlockEntityType<SnowGlobeBlockEntity> SNOW_GLOBE_BLOCK_ENTITY_TYPE;
    public static BlockEntityType<ExteriorBlockEntity> EXTERIOR_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(ExteriorBlockEntity::new, AITBlocks.EXTERIOR_BLOCK).build();
    public static BlockEntityType<DoorBlockEntity> DOOR_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(DoorBlockEntity::new, AITBlocks.DOOR_BLOCK).build();
    public static BlockEntityType<ConsoleBlockEntity> CONSOLE_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(ConsoleBlockEntity::new, AITBlocks.CONSOLE).build();
    public static BlockEntityType<ConsoleGeneratorBlockEntity> CONSOLE_GENERATOR_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(ConsoleGeneratorBlockEntity::new, AITBlocks.CONSOLE_GENERATOR).build();
    public static BlockEntityType<CoralBlockEntity> CORAL_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(CoralBlockEntity::new, AITBlocks.CORAL_PLANT).build();
    public static BlockEntityType<MatrixEnergizerBlockEntity> MATRIX_ENERGIZER_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(MatrixEnergizerBlockEntity::new, AITBlocks.MATRIX_ENERGIZER).build();
    public static BlockEntityType<MonitorBlockEntity> MONITOR_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(MonitorBlockEntity::new, AITBlocks.MONITOR_BLOCK).build();
    public static BlockEntityType<DetectorBlockEntity> DETECTOR_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(DetectorBlockEntity::new, AITBlocks.DETECTOR_BLOCK).build();
    public static BlockEntityType<ArtronCollectorBlockEntity> ARTRON_COLLECTOR_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(ArtronCollectorBlockEntity::new, AITBlocks.ARTRON_COLLECTOR_BLOCK).build();
    public static BlockEntityType<PlaqueBlockEntity> PLAQUE_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(PlaqueBlockEntity::new, AITBlocks.PLAQUE_BLOCK).build();
    public static BlockEntityType<EngineBlockEntity> ENGINE_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(EngineBlockEntity::new, AITBlocks.ENGINE_BLOCK).build();
    public static BlockEntityType<WallMonitorBlockEntity> WALL_MONITOR_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(WallMonitorBlockEntity::new, AITBlocks.WALL_MONITOR_BLOCK).build();
    public static BlockEntityType<MachineCasingBlockEntity> MACHINE_CASING_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(MachineCasingBlockEntity::new, AITBlocks.MACHINE_CASING).build();
    public static BlockEntityType<FabricatorBlockEntity> FABRICATOR_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(FabricatorBlockEntity::new, AITBlocks.FABRICATOR).build();
    public static BlockEntityType<EnvironmentProjectorBlockEntity> ENVIRONMENT_PROJECTOR_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(EnvironmentProjectorBlockEntity::new, AITBlocks.ENVIRONMENT_PROJECTOR).build();
    public static BlockEntityType<WaypointBankBlockEntity> WAYPOINT_BANK_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(WaypointBankBlockEntity::new, AITBlocks.WAYPOINT_BANK).build();
    public static final BlockEntityType<AITRadioBlockEntity> AIT_RADIO_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(AITRadioBlockEntity::new, AITBlocks.RADIO).build();
    public static BlockEntityType<RedstoneControlBlockEntity> REDSTONE_CONTROL_BLOCK_ENTITY = FabricBlockEntityTypeBuilder
            .create(RedstoneControlBlockEntity::new, AITBlocks.REDSTONE_CONTROL_BLOCK).build();
    public static final BlockEntityType<FlagBlockEntity> FLAG_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(FlagBlockEntity::new, PlanetBlocks.FLAG).build();
    public static BlockEntityType<CableBlockEntity> CABLE_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(CableBlockEntity::new, AITBlocks.CABLE_BLOCK).build();
    public static BlockEntityType<FullCableBlockEntity> FULL_CABLE_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(FullCableBlockEntity::new, AITBlocks.FULL_CABLE_BLOCK).build();
    public static BlockEntityType<PowerConverterBlock.BlockEntity> POWER_CONVERTER_BLOCK_TYPE = FabricBlockEntityTypeBuilder
            .create(PowerConverterBlock.BlockEntity::new, AITBlocks.POWER_CONVERTER).build();
    public static BlockEntityType<GenericStructureSystemBlockEntity> GENERIC_SUBSYSTEM_BLOCK_TYPE = FabricBlockEntityTypeBuilder
            .create(GenericStructureSystemBlockEntity::new, AITBlocks.GENERIC_SUBSYSTEM).build();
    public static BlockEntityType<AstralMapBlockEntity> ASTRAL_MAP = FabricBlockEntityTypeBuilder
            .create(AstralMapBlockEntity::new, AITBlocks.ASTRAL_MAP).build();
    // TODO ADVENT might have to make this work like the block as well
    static {
        if (isUnlockedOnThisDay(Calendar.DECEMBER, 30)) {
            SNOW_GLOBE_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
                    .create(SnowGlobeBlockEntity::new, AITBlocks.SNOW_GLOBE).build();
        }
    }
}
