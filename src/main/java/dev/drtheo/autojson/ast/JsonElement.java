package dev.drtheo.autojson.ast;

public interface JsonElement {

    default JsonObject getAsJsonObject() {
        return (JsonObject) this;
    }

    default JsonPrimitive getAsJsonPrimitive() {
        return (JsonPrimitive) this;
    }
}
