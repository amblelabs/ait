package dev.drtheo.mcecs;

public interface MEvent<E extends MEvent<E>> {

    default <C extends MComponent<C>, T extends MEvent.Type<C, E>> void accept(C component, T t) {
        t.handle(component, (E) this);
    }

    interface Type<C extends MComponent<C>, E extends MEvent<E>> {
        void handle(C component, E event);

        default C conformComp(MComponent<?> comp) {
            return (C) comp;
        }

        default E conformEvent(MEvent<?> event) {
            return (E) event;
        }
    }
}
