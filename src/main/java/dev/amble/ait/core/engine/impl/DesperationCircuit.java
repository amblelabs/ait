package dev.amble.ait.core.engine.impl;

import dev.amble.ait.core.engine.StructureHolder;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.engine.block.multi.MultiBlockStructure;

public class DesperationCircuit extends SubSystem implements StructureHolder {
    public DesperationCircuit() {
        super(Id.DESPERATION);
    }

    @Override
    public MultiBlockStructure getStructure() {
        return MultiBlockStructure.EMPTY;
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        this.tardis().stats().hailMary().set(false);
        this.tardis().siege().setActive(false);
    }
}
