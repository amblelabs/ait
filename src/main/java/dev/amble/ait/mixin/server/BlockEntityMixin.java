package dev.amble.ait.mixin.server;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import dev.amble.ait.core.item.SiegeInventoryUtil;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.drtheo.scheduler.api.TimeUnit;
import dev.drtheo.scheduler.api.common.Scheduler;
import dev.drtheo.scheduler.api.common.TaskStage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {

    @Unique private static final int SIEGE_SCAN_COOLDOWN_TICKS = 20;

    @Shadow @Nullable protected World world;

    @Shadow public abstract BlockPos getPos();

    @Unique private Set<UUID> ait$trackedSiegeItems = Set.of();
    @Unique private boolean ait$siegeScanScheduled;
    @Unique private long ait$nextSiegeScan;

    @Inject(method = "setWorld", at = @At("TAIL"))
    private void ait$trackLoadedSiegeItems(World world, CallbackInfo ci) {
        this.ait$scheduleSiegeScan();
    }

    @Inject(method = "markDirty", at = @At("TAIL"))
    private void ait$trackChangedSiegeItems(CallbackInfo ci) {
        this.ait$scheduleSiegeScan();
    }

    @Inject(method = "markRemoved", at = @At("HEAD"))
    private void ait$trackRemovedSiegeItems(CallbackInfo ci) {
        if (!this.ait$trackedSiegeItems.isEmpty())
            this.ait$trackSiegeItems();
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void ait$writeTrackedSiegeItems(NbtCompound nbt, CallbackInfo ci) {
        SiegeInventoryUtil.writeTrackedCarrierIds(nbt, this.ait$trackedSiegeItems);
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void ait$readTrackedSiegeItems(NbtCompound nbt, CallbackInfo ci) {
        this.ait$trackedSiegeItems = SiegeInventoryUtil.readTrackedCarrierIds(nbt);
    }

    @Unique private void ait$scheduleSiegeScan() {
        if (this.ait$siegeScanScheduled || !(this.world instanceof ServerWorld serverWorld)
                || !((Object) this instanceof Inventory))
            return;

        this.ait$siegeScanScheduled = true;
        BlockPos pos = this.getPos().toImmutable();
        long delay = this.ait$nextSiegeScan == 0
                ? 1 + Math.floorMod(pos.asLong(), SIEGE_SCAN_COOLDOWN_TICKS)
                : Math.max(1, this.ait$nextSiegeScan - serverWorld.getTime());
        Scheduler.get().runTaskLater(() -> {
            this.ait$siegeScanScheduled = false;
            this.ait$nextSiegeScan = serverWorld.getTime() + SIEGE_SCAN_COOLDOWN_TICKS;
            if (this.world == serverWorld && serverWorld.isChunkLoaded(pos)
                    && serverWorld.getBlockEntity(pos) == (Object) this)
                this.ait$trackSiegeItems();
        }, TaskStage.startWorldTick(serverWorld), TimeUnit.TICKS, delay);
    }

    @Unique private void ait$trackSiegeItems() {
        if (!(this.world instanceof ServerWorld serverWorld)
                || !((Object) this instanceof Inventory inventory))
            return;

        SiegeInventoryUtil.FindResult found = SiegeInventoryUtil.findResult(inventory);
        Set<UUID> current = found.tardisIds();
        ServerTardisManager manager = ServerTardisManager.getInstance();
        if (manager == null)
            return;

        for (UUID tardisId : current) {
            if (this.ait$trackedSiegeItems.contains(tardisId)) {
                var loaded = manager.getLoadedTardis(tardisId);
                if (loaded != null && loaded.siege().isActive()
                        && !loaded.returnHome().isTrackedSiegeItemContainer(serverWorld, this.getPos()))
                    loaded.returnHome().trackSiegeItemContainer(serverWorld, this.getPos());
                continue;
            }

            manager.getTardis(serverWorld.getServer(), tardisId, tardis -> {
                if (tardis.siege().isActive())
                    tardis.returnHome().trackSiegeItemContainer(serverWorld, this.getPos());
            });
        }

        Set<UUID> retained = new HashSet<>(current);
        if (!found.complete()) {
            retained.addAll(this.ait$trackedSiegeItems);
            for (UUID tardisId : retained) {
                if (current.contains(tardisId))
                    continue;
                manager.getTardis(serverWorld.getServer(), tardisId, tardis -> {
                    if (tardis.siege().isActive())
                        tardis.returnHome().trackSiegeItemContainer(serverWorld, this.getPos());
                });
            }
        } else {
            BlockPos pos = this.getPos().toImmutable();
            for (UUID tardisId : this.ait$trackedSiegeItems) {
                if (current.contains(tardisId))
                    continue;

                manager.getTardis(serverWorld.getServer(), tardisId, tardis -> {
                    if (!tardis.siege().isActive())
                        return;

                    if (serverWorld.isChunkLoaded(pos)) {
                        BlockEntity live = serverWorld.getBlockEntity(pos);
                        if (live instanceof Inventory liveInventory
                                && SiegeInventoryUtil.scan(liveInventory, tardisId)
                                != SiegeInventoryUtil.ScanResult.NOT_FOUND)
                            return;
                    }
                    tardis.returnHome().forgetSiegeItemContainer(serverWorld, pos);
                });
            }
        }

        Set<UUID> normalized = SiegeInventoryUtil.limitTrackedIds(retained);
        if (!normalized.equals(this.ait$trackedSiegeItems)) {
            this.ait$trackedSiegeItems = normalized;
            ((BlockEntity) (Object) this).markDirty();
        }
    }
}
