package dev.amble.ait.tardis.v2.handler;

import dev.amble.ait.api.tardis.v2.handler.THandler;
import dev.amble.ait.tardis.v2.Tardis;
import dev.amble.ait.tardis.v2.event.TardisEvents;

public class HadsHandler implements THandler, TardisEvents {

    @Override
    public void event$disablePower(Tardis tardis) {
        HadsData hads = tardis.resolve(HadsData.ID);
        hads.enabled().set(false);
    }
}
