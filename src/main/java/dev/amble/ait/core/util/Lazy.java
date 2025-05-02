package dev.amble.ait.core.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Lazy<T> {

    private final Supplier<T> creator;

    private T value;
    private boolean cached;

    public Lazy(Supplier<T> creator) {
        this.creator = creator;
    }

    public T get() {
        if (!cached) {
            value = creator.get();
            cached = true;
        }

        return value;
    }

    public void invalidate() {
        this.value = null;
        this.cached = false;
    }

    public boolean isCached() {
        return cached;
    }

    public void ifPresent(Consumer<? super T> action) {
        if (cached)
            action.accept(value);
    }

    public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
        if (cached) {
            action.accept(value);
        } else {
            emptyAction.run();
        }
    }

    public T getOrDefault(Supplier<? extends T> supplier) {
        if (this.cached)
            return value;

        return supplier.get();
    }
}
