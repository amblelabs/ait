package dev.amble.ait.data.properties.integer.ranged;

import java.util.function.Function;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.MathHelper;

import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.PropertyType;

public class RangedIntProperty extends Property<Integer> {

    public static final PropertyType<Integer> TYPE = new PropertyType<>(Integer.class, PacketByteBuf::writeInt, PacketByteBuf::readInt);

    private final int min;
    private final int max;

    public RangedIntProperty(String name, int max) {
        this(name, 0, max, 0);
    }

    public RangedIntProperty(String name, int max, Integer def) {
        this(name, 0, max, normalize(0, max, def));
    }

    public RangedIntProperty(String name, int min, int max, Integer def) {
        super(TYPE, name, normalize(min, max, def));

        this.min = min;
        this.max = max;
    }

    public RangedIntProperty(String name, int max, int def) {
        super(TYPE, name, def);

        this.min = 0;
        this.max = max;
    }

    public RangedIntProperty(String name, int min, int max, Function<KeyedTardisComponent, Integer> def) {
        super(TYPE, name, def.andThen(i -> RangedIntProperty.normalize(min, max, i)));

        this.min = min;
        this.max = max;
    }

    @Override
    public RangedIntValue create(KeyedTardisComponent holder) {
        return (RangedIntValue) super.create(holder);
    }

    @Override
    protected RangedIntValue create(Integer integer) {
        return new RangedIntValue(integer);
    }

    public static int normalize(int min, int max, Integer value) {
        return MathHelper.clamp(value == null ? 0 : value, min, max);
    }

    public static int normalize(RangedIntProperty property, Integer value) {
        return normalize(property.min, property.max, value);
    }
}
