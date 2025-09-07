package dev.amble.ait.core.tardis.control.impl;

import java.util.Random;

import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.drtheo.scheduler.api.TimeUnit;
import dev.drtheo.scheduler.api.common.Scheduler;
import dev.drtheo.scheduler.api.common.TaskStage;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;

public class EngineOverloadControl extends Control {

    private static final Random RANDOM = new Random();
    private static final String[] SPINNER = {"/", "-", "\\", "|"};

    public EngineOverloadControl() {
        super(AITMod.id("engine_overload"));
    }



    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);



        if (tardis.fuel().getCurrentFuel() < 25000) {
            player.sendMessage(Text.translatable("tardis.message.control.engine_overdrive.insufficient_fuel").formatted(Formatting.RED), true);
            world.playSound(null, player.getBlockPos(), AITSounds.CLOISTER, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return Result.FAILURE;
        }


        if (!TravelHandler.isEngineOverloadArmed(tardis.getUuid())) {
            player.sendMessage(Text.translatable("tardis.message.control.engine_overdrive.primed").formatted(Formatting.RED), true);
            TravelHandler.armEngineOverload(tardis.getUuid(), world);
            return Result.SUCCESS_ALT;
        }
        TravelHandler.disarmEngineOverload(tardis.getUuid());

        boolean isInFlight = tardis.travel().getState() == TravelHandlerBase.State.FLIGHT;

        if (!isInFlight) {
            tardis.travel().finishDemat();
        }

        runDumpingArtronSequence(player, () -> {
            world.playSound(null, player.getBlockPos(), AITSounds.ENGINE_OVERLOAD, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.getServer().execute(() -> {
                tardis.travel().handbrake(false);

                if (!isInFlight)
                    tardis.travel().finishDemat();

                tardis.setFuelCount(0);
                tardis.travel().decreaseFlightTime(999999999);
                tardis.setRefueling(false);

                Scheduler.get().runTaskLater(() -> triggerExplosion(world, console, tardis, 4), TaskStage.END_SERVER_TICK, TimeUnit.SECONDS, 0);
            });
        });

        return Result.SUCCESS;
    }

    private void triggerExplosion(ServerWorld world, BlockPos console, Tardis tardis, int stage) {
        if (stage <= 0) return;

        //DONT BUFF THE DAMAGE, THIS HAPPENS EACH TIME THE CONSOLE EXPLODES SO 4x IT
        tardis.alarm().enable();
        tardis.subsystems().demat().removeDurability(250);
        tardis.subsystems().chameleon().removeDurability(250);
        tardis.subsystems().shields().removeDurability(250);
        tardis.subsystems().lifeSupport().removeDurability(250);
        tardis.subsystems().engine().removeDurability(250);
        tardis.crash().addRepairTicks(999999999);

        spawnParticles(world, console);
        Scheduler.get().runTaskLater(() -> spawnExteriorParticles(tardis), TaskStage.END_SERVER_TICK, TimeUnit.SECONDS, 3);

        int nextDelay = (stage == 4) ? 2 : 3;
        Scheduler.get().runTaskLater(() -> triggerExplosion(world, console, tardis, stage - 1), TaskStage.END_SERVER_TICK, TimeUnit.SECONDS, nextDelay);
    }

    private void runDumpingArtronSequence(ServerPlayerEntity player, Runnable onFinish) {
        for (int i = 0; i < 6; i++) {
            int delay = i + 1;
            Scheduler.get().runTaskLater(() -> {
                String frame = SPINNER[delay % SPINNER.length];

                player.sendMessage(Text.translatable("tardis.message.control.engine_overdrive.dumping_artron").append(" " + frame).formatted(Formatting.GOLD), true);
            }, TaskStage.END_SERVER_TICK, TimeUnit.SECONDS, delay);
        }

        Scheduler.get().runTaskLater(() -> runFlashingFinalMessage(player, onFinish), TaskStage.END_SERVER_TICK, TimeUnit.SECONDS, 3);
    }

    private void runFlashingFinalMessage(ServerPlayerEntity player, Runnable onFinish) {
        for (int i = 0; i < 6; i++) {
            int delay = i + 1;
            Scheduler.get().runTaskLater(() -> {
                Formatting flashColor = (delay % 2 == 0) ? Formatting.RED : Formatting.WHITE;
                player.sendMessage(Text.translatable("tardis.message.control.engine_overdrive.engines_overloaded").formatted(flashColor), true);
            }, TaskStage.END_SERVER_TICK, TimeUnit.SECONDS, delay);
        }

        Scheduler.get().runTaskLater(onFinish, TaskStage.END_SERVER_TICK, TimeUnit.SECONDS, 3);
    }

    private void spawnParticles(ServerWorld world, BlockPos position) {
        for (int i = 0; i < 50; i++) {
            double offsetX = (RANDOM.nextDouble() - 0.5) * 2.0;
            double offsetY = RANDOM.nextDouble() * 1.5;
            double offsetZ = (RANDOM.nextDouble() - 0.5) * 2.0;

            world.spawnParticles(ParticleTypes.SNEEZE, position.getX() + 0.5 + offsetX, position.getY() + 1.5 + offsetY, position.getZ() + 0.5 + offsetZ, 2, 0, 0.05, 0, 0.1);
            world.spawnParticles(ParticleTypes.ASH, position.getX() + 0.5 + offsetX, position.getY() + 1.5 + offsetY, position.getZ() + 0.5 + offsetZ, 2, 0, 0.05, 0, 0.1);
            world.spawnParticles(ParticleTypes.EXPLOSION, position.getX() + 0.5 + offsetX, position.getY() + 1.5 + offsetY, position.getZ() + 0.5 + offsetZ, 2, 0, 0.05, 0, 0.1);
            world.spawnParticles(ParticleTypes.LAVA, position.getX() + 0.5 + offsetX, position.getY() + 1.5 + offsetY, position.getZ() + 0.5 + offsetZ, 2, 0, 0.05, 0, 0.1);
            world.spawnParticles(ParticleTypes.SMALL_FLAME, position.getX() + 0.5 + offsetX, position.getY() + 1.5 + offsetY, position.getZ() + 0.5 + offsetZ, 2, 0, 0.05, 0, 0.1);
            world.spawnParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, position.getX() + 0.5 + offsetX, position.getY() + 1.5 + offsetY, position.getZ() + 0.5 + offsetZ, 2, 0, 0.05, 0, 0.1);
        }
    }

    private void spawnExteriorParticles(Tardis tardis) {
        CachedDirectedGlobalPos exteriorPos = tardis.travel().position();

        if (exteriorPos == null) return;
        ServerWorld exteriorWorld = exteriorPos.getWorld();
        BlockPos exteriorBlockPos = exteriorPos.getPos();

        spawnParticles(exteriorWorld, exteriorBlockPos);
    }

    @Override
    protected SubSystem.IdLike requiredSubSystem() {
        return SubSystem.Id.ENGINE;
    }

    @Override
    public long getDelayLength(Tardis tardis) {
        if (TravelHandler.isEngineOverloadArmed(tardis.getUuid()))
            return 360000;

        return 5;
    }

    @Override
    public SoundEvent getFallbackSound() {
        return AITSounds.BWEEP;
    }
}
