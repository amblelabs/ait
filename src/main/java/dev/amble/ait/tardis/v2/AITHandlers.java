package dev.amble.ait.tardis.v2;

import dev.amble.ait.api.tardis.v2.event.TEventsRegistry;
import dev.amble.ait.api.tardis.v2.handler.THandlerRegistry;
import dev.amble.ait.tardis.v2.event.ServerEvents;
import dev.amble.ait.tardis.v2.handler.SonicHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

/**
 * @implNote All the fields here are non-final and can be {@code null},
 * if called too early. This is to prevent mishaps and other loading order mishaps.
 */
public class AITHandlers {

    public static SonicHandler SONIC;

    public static void init() {
        TEventsRegistry.register(ServerEvents.HOLDER);
        TEventsRegistry.freeze();

        SONIC = new SonicHandler();
        THandlerRegistry.register(SONIC);

        THandlerRegistry.freeze();

        ServerTickEvents.START_SERVER_TICK.register(server ->
                TEventsRegistry.handle(ServerEvents.StartServerTick.get(server)));

        ServerTickEvents.END_SERVER_TICK.register(server ->
                TEventsRegistry.handle(ServerEvents.EndServerTick.get(server)));
    }
}
