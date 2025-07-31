package dev.amble.ait.data.schema.console;

import java.lang.reflect.Type;

import com.google.gson.*;
import dev.amble.ait.client.models.consoles.ConsoleModel;
import dev.amble.lib.api.Identifiable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Vector3f;

import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import dev.amble.ait.client.models.consoles.SimpleConsoleModel;
import dev.amble.ait.data.schema.console.variant.hartnell.HartnellVariant;
import dev.amble.ait.registry.impl.console.variant.ClientConsoleVariantRegistry;
import dev.amble.ait.registry.impl.console.variant.ConsoleVariantRegistry;

@Environment(EnvType.CLIENT)
public abstract class ClientConsoleVariantSchema implements Identifiable {

    private final Identifier parent;
    private final Identifier id;

    protected ClientConsoleVariantSchema(Identifier parent, Identifier id) {
        this.parent = parent;
        this.id = id;
    }

    protected ClientConsoleVariantSchema(Identifier parent) {
        this.id = parent;
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        return o instanceof ClientConsoleVariantSchema other && this.id.equals(other.id);
    }

    public ConsoleVariantSchema parent() {
        return ConsoleVariantRegistry.getInstance().get(this.parent);
    }

    public Identifier id() {
        return id;
    }

    public abstract Identifier texture();

    public abstract Identifier emission();

    @Environment(EnvType.CLIENT)
    public abstract ConsoleModel model();

    public static Object serializer() {
        return new Serializer();
    }

    public Vector3f sonicItemTranslations() {
        return ConsoleVariantSchema.DEFAULT_SONIC_POS;
    }

    public float[] sonicItemRotations() {
        return ConsoleVariantSchema.DEFAULT_SONIC_ROTATION;
    }

    public Vector3f handlesTranslations() {
        return ConsoleVariantSchema.DEFAULT_HANDLES_POS;
    }

    public float[] handlesRotations() {
        return ConsoleVariantSchema.DEFAULT_HANDLES_ROTATION;
    }

    private static class Serializer
            implements
                JsonSerializer<ClientConsoleVariantSchema>,
                JsonDeserializer<ClientConsoleVariantSchema> {

        @Override
        public ClientConsoleVariantSchema deserialize(JsonElement json, Type typeOfT,
                JsonDeserializationContext context) throws JsonParseException {
            Identifier id;

            try {
                id = new Identifier(json.getAsJsonPrimitive().getAsString());
            } catch (InvalidIdentifierException e) {
                id = HartnellVariant.REFERENCE;
            }

            return ClientConsoleVariantRegistry.getInstance().get(id);
        }

        @Override
        public JsonElement serialize(ClientConsoleVariantSchema src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.id().toString());
        }
    }
}
