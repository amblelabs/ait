package dev.amble.ait.data.properties.dbl;

import java.util.function.Function;

import net.minecraft.network.PacketByteBuf;

import dev.amble.ait.api.KeyedTardisComponent;
import dev.amble.ait.data.properties.Property;

public class DoubleProperty extends Property<Double> {

    public static final Type<Double> TYPE = new Type<>(Double.class, PacketByteBuf::writeDouble,
            PacketByteBuf::readDouble);

    public DoubleProperty(String name) {
        this(name, 0);
    }

    public DoubleProperty(String name, Double def) {
        this(name, normalize(def));
    }

    public DoubleProperty(String name, double def) {
        super(TYPE, name, def);
    }

    public DoubleProperty(String name, Function<KeyedTardisComponent, Double> def) {
        super(TYPE, name, def.andThen(DoubleProperty::normalize));
    }

    @Override
    public DoubleValue create(KeyedTardisComponent holder) {
        return (DoubleValue) super.create(holder);
    }

    @Override
    protected DoubleValue create(Double integer) {
        return new DoubleValue(integer);
    }

    public static double normalize(Double value) {
        return value == null ? 0 : value;
    }
}
