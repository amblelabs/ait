package dev.amble.ait.core.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.server.world.ServerWorld;

public class WorldSaveEvent {

    public static final Event<Save> EVENT = EventFactory.createArrayBacked(Save.class, callbacks -> world -> {
        for (Save callback : callbacks) {
            callback.onWorldSave(world);
        }
    });

    @FunctionalInterface
    public interface Save {
        void onWorldSave(ServerWorld world);
    }
}
