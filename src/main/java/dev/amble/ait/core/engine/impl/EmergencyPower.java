package dev.amble.ait.core.engine.impl;

import dev.amble.lib.data.CachedDirectedGlobalPos;

import net.minecraft.item.Item;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;

import dev.amble.ait.api.ArtronHolder;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.engine.StructureHolder;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.engine.block.multi.MultiBlockStructure;
import dev.amble.ait.core.item.RiftScannerItem;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;

public class EmergencyPower extends SubSystem implements ArtronHolder, StructureHolder {
    private double fuel;

    static {
        TardisEvents.USE_BACKUP_POWER.register((tdis, power) -> {
            tdis.alarm().enable();

            // if power is below 200, find the nearest rift and head there
            if (power > 200) return;
            TravelHandler travel = tdis.travel();
            RiftScannerItem.findNearestRift(travel.position().getWorld(), new ChunkPos(travel.position().getPos()), pos -> {
                travel.destination(CachedDirectedGlobalPos.create(travel.position().getWorld(), pos.getCenterAtY(70), (byte) 0));
                travel.autopilot(true);
                travel.dematerialize();
            });
        });
    }

    public EmergencyPower() {
        super(Id.EMERGENCY_POWER);
    }

    @Override
    public double getCurrentFuel() {
        return fuel;
    }

    @Override
    public void setCurrentFuel(double var) {
        fuel = MathHelper.clamp(var, 0, getMaxFuel());
        this.sync();
    }

    @Override
    public double getMaxFuel() {
        return 1000;
    }
    public boolean hasBackupPower() {
        return this.getCurrentFuel() > 0 && this.isEnabled();
    }

    @Override
    public MultiBlockStructure getStructure() {
        return MultiBlockStructure.EMPTY;
    }

    @Override
    public Item asItem() {
        return AITItems.BACKUP_CIRCUIT;
    }
}
