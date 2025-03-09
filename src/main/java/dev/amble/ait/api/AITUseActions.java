package dev.amble.ait.api;

import net.minecraft.util.UseAction;

public interface AITUseActions {
    UseAction SONIC = ((AITUseActions) (Object) UseAction.NONE).ait$sonic();
    UseAction ait$sonic();
}
