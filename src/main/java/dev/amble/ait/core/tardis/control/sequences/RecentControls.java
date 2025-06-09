package dev.amble.ait.core.tardis.control.sequences;

import dev.amble.ait.core.tardis.control.Control;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecentControls extends ArrayList<Control> {
    private final UUID tardisId;

    public RecentControls(UUID tardisId, List<Control> controls) {
        super(controls);
        this.tardisId = tardisId;
    }

    public RecentControls(UUID tardisId) {
        this(tardisId, new ArrayList<>());
    }
}
