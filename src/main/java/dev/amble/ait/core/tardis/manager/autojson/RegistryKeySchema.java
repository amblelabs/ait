package dev.amble.ait.core.tardis.manager.autojson;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.schema.ArraySchema;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class RegistryKeySchema implements ArraySchema<RegistryKey<?>, Identifier[]> {

    @Override
    public <To> void serialize(JsonAdapter<Object, To> adapter, JsonSerializationContext.Array array, RegistryKey<?> registryKey) {
        array.array$element(registryKey.getRegistry());
        array.array$element(registryKey.getValue());
    }

    @Override
    public Identifier[] instantiate() {
        return new Identifier[2];
    }

    @Override
    public <To> Identifier[] deserialize(JsonAdapter<Object, To> adapter, JsonDeserializationContext ctx, Identifier[] identifiers, int i) {
        identifiers[i] = ctx.decode(Identifier.class);
        return identifiers;
    }

    @Override
    public RegistryKey<?> pack(Identifier[] obj) {
        Identifier registry = obj[0];
        Identifier value = obj[1];

        return RegistryKey.of(RegistryKey.ofRegistry(registry), value);
    }
}
