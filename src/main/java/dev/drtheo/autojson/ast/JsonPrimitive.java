package dev.drtheo.autojson.ast;

import com.google.gson.internal.LazilyParsedNumber;
import com.sun.jdi.ShortValue;
import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.bake.UnsafeUtil;
import dev.drtheo.autojson.bake.Wrapped;

import java.util.Objects;

public class JsonPrimitive implements JsonElement {

    protected final Object value;

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
        value = Objects.requireNonNull(c);
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
        return this.getAsString().charAt(0);
    }

    public boolean isBoolean() {
        return value instanceof Boolean;
    }

    public boolean isNumber() {
        return value instanceof Number;
    }

    public boolean isString() {
        return value instanceof String;
    }

    public Object unwrap() {
        return value;
    }

    public static class Lazy extends JsonPrimitive implements Wrapped {

        public Lazy(LazilyParsedNumber lazy) {
            super(lazy);
        }

        @Override
        public Object unwrap(Class<?> clazz) {
            if (clazz == int.class || clazz == Integer.class)
                return ((LazilyParsedNumber) this.value).intValue();

            if (clazz == long.class || clazz == Long.class)
                return ((LazilyParsedNumber) this.value).longValue();

            if (clazz == double.class || clazz == Double.class)
                return ((LazilyParsedNumber) this.value).doubleValue();

            if (clazz == float.class || clazz == Float.class)
                return ((LazilyParsedNumber) this.value).floatValue();

            if (clazz == short.class || clazz == Short.class)
                return ((LazilyParsedNumber) this.value).shortValue();

            return this.value;
        }
    }
}
