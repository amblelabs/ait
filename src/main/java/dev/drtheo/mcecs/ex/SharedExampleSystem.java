package dev.drtheo.mcecs.ex;

import dev.amble.ait.AITMod;
import dev.drtheo.mcecs.base.system.MSharedSystem;
import net.minecraft.util.Identifier;

public abstract class SharedExampleSystem extends MSharedSystem {

    private static final Identifier ID = AITMod.id("example");

    protected SharedExampleSystem() {
        super(ID);

        this.subscribeLocalEvent(ExampleComponent.class, ExampleEvent.class, this::onHelloWorld);
    }

    public void onHelloWorld(ExampleComponent component, ExampleEvent event) {
        System.out.print("Hello World!");
    }
}
