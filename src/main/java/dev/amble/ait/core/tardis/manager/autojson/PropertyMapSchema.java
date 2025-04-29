package dev.amble.ait.core.tardis.manager.autojson;

import dev.amble.ait.data.properties.PropertyMap;
import dev.amble.ait.data.properties.Value;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.schema.base.ObjectSchema;

/**
 * Since AutoJSON doesn't check/get the superclass' schema to delegate
 * serialization, it can't figure out that property map is a hash map.
 */
@Deprecated(forRemoval = true)
public class PropertyMapSchema implements ObjectSchema<PropertyMap> {

    public PropertyMapSchema() {

    }

    @Override
    public <To> void serialize(JsonAdapter<Object, To> adapter, JsonSerializationContext.Obj ctx, PropertyMap map) {
        map.forEach(ctx::obj$put);
    }

    @Override
    public PropertyMap instantiate() {
        return new PropertyMap();
    }

    @Override
    public <To> void deserialize(JsonAdapter<Object, To> adapter, JsonDeserializationContext ctx, PropertyMap map, String key) {
        map.put(key, ctx.decode(Value.class));
    }
}