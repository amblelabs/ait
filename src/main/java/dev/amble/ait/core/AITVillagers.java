package dev.amble.ait.core;

import com.google.common.collect.ImmutableSet;
import dev.amble.ait.AITMod;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class AITVillagers {
    public static final RegistryKey<PointOfInterestType> FABRICATOR_ENGINEER_POI_KEY = poiKey("fabricator_engineer_poi");
    public static final PointOfInterestType FABRICATOR_ENGINEER_POI = registerPoi("fabricator_engineer_poi", AITBlocks.FABRICATOR);

    public static final VillagerProfession FABRICATOR_ENGINEER = registerProfession("fabricator_engineer", FABRICATOR_ENGINEER_POI_KEY);


    private static VillagerProfession registerProfession(String name, RegistryKey<PointOfInterestType> type) {
        return Registry.register(Registries.VILLAGER_PROFESSION, AITMod.id(name),
                new VillagerProfession(name, entry -> entry.matchesKey(type), entry -> entry.matchesKey(type),
                        ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_WEAPONSMITH));
    }

    private static PointOfInterestType registerPoi(String name, Block block) {
        return PointOfInterestHelper.register(AITMod.id(name), 1, 1, block);
    }

    private static RegistryKey<PointOfInterestType> poiKey(String name) {
        return RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, AITMod.id(name));
    }

    public static void init() {
    }
}
