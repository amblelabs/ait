package dev.drtheo.autojson.adapter.string;

import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.bake.UnsafeUtil;

public class JsonStringBuilder implements JsonSerializationContext {

    private final JsonStringAdapter adapter;

    private final StringBuilder builder = new StringBuilder();
    private boolean first = true;

    public JsonStringBuilder(JsonStringAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void put(String key, Object value) {
        if (!first)
            builder.append(",");

        builder.append("\"").append(key).append("\":");

        if (value == null) {
            builder.append("null");
        } else if (value instanceof String || value instanceof Character) {
            //noinspection UnnecessaryToStringCall
            builder.append("\"").append(value.toString()).append("\"");
        } else if (UnsafeUtil.isPrimitive(value.getClass())) {
            //noinspection UnnecessaryToStringCall
            builder.append(value.toString());
        } else {
            this.adapter.toJson(this, value, value.getClass());
        }

        first = false;
    }

    public void begin() {
        this.builder.append("{");
    }

    public void end() {
        this.builder.append("}");
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
