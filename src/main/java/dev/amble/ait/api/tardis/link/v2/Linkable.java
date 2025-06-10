package dev.amble.ait.api.tardis.link.v2;

import java.util.UUID;

import dev.amble.ait.api.tardis.link.v2.block.InteriorLinkableBlockEntity;
import dev.amble.ait.core.tardis.Tardis;

public interface Linkable {

    void link(Tardis tardis);

    void link(UUID id);

    TardisRef tardis();

    default boolean isLinked() {
        return this.tardis() != null && this.tardis().isPresent();
    }

    /**
     * @implNote This method is called when the TardisRef instance gets created. This
     *           means that the ref is no longer null BUT the TARDIS instance still
     *           could be missing. Primarily this is true for
     *           {@link InteriorLinkableBlockEntity}s
     */
    default void onLinked() {
    }
}
