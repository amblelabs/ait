package dev.amble.ait.data.properties.dbl;

import dev.amble.ait.data.properties.Value;
import dev.drtheo.autojson.SchemaHolder;

public class DoubleValue extends Value<Double> {

    protected DoubleValue(Double value) {
        super(value);
    }

    @Override
    public void set(Double value, boolean sync) {
        super.set(DoubleProperty.normalize(value), sync);
    }

    public static Object serializer() {
        return new Serializer<>(DoubleProperty.TYPE, DoubleValue::new);
    }

    public static dev.drtheo.autojson.schema.base.Schema<DoubleValue> serial(SchemaHolder holder) {
        return new SchemaImpl<>(holder, double.class, DoubleValue::new);
    }
}
