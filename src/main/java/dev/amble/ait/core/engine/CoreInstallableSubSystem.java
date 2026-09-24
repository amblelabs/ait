package dev.amble.ait.core.engine;

import dev.amble.ait.core.engine.block.generic.GenericStructureSystemBlockEntity;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Optional lifecycle hooks for subsystems installed in a generic subsystem core.
 */
public interface CoreInstallableSubSystem {

    default boolean canInstall(GenericStructureSystemBlockEntity core, ServerPlayerEntity player, ItemStack stack) {
        return true;
    }

    default void prepareInstall(GenericStructureSystemBlockEntity core, ServerPlayerEntity player, ItemStack stack) {
    }

    default void onInstalled(GenericStructureSystemBlockEntity core, ServerPlayerEntity player, ItemStack stack) {
    }

    default void onRemoved(GenericStructureSystemBlockEntity core, ItemStack stack) {
    }

    /**
     * Reconciles persistent per-core state after the containing block entity has
     * been loaded and linked back to its TARDIS.
     */
    default void onCoreLoaded(GenericStructureSystemBlockEntity core) {
    }

    /**
     * Allows a subsystem with multiple physical installations to aggregate
     * their power state instead of using the legacy single global toggle.
     */
    default boolean managesCorePowerState() {
        return false;
    }

    default void onCorePowerChanged(GenericStructureSystemBlockEntity core, boolean powered) {
    }
}
