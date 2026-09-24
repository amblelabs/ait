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
        Tardis tardis = getTardisForFluid();
        if (tardis == null) return;

        double before = level();
        tardis.fuel().setCurrentFuel(level);
        onChange(before, level());
    }

    @Override
    default double extractLevel(double requested) {
        if (!Double.isFinite(requested) || requested <= 0)
            return 0;

        Tardis tardis = getTardisForFluid();
        if (tardis == null)
            return 0;

        double before = level();
        if (!Double.isFinite(before) || before <= 0)
            return 0;

        // The fuel handler may promote emergency power while the primary tank reaches zero.
        // Delegate the accounting to it, while keeping a network source limited to the amount
        // that was visible through level() when the transaction began.
        double extracted = tardis.fuel().extractFuel(Math.min(requested, before));
        onChange(before, level());

        return Double.isFinite(extracted) ? Math.max(0, Math.min(extracted, Math.min(requested, before))) : 0;
    }

    @Override
    default double maxLevel() {
        if (getTardisForFluid() == null) return 0;

        return getTardisForFluid().fuel().getMaxFuel();
    }
}
