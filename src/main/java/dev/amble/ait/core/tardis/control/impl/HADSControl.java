package dev.amble.ait.core.tardis.control.impl;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;

public class HADSControl extends Control {
    public static final Identifier ID = AITMod.id("alarms");

    // @TODO fix hads but for now it's changed to the alarm toggle
    public HADSControl() {
        super(ID);
    }

    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);

        tardis.alarm().toggle();
        return tardis.alarm().enabled().get() ? Result.SUCCESS_ALT : Result.SUCCESS;
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
