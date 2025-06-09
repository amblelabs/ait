package dev.amble.ait.core.handles;

import dev.amble.ait.core.tardis.ServerTardis;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

/**
 * For Handles the robot, this is used to play sounds.
 */
public interface HandlesSound {
    void playSound(SoundEvent sound, SoundCategory category, float volume, float pitch);

    static HandlesSound of(ServerPlayerEntity player) {
        return (sound, category, volume, pitch) -> player.getServerWorld().playSound(null, player.getBlockPos(), sound, category, volume, pitch);
    }
    static HandlesSound of(ServerTardis tardis) {
        return (sound, category, volume, pitch) -> tardis.getDesktop().playSoundAtEveryConsole(sound, category, volume, pitch);
    }
}
