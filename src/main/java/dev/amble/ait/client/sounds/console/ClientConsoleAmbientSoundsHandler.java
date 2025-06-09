package dev.amble.ait.client.sounds.console;

import dev.amble.ait.client.sounds.SoundHandler;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;
import net.minecraft.client.MinecraftClient;

public class ClientConsoleAmbientSoundsHandler extends SoundHandler {
    public static ConsoleAmbienceSound AMBIENCE;

    public ConsoleAmbienceSound getFlightLoop(ClientTardis tardis) {
        if (AMBIENCE == null)
            this.generate(tardis);

        return AMBIENCE;
    }

    private ConsoleAmbienceSound createFlightSound(ClientTardis tardis) {
        return new ConsoleAmbienceSound();
    }

    public static ClientConsoleAmbientSoundsHandler create() {

        return new ClientConsoleAmbientSoundsHandler();
    }

    private void generate(ClientTardis tardis) {
        if (AMBIENCE == null)
            AMBIENCE = createFlightSound(tardis);


        AMBIENCE.refresh();

        this.ofSounds(AMBIENCE);
    }

    private void play(ClientTardis tardis) {
        this.startIfNotPlaying(this.getFlightLoop(tardis));

        ConsoleAmbienceSound sound = this.getFlightLoop(tardis);
        sound.tick();
    }

    private boolean shouldPlaySounds(ClientTardis tardis) {
        return tardis != null && tardis.fuel().hasPower();
    }

    public void tick(MinecraftClient client) {
        ClientTardis tardis = ClientTardisUtil.getCurrentTardis();

        if (tardis == null) {
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
