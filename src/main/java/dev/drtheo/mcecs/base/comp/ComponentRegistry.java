package dev.drtheo.mcecs.base.comp;

public interface ComponentRegistry {

    int getId(Class<? extends Component<?>> clazz);

    void register(Class<? extends Component<?>> component);
}
