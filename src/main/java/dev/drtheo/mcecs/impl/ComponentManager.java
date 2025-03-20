package dev.drtheo.mcecs.impl;

import dev.drtheo.mcecs.base.EEntity;
import dev.drtheo.mcecs.base.comp.Component;
import dev.drtheo.mcecs.base.comp.ComponentRegistry;
import dev.drtheo.mcecs.base.data.ComponentData;

public class ComponentManager extends ComponentData {

    public static final ComponentManager INSTANCE = new ComponentManager(2048, 128);

    public ComponentManager(int maxEntities, int maxComponents) {
        super(maxEntities, maxComponents);
    }

    public static void addComp(EEntity entity, Component<?> comp) {
        INSTANCE.addComp(MComponentRegistry.INSTANCE, entity, comp);
    }
}
