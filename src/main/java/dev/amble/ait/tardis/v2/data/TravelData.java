package dev.amble.ait.tardis.v2.data;

import dev.amble.ait.api.tardis.v2.data.TData;
import dev.amble.ait.api.tardis.v2.data.TDataHolder;
import dev.amble.ait.api.tardis.v2.data.properties.Value;
import dev.amble.ait.api.tardis.v2.data.properties.bool.BoolValue;
import dev.amble.ait.api.tardis.v2.data.properties.integer.IntValue;
import dev.amble.lib.data.CachedDirectedGlobalPos;

public class TravelData implements TData<TravelData> {

    public static final TDataHolder<TravelData> ID = null;

    public IntValue instability() {

    }

    public IntValue speed() {

    }

    public Value<CachedDirectedGlobalPos> position() {

    }

    public BoolValue antigravs() {

    }

    public Value<State> state() {

    }

    public BoolValue crashing() {

    }

    public IntValue hammerUses() {

    }

    public enum State {
        LANDED,
        DEMAT,
        FLIGHT,
        MAT
    }
}
