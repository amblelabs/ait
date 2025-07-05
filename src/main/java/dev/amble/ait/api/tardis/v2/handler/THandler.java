package dev.amble.ait.api.tardis.v2.handler;

import dev.amble.ait.api.tardis.v2.event.TEvent;
import dev.amble.ait.api.tardis.v2.event.TEvents;
import dev.amble.ait.api.tardis.v2.event.TEventsRegistry;

public interface THandler {

    /**
     * @implNote Never changes value, just a marker/stub value for final fields.
     * @return Always {@code null}.
     */
    default <T> T handler() {
        return null;
    }

    /**
     * Redirects to {@link TEventsRegistry#handle(TEvent)}
     */
    default <T extends TEvents> void handle(TEvent<T> event) {
        TEventsRegistry.handle(event);
    }
}
