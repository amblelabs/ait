package dev.drtheo.autojson.ast;

import dev.drtheo.autojson.AutoJSON;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class JsonObject implements JsonElement {

    public static JsonObject create() {
        return new Simple();
    }

    public abstract void put(String key, JsonElement value);
    public abstract JsonElement get(String key);

    public abstract void remove(String key);
    public abstract void forEach(BiConsumer<String, JsonElement> consumer);

    static class Simple extends JsonObject {

        private final Map<String, JsonElement> elements = new HashMap<>();

        @Override
        public void put(String key, JsonElement value) {
            this.elements.put(key, value);
        }

        @Override
        public JsonElement get(String key) {
            return elements.get(key);
        }

        @Override
        public void remove(String key) {
            this.elements.remove(key);
        }

        @Override
        public void forEach(BiConsumer<String, JsonElement> consumer) {
            this.elements.forEach(consumer);
        }
    }
}
