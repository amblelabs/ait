package dev.amble.ait.client.sounds.drifting;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.sounds.PlayerFollowingSound;
import dev.amble.ait.client.sounds.SoundHandler;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.core.AITSounds;

// Client only class. One of the last surviving remnants of Duzocode.
// remove all instances of the word "drifting".
public class ClientTwoThousandSoundHandler extends SoundHandler {

    public static SoundInstance TWO_THOUSAND;
    private long counter;

    public SoundInstance getDrifting() {
        if (TWO_THOUSAND == null)
            TWO_THOUSAND = createTwoThousandSound();

        return TWO_THOUSAND;
    }

    private SoundInstance createTwoThousandSound() {
        return new PlayerFollowingSound(AITSounds.TWO_THOUSAND, SoundCategory.MUSIC, 0.15f);
    }

    public static ClientTwoThousandSoundHandler create() {
        ClientTwoThousandSoundHandler handler = new ClientTwoThousandSoundHandler();

        handler.generate();
        return handler;
    }

    private void generate() {
        if (TWO_THOUSAND == null)
            TWO_THOUSAND = createTwoThousandSound();

        this.ofSounds(TWO_THOUSAND);
    }

    private boolean shouldPlaySound(ClientTardis tardis) {
        return tardis != null && !tardis.fuel().hasPower();
    }

    public void tick(MinecraftClient client) {
        this.counter++;
        ClientTardis tardis = ClientTardisUtil.getCurrentTardis();

        // check the ticks every 2 minutes
        if (client.player == null && this.counter % (120 * 20) != 0)
            return;

        if (this.sounds == null)
            this.generate();

        if (this.shouldPlaySound(tardis)) {
            if (AITMod.RANDOM.nextBoolean()) {
                this.startIfNotPlaying(this.getDrifting());
            }
            client.getMusicTracker().stop();
        } else {
            this.counter = 0;
            this.stopSounds();
        }
    }
}
