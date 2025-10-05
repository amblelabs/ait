package dev.amble.ait.data.schema.exterior.variant.box;

import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.PoliceBoxCoralDoorVariant;
import dev.amble.ait.registry.impl.door.DoorRegistry;

public class PoliceBoxCoralVariant extends PoliceBoxVariant {
    public PoliceBoxCoralVariant() {
        super("coral");
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.getInstance().get(PoliceBoxCoralDoorVariant.REFERENCE);
    }

    @Override
    public double portalHeight() {
        return 2.4;
    }
}
