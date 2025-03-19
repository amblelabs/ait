package dev.drtheo.mcecs.event;

import dev.amble.ait.data.enummap.Ordered;
import dev.drtheo.mcecs.ComponentRegistry;
import dev.drtheo.mcecs.EventRegistry;

public class EventUid<C extends MEvent<C>> implements Ordered {

    private final Class<C> comp;
    private int id;

    public EventUid(Class<C> comp) {
        this.comp = comp;
    }

    public int get(EventRegistry registry) {
        if (this.id != -1)
            return this.id;

        this.id = registry.getIdOrRegister(this.comp);
        return this.id;
    }

    public int getCached() {
        return this.id;
    }

    @Override
    public int index() {
        return id;
    }
}
