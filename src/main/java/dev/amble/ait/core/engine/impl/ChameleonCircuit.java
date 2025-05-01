package dev.amble.ait.core.engine.impl;

import net.minecraft.item.Item;

import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.engine.DurableSubSystem;
import dev.amble.ait.core.engine.StructureHolder;
import dev.amble.ait.core.engine.block.multi.MultiBlockStructure;
import dev.amble.ait.core.tardis.TardisExterior;
import dev.amble.ait.registry.impl.CategoryRegistry;

public class ChameleonCircuit extends DurableSubSystem implements StructureHolder {
    static {
        TardisEvents.EXTERIOR_CHANGE.register(tardis -> {
            if (tardis.subsystems().chameleon().isUsable()) return;

            TardisExterior exterior = tardis.getExterior();
            if (exterior.getCategory().equals(CategoryRegistry.CAPSULE)) return;

            exterior.setType(CategoryRegistry.CAPSULE);
        });
    }

    public ChameleonCircuit() {
        super(Id.CHAMELEON);
    }

    @Override
    protected float cost() {
        return 0.2f;
    }

    @Override
    protected boolean shouldDurabilityChange() {
        return this.tardis().cloak().cloaked().get();
    }
    @Override
    public MultiBlockStructure getStructure() {
        return MultiBlockStructure.EMPTY;
    }

    @Override
    public Item asItem() {
        return AITItems.CHAMELEON_CIRCUIT;
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        //this.tardis().getExterior().setType(CategoryRegistry.CAPSULE);
    }
}
