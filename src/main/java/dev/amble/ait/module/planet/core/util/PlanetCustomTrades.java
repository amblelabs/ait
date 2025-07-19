package dev.amble.ait.module.planet.core.util;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;

import dev.amble.ait.module.planet.core.PlanetBlocks;
import dev.amble.ait.module.planet.core.PlanetItems;

public class PlanetCustomTrades {
    public static void registerCustomTrades() {
        // Wandering Trader
        TradeOfferHelper.registerWanderingTraderOffers(3,
                factories -> {
                    factories.add((entity, random) -> new TradeOffer(
                            new ItemStack(Items.EMERALD, 6),
                            new ItemStack(PlanetBlocks.ANORTHOSITE, 3),
                            1, 24, 0.075f));

                    factories.add((entity, random) -> new TradeOffer(
                            new ItemStack(Items.EMERALD, 9),
                            new ItemStack(PlanetBlocks.MARTIAN_STONE, 3),
                            1, 24, 0.075f));
                });

        TradeOfferHelper.registerWanderingTraderOffers(2,
                factories -> {
                    factories.add((entity, random) -> new TradeOffer(
                            new ItemStack(Items.EMERALD, 2),
                            new ItemStack(PlanetItems.ANORTHOSITE_PICKAXE, 1),
                            3, 12, 0.075f));

                    factories.add((entity, random) -> new TradeOffer(
                            new ItemStack(Items.EMERALD, 3),
                            new ItemStack(PlanetItems.ANORTHOSITE_SWORD, 1),
                            5, 12, 0.075f));

                    factories.add((entity, random) -> new TradeOffer(
                            new ItemStack(Items.EMERALD, 3),
                            new ItemStack(PlanetItems.MARTIAN_STONE_PICKAXE, 1),
                            7, 12, 0.075f));

                    factories.add((entity, random) -> new TradeOffer(
                            new ItemStack(Items.EMERALD, 1),
                            new ItemStack(PlanetItems.ANORTHOSITE_HOE, 1),
                            4, 12, 0.075f));
                });
    }
}