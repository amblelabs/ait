package dev.amble.ait.core.tardis.handler.travel;

import net.minecraft.server.MinecraftServer;

public abstract class AnimatedTravelHandler extends ProgressiveTravelHandler {

    private int animationTicks;

    public AnimatedTravelHandler(Id id) {
        super(id);
    }

    @Override
    public void tick(MinecraftServer server) {
        super.tick(server);

        State state = this.getState();

        if (this.shouldTickAnimation())
            this.tickAnimationProgress(state);
    }

    protected void tickAnimationProgress(State state) {
        if (this.animationTicks++ < tardis.stats().getTravelEffects().get(state).length())
            return;

        this.animationTicks = 0;

        if (this instanceof TravelHandler handler)
            state.finish(handler);
    }

    public int getAnimTicks() {
        return animationTicks;
    }

    public int getMaxAnimTicks() {
        return tardis.stats().getTravelEffects().get(this.getState()).length();
    }

    public abstract boolean shouldTickAnimation();
}
