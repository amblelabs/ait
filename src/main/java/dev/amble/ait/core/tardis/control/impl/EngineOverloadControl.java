package dev.amble.ait.core.tardis.control.impl;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.drtheo.scheduler.api.Scheduler;
import dev.drtheo.scheduler.api.TimeUnit;

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
    private static final int REQUIRED_FUEL = 25000;
    private static final int OVERLOAD_SPEED = 99999;
    private static final int CIRCUIT_DAMAGE = 1000;
    private static final int CRASH_REPAIR_TIME = 999999;
    private static final int STAGE_COUNT = 4;

    private final AtomicBoolean overloadRunning = new AtomicBoolean(false);

    public EngineOverloadControl() {
        super(AITMod.id("engine_overload"));
    }

    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        if (overloadRunning.get()) {
            player.sendMessage(Text.translatable("tardis.message.overload_in_progress").formatted(Formatting.RED), true);
            return Result.FAILURE;
        }

        if (tardis.travel().getState() != TravelHandlerBase.State.FLIGHT) {
            player.sendMessage(Text.translatable("tardis.message.overload.invalid_state").formatted(Formatting.RED), true);
            return Result.FAILURE;
        }

        if (tardis.fuel().getCurrentFuel() <= REQUIRED_FUEL) {
            player.sendMessage(Text.translatable("tardis.message.overload.not_enough_fuel").formatted(Formatting.RED), true);
            world.playSound(null, player.getBlockPos(), AITSounds.CLOISTER, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return Result.FAILURE;
        }

        overloadRunning.set(true);
        runDumpingArtronSequence(player, () -> executeOverloadSequence(tardis, player, world, console));
        return Result.SUCCESS;
    }

    private void executeOverloadSequence(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console) {
        world.getServer().execute(() -> {
            tardis.travel().handbrake(false);
            tardis.setRefueling(false);
            tardis.setFuelCount(0);
            tardis.travel().decreaseFlightTime(OVERLOAD_SPEED);

            world.playSound(null, player.getBlockPos(), AITSounds.ENGINE_OVERLOAD, SoundCategory.BLOCKS, 1.0F, 1.0F);

            triggerExplosion(world, console, tardis, STAGE_COUNT);
        });
    }

    private void triggerExplosion(ServerWorld world, BlockPos console, Tardis tardis, int stage) {
        if (stage <= 0) {
            overloadRunning.set(false);
            return;
        }

        tardis.alarm().enable();
        applyCircuitDamage(tardis);
        spawnParticles(world, console);
        spawnExteriorParticles(tardis);

        int nextDelay = (stage == STAGE_COUNT) ? 2 : 3;
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
            int delay = i;
            Scheduler.get().runTaskLater(() -> {
                String frame = SPINNER[delay % SPINNER.length];
                player.sendMessage(Text.translatable("tardis.message.overload.dumping", frame).formatted(Formatting.GOLD), true);
            }, TimeUnit.SECONDS, delay + 1);
        }

        Scheduler.get().runTaskLater(() -> runFlashingFinalMessage(player, onFinish), TimeUnit.SECONDS, 3);
    }

    private void runFlashingFinalMessage(ServerPlayerEntity player, Runnable onFinish) {
        for (int i = 0; i < 6; i++) {
            int delay = i;
            Scheduler.get().runTaskLater(() -> {
                Formatting color = (delay % 2 == 0) ? Formatting.RED : Formatting.WHITE;
                player.sendMessage(Text.translatable("tardis.message.overload.triggering").formatted(color), true);
            }, TimeUnit.SECONDS, delay + 1);
        }

        Scheduler.get().runTaskLater(onFinish, TimeUnit.SECONDS, 3);
    }

    private void spawnParticles(ServerWorld world, BlockPos position) {
        for (int i = 0; i < 50; i++) {
            double dx = (RANDOM.nextDouble() - 0.5) * 2.0;
            double dy = RANDOM.nextDouble() * 1.5;
            double dz = (RANDOM.nextDouble() - 0.5) * 2.0;

            spawnParticle(world, position, ParticleTypes.SNEEZE, dx, dy, dz);
            spawnParticle(world, position, ParticleTypes.ASH, dx, dy, dz);
            spawnParticle(world, position, ParticleTypes.EXPLOSION, dx, dy, dz);
            spawnParticle(world, position, ParticleTypes.LAVA, dx, dy, dz);
            spawnParticle(world, position, ParticleTypes.SMALL_FLAME, dx, dy, dz);
            spawnParticle(world, position, ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, dx, dy, dz);
        }
    }

    private void spawnParticle(ServerWorld world, BlockPos pos, net.minecraft.particle.ParticleEffect type, double dx, double dy, double dz) {
        world.spawnParticles(type, pos.getX() + 0.5 + dx, pos.getY() + 1.5 + dy, pos.getZ() + 0.5 + dz, 1, 0, 0, 0, 0.1);
    }

    private void spawnExteriorParticles(Tardis tardis) {
        CachedDirectedGlobalPos exterior = tardis.travel().position();
        if (exterior != null) {
            spawnParticles(exterior.getWorld(), exterior.getPos());
        }
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
