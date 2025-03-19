package dev.drtheo.mcecs.base.comp;

public interface ComponentRegistry {

    int getId(Class<? extends MComponent<?>> clazz);

    void register(Class<? extends MComponent<?>> component);
}
