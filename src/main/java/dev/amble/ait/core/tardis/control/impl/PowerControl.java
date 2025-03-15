package dev.amble.ait.core.tardis.control.impl;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.data.schema.console.variant.renaissance.*;

public class PowerControl extends Control {

    public PowerControl() {
        super(AITMod.id("power"));
    }

    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);

        tardis.fuel().togglePower();

        return tardis.fuel().hasPower() ? Result.SUCCESS : Result.SUCCESS_ALT;
    }

    @Override
    public SoundEvent getFallbackSound() {
        return AITSounds.POWER_FLICK;
    }

    @Override
    public boolean requiresPower() {
        return false;
    }

    @Override
    public long getDelayLength() {
        return 200;
    }

    @Override
    public boolean shouldHaveDelay(Tardis tardis) {
        return !tardis.fuel().hasPower() && super.shouldHaveDelay();
    }
}
