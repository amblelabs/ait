package dev.amble.ait.core.engine.link;

public interface IFluidSource extends IFluidLink {
    @Override
    default IFluidSource source(boolean search) {
        return this;
    }

    @Override
    default IFluidLink last() {
        return this;
    }

    double level();

    /**
     * Changes the stored level.
     *
     * @implSpec Historically this method was required to invoke {@link #onChange(double, double)}.
     * Existing implementations keep that behaviour by default. A silent setter must override
     * {@link #setLevelNotifies()} to return {@code false} so the common transfer helpers can
     * provide the missing notification without double-firing legacy addon callbacks.
     */
    void setLevel(double level);

    /**
     * @return whether {@link #setLevel(double)} already invokes the zero-crossing callback
     */
    default boolean setLevelNotifies() {
        return true;
    }

    default void setLevelAndNotify(double level) {
        double before = level();
        double maximum = maxLevel();
        if (!Double.isFinite(maximum) || maximum < 0) maximum = 0;

        double normalized;
        if (level == Double.POSITIVE_INFINITY) {
            normalized = maximum;
        } else if (!Double.isFinite(level)) {
            normalized = 0;
        } else {
            normalized = Math.max(0, Math.min(level, maximum));
        }

        setLevel(normalized);

        if (!setLevelNotifies())
            onChange(before, level());
    }

    /**
     * Inserts as much of {@code requested} as this source can accept.
     *
     * @return the amount that was actually accepted
     */
    default double insertLevel(double requested) {
        if (!Double.isFinite(requested) || requested <= 0) return 0;

        double before = level();
        if (!Double.isFinite(before)) return 0;

        double remainingCapacity = Math.max(0, maxLevel() - before);
        if (!Double.isFinite(remainingCapacity) || remainingCapacity <= 0) return 0;

        double toInsert = Math.min(requested, remainingCapacity);
        setLevelAndNotify(before + toInsert);

        return Math.min(requested, Math.max(0, level() - before));
    }

    /**
     * Extracts as much of {@code requested} as this source currently contains.
     *
     * @return the amount that was actually extracted
     */
    default double extractLevel(double requested) {
        if (!Double.isFinite(requested) || requested <= 0) return 0;

        double before = level();
        if (!Double.isFinite(before)) return 0;

        double toExtract = Math.min(requested, Math.max(0, before));
        if (toExtract <= 0) return 0;

        setLevelAndNotify(before - toExtract);

        return Math.min(requested, Math.max(0, before - level()));
    }

    default void addLevel(double amount) {
        insertLevel(amount);
    }

    default void removeLevel(double amount) {
        extractLevel(amount);
    }

    /**
     * Called after a source applies its effective value. Implementations that preserve the
     * historic notifying-setter contract call this from {@link #setLevel(double)}; silent
     * implementations opt out through {@link #setLevelNotifies()} and let
     * {@link #setLevelAndNotify(double)} call it.
     */
    default void onChange(double before, double after) {
        boolean wasPowered = before > 0;
        boolean isPowered = after > 0;

        if (!wasPowered && isPowered) {
            onGainFluid();
        } else if (wasPowered && !isPowered) {
            onLoseFluid();
        }
    }

    double maxLevel();

    default boolean isLevelFull() {
        return level() >= maxLevel();
    }
}
