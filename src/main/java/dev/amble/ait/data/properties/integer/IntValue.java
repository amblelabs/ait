package dev.amble.ait.data.properties.integer;

import dev.amble.ait.data.properties.Value;

public class IntValue extends Value<Integer> {

    protected IntValue(Integer value) {
        super(value);
    }

    @Override
    public void set(Integer value, boolean sync) {
        super.set(IntProperty.normalize(value), sync);
    }

    public static Object serializer() {
        return new Serializer<>(IntProperty.TYPE, IntValue::new);
    }
}
