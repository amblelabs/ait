package dev.amble.ait.core.tardis.animation;

import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.sounds.travel.TravelSound;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;

public class PulsatingAnimation extends ExteriorAnimation {

    private static final int PULSE_LENGTH = 20;

    private int pulses = 0;
    private float frequency, intensity;

    public PulsatingAnimation(ExteriorBlockEntity exterior) {
        super(exterior);
    }

    @Override
    public void tick(Tardis tardis) {
        TravelHandler travel = tardis.travel();
        TravelHandlerBase.State state = travel.getState();

        if (this.timeLeft < 0)
            this.setupAnimation(travel.getState()); // fixme is a jank fix for the timeLeft going negative on
        // client

        boolean hasStarted = timeLeft < startTime;

        if (state == TravelHandlerBase.State.DEMAT) {
            this.setAlpha(hasStarted ? 1f - this.getPulseAlpha() : 1f);
        }

        if (state == TravelHandlerBase.State.MAT) {
            this.setAlpha(hasStarted ? this.getPulseAlpha() : 0f);
        }

        this.timeLeft--;
    }

    @Override
    public boolean reset() {
        if (!super.reset())
            return false;

        this.pulses = 0;
        return true;
    }

    public float getPulseAlpha() {
        if (timeLeft != maxTime && timeLeft % PULSE_LENGTH == 0)
            pulses++;

        return (float) ((float) (pulses / Math.floor((double) maxTime / PULSE_LENGTH))
                + (Math.cos(timeLeft * frequency) * intensity));
    }

    @Override
    public boolean setupAnimation(TravelHandlerBase.State state) {
        if (!super.setupAnimation(state))
            return false;

        TravelSound sound = exterior.tardis().get().stats().getTravelEffects().get(state);

        this.frequency = sound.frequency();
        this.intensity = sound.intensity();

        this.pulses = 0;
        return true;
    }
}
