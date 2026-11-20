package dev.amble.ait.core.tardis.control.impl;

import dev.amble.ait.AITMod;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.item.PhoneItem;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.comms.TardisComms;
import dev.amble.ait.core.tardis.comms.TardisPhones;
import dev.amble.ait.core.tardis.control.Control;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

public class CommsControl extends Control {

    public CommsControl() {
        super(AITMod.id("comms"));
    }

    @Override
    protected Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick,
                               @Nullable Entity source) throws ControlSequencedException {
        super.runServer(tardis, player, world, console, leftClick);

        ItemStack held = player.getMainHandStack();
        boolean holdingPhone = held.getItem() instanceof PhoneItem && PhoneItem.isFor(held, tardis.getUuid());

        if (DependencyChecker.hasVoiceChat() && TardisComms.status(tardis.getUuid()) == TardisComms.Status.INCOMING) {
            if (!holdingPhone)
                TardisPhones.lift(player, tardis.asServer(), world, console, source);

            TardisComms.answer(world.getServer(), player, tardis.getUuid());
            return Result.SUCCESS;
        }

        if (holdingPhone && TardisPhones.hangUp(player, held, leftClick))
            return Result.SUCCESS;

        if (player.isSneaking() && DependencyChecker.hasVoiceChat()) {
            TardisPhones.lift(player, tardis.asServer(), world, console, source);
            return Result.SUCCESS;
        }

        AITMod.openScreen(player, 4, tardis.getUuid(), console);
        return Result.SUCCESS;
    }

    @Override
    public long getDelayLength(Tardis tardis) {
        return 20;
    }

    @Override
    public SoundEvent getFallbackSound() {
        return AITSounds.XYZ;
    }
}
