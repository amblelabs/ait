package dev.drtheo.mcecs;

import dev.drtheo.mcecs.base.system.MSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class MCECSClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            MCECSUtil.collectAndRegister(MSystem.Type.CLIENT);
        });
    }
}
