package loqor.ait.core.engine.link;

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
    void setLevel(double level);

    /**
     * @implNote you will need to call this yourself in setLevel
     */
    default void onChange(double before, double after) {
        if (before == 0 && after > 0) {
            onGainFluid();
        } else if (before > 0 && after == 0) {
            onLoseFluid();
        }
    }

    double max();
    default boolean isFull() {
        return level() >= max();
    }
}
