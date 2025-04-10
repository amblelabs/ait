package dev.drtheo.autojson.ast;

public interface JsonElement {

    default JsonObject getAsJsonObject() {
        return (JsonObject) this;
    }

    default JsonPrimitive getAsJsonPrimitive() {
        return (JsonPrimitive) this;
    }

    default boolean isJsonPrimitive() {
        return this instanceof JsonPrimitive;
    }

    default boolean isJsonObject() {
        return this instanceof JsonObject;
    }
}
