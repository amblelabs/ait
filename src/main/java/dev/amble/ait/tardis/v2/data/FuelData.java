package dev.amble.ait.tardis.v2.data;

import dev.amble.ait.api.tardis.v2.data.TData;
import dev.amble.ait.api.tardis.v2.data.TDataHolder;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;
import dev.amble.ait.data.properties.dbl.DoubleProperty;
import dev.amble.ait.data.properties.dbl.DoubleValue;

public class FuelData implements TData<FuelData> {

    public static final TDataHolder<FuelData> ID = null;

    public static final double MAX_FUEL = 50000;

    private static final DoubleProperty FUEL = new DoubleProperty("fuel", 1000d);
    private static final BoolProperty REFUELING = new BoolProperty("refueling", false);
    private static final BoolProperty POWER = new BoolProperty("power", false);

    private final DoubleValue fuel = FUEL.create(this);
    private final BoolValue refueling = REFUELING.create(this);
    private final BoolValue power = POWER.create(this);

    public DoubleValue fuel() {
        return fuel;
    }

    public BoolValue refueling() {
        return refueling;
    }

    public BoolValue power() {
        return power;
    }
}
