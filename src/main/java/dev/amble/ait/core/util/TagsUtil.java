package dev.amble.ait.core.util;

import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.World;

public class TagsUtil {
    public static Item getRandomItemFromTag(World world, TagKey<Item> tag) {
        RegistryWrapper.Impl<Item> itemLookup = world.getRegistryManager().getWrapperOrThrow(RegistryKeys.ITEM);
        Optional<RegistryEntryList.Named<Item>> optionalList = itemLookup.getOptional(tag);

        if (optionalList.isEmpty()) {
            return Items.AIR;
        }

        RegistryEntryList.Named<Item> list = optionalList.get();
        RegistryEntry<Item> randomEntry = list.get(world.random.nextInt(list.size()));

        return randomEntry.value();
    }

    public static Block getRandomBlockFromTag(World world, TagKey<Block> tag) {
        RegistryWrapper.Impl<Block> blockLookup = world.getRegistryManager().getWrapperOrThrow(RegistryKeys.BLOCK);
        Optional<RegistryEntryList.Named<Block>> optionalList = blockLookup.getOptional(tag);

        if (optionalList.isEmpty()) {
            return Blocks.AIR;
        }

        RegistryEntryList.Named<Block> list = optionalList.get();
        RegistryEntry<Block> randomEntry = list.get(world.random.nextInt(list.size()));

        return randomEntry.value();
    }

}
