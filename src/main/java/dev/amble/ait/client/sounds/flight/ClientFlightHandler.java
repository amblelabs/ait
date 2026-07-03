package dev.amble.ait.client.sounds.flight;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;

import dev.amble.ait.api.tardis.TardisClientEvents;
import dev.amble.ait.client.sounds.ClientSoundManager;
import dev.amble.ait.client.sounds.SoundHandler;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;

// FIXME: god this is so stupid
//   why does the client have to go through the trouble of finding every tardis in some radius
public class ClientFlightHandler extends SoundHandler {

    public static final double MAX_DISTANCE = 16;

    public static FlightSoundPlayer FLIGHT;
    private boolean needsReinit = false;

    static {
        TardisClientEvents.ENTER_CLIENT_TARDIS.register(tardis -> {
            refresh();
        });
    }

    private static void refresh() {
        if (MinecraftClient.getInstance().world == null)
            return;

        ClientFlightHandler handler = ClientSoundManager.getFlight();
        handler.stopSounds();
        if (FLIGHT != null)
            MinecraftClient.getInstance().getSoundManager().stop(FLIGHT);
        FLIGHT = null;
        handler.needsReinit = true;
    }

    public FlightSoundPlayer getFlightLoop(ClientTardis tardis) {
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

        FlightSoundPlayer interior = this.getFlightLoop(tardis);
        interior.tick();

        if (interior.isDirty()) {
            interior.setDirty(false);

            if (interior.getData().id().equals(tardis.stats().getFlightEffects().id())) return;

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
        return tardis != null && tardis.travel().isLanded() && tardis.travel().speed() > 0 && tardis.travel().handbrake();
    }

    public void tick(MinecraftClient client) {
        ClientTardis tardis = ClientTardisUtil.getCurrentTardis();

        if (this.needsReinit && tardis != null) {
            this.needsReinit = false;
            FLIGHT = null;
            this.generate(tardis);
        }

        if (tardis == null) {
            this.stopSounds();
            return;
        }

        if (this.shouldPlaySounds(tardis)) {
            this.playFlightSound(tardis);
        } else {
            this.stopSounds();
        }
    }
}