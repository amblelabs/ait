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

    public record Bounds(int minimum, int maximum) {
    }
}
