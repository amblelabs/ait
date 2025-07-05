package dev.amble.ait.tardis.animation;

import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.tardis.Tardis;
import dev.amble.ait.tardis.handler.travel.TravelHandlerBase;

@Deprecated(forRemoval = true, since = "1.3.0")
public class ClassicAnimation extends ExteriorAnimation {

    public ClassicAnimation(ExteriorBlockEntity exterior) {
        super(exterior);
    }

    @Override
    public void tick(Tardis tardis) {
        TravelHandlerBase.State state = tardis.travel().getState();

        if (this.timeLeft < 0)
            this.setupAnimation(tardis.travel().getState()); // fixme is a jank fix for the timeLeft going negative on
                                                                // client

        if (state == TravelHandlerBase.State.DEMAT) {
            timeLeft--;
            this.setAlpha(getFadingAlpha());
        } else if (state == TravelHandlerBase.State.MAT) {
            timeLeft++;

            if (timeLeft > 680) {
                this.setAlpha(((float) timeLeft - 680) / (860 - 620));
            } else {
                this.setAlpha(0f);
            }
        } else if (state == TravelHandlerBase.State.LANDED) {
            this.setAlpha(1f);
        }
    }

    public float getFadingAlpha() {
        return (float) (timeLeft) / (maxTime);
    }
}
