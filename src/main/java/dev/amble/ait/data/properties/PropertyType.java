package dev.amble.ait.data.properties;

import net.minecraft.network.PacketByteBuf;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PropertyType<T> {

    private final Class<?> clazz;
    private final BiConsumer<PacketByteBuf, T> encoder;
    private final Function<PacketByteBuf, T> decoder;

    public PropertyType(Class<?> clazz, BiConsumer<PacketByteBuf, T> encoder, Function<PacketByteBuf, T> decoder) {
        this.clazz = clazz;
        this.encoder = encoder;
        this.decoder = decoder;
    }

    public boolean isValid(T t) {
        return t != null;
    }

    public boolean equals(T first, T other) {
        return Objects.equals(first, other);
    }

    public void encode(PacketByteBuf buf, T value) {
        this.encoder.accept(buf, value);
    }

    public T decode(PacketByteBuf buf) {
        return this.decoder.apply(buf);
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public static <T extends Enum<T>> PropertyType<T> forEnum(Class<T> clazz) {
        return new PropertyType<>(clazz, PacketByteBuf::writeEnumConstant, buf -> buf.readEnumConstant(clazz));
    }

    public static class Nullable<T> extends PropertyType<T> {

        public Nullable(Class<?> clazz, BiConsumer<PacketByteBuf, T> encoder, Function<PacketByteBuf, T> decoder) {
            super(clazz, encoder, decoder);
        }

        @Override
        public boolean isValid(T t) {
            return true;
        }

        @Override
        public void encode(PacketByteBuf buf, T value) {
            buf.writeNullable(value, super::encode);
        }

        @Override
        public T decode(PacketByteBuf buf) {
            return buf.readNullable(super::decode);
        }
    }
}