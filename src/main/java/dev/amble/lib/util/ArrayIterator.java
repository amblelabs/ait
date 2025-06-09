package dev.amble.lib.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterator<T> implements Iterator<T> {

    private final T[] array;
    private int index;

    public ArrayIterator(T[] array) {
        this.array = array;
        this.index = 0;
    }

    @Override
    public boolean hasNext() {
        return index < array.length;
    }

    @Override
    public T next() {
        if (!this.hasNext()) throw new NoSuchElementException();

        return array[index++];
    }
}