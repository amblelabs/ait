package dev.drtheo.mcecs.impl;

import dev.drtheo.mcecs.base.EEntity;
import dev.drtheo.mcecs.base.comp.Component;
import dev.drtheo.mcecs.base.data.ComponentData;

import java.util.ArrayList;

public class EntityManager extends ArrayList<EEntity> {

    public static final EntityManager INSTANCE = new EntityManager();

    public EEntity allocate() {
        EEntity result = new MEntity(this.size());
        this.add(result);

        return result;
    }

    public static EEntity create(Component<?>... components) {
        EEntity result = INSTANCE.allocate();

        for (Component<?> component : components) {
            ComponentManager.addComp(result, component);
        }

        return result;
    }
}
