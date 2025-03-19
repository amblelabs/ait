package dev.drtheo.mcecs;

public interface EventRegistry {

    int getIdOrRegister(Class<? extends MEvent<?>> event);

    int getId(Class<? extends MEvent<?>> clazz);

    void register(Class<? extends MEvent<?>> component);
}
