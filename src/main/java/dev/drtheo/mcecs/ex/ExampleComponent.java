package dev.drtheo.mcecs.ex;

import dev.drtheo.mcecs.base.comp.CompUid;
import dev.drtheo.mcecs.base.comp.MComponent;

public class ExampleComponent extends MComponent<ExampleComponent> {

    public static final CompUid<ExampleComponent> ID = new CompUid<>(ExampleComponent.class);

    @Override
    public CompUid<ExampleComponent> getUid() {
        return ID;
    }
}
