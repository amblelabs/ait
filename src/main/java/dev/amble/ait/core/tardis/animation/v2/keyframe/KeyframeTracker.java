package dev.amble.ait.core.tardis.animation.v2.keyframe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.Disposable;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.AITSounds;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.security.Key;
import java.util.*;

/**
 * A collection of keyframes that can be tracked.
 */
public class KeyframeTracker extends ArrayList<AnimationKeyframe> implements TardisTickable, Disposable {
	public static final Codec<KeyframeTracker> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(Identifier.CODEC.fieldOf("sound").forGetter(KeyframeTracker::soundId),
					AnimationKeyframe.CODEC.listOf().fieldOf("keyframes").forGetter(KeyframeTracker::getFrames)
			).apply(instance, KeyframeTracker::new));

	protected final Identifier soundId;
	protected int current; // The current keyframe we are on.
	private int duration;

	/**
	 * A collection of keyframes that can be tracked.
	 * @param sound The (optional) sound to play when this begins.
	 * @param frames The keyframes to track
	 */
	public KeyframeTracker(@Nullable Identifier sound, Collection<AnimationKeyframe> frames) {
		super();

		this.soundId = sound;
		this.current = 0;

		this.addAll(frames);
		this.duration = -1;
	}

	/**
	 * Get the current keyframe.
	 * @return The current keyframe.
	 */
	public AnimationKeyframe getCurrent() {
		if (this.size() == 0) {
			throw new NoSuchElementException("Keyframe Tracker " + this + " is missing keyframes!");
		}

		return this.get(this.current);
	}

	public float getAlpha() {
		return this.getCurrent().getAlpha();
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
		AnimationKeyframe current = this.get(this.current);

		current.tickCommon(isClient);

		if (current.isDone() && !this.isDone()) {
			this.current++; // current is now previous

			this.getCurrent().setStartingAlpha(current.getAlpha());

			current.dispose();
		}
	}

	public SoundEvent getSound() {
		SoundEvent sfx = Registries.SOUND_EVENT.get(this.soundId);

		if (sfx == null) {
			AITMod.LOGGER.error("Unknown sound event: {} in keyframe tracker {}", this.soundId, this);
			sfx = AITSounds.ERROR;
		}

		return sfx;
	}

	// required for datapacks
	private Identifier soundId() {
		return this.soundId;
	}

	// required for datapacks
	private List<AnimationKeyframe> getFrames() {
		return this;
	}

	public void start(@Nullable CachedDirectedGlobalPos source, float alpha) {
		this.dispose();

		this.getCurrent().setStartingAlpha(alpha);

		if (source == null || source.getWorld() == null) return;

		// Play sound at source
		source.getWorld().playSound(null, source.getPos(), this.getSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
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

		for (AnimationKeyframe keyframe : this) {
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

	public KeyframeTracker instantiate() {
		Collection<AnimationKeyframe> frames = new ArrayList<>();

		for (AnimationKeyframe keyframe : this) {
			frames.add(keyframe.instantiate());
		}

		return new KeyframeTracker(this.soundId, frames);
	}
}
