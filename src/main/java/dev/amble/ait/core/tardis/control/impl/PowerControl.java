package dev.amble.ait.core.tardis.control.impl;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;

public class PowerControl extends Control {

    public PowerControl() {
        super(AITMod.id("power"));
    }

    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);
        tardis.fuel().togglePower();

        if (tardis.fuel().hasPower()) {
            int power = (int) tardis.fuel().getCurrentFuel();
            boolean inRange = power >= 1500 && power <= 2017;
            boolean doorLocked = tardis.door().locked();
            boolean refueling = !tardis.isRefueling();

            if (inRange && doorLocked && refueling) {
                SoundEvent track = world.getRandom().nextBoolean() ? AITSounds.WONDERFUL_TIME_IN_SPACE : AITSounds.GOOD_MAN_MUSIC;
                world.playSound(null, console, track, SoundCategory.BLOCKS, 3.0f, 1.0f);
            }
        }

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
