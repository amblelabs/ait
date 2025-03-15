package dev.amble.ait.api;

public interface ArtronHolder {
    double getCurrentFuel();

    void setCurrentFuel(double var);

    default double addFuel(double var) {
        double previousFuel = this.getCurrentFuel();
        double toAdd = var + previousFuel;

        if (toAdd >= this.getMaxFuel()) {
            this.setCurrentFuel(this.getMaxFuel());
            return toAdd - this.getMaxFuel();
        }

        this.setCurrentFuel(toAdd);
        return 0;
    }

    default void removeFuel(double var) {
        double toRemove = this.getCurrentFuel() - var;

        if (toRemove < 0) {
            this.setCurrentFuel(0);
            return;
        }

        this.setCurrentFuel(toRemove);
    }

    double getMaxFuel();

    default boolean isOutOfFuel() {
        return this.getCurrentFuel() <= 0;
    }
    default boolean isFull() {
        return this.getCurrentFuel() >= this.getMaxFuel();
    }
}
