package dev.drtheo.mcecs;

// map<component id, lambda>
public class ComponentEventTypeSet extends SparseSet {

    protected final MEvent.Type<?, ?>[] types;

    public ComponentEventTypeSet(int maxEntities) {
        super(maxEntities);

        this.types = new MEvent.Type[maxEntities];
    }

    public void add(int compId, MEvent.Type<?, ?> type) {
        if (!canAdd(compId))
            return;

        types[size] = type;
        super.add(compId);
    }

    public MEvent.Type<?, ?> get(int compId) {
        int i = canGet(compId);
        return i == -1 ? null : types[i];
    }

    public void remove(int compId) {
        int dense = canRemove(compId);

        if (dense == -1)
            return;

        types[dense] = types[size - 1];
        types[size - 1] = null;

        super.remove(compId, dense);
    }
}
