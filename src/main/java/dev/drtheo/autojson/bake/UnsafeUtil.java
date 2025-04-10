package dev.drtheo.autojson.bake;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeUtil {

    public static final Unsafe UNSAFE;

    // Used in benchmarks
    public static void init() { }

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isPrimitive(Class<?> clazz) {
        return clazz.isPrimitive() || Number.class.isAssignableFrom(clazz) || clazz == Character.class || clazz == String.class || clazz == Boolean.class;
    }

    public static boolean isChar(Class<?> clazz) {
        return clazz == char.class || clazz == Character.class;
    }

    public static boolean isNumber(Class<?> clazz) {
        return (clazz.isPrimitive() && (clazz == int.class || clazz == short.class
                || clazz == byte.class || clazz == double.class || clazz == float.class
                || clazz == long.class)) || Number.class.isAssignableFrom(clazz);
    }

    public static boolean isBool(Class<?> clazz) {
        return clazz == boolean.class || clazz == Boolean.class;
    }

    // Used in benchmarks
    public static void warmup() {
        for (int i = 0; i < 100_000; i++) {
            Dummy dummy = new Dummy();
            dummy.m();
        }
    }
}
