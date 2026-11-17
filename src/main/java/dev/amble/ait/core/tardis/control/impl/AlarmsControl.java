package dev.amble.ait.core.tardis.control.impl;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class AlarmsControl extends Control {
    public static final Identifier ID = AITMod.id("alarms");

    public AlarmsControl() {
        super(ID);
    }

    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);

        if (tardis.interiorChangingHandler().queued().get()) {
            if (tardis.stats().security().get() && !SecurityControl.hasMatchingKey(player, tardis))
                return Result.FAILURE;

            if (tardis.interiorChangingHandler().cancelQueue())
                return Result.SUCCESS;
        }

        tardis.alarm().toggle();
        return tardis.alarm().isEnabled() ? Result.SUCCESS_ALT : Result.SUCCESS;
    }

    @Override
    public boolean requiresPower() {
        return false; // todo remember to change this back when this becomes a HADS control again!!
    }

    @Override
    public SoundEvent getFallbackSound() {
        return AITSounds.ALARM;
    }
}
