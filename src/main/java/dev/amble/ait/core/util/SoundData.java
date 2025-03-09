package dev.amble.ait.core.util;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

public record SoundData(SoundEvent sound, SoundCategory category, float volume, float pitch) {
    public SoundData {
        if (volume < 0.0F) {
            throw new IllegalArgumentException("Volume must be positive");
        }
        if (pitch < 0.0F) {
            throw new IllegalArgumentException("Pitch must be positive");
        }
    }

    public void play(ServerWorld world, BlockPos pos) {
        world.playSound(null, pos, sound, category, volume, pitch);
    }
}
