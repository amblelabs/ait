package dev.drtheo.mcecs;

import dev.amble.ait.data.enummap.Ordered;

import java.util.Optional;

public interface MEntity extends Ordered {

    Iterable<MComponent<?>> getComponents();

    default <C extends MComponent<C>> Optional<C> tryGetComponent(CompUid<C> component) {
        return Optional.ofNullable(getComponent(component));
    }

    <C extends MComponent<C>> C getComponent(CompUid<C> component);

    void addComponent(MComponent<?> component);
}
