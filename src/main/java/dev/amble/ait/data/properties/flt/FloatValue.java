package dev.amble.ait.data.properties.flt;

import dev.amble.ait.data.properties.Value;
import dev.drtheo.autojson.SchemaHolder;

public class FloatValue extends Value<Float> {

    protected FloatValue(Float value) {
        super(value);
    }

    @Override
    public void set(Float value, boolean sync) {
        super.set(FloatProperty.normalize(value), sync);
    }

    public static Object serializer() {
        return new Serializer<>(FloatProperty.TYPE, FloatValue::new);
    }

    public static dev.drtheo.autojson.schema.base.Schema<FloatValue> serial(SchemaHolder holder) {
        return new SchemaImpl<>(holder, float.class, FloatValue::new);
    }
}
