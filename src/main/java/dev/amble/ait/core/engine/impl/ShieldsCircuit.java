package dev.amble.ait.core.engine.impl;


import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.engine.CoreBoundDurableSubSystem;
import dev.amble.ait.core.engine.StructureHolder;
import dev.amble.ait.core.engine.block.multi.MultiBlockStructure;

import net.minecraft.item.Item;

public class ShieldsCircuit extends CoreBoundDurableSubSystem implements StructureHolder {

    public ShieldsCircuit() {
        super(Id.SHIELDS);
    }

    @Override
    protected float cost() {
        return 0.1f;
    }

    @Override
    protected boolean shouldDurabilityChange() {
        return this.tardis.areShieldsActive() || tardis.areVisualShieldsActive();
    }

    @Override
    public MultiBlockStructure getStructure() {
        return MultiBlockStructure.EMPTY;
    }

    @Override
    public Item asItem() {
        return AITItems.SHIELDS_CIRCUIT;
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        this.tardis.shields().disableAll();
    }
}
