package dev.amble.ait.core.likes;

import java.util.*;

import dev.amble.lib.register.datapack.SimpleDatapackRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.ResourceType;

import dev.amble.ait.AITMod;

public class ItemRewardRegistry extends SimpleDatapackRegistry<ItemReward> {
    private static final ItemRewardRegistry INSTANCE = new ItemRewardRegistry();
    private final Random random = new Random();

    public ItemRewardRegistry() {
        super(ItemReward::fromInputStream, ItemReward.CODEC, "opinion_reward", "opinion/reward", true, AITMod.MOD_ID);
    }

    @Override
    protected void defaults() {
        this.register(new ItemReward(AITMod.id("fallback"), new ItemStack(Items.CAKE), 10));
    }

    @Override
    public void onCommonInit() {
        super.onCommonInit();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(this);
    }

    @Override
    public ItemReward fallback() {
        return new ItemReward(AITMod.id("fallback"), new ItemStack(Items.CAKE), 10);
    }

    public static ItemRewardRegistry getInstance() {
        return INSTANCE;
    }

    public ItemStack getRandomReward() {
        List<ItemReward> allRewards = new ArrayList<>(REGISTRY.values());
        if (allRewards.isEmpty()) return ItemStack.EMPTY;

        int totalWeight = allRewards.stream().mapToInt(ItemReward::weight).sum();
        int roll = random.nextInt(totalWeight);

        for (ItemReward reward : allRewards) {
            roll -= reward.weight();
            if (roll < 0) {
                return reward.stack().copy();
            }
        }
        return ItemStack.EMPTY;
    }
}
