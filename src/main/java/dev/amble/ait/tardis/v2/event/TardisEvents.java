package dev.amble.ait.tardis.v2.event;

import dev.amble.ait.api.tardis.v2.event.TEvent;
import dev.amble.ait.api.tardis.v2.event.TEvents;
import dev.amble.ait.tardis.v2.Tardis;
import dev.amble.ait.tardis.v2.data.RepairData;
import dev.amble.ait.tardis.v2.handler.AlarmHandler;
import dev.amble.lib.data.Mutable;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

// TODO: split into FuelEvents, TravelEvents, etc for performance!
public interface TardisEvents extends TEvents {

    Holder<TardisEvents> HOLDER = new Holder<>(TardisEvents.class);

    default void event$outOfFuel(Tardis tardis) { }
    default void event$backupPowerUsed(Tardis tardis, double fuel) { }

    default void event$disablePower(Tardis tardis) { }
    default void event$enablePower(Tardis tardis) { }

    default void event$crash(Tardis tardis, int power) { }

    default void event$alarmToll(Tardis tardis, @Nullable AlarmHandler.Alarm alarm) { }

    default void event$repaired(Tardis tardis) { }

    /**
     * @return How many repair ticks to decrement by.
     */
    default int event$repairTick(Tardis tardis, MinecraftServer server, RepairData repair) {
        return 0;
    }

    interface TardisEvents$Event extends TEvent.Notify<TardisEvents> {

        @Override
        default Holder<TardisEvents> handler() {
            return HOLDER;
        }
    }

    record AbstractTardisEvent(Tardis tardis, BiConsumer<TardisEvents, Tardis> func) implements TardisEvents$Event {

        @Override
        public void handle(TardisEvents handler) {
            this.func.accept(handler, tardis);
        }
    }

    static AbstractTardisEvent outOfFuel(Tardis tardis) {
        return new AbstractTardisEvent(tardis, TardisEvents::event$outOfFuel);
    }

    static AbstractTardisEvent backupPowerUsed(Tardis tardis, double fuel) {
        return new AbstractTardisEvent(tardis, (events, t) -> events.event$backupPowerUsed(t, fuel));
    }

    static AbstractTardisEvent disablePower(Tardis tardis) {
        return new AbstractTardisEvent(tardis, TardisEvents::event$disablePower);
    }

    static AbstractTardisEvent enablePower(Tardis tardis) {
        return new AbstractTardisEvent(tardis, TardisEvents::event$enablePower);
    }

    static AbstractTardisEvent crash(Tardis tardis, int power) {
        return new AbstractTardisEvent(tardis, (events, t) -> events.event$crash(t, power));
    }

    static AbstractTardisEvent alarmToll(Tardis tardis, @Nullable AlarmHandler.Alarm alarm) {
        return new AbstractTardisEvent(tardis, (events, t) -> events.event$alarmToll(t, alarm));
    }

    static AbstractTardisEvent repaired(Tardis tardis) {
        return new AbstractTardisEvent(tardis, TardisEvents::event$repaired);
    }

    record RepairTickEvent(Mutable.Int res, Tardis tardis, MinecraftServer server, RepairData repair)
            implements TEvent.Result<TardisEvents, Integer> {

        public RepairTickEvent(Tardis tardis, MinecraftServer server, RepairData repair) {
            this(new Mutable.Int(), tardis, server, repair);
        }

        @Override
        public Integer result() {
            return res.value;
        }

        @Override
        public BaseHolder<TardisEvents> handler() {
            return TardisEvents.HOLDER;
        }

        @Override
        public void handleAll(Iterable<TardisEvents> subscribed) {
            for (TardisEvents handler : subscribed) {
                TEvent.handleSilent(this, handler, () ->
                        handler.event$repairTick(tardis, server, repair));
            }
        }
    }

    static RepairTickEvent repairTick(Tardis tardis, @NotNull MinecraftServer server, @NotNull RepairData repair) {
        return new RepairTickEvent(tardis, server, repair);
    }
}
