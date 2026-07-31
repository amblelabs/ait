package dev.amble.ait.mixin.server;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import dev.amble.ait.api.SiegeInventoryProvider;
import dev.amble.ait.core.item.SiegeInventoryUtil;
import dev.drtheo.scheduler.api.TimeUnit;
import dev.drtheo.scheduler.api.common.Scheduler;
import dev.drtheo.scheduler.api.common.TaskStage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.server.world.ServerWorld;

@Mixin(SimpleInventory.class)
public abstract class SimpleInventoryMixin {

    @Unique private static final int SIEGE_SCAN_COOLDOWN_TICKS = 20;

    @Unique private Set<Entity> ait$inventoryOwners;
    @Unique private boolean ait$siegeScanScheduled;
    @Unique private long ait$nextSiegeScan;

    @Inject(method = "addListener", at = @At("TAIL"))
    private void ait$registerInventoryOwner(InventoryChangedListener listener, CallbackInfo ci) {
        if (!(listener instanceof Entity entity) || !(entity instanceof SiegeInventoryProvider provider))
            return;

        if (this.ait$inventoryOwners == null)
            this.ait$inventoryOwners = Collections.newSetFromMap(new IdentityHashMap<>());
        this.ait$inventoryOwners.add(entity);
        provider.ait$registerSiegeInventory((SimpleInventory) (Object) this);
    }

    @Inject(method = "removeListener", at = @At("TAIL"))
    private void ait$unregisterInventoryOwner(InventoryChangedListener listener, CallbackInfo ci) {
        if (!(listener instanceof Entity entity) || !(entity instanceof SiegeInventoryProvider provider))
            return;

        if (this.ait$inventoryOwners != null) {
            this.ait$inventoryOwners.remove(entity);
            if (this.ait$inventoryOwners.isEmpty())
                this.ait$inventoryOwners = null;
        }
        provider.ait$unregisterSiegeInventory((SimpleInventory) (Object) this);
    }

    @Inject(method = "markDirty", at = @At("TAIL"))
    private void ait$trackChangedSiegeItems(CallbackInfo ci) {
        if (this.ait$inventoryOwners == null || this.ait$siegeScanScheduled)
            return;

        ServerWorld world = null;
        for (Entity owner : this.ait$inventoryOwners)
            if (owner != null && owner.getWorld() instanceof ServerWorld serverWorld) {
                world = serverWorld;
                break;
            }

        if (world == null)
            return;

        this.ait$siegeScanScheduled = true;
        ServerWorld scheduledWorld = world;
        long delay = Math.max(1, this.ait$nextSiegeScan - scheduledWorld.getTime());
        Scheduler.get().runTaskLater(() -> {
            this.ait$siegeScanScheduled = false;
            this.ait$nextSiegeScan = scheduledWorld.getTime() + SIEGE_SCAN_COOLDOWN_TICKS;
            if (this.ait$inventoryOwners == null)
                return;

            for (Entity owner : Set.copyOf(this.ait$inventoryOwners)) {
                if (owner != null && !owner.isRemoved() && owner.getWorld() == scheduledWorld)
                    SiegeInventoryUtil.track(owner);
            }
        }, TaskStage.startWorldTick(scheduledWorld), TimeUnit.TICKS, delay);
    }
}
