package dev.drtheo.mcecs;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.util.ArrayList;
import java.util.List;

public class MCECSClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            MCECSUtil.collectAndRegister(MSystem.Type.CLIENT);
        });
    }
}
