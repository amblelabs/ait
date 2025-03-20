package dev.drtheo.mcecs.base.data;

import dev.drtheo.mcecs.base.comp.Component;

// map<entity id, component>
public class EntityComponentSet extends SparseSet {

    protected final Component<?>[] components;

    public EntityComponentSet(int maxEntities) {
        super(maxEntities);

        this.components = new Component[maxEntities];
    }

    public void add(int entityId, Component<?> component) {
        if (!canAdd(entityId))
            return;

        components[size] = component;
        super.add(entityId);
    }

    public Component<?> get(int entityId) {
        int i = canGet(entityId);
        return i == -1 ? null : components[i];
    }

    public void remove(int entityId) {
        int dense = canRemove(entityId);

        if (dense == -1)
            return;

        components[dense] = components[size - 1];
        components[size - 1] = null;

        super.remove(entityId, dense);
    }
}
