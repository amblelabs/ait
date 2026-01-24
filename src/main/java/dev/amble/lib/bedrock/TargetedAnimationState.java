package dev.amble.lib.bedrock;

import dev.amble.lib.client.bedrock.BedrockAnimation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.AnimationState;
import net.minecraft.util.math.MathHelper;

/**
 * An animation state that allows targeting a specific progress (0-1) and smoothly
 * transitioning to that target. Supports forward and reverse playback.
 *
 * <p>Usage example:
 * <pre>{@code
 * TargetedAnimationState state = new TargetedAnimationState();
 * state.setAnimationLength(1000); // 1 second animation
 *
 * // To play animation forward to completion:
 * state.setTargetProgress(1.0f);
 *
 * // To play animation in reverse:
 * state.setTargetProgress(0.0f);
 *
 * // Each tick, call:
 * state.tick(deltaTimeMs);
 *
 * // Get the current animation time for rendering:
 * long animTime = state.getAnimationTimeMs();
 * }</pre>
 *
 * Even though this implements animation state, it does not change the built-in
 * animation time directly. Instead, use getAnimationTimeMs() to retrieve the
 * current time based on progress.
 */
public class TargetedAnimationState extends AnimationState {

	/**
	 * The target progress to animate toward (0-1)
	 */
	private float targetProgress = 0f;

	/**
	 * The current progress of the animation (0-1)
	 */
	private float currentProgress = 0f;

	/**
	 * The total length of the animation in milliseconds
	 */
	private long animationLengthMs = 1000L;

	/**
	 * Speed multiplier for transitioning (1.0 = normal speed)
	 */
	private float transitionSpeed = 1.0f;

	/**
	 * Whether the animation is currently transitioning
	 */
	private boolean running = false;

	/**
	 * The last time the animation was updated (in milliseconds)
	 */
	private long lastUpdateTime = 0L;

	public TargetedAnimationState() {
	}

	/**
	 * Creates a TargetedAnimationState with a specified animation length.
	 *
	 * @param animationLengthMs The total animation length in milliseconds
	 */
	public TargetedAnimationState(long animationLengthMs) {
		this.animationLengthMs = animationLengthMs;
	}

	/**
	 * Sets the target progress to 1.0 (fully played).
	 */
	public void playForward() {
		setTargetProgress(1.0f);
	}

	/**
	 * Sets the target progress to 0.0 (reversed to start).
	 */
	public void playReverse() {
		setTargetProgress(0.0f);
	}

	/**
	 * Gets the current target progress.
	 *
	 * @return The target progress (0-1)
	 */
	public float getTargetProgress() {
		return targetProgress;
	}

	/**
	 * Sets the target progress for the animation.
	 * The current progress will smoothly transition toward this value.
	 *
	 * @param target The target progress (0-1), will be clamped
	 */
	public void setTargetProgress(float target) {
		this.targetProgress = MathHelper.clamp(target, 0f, 1f);
		if (!this.running && this.currentProgress != this.targetProgress) {
			this.running = true;
			this.lastUpdateTime = System.currentTimeMillis();
		}
	}

	/**
	 * Gets the current animation progress.
	 *
	 * @return The current progress (0-1)
	 */
	public float getCurrentProgress() {
		return currentProgress;
	}

	/**
	 * Sets the current progress directly without animation.
	 *
	 * @param progress The progress to set (0-1), will be clamped
	 */
	public void setCurrentProgress(float progress) {
		this.currentProgress = MathHelper.clamp(progress, 0f, 1f);
	}

	/**
	 * Gets the total animation length in milliseconds.
	 *
	 * @return The animation length in milliseconds
	 */
	public long getAnimationLength() {
		return animationLengthMs;
	}

	/**
	 * Sets the total animation length in milliseconds.
	 *
	 * @param lengthMs The animation length in milliseconds
	 */
	public void setAnimationLength(long lengthMs) {
		this.animationLengthMs = Math.max(1L, lengthMs);
	}

