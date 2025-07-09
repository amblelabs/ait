package dev.amble.ait.api.tardis.v2.event;

import dev.amble.ait.api.tardis.v2.data.DataResolveError;

import java.util.function.Supplier;

public interface TEvent<T extends TEvents> {

    TEvents.BaseHolder<T> handler();

    void handleAll(Iterable<T> subscribed);

    static void handleSilent(TEvent<?> event, TEvents handler, Runnable r) {
        try {
            r.run();
        } catch (DataResolveError ignored) {
            // ignored
        } catch (Throwable e) {
            System.err.println("Failed to handle event '" + event.getClass() + " for handler " + handler.getClass());
        }
    }

    static <R> R handleSilent(TEvent<?> event, TEvents handler, Supplier<R> s, Supplier<R> def) {
        try {
            return s.get();
        } catch (DataResolveError ignored) {
            // ignored
        } catch (Throwable e) {
            System.err.println("Failed to handle event '" + event.getClass() + " for handler " + handler.getClass());
        }

        return def.get();
    }

    static <R> R handleSilent(TEvent<?> event, TEvents handler, Supplier<R> s, R def) {
        try {
            return s.get();
        } catch (DataResolveError ignored) {
            // ignored
        } catch (Throwable e) {
            System.err.println("Failed to handle event '" + event.getClass() + " for handler " + handler.getClass());
        }

        return def;
    }

    interface Notify<T extends TEvents> extends TEvent<T> {
        void handle(T handler) throws DataResolveError;

        @Override
        default void handleAll(Iterable<T> subscribed) {
            for (T handler : subscribed) {
                handleSilent(this, handler, () -> this.handle(handler));
            }
        }
    }

    interface Result<T extends TEvents, R> extends TEvent<T> {
        R result();
    }
}
