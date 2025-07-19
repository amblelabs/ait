package dev.amble.ait.core.util;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;

import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITVillagers;
import dev.amble.ait.module.planet.core.PlanetBlocks;

public class CustomTrades {
    public static void register() {
        // Level 1
        TradeOfferHelper.registerVillagerOffers(AITVillagers.FABRICATOR_ENGINEER, 1, factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 3 + random.nextInt(3)),
                    new ItemStack(AITItems.RIFT_SCANNER, 1),
                    1, 5, 0.35f));

            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 2 + random.nextInt(2)),
                    new ItemStack(AITItems.ZEITON_SHARD, 2),
                    5, 3, 0.25f));

            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 4),
                    new ItemStack(AITItems.HYPERION_CORE_SHAFT, 1),
                    2, 3, 0.3f));
        });

        // Level 2
        TradeOfferHelper.registerVillagerOffers(AITVillagers.FABRICATOR_ENGINEER, 2, factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 4),
                    new ItemStack(AITItems.ARTRON_FLUID_LINK, 1),
                    3, 4, 0.35f));

            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 4),
                    new ItemStack(AITItems.ARTRON_MERCURIAL_LINK, 1),
                    3, 4, 0.35f));

            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 2 + random.nextInt(2)),
                    new ItemStack(AITItems.TRANSWARP_RESONATOR, 1),
                    3, 4, 0.3f));

            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 17),
                    new ItemStack(AITBlocks.MATRIX_ENERGIZER, 1),
                    1, 2, 0.45f));
        });

        // Level 3
        TradeOfferHelper.registerVillagerOffers(AITVillagers.FABRICATOR_ENGINEER, 3, factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 2 + random.nextInt(2)),
                    new ItemStack(AITItems.SUPERHEATED_ZEITON, 1),
                    new ItemStack(AITItems.PLASMIC_MATERIAL, 1),
                    1, 4, 3, 0.4f));

            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 1 + random.nextInt(2)),
                    new ItemStack(Items.COPPER_INGOT, 1),
                    new ItemStack(AITBlocks.CABLE_BLOCK, 5),
                    3, 4, 2, 0.25f));

            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 3 + random.nextInt(2)),
                    new ItemStack(AITItems.PHOTON_ACCELERATOR, 1),
                    2, 2, 0.35f));

            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 8),
                    new ItemStack(AITBlocks.CONSOLE_GENERATOR, 1),
                    1, 2, 0.4f));
        });

        // Level 4
        TradeOfferHelper.registerVillagerOffers(AITVillagers.FABRICATOR_ENGINEER, 4, factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 5 + random.nextInt(2)),
                    new ItemStack(AITItems.ZEITON_SHARD, 1),
                    new ItemStack(AITBlocks.GENERIC_SUBSYSTEM, 1),
                    2, 4, 3, 0.45f));

            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 2 + random.nextInt(2)),
                    new ItemStack(AITItems.BLUEPRINT, 1),
                    1, 4, 0.35f));

            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 4 + random.nextInt(2)),
                    new ItemStack(AITItems.ORTHOGONAL_ENGINE_FILTER, 1),
                    4, 3, 0.3f));

            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 4),
                    new ItemStack(AITItems.WAYPOINT_CARTRIDGE, 1),
                    4, 3, 0.25f));

            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 12),
                    new ItemStack(AITItems.WAYPOINT_CARTRIDGE, 1),
                    new ItemStack(AITBlocks.WAYPOINT_BANK, 1),
                    1, 2, 3, 0.5f));
        });

        // Level 5
        TradeOfferHelper.registerVillagerOffers(AITVillagers.FABRICATOR_ENGINEER, 5, factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 17),
                    new ItemStack(AITItems.GOLD_KEY_UPGRADE_SMITHING_TEMPLATE, 1),
                    1, 3, 0.3f));

            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 10),
                    new ItemStack(PlanetBlocks.OXYGENATOR_BLOCK, 1),
                    2, 3, 0.25f));

            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 4),
                    new ItemStack(AITBlocks.LANDING_PAD, 1),
                    1, 2, 0.2f));

            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 2 + random.nextInt(3)),
                    new ItemStack(AITItems.REDSTONE_CONTROL, 1),
                    3, 2, 0.2f));
        });
    }
}
