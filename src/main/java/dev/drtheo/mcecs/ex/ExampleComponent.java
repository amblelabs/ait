package dev.drtheo.mcecs.ex;

import dev.drtheo.mcecs.base.comp.CompUid;
import dev.drtheo.mcecs.base.comp.Component;

public class ExampleComponent extends Component<ExampleComponent> {

    public static final CompUid<ExampleComponent> ID = new CompUid<>(ExampleComponent.class);

    @Override
    public CompUid<ExampleComponent> getUid() {
        return ID;
    }
}
