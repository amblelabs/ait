package dev.drtheo.autojson.adapter;

public interface JsonAdapter<From, To> {

    default To toJson(Object obj) {
        return toJson(obj, obj.getClass());
    }

    <T> To toJson(T obj, Class<?> clazz);
    <R extends From> R fromJson(To object, Class<R> clazz);
}