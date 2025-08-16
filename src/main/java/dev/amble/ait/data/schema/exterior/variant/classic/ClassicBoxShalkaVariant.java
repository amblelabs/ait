package dev.amble.ait.data.schema.exterior.variant.classic;

import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.ClassicHudolinDoorVariant;
import dev.amble.ait.registry.impl.door.DoorRegistry;

public class ClassicBoxShalkaVariant extends ClassicBoxVariant {
    public ClassicBoxShalkaVariant() {
        super("shalka");
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.getInstance().get(ClassicHudolinDoorVariant.REFERENCE);
    }
}