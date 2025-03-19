package dev.drtheo.mcecs.v2;

import dev.drtheo.mcecs.CompUid;
import dev.drtheo.mcecs.MComponent;
import dev.drtheo.mcecs.event.MEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBus {

    public static final EventBus INSTANCE = new EventBus();

    private final Map<Class<? extends MEvent<?>>, List<EventTarget<?, ?, ?>>> registered = new HashMap<>();

    // map<component id, list<reventtarget>>

    public <E extends MEvent<E>, C extends MComponent<C>, T extends MEvent.Type<C, E>> void subscribe(Class<E> event, CompUid<C> comp, T type) {
        registered.computeIfAbsent(event, k -> new ArrayList<>())
                .add(new EventTarget<>(comp, type));
    }

    public void raise(ComponentData data, MEvent<?> event) {
        List<EventTarget<?, ?, ?>> targets = registered.get(event.getClass());

        if (targets == null)
            return;

        for (EntityComponentSet set : data) {
            for (int i = 0; i < set.size; i++) {
                MComponent<?> component = set.components[i];
                for (EventTarget<?, ?, ?> target : targets) {
                    target.tryRun(event, component);
                }
            }
        }
    }

    record EventTarget<E extends MEvent<E>, C extends MComponent<C>, T extends MEvent.Type<C, E>>(CompUid<C> comp, T type) {

        public void tryRun(MEvent<?> event, MComponent<?> comp) {
            if (this.comp.index() != comp.index())
                return;

            this.type.handle((C) comp, (E) event);
        }
    }
}
