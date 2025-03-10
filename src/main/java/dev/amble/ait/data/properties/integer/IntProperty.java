package dev.amble.ait.data.properties.integer;

import java.util.function.Function;

import net.minecraft.network.PacketByteBuf;

import dev.amble.ait.api.KeyedTardisComponent;
import dev.amble.ait.data.properties.Property;

public class IntProperty extends Property<Integer> {

    public static final Type<Integer> TYPE = new Type<>(Integer.class, PacketByteBuf::writeInt, PacketByteBuf::readInt);

    public IntProperty(String name) {
        this(name, 0);
    }

    public IntProperty(String name, Integer def) {
        this(name, normalize(def));
    }

    public IntProperty(String name, int def) {
        super(TYPE, name, def);
    }

    public IntProperty(String name, Function<KeyedTardisComponent, Integer> def) {
        super(TYPE, name, def.andThen(IntProperty::normalize));
    }

    @Override
    public IntValue create(KeyedTardisComponent holder) {
        return (IntValue) super.create(holder);
    }

    @Override
    protected IntValue create(Integer integer) {
        return new IntValue(integer);
    }

    public static int normalize(Integer value) {
        return value == null ? 0 : value;
    }
}
