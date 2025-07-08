package dev.amble.lib.register;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.amble.ait.AITMod;
import dev.amble.lib.api.Identifiable;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicReference;

public class JsonDecoder {
	public static <T extends Identifiable, C extends Codec<T>> T fromInputStream(InputStream stream, C codec, @Nullable String id) {
		return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject(), codec, id);
	}

	public static <T extends Identifiable, C extends Codec<T>> T fromJson(JsonObject json, C codec, @Nullable String id) {
		AtomicReference<T> created = new AtomicReference<>();

		codec.decode(JsonOps.INSTANCE, json).get().ifLeft(desktop -> created.set(desktop.getFirst())).ifRight(err -> {
			created.set(null);
			AITMod.LOGGER.error("Error decoding datapack {}: {}", id != null ? id : "object", err);
		});

		return created.get();
	}
}
