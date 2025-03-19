package dev.drtheo.mcecs;

import dev.amble.ait.data.enummap.Ordered;

public abstract class MComponent<S extends MComponent<S>> implements Ordered {

    @Override
    public int index() {
        return this.getUid().getCached();
    }

    public abstract CompUid<S> getUid();
}
