package dev.drtheo.mcecs;

import dev.drtheo.mcecs.impl.MEventBus;
import dev.drtheo.mcecs.impl.MSystem;
import dev.drtheo.mcecs.impl.client.ClientTickEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class MCECSClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MCECSUtil.collectAndRegister(MSystem.Type.CLIENT);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            MEventBus.INSTANCE.raiseGlobal(ClientTickEvent.INSTANCE);
        });
    }
}
