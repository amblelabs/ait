package dev.drtheo.mcecs;

public interface ComponentRegistry {

    int getId(Class<? extends MComponent<?>> clazz);

    void register(Class<? extends MComponent<?>> component);
}
