package dev.amble.ait.api.link;

import java.util.Optional;

import dev.amble.ait.core.tardis.Tardis;

/**
 * @deprecated Use {@link dev.amble.ait.api.link.v2.Linkable} instead.
 */
@Deprecated(since = "1.2.0", forRemoval = true)
public interface Linkable {
    Optional<Tardis> findTardis(boolean isClient);

    void setTardis(Tardis tardis);
}
