package dev.amble.ait.data.properties.flt;

import java.util.function.Function;

import net.minecraft.network.PacketByteBuf;

import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.PropertyType;

public class FloatProperty extends Property<Float> {

    public static final PropertyType<Float> TYPE = new PropertyType<>(Float.class, PacketByteBuf::writeFloat,
            PacketByteBuf::readFloat);

    public FloatProperty(String name) {
        this(name, 0);
    }

    public FloatProperty(String name, Float def) {
        this(name, normalize(def));
    }

    public FloatProperty(String name, float def) {
        super(TYPE, name, def);
    }

    public FloatProperty(String name, Function<KeyedTardisComponent, Float> def) {
        super(TYPE, name, def.andThen(FloatProperty::normalize));
    }

    @Override
    public FloatValue create(KeyedTardisComponent holder) {
        return (FloatValue) super.create(holder);
    }

    @Override
    protected FloatValue create(Float flt) {
        return new FloatValue(flt);
    }

    public static float normalize(Float value) {
        return value == null ? 0 : value;
    }
}
