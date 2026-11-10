package dev.amble.ait.api;

public interface ArtronHolder {
    double getCurrentFuel();

    void setCurrentFuel(double var);

    /**
     * Inserts as much of {@code amount} as possible.
     *
     * @return the amount that did not fit
     */
    default double addFuel(double amount) {
        double requested = sanitizeAmount(amount);
        double maximum = sanitizeCapacity(this.getMaxFuel());
        double stored = this.getCurrentFuel();
        double current = clampFuel(stored, maximum);

        if (Double.compare(stored, current) != 0)
            this.setCurrentFuel(current);

        double accepted = Math.min(requested, maximum - current);
        if (accepted > 0)
            this.setCurrentFuel(current + accepted);

        return requested - accepted;
    }

    /**
     * Inserts as much of {@code amount} as possible.
     *
     * @return the amount actually inserted
     */
    default double insertFuel(double amount) {
        double requested = sanitizeAmount(amount);
        double remainder = clampFuel(this.addFuel(requested), requested);

        return requested - remainder;
    }

    /**
     * Extracts up to {@code amount}.
     *
     * @return the amount actually extracted
     */
    default double extractFuel(double amount) {
        double requested = sanitizeAmount(amount);
        double maximum = sanitizeCapacity(this.getMaxFuel());
        double stored = this.getCurrentFuel();
        double current = clampFuel(stored, maximum);

        if (Double.compare(stored, current) != 0)
            this.setCurrentFuel(current);

        double extracted = Math.min(requested, current);
        if (extracted > 0)
            this.setCurrentFuel(current - extracted);

        return extracted;
    }

    /**
     * Compatibility wrapper for callers compiled against the original void method.
     */
    default void removeFuel(double amount) {
        this.extractFuel(amount);
    }

    double getMaxFuel();

    default boolean isOutOfFuel() {
        return this.getCurrentFuel() <= 0;
    }
    default boolean isFull() {
        return this.getCurrentFuel() >= this.getMaxFuel();
    }

    private static double sanitizeAmount(double amount) {
        return Double.isFinite(amount) && amount > 0 ? amount : 0;
    }

    private static double sanitizeCapacity(double capacity) {
        return Double.isFinite(capacity) && capacity > 0 ? capacity : 0;
    }

    private static double clampFuel(double amount, double maximum) {
        if (!Double.isFinite(amount) || amount <= 0)
            return 0;

        return Math.min(amount, maximum);
    }
}
