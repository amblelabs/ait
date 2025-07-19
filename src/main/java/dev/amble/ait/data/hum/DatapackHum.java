package dev.amble.ait.data.hum;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import dev.amble.ait.AITMod;

public class DatapackHum extends Hum {
    public static final Codec<Hum> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Identifier.CODEC.fieldOf("id").forGetter(Hum::id), SoundEvent.CODEC.fieldOf("sound").forGetter(Hum::sound),
                    Codecs.NON_EMPTY_STRING.optionalFieldOf("name").forGetter(Hum::nameOptional))
            .apply(instance, DatapackHum::new));

    protected DatapackHum(Identifier id, SoundEvent sound, Optional<String> name) {
        super(name.orElse(id.getPath()), id, sound);
    }

    public static Hum fromInputStream(InputStream stream) {
        return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
    }

    public static Hum fromJson(JsonObject json) {
        AtomicReference<Hum> created = new AtomicReference<>();

        CODEC.decode(JsonOps.INSTANCE, json).get().ifLeft(hum -> created.set(hum.getFirst())).ifRight(err -> {
            created.set(null);
            AITMod.LOGGER.error("Error decoding datapack hum: {}", err);
        });

        return created.get();
    }
}
