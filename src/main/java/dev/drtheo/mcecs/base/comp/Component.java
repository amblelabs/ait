package dev.drtheo.mcecs.base.comp;

import dev.amble.ait.data.enummap.Ordered;
import dev.drtheo.mcecs.base.EEntity;
import org.jetbrains.annotations.Nullable;

public abstract class Component<S extends Component<S>> implements Ordered {

    private EEntity entity = null;

    @Override
    public int index() {
        return this.getUid().getCached();
    }

    @Nullable
    public EEntity getOwner() {
        return entity;
    }

    public void setOwner(EEntity entity) {
        this.entity = entity;
    }

    public abstract CompUid<S> getUid();
}
