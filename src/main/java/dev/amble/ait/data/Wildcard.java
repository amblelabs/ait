package dev.amble.ait.data;

public record Wildcard<T>(T get) {

    public static <T> Wildcard<T> of(T t) {
        return new Wildcard<>(t);
    }

    public static <E> Wildcard<E> wildcard() {
        return new Wildcard<>(null);
    }

    public boolean isEmpty() {
        return this.get == null;
    }

    public boolean isPresent() {
        return !this.isEmpty();
    }
}
