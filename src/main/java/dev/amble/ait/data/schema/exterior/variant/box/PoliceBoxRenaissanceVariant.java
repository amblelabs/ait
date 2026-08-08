package dev.amble.ait.data.schema.exterior.variant.box;

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.Vec3d;

import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.PoliceBoxRenaissanceDoorVariant;
import dev.amble.ait.registry.impl.door.DoorRegistry;

public class PoliceBoxRenaissanceVariant extends PoliceBoxVariant {
    public PoliceBoxRenaissanceVariant() {
        super("renaissance");
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.getInstance().get(PoliceBoxRenaissanceDoorVariant.REFERENCE);
    }

    @Override
    public @Nullable Vec3d getPortalPosition() {
        return new Vec3d(0, 0.01, -0.591);
    }

    @Override
    public double portalHeight() {
        return 2.35d;
    }
}
