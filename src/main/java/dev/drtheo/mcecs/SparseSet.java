package dev.drtheo.mcecs;

import dev.amble.ait.data.enummap.Ordered;

import java.util.function.Function;

// for future, read: https://skypjack.github.io/2020-08-02-ecs-baf-part-9/
public class SparseSet<T extends Ordered> {

    private final int[] sparse; // array to store the index of elements in dense array
    private final T[] dense; // array to store the actual elements
    private final int capacity; // maximum capacity of the set
    private final int maxValue; // maximum value that can be stored in the set
    private int n; // number of elements in the set

    // Constructor to create an SSet object with given max value and capacity
    public SparseSet(int maxV, int cap, Function<Integer, T[]> f) {
        sparse = new int[maxV + 1]; // create a sparse array with max value + 1
        dense = f.apply(cap); // create a dense array with the given capacity
        capacity = cap;
        maxValue = maxV;
        n = 0; // initially the set is empty

        System.out.println(sparse[0]);
    }

    public boolean contains(T x) {
        return x.index() < maxValue && sparse[x.index()] < n && dense[sparse[x.index()]] == x;
    }

    public T get(int x) {
        return dense[sparse[x]];
    }

    // Search for an element in the set and return its index
    public int indexOf(T x) {
        return contains(x) ? sparse[x.index()] : -1;
    }

    // Insert an element into the set
    public void add(T x) {
        // check if the element is out of range or the set is full or
        // the element already exists in the set
        if (n >= capacity)
            throw new IllegalStateException("n >= capacity");

        if (x.index() > maxValue - 1 || indexOf(x) != -1)
            return;

        // add the element to the end of the dense array
        dense[n] = x;

        // update the index of the element in the sparse array
        sparse[x.index()] = n;

        n++; // increment the size of the set
    }

    // Delete an element from the set
    public void remove(T x) {
        int index = indexOf(x); // find the index of the element

        // check if the element exists in the set
        if (index == -1) {
            return; // if not, do nothing and return
        }

        // swap the element with the last element in the dense array
        T temp = dense[n - 1];
        dense[index] = temp;
        sparse[temp.index()] = index;
        n--; // decrement the size of the set
    }

    // Print the elements in the set
    public void printSet() {
        // print the elements in the dense array
        for (int i = 0; i < n; i++) {
            System.out.print(dense[i] + " "); // print the elements in the dense array
        }
        System.out.println();
    }

    public T[] values() {
        return dense;
    }
}
