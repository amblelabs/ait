package dev.amble.ait.tardis.v2;

import dev.amble.ait.api.tardis.v2.event.TEvents;
import dev.amble.ait.api.tardis.v2.event.TEventsRegistry;
import dev.amble.ait.api.tardis.v2.handler.THandler;
import dev.amble.ait.api.tardis.v2.handler.THandlerRegistry;
import dev.amble.ait.tardis.v2.data.*;
import dev.amble.ait.tardis.v2.event.ServerEvents;
import dev.amble.ait.tardis.v2.event.TardisEvents;
import dev.amble.ait.tardis.v2.handler.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

/**
 * @implNote All the fields here are non-final and can be {@code null},
 * if called too early. This is to prevent mishaps and other loading order mishaps.
 */
public class AITHandlers {

    public static DesktopHandler DESKTOP;
    public static FuelHandler FUEL;
    public static SonicHandler SONIC;
    public static RepairHandler REPAIR;
    public static TravelHandler TRAVEL;

    public static void init() {
        TEventsRegistry.register(ServerEvents.HOLDER);
        TEventsRegistry.register(TardisEvents.HOLDER);
        TEventsRegistry.freeze();

        DESKTOP = register(new DesktopHandler());
        FUEL = register(new FuelHandler());
        register(new HadsHandler());
        register(new HailMaryHandler());
        SONIC = register(new SonicHandler());
        REPAIR = register(new RepairHandler());
        TRAVEL = register(new TravelHandler());

        THandlerRegistry.freeze();

        ServerTickEvents.START_SERVER_TICK.register(server ->
                TEvents.handle(ServerEvents.StartServerTick.get(server)));

        ServerTickEvents.END_SERVER_TICK.register(server ->
                TEvents.handle(ServerEvents.EndServerTick.get(server)));
    }

    private static void attachDefault(Tardis tardis) {
        tardis.attach(new AlarmData());
        tardis.attach(new DesktopData());
        tardis.attach(new TravelData());
    }

    public static void attachGrowth(Tardis tardis) {
        attachDefault(tardis);
        tardis.attach(new GrowthData());
    }

    public static void deattachGrowth(Tardis tardis) {
        tardis.deattach(GrowthData.ID);
    }

    public static void attachNormal(Tardis tardis) {
        attachDefault(tardis);
        tardis.attach(new FuelData());
        tardis.attach(new FuelData());
        tardis.attach(new RepairData());
        tardis.attach(new SiegeData());;
    }

    private static <T extends THandler> T register(T t) {
        THandlerRegistry.register(t);
        return t;
    }
}
