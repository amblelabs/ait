package dev.amble.ait.core.tardis.handler.travel;

import java.util.Optional;
import java.util.Random;

import dev.amble.ait.core.AITSounds;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.drtheo.queue.api.ActionQueue;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisDesktop;
import dev.amble.ait.core.tardis.handler.ServerAlarmHandler;
import dev.amble.ait.core.tardis.handler.TardisCrashHandler;
import dev.amble.ait.data.properties.bool.BoolValue;

public sealed interface CrashableTardisTravel permits TravelHandler {

    Tardis tardis();

    TravelHandlerBase.State getState();

    void resetHammerUses();

    int getHammerUses();

    boolean isCrashing();

    void setCrashing(boolean value);

    int speed();

    void speed(int speed);

    BoolValue antigravs();

    Optional<ActionQueue> forceRemat();

    CachedDirectedGlobalPos position();

    CachedDirectedGlobalPos getProgress();

    void destination(CachedDirectedGlobalPos cached);

    /**
     * Performs a crash for the Tardis. If the Tardis is not in flight state, the
     * crash will not be executed.
     */
    default void crash() {
        if (this.getState() != TravelHandler.State.FLIGHT || this.isCrashing())
            return;

        Tardis tardis = this.tardis();
        int power = this.speed() + this.getHammerUses() + 1;

        if (tardis.sequence().hasActiveSequence())
            tardis.sequence().setActiveSequence(null, true);

        ServerWorld world = tardis.asServer().world();

        tardis.getDesktop().getConsolePos().forEach(console -> {
            startCrashEffects(tardis, console);
        });

        Random random = AITMod.RANDOM;

        for (ServerPlayerEntity player : world.getPlayers()) {
            float xVel = random.nextFloat(-2f, 3f);
            float yVel = random.nextFloat(-1f, 2f);
            float zVel = random.nextFloat(-2f, 3f);

            player.setVelocity(xVel * power, yVel * power, zVel * power);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 40 * power, 1, true, false, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 40 * power, 1, true, false, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 40 * power, 2, true, false, false));

            int damage = (int) Math.round(0.5 * power);
            player.damage(world.getDamageSources().generic(), damage);
        }

        tardis.door().setLocked(true);
        tardis.alarm().enable(ServerAlarmHandler.AlarmType.CRASHING);
        this.antigravs().set(false);
        tardis.removeFuel(700 * power);
        this.resetHammerUses();
        this.setCrashing(true);
        this.speed(0);
        this.forceRemat();

        int repairTicks = 1200 * power;
        tardis.crash().setRepairTicks(repairTicks);

        if (repairTicks > TardisCrashHandler.UNSTABLE_TICK_START_THRESHOLD) {
            tardis.crash().setState(TardisCrashHandler.State.TOXIC);
        } else {
            tardis.crash().setState(TardisCrashHandler.State.UNSTABLE);
        }

        // play new arpalarm music - its stereo so it shouldn't matter where it's played from
        if (random.nextInt(0, 15) == 2){
            tardis.asServer().world().playSound(null, 0, 0, 0, AITSounds.ARPALARM, SoundCategory.MASTER, 100000f, 1f);
        }

        TardisEvents.CRASH.invoker().onCrash(tardis);
    }

    default void startCrashEffects(Tardis tardis, BlockPos console) {
        ServerWorld world = tardis.asServer().world();
        TardisDesktop.playSoundAtConsole(world, console, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 3f, 1f);
    }
}