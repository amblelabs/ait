package dev.amble.ait.data.properties;

import dev.amble.ait.api.tardis.Disposable;

import java.util.HashMap;

public class PropertyMap extends HashMap<String, Value<?>> implements Disposable {

    @SuppressWarnings("unchecked")
    public <T> Value<T> getExact(String key) {
        return (Value<T>) this.get(key);
    }

    @Override
    public void dispose() {
        for (Value<?> value : this.values()) {
            value.dispose();
        }

        this.clear();
    }
}
