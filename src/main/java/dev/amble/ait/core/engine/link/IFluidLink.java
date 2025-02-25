package dev.amble.ait.core.engine.link;

public interface IFluidLink {
    IFluidSource source(boolean search);
    void setSource(IFluidSource source);
    IFluidLink last();
    void setLast(IFluidLink last);

    /**
     * when this fluid source fully loses all of its fluid
     */
    default void onLoseFluid() {}

    /**
     * when this fluid source fully gains all of its fluid
     */
    default void onGainFluid() {}
}
