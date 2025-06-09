package dev.amble.ait.core.engine.impl;


import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.engine.DurableSubSystem;
import dev.amble.ait.core.engine.StructureHolder;
import dev.amble.ait.core.engine.block.multi.MultiBlockStructure;
import net.minecraft.item.Item;

public class DematCircuit extends DurableSubSystem implements StructureHolder {

    static {
        TardisEvents.DEMAT.register(tardis -> {
            DematCircuit circuit = tardis.subsystems().demat();

            return circuit.isEnabled() && !circuit.isBroken()
                    ? TardisEvents.Interaction.PASS : TardisEvents.Interaction.FAIL;
        });
    }

    public DematCircuit() {
        super(Id.DEMAT);
    }

    @Override
    protected float cost() {
        return 0.05f * this.tardis().travel().speed();
    }

    @Override
    public MultiBlockStructure getStructure() {
        return MultiBlockStructure.EMPTY;
    }

    @Override
    protected boolean shouldDurabilityChange() {
        return this.tardis.travel().inFlight();
    }

    @Override
    public Item asItem() {
        return AITItems.DEMATERIALIZATION_CIRCUIT;
    }
}
