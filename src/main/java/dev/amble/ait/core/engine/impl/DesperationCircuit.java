package dev.amble.ait.core.engine.impl;

import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.engine.StructureHolder;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.engine.block.multi.MultiBlockStructure;
import net.minecraft.item.Item;

public class DesperationCircuit extends SubSystem implements StructureHolder {
    public DesperationCircuit() {
        super(Id.DESPERATION);
    }

    @Override
    public MultiBlockStructure getStructure() {
        return MultiBlockStructure.EMPTY;
    }

    @Override
    public Item asItem() {
        return AITItems.DESPERATION_CIRCUIT;
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        this.tardis().stats().hailMary().set(false);
        this.tardis().siege().setActive(false);
    }
}
