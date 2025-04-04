package dev.amble.ait.core.tardis.control.impl;

import java.util.Random;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.drtheo.scheduler.api.Scheduler;
import dev.drtheo.scheduler.api.TimeUnit;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
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
    private static final int MIN_FUEL = 25000;
    private static final int OVERLOAD_SPEED = 5000;
    private static final int CIRCUIT_DAMAGE = 1000;
    private static final int CRASH_REPAIR_TIME = 999999;

    public EngineOverloadControl() {
        super(AITMod.id("engine_overload"));
    }

    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        if (tardis.fuel().getCurrentFuel() < MIN_FUEL) {
            sendErrorMessage(player, world);
            return Result.FAILURE;
        }

        if (tardis.travel().getState() != TravelHandlerBase.State.FLIGHT) {
            player.sendMessage(Text.literal("§cERROR: ENGINE OVERLOAD CAN ONLY BE INITIATED WHILE IN FLIGHT."), true);
            return Result.FAILURE;
        }

        runDumpingArtronSequence(player, () -> executeOverloadSequence(tardis, player, world, console));
        return Result.SUCCESS;
    }

    private void sendErrorMessage(ServerPlayerEntity player, ServerWorld world) {
        player.sendMessage(Text.literal("§cERROR, TARDIS REQUIRES AT LEAST 25K ARTRON TO EXECUTE THIS ACTION."), true);
        world.playSound(null, player.getBlockPos(), AITSounds.CLOISTER, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    private void executeOverloadSequence(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console) {
        world.getServer().execute(() -> {
            tardis.travel().handbrake(false);
            tardis.setRefueling(false);
            tardis.setFuelCount(0);
            tardis.travel().speed(OVERLOAD_SPEED);

            world.playSound(null, player.getBlockPos(), AITSounds.ENGINE_OVERLOAD, SoundCategory.BLOCKS, 1.0F, 1.0F);
            Scheduler.get().runTaskLater(() -> triggerExplosion(world, console, tardis, 4), TimeUnit.SECONDS, 0);
        });
    }

    private void triggerExplosion(ServerWorld world, BlockPos console, Tardis tardis, int stage) {
        if (stage <= 0) return;

        tardis.alarm().enable();
        applyCircuitDamage(tardis);
        spawnParticles(world, console);
        Scheduler.get().runTaskLater(() -> spawnExteriorParticles(tardis), TimeUnit.SECONDS, 3);

        int nextDelay = (stage == 4) ? 2 : 3;
        Scheduler.get().runTaskLater(() -> triggerExplosion(world, console, tardis, stage - 1), TimeUnit.SECONDS, nextDelay);
    }

    private void applyCircuitDamage(Tardis tardis) {
        tardis.subsystems().demat().removeDurability(CIRCUIT_DAMAGE);
        tardis.subsystems().chameleon().removeDurability(CIRCUIT_DAMAGE);
        tardis.subsystems().shields().removeDurability(CIRCUIT_DAMAGE);
        tardis.subsystems().lifeSupport().removeDurability(CIRCUIT_DAMAGE);
        tardis.subsystems().engine().removeDurability(CIRCUIT_DAMAGE);
        tardis.crash().addRepairTicks(CRASH_REPAIR_TIME);
    }

    private void runDumpingArtronSequence(ServerPlayerEntity player, Runnable onFinish) {
        for (int i = 0; i < 6; i++) {
            int delay = i + 1;
            Scheduler.get().runTaskLater(() -> {
                String frame = SPINNER[delay % SPINNER.length];
                player.sendMessage(Text.literal("§6DUMPING ARTRON " + frame), true);
            }, TimeUnit.SECONDS, delay);
        }

        Scheduler.get().runTaskLater(() -> runFlashingFinalMessage(player, onFinish), TimeUnit.SECONDS, 3);
    }

    private void runFlashingFinalMessage(ServerPlayerEntity player, Runnable onFinish) {
        for (int i = 0; i < 6; i++) {
            int delay = i + 1;
            Scheduler.get().runTaskLater(() -> {
                String flashColor = (delay % 2 == 0) ? "§c" : "§f";
                player.sendMessage(Text.literal(flashColor + "ARTRON DUMPED, ENGINES OVERLOADED, TRIGGERING EMERGENCY ARTRON RELEASE"), true);
            }, TimeUnit.SECONDS, delay);
        }

        Scheduler.get().runTaskLater(onFinish, TimeUnit.SECONDS, 3);
    }

    private void spawnParticles(ServerWorld world, BlockPos position) {
        for (int i = 0; i < 50; i++) {
            double offsetX = (RANDOM.nextDouble() - 0.5) * 2.0;
            double offsetY = RANDOM.nextDouble() * 1.5;
            double offsetZ = (RANDOM.nextDouble() - 0.5) * 2.0;

            spawnParticle(world, position, ParticleTypes.SNEEZE, offsetX, offsetY, offsetZ);
            spawnParticle(world, position, ParticleTypes.ASH, offsetX, offsetY, offsetZ);
            spawnParticle(world, position, ParticleTypes.EXPLOSION, offsetX, offsetY, offsetZ);
            spawnParticle(world, position, ParticleTypes.LAVA, offsetX, offsetY, offsetZ);
            spawnParticle(world, position, ParticleTypes.SMALL_FLAME, offsetX, offsetY, offsetZ);
            spawnParticle(world, position, ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, offsetX, offsetY, offsetZ);
        }
    }

    private void spawnParticle(ServerWorld world, BlockPos position, net.minecraft.particle.ParticleEffect type, double offsetX, double offsetY, double offsetZ) {
        world.spawnParticles(type, position.getX() + 0.5 + offsetX, position.getY() + 1.5 + offsetY, position.getZ() + 0.5 + offsetZ, 2, 0, 0.05, 0, 0.1);
    }

    private void spawnExteriorParticles(Tardis tardis) {
        CachedDirectedGlobalPos exteriorPos = tardis.travel().position();
        if (exteriorPos == null) return;
        spawnParticles(exteriorPos.getWorld(), exteriorPos.getPos());
    }

    @Override
    protected SubSystem.IdLike requiredSubSystem() {
        return SubSystem.Id.ENGINE;
    }

    @Override
    public long getDelayLength() {
        return 360000;
    }

    @Override
    public SoundEvent getFallbackSound() {
        return AITSounds.BWEEP;
    }
}
