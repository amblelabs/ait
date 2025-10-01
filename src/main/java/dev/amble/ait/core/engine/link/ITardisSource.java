package dev.amble.ait.core.engine.link;

import dev.amble.ait.core.tardis.Tardis;

public interface ITardisSource extends IFluidSource {
    Tardis getTardisForFluid();

    @Override
    default double level() {
        if (getTardisForFluid() == null) return 0;

        return getTardisForFluid().fuel().getCurrentFuel();
    }

    @Override
    default void setLevel(double level) {
        if (getTardisForFluid() == null) return;

        double before = level();
        getTardisForFluid().fuel().setCurrentFuel(level);
        onChange(before, level);
    }

    @Override
    default double max() {
        if (getTardisForFluid() == null) return 0;

        return getTardisForFluid().fuel().getMaxFuel();
    }
}
