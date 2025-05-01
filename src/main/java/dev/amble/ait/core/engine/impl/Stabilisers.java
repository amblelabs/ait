package dev.amble.ait.core.engine.impl;


import net.minecraft.item.Item;

import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.engine.StructureHolder;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.engine.block.multi.MultiBlockStructure;

public class Stabilisers extends SubSystem implements StructureHolder {
    static {
        TardisEvents.DEMAT.register(tdis -> {
            Stabilisers stabilisers = tdis.subsystems().stabilisers();

            // i dont like this feature, so i commented it out :p
            /*
            if (!stabilisers.isEnabled()) return TardisEvents.Interaction.FAIL;
            ServerPlayerEntity player = tdis.loyalty().getLoyalPlayerInside();
            if (!tdis.loyalty().get(player).isOf(Loyalty.Type.PILOT)) return TardisEvents.Interaction.FAIL;
             */
            return TardisEvents.Interaction.PASS;
        });
    }

    public Stabilisers() {
        super(Id.STABILISERS);
    }

    @Override
    public MultiBlockStructure getStructure() {
        return MultiBlockStructure.EMPTY;
    }

    @Override
    public Item asItem() {
        return AITItems.STABILISERS;
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        this.tardis().travel().autopilot(false);
    }
}
