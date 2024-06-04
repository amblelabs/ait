package loqor.ait.tardis.data.properties.v2.integer;

import loqor.ait.tardis.base.TardisComponent;
import loqor.ait.tardis.data.properties.v2.Value;

public class IntValue extends Value<Integer> {

    protected IntValue(TardisComponent holder, IntProperty property, int value) {
        super(holder, property, value);
    }

    private IntValue(Integer value) {
        super(value);
    }

    @Override
    public void set(Integer value, boolean sync) {
        super.set(IntProperty.normalize(value), sync);
    }

    public static Object serializer() {
        return new Serializer<Integer, IntValue>(Integer.class, IntValue::new);
    }
}