package dev.amble.ait.compat.lambdynlights;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NbtPredicate;

// omega hack
public class PlayerUsingItemPredicate extends NbtPredicate {

    public static final PlayerUsingItemPredicate INSTANCE = new PlayerUsingItemPredicate();

    private PlayerUsingItemPredicate() {
        super(null);
    }

    @Override
    public boolean test(ItemStack stack) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        return player != null && player.isUsingItem();
    }
}
