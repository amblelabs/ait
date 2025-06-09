package dev.amble.ait.data.properties.dbl;

import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.PropertyType;
import net.minecraft.network.PacketByteBuf;

import java.util.function.Function;

public class DoubleProperty extends Property<Double> {

    public static final PropertyType<Double> TYPE = new PropertyType<>(Double.class, PacketByteBuf::writeDouble,
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
