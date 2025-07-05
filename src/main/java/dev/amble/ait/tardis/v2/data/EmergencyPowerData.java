package dev.amble.ait.tardis.v2.data;

import dev.amble.ait.api.ArtronHolder;
import dev.amble.ait.api.tardis.v2.data.TData;
import dev.amble.ait.api.tardis.v2.data.TDataHolder;

public class EmergencyPowerData implements TData<EmergencyPowerData>, ArtronHolder {

    public static final TDataHolder<EmergencyPowerData> ID = null;

    @Override
    public double getCurrentFuel() {
        return 0;
    }

    @Override
    public void setCurrentFuel(double var) {

    }

    @Override
    public double getMaxFuel() {
        return 0;
    }

    public boolean isFull() {

    }

    @Override
    public TDataHolder<EmergencyPowerData> holder() {
        return null;
    }

    @Override
    public void dispose() {

    }
}
