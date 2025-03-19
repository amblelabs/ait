package dev.drtheo.mcecs.v2;

import dev.drtheo.mcecs.MComponent;

public class WorldData {
    private final int[] sparse;
    private final int[] dense;
    private final MComponent<?>[] components;
    private int size;

    private final int max;

    public WorldData(int maxEntities) {
        this.max = maxEntities;

        sparse = new int[maxEntities];
        dense = new int[maxEntities];
        components = new MComponent<?>[maxEntities];
        size = 0;

        for (int i = 0; i < maxEntities; i++) {
            sparse[i] = maxEntities - 1;
        }
    }

    public void add(int entityId, MComponent<?> component) {
        int i = sparse[entityId];

        if (i >= size || i == max - 1)
            return;

        // Add to dense array
        dense[size] = entityId;
        components[size] = component;
        sparse[entityId] = size;
        size++;
    }

    public MComponent<?> get(int entityId) {
        int i = sparse[entityId];
        return i < size && i != max - 1 ? components[i] : null;
    }

    public void remove(int entityId) {
        if (entityId > size)
            return;

        int denseIndex = sparse[entityId];

        if (denseIndex == max - 1)
            return; // no component

        // Swap with the last element in the dense array
        int lastEntityId = dense[size - 1];
        dense[denseIndex] = lastEntityId;
        components[denseIndex] = components[size - 1];
        sparse[lastEntityId] = denseIndex;

        // Clear the last element
        dense[size - 1] = -1;
        components[size - 1] = null;
        sparse[entityId] = max - 1; // Reset to the reserved index
        size--;
    }

    public int size() {
        return size;
    }

    public MComponent<?> getComponent(int denseIndex) {
        return components[denseIndex];
    }
}
