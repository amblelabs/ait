package dev.amble.ait.core.sounds.travel;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.lib.api.Identifiable;

import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.Nameable;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;

// @TODO better variable names
public record TravelSound(TravelHandlerBase.State target, Identifier id, Identifier soundId, int timeLeft, int maxTime, int startTime, int length, float frequency,
                          float intensity, String name) implements Identifiable, Nameable {
    public static final Codec<TravelSound> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    TravelHandlerBase.State.CODEC.fieldOf("target").forGetter(TravelSound::target),
                    Identifier.CODEC.fieldOf("id").forGetter(TravelSound::id),
                    Identifier.CODEC.fieldOf("sound").forGetter(TravelSound::soundId),
                    Codec.INT.fieldOf("timeLeft").forGetter(TravelSound::timeLeft),
                    Codec.INT.fieldOf("maxTime").forGetter(TravelSound::maxTime),
                    Codec.INT.fieldOf("startTime").forGetter(TravelSound::startTime),
                    Codec.INT.fieldOf("length").forGetter(TravelSound::length),
                    Codec.FLOAT.fieldOf("frequency").forGetter(TravelSound::frequency),
                    Codec.FLOAT.fieldOf("intensity").forGetter(TravelSound::intensity),
            Codec.STRING.optionalFieldOf("name", "").forGetter(TravelSound::name))
            .apply(instance, TravelSound::new));

    public TravelSound {
        if (name.isEmpty()) {
            name = id.getPath();
        }
    }

    public TravelSound(TravelHandlerBase.State target, Identifier id, Identifier soundId, int timeLeft, int maxTime, int startTime, int length, float frequency, float intensity) {
        this(target, id, soundId, timeLeft, maxTime, startTime, length, frequency, intensity, id.getPath());
    }

    @Override
    public Identifier id() {
        return this.id;
    }

    public SoundEvent sound() {
        SoundEvent sfx = Registries.SOUND_EVENT.get(this.soundId());

        if (sfx == null) {
            AITMod.LOGGER.error("Unknown sound event: {} in travel sfx {}", this.soundId(), this.id());
            sfx = AITSounds.ERROR;
        }

        return sfx;
    }

    public static TravelSound fromInputStream(InputStream stream) {
        return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
    }

    public static TravelSound fromJson(JsonObject json) {
        AtomicReference<TravelSound> created = new AtomicReference<>();

        CODEC.decode(JsonOps.INSTANCE, json).get().ifLeft(var -> created.set(var.getFirst())).ifRight(err -> {
            created.set(null);
            AITMod.LOGGER.error("Error decoding datapack travel sfx: {}", err);
        });

        return created.get();
    }
}
