package dev.amble.ait.client.sounds.flight;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;

import dev.amble.ait.client.sounds.SoundHandler;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;

public class ClientFlightHandler extends SoundHandler {

    public static double MAX_DISTANCE = 16; // distance from console before the sound stops
    public static InteriorFlightSound FLIGHT;

    public InteriorFlightSound getFlightLoop(ClientTardis tardis) {
        if (FLIGHT == null)
            this.generate(tardis);

        return FLIGHT;
    }

    private InteriorFlightSound createFlightSound(ClientTardis tardis) {
        return new InteriorFlightSound(tardis.stats().getFlightEffects(), SoundCategory.BLOCKS);
    }

    public static ClientFlightHandler create() {

        return new ClientFlightHandler();
    }

    private void generate(ClientTardis tardis) {
        if (FLIGHT == null)
            FLIGHT = createFlightSound(tardis);

        FLIGHT.refresh();

        this.ofSounds(FLIGHT);
    }

    private void playFlightSound(ClientTardis tardis) {
        this.startIfNotPlaying(this.getFlightLoop(tardis));

        InteriorFlightSound sound = this.getFlightLoop(tardis);
        sound.tick();

        if (sound.isDirty()) {
            sound.setDirty(false);

            if (sound.getData().id().equals(tardis.stats().getFlightEffects().id())) return;

            this.stopSounds();
            MinecraftClient.getInstance().getSoundManager().stop(FLIGHT);
            FLIGHT = null;
            this.generate(tardis);
        }
    }

    private boolean shouldPlaySounds(ClientTardis tardis) {
        return tardis != null && tardis.fuel().hasPower()
                && (tardis.travel().inFlight() || hasThrottleAndHandbrakeDown(tardis));
    }

    public boolean hasThrottleAndHandbrakeDown(ClientTardis tardis) {
        return tardis != null && tardis.travel().speed() > 0 && tardis.travel().handbrake();
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
            this.playFlightSound(tardis);
        } else {
            this.stopSounds();
        }
    }
}
