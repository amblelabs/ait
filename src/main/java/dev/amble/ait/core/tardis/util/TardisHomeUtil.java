package dev.amble.ait.core.tardis.util;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.lib.data.CachedDirectedGlobalPos;

/** Shared queries for a TARDIS's configured home location. */
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

    public static boolean isAtExactHome(Tardis tardis) {
        return tardis != null && sameLocation(tardis.travel().position(), tardis.stats().getHome());
    }

    public static boolean isParkedAtExactHome(Tardis tardis) {
        return tardis != null && tardis.travel().isLanded() && !tardis.flight().isFlying()
                && !tardis.flight().falling().get() && isAtExactHome(tardis);
    }

    public static int homeRadius() {
        return Math.max(1, AITMod.CONFIG.homeRadius);
    }
}
