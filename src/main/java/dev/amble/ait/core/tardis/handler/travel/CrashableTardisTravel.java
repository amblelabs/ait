package dev.amble.ait.core.tardis.handler.travel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.util.ServerLifecycleHooks;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.Explosion;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.TardisEvents;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisDesktop;
import dev.amble.ait.core.tardis.handler.TardisCrashHandler;
import dev.amble.ait.core.tardis.util.TardisUtil;
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

    void forceRemat();

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
        List<Explosion> explosions = new ArrayList<>();

        ServerWorld world = tardis.asServer().getInteriorWorld();
        MinecraftServer server = ServerLifecycleHooks.get();

        boolean fireGriefing = server.getGameRules().getBoolean(AITMod.TARDIS_FIRE_GRIEFING);

        tardis.getDesktop().getConsolePos().forEach(console -> {
            TardisDesktop.playSoundAtConsole(world, console, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 3f, 1f);

            startCrashEffects(tardis, console, power);
        });

        if (tardis.sequence().hasActiveSequence())
            tardis.sequence().setActiveSequence(null, true);

        Random random = AITMod.RANDOM;

        for (ServerPlayerEntity player : TardisUtil.getPlayersInsideInterior(tardis.asServer())) {
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
        tardis.alarm().enabled().set(true);
        this.antigravs().set(false);
        this.speed(0);
        tardis.removeFuel(700 * power);
        this.resetHammerUses();
        this.setCrashing(true);
        this.forceRemat();

        int repairTicks = 1200 * power;
        tardis.crash().setRepairTicks(repairTicks);

        if (repairTicks > TardisCrashHandler.UNSTABLE_TICK_START_THRESHOLD) {
            tardis.crash().setState(TardisCrashHandler.State.TOXIC);
        } else {
            tardis.crash().setState(TardisCrashHandler.State.UNSTABLE);
        }

        TardisEvents.CRASH.invoker().onCrash(tardis);
    }


    default void startCrashEffects(Tardis tardis, BlockPos console, int power) {
        ServerWorld world = tardis.asServer().getInteriorWorld();
        MinecraftServer server = world.getServer();

        if (!server.getGameRules().getBoolean(AITMod.TARDIS_FIRE_GRIEFING)) {
            return;
        }

        world.playSound(null, console, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 3.0f, 1.0f);
        world.playSound(null, console, SoundEvents.ENTITY_WITHER_HURT, SoundCategory.BLOCKS, 3.0f, 1.0f);

    }

    private float calculateFireProbability(double distance, int maxRadius) {
        float normalizedDistance = (float) (distance / (maxRadius * maxRadius));
        return Math.max(0.0f, 1.0f - normalizedDistance);
    }
}