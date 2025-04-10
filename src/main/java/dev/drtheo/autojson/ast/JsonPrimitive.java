package dev.drtheo.autojson.ast;

import dev.drtheo.autojson.AutoJSON;

import java.util.Objects;

public class JsonPrimitive implements JsonElement {

    private final Object value;

    public JsonPrimitive(Boolean bool) {
        value = Objects.requireNonNull(bool);
    }

    public JsonPrimitive(Number number) {
        value = Objects.requireNonNull(number);
    }

    public JsonPrimitive(String string) {
        value = Objects.requireNonNull(string);
    }

    public JsonPrimitive(Character c) {
        value = c.toString();
    }

    public boolean getAsBoolean() {
        return (Boolean) value;
    }

    public Number getAsNumber() {
        return (Number) value;
    }

    public String getAsString() {
        return (String) value;
    }

    public Character getAsCharacter() {
        return (Character) value;
    }
}
