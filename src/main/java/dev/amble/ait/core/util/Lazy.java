package dev.amble.ait.core.util;

import java.util.function.Supplier;

public class Lazy<T> {

    private final Supplier<T> creator;

    private T value;
    private boolean cached;

    public Lazy(Supplier<T> creator) {
        this.creator = creator;
        this.invalidate();
    }

    public T get() {
        if (this.value == null)
            value = creator.get();

        return value;
    }

    public void invalidate() {
        this.value = null;
    }
}
