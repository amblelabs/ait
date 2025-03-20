package dev.drtheo.mcecs.base.event;

public interface GlobalEvent<E extends GlobalEvent<E>> extends Event {

    default <T extends Type<E>> void accept(T t) {
        t.handle((E) this);
    }

    interface Type<E extends GlobalEvent<E>> {
        void handle(E event);

        default E conformEvent(LocalEvent<?> event) {
            return (E) event;
        }
    }
}
