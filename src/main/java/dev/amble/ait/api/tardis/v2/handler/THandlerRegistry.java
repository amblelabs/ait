package dev.amble.ait.api.tardis.v2.handler;

import dev.amble.ait.api.tardis.v2.event.TEvents;
import dev.amble.ait.api.tardis.v2.event.TEventsRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class THandlerRegistry {

    private static boolean frozen;
    private static final Set<THandler> handlers = Collections.newSetFromMap(new IdentityHashMap<>());

    public static void register(THandler handler) {
        if (frozen)
            throw new IllegalStateException("Already frozen");

        if (!TEventsRegistry.isFrozen())
            throw new IllegalStateException("Events registry was not frozen yet!");

        handlers.add(handler);
        buildEvents(handler);
    }

    public static void freeze() {
        frozen = true;

        Map<Class<?>, THandler> lookup = new HashMap<>();

        for (THandler handler : handlers) {
            lookup.put(handler.getClass(), handler);
        }

        for (THandler handler : handlers) {
            Class<? extends THandler> clazz = handler.getClass();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                if (!Modifier.isFinal(field.getModifiers()))
                    continue;

                Resolve resolve = field.getAnnotation(Resolve.class);

                if (resolve == null)
                    continue;

                Class<?> target = field.getType();

                try {
                    field.set(handler, lookup.get(target));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static void buildEvents(THandler handler) {
        for (TEvents.Holder<?> holder : TEventsRegistry.registered()) {
            if (!holder.isApplicable(handler))
                continue;

            holder.subscribe(handler);
        }
    }
}
