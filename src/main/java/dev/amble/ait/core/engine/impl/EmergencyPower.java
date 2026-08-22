package dev.amble.ait.core.engine.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import dev.amble.ait.api.ArtronHolder;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.engine.CoreInstallableSubSystem;
import dev.amble.ait.core.engine.StructureHolder;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.engine.block.generic.GenericStructureSystemBlockEntity;
import dev.amble.ait.core.engine.block.multi.MultiBlockStructure;
import dev.amble.ait.core.item.RiftScannerItem;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.data.Exclude;
import dev.amble.lib.data.CachedDirectedGlobalPos;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;

public class EmergencyPower extends SubSystem implements ArtronHolder, StructureHolder, CoreInstallableSubSystem {
    private static final double FUEL_PER_CIRCUIT = 1000;

    private double fuel;
    private Set<UUID> circuitIds = new HashSet<>();
    @Exclude(strategy = Exclude.Strategy.NETWORK)
    private Set<UUID> poweredCircuitIds = new HashSet<>();
    private boolean circuitTrackingInitialized;

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
    public void onCreate() {
        this.ensureCollections();
        this.circuitTrackingInitialized = true;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled && this.getCircuitCount() == 0)
            return;

        super.setEnabled(enabled);
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
        return FUEL_PER_CIRCUIT * this.getCircuitCount();
    }

    public boolean hasBackupPower() {
        return this.getCurrentFuel() > 0 && this.isEnabled();
    }

    public int getCircuitCount() {
        this.ensureCollections();
        if (this.circuitTrackingInitialized)
            return this.circuitIds.size();

        // Old saves only persisted the shared 1000 AU buffer. Until their core
        // loads and gains an identity, retain that single-circuit capacity.
        return this.getCurrentFuel() > 0 || this.isEnabled() || this.isReal() ? 1 : 0;
    }

    @Override
    public void onInstalled(GenericStructureSystemBlockEntity core, ServerPlayerEntity player, ItemStack stack) {
        this.registerCore(core);
    }

    @Override
    public void onCoreLoaded(GenericStructureSystemBlockEntity core) {
        this.registerCore(core);
    }

    @Override
    public void onRemoved(GenericStructureSystemBlockEntity core, ItemStack stack) {
        this.ensureCollections();
        core.getInstallationId().ifPresent(id -> {
            this.circuitIds.remove(id);
            this.poweredCircuitIds.remove(id);
        });
        this.circuitTrackingInitialized = true;
        this.fuel = Math.min(this.fuel, this.getMaxFuel());
        this.updateEnabledState();
        this.sync();
    }

    @Override
    public boolean managesCorePowerState() {
        return true;
    }

    @Override
    public void onCorePowerChanged(GenericStructureSystemBlockEntity core, boolean powered) {
        UUID id = this.registerCore(core);
        boolean changed = powered ? this.poweredCircuitIds.add(id) : this.poweredCircuitIds.remove(id);
        if (changed) {
            this.updateEnabledState();
            this.sync();
        }
    }

    private UUID registerCore(GenericStructureSystemBlockEntity core) {
        this.ensureCollections();
        UUID id = core.getOrCreateInstallationId();
        boolean changed = this.circuitIds.add(id);
        this.circuitTrackingInitialized = true;

        if (core.isPowered())
            changed |= this.poweredCircuitIds.add(id);
        else
            changed |= this.poweredCircuitIds.remove(id);

        if (changed) {
            this.updateEnabledState();
            this.sync();
        }
        return id;
    }

    private void updateEnabledState() {
        boolean enabled = !this.poweredCircuitIds.isEmpty();
        if (this.isEnabled() != enabled)
            this.setEnabled(enabled);
    }

    private void ensureCollections() {
        if (this.circuitIds == null)
            this.circuitIds = new HashSet<>();
        if (this.poweredCircuitIds == null)
            this.poweredCircuitIds = new HashSet<>();
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
