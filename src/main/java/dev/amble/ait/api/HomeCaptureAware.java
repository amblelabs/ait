package dev.amble.ait.api;

/** Per-entity state used to prevent deliberately ejected items from being recaptured. */
public interface HomeCaptureAware {

    boolean ait$isHomeCaptureExcluded();

    void ait$setHomeCaptureExcluded(boolean excluded);
}
