package dev.pavatus.christmas.core;

import dev.pavatus.christmas.ChristmasModule;
import io.wispforest.owo.itemgroup.OwoItemSettings;

import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import loqor.ait.AITMod;
import loqor.ait.core.item.KeyItem;

public class ChristmasItems {
    public static KeyItem FESTIVE_KEY = new KeyItem(new OwoItemSettings().group(ChristmasModule.ITEM_GROUP).rarity(Rarity.EPIC));

    public static void init() {
        if (ChristmasModule.Feature.FESTIVE_KEY.isUnlocked()) {
            register(FESTIVE_KEY, "festive_key");
        }
    }
    public static <T extends Item> T register(T item, Identifier id) {
        return Registry.register(Registries.ITEM, id, item);
    }
    public static <T extends Item> T register(T item, String name) {
        return register(item, AITMod.id(name));
    }
}
