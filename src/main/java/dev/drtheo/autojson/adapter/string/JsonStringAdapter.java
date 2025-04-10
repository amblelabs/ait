package dev.drtheo.autojson.adapter.string;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.ast.JsonElement;
import dev.drtheo.autojson.ast.JsonNull;
import dev.drtheo.autojson.ast.JsonObject;
import dev.drtheo.autojson.ast.JsonPrimitive;
import dev.drtheo.autojson.bake.UnsafeUtil;

import java.io.IOException;

public class JsonStringAdapter implements JsonAdapter<Object, String> {

    private final AutoJSON auto;

    public JsonStringAdapter(AutoJSON auto) {
        this.auto = auto;
    }

    @Override
    public <T> String toJson(T obj, Class<?> clazz) {
        if (UnsafeUtil.isPrimitive(clazz))
            return obj.toString();

        JsonStringBuilder jsb = new JsonStringBuilder(this);

        toJson(jsb, obj, clazz);
        return jsb.toString();
    }

    protected <T> void toJson(JsonStringBuilder builder, T obj, Class<?> clazz) {
        builder.begin();
        auto.schema(clazz).serialize(this, builder, obj);
        builder.end();
    }

    @Override
    public <R> R fromJson(String object, Class<R> clazz) {
        try {
            JsonElement element = JsonParser.parse(object);
            return fromJson(element, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <R> R fromJson(JsonElement json, Class<R> clazz) {
        if (json instanceof JsonObject o)
            return (R) this.auto.schema(clazz).deserialize(this, o);

        if (json instanceof JsonPrimitive p)
            return (R) p.unwrap();

        return null;
    }
}
