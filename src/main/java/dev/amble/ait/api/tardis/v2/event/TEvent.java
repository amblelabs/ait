package dev.amble.ait.api.tardis.v2.event;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.v2.data.DataResolveError;

import java.util.function.Consumer;

public interface TEvent<T extends TEvents> {

    TEvents.Holder<T> handler();
    void handle(T handler);

    default boolean debug() {
        return false;
    }

    default void handleAll(Iterable<T> subscribed) {
        for (T handler : subscribed) {
            try {
                this.handle(handler);
            } catch (DataResolveError e) {
                if (debug())
                    AITMod.LOGGER.debug("Failed to resolve data", e);
            } catch (Throwable e) {
                AITMod.LOGGER.warn("Failed to handle event '{} for handler {}", this.getClass(), handler.getClass());
            }
        }
    }

    record Impl<T extends TEvents>(
            TEvents.Holder<T> handler,
            Consumer<T> consumer
    ) implements TEvent<T> {

        @Override
        public void handle(T handler) {
            consumer.accept(handler);
        }
    }

    static <T extends TEvents> Impl<T> make(TEvents.Holder<T> handler, Consumer<T> consumer) {
        return new Impl<>(handler, consumer);
    }

    static <T extends TEvents> void apply(TEvents.Holder<T> handler, Consumer<T> consumer) {
        TEventsRegistry.handle(new Impl<>(handler, consumer));
    }
}
