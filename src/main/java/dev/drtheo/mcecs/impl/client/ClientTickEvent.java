package dev.drtheo.mcecs.impl.client;

import dev.drtheo.mcecs.base.event.GlobalEvent;
import dev.drtheo.mcecs.impl.MEventBus;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public record ClientTickEvent() implements GlobalEvent<ClientTickEvent> {

    public static final ClientTickEvent INSTANCE = new ClientTickEvent();
}
