package dev.drtheo.autojson.bake;

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

    final class Simple<T> implements ClassAdapter<T> {

        private final UnsafeGetter<T> getter;
        private final UnsafeSetter<T> setter;
        
        public Simple(UnsafeGetter<T> getter, UnsafeSetter<T> setter) {
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public T get(Unsafe unsafe, Object obj, long address) {
            return getter.get(unsafe, obj, address);
        }

        @Override
        public void set(Unsafe unsafe, Object obj, long address, T value) {
            setter.set(unsafe, obj, address, value);
        }
    }

    final class Primitive<T> implements ClassAdapter<T> {
        private final ClassAdapter<T> child;
        private final T def;
        
        public Primitive(UnsafeGetter<T> getter, UnsafeSetter<T> setter, T def) {
            this(new Simple<>(getter, setter), def);
        }

        public Primitive(ClassAdapter<T> child, T def) {
            this.child = child;
            this.def = def;
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

    class Wrapped<T, B> implements ClassAdapter<T> {

        private final Class<T> clazz;
        private final Class<B> base;
        private final Function<B, T> f;
        private final ClassAdapter<T> child;

        public Wrapped(Class<T> clazz, Class<B> base, Function<B, T> f, ClassAdapter<T> child) {
            this.clazz = clazz;
            this.base = base;
            this.f = f;
            this.child = child;
        }

        @Override
        public T get(Unsafe unsafe, Object obj, long address) {
            return child.get(unsafe, obj, address);
        }

        @Override
        public void set(Unsafe unsafe, Object obj, long address, T value) {
            if (value.getClass() != clazz && base.isInstance(value))
                value = f.apply(base.cast(value));

            child.set(unsafe, obj, address, value);
        }
    }

    final class Num<T extends Number> extends Wrapped<T, Number> {

        public Num(Class<T> clazz, Function<Number, T> f, ClassAdapter<T> child) {
            super(clazz, Number.class, f, child);
        }
    }

    ClassAdapter<Boolean> BOOL = new Primitive<>(
            Unsafe::getBoolean, Unsafe::putBoolean, false);

    ClassAdapter<Character> CHAR = new Wrapped<>(Character.class,
            CharSequence.class, s -> s.charAt(0),
            new Primitive<>(Unsafe::getChar, Unsafe::putChar, (char) 0));

    ClassAdapter<Byte> BYTE = new Num<>(Byte.class, Number::byteValue,
            new Primitive<>(Unsafe::getByte, Unsafe::putByte, (byte) 0));

    ClassAdapter<Short> SHORT = new Num<>(Short.class, Number::shortValue,
            new Primitive<>(Unsafe::getShort, Unsafe::putShort, (short) 0));

    ClassAdapter<Integer> INT = new Num<>(Integer.class, Number::intValue,
            new Primitive<>(Unsafe::getInt, Unsafe::putInt, 0));

    ClassAdapter<Float> FLOAT = new Num<>(Float.class, Number::floatValue,
            new Primitive<>(Unsafe::getFloat, Unsafe::putFloat, 0f));

    ClassAdapter<Double> DOUBLE = new Num<>(Double.class, Number::doubleValue,
            new Primitive<>(Unsafe::getDouble, Unsafe::putDouble, 0d));

    ClassAdapter<Long> LONG = new Num<>(Long.class, Number::longValue,
            new Primitive<>(Unsafe::getLong, Unsafe::putLong, 0L));

    ClassAdapter<Object> OBJECT = new Simple<>(Unsafe::getObject, Unsafe::putObject);
}
