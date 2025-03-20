package dev.drtheo.mcecs.impl;

import dev.drtheo.mcecs.base.comp.CompUid;
import dev.drtheo.mcecs.base.comp.Component;
import dev.drtheo.mcecs.base.event.GlobalEvent;
import dev.drtheo.mcecs.base.event.LocalEvent;
import dev.drtheo.mcecs.base.system.System;
import net.minecraft.util.Identifier;

public abstract class MSystem implements System {

    private final Identifier id;

    protected MSystem(Identifier id) {
        this.id = id;
    }

    protected  <E extends LocalEvent<E>, C extends Component<C>, T extends LocalEvent.Type<C, E>> void subscribeLocal(Class<E> event, CompUid<C> comp, T type) {
        MEventBus.INSTANCE.subscribeLocal(event, comp, type);
    }

    protected <E extends GlobalEvent<E>, T extends GlobalEvent.Type<E>> void subscribeGlobal(Class<E> event, T type) {
        MEventBus.INSTANCE.subscribeGlobal(event, type);
    }

    public void init() {

    }

    public void onLoad() {

    }

    public Identifier id() {
        return id;
    }

    public abstract Type type();

    public enum Type {
        CLIENT,
        SERVER,
    }
}
