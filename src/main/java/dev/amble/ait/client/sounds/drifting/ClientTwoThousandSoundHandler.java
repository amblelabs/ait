package dev.amble.ait.client.sounds.drifting;

import java.util.UUID;

import dev.amble.ait.client.sounds.PlayerFollowingSound;
import dev.amble.ait.client.sounds.SoundHandler;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.core.AITSounds;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

// Client only class. One of the last surviving remnants of Duzocode.
// remove all instances of the word "drifting".
public class ClientTwoThousandSoundHandler extends SoundHandler {

    private SoundInstance twoThousand;
    private SoundInstance stage4;
    private UUID current;
    private boolean wasPowered = true;
    private int powerLosses;

    public static ClientTwoThousandSoundHandler create() {
        return new ClientTwoThousandSoundHandler();
    }

    public void tick(MinecraftClient client) {
        ClientTardis tardis = ClientTardisUtil.getCurrentTardis();

        if (client.player == null || tardis == null) {
            this.current = null;
            this.stopAll();
            return;
        }

        boolean powered = tardis.fuel().hasPower();

        if (!tardis.getUuid().equals(this.current)) {
            this.current = tardis.getUuid();
            this.wasPowered = powered;
        }

        boolean siege = tardis.siege().isActive();

        if (powered) {
            this.wasPowered = true;
            this.stopAll();
            return;
        }

        if (siege) {
            this.stop(this.twoThousand);
            this.twoThousand = null;

            if (!this.isPlaying(this.stage4))
                this.stage4 = this.play(AITSounds.STAGE_4);
        } else {
            this.stop(this.stage4);
            this.stage4 = null;

            if (this.wasPowered && this.powerLosses++ % 2 == 0)
                this.twoThousand = this.play(AITSounds.TWO_THOUSAND);
        }

        this.wasPowered = false;

        if (this.isPlaying(this.twoThousand) || this.isPlaying(this.stage4))
            client.getMusicTracker().stop();
    }

    private SoundInstance play(SoundEvent event) {
        SoundInstance sound = new PlayerFollowingSound(event, SoundCategory.MUSIC, 0.15f);
        this.startSound(sound);
        return sound;
    }

    private void stop(SoundInstance sound) {
        if (sound != null)
            this.stopSound(sound);
    }

    private void stopAll() {
        this.stop(this.twoThousand);
        this.stop(this.stage4);
        this.twoThousand = null;
        this.stage4 = null;
    }

    @Override
    public boolean isPlaying(SoundInstance sound) {
        return sound != null && super.isPlaying(sound);
    }
}
