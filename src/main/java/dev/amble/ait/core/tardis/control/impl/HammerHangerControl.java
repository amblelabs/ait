package dev.amble.ait.core.tardis.control.impl;

import dev.amble.ait.core.AITSounds;
import dev.amble.ait.data.schema.console.ConsoleTypeSchema;
import dev.amble.lib.item.AItemSettings;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.item.HammerItem;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisDesktop;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.handler.ExtraHandler;

public class HammerHangerControl extends Control {

    public HammerHangerControl() {
        super(AITMod.id("hammer_hanger"));
    }

    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console,
                             boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);

        ExtraHandler handler = tardis.extra();

        if ((leftClick || player.isSneaking()) && (handler.getConsoleHammer() != null)) {
            ItemStack item;

            item = handler.takeConsoleHammer();

            player.getInventory().offerOrDrop(item);
            return Result.SUCCESS_ALT;
        }

        ItemStack stack = player.getMainHandStack();

        if (stack.getItem() instanceof HammerItem) {
            handler.insertConsoleHammer(stack, console);
            player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        }

        return Result.SUCCESS;
    }

    @Override
    public SoundEvent getFallbackSound() {
        return SoundEvents.BLOCK_CHAIN_HIT;
    }
}
