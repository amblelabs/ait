package dev.amble.ait.core.entities.daleks;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.lib.api.Identifiable;

import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import dev.amble.ait.AITMod;

public record Dalek(Identifier id, Identifier texture, Identifier emission) implements Identifiable {
    public static final Codec<Dalek> CODEC = Codecs.exceptionCatching(RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(Dalek::id),
            Identifier.CODEC.fieldOf("texture").forGetter(Dalek::texture),
                    Identifier.CODEC.fieldOf("emission").forGetter(Dalek::emission))
            .apply(instance, Dalek::new)));
    @Override
    public Identifier id() {
        return this.id;
    }
    @Override
    public Identifier texture() {
        return this.texture;
    }
    @Override
    public Identifier emission() {
        return this.emission;
    }

    public static Dalek fromInputStream(InputStream stream) {
        return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
    }

    public static Dalek fromJson(JsonObject json) {
        AtomicReference<Dalek> created = new AtomicReference<>();

        CODEC.decode(JsonOps.INSTANCE, json).get().ifLeft(var -> created.set(var.getFirst())).ifRight(err -> {
            created.set(null);
            AITMod.LOGGER.error("Error decoding datapack dalek: {}", err);
        });

        return created.get();
    }
}
