package dev.drtheo.autojson.bake;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

import java.util.function.Function;

public interface ClassAdapter<T> {

    T get(Unsafe unsafe, Object obj, long address);
    void set(Unsafe unsafe, Object obj, long address, T value);

    static ClassAdapter<?> match(Class<?> clazz) {
        if (!clazz.isPrimitive())
            return OBJECT;

        if (clazz == Boolean.TYPE)
            return BOOL;

        if (clazz == Byte.TYPE)
            return BYTE;

        if (clazz == Short.TYPE)
            return SHORT;

        if (clazz == Character.TYPE)
            return CHAR;

        if (clazz == Integer.TYPE)
            return INT;

        if (clazz == Float.TYPE)
            return FLOAT;

        if (clazz == Double.TYPE)
            return DOUBLE;

        if (clazz == Long.TYPE)
            return LONG;

        return OBJECT;
    }

    record Primitive<T>(ClassAdapter<T> child, T def) implements ClassAdapter<T> {

        public static <T> Primitive<T> simple(UnsafeGetter<T> getter, UnsafeSetter<T> setter, T def) {
            return new Primitive<>(new Simple<>(getter, setter), def);
        }

        @NotNull
        @Override
        public T get(Unsafe unsafe, Object obj, long address) {
            return child.get(unsafe, obj, address);
        }

        @Override
        public void set(Unsafe unsafe, Object obj, long address, T value) {
            if (value == null)
                value = def;

            child.set(unsafe, obj, address, value);
        }
    }

    record Simple<T>(UnsafeGetter<T> getter, UnsafeSetter<T> setter) implements ClassAdapter<T> {

        @Override
        public T get(Unsafe unsafe, Object obj, long address) {
            return getter.get(unsafe, obj, address);
        }

        @Override
        public void set(Unsafe unsafe, Object obj, long address, T value) {
            setter.set(unsafe, obj, address, value);
        }
    }

    Primitive<Boolean> BOOL = Primitive.simple(
            Unsafe::getBoolean, Unsafe::putBoolean, false);

    Primitive<Byte> BYTE = Primitive.simple(
            Unsafe::getByte, Unsafe::putByte, (byte) 0);

    Primitive<Character> CHAR = Primitive.simple(
            Unsafe::getChar, Unsafe::putChar, (char) 0);

    Primitive<Short> SHORT = Primitive.simple(
            Unsafe::getShort, Unsafe::putShort, (short) 0);

    Primitive<Integer> INT = Primitive.simple(
            Unsafe::getInt, Unsafe::putInt, 0);

    Primitive<Float> FLOAT = Primitive.simple(
            Unsafe::getFloat, Unsafe::putFloat, 0f);

    Primitive<Double> DOUBLE = Primitive.simple(
            Unsafe::getDouble, Unsafe::putDouble, 0d);

    Primitive<Long> LONG = Primitive.simple(
            Unsafe::getLong, Unsafe::putLong, 0L);

    Simple<Object> OBJECT = new Simple<>(
            Unsafe::getObject, Unsafe::putObject);

    static <T, O> ClassAdapter<T> conform(ClassAdapter<O> adapter) {
        return new ClassAdapter<>() {
            @Override
            public T get(Unsafe unsafe, Object obj, long address) {
                return (T) adapter.get(unsafe, obj, address);
            }

            @Override
            public void set(Unsafe unsafe, Object obj, long address, T value) {
                adapter.set(unsafe, obj, address, (O) value);
            }
        };
    }
}
