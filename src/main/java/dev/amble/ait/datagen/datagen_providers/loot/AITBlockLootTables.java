package dev.amble.ait.datagen.datagen_providers.loot;

import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITTags;
import dev.amble.ait.module.ModuleRegistry;
import dev.amble.ait.module.planet.core.PlanetBlocks;
import dev.amble.lib.datagen.loot.AmbleBlockLootTable;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.item.ItemPredicate;

public class AITBlockLootTables extends AmbleBlockLootTable {

    public AITBlockLootTables(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate() {
        ModuleRegistry.instance().iterator().forEachRemaining(module -> module.getBlockRegistry().ifPresent(this::withBlocks));
        this.withBlocks(AITBlocks.class);

        super.generate();

        this.addDrop(AITBlocks.ZEITON_CLUSTER,
                (block) -> dropsWithSilkTouch(block, ItemEntry.builder(AITItems.ZEITON_SHARD)
                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(4.0F)))
                        .apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))
                        .conditionally(MatchToolLootCondition
                                .builder(ItemPredicate.Builder.create().tag(AITTags.Items.CLUSTER_MAX_HARVESTABLES)))
                        .alternatively(this.applyExplosionDecay(block, ItemEntry.builder(AITItems.ZEITON_SHARD)
                                .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F)))))));


        addDrop(AITBlocks.WAYPOINT_BANK, doorDrops(AITBlocks.WAYPOINT_BANK));

        // Martian
        addDrop(PlanetBlocks.MARTIAN_STONE, drops(PlanetBlocks.MARTIAN_STONE, PlanetBlocks.MARTIAN_COBBLESTONE));
        addDrop(PlanetBlocks.MARTIAN_BRICK_SLAB, slabDrops(PlanetBlocks.MARTIAN_BRICK_SLAB));
        addDrop(PlanetBlocks.MARTIAN_COBBLESTONE_SLAB, slabDrops(PlanetBlocks.MARTIAN_COBBLESTONE_SLAB));
        addDrop(PlanetBlocks.SMOOTH_MARTIAN_STONE_SLAB, slabDrops(PlanetBlocks.SMOOTH_MARTIAN_STONE_SLAB));

        // Ore
        addDrop(PlanetBlocks.ANORTHOSITE_COAL_ORE, oreDrops(PlanetBlocks.ANORTHOSITE_COAL_ORE, Items.COAL));
        addDrop(PlanetBlocks.ANORTHOSITE_COPPER_ORE, oreDrops(PlanetBlocks.ANORTHOSITE_COPPER_ORE, Items.RAW_COPPER));
        addDrop(PlanetBlocks.ANORTHOSITE_IRON_ORE, oreDrops(PlanetBlocks.ANORTHOSITE_IRON_ORE, Items.RAW_IRON));
        addDrop(PlanetBlocks.ANORTHOSITE_LAPIS_ORE, oreDrops(PlanetBlocks.ANORTHOSITE_LAPIS_ORE, Items.LAPIS_LAZULI));
        addDrop(PlanetBlocks.ANORTHOSITE_REDSTONE_ORE, oreDrops(PlanetBlocks.ANORTHOSITE_REDSTONE_ORE, Items.REDSTONE));
        addDrop(PlanetBlocks.ANORTHOSITE_GOLD_ORE, oreDrops(PlanetBlocks.ANORTHOSITE_GOLD_ORE, Items.RAW_GOLD));
        addDrop(PlanetBlocks.ANORTHOSITE_DIAMOND_ORE, oreDrops(PlanetBlocks.ANORTHOSITE_DIAMOND_ORE, Items.DIAMOND));
        addDrop(PlanetBlocks.ANORTHOSITE_EMERALD_ORE, oreDrops(PlanetBlocks.ANORTHOSITE_EMERALD_ORE, Items.EMERALD));

        addDrop(PlanetBlocks.MARTIAN_COAL_ORE, oreDrops(PlanetBlocks.MARTIAN_COAL_ORE, Items.COAL));
        addDrop(PlanetBlocks.MARTIAN_COPPER_ORE, oreDrops(PlanetBlocks.MARTIAN_COPPER_ORE, Items.RAW_COPPER));
        addDrop(PlanetBlocks.MARTIAN_IRON_ORE, oreDrops(PlanetBlocks.MARTIAN_IRON_ORE, Items.RAW_IRON));
        addDrop(PlanetBlocks.MARTIAN_LAPIS_ORE, oreDrops(PlanetBlocks.MARTIAN_LAPIS_ORE, Items.LAPIS_LAZULI));
        addDrop(PlanetBlocks.MARTIAN_REDSTONE_ORE, oreDrops(PlanetBlocks.MARTIAN_REDSTONE_ORE, Items.REDSTONE));
        addDrop(PlanetBlocks.MARTIAN_GOLD_ORE, oreDrops(PlanetBlocks.MARTIAN_GOLD_ORE, Items.RAW_GOLD));
        addDrop(PlanetBlocks.MARTIAN_DIAMOND_ORE, oreDrops(PlanetBlocks.MARTIAN_DIAMOND_ORE, Items.DIAMOND));
        addDrop(PlanetBlocks.MARTIAN_EMERALD_ORE, oreDrops(PlanetBlocks.MARTIAN_EMERALD_ORE, Items.EMERALD));

        // Anorthosite
        addDrop(PlanetBlocks.ANORTHOSITE_BRICK_SLAB, slabDrops(PlanetBlocks.ANORTHOSITE_BRICK_SLAB));
        addDrop(PlanetBlocks.ANORTHOSITE_SLAB, slabDrops(PlanetBlocks.ANORTHOSITE_SLAB));
        addDrop(PlanetBlocks.POLISHED_ANORTHOSITE_SLAB, slabDrops(PlanetBlocks.POLISHED_ANORTHOSITE_SLAB));
    }
}
