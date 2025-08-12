package dev.amble.ait.client.sounds.hum.exterior;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;

import dev.amble.ait.api.ClientWorldEvents;
import dev.amble.ait.client.sounds.SoundHandler;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.core.tardis.handler.ChameleonHandler;

public class ExteriorHumHandler extends SoundHandler {

    public static double MAX_DISTANCE = 8; // distance from exterior before the sound stops
    public static ExteriorHumSound SOUND;

    static {
        ClientWorldEvents.CHANGE_WORLD.register((client, world) -> {
            if (SOUND != null)
                MinecraftClient.getInstance().getSoundManager().stop(SOUND);
            SOUND = null;
        });
    }

    public ExteriorHumSound getSound(ClientTardis tardis) {
        if (SOUND == null)
            this.generate(tardis);

        return SOUND;
    }

    private ExteriorHumSound soundFor(ClientTardis tardis) {
        ExteriorHumSound sfx = new ExteriorHumSound(tardis.hum().get(), SoundCategory.BLOCKS);
        sfx.link(tardis);
        return sfx;
    }

    public static ExteriorHumHandler create() {
        return new ExteriorHumHandler();
    }

    private void generate(ClientTardis tardis) {
        if (SOUND == null)
            SOUND = soundFor(tardis);

        SOUND.refresh();

        this.ofSounds(SOUND);
    }

    private void play(ClientTardis tardis) {
        this.startIfNotPlaying(this.getSound(tardis));

        ExteriorHumSound sound = this.getSound(tardis);

        sound.tick();

        if (sound.isDirty()) {
            sound.setDirty(false);

            if (sound.isLinked()) {
                // update pos
                sound.setPosition(sound.tardis().get().travel().position().getPos());

                // check equal
                if (sound.getData().id().equals(tardis.hum().get().id())) return;
            }

            this.stopSounds();
            MinecraftClient.getInstance().getSoundManager().stop(SOUND);
            SOUND = null;
            this.generate(tardis);
            this.startIfNotPlaying(SOUND);
        }
    }

    private boolean shouldPlaySounds(ClientTardis tardis) {
        return tardis != null && tardis.fuel().hasPower();
    }

    public void tick(MinecraftClient client) {
        ClientTardis tardis = ClientTardisUtil.getNearestTardis(MAX_DISTANCE).orElse(null);

        if (tardis == null || ChameleonHandler.isDisguised(tardis)) {
            this.stopSounds();
            return;
        }

        if (!tardis.travel().isLanded()) {
            this.stopSounds();
            return;
        }

        if (this.sounds == null)
            this.generate(tardis);

        if (this.shouldPlaySounds(tardis)) {
            this.play(tardis);
        } else {
            this.stopSounds();
        }
    }
}
