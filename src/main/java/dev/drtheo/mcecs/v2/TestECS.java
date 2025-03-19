package dev.drtheo.mcecs.v2;

import dev.drtheo.mcecs.*;
import dev.drtheo.mcecs.event.MEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class TestECS {

    static final int ENTITIES_MAX = 100_000;
    static final int COMP_MAX = 1_000;

    static final ComponentRegistry registry = new ComponentRegistry() {
        final Map<Class<? extends MComponent<?>>, Integer> map = new HashMap<>();

        @Override
        public int getId(Class<? extends MComponent<?>> clazz) {
            return map.computeIfAbsent(clazz, c -> map.size());
        }

        @Override
        public void register(Class<? extends MComponent<?>> component) {

        }
    };

    static final ComponentData comps = new ComponentData(ENTITIES_MAX, COMP_MAX);

    static class TestComp extends MComponent<TestComp> {

        private final int iter;

        public TestComp(int iter) {
            this.iter = iter;
        }

        public CompUid<TestComp> getUid() {
            return new CompUid<>(iter);
        }
    }

    public static void main(String[] args) {
        Identifier id = new Identifier("e", "e");

        CompUid<TestComp>[] compIds = new CompUid[COMP_MAX];
        TestComp[] comps = new TestComp[COMP_MAX];

        for (int i = 0; i < COMP_MAX; i++) {
            var comp = new TestComp(i);

            compIds[i] = comp.getUid();
            comps[i] = comp;
        }

        BenchmarkEntity[] entitys = new BenchmarkEntity[ENTITIES_MAX];

        long start = System.nanoTime();
        for (int i = 0; i < ENTITIES_MAX; i++) {
            var entity = new BenchmarkEntity(i);
            entitys[i] = entity;

            for (int j = 0; j < COMP_MAX; j++) {
                addComp(entity, comps[j]);
            }
        }

        System.out.println("addComp: avg " + (System.nanoTime() - start) / (ENTITIES_MAX * COMP_MAX) + "ns/op");
        start = System.nanoTime();

        for (int i = 0; i < ENTITIES_MAX; i++) {
            var entity = entitys[i];

            for (int j = 0; j < COMP_MAX; j++) {
                getComp(entity, compIds[j]);
            }
        }

        System.out.println("getComp: avg " + (System.nanoTime() - start) / (ENTITIES_MAX * COMP_MAX) + "ns/op");

        EventBus bus = EventBus.INSTANCE;
        start = System.nanoTime();

        BenchmarkSystem system = new BenchmarkSystem();

        System.out.println("subscribe: " + (System.nanoTime() - start) + "ns/op");
        start = System.nanoTime();

        for (int i = 0; i < ENTITIES_MAX; i++) {
            bus.raise(TestECS.comps, new BenchmarkEvent());
        }

        System.out.println("raise: avg " + (System.nanoTime() - start) / ENTITIES_MAX + "ns/op");
    }

    static class BenchmarkEntity implements MEntity {

        private final int index;

        public BenchmarkEntity(int idx) {
            this.index = idx;
        }

        @Override
        public Iterable<MComponent<?>> getComponents() {
            return null;
        }

        @Override
        public <C extends MComponent<C>> C getComponent(CompUid<C> component) {
            return null;
        }

        @Override
        public void addComponent(MComponent<?> component) {

        }

        @Override
        public int index() {
            return index;
        }
    }

    static class BenchmarkSystem extends MSystem {

        protected BenchmarkSystem() {
            super(Identifier.of("benchmark", "benchmark"));

            EventBus.INSTANCE.subscribe(BenchmarkEvent.class, new CompUid<TestComp>(0), this::onEvent);
        }

        public void onEvent(TestComp comp, BenchmarkEvent event) {
            System.out.print(1);
        }

        @Override
        public Type type() {
            return null;
        }
    }

    static class BenchmarkEvent implements MEvent<BenchmarkEvent> {
    }

    static <C extends MComponent<C>> C getComp(MEntity entity, CompUid<C> compUid) {
        return comps.getComp(registry, entity, compUid);
    }

    static void addComp(MEntity entity, MComponent<?> comp) {
        comps.addComp(registry, entity, comp);
    }

    static final Int2ObjectMap<Int2ObjectMap<MComponent<?>>> compsMap = new Int2ObjectOpenHashMap<>();

    static <C extends MComponent<C>> C getComp1(MEntity entity, CompUid<C> compUid) {
        return (C) compsMap.get(compUid.get(registry)).get(entity.index());
    }

    static void addComp1(MEntity entity, MComponent<?> comp) {
        compsMap.computeIfAbsent(comp.getUid().get(registry), integer -> new Int2ObjectOpenHashMap<>())
                        .put(entity.index(), comp);
    }
}
