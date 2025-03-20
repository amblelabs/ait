package dev.drtheo.mcecs.base.event;

import dev.drtheo.mcecs.base.comp.CompUid;
import dev.drtheo.mcecs.base.comp.Component;
import dev.drtheo.mcecs.base.data.ComponentData;
import dev.drtheo.mcecs.base.data.EntityComponentSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBus {

    // map<class<event>, map<component id, lambda>>
    private final Map<Class<? extends LocalEvent<?>>, List<LocalEventTarget<?, ?, ?>>> local = new HashMap<>();

    private final Map<Class<? extends GlobalEvent<?>>, List<GlobalEventTarget<?, ?>>> global = new HashMap<>();

    // map<component id, map<class<event>, list<types>>
    //private final Int2ObjectMap<Map<Class<? extends MEvent<?>>, List<MEvent.Type<?,?>>>> map = new Int2ObjectOpenHashMap<>();

    // map<component id, map<entity id, component>>

    public <E extends LocalEvent<E>, C extends Component<C>, T extends LocalEvent.Type<C, E>> void subscribeLocal(Class<E> event, CompUid<C> comp, T type) {
        local.computeIfAbsent(event, k -> new ArrayList<>())
                .add(new LocalEventTarget<>(comp, type));
    }

    public <E extends GlobalEvent<E>, T extends GlobalEvent.Type<E>> void subscribeGlobal(Class<E> event, T type) {
        global.computeIfAbsent(event, k -> new ArrayList<>())
                .add(new GlobalEventTarget<>(type));
    }

    public void raiseLocal(ComponentData data, int entityId, LocalEvent<?> event) {
        List<LocalEventTarget<?, ?, ?>> targets = local.get(event.getClass());

        for (LocalEventTarget<?, ?, ?> target : targets) {
            EntityComponentSet comps = data.get(target.comp.index());
            Component<?> comp = comps.get(entityId);

            target.tryRun(event, comp);
        }
    }

    public void raiseGlobal(GlobalEvent<?> event) {
        List<GlobalEventTarget<?, ?>> targets = global.get(event.getClass());

        for (GlobalEventTarget<?, ?> target : targets) {
            target.tryRun(event);
        }
    }

    record LocalEventTarget<E extends LocalEvent<E>, C extends Component<C>, T extends LocalEvent.Type<C, E>>(CompUid<C> comp, T type) {

        public void tryRun(LocalEvent<?> event, Component<?> comp) {
            if (comp == null || this.comp.index() != comp.index())
                return;

            this.type.handle((C) comp, (E) event);
        }
    }

    record GlobalEventTarget<E extends GlobalEvent<E>, T extends GlobalEvent.Type<E>>(T type) {

        public void tryRun(GlobalEvent<?> event) {
            this.type.handle((E) event);
        }
    }
}
