package dev.amble.ait.tardis.v2.handler;

import dev.amble.ait.api.tardis.v2.handler.THandler;
import dev.amble.ait.tardis.v2.Tardis;
import dev.amble.ait.tardis.v2.event.TardisEvents;

public class HailMaryHandler implements THandler, TardisEvents {

    @Override
    public void event$disablePower(Tardis tardis) {
        HailMaryData hailMary = tardis.resolve(HailMary.ID);
        return hailMary.enabled().set(false);
    }
}
