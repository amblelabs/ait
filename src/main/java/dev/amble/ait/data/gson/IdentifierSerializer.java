package dev.amble.ait.data.gson;

import com.google.gson.*;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;

/**
 * A more compact identifier serializer.
 */
public class IdentifierSerializer implements JsonSerializer<Identifier>, JsonDeserializer<Identifier> {

    @Override
    public Identifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return Identifier.tryParse(json.getAsString());
    }

    @Override
    public JsonElement serialize(Identifier src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }
}
