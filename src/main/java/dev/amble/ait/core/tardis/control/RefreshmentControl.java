package dev.amble.ait.core.tardis.control;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.drinks.DrinkRegistry;
import dev.amble.ait.core.drinks.DrinkUtil;
import dev.amble.ait.core.tardis.Tardis;

public class RefreshmentControl extends Control {
    private final List<ItemStack> itemList = new ArrayList<>();
    private int currentIndex = 0;

    public RefreshmentControl() {
        super(AITMod.id("refreshment_control"));
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        currentIndex = (currentIndex + 1) % DrinkRegistry.getInstance().size();
        ItemStack selectedItem = DrinkUtil.setDrink(new ItemStack(AITItems.MUG), DrinkRegistry.getInstance().toList().get(currentIndex));

        tardis.extra().setRefreshmentItem(selectedItem);
        player.sendMessage(Text.literal("Refreshment set to: " + selectedItem.getName().getString() + "!"), true);
        return true;
    }

    @Override
    public boolean requiresPower() {
        return true;
    }

    @Override
    public SoundEvent getSound() {
        return AITSounds.ALARM;
    }
}
