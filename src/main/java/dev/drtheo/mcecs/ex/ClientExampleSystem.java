package dev.drtheo.mcecs.ex;

import dev.drtheo.mcecs.impl.MEventBus;
import dev.drtheo.mcecs.impl.client.ClientTickEvent;

public class ClientExampleSystem extends SharedExampleSystem {

    public ClientExampleSystem() {
        super();

        MEventBus.INSTANCE.subscribeGlobal(ClientTickEvent.class, this::onClientTick);
    }

    private void onClientTick(ClientTickEvent event) {
        System.out.print("client tick");
    }

    @Override
    public Type type() {
        return Type.CLIENT;
    }
}
