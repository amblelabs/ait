package dev.amble.ait.data.schema.exterior.variant.box;

import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.PoliceBoxRenaissanceDoorVariant;
import dev.amble.ait.registry.impl.door.DoorRegistry;
import net.minecraft.util.math.Vec3d;

public class PoliceBoxRenaissanceVariant extends PoliceBoxVariant {
    public PoliceBoxRenaissanceVariant() {
        super("renaissance");
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.REGISTRY.get(PoliceBoxRenaissanceDoorVariant.REFERENCE);
    }

    @Override
    public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
        pos = super.adjustPortalPos(pos, direction);
        return pos.add(0, -0.05, 0);
    }

    @Override
    public double portalHeight() {
        return 2.3;
    }
}
