package dev.amble.ait.data.properties.bool;

import dev.amble.ait.data.properties.Value;
import dev.amble.ait.data.properties.integer.IntValue;
import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.schema.Schema;

public class BoolValue extends Value<Boolean> {

    protected BoolValue(Boolean value) {
        super(value);
    }

    @Override
    public void set(Boolean value, boolean sync) {
        super.set(BoolProperty.normalize(value), sync);
    }

    public void toggle() {
        this.flatMap(b -> !b);
    }

    public void toggle(boolean sync) {
        this.flatMap(b -> !b, sync);
    }

    public static Object serializer() {
        return new Serializer<>(BoolProperty.TYPE, BoolValue::new);
    }

    public static dev.drtheo.autojson.schema.Schema<BoolValue> serial() {
        return new Schema<>(boolean.class, BoolValue::new);
    }
}
