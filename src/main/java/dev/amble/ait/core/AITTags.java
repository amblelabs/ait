package dev.amble.ait.core;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.event.GameEvent;

import dev.amble.ait.AITMod;

public class AITTags {

    public static class Blocks {
        public static final TagKey<Block> SONIC_INTERACTABLE = createTag("sonic_interactable");
        public static final TagKey<Block> SONIC_CAN_LOCATE = createTag("sonic_can_locate");
        public static final TagKey<Block> FLUID_LINK_CAN_CONNECT = createTag("fluid_link_can_connect");

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, AITMod.id(name));
        }
    }

    public static class Items {
        public static final TagKey<Item> FULL_RESPIRATORS = createTag("full_respirators");
        public static final TagKey<Item> HALF_RESPIRATORS = createTag("half_respirators");
        public static final TagKey<Item> SONIC_ITEM = createTag("sonic_item");
        public static final TagKey<Item> CLUSTER_MAX_HARVESTABLES = createTag("cluster_max_harvestables");

        public static final TagKey<Item> NO_BOP = createTag("no_bop");
        public static final TagKey<Item> KEY = createTag("key");
        public static final TagKey<Item> GOAT_HORN = createTag("goat_horn");
        public static final TagKey<Item> LINK = createTag("link"); // TODO use the tag instead of the item instanceof
        public static final TagKey<Item> IS_TARDIS_FUEL = createTag("is_tardis_fuel");
        public static final TagKey<Item> REPAIRS_SUBSYSTEM = createTag("repairs_subsystem");
        public static final TagKey<Item> INSERTABLE_DISCS = createTag("insertable_discs");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, AITMod.id(name));
        }
    }

    public static class EntityTypes {

        public static final TagKey<EntityType<?>> BOSS = createTag("boss");
        public static final TagKey<EntityType<?>> NON_DISMOUNTABLE = createTag("non_dismountable");

        private static TagKey<EntityType<?>> createTag(String name) {
            return TagKey.of(RegistryKeys.ENTITY_TYPE, AITMod.id(name));
        }
    }

    public static class GameEvents {
        public static final TagKey<GameEvent> MATRIX_CAN_LISTEN = createTag("matrix_can_listen");
        public static TagKey<GameEvent> createTag(String name) {
            return TagKey.of(RegistryKeys.GAME_EVENT, AITMod.id(name));
        }
    }
}
