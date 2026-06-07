package dev.amble.ait.datagen.datagen_providers;


import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;

import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITTags;
import dev.amble.ait.module.ModuleRegistry;
import dev.amble.ait.module.planet.core.PlanetItems;

public class AITItemTagProvider extends FabricTagProvider<Item> {
    public AITItemTagProvider(FabricDataOutput output,
            @Nullable CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, RegistryKeys.ITEM, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        // Items
        getOrCreateTagBuilder(AITTags.Items.SONIC_ITEM).add(AITItems.SONIC_SCREWDRIVER);

        getOrCreateTagBuilder(ItemTags.CREEPER_DROP_MUSIC_DISCS)
                .add(AITItems.TWO_THOUSAND_MUSIC_DISC)
                .add(AITItems.WONDERFUL_TIME_IN_SPACE_MUSIC_DISC)
                .add(AITItems.GOOD_MAN_MUSIC_DISC)
                .add(AITItems.AIT_THEME_MUSIC_DISC)
                .add(AITItems.EARTH_MUSIC_DISC)
                .add(AITItems.VENUS_MUSIC_DISC)
                .add(AITItems.CRASH_MUSIC_DISC);

        getOrCreateTagBuilder(AITTags.Items.CLUSTER_MAX_HARVESTABLES).add(AITItems.ZEITON_SHARD);

        getOrCreateTagBuilder(AITTags.Items.NO_BOP).add(AITItems.SONIC_SCREWDRIVER);

        getOrCreateTagBuilder(AITTags.Items.FULL_RESPIRATORS).add(AITItems.RESPIRATOR);

        getOrCreateTagBuilder(AITTags.Items.HALF_RESPIRATORS).add(AITItems.FACELESS_RESPIRATOR);

        getOrCreateTagBuilder(AITTags.Items.KEY).add(AITItems.IRON_KEY, AITItems.GOLD_KEY, AITItems.CLASSIC_KEY,
                AITItems.NETHERITE_KEY, AITItems.SKELETON_KEY);

        getOrCreateTagBuilder(AITTags.Items.IS_TARDIS_FUEL).add(AITItems.ZEITON_DUST, AITItems.ZEITON_SHARD,
                AITBlocks.TARDIS_CORAL_BLOCK.asItem(), AITBlocks.TARDIS_CORAL_SLAB.asItem(),
                AITBlocks.TARDIS_CORAL_FAN.asItem(), AITBlocks.TARDIS_CORAL_STAIRS.asItem(),
                AITItems.CORAL_FRAGMENT);
        getOrCreateTagBuilder(AITTags.Items.IS_TARDIS_FUEL).forceAddTag(ItemTags.LOGS_THAT_BURN);
        getOrCreateTagBuilder(AITTags.Items.IS_TARDIS_FUEL).forceAddTag(ItemTags.COALS);
        getOrCreateTagBuilder(AITTags.Items.IS_TARDIS_FUEL).add(Items.LAVA_BUCKET);

        // Rifts

        getOrCreateTagBuilder(AITTags.Items.RIFT_SUCCESS_EXTRA_ITEM).add(AITItems.ZEITON_SHARD);
        getOrCreateTagBuilder(AITTags.Items.RIFT_FAIL_ITEM).add(Items.PAPER);

        //Linkable

        getOrCreateTagBuilder(AITTags.Items.LINK).add(AITItems.SONIC_SCREWDRIVER, AITItems.CLASSIC_KEY, AITItems.GOLD_KEY, AITItems.IRON_KEY, AITItems.REMOTE_ITEM,AITItems.NETHERITE_KEY, PlanetItems.HANDLES);

        ModuleRegistry.instance().iterator().forEachRemaining(module -> {
            module.getDataGenerator().ifPresent(generator -> {
                generator.itemTags(this);
            });
        });
    }

    @Override
    public FabricTagProvider<Item>.FabricTagBuilder getOrCreateTagBuilder(TagKey<Item> tag) {
        return super.getOrCreateTagBuilder(tag);
    }
}
