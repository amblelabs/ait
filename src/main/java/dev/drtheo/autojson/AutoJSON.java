package dev.drtheo.autojson;

import dev.drtheo.autojson.adapter.JsonAdapter;

import java.util.HashMap;
import java.util.Map;

public class AutoJSON {

    private final Map<Class<?>, BakedAutoSchema<?>> schemas = new HashMap<>();

    public void bake(Class<?> clazz) {
        this.schemas.put(clazz, BakedAutoSchema.bake(clazz));
    }

    public <T> Schema<T> schema(Class<?> clazz) {
        return (Schema<T>) schemas.computeIfAbsent(clazz, BakedAutoSchema::bake);
    }

    public <F, T> T toJson(JsonAdapter<F, T> adapter, Object obj) {
        return toJson(adapter, obj, obj.getClass());
    }

    public <F, T> T toJson(JsonAdapter<F, T> adapter, Object obj, Class<?> clazz) {
        return adapter.toJson(obj, clazz);
    }

    public <F, T> F fromJson(JsonAdapter<F, T> adapter, T object, Class<F> clazz) {
        return adapter.fromJson(object, clazz);
    }
}
