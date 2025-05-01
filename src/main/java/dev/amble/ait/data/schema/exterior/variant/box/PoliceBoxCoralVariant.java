package dev.amble.ait.data.schema.exterior.variant.box;

import net.minecraft.util.math.Vec3d;

import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.PoliceBoxCoralDoorVariant;
import dev.amble.ait.registry.impl.door.DoorRegistry;

public class PoliceBoxCoralVariant extends PoliceBoxVariant {
    public PoliceBoxCoralVariant() {
        super("coral");
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.REGISTRY.get(PoliceBoxCoralDoorVariant.REFERENCE);
    }

    @Override
    public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
        pos = super.adjustPortalPos(pos, direction);
        return pos.add(0, 0, 0);
    }

    @Override
    public double portalHeight() {
        return 2.4;
    }
}
