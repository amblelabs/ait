package dev.amble.ait.core.tardis.animation.v2.datapack;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.animation.v2.TardisAnimation;
import dev.amble.ait.core.tardis.animation.v2.keyframe.KeyframeTracker;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicReference;

public class DatapackAnimation extends TardisAnimation {
	public static final Codec<TardisAnimation> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(
					Identifier.CODEC.fieldOf("id").forGetter(TardisAnimation::id),
					KeyframeTracker.CODEC.fieldOf("tracker").forGetter(TardisAnimation::tracker)
			).apply(instance, DatapackAnimation::new));

	protected DatapackAnimation(Identifier id, KeyframeTracker tracker) {
		super(id, tracker);
	}

	@Override
	public DatapackAnimation instantiate() {
		return new DatapackAnimation(this.id(), this.tracker.instantiate());
	}

	public static DatapackAnimation fromInputStream(InputStream stream) {
		return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
	}

	public static DatapackAnimation fromJson(JsonObject json) {
		AtomicReference<DatapackAnimation> created = new AtomicReference<>();

		CODEC.decode(JsonOps.INSTANCE, json).get().ifLeft(var -> {
			created.set((DatapackAnimation) var.getFirst());
		}).ifRight(err -> {
			created.set(null);
			AITMod.LOGGER.error("Error decoding datapack animation: {}", err);
		});

		return created.get();
	}
}
