package dev.drtheo.mcecs.base.event;

import dev.drtheo.mcecs.base.comp.CompUid;
import dev.drtheo.mcecs.base.comp.MComponent;
import dev.drtheo.mcecs.base.data.ComponentData;
import dev.drtheo.mcecs.base.data.EntityComponentSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBus {

    public static final EventBus INSTANCE = new EventBus();

    // map<class<event>, map<component id, lambda>>
    private final Map<Class<? extends MEvent<?>>, List<EventTarget<?, ?, ?>>> registered = new HashMap<>();

    // map<component id, map<class<event>, list<types>>
    //private final Int2ObjectMap<Map<Class<? extends MEvent<?>>, List<MEvent.Type<?,?>>>> map = new Int2ObjectOpenHashMap<>();

    // map<component id, map<entity id, component>>

    public <E extends MEvent<E>, C extends MComponent<C>, T extends MEvent.Type<C, E>> void subscribe(Class<E> event, CompUid<C> comp, T type) {
        registered.computeIfAbsent(event, k -> new ArrayList<>())
                .add(new EventTarget<>(comp, type));
    }

    public void raise(ComponentData data, int entityId, MEvent<?> event) {
        List<EventTarget<?, ?, ?>> targets = registered.get(event.getClass());

        for (EventTarget<?, ?, ?> target : targets) {
            EntityComponentSet comps = data.get(target.comp.index());
            MComponent<?> comp = comps.get(entityId);

            target.tryRun(event, comp);
        }
    }

    private <C extends MComponent<C>, E extends MEvent<E>> void run(EntityComponentSet set, int entityId, MEvent<?> event, MEvent.Type<C, E> type) {
        type.handle(type.conformComp(set.get(entityId)), type.conformEvent(event));
    }

    private <C extends MComponent<C>, E extends MEvent<E>> void run(MComponent<?> comp, MEvent<?> event, MEvent.Type<C, E> type) {
        type.handle(type.conformComp(comp), type.conformEvent(event));
    }

    record EventTarget<E extends MEvent<E>, C extends MComponent<C>, T extends MEvent.Type<C, E>>(CompUid<C> comp, T type) {

        public void tryRun(MEvent<?> event, MComponent<?> comp) {
            if (comp == null || this.comp.index() != comp.index())
                return;

            this.type.handle((C) comp, (E) event);
        }
    }
}
