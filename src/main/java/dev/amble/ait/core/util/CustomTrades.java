package dev.amble.ait.core.util;

import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITVillagers;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;

public class CustomTrades {
    public static void register() {
        // Fabricator Engineers Trades
        // Level 1
        TradeOfferHelper.registerVillagerOffers(AITVillagers.FABRICATOR_ENGINEER, 1,
                factories -> {
                    factories.add((entity, random) -> new TradeOffer(
                            new ItemStack(Items.EMERALD, 4),
                            new ItemStack(AITItems.PHOTON_ACCELERATOR, 3),
                            6, 5, 0.075f));
                });


        TradeOfferHelper.registerVillagerOffers(AITVillagers.FABRICATOR_ENGINEER, 1,
                factories -> {
                    factories.add((entity, random) -> new TradeOffer(
                            new ItemStack(Items.IRON_INGOT, 5),
                            new ItemStack(AITItems.ORTHOGONAL_ENGINE_FILTER, 2),
                            3, 3, 0.045f));
                });

        TradeOfferHelper.registerVillagerOffers(AITVillagers.FABRICATOR_ENGINEER, 1,
                factories -> {
                    factories.add((entity, random) -> new TradeOffer(
                            new ItemStack(Items.COPPER_INGOT, 5),
                            new ItemStack(Items.IRON_INGOT, 3),
                            new ItemStack(AITItems.HYPERION_CORE_SHAFT, 1),
                            2, 4, 3, 0.105f));
                });

        // Level 2
        TradeOfferHelper.registerVillagerOffers(AITVillagers.FABRICATOR_ENGINEER, 2,
                factories -> {
                    factories.add((entity, random) -> new TradeOffer(
                            new ItemStack(AITItems.ZEITON_SHARD, 3),
                            new ItemStack(AITItems.ARTRON_FLUID_LINK, 1),
                            4, 3, 0.025f));
                });
        TradeOfferHelper.registerVillagerOffers(AITVillagers.FABRICATOR_ENGINEER, 2,
                factories -> {
                    factories.add((entity, random) -> new TradeOffer(
                            new ItemStack(Items.EMERALD, 5),
                            new ItemStack(AITItems.DATA_FLUID_LINK, 2),
                            3, 4, 0.095f));
                });
        TradeOfferHelper.registerVillagerOffers(AITVillagers.FABRICATOR_ENGINEER, 2,
                factories -> {
                    factories.add((entity, random) -> new TradeOffer(
                            new ItemStack(AITItems.CHARGED_ZEITON_CRYSTAL, 3),
                            new ItemStack(AITItems.ARTRON_FLUID_LINK, 1),
                            4, 2, 0.075f));
                });
        TradeOfferHelper.registerVillagerOffers(AITVillagers.FABRICATOR_ENGINEER, 2,
                factories -> {
                    factories.add((entity, random) -> new TradeOffer(
                            new ItemStack(Items.EMERALD, 3),
                            new ItemStack(AITItems.SONIC_SCREWDRIVER, 1),
                            1, 2, 0.075f));
                });
        // Level 3

        TradeOfferHelper.registerVillagerOffers(AITVillagers.FABRICATOR_ENGINEER, 3,
                factories -> {
                    factories.add((entity, random) -> new TradeOffer(
                            new ItemStack(Items.GOLD_INGOT, 6),
                            new ItemStack(AITItems.BLUEPRINT, 1),
                            4, 3, 0.025f));
                });
        TradeOfferHelper.registerVillagerOffers(AITVillagers.FABRICATOR_ENGINEER, 3,
                factories -> {
                    factories.add((entity, random) -> new TradeOffer(
                            new ItemStack(Items.EMERALD, 1),
                            new ItemStack(AITItems.BLUEPRINT, 1),
                            3, 4, 0.095f));
                });
        TradeOfferHelper.registerVillagerOffers(AITVillagers.FABRICATOR_ENGINEER, 3,
                factories -> {
                    factories.add((entity, random) -> new TradeOffer(
                            new ItemStack(AITItems.HYPERION_CORE_SHAFT, 8),
                            new ItemStack(AITItems.BLUEPRINT, 1),
                            4, 2, 0.075f));
                });

    }
}
