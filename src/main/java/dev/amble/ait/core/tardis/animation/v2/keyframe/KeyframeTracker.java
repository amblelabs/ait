package dev.amble.ait.core.tardis.animation.v2.keyframe;

import java.util.*;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.Disposable;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.AITSounds;

/**
 * A collection of keyframes that can be tracked.
 */
public class KeyframeTracker<T> extends ArrayList<AnimationKeyframe<T>> implements TardisTickable, Disposable {
    protected int current; // The current keyframe we are on.
    private int duration;

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

    public T getValue() {
        return this.getCurrent().getValue();
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

            this.getCurrent().setStart(current.getValue());

            current.dispose();
        }
    }

    public void start(T val) {
        this.dispose();
        this.getCurrent().setStart(val);
    }

    public boolean isDone() {
        return this.getCurrent().isDone() && this.current == (this.size() - 1);
    }

    public int duration() {
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
