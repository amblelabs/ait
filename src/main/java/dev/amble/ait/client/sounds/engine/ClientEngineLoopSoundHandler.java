package dev.amble.ait.client.sounds.engine;

import dev.amble.ait.client.sounds.SoundHandler;
import dev.amble.ait.client.sounds.console.ClientConsoleAmbientSoundsHandler;
import dev.amble.ait.client.sounds.console.ConsoleAmbienceSound;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;
import net.minecraft.client.MinecraftClient;

public class ClientEngineLoopSoundHandler extends SoundHandler {
    public static EngineLoopSound ENGINE_LOOP;

    public EngineLoopSound getFlightLoop(ClientTardis tardis) {
        if (ENGINE_LOOP == null)
            this.generate(tardis);

        return ENGINE_LOOP;
    }

    private EngineLoopSound createFlightSound(ClientTardis tardis) {
        return new EngineLoopSound();
    }

    public static ClientEngineLoopSoundHandler create() {

        return new ClientEngineLoopSoundHandler();
    }

    private void generate(ClientTardis tardis) {
        if (ENGINE_LOOP == null)
            ENGINE_LOOP = createFlightSound(tardis);


        ENGINE_LOOP.refresh();

        this.ofSounds(ENGINE_LOOP);
    }

    private void play(ClientTardis tardis) {
        this.startIfNotPlaying(this.getFlightLoop(tardis));

        EngineLoopSound sound = this.getFlightLoop(tardis);
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
