package dev.amble.ait.core.tardis.animation.v2;

import dev.amble.ait.core.tardis.animation.v2.datapack.TardisAnimationRegistry;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.enummap.EnumMap;
import net.minecraft.util.Identifier;

import java.util.Arrays;

public class TardisAnimationMap extends EnumMap.Compliant<TravelHandlerBase.State, TardisAnimation> {
	public TardisAnimationMap() {
		super(TravelHandlerBase.State::values, TardisAnimationMap::createArray);
	}

	private static TardisAnimation[] createArray(int length) {
		TardisAnimation[] array = new TardisAnimation[length];

		TardisAnimation fallback = TardisAnimationRegistry.getInstance().fallback();
		Arrays.fill(array, fallback.instantiate());

		return array;
	}

	public TardisAnimationMap of(TravelHandlerBase.State state, TardisAnimation sound) {
		this.put(state, sound);
		return this;
	}
	public TardisAnimationMap of(TravelHandlerBase.State state, Identifier id) {
		return this.of(state, TardisAnimationRegistry.getInstance().instantiate(id));
	}
}
