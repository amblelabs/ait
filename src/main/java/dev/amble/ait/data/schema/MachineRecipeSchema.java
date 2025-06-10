package dev.amble.ait.data.schema;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;
import dev.amble.lib.api.Identifiable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import dev.amble.ait.core.util.StackUtil;
import dev.amble.ait.registry.impl.MachineRecipeRegistry;

public class MachineRecipeSchema implements Identifiable {

    private final Identifier id;
    private final ItemStack output;
    private final List<ItemStack> input;

    public MachineRecipeSchema(Identifier id, ItemStack output, List<ItemStack> input) {
        this.id = id;
        this.output = output;
        this.input = input;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o instanceof MachineRecipeSchema other)
            return this.id.equals(other.id);

        return false;
    }

    public Identifier id() {
        return id;
    }

    public ItemStack output() {
        return output;
    }

    public List<ItemStack> input() {
        return this.input;
    }

    public MachineRecipeSchema copy() {
        return new MachineRecipeSchema(this.id, this.output.copy(), StackUtil.copy(this.input, ArrayList::new));
    }

    public static Object serializer() {
        return new Serializer();
    }

    private static class Serializer
            implements
                JsonSerializer<MachineRecipeSchema>,
                JsonDeserializer<MachineRecipeSchema> {

        @Override
        public MachineRecipeSchema deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return MachineRecipeRegistry.getInstance().get(new Identifier(json.getAsJsonPrimitive().getAsString()));
        }

        @Override
        public JsonElement serialize(MachineRecipeSchema src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.id().toString());
        }
    }
}