	@Environment(EnvType.CLIENT)
	public void setAnimationLength(BedrockAnimation animation) {
		setAnimationLength((long) (animation.animationLength * 1000L));
	}

	/**
	 * Gets the transition speed multiplier.
	 *
	 * @return The speed multiplier
	 */
	public float getTransitionSpeed() {
		return transitionSpeed;
	}

	/**
	 * Sets the transition speed multiplier.
	 *
	 * @param speed The speed multiplier (1.0 = normal, 2.0 = double speed, etc.)
	 */
	public void setTransitionSpeed(float speed) {
		this.transitionSpeed = Math.max(0.001f, speed);
	}

	/**
	 * Updates the animation state. Call this every tick or frame.
	 * Uses system time to calculate delta.
	 */
	public void tick() {
		long currentTime = System.currentTimeMillis();
		if (lastUpdateTime == 0L) {
			lastUpdateTime = currentTime;
		}
		long deltaMs = currentTime - lastUpdateTime;
		lastUpdateTime = currentTime;

		tick(deltaMs);
	}

	/**
	 * Updates the animation state with a specific delta time.
	 *
	 * @param deltaMs The time elapsed since last update in milliseconds
	 */
	public void tick(long deltaMs) {
		if (!running || currentProgress == targetProgress) {
			if (currentProgress == targetProgress) {
				running = false;
			}
			return;
		}

		// Calculate how much progress to add based on delta time
		float progressDelta = (deltaMs * transitionSpeed) / (float) animationLengthMs;

		if (targetProgress > currentProgress) {
			// Moving forward
			currentProgress = Math.min(currentProgress + progressDelta, targetProgress);
		} else {
			// Moving backward (reverse)
			currentProgress = Math.max(currentProgress - progressDelta, targetProgress);
		}

		// Check if we've reached the target
		if (currentProgress == targetProgress) {
			running = false;
		}
	}

	/**
	 * Gets the current animation time in milliseconds based on current progress.
	 * Use this value when applying animations.
	 *
	 * @return The animation time in milliseconds
	 */
	public long getAnimationTimeMs() {
		return (long) (currentProgress * animationLengthMs);
	}

	/**
	 * Gets the current animation time in seconds based on current progress.
	 *
	 * @return The animation time in seconds
	 */
	public float getAnimationTimeSecs() {
		return currentProgress * (animationLengthMs / 1000f);
	}

	/**
	 * Returns whether the animation is currently transitioning toward the target.
	 *
	 * @return true if currently animating
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Returns whether the animation has reached its target.
	 *
	 * @return true if current progress equals target progress
	 */
	public boolean isAtTarget() {
		return currentProgress == targetProgress;
	}

	/**
	 * Returns whether the animation is at the start (progress = 0).
	 *
	 * @return true if at the beginning
	 */
	public boolean isAtStart() {
		return currentProgress == 0f;
	}

	/**
	 * Returns whether the animation is at the end (progress = 1).
	 *
	 * @return true if at the end
	 */
	public boolean isAtEnd() {
		return currentProgress == 1f;
	}

	/**
	 * Resets the animation to the start position without animating.
	 */
	public void reset() {
		this.currentProgress = 0f;
		this.targetProgress = 0f;
		this.running = false;
		this.lastUpdateTime = 0L;
	}

	/**
	 * Stops the animation at its current position.
	 */
	public void stop() {
		this.targetProgress = this.currentProgress;
		this.running = false;
	}

	/**
	 * Jumps directly to the target progress without animating.
	 */
	public void jumpToTarget() {
		this.currentProgress = this.targetProgress;
		this.running = false;
	}

	@Override
	public String toString() {
		return "TargetedAnimationState{" +
				"targetProgress=" + targetProgress +
				", currentProgress=" + currentProgress +
				", animationLengthMs=" + animationLengthMs +
				", transitionSpeed=" + transitionSpeed +
				", running=" + running +
				", lastUpdateTime=" + lastUpdateTime +
				'}';
	}
}
