package dev.amble.ait.data.enummap;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

/**
 * Custom and lightweight map implementation for enums.
 * Faster than {@link java.util.EnumMap}.
 *
 * @author Theo.
 */
public class EnumMap<K extends Ordered, V> {

    private final V[] values;

    public EnumMap(Supplier<K[]> values, Function<Integer, V[]> supplier) {
        this.values = supplier.apply(values.get().length);
    }

    public void map(Function<V, V> func) {
        for (int i = 0; i < values.length; i++) {
            values[i] = func.apply(values[i]);
        }
    }

    /**
     * @implNote Will return ALL values, including nulls.
     * @return All values associated with each variant of an enum, null if no value
     *         is present.
     */
    public V[] getValues() {
        return this.values;
    }

    public V put(K k, V v) {
        V prev = values[k.index()];
        values[k.index()] = v;

        return prev;
    }

    public V remove(K k) {
        return put(k, null);
    }

    public V get(K k) {
        return this.get(k.index());
    }

    public V get(int index) {
        return values[index];
    }

    public boolean containsKey(K k) {
        if ((this.size() - 1) < k.index()) return false;

        return this.get(k) != null;
    }

    public void clear() {
        Arrays.fill(this.values, null);
    }

    public int size() {
        return this.values.length;
    }

    public static class Compliant<K extends Ordered, V> extends EnumMap<K, V> implements Map<K, V> {

        private final K[] keys;

        public Compliant(Supplier<K[]> values, Function<Integer, V[]> supplier) {
            super(values, supplier);
            this.keys = values.get();
        }

        @Override
        public V remove(Object k) {
            return this.remove((K) k);
        }

        @Override
        public V get(Object key) {
            return this.get((K) key);
        }

        @Override
        public void putAll(@NotNull Map<? extends K, ? extends V> m) {
            m.forEach(this::put);
        }

        @Override
        public boolean containsKey(Object key) {
            return this.containsKey((K) key);
        }

        @NotNull @Override
        public Set<Entry<K, V>> entrySet() {
            V[] values = this.getValues();
            Set<Entry<K, V>> set = new HashSet<>(values.length);

            for (int i = 0; i < this.size(); i++) {
                V value = values[i];

                if (value != null)
                    set.add(Map.entry(this.keys[i], value));
            }

            return set;
        }

        @NotNull @Override
        public Set<K> keySet() {
            return Set.of(this.keys);
        }

        @NotNull @Override
        public Collection<V> values() {
            return List.of(this.getValues());
        }

        @Override
        public boolean isEmpty() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsValue(Object value) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }
    }
}
