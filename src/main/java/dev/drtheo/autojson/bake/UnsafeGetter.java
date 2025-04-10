package dev.drtheo.autojson.bake;

import sun.misc.Unsafe;

@FunctionalInterface
public interface UnsafeGetter<T> {
    T get(Unsafe unsafe, Object obj, long address);
}
