package dev.amble.ait.data.properties.integer.ranged;

import dev.amble.ait.data.properties.Value;

public class RangedIntValue extends Value<Integer> {

    protected RangedIntValue(int value) {
        super(value);
    }

    public static RangedIntValue of(int value) {
        return new RangedIntValue(value);
    }

    @Override
    public void set(Integer value, boolean sync) {
        super.set(RangedIntProperty.normalize(this.asRanged(), value), sync);
    }

    private RangedIntProperty asRanged() {
        return (RangedIntProperty) this.property;
    }

    public static Object serializer() {
        return new Serializer<>(RangedIntProperty.TYPE, RangedIntValue::new);
    }
}
