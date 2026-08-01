package dev.amble.ait.config;

public final class ArtronConfigSettings {

    public static final int DEFAULT_RIFT_CHUNK_MIN_ARTRON = 2000;
    public static final int DEFAULT_RIFT_CHUNK_MAX_ARTRON = 4000;
    public static final double DEFAULT_RIFT_CHUNK_REGEN_PER_SECOND = 1;
    public static final double DEFAULT_TARDIS_AMBIENT_REFUEL_PER_SECOND = 140;
    public static final double DEFAULT_TARDIS_RIFT_REFUEL_BONUS_PER_SECOND = 40;

    private ArtronConfigSettings() {
    }

    public static Bounds normalizeBounds(int minimum, int maximum) {
        int first = Math.max(minimum, 0);
        int second = Math.max(maximum, 0);

        return new Bounds(Math.min(first, second), Math.max(first, second));
    }

    public static double normalizeRate(double rate, double fallback) {
        return Double.isFinite(rate) ? Math.max(rate, 0) : fallback;
    }

    /**
     * Keeps a preferred operational threshold reachable for stores whose configured capacity is
     * lower than the normal threshold.
     */
    public static double clampThresholdToCapacity(double threshold, double capacity) {
        double normalizedThreshold = normalizeRate(threshold, 0);
        double normalizedCapacity = normalizeRate(capacity, 0);

        return Math.min(normalizedThreshold, normalizedCapacity);
    }

    public record Bounds(int minimum, int maximum) {
    }
}