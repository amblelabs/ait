package dev.amble.ait.data.gson;

import java.lang.reflect.Type;

import com.google.gson.*;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class RegistryKeySerializer implements JsonSerializer<RegistryKey<?>>, JsonDeserializer<RegistryKey<?>> {

    @Override
    public RegistryKey<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        Identifier registry = context.deserialize(object.get("registry"), Identifier.class);
        Identifier value = context.deserialize(object.get("value"), Identifier.class);

        return RegistryKey.of(RegistryKey.ofRegistry(registry), value);
    }

    @Override
    public JsonElement serialize(RegistryKey<?> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.add("registry", context.serialize(src.getRegistry()));
        object.add("value", context.serialize(src.getValue()));

        return object;
    }
}
