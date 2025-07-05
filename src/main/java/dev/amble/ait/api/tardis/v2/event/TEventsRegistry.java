package dev.amble.ait.api.tardis.v2.event;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class TEventsRegistry {

    private static boolean frozen;
    private static final Set<TEvents.Holder<?>> holders = Collections.newSetFromMap(new IdentityHashMap<>());

    public static void register(TEvents.Holder<?> events) {
        if (frozen)
            throw new IllegalStateException("Already frozen");

        holders.add(events);
    }

    public static Collection<TEvents.Holder<?>> registered() {
        return holders;
    }

    public static void freeze() {
        frozen = true;
    }

    public static boolean isFrozen() {
        return frozen;
    }

    public static <T extends TEvents> void handle(TEvent<T> event) {
        event.handler().handle(event);
    }
}
