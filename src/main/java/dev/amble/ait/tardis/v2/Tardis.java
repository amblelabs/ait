package dev.amble.ait.tardis.v2;

import dev.amble.ait.api.tardis.v2.data.DataResolveError;
import dev.amble.ait.api.tardis.v2.data.TData;
import dev.amble.ait.api.tardis.v2.data.TDataHolder;
import dev.amble.ait.api.tardis.v2.data.TDataRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Tardis {

    private final UUID id;
    private final TData<?>[] data = new TData[TDataRegistry.size()];

    private final Object lock = new Object();

    public Tardis() {
        this(UUID.randomUUID());
    }

    public Tardis(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void attach(TData<?> data, boolean overwrite) {
        synchronized(lock) {
            if (!overwrite && this.has(data.holder()))
                return;

            this.data[data.index()] = data;
        }
    }

    public void attach(TData<?> data) {
        this.attach(data, false);
    }

    public void deattach(TDataHolder<?> data) {
        synchronized(lock) {
            this.data[data.index()] = null;
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends TData<T>> T get(TDataHolder<T> holder) {
        return (T) data[holder.index()];
    }

    public <T extends TData<T>> T resolve(TDataHolder<T> holder) {
        T result = this.get(holder);

        if (result == null)
            throw new DataResolveError();

        return result;
    }

    public boolean has(TDataHolder<?> holder) {
        return this.get(holder) != null;
    }

    public <T extends TData<T>> void ifAttached(TDataHolder<T> holder, Consumer<T> consumer) {
        T t = this.get(holder);

        if (t == null)
            return;

        consumer.accept(t);
    }

    public <T extends TData<T>, R> R ifAttachedOr(TDataHolder<T> holder, Function<T, R> func, Supplier<R> supplier) {
        T t = this.get(holder);

        if (t == null)
            return supplier.get();

        return func.apply(t);
    }

    public <T extends TData<T>, R> R ifHasOrElse(TDataHolder<T> holder, Function<T, R> func, R res) {
        return ifAttachedOr(holder, func, () -> res);
    }

    public void forEachData(Consumer<TData<?>> consumer) {
        for (TData<?> data : this.data) {
            consumer.accept(data);
        }
    }
}
