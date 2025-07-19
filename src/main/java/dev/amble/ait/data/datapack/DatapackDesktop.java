package dev.amble.ait.data.datapack;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.desktop.TardisDesktopSchema;
import dev.amble.ait.data.schema.desktop.textures.DesktopPreviewTexture;

public class DatapackDesktop extends TardisDesktopSchema {
    public static final Codec<TardisDesktopSchema> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Identifier.CODEC.fieldOf("id").forGetter(TardisDesktopSchema::id),
                    Loyalty.CODEC.optionalFieldOf("loyalty").forGetter(TardisDesktopSchema::requirement))
            .apply(instance, DatapackDesktop::new));

    public DatapackDesktop(Identifier id, Optional<Loyalty> loyalty) {
        super(id, new DesktopPreviewTexture(DesktopPreviewTexture.pathFromDesktopId(id)), loyalty);
    }

    public static TardisDesktopSchema fromInputStream(InputStream stream) {
        return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
    }

    public static TardisDesktopSchema fromJson(JsonObject json) {
        AtomicReference<TardisDesktopSchema> created = new AtomicReference<>();

        CODEC.decode(JsonOps.INSTANCE, json).get().ifLeft(desktop -> created.set(desktop.getFirst())).ifRight(err -> {
            created.set(null);
            AITMod.LOGGER.error("Error decoding datapack desktop: {}", err);
        });

        return created.get();
    }
}
