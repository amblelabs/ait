package dev.amble.ait.data.properties.bool;

import java.util.function.Function;

import net.minecraft.network.PacketByteBuf;

import dev.amble.ait.api.KeyedTardisComponent;
import dev.amble.ait.data.properties.Property;

public class BoolProperty extends Property<Boolean> {

    public static final Type<Boolean> TYPE = new Type<>(Boolean.class, PacketByteBuf::writeBoolean,
            PacketByteBuf::readBoolean);

    public BoolProperty(String name) {
        this(name, false);
    }

    public BoolProperty(String name, Boolean def) {
        this(name, normalize(def));
    }

    public BoolProperty(String name, boolean def) {
        super(TYPE, name, def);
    }

    public BoolProperty(String name, Function<KeyedTardisComponent, Boolean> def) {
        super(TYPE, name, def.andThen(BoolProperty::normalize));
    }

    @Override
    protected BoolValue create(Boolean bool) {
        return new BoolValue(bool);
    }

    @Override
    public BoolValue create(KeyedTardisComponent holder) {
        return (BoolValue) super.create(holder);
    }

    public static boolean normalize(Boolean value) {
        return value != null && value;
    }
}
