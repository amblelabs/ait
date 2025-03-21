package dev.amble.ait.client.sounds.rain;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;

import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.client.sounds.LoopingSound;
import dev.amble.ait.client.sounds.PositionedLoopingSound;
import dev.amble.ait.client.sounds.SoundHandler;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.TardisManager;
import dev.amble.ait.core.tardis.handler.ExteriorEnvironmentHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;

public class ClientRainSoundHandler extends SoundHandler {

    public static LoopingSound RAIN_SOUND;

    public LoopingSound getRainSound(ClientTardis tardis) {
        if (RAIN_SOUND == null)
            RAIN_SOUND = this.createRainSound(tardis);

        return RAIN_SOUND;
    }

    private LoopingSound createRainSound(ClientTardis tardis) {
        if (tardis == null || tardis.getDesktop().getDoorPos().getPos() == null)
            return null;

        return new PositionedLoopingSound(AITSounds.RAIN, SoundCategory.WEATHER,
                tardis.getDesktop().getDoorPos().getPos(), 0.2f);
    }

    public static ClientRainSoundHandler create() {
        ClientRainSoundHandler handler = new ClientRainSoundHandler();

        handler.generate(TardisManager.client().getCurrent());
        return handler;
    }

    private void generate(ClientTardis tardis) {
        if (RAIN_SOUND == null)
            RAIN_SOUND = createRainSound(tardis);

        this.ofSounds(RAIN_SOUND);
    }

    private boolean shouldPlaySounds(ClientTardis tardis) {
        return tardis != null && tardis.travel().getState() == TravelHandlerBase.State.LANDED
                && tardis.<ExteriorEnvironmentHandler>handler(TardisComponent.Id.ENVIRONMENT).isRaining();
    }

    private boolean isDoorOpen(ClientTardis tardis) {
        return tardis != null && tardis.door().isOpen();
    }

    public void tick(MinecraftClient client) {
        ClientTardis tardis = TardisManager.client().getCurrent();

        if (this.sounds == null)
            this.generate(tardis);

        if (this.shouldPlaySounds(tardis)) {
            LoopingSound rainSound = this.getRainSound(tardis);

            if (rainSound != null) {
                rainSound.setVolume(this.isDoorOpen(tardis) ? 2.5f : 0.4f);
                this.startIfNotPlaying(rainSound);
            }
        } else {
            this.stopSound(RAIN_SOUND);
        }
    }
}
