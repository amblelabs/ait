package dev.amble.ait.data.schema.exterior.variant.exclusive.wanderer;

import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.BoothDoorVariant;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.category.ExclusiveCategory;
import dev.amble.ait.registry.impl.door.DoorRegistry;

public class BoothWandererVariant extends ExteriorVariantSchema {

    public BoothWandererVariant() {
        super(ExclusiveCategory.REFERENCE, AITMod.id("exterior/exclusive/wanderer"));
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.getInstance().get(BoothDoorVariant.REFERENCE);
    }

    @Override
    public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
        return switch (direction) {
            case 0 -> pos.add(0, -0.1, -0.48f); // NORTH
            case 1, 2, 3 -> pos.add(0.35f, -0.1, -0.35f); // NORTH EAST p n
            case 4 -> pos.add(0.48f, -0.1, 0); // EAST
            case 5, 6, 7 -> pos.add(0.35f, -0.1, 0.35f); // SOUTH EAST p p
            case 8 -> pos.add(0, -0.1, 0.48f); // SOUTH
            case 9, 10, 11 -> pos.add(-0.35f, -0.1, 0.35f); // SOUTH WEST n p
            case 12 -> pos.add(-0.48f, -0.1, 0); // WEST
            case 13, 14, 15 -> pos.add(-0.35f, -0.1, -0.35f); // NORTH WEST n n
            default -> pos;
        };
    }

    @Override
    public Vec3d seatTranslations() {
        return new Vec3d(0.5, 1, 0.5);
    }

    @Override
    public boolean hasPortals() {
        return true;
    }

    @Override
    public double portalWidth() {
        return super.portalWidth();
    }

    @Override
    public double portalHeight() {
        return 2.3d;
    }
}
