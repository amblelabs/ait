package dev.amble.ait.module.gun.core.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;

import dev.amble.lib.container.impl.EntityContainer;

public class GunEntityTypes implements EntityContainer {

    public static final EntityType<StaserBoltEntity> STASER_BOLT_ENTITY_TYPE = EntityType.Builder
            .create(StaserBoltEntity::new, SpawnGroup.MISC)
            .setDimensions(0.5f, 0.5f).build("staser_bolt_entity_type");
}
