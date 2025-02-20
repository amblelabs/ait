package dev.amble.ait.core.engine.impl;


import dev.amble.ait.core.engine.DurableSubSystem;
import dev.amble.ait.core.engine.StructureHolder;
import dev.amble.ait.core.engine.block.multi.MultiBlockStructure;

public class ShieldsCircuit extends DurableSubSystem implements StructureHolder {


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
    protected void onDisable() {
        super.onDisable();

        this.tardis.shields().disableAll();
    }
}
