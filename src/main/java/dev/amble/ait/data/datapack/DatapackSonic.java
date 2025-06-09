package dev.amble.ait.data.datapack;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.sonic.SonicSchema;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class DatapackSonic extends SonicSchema {

    public static final Codec<SonicSchema> CODEC = RecordCodecBuilder
            .create(instance -> instance
                    .group(Identifier.CODEC.fieldOf("id").forGetter(SonicSchema::id),
                            Models.CODEC.fieldOf("models").forGetter(SonicSchema::models),

                            // TODO move this to an item model display type thing
                            Rendering.CODEC.optionalFieldOf("rendering")
                                    .forGetter(schema -> Optional.of(schema.rendering())),
                            Loyalty.CODEC.optionalFieldOf("loyalty").forGetter(SonicSchema::requirement))
                    .apply(instance, DatapackSonic::new));

    public DatapackSonic(Identifier id, Models models, Optional<Rendering> rendering, Optional<Loyalty> loyalty) {
        super(id, models, rendering.orElse(new Rendering()), loyalty);
    }

    public static SonicSchema fromInputStream(InputStream stream) {
        return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
    }

    public static SonicSchema fromJson(JsonObject json) {
        AtomicReference<SonicSchema> created = new AtomicReference<>();

        CODEC.decode(JsonOps.INSTANCE, json).get().ifLeft(sonic -> created.set(sonic.getFirst())).ifRight(err -> {
            created.set(null);
            AITMod.LOGGER.error("Error decoding datapack sonic: {}", err);
        });

        return created.get();
    }
}
