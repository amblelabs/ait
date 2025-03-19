package dev.drtheo.mcecs;

import dev.drtheo.mcecs.event.MEvent;

public interface EventRegistry {

    int getIdOrRegister(Class<? extends MEvent<?>> event);

    int getId(Class<? extends MEvent<?>> clazz);

    void register(Class<? extends MEvent<?>> component);
}
