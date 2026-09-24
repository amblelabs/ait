package dev.amble.ait.api;

import java.util.Map;
import java.util.UUID;

/** Internal ownership metadata retained when dropped item entities merge. */
public interface TemporalItemOwnership {
    boolean ait$hasTemporalOwners();

    Map<UUID, Integer> ait$getTemporalOwners();

    void ait$setTemporalOwners(Map<UUID, Integer> owners);

    void ait$markTemporalMerge();

    void ait$setTemporalMergeInProgress(boolean merging);

    boolean ait$isTemporalMergeInProgress();

    void ait$suppressTemporalDestruction();

    boolean ait$recordTemporalDestruction();
}
