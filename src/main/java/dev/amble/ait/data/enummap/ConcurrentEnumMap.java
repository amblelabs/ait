package dev.amble.ait.data.enummap;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A thread-safe implementation of the {@link EnumMap}.
 *
 * @author Theo
 */
public class ConcurrentEnumMap<K extends Ordered, V> extends EnumMap<K, V> {

    public ConcurrentEnumMap(Supplier<K[]> values, Function<Integer, V[]> supplier) {
        super(values, supplier);
    }

    @Override
    public synchronized V put(K k, V v) {
        return super.put(k, v);
    }

    @Override
    public synchronized void clear() {
        super.clear();
    }

    @Override
    public synchronized V get(K k) {
        return super.get(k);
    }

    @Override
    public synchronized V get(int index) {
        return super.get(index);
    }
}
