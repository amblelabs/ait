package dev.amble.ait.core;

import dev.amble.ait.core.entities.*;
import dev.amble.lib.container.AssignedName;
import dev.amble.lib.container.impl.EntityContainer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.world.Heightmap;

public class AITEntityTypes implements EntityContainer {

    @AssignedName("control_entity")
    public static final EntityType<ConsoleControlEntity> CONTROL_ENTITY_TYPE = FabricEntityTypeBuilder
            .create(SpawnGroup.MISC, ConsoleControlEntity::new).dimensions(EntityDimensions.changing(0.125f, 0.125f))
            .disableSummon()
            .build();

    @AssignedName("falling_tardis")
    public static final EntityType<FallingTardisEntity> FALLING_TARDIS_TYPE = FabricEntityTypeBuilder
            .create(SpawnGroup.MISC, FallingTardisEntity::new).dimensions(EntityDimensions.changing(0.98f, 0.98f))
            .disableSummon()
            .build();

    @AssignedName("flight_tardis")
    public static final EntityType<FlightTardisEntity> FLIGHT_TARDIS_TYPE = FabricEntityTypeBuilder
            .create(SpawnGroup.MISC, FlightTardisEntity::new).dimensions(EntityDimensions.changing(0.98f, 0.98f))
            .disableSummon()
            .build();
    public static final EntityType<GallifreyFallsPaintingEntity> GALLIFREY_FALLS_PAINTING_ENTITY_TYPE = FabricEntityTypeBuilder
            .create(SpawnGroup.MISC, GallifreyFallsPaintingEntity::new)
            .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build();
    public static final EntityType<TrenzalorePaintingEntity> TRENZALORE_PAINTING_ENTITY_TYPE = FabricEntityTypeBuilder
            .create(SpawnGroup.MISC, TrenzalorePaintingEntity::new)
            .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build();

//    public static final EntityType<CobbledSnowballEntity> COBBLED_SNOWBALL_TYPE = FabricEntityTypeBuilder
//            .<CobbledSnowballEntity>create(SpawnGroup.MISC, CobbledSnowballEntity::new)
//            .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).trackRangeBlocks(4).trackedUpdateRate(10).build();

    public static final EntityType<RiftEntity> RIFT_ENTITY = FabricEntityTypeBuilder.Mob.createMob()
            .spawnGroup(SpawnGroup.AMBIENT).entityFactory(RiftEntity::new)
            .dimensions(EntityDimensions.fixed(1f, 1f)).spawnRestriction(SpawnRestriction.Location.NO_RESTRICTIONS,
                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, RiftEntity::canSpawn).spawnableFarFromPlayer().build();
}
