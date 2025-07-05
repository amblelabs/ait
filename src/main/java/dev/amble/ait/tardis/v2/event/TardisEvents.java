package dev.amble.ait.tardis.v2.event;

import dev.amble.ait.api.tardis.v2.event.TEvent;
import dev.amble.ait.api.tardis.v2.event.TEvents;
import dev.amble.ait.tardis.v2.Tardis;

import java.util.function.BiConsumer;

public interface TardisEvents extends TEvents {

    Holder<TardisEvents> HOLDER = new Holder<>(TardisEvents.class);

    default void event$outOfFuel(Tardis tardis) { }
    default void event$backupPowerUsed(Tardis tardis, double fuel) { }

    default void event$disablePower(Tardis tardis) { }
    default void event$enablePower(Tardis tardis) { }

    interface TardisEvents$Event extends TEvent<TardisEvents> {

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
}
