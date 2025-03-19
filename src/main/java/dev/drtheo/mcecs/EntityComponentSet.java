package dev.drtheo.mcecs;

// map<entity id, component>
public class EntityComponentSet extends SparseSet {

    protected final MComponent<?>[] components;

    public EntityComponentSet(int maxEntities) {
        super(maxEntities);

        this.components = new MComponent[maxEntities];
    }

    public void add(int entityId, MComponent<?> component) {
        if (!canAdd(entityId))
            return;

        components[size] = component;
        super.add(entityId);
    }

    public MComponent<?> get(int entityId) {
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
