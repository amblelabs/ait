package dev.amble.ait.core.tardis.manager.autojson;

import dev.drtheo.autojson.SchemaHolder;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.schema.ObjectSchema;
import dev.drtheo.autojson.schema.Schema;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UuidMapSchema<V> implements ObjectSchema<Map<UUID, V>> {

    private final Class<V> clazz;
    private final Schema<V> schema;

    public UuidMapSchema(SchemaHolder holder, ParameterizedType type) {
        this.clazz = (Class<V>) type.getActualTypeArguments()[1];
        this.schema = holder.schema(this.clazz);
    }

    @Override
    public <To> void serialize(JsonAdapter<Object, To> adapter, JsonSerializationContext.Obj obj, Map<UUID, V> map) {
        map.forEach((uuid, v) -> obj.obj$put(uuid.toString(), v));
    }

    @Override
    public Map<UUID, V> instantiate() {
        return new HashMap<>();
    }

    @Override
    public <To> void deserialize(JsonAdapter<Object, To> adapter, JsonDeserializationContext ctx, Map<UUID, V> map, String s) {
        map.put(UUID.fromString(s), ctx.decode(clazz, schema));
    }
}
