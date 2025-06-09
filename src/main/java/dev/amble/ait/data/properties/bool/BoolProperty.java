package dev.amble.ait.data.properties.bool;

import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.PropertyType;
import net.minecraft.network.PacketByteBuf;

import java.util.function.Function;

public class BoolProperty extends Property<Boolean> {

    public static final PropertyType<Boolean> TYPE = new PropertyType<>(Boolean.class, PacketByteBuf::writeBoolean,
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
