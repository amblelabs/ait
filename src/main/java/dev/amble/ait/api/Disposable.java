package dev.amble.ait.api;

public interface Disposable {

    default void age() {
    }

    void dispose();

    default boolean isAged() {
        return false;
    }
}
