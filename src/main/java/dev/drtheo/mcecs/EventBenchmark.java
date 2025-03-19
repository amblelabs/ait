package dev.drtheo.mcecs;

import dev.drtheo.mcecs.event.EventUid;
import dev.drtheo.mcecs.event.MEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBenchmark {

    static class Comp1 extends MComponent<Comp1> {
        public static final CompUid<Comp1> ID = new CompUid<>(Comp1.class);

        @Override
        public CompUid<Comp1> getUid() {
            return ID;
        }
    }

    static class Comp2 extends MComponent<Comp2> {
        public static final CompUid<Comp2> ID = new CompUid<>(Comp2.class);

        @Override
        public CompUid<Comp2> getUid() {
            return ID;
        }
    }

    static class Comp3 extends MComponent<Comp3> {
        public static final CompUid<Comp3> ID = new CompUid<>(Comp3.class);

        @Override
        public CompUid<Comp3> getUid() {
            return ID;
        }
    }

    static class Comp4 extends MComponent<Comp4> {
        public static final CompUid<Comp4> ID = new CompUid<>(Comp4.class);

        @Override
        public CompUid<Comp4> getUid() {
            return ID;
        }
    }

    // map<class<component>, map<entity, component>>

    // map<class<component>, component>

    static ComponentRegistry compRegistry = new ComponentRegistry() {

        private final Map<Class<? extends MComponent>, Integer> map = new HashMap<>();

        @Override
        public int getId(Class<? extends MComponent<?>> clazz) {
            return map.get(clazz);
        }

        @Override
        public void register(Class<? extends MComponent<?>> component) {
            this.map.put(component, this.map.size());
        }
    };

    static EventRegistry eventRegistry = new EventRegistry() {

        private final Map<Class<? extends MEvent<?>>, Integer> map = new HashMap<>();

        @Override
        public int getIdOrRegister(Class<? extends MEvent<?>> event) {
            return map.computeIfAbsent(event, c -> map.size());
        }

        @Override
        public int getId(Class<? extends MEvent<?>> clazz) {
            return -1;
        }

        @Override
        public void register(Class<? extends MEvent<?>> component) {

        }
    };

    public static void main(String[] args) {

        compRegistry.register(Comp1.class);
        compRegistry.register(Comp2.class);
        compRegistry.register(Comp3.class);
        compRegistry.register(Comp4.class);

        eventRegistry.register(BenchmarkEvent.class);

        MEntity entity = new MEntity() {
            private final SparseSet<MComponent<?>> components = new SparseSet<>(2048, 2048, MComponent<?>[]::new);

            @Override
            public Iterable<MComponent<?>> getComponents() {
                return List.of(components.values());
            }

            @Override
            public <C extends MComponent<C>> C getComponent(CompUid<C> component) {
                return (C) components.get(component.get(compRegistry));
            }

            @Override
            public int index() {
                return 0;
            }

            @Override
            public void addComponent(MComponent<?> component) {
                component.getUid().get(compRegistry);

                components.add(component);
            }
        };

        BenchmarkSystem system = new BenchmarkSystem(0);

        //EventBus.INSTANCE.raise(entity, new BenchmarkEvent(123));
    }

    record EventTarget<C extends MComponent<C>, E extends MEvent<E>, T extends MEvent.Type<C, E>>(CompUid<C> compClass, T type) {

        public void tryInvoke(MEntity entity, E event) {
            entity.tryGetComponent(compClass).ifPresent(comp -> this.type.handle(comp, event));
        }
    }

    static class BenchmarkSystem extends MSharedSystem {
        protected BenchmarkSystem(int id) {
            super(Identifier.of("benchmark", String.valueOf(id)));

            //this.subscribeLocalEvent(Comp1.class, BenchmarkEvent.class, this::handleBenchmarkEvent);
        }

        @Override
        public Type type() {
            return null;
        }

        public void handleBenchmarkEvent(Comp1 component, BenchmarkEvent event) {
            System.out.println("hello from comp1 " + event.iter);
        }
    }

    record BenchmarkEvent(int iter) implements MEvent<BenchmarkEvent> {

        public static final EventUid<BenchmarkEvent> ID = new EventUid<>(BenchmarkEvent.class);

        @Override
        public EventUid<BenchmarkEvent> getUid() {
            return ID;
        }
    }
}
