package dev.amble.ait.data.schema.exterior.variant.classic;

import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.ClassicHudolinDoorVariant;
import dev.amble.ait.registry.impl.door.DoorRegistry;
import net.minecraft.util.math.Vec3d;

public class ClassicBoxHudolinVariant extends ClassicBoxVariant {
    public ClassicBoxHudolinVariant() {
        super("hudolin");
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.getInstance().get(ClassicHudolinDoorVariant.REFERENCE);
    }

    @Override
    public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
        return switch (direction) {
            case 0 -> pos.add(0, 0, -0.599); // NORTH
            case 1, 2, 3 -> pos.add(0.45f, 0, -0.45f); // NORTH EAST p n
            case 4 -> pos.add(0.599, 0, 0); // EAST
            case 5, 6, 7 -> pos.add(0.45f, 0, 0.45f); // SOUTH EAST p p
            case 8 -> pos.add(0, 0, 0.599); // SOUTH
            case 9, 10, 11 -> pos.add(-0.45f, 0, 0.45f); // SOUTH WEST n p
            case 12 -> pos.add(-0.599, 0, 0); // WEST
            case 13, 14, 15 -> pos.add(-0.45f, 0, -0.45f); // NORTH WEST n n
            default -> pos;
        };
    }
}
