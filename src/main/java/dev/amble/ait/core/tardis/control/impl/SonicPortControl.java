package dev.amble.ait.core.tardis.control.impl;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.link.LinkableItem;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.item.HandlesItem;
import dev.amble.ait.core.item.SonicItem;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.control.sequences.SequenceHandler;
import dev.amble.ait.core.tardis.handler.ButlerHandler;
import dev.amble.ait.core.tardis.handler.SonicHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class SonicPortControl extends Control {

    public SonicPortControl() {
        super(AITMod.id("sonic_port"));
    }

    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);

        SonicHandler handler = tardis.sonic();
        ButlerHandler butler = tardis.butler();

        if ((leftClick || player.isSneaking()) && (handler.getConsoleSonic() != null || butler.getHandles() != null)) {
            ItemStack item;

            if (handler.getConsoleSonic() != null) {
                item = handler.takeConsoleSonic();
            } else {
                item = butler.takeHandles();
            }

            player.getInventory().offerOrDrop(item);
            return Result.SUCCESS;
        }

        ItemStack stack = player.getMainHandStack();

        if (!((stack.getItem() instanceof SonicItem) || (stack.getItem() instanceof HandlesItem)))
            return Result.FAILURE;

        LinkableItem linker = (LinkableItem) stack.getItem();

        if (!linker.isLinked(stack) || player.isSneaking()) {
            linker.link(stack, tardis);
            world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.BLOCKS,
                    1.0F, 1.0F);

            SequenceHandler.spawnControlParticles(world, Vec3d.ofBottomCenter(console).add(0.0, 1.2f, 0.0));
        }

        if (handler.getConsoleSonic() == null && stack.getItem() instanceof HandlesItem) {
            butler.insertHandles(stack, console);
            player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        } else if (butler.getHandles() == null && stack.getItem() instanceof SonicItem) {
            handler.insertConsoleSonic(stack, console);
            player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        }

        boolean hasSonic = handler.getConsoleSonic() != null || butler.getHandles() != null;

        return hasSonic ? Result.SUCCESS : Result.SUCCESS_ALT;
    }

    @Override
    public SoundEvent getFallbackSound() {
        return AITSounds.SONIC_PORT;
    }

    @Override
    public boolean requiresPower() {
        return false;
    }
}
