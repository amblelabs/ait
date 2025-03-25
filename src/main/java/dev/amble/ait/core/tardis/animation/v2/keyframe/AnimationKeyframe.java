package dev.amble.ait.core.tardis.animation.v2.keyframe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.ait.api.tardis.Disposable;
import dev.amble.ait.api.tardis.TardisTickable;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;

/**
 * Represents a keyframe in an animation.
 * The keyframe interpolates between two alpha values over a duration of ticks.
 */
public class AnimationKeyframe implements TardisTickable, Disposable {
	public static final Codec<AnimationKeyframe> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(Codecs.NONNEGATIVE_INT.fieldOf("duration").forGetter(AnimationKeyframe::getDuration),
				Codec.FLOAT.fieldOf("alpha").forGetter(AnimationKeyframe::getTargetAlpha)
			).apply(instance, AnimationKeyframe::new)
	);

	protected final int duration;
	protected final float targetAlpha;

	protected int ticks;

	private float startingAlpha;

	/**
	 * @param duration The duration of the keyframe in ticks.
	 * @param alpha The target alpha value of the keyframe.
	 * @param sAlpha The starting alpha value of the keyframe.
	 */
	public AnimationKeyframe(int duration, float alpha, float sAlpha) {
		if (duration < 0 || alpha < 0 || sAlpha < 0 || sAlpha > 1) {
			throw new IllegalArgumentException("Invalid keyframe parameters: " + duration + ", " + alpha + ", " + sAlpha);
		}

		this.duration = duration;
		this.targetAlpha = alpha;
		this.startingAlpha = sAlpha;

		this.ticks = 0;
	}

	public AnimationKeyframe(int duration, float alpha) {
		this(duration, alpha, 0);
	}

	public boolean isDone() {
		return ticks >= duration;
	}

	public void tickCommon(boolean isClient) {
		ticks++;
	}

	public void setStartingAlpha(float sAlpha) {
		this.startingAlpha = sAlpha;
	}

	@Override
	public void dispose() {
		this.ticks = 0;
		this.startingAlpha = 0;
	}

	@Override
	public boolean isAged() {
		return this.isDone();
	}

	@Override
	public void age() {
		this.ticks = this.duration;
	}

	public int ticks() {
		return this.ticks;
	}

	public float getAlpha() {
		return this.calculateAlpha();
	}

	private int getDuration() {
		return this.duration;
	}
	private float getTargetAlpha() {
		return this.targetAlpha;
	}

	/**
	 * Catmull-Rom (CUBIC) interpolation between startingAlpha and targetAlpha based off ticks and duration
	 * @return The interpolated alpha value.
	 */
	protected float calculateAlpha() {
		float progress = (float) this.ticks / this.duration;

		return MathHelper.catmullRom(progress, this.startingAlpha, 0, this.targetAlpha, this.targetAlpha);
	}

	public AnimationKeyframe instantiate() {
		return new AnimationKeyframe(this.duration, this.targetAlpha);
	}
}
