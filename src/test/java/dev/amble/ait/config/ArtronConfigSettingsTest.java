package dev.amble.ait.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ArtronConfigSettingsTest {

    @Test
    void preservesCurrentDefaults() {
        assertEquals(2000, ArtronConfigSettings.DEFAULT_RIFT_CHUNK_MIN_ARTRON);
        assertEquals(4000, ArtronConfigSettings.DEFAULT_RIFT_CHUNK_MAX_ARTRON);
        assertEquals(1, ArtronConfigSettings.DEFAULT_RIFT_CHUNK_REGEN_PER_SECOND);
        assertEquals(140, ArtronConfigSettings.DEFAULT_TARDIS_AMBIENT_REFUEL_PER_SECOND);
        assertEquals(40, ArtronConfigSettings.DEFAULT_TARDIS_RIFT_REFUEL_BONUS_PER_SECOND);
    }

    @Test
    void keepsValidAndEqualBounds() {
        assertEquals(new ArtronConfigSettings.Bounds(2000, 4000),
                ArtronConfigSettings.normalizeBounds(2000, 4000));
        assertEquals(new ArtronConfigSettings.Bounds(3000, 3000),
                ArtronConfigSettings.normalizeBounds(3000, 3000));
    }

    @Test
    void ordersReversedBounds() {
        assertEquals(new ArtronConfigSettings.Bounds(2000, 4000),
                ArtronConfigSettings.normalizeBounds(4000, 2000));
    }

    @Test
    void clampsNegativeBoundsBeforeOrdering() {
        assertEquals(new ArtronConfigSettings.Bounds(0, 50),
                ArtronConfigSettings.normalizeBounds(50, -10));
        assertEquals(new ArtronConfigSettings.Bounds(0, 0),
                ArtronConfigSettings.normalizeBounds(-20, -10));
    }

    @Test
    void sanitizesRates() {
        assertEquals(2.5, ArtronConfigSettings.normalizeRate(2.5, 1));
        assertEquals(0, ArtronConfigSettings.normalizeRate(-2.5, 1));
        assertEquals(1, ArtronConfigSettings.normalizeRate(Double.NaN, 1));
        assertEquals(1, ArtronConfigSettings.normalizeRate(Double.POSITIVE_INFINITY, 1));
    }
}
