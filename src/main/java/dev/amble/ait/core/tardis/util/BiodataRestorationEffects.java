package dev.amble.ait.core.tardis.util;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/** Visual, audio and survival effects owned by Biodata Restoration. */
public final class BiodataRestorationEffects {
    private BiodataRestorationEffects() {
    }

    public static void rejectInteraction(ServerPlayerEntity player, BlockPos pos) {
        if (player == null || pos == null)
            return;

        spawnBurst(player.getServerWorld(), Vec3d.ofCenter(pos).add(0, 0.5, 0));
    }

    public static void restore(ServerPlayerEntity player) {
        if (player == null)
            return;

        player.setHealth(1.0f);
        player.clearStatusEffects();
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
        player.extinguish();

        ServerWorld world = player.getServerWorld();
        spawnBurst(world, player.getPos().add(0, player.getHeight() * 0.5, 0));
        world.playSound(null, player.getBlockPos(), AITSounds.WAYPOINT_ACTIVATE,
                SoundCategory.PLAYERS, 1.0f, 0.75f);
        BiodataRestorationNetwork.showTardisItem(player);
    }

    public static void spawnBurst(ServerWorld world, Vec3d center) {
        if (world == null || center == null)
            return;

        int particles = 48;
        double goldenAngle = Math.PI * (3.0 - Math.sqrt(5.0));
        for (int index = 0; index < particles; index++) {
            double y = 1.0 - (index + 0.5) * 2.0 / particles;
            double horizontal = Math.sqrt(Math.max(0.0, 1.0 - y * y));
            double angle = index * goldenAngle;
            double x = Math.cos(angle) * horizontal;
            double z = Math.sin(angle) * horizontal;
            world.spawnParticles(AITMod.CORAL_PARTICLE, center.x, center.y, center.z,
                    0, x, y, z, 0.65);
        }
    }
}
