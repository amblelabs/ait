package dev.amble.ait.core.tardis.util;

import dev.amble.lib.data.CachedDirectedGlobalPos;

/** Shared comparisons for a TARDIS's configured home location. */
public final class TardisHomeUtil {

    private TardisHomeUtil() {
    }

    public static boolean sameLocation(CachedDirectedGlobalPos first, CachedDirectedGlobalPos second) {
        return first != null && second != null
                && first.getDimension() != null && second.getDimension() != null
                && first.getPos() != null && second.getPos() != null
                && first.getDimension().equals(second.getDimension())
                && first.getPos().equals(second.getPos());
    }

    public static boolean sameHome(CachedDirectedGlobalPos first, CachedDirectedGlobalPos second) {
        return sameLocation(first, second) && first.getRotation() == second.getRotation();
    }
}
