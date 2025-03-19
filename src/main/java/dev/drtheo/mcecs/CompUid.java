package dev.drtheo.mcecs;

import dev.amble.ait.data.enummap.Ordered;

public class CompUid<C extends MComponent<C>> implements Ordered {

    private final Class<C> comp;
    private int id = -1;

    public CompUid(Class<C> comp) {
        this.comp = comp;
    }

    public CompUid(int id) {
        this.id = id;
        this.comp = null;
    }

    public int get(ComponentRegistry registry) {
        if (this.id != -1)
            return this.id;

        this.id = registry.getId(this.comp);
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
