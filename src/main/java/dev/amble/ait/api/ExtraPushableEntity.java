package dev.amble.ait.api;

import net.fabricmc.fabric.api.util.TriState;

public interface ExtraPushableEntity {
    void ait$setPushBehaviour(TriState pushable);
    TriState ait$pushBehaviour();
}
