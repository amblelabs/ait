package dev.amble.ait.data.enummap;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A thread-safe implementation of the {@link EnumMap}.
 *
 * @author Theo
 */
public class ConcurrentEnumMap<K extends Ordered, V> extends EnumMap<K, V> {

    private final Object lock = new Object();

    public ConcurrentEnumMap(Supplier<K[]> values, Function<Integer, V[]> supplier) {
        super(values, supplier);
    }

    @Override
    public V put(K k, V v) {
        synchronized (lock) {
            return super.put(k, v);
        }
    }

    @Override
    public void clear() {
        synchronized (lock) {
            super.clear();
        }
    }

    @Override
    public V get(int index) {
        synchronized (lock) {
            return super.get(index);
        }
    }
}
