package dev.amble.ait.data.schema.exterior.variant.present;

import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.PresentDoorVariant;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.category.PresentCategory;
import dev.amble.ait.registry.impl.door.DoorRegistry;

public abstract class PresentVariant extends ExteriorVariantSchema {
    protected static final String TEXTURE_PATH = "textures/blockentities/exteriors/present/present_";

    protected PresentVariant(String name) {
        super(PresentCategory.REFERENCE, AITMod.id("exterior/present/" + name),
                new Loyalty(Loyalty.Type.NEUTRAL));
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.REGISTRY.get(PresentDoorVariant.REFERENCE);
    }

    @Override
    public boolean hasPortals() {
        return true;
    }

    @Override
    public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
        return switch (direction) {
            case 0 -> pos.add(0, 0.15, -0.65f); // NORTH
            case 1, 2, 3 -> pos.add(0.349f, 0.15, -0.65f); // NORTH EAST p n
            case 4 -> pos.add(0.499f, 0.15, 0.65); // EAST
            case 5, 6, 7 -> pos.add(0.349f, 0.15, 0.65f); // SOUTH EAST p p
            case 8 -> pos.add(0, 0.15, 0.65f); // SOUTH
            case 9, 10, 11 -> pos.add(-0.349f, 0.15, 0.65f); // SOUTH WEST n p
            case 12 -> pos.add(-0.499f, 0.15, 0.65); // WEST
            case 13, 14, 15 -> pos.add(-0.349f, 0.15, -0.65f); // NORTH WEST n n
            default -> pos;
        };
    }

    @Override
    public double portalHeight() {
        return 2d;
    }

    @Override
    public double portalWidth() {
        return 1.25d;
    }

    @Override
    public Vec3d seatTranslations() {
        return new Vec3d(0.5, 1, 0.5);
    }
}
