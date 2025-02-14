package dev.amble.ait.core.engine.impl;


import dev.amble.ait.core.engine.DurableSubSystem;
import dev.amble.ait.core.engine.StructureHolder;
import dev.amble.ait.core.engine.block.multi.MultiBlockStructure;

public class GravitationalCircuit extends DurableSubSystem implements StructureHolder {

    public GravitationalCircuit() {
        super(Id.GRAVITATIONAL);
    }

    @Override
    protected float cost() {
        return 0.25f;
    }

    @Override
    protected boolean shouldDurabilityChange() {
        return this.tardis().travel().antigravs().get();
    }

    @Override
    public MultiBlockStructure getStructure() {
        return MultiBlockStructure.EMPTY;
    }


    @Override
    protected void onDisable() {
        super.onDisable();

        this.tardis().travel().antigravs().set(false);
    }
}
