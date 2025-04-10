package dev.drtheo.autojson;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.ast.JsonElement;

public interface Schema<T> {
    <To> void serialize(JsonAdapter<? super T, To> auto, JsonSerializationContext c, T t);
    <To> T deserialize(JsonAdapter<T, To> auto, JsonElement element);

    @FunctionalInterface
    interface F {
        void put(String key, Object value);
    }
}
