package dev.amble.ait.core;

import dev.amble.lib.container.impl.ItemGroupContainer;
import dev.amble.lib.itemgroup.AItemGroup;

import net.minecraft.item.ItemStack;

import dev.amble.ait.AITMod;

public class AITItemGroups implements ItemGroupContainer {

    public static final AItemGroup MAIN = AItemGroup.builder(AITMod.id("item_group"))
            .icon(() -> new ItemStack(AITItems.TARDIS_ITEM)).build();

    public static final AItemGroup FABRICATOR = AItemGroup.builder(AITMod.id("fabricator"))
            .icon(() -> new ItemStack(AITItems.BLUEPRINT)).build();
}
