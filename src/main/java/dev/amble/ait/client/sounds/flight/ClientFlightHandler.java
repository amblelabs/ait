package dev.amble.ait.client.sounds.flight;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;

import dev.amble.ait.client.sounds.SoundHandler;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;

public class ClientFlightHandler extends SoundHandler {

    public static double MAX_DISTANCE = 16; // distance from console before the sound stops
    public static FlightSoundPlayer FLIGHT;
    public static FlightSoundPlayer EXTERIOR;

    public FlightSoundPlayer getFlightLoop(ClientTardis tardis) {
        if (FLIGHT == null)
            this.generate(tardis);

        return FLIGHT;
    }

    public FlightSoundPlayer getExteriorLoop(ClientTardis tardis) {
        if (EXTERIOR == null)
            this.generate(tardis);

        return EXTERIOR;
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
        if (EXTERIOR == null)
            EXTERIOR = new ExteriorFlightSound(tardis.stats().getFlightEffects(), SoundCategory.BLOCKS);

        FLIGHT.refresh();
        EXTERIOR.refresh();

        this.ofSounds(FLIGHT, EXTERIOR);
    }

    private void playFlightSound(ClientTardis tardis) {
        this.startIfNotPlaying(this.getFlightLoop(tardis));
        this.startIfNotPlaying(this.getExteriorLoop(tardis));

        FlightSoundPlayer interior = this.getFlightLoop(tardis);
        interior.tick();

        FlightSoundPlayer exterior = this.getExteriorLoop(tardis);
        exterior.tick();

        if (interior.isDirty()) {
            interior.setDirty(false);

            if (interior.getData().id().equals(tardis.stats().getFlightEffects().id())) return;

            this.stopSounds();
            MinecraftClient.getInstance().getSoundManager().stop(FLIGHT);
            FLIGHT = null;
            this.generate(tardis);
        }

        if (exterior.isDirty()) {
            exterior.setDirty(false);

            if (exterior.getData().id().equals(tardis.stats().getFlightEffects().id())) return;

            this.stopSounds();
            MinecraftClient.getInstance().getSoundManager().stop(EXTERIOR);
            EXTERIOR = null;
            this.generate(tardis);
        }
    }

    private boolean shouldPlaySounds(ClientTardis tardis) {
        return tardis != null && tardis.fuel().hasPower()
                && (tardis.travel().inFlight() || hasThrottleAndHandbrakeDown(tardis));
    }

    public boolean hasThrottleAndHandbrakeDown(ClientTardis tardis) {
        return tardis != null && tardis.travel().isLanded() && tardis.travel().speed() > 0 && tardis.travel().handbrake();
    }

    public void tick(MinecraftClient client) {
        ClientTardis tardis = ClientTardisUtil.getCurrentTardis();

        if (tardis == null)
            tardis = ClientTardisUtil.getNearestTardis(ClientFlightHandler.MAX_DISTANCE).orElse(null);

        if (tardis == null) return;

        if (this.sounds == null)
            this.generate(tardis);

        if (this.shouldPlaySounds(tardis)) {
            this.playFlightSound(tardis);
        } else {
            this.stopSounds();
        }
    }
}
