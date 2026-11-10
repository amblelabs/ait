package dev.amble.ait.api;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import net.minecraft.inventory.Inventory;

public interface SiegeInventoryProvider {

    Collection<? extends Inventory> ait$getSiegeInventories();

    void ait$registerSiegeInventory(Inventory inventory);

    void ait$unregisterSiegeInventory(Inventory inventory);

    Set<UUID> ait$getTrackedSiegeItems();

    void ait$setTrackedSiegeItems(Set<UUID> tardisIds);
}
