package dev.amble.ait.core.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashReport;

public class ServerCrashEvent {
    public static final Event<Crash> EVENT = EventFactory.createArrayBacked(Crash.class,
            callbacks -> (server, report) -> {
                for (Crash callback : callbacks) {
                    callback.onServerCrash(server, report);
                }
            });

    @FunctionalInterface
    public interface Crash {
        void onServerCrash(MinecraftServer server, CrashReport report);
    }
}
