package dev.drtheo.mcecs.base.comp;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class DynamicComponentRegistry implements ComponentRegistry {

    private final Object2IntMap<Class<? extends MComponent<?>>> map = new Object2IntOpenHashMap<>();

    @Override
    public int getId(Class<? extends MComponent<?>> clazz) {
        return map.computeIfAbsent(clazz, k -> map.size());
    }

    @Override
    public void register(Class<? extends MComponent<?>> component) {

    }
}
