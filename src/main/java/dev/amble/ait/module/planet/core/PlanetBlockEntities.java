package dev.amble.ait.module.planet.core;

import dev.amble.lib.container.impl.BlockEntityContainer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import net.minecraft.block.entity.BlockEntityType;

import dev.amble.ait.module.planet.core.blockentities.OxygenatorBlockEntity;

public class PlanetBlockEntities implements BlockEntityContainer {

    public static BlockEntityType<OxygenatorBlockEntity> OXYGENATOR_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(OxygenatorBlockEntity::new, PlanetBlocks.OXYGENATOR_BLOCK).build();

}
