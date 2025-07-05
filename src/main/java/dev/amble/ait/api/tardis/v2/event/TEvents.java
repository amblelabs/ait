package dev.amble.ait.api.tardis.v2.event;

import dev.amble.ait.api.tardis.v2.handler.THandler;

import java.util.ArrayList;
import java.util.List;

public interface TEvents {

    record Holder<T extends TEvents>(Class<T> clazz, List<T> handlers) {

        public Holder(Class<T> clazz) {
            this(clazz, new ArrayList<>());
        }

        @SuppressWarnings("unchecked")
        public void subscribe(THandler handler) {
            if (!this.isApplicable(handler))
                throw new IllegalArgumentException("you're crazy");

            handlers.add((T) handler);
        }

        public void handle(TEvent<T> event) {
            event.handleAll(handlers);
        }

        public boolean isApplicable(THandler handler) {
            return clazz.isInstance(handler);
        }
    }
}
