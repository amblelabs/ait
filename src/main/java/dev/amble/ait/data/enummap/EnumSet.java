package dev.amble.ait.data.enummap;

import java.util.function.Supplier;

public class EnumSet<K extends Ordered> {

    private final boolean[] values;

    public EnumSet(Supplier<K[]> values) {
        this.values = new boolean[values.get().length];
    }

    public boolean contains(K k) {
        return values[k.index()];
    }

    public void add(K k) {
        values[k.index()] = true;
    }

    public void addAll(K[] ks) {
        for (K k : ks) {
            this.add(k);
        }
    }

    public void remove(K k) {
        values[k.index()] = false;
    }
}
