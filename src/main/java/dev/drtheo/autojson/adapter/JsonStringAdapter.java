package dev.drtheo.autojson.adapter;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.bake.UnsafeUtil;

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
        return null;
    }
}
