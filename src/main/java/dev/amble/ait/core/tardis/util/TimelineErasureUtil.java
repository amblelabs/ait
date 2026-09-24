package dev.amble.ait.core.tardis.util;

import java.util.UUID;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITDamageTypes;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import org.joml.Vector3f;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

/** Applies the deliberate timeline-erasure consequence triggered by a rejected player. */
public final class TimelineErasureUtil {
    private static final Vector3f[] PARTICLE_COLORS = {
            new Vector3f(1.0f, 0.15f, 0.15f),
            new Vector3f(1.0f, 0.85f, 0.1f),
            new Vector3f(0.2f, 1.0f, 0.35f),
            new Vector3f(0.15f, 0.6f, 1.0f),
            new Vector3f(0.65f, 0.2f, 1.0f),
            new Vector3f(1.0f, 0.2f, 0.75f)
    };

    private TimelineErasureUtil() {
    }

    public static void erase(Tardis tardis, ServerPlayerEntity player) {
        if (tardis == null || player == null || !player.isAlive())
            return;

        UUID playerId = player.getUuid();
        ServerWorld world = player.getServerWorld();
        tardis.alarm().enable();
        tardis.door().closeDoors();
        player.getInventory().clear();

        try {
            spawnEffects(world, player);
            applyGlobalLoyaltyConsequences(tardis, player);
        } catch (RuntimeException exception) {
            AITMod.LOGGER.error("Failed to apply Timeline Erasure for player {}", playerId, exception);
        }

        DamageSource source = AITDamageTypes.of(world, AITDamageTypes.TIMELINE_ERASURE);
        player.damage(source, Float.MAX_VALUE);
        if (player.isAlive()) {
            player.getDamageTracker().onDamage(source, Float.MAX_VALUE);
            player.setHealth(0);
            player.onDeath(source);
        }
    }

    private static void applyGlobalLoyaltyConsequences(Tardis current, ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        current.loyalty().setRejected(playerId);

        MinecraftServer server = player.getServer();
        ServerTardisManager manager = ServerTardisManager.getInstance();
        if (server == null || manager == null)
            return;

        manager.loadAll(server, other -> {
            if (other != null && !other.isRemoved())
                other.loyalty().setRejected(playerId);
        });
    }

    private static void spawnEffects(ServerWorld world, Entity entity) {
        if (world == null || entity == null)
            return;

        double x = entity.getX();
        double y = entity.getBodyY(0.5);
        double z = entity.getZ();
        world.playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST,
                SoundCategory.PLAYERS, 1.5f, 0.9f);
        world.spawnParticles(ParticleTypes.FIREWORK, x, y, z, 40, 0.45, 0.8, 0.45, 0.12);
        world.spawnParticles(AITMod.CORAL_PARTICLE, x, y, z, 30, 0.6, 0.9, 0.6, 0.08);
        for (Vector3f color : PARTICLE_COLORS) {
            world.spawnParticles(new DustParticleEffect(color, 1.2f), x, y, z, 12,
                    0.5, 0.9, 0.5, 0.16);
        }
    }
}
