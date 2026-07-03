package dev.amble.ait.data.schema.exterior.variant.classic;

import java.util.Optional;

import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.ClassicHudolinDoorVariant;
import dev.amble.ait.registry.impl.door.DoorRegistry;

public class ClassicBoxShalkaVariant extends ClassicBoxVariant {
    public ClassicBoxShalkaVariant() {
        super("shalka");
    }

    @Override
    public Optional<Loyalty> requirement() {
        return Optional.of(new Loyalty(Loyalty.Type.COMPANION));
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.getInstance().get(ClassicHudolinDoorVariant.REFERENCE);
    }
}
