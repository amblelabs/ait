package dev.amble.ait.core.tardis.animation.v2.keyframe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;

import dev.amble.ait.api.tardis.Disposable;
import dev.amble.ait.api.tardis.TardisTickable;

/**
 * A collection of keyframes that can be tracked.
 */
public class KeyframeTracker<T> extends ArrayList<AnimationKeyframe<T>> implements TardisTickable, Disposable {
    protected int current; // The current keyframe we are on.
    private float duration;

    /**
     * A collection of keyframes that can be tracked.
     * @param frames The keyframes to track
     */
    public KeyframeTracker(Collection<AnimationKeyframe<T>> frames) {
        super();

        this.current = 0;

        this.addAll(frames);
        this.duration = -1;
    }

    /**
     * Get the current keyframe.
     * @return The current keyframe.
     */
    public AnimationKeyframe<T> getCurrent() {
        if (this.size() == 0) {
            throw new NoSuchElementException("Keyframe Tracker " + this + " is missing keyframes!");
        }

        return this.get(this.current);
    }

    public boolean isStarting() {
        return this.current == 0;
    }

    public T getValue(float delta) {
        return this.getCurrent().getValue(delta);
    }

    @Override
    public void tick(MinecraftServer server) {
        this.tickCommon(false);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void tick(MinecraftClient client) {
        this.tickCommon(true);
    }

    protected void tickCommon(boolean isClient) {
        AnimationKeyframe<T> current = this.get(this.current);

        current.tickCommon(isClient);

        if (current.isDone() && !this.isDone()) {
            this.current++; // current is now previous

            AnimationKeyframe<T> previous = current;
            current = this.getCurrent();
            AnimationKeyframe<T> next = null;

            if (this.current + 1 < this.size()) {
                next = this.get(this.current + 1);
            }

            current.setStart(previous.getValue(0F));
            current.setPrevious(previous);
            current.setNext(next);

            previous.dispose();
        }
    }

    public void start(T val) {
        this.dispose();
        // this.getCurrent().setStart(val);
    }

    public boolean isDone() {
        return this.getCurrent().isDone() && this.current == (this.size() - 1);
    }

    public float duration() {
        if (this.duration == -1) {
            return this.calculateDuration();
        }

        return this.duration;
    }
    private int calculateDuration() {
        int total = 0;

        for (AnimationKeyframe<T> keyframe : this) {
            total += keyframe.duration;
        }

        this.duration = total;
        return total;
    }

    @Override
    public boolean isAged() {
        return this.isDone();
    }

    @Override
    public void age() {
        this.current = this.size() - 1;
        this.getCurrent().age();
    }

    @Override
    public void dispose() {
        // dispose all keyframes
        this.forEach(AnimationKeyframe::dispose);
        this.current = 0;
    }

    public KeyframeTracker<T> instantiate() {
        Collection<AnimationKeyframe<T>> frames = new ArrayList<>();

        for (AnimationKeyframe<T> keyframe : this) {
            frames.add(keyframe.instantiate());
        }

        return new KeyframeTracker<>(frames);
    }
}
