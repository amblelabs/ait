package dev.amble.ait.core.engine.impl;

import java.util.UUID;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.item.NestedSiegeItemUtil;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.util.TardisHomeUtil;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.Exclude;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.data.DirectedBlockPos;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class EnderChestCollector extends HomeBoundSubSystem {
    private static final int EXISTING_ITEM_SCAN_INTERVAL = 5 * 20;
    private static final double COLLECTION_POINT_RADIUS_SQUARED = 1.5 * 1.5;

    @Exclude private long lastExistingItemScan = Long.MIN_VALUE;

    public EnderChestCollector() {
        super(Id.ENDER_CHEST_COLLECTOR);
    }

    @Override
    public Item asItem() {
        return Items.ENDER_CHEST;
    }

    @Override
    public boolean requiresLifeSupport() {
        return false;
    }

    @Override
    protected void tickAtHome(MinecraftServer server) {
        HomeEntityCapture.register(this);
        int fuelCost = Math.max(0, AITMod.CONFIG.enderChestFuelPerSecond);
        if (fuelCost > 0)
            this.serverTardis().fuel().removeFuel(fuelCost);
        if (!this.isOperational()) {
            HomeEntityCapture.unregister(this);
            return;
        }

        this.captureExistingItems(server);
    }

    @Override
    protected void onInstallationChanged(boolean installed) {
        if (!this.isServer())
            return;

        this.lastExistingItemScan = Long.MIN_VALUE;
        if (installed)
            HomeEntityCapture.register(this);
        else
            HomeEntityCapture.unregister(this);
    }

    private void captureExistingItems(MinecraftServer server) {
        long now = server.getTicks();
        if (this.lastExistingItemScan != Long.MIN_VALUE) {
            long elapsed = now - this.lastExistingItemScan;
            if (elapsed >= 0 && elapsed < EXISTING_ITEM_SCAN_INTERVAL)
                return;
        }

        this.lastExistingItemScan = now;
        ServerTardis tardis = this.serverTardis();
        if (tardis.hasWorld()) {
            for (ItemEntity item : tardis.world().getEntitiesByType(
                    TypeFilter.instanceOf(ItemEntity.class), candidate -> !candidate.isRemoved())) {
                HomeEntityCapture.tryCaptureExistingItem(item);
            }
        }

        CachedDirectedGlobalPos home = tardis.stats().getHome();
        if (home == null)
            return;

        home.init(server);
        ServerWorld world = home.getWorld();
        if (world == null)
            return;

        double radius = TardisHomeUtil.homeRadius();
        double radiusSquared = radius * radius;
        Vec3d center = Vec3d.ofCenter(home.getPos());
        Box bounds = Box.of(center, radius * 2, radius * 2, radius * 2);
        for (ItemEntity item : world.getEntitiesByType(
                TypeFilter.instanceOf(ItemEntity.class), bounds,
                candidate -> !candidate.isRemoved()
                        && candidate.squaredDistanceTo(center) <= radiusSquared)) {
            HomeEntityCapture.tryCaptureExistingItem(item);
        }
    }

    boolean captureItem(ItemEntity source) {
        ServerTardis tardis = this.serverTardis();
        if (!this.isOperational() || source == null || source.getStack().isEmpty()
                || NestedSiegeItemUtil.contains(source.getStack(), tardis.getUuid()))
            return false;

        TardisServerWorld interior = tardis.world();
        if (interior == null)
            return false;

        DirectedBlockPos door = tardis.getDesktop().getDoorPos();
        if (door == null)
            return false;

        Vec3d interiorDoor = TardisUtil.offset(
                TardisUtil.offsetInteriorDoorPos(door), door, -0.5);
        if (source.getWorld() == interior
                && source.squaredDistanceTo(interiorDoor) <= COLLECTION_POINT_RADIUS_SQUARED)
            return false;

        Entity copy = source.getType().create(interior);
        if (!(copy instanceof ItemEntity target))
            return false;

        UUID targetId = target.getUuid();
        target.copyFrom(source);
        target.setUuid(targetId);
        target.refreshPositionAndAngles(interiorDoor.x, interiorDoor.y, interiorDoor.z,
                source.getYaw(), source.getPitch());
        target.setVelocity(source.getVelocity());
        return interior.spawnEntity(target);
    }
}
