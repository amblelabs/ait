package dev.amble.ait.data.schema.console;

import java.lang.reflect.Type;

import com.google.gson.*;
import dev.amble.lib.api.Identifiable;

import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import dev.amble.ait.api.Nameable;
import dev.amble.ait.core.tardis.control.ControlTypes;
import dev.amble.ait.data.schema.exterior.category.CapsuleCategory;
import dev.amble.ait.registry.impl.console.ConsoleRegistry;
import dev.amble.ait.registry.impl.console.variant.ConsoleVariantRegistry;

public abstract class ConsoleTypeSchema implements Identifiable, Nameable {
    private final Identifier id;
    private final String name;

    protected ConsoleTypeSchema(Identifier id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        return o instanceof ConsoleTypeSchema schema && id.equals(schema.id);
    }

    @Override
    public Identifier id() {
        return this.id;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name();
    }

    // @TODO protocol abstraction with numbered letters

    public abstract ControlTypes[] getControlTypes(); // fixme this kinda sucks idk

    /**
     * The default console for this category
     */
    public ConsoleVariantSchema getDefaultVariant() {
        return ConsoleVariantRegistry.withParent(this).get(0);
    }

    public static Object serializer() {
        return new Serializer();
    }

    private static class Serializer implements JsonSerializer<ConsoleTypeSchema>, JsonDeserializer<ConsoleTypeSchema> {

        @Override
        public ConsoleTypeSchema deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            Identifier id;

            try {
                id = new Identifier(json.getAsJsonPrimitive().getAsString());
            } catch (InvalidIdentifierException e) {
                id = CapsuleCategory.REFERENCE;
            }

            return ConsoleRegistry.getInstance().get(id);
        }

        @Override
        public JsonElement serialize(ConsoleTypeSchema src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.id().toString());
        }
    }
}
