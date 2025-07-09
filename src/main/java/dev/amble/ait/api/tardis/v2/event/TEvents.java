package dev.amble.ait.api.tardis.v2.event;

import dev.amble.ait.api.tardis.v2.handler.THandler;

import java.util.ArrayList;
import java.util.List;

public interface TEvents {

    static <T extends TEvents> void handle(TEvent<T> event) {
        event.handler().handle(event);
    }

    static <T extends TEvents, R> R handle(TEvent.Result<T, R> event) {
        event.handler().handle(event);
        return event.result();
    }

    interface BaseHolder<T extends TEvents> {
        boolean isApplicable(THandler handler);

        void subscribe(THandler handler);
        void handle(TEvent<T> event);
    }

    record Holder<T extends TEvents>(Class<T> clazz, List<T> handlers) implements BaseHolder<T> {

        public Holder(Class<T> clazz) {
            this(clazz, new ArrayList<>());
        }

        @Override
        @SuppressWarnings("unchecked")
        public void subscribe(THandler handler) {
            if (!this.isApplicable(handler))
                throw new IllegalArgumentException("you're crazy");

            handlers.add((T) handler);
        }

        @Override
        public void handle(TEvent<T> event) {
            event.handleAll(handlers);
        }

        @Override
        public boolean isApplicable(THandler handler) {
            return clazz.isInstance(handler);
        }
    }
}
