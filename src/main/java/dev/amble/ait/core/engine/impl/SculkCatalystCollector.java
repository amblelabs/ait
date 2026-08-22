package dev.amble.ait.core.engine.impl;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.engine.DurableSubSystem;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.util.TardisHomeUtil;
import dev.amble.ait.data.Exclude;
import dev.amble.lib.data.CachedDirectedGlobalPos;

import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class SculkCatalystCollector extends HomeBoundSubSystem {
    private static final int EXISTING_EXPERIENCE_SCAN_INTERVAL = 5 * 20;

    private long storedExperience;
    @Exclude private boolean captureDirty;
    @Exclude private long lastExistingExperienceScan = Long.MIN_VALUE;

    public SculkCatalystCollector() {
        super(Id.SCULK_CATALYST_COLLECTOR);
    }

    @Override
    public Item asItem() {
        return Items.SCULK_CATALYST;
    }

    @Override
    public boolean requiresLifeSupport() {
        return false;
    }

    @Override
    protected void tickAtHome(MinecraftServer server) {
        HomeEntityCapture.register(this);
        ServerTardis tardis = this.serverTardis();
        int fuelCost = Math.max(0, AITMod.CONFIG.sculkCatalystFuelPerSecond);
        if (fuelCost > 0)
            tardis.fuel().removeFuel(fuelCost);
        if (!this.isOperational())
            return;

        this.captureExistingExperience(server);

        long before = this.storedExperience;
        this.processExperience();
        if (this.storedExperience != before)
            this.captureDirty = true;

        if (this.captureDirty) {
            this.captureDirty = false;
            this.sync();
        }
    }

    @Override
    protected void onInstallationChanged(boolean installed) {
        if (!this.isServer())
            return;

        this.lastExistingExperienceScan = Long.MIN_VALUE;
        if (installed)
            HomeEntityCapture.register(this);
        else
            HomeEntityCapture.unregister(this);
    }

    boolean captureExperience(long amount) {
        if (!this.isOperational() || amount <= 0)
            return false;

        this.storedExperience = saturatingAdd(this.storedExperience, amount);
        this.processExperience();
        this.captureDirty = true;
        return true;
    }

    private void captureExistingExperience(MinecraftServer server) {
        long now = server.getTicks();
        if (this.lastExistingExperienceScan != Long.MIN_VALUE) {
            long elapsed = now - this.lastExistingExperienceScan;
            if (elapsed >= 0 && elapsed < EXISTING_EXPERIENCE_SCAN_INTERVAL)
                return;
        }

        this.lastExistingExperienceScan = now;
        ServerTardis tardis = this.serverTardis();
        if (tardis.hasWorld()) {
            for (ExperienceOrbEntity orb : tardis.world().getEntitiesByType(
                    TypeFilter.instanceOf(ExperienceOrbEntity.class), candidate -> !candidate.isRemoved())) {
                HomeEntityCapture.tryCaptureExistingExperience(orb);
            }
        }

        CachedDirectedGlobalPos home = tardis.stats().getHome();
        if (home == null)
            return;

        home.init(server);
        ServerWorld world = home.getWorld();
        BlockPos homePos = home.getPos();
        if (world == null || !world.isChunkLoaded(homePos))
            return;

        double radius = TardisHomeUtil.homeRadius();
        double radiusSquared = radius * radius;
        Vec3d center = Vec3d.ofCenter(homePos);
        Box bounds = Box.of(center, radius * 2, radius * 2, radius * 2);
        for (ExperienceOrbEntity orb : world.getEntitiesByType(
                TypeFilter.instanceOf(ExperienceOrbEntity.class), bounds,
                candidate -> !candidate.isRemoved()
                        && candidate.squaredDistanceTo(center) <= radiusSquared)) {
            HomeEntityCapture.tryCaptureExistingExperience(orb);
        }
    }

    private void processExperience() {
        long experiencePerDurability = Math.max(1, AITMod.CONFIG.sculkCatalystExperiencePerDurability);
        long experiencePerArtron = Math.max(1, AITMod.CONFIG.sculkCatalystExperiencePerArtron);
        if (this.storedExperience < Math.min(experiencePerDurability, experiencePerArtron))
            return;

        ServerTardis tardis = this.serverTardis();
        DurableSubSystem damaged;
        while (this.storedExperience >= experiencePerDurability
                && (damaged = mostDamaged(tardis)) != null) {
            long availablePoints = this.storedExperience / experiencePerDurability;
            int missingPoints = (int) Math.ceil(DurableSubSystem.MAX_DURABILITY - damaged.durability());
            int repair = (int) Math.min(availablePoints, Math.max(1, missingPoints));
            damaged.addDurability(repair);
            this.storedExperience -= repair * experiencePerDurability;
        }

        if (mostDamaged(tardis) != null || this.storedExperience < experiencePerArtron)
            return;

        long artron = this.storedExperience / experiencePerArtron;
        double offered = Math.min(artron, (long) Math.ceil(tardis.fuel().getMaxFuel()));
        double overflow = tardis.fuel().addFuel(offered);
        double accepted = Math.max(0, offered - overflow);
        long consumedExperience = (long) Math.ceil(accepted * experiencePerArtron - 0.000_001);
        this.storedExperience = Math.max(0, this.storedExperience - consumedExperience);
    }

    private static DurableSubSystem mostDamaged(ServerTardis tardis) {
        ItemStack handles = tardis.butler().getHandles();
        DurableSubSystem engine = tardis.subsystems().engine();
        if (AITMod.CONFIG.homeDefenseAvailable && AITMod.CONFIG.homeDefenseEngineDamagePerKill > 0
                && tardis.homeSystems().defenseEnabled() && handles != null && !handles.isEmpty()
                && engine.durability() < DurableSubSystem.MAX_DURABILITY)
            return engine;

        DurableSubSystem result = null;
        for (SubSystem system : tardis.subsystems()) {
            if (!(system instanceof DurableSubSystem durable)
                    || durable.durability() >= DurableSubSystem.MAX_DURABILITY)
                continue;

            if (result == null || durable.durability() < result.durability())
                result = durable;
        }
        return result;
    }

    private static long saturatingAdd(long first, long second) {
        if (second > 0 && first > Long.MAX_VALUE - second)
            return Long.MAX_VALUE;
        return first + second;
    }
}
