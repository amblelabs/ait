package dev.amble.ait.compat.lambdynlights;

import dev.amble.ait.core.AITItems;
import dev.lambdaurora.lambdynlights.api.DynamicLightsContext;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSource;
import dev.lambdaurora.lambdynlights.api.predicate.ItemPredicate;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.registry.entry.RegistryEntryList;

import java.util.Optional;

public class LambDynLightsCompat implements DynamicLightsInitializer {

    @Override
    public void onInitializeDynamicLights(DynamicLightsContext context) {
        context.itemLightSourceManager().onRegisterEvent().register(ctx ->
                ctx.register(new ItemLightSource(new ItemPredicate(getItems(AITItems.SONIC_SCREWDRIVER),
                        NumberRange.IntRange.ANY, NumberRange.IntRange.ANY,
                        new EnchantmentPredicate[0], new EnchantmentPredicate[0],
                        Optional.empty(), PlayerUsingItemPredicate.INSTANCE
                ), 10))
        );
    }

    private Optional<RegistryEntryList<Item>> getItems(ItemConvertible items) {
        return Optional.of(RegistryEntryList.of((itemLike) -> itemLike.asItem().getRegistryEntry(), items));
    }

    @Override
    @SuppressWarnings("removal")
    public void onInitializeDynamicLights() { }
}
