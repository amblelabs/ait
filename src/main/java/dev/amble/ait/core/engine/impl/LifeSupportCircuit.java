package dev.amble.ait.core.engine.impl;

import java.util.List;

import dev.amble.lib.util.ServerLifecycleHooks;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import dev.amble.ait.core.engine.DurableSubSystem;
import dev.amble.ait.core.engine.StructureHolder;
import dev.amble.ait.core.engine.block.multi.MultiBlockStructure;
import dev.amble.ait.core.entities.ConsoleControlEntity;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.util.TardisUtil;

public class LifeSupportCircuit extends DurableSubSystem implements StructureHolder {
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
    public void tick() {
        super.tick();

        ServerTardis tardis = this.tardis().asServer();

        if (!this.isEnabled()) return;
        if (ServerLifecycleHooks.get().getTicks() % 20 != 0)
            return;

        List<LivingEntity> entities = TardisUtil.getLivingEntitiesInInterior(tardis);

        for (LivingEntity entity : entities) {
            if (entity instanceof ConsoleControlEntity) continue;
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 20, 1, true, false));
        }
    }
}
