package dev.amble.ait.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class AITRegistryEvents {
    /**
     * @deprecated You can just statically register to the registry instead now
     * ExteriorVariantRegistry#register(Identifier, ExteriorVariant)
     */
    @Deprecated(forRemoval = true)
    public static final Event<Defaults> EXTERIOR_DEFAULTS = EventFactory.createArrayBacked(Defaults.class, callbacks -> () -> {
        for (Defaults callback : callbacks) {
            callback.defaults();
        }
    });

    /**
     * Called when a Registries is initialised
     */
    @FunctionalInterface
    public interface Defaults {
        void defaults();
    }
}
