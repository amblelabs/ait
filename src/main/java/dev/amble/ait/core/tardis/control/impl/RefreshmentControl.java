package dev.amble.ait.core.tardis.control.impl;


import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.drinks.DrinkRegistry;
import dev.amble.ait.core.drinks.DrinkUtil;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class RefreshmentControl extends Control {
    private int currentIndex = 0;

    public RefreshmentControl() {
        super(AITMod.id("refreshment_control"));
    }

    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        currentIndex = (currentIndex + 1) % DrinkRegistry.getInstance().size();
        ItemStack selectedItem = DrinkUtil.setDrink(new ItemStack(AITItems.MUG), DrinkRegistry.getInstance().toList().get(currentIndex));

        tardis.extra().setRefreshmentItem(selectedItem);
        player.sendMessage(Text.literal("Refreshment set to: " + selectedItem.getName().getString() + "!"), true);

        return Result.SUCCESS;
    }

    @Override
    public boolean requiresPower() {
        return true;
    }

    @Override
    public SoundEvent getFallbackSound() {
        return AITSounds.ALARM;
    }
}
