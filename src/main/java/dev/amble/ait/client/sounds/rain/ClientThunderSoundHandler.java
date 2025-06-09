package dev.amble.ait.client.sounds.rain;

import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.client.sounds.LoopingSound;
import dev.amble.ait.client.sounds.PositionedLoopingSound;
import dev.amble.ait.client.sounds.SoundHandler;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.handler.ExteriorEnvironmentHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;

public class ClientThunderSoundHandler extends SoundHandler {

    public static LoopingSound THUNDER_SOUND;

    public LoopingSound getThunderSound(ClientTardis tardis) {
        if (THUNDER_SOUND == null)
            THUNDER_SOUND = this.createThunderSound(tardis);

        return THUNDER_SOUND;
    }

    private LoopingSound createThunderSound(ClientTardis tardis) {
        if (tardis == null || tardis.getDesktop().getDoorPos().getPos() == null)
            return null;

        return new PositionedLoopingSound(AITSounds.THUNDER, SoundCategory.WEATHER,
                tardis.getDesktop().getDoorPos().getPos(), 0.3f);
    }

    public static ClientThunderSoundHandler create() {
        ClientThunderSoundHandler handler = new ClientThunderSoundHandler();

        handler.generate(ClientTardisUtil.getCurrentTardis());
        return handler;
    }

    private void generate(ClientTardis tardis) {
        if (THUNDER_SOUND == null)
            THUNDER_SOUND = createThunderSound(tardis);

        this.ofSounds(THUNDER_SOUND);
    }

    private boolean shouldPlaySounds(ClientTardis tardis) {
        return tardis != null && tardis.travel().getState() == TravelHandlerBase.State.LANDED
                && tardis.<ExteriorEnvironmentHandler>handler(TardisComponent.Id.ENVIRONMENT).isThundering();
    }

    private boolean isDoorOpen(ClientTardis tardis) {
        return tardis != null && tardis.door().isOpen();
    }

    public void tick(MinecraftClient client) {
        ClientTardis tardis = ClientTardisUtil.getCurrentTardis();

        if (this.sounds == null)
            this.generate(tardis);

        if (this.shouldPlaySounds(tardis)) {
            LoopingSound thunderSound = this.getThunderSound(tardis);

            if (thunderSound != null) {
                thunderSound.setVolume(this.isDoorOpen(tardis) ? 2.5f : 0.4f);
                this.startIfNotPlaying(thunderSound);
            }
        } else {
            this.stopSound(THUNDER_SOUND);
        }
    }
}
