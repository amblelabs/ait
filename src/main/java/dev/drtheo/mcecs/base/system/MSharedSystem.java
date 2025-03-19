package dev.drtheo.mcecs.base.system;

import dev.drtheo.mcecs.base.comp.MComponent;
import dev.drtheo.mcecs.base.event.MEvent;
import net.minecraft.util.Identifier;

public abstract class MSharedSystem extends MSystem {

    protected MSharedSystem(Identifier id) {
        super(id);
    }

    public <C extends MComponent<C>, E extends MEvent<E>> void subscribeLocalEvent(Class<C> component, Class<E> event, MEvent.Type<C, E> handler) {
        //EventBus.INSTANCE.subscribe(event, component, handler);
    }
}
