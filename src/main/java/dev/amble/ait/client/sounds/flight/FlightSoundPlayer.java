package dev.amble.ait.client.sounds.flight;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.sounds.ClientSoundManager;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.sounds.flight.FlightSound;
import net.minecraft.client.sound.SoundInstance;

public interface FlightSoundPlayer extends SoundInstance {
	void refresh();
	void tick();
	FlightSound getData();
	/**
	 * How far along the sound is in its loop.
	 * @return A float between 0 and 1, where 0 is the start of the sound and 1 is the end.
	 */
	float getProgress();
	boolean isDirty();
	void setDirty(boolean dirty);
	ClientTardis tardis();

	static float getRandomPitch(ClientTardis tardis) {
		if (tardis == null)
			return 1f;

		int speed = tardis.travel().speed();
		int maxSpeed = tardis.travel().maxSpeed().get();
		float speedPercentage = (float) speed / maxSpeed;

		if (ClientSoundManager.getFlight().hasThrottleAndHandbrakeDown(tardis)) {
			if (speedPercentage <= 0.33) {
				return 0.5f;
			} else if (speedPercentage <= 0.66) {
				return 0.55f;
			} else {
				return 0.6f;
			}
		}

		if (speedPercentage <= 0.33) {
			return AITMod.RANDOM.nextFloat(0.9f, 0.95f);
		} else if (speedPercentage <= 0.66) {
			return AITMod.RANDOM.nextFloat(0.95f, 1.0f);
		} else {
			return AITMod.RANDOM.nextFloat(1.0f, 1.25f);
		}
	}
}
