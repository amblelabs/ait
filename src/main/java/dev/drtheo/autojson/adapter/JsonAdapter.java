package dev.drtheo.autojson.adapter;

import dev.drtheo.autojson.ast.JsonElement;
import dev.drtheo.autojson.ast.JsonNull;
import dev.drtheo.autojson.ast.JsonObject;

public interface JsonAdapter<From, To> {

    default To toJson(Object obj) {
        return toJson(obj, obj.getClass());
    }

    <T> To toJson(T obj, Class<?> clazz);
    <R extends From> R fromJson(To object, Class<R> clazz);

    <R extends From> R fromJson(JsonElement json, Class<R> clazz);
}