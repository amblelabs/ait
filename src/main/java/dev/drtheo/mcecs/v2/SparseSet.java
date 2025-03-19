package dev.drtheo.mcecs.v2;

import dev.drtheo.mcecs.MComponent;

public class SparseSet {
    protected final int[] sparse;
    protected final int[] dense;
    protected int size;

    protected final int max;

    public SparseSet(int maxEntities) {
        this.max = maxEntities;

        sparse = new int[maxEntities];
        dense = new int[maxEntities];
        size = 0;

        for (int i = 0; i < maxEntities; i++) {
            sparse[i] = maxEntities - 1;
        }
    }

    protected int canGet(int entityId) {
        int i = sparse[entityId];
        return i < size && i != max - 1 ? i : -1;
    }

    public int getDense(int entityId) {
        int i = canGet(entityId);
        return i == -1 ? i : dense[i];
    }

    protected boolean canAdd(int entityId) {
        int i = sparse[entityId];
        return i < size && i != max - 1;
    }

    public void add(int entityId) {
        // Add to dense array
        this.dense[size] = entityId;
        sparse[entityId] = size;

        size++;
    }

    protected int canRemove(int entityId) {
        if (entityId > size)
            return -1;

        int denseIndex = sparse[entityId];
        return denseIndex != max - 1 ? denseIndex : -1; // no component
    }

    public void remove(int entityId, int denseIndex) {
        // Swap with the last element in the dense array
        int lastEntityId = dense[size - 1];
        dense[denseIndex] = lastEntityId;
        sparse[lastEntityId] = denseIndex;

        // Clear the last element
        dense[size - 1] = -1;
        sparse[entityId] = max - 1; // Reset to the reserved index
        size--;
    }

    public int size() {
        return size;
    }
}
