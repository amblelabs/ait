package dev.amble.ait.core.tardis.handler.travel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.util.ServerLifecycleHooks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

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
        tardis.getDesktop().getConsolePos().forEach(console -> {
            TardisDesktop.playSoundAtConsole(world, console, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 3f, 1f);

            Explosion explosion = world.createExplosion(null, null, new ExplosionBehavior() {
                        @Override
                        public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
                            return false;
                        }
                    },
                    console.toCenterPos(), 3f * power, false, World.ExplosionSourceType.MOB);

            explosions.add(explosion);

            if (server.getGameRules().getBoolean(AITMod.TARDIS_FIRE_GRIEFING)) {
                this.fireExplosionEffect(tardis, console);
            }
        });

        if (tardis.sequence().hasActiveSequence())
            tardis.sequence().setActiveSequence(null, true);

        Random random = AITMod.RANDOM;

        for (ServerPlayerEntity player : TardisUtil.getPlayersInsideInterior(tardis.asServer())) {
            float xVel = random.nextFloat(-2f, 3f);
            float yVel = random.nextFloat(-1f, 2f);
            float zVel = random.nextFloat(-2f, 3f);

            player.setVelocity(xVel * power, yVel * power, zVel * power);
            player.addStatusEffect(
                    new StatusEffectInstance(StatusEffects.BLINDNESS, 20 * power, 1, true, false, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 20 * power,
                    (int) Math.round(0.25 * power), true, false, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 20 * power,
                    (int) Math.round(0.25 * power), true, false, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20 * power,
                    (int) Math.round(0.25 * power), true, false, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 20 * power,
                    (int) Math.round(0.75 * power), true, false, false));
            int damage = (int) Math.round(0.5 * power);

            if (!explosions.isEmpty()) {
                player.damage(world.getDamageSources().explosion(explosions.get(0)), damage);
            } else {
                player.damage(world.getDamageSources().generic(), damage);
            }
        }

        tardis.door().setLocked(true);
        tardis.alarm().enabled().set(true);
        this.antigravs().set(false);

        this.speed(0);
        tardis.removeFuel(500 * power);

        this.resetHammerUses();

        this.setCrashing(true);

        // TODO Causes more issues than it solves, believe me - Loqor
        //this.destination(TravelUtil.jukePos(this.getProgress(), 10, 100, power).world(World.OVERWORLD));
        //tardis.travel().forcePosition(this.position().world(World.OVERWORLD)); // im sorry theo

        this.forceRemat();

        int repairTicks = 1000 * power;
        tardis.crash().setRepairTicks(repairTicks);

        if (repairTicks > TardisCrashHandler.UNSTABLE_TICK_START_THRESHOLD) {
            tardis.crash().setState(TardisCrashHandler.State.TOXIC);
        } else {
            tardis.crash().setState(TardisCrashHandler.State.UNSTABLE);
        }

        TardisEvents.CRASH.invoker().onCrash(tardis);
    }

    default void fireExplosionEffect(Tardis tardis, BlockPos consolePos) {
        World world = tardis.asServer().getInteriorWorld();

        // Play explosion sound (for visual & audio effects)
        world.playSound(null, consolePos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 0.5f, 1.0f);

        // Number of fire blocks to place in the area
        int fireRadius = 6;  // Adjust radius for desired fire spread range
        Random random = new Random();

        // Loop through blocks around the console
        for (int x = -fireRadius; x <= fireRadius; x++) {
            for (int y = -fireRadius; y <= fireRadius; y++) {
                for (int z = -fireRadius; z <= fireRadius; z++) {
                    BlockPos targetPos = consolePos.add(x, y, z);

                    // Calculate distance from the console
                    double distance = consolePos.getSquaredDistance(targetPos);

                    // We make the probability based on the distance from the console
                    float probability = calculateFireProbability(distance, fireRadius);

                    // Random chance to place fire based on distance
                    if (random.nextFloat() < probability) {
                        // Only place fire on air or flammable blocks
                        if (world.getBlockState(targetPos).isAir() || world.getBlockState(targetPos).isBurnable()) {
                            world.setBlockState(targetPos, Blocks.FIRE.getDefaultState());  // Place fire
                        }
                    }
                }
            }
        }
    }

    private float calculateFireProbability(double distance, int maxRadius) {
        // Normalize the distance based on the max radius, then apply a higher probability further out
        // Closer to the console (smaller distance), fire is less common
        // Farther from the console (greater distance), fire is more common

        float normalizedDistance = (float) (distance / (maxRadius * maxRadius));  // Normalize to a [0, 1] range
        // Reverse it: closer (smaller distance) should be less likely to have fire
        float probability = Math.max(0.0f, 1.0f - normalizedDistance);  // Higher probability farther out

        return probability;  // Return probability for the fire to appear
    }

}
