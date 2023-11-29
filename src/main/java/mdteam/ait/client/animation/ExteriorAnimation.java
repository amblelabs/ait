package mdteam.ait.client.animation;

import mdteam.ait.core.blockentities.ExteriorBlockEntity;
import org.joml.Math;
import the.mdteam.ait.TardisTravel;

public abstract class ExteriorAnimation {

    protected float alpha;
    protected ExteriorBlockEntity exterior;
    protected int timeLeft;
    protected int maxTime;
    protected float alphaChangeAmount = 0.005f;

    public ExteriorAnimation(ExteriorBlockEntity exterior) {
        this.exterior = exterior;
    }

    protected void runAlphaChecks(TardisTravel.State state) {
        if (this.exterior.getWorld().isClient)
            return;

        if (alpha <= 0f && state == TardisTravel.State.DEMAT) {
            exterior.getTardis().getTravel().toFlight();
        }
        if (alpha >= 1f && state == TardisTravel.State.MAT) {
            exterior.getTardis().getTravel().setState(TardisTravel.State.LANDED);
            exterior.getTardis().getTravel().runAnimations(exterior);
        }
    }

    public float getAlpha() {
        return Math.clamp(0.0F,1.0F,this.alpha);
    }

    public abstract void tick();

    public abstract void setupAnimation(TardisTravel.State state);

    public void setAlpha(float alpha) {
        this.alpha = Math.clamp(0.0F,1.0F, alpha);
    }
}