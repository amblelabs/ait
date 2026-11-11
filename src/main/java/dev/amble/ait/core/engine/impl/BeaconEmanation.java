package dev.amble.ait.core.engine.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.util.TardisHomeUtil;
import dev.amble.ait.data.Loyalty;
import dev.amble.lib.data.CachedDirectedGlobalPos;

import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public final class BeaconEmanation extends HomeBoundSubSystem {

    public BeaconEmanation() {
        super(Id.BEACON_EMANATION);
    }

    @Override
    public Item asItem() {
        return Items.BEACON;
    }

    @Override
    protected void tickAtHome(MinecraftServer server) {
        ServerTardis tardis = this.serverTardis();
        int fuelCost = Math.max(0, AITMod.CONFIG.beaconEmanationFuelPerSecond);
        if (fuelCost > 0)
            tardis.fuel().removeFuel(fuelCost);

        if (!this.isOperational())
            return;

        if (tardis.hasWorld()) {
            for (ServerPlayerEntity player : tardis.world().getPlayers())
                this.applyEffects(tardis, player, true);
        }

        CachedDirectedGlobalPos home = tardis.stats().getHome();
        if (home == null)
            return;

        home.init(server);
        ServerWorld homeWorld = home.getWorld();
        if (homeWorld == null)
            return;

        int radius = TardisHomeUtil.homeRadius();
        double radiusSquared = (double) radius * radius;
        for (ServerPlayerEntity player : homeWorld.getPlayers()) {
            if (player.squaredDistanceTo(home.getPos().getX() + 0.5, home.getPos().getY() + 0.5,
                    home.getPos().getZ() + 0.5) <= radiusSquared)
                this.applyEffects(tardis, player, false);
        }
    }

    private void applyEffects(ServerTardis tardis, ServerPlayerEntity player, boolean inside) {
        Loyalty loyalty = tardis.loyalty().get(player);
        int maxTier = maxTier(loyalty);
        if (maxTier < 0)
            return;

        Map<StatusEffect, Integer> effects = new LinkedHashMap<>();
        StatusEffect[][] effectsByLevel = BeaconBlockEntity.EFFECTS_BY_LEVEL;
        if (effectsByLevel == null || effectsByLevel.length == 0)
            return;
        int lastTier = Math.min(maxTier, effectsByLevel.length - 1);

        for (int tier = 0; tier <= lastTier; tier++) {
            StatusEffect[] tierEffects = effectsByLevel[tier];
            if (tierEffects == null)
                continue;

            for (StatusEffect effect : tierEffects) {
                if (effect == null || !effect.isBeneficial()
                        || inside && (effect == StatusEffects.REGENERATION
                        || maxTier == 3 && effect == StatusEffects.RESISTANCE))
                    continue;

                int amplifier = maxTier == 3 && tier < 3 ? 1 : 0;
                effects.merge(effect, amplifier, Math::max);
            }
        }

        int duration = (9 + (lastTier + 1) * 2) * 20;
        effects.forEach((effect, amplifier) -> player.addStatusEffect(
                new StatusEffectInstance(effect, duration, amplifier, true, true)));
    }

    private static int maxTier(Loyalty loyalty) {
        if (loyalty.isOf(Loyalty.Type.OWNER))
            return 3;
        if (loyalty.isOf(Loyalty.Type.PILOT))
            return 2;
        if (loyalty.isOf(Loyalty.Type.COMPANION))
            return 0;
        return -1;
    }
}
