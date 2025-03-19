package dev.drtheo.mcecs.event;

import dev.amble.ait.data.enummap.Ordered;
import dev.drtheo.mcecs.MComponent;

public interface MEvent<E extends MEvent<E>> extends Ordered {

    EventUid<E> getUid();

    @Override
    default int index() {
        return getUid().getCached();
    }

    default <C extends MComponent<C>, T extends MEvent.Type<C, E>> void accept(C component, T t) {
        t.handle(component, (E) this);
    }

    interface Type<C extends MComponent<C>, E extends MEvent<E>> {
        void handle(C component, E event);
    }
}
