package dev.drtheo.mcecs.base.event;

import dev.drtheo.mcecs.base.comp.Component;

public interface LocalEvent<E extends LocalEvent<E>> extends Event {

    default <C extends Component<C>, T extends LocalEvent.Type<C, E>> void accept(C component, T t) {
        t.handle(component, (E) this);
    }

    interface Type<C extends Component<C>, E extends LocalEvent<E>> {
        void handle(C component, E event);

        default C conformComp(Component<?> comp) {
            return (C) comp;
        }

        default E conformEvent(LocalEvent<?> event) {
            return (E) event;
        }
    }
}
