package dev.amble.ait.client.sounds.crash_music;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;

import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.sounds.LoopingSound;
import dev.amble.ait.client.sounds.PlayerFollowingLoopingSound;
import dev.amble.ait.client.sounds.SoundHandler;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.core.AITSounds;

public class ClientCrashMusicSoundHandler extends SoundHandler {

    public static LoopingSound CRASH_MUSIC;

    public LoopingSound getCrashMusic() {
        if (CRASH_MUSIC == null)
            CRASH_MUSIC = createCrashMusic();

        return CRASH_MUSIC;
    }

    private LoopingSound createCrashMusic() {
        return new PlayerFollowingLoopingSound(AITSounds.ARPALARM, SoundCategory.AMBIENT,
                AITModClient.CONFIG.crashMusicVolume);
    }

    public static ClientCrashMusicSoundHandler create() {
        ClientCrashMusicSoundHandler handler = new ClientCrashMusicSoundHandler();

        handler.generate();
        return handler;
    }

    private void generate() {
        if (CRASH_MUSIC == null)
            CRASH_MUSIC = createCrashMusic();

        this.ofSounds(CRASH_MUSIC);
    }

    private boolean shouldPlaySound(ClientTardis tardis) {
        return tardis != null && !tardis.crash().isNormal();
    }

    public void tick(MinecraftClient client) {
        ClientTardis tardis = ClientTardisUtil.getCurrentTardis();

        if (this.sounds == null)
            this.generate();

        if (this.shouldPlaySound(tardis)) {
            this.getCrashMusic().setVolume(AITModClient.CONFIG.crashMusicVolume);
            this.startIfNotPlaying(this.getCrashMusic());
        } else {
            this.stopSounds();
        }
    }
}
