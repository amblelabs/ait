package dev.amble.lib.register.unlockable;

import java.util.Optional;

import dev.amble.ait.data.Loyalty;
import dev.amble.lib.api.Identifiable;

public interface Unlockable extends Identifiable {
    UnlockType unlockType();

    Optional<Loyalty> requirement();

    /**
     * Decides whether this desktop should be auto-unlocked on creation. aka -
     * freebee, freeby
     */
    default boolean freebie() {
        return this.requirement().isEmpty();
    }

    enum UnlockType {
        EXTERIOR, CONSOLE, SONIC, DESKTOP, DIMENSION
    }
}
