package dev.amble.ait.client.sounds.crash_music;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;

import dev.amble.ait.client.AITModClient;
import dev.amble.ait.core.AITSounds;

public class ClientCrashMusicSoundHandler {
    public static void play() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.getSoundManager().play(PositionedSoundInstance.master(AITSounds.ARPALARM, 1f, AITModClient.CONFIG.crashMusicVolume));
    }
}