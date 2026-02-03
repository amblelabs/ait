package dev.amble.ait.data.preset;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.lib.api.Identifiable;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.Nameable;

/**
 * A datapackable TARDIS preset that defines default customization options.
 * All fields are optional - if not specified, the Hartnell/Classic defaults will be used.
 */
public record TardisPreset(
        Identifier id,
        String name,
        Optional<Identifier> exterior,
        Optional<Identifier> console,
        Optional<Identifier> desktop,
        Optional<Identifier> hum,
        Optional<Identifier> takeoffSound,
        Optional<Identifier> flightSound,
        Optional<Identifier> landingSound,
        Optional<Identifier> vortex
) implements Identifiable, Nameable {

    public static final Codec<TardisPreset> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(TardisPreset::id),
            Codec.STRING.optionalFieldOf("name", "").forGetter(TardisPreset::name),
            Identifier.CODEC.optionalFieldOf("exterior").forGetter(TardisPreset::exterior),
            Identifier.CODEC.optionalFieldOf("console").forGetter(TardisPreset::console),
            Identifier.CODEC.optionalFieldOf("desktop").forGetter(TardisPreset::desktop),
            Identifier.CODEC.optionalFieldOf("hum").forGetter(TardisPreset::hum),
            Identifier.CODEC.optionalFieldOf("takeoff_sound").forGetter(TardisPreset::takeoffSound),
            Identifier.CODEC.optionalFieldOf("flight_sound").forGetter(TardisPreset::flightSound),
            Identifier.CODEC.optionalFieldOf("landing_sound").forGetter(TardisPreset::landingSound),
            Identifier.CODEC.optionalFieldOf("vortex").forGetter(TardisPreset::vortex)
    ).apply(instance, TardisPreset::new));

    public TardisPreset {
        if (name.isEmpty()) {
            name = id.getPath();
        }
    }

    @Override
    public Identifier id() {
        return this.id;
    }

    @Override
    public String name() {
        return this.name;
    }

    public static TardisPreset fromInputStream(InputStream stream) {
        return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
    }

    public static TardisPreset fromJson(JsonObject json) {
        AtomicReference<TardisPreset> created = new AtomicReference<>();

        CODEC.decode(JsonOps.INSTANCE, json).get().ifLeft(preset -> created.set(preset.getFirst())).ifRight(err -> {
            created.set(null);
            AITMod.LOGGER.error("Error decoding datapack tardis preset: {}", err);
        });

        return created.get();
    }
}
