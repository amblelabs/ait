package dev.amble.ait.core.engine.impl;

import java.util.List;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.engine.CoreBoundDurableSubSystem;
import dev.amble.ait.core.engine.StructureHolder;
import dev.amble.ait.core.engine.block.multi.MultiBlockStructure;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.util.TardisHomeUtil;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.data.Loyalty;
import dev.amble.lib.util.ServerLifecycleHooks;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;

public class LifeSupportCircuit extends CoreBoundDurableSubSystem implements StructureHolder {

    private static final int TICK_RATE = 20;

    private static final MultiBlockStructure STRUCTURE = createStructure();
    private static MultiBlockStructure createStructure() {
        MultiBlockStructure made = new MultiBlockStructure();

        return made;
    }

    public LifeSupportCircuit() {
        super(Id.LIFE_SUPPORT);
    }

    @Override
    protected float cost() {
        return 0.25f;
    }

    @Override
    protected boolean shouldDurabilityChange() {
        return !this.tardis.crash().isNormal();
    }

    @Override
    public MultiBlockStructure getStructure() {
        return MultiBlockStructure.EMPTY;
    }

    @Override
    public Item asItem() {
        return AITItems.LIFE_SUPPORT;
    }

    @Override
    public void tick() {
        super.tick();

        ServerTardis tardis = this.tardis().asServer();

        if (!this.isEnabled()) return;
        if (ServerLifecycleHooks.get().getTicks() % TICK_RATE != 0)
            return;

        List<LivingEntity> entities = TardisUtil.getLivingEntitiesInInterior(tardis);

        for (LivingEntity entity : entities) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, TICK_RATE * 2, 1, true, false));
        }

        if (!this.isUsable() || !tardis.fuel().hasPower() || !TardisHomeUtil.isParkedAtExactHome(tardis))
            return;

        int saturationAmplifier = Math.max(0, AITMod.CONFIG.exactHomeSaturationLevel - 1);
        int resistanceLevel = AITMod.CONFIG.exactHomeOwnerResistanceLevel;
        int resistanceAmplifier = Math.max(0, resistanceLevel - 1);

        for (ServerPlayerEntity player : tardis.world().getPlayers()) {
            Loyalty loyalty = tardis.loyalty().get(player);
            if (!loyalty.isOf(Loyalty.Type.COMPANION))
                continue;

            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION,
                    TICK_RATE * 2, saturationAmplifier, true, false));
            if (loyalty.isOf(Loyalty.Type.OWNER))
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE,
                        TICK_RATE * 2, resistanceAmplifier, true, false));
        }
    }
}
