package dev.amble.ait.data.schema.exterior.variant.box;

import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.PoliceBoxCoralDoorVariant;
import dev.amble.ait.registry.impl.door.DoorRegistry;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class PoliceBoxCoralVariant extends PoliceBoxVariant {
    public PoliceBoxCoralVariant() {
        super("coral");
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.getInstance().get(PoliceBoxCoralDoorVariant.REFERENCE);
    }

    @Override
    public @Nullable Vec3d getPortalPosition() {
        return new Vec3d(0, -0.02, -0.591);
    }
}
