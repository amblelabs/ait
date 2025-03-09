package dev.amble.ait.client.animation.controls;

import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.entity.AnimationState;

public class ControlAnimationState {
    private final Animation animation;
    private final AnimationState state;

    public ControlAnimationState(Animation animation) {
        this.animation = animation;
        this.state = new AnimationState();
    }

    public Animation getAnimation() {
        return animation;
    }

    public AnimationState getAnimationState() {
        return state;
    }

    public boolean isRunning() {
        return state.isRunning();
    }

    public void start(int age) {
        state.start(age);
    }

    public void startIfNotRunning(int age) {
        state.startIfNotRunning(age);
    }

    public void stop() {
        state.stop();
    }

    // Add any other methods you need
}
