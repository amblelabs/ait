package dev.amble.ait.data.schema.door;

import com.google.gson.*;
import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.data.schema.door.impl.CapsuleDoorVariant;
import dev.amble.ait.registry.impl.door.ClientDoorRegistry;
import dev.amble.ait.registry.impl.door.DoorRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.lang.reflect.Type;

@Environment(EnvType.CLIENT)
public abstract class ClientDoorSchema {
    private final Identifier parent;
    private final Identifier id;

    protected ClientDoorSchema(Identifier parent, Identifier id) {
        this.parent = parent;
        this.id = id;
    }

    protected ClientDoorSchema(Identifier parent) {
        this.id = parent;
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() == null)
            return false;

        ClientDoorSchema that = (ClientDoorSchema) o;

        return id.equals(that.id);
    }

    public DoorSchema parent() {
        return DoorRegistry.REGISTRY.get(this.parent);
    }

    public Identifier id() {
        return id;
    }

    // public abstract Identifier texture();
    // public abstract Identifier emission();
    public abstract DoorModel model();

    public static Object serializer() {
        return new Serializer();
    }

    private static class Serializer implements JsonSerializer<ClientDoorSchema>, JsonDeserializer<ClientDoorSchema> {

        @Override
        public ClientDoorSchema deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            Identifier id;

            try {
                id = new Identifier(json.getAsJsonPrimitive().getAsString());
            } catch (InvalidIdentifierException e) {
                id = CapsuleDoorVariant.REFERENCE;
            }

            return ClientDoorRegistry.REGISTRY.get(id);
        }

        @Override
        public JsonElement serialize(ClientDoorSchema src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.id().toString());
        }
    }
}
