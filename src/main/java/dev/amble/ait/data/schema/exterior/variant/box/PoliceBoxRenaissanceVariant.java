package dev.amble.ait.data.schema.exterior.variant.box;

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
        return DoorRegistry.REGISTRY.get(PoliceBoxRenaissanceDoorVariant.REFERENCE);
    }

    @Override
    public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
        /*
         * return switch(direction) { case DOWN, UP -> pos; case NORTH ->
         * pos.add(0,0.207,-0.63f); case SOUTH -> pos.add(0,0.207,0.63f); case WEST ->
         * pos.add(-0.63f,0.207,0); case EAST -> pos.add(0.63f,0.207,0); };
         */
        return super.adjustPortalPos(pos, direction);
    }
}
