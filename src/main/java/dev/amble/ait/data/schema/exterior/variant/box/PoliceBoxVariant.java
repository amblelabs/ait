package dev.amble.ait.data.schema.exterior.variant.box;

import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.PoliceBoxDoorVariant;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.category.PoliceBoxCategory;
import dev.amble.ait.registry.impl.door.DoorRegistry;

// a useful class for creating tardim variants as they all have the same filepath you know
public abstract class PoliceBoxVariant extends ExteriorVariantSchema {
    protected static final String TEXTURE_PATH = "textures/blockentities/exteriors/police_box/police_box_";

    protected PoliceBoxVariant(String name) { // idk why i added the modid bit i dont use it later lol
        super(PoliceBoxCategory.REFERENCE, AITMod.id("exterior/police_box/" + name),
                new Loyalty(Loyalty.Type.COMPANION));
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.getInstance().get(PoliceBoxDoorVariant.REFERENCE);
    }

    @Override
    public boolean hasPortals() {
        return true;
    }

    @Override
    public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
        return switch (direction) {
            case 0 -> pos.add(0, -0.05, -0.628); // NORTH
            case 1, 2, 3 -> pos.add(0.43, -0.05, -0.43); // NORTH EAST p n
            case 4 -> pos.add(0.628, -0.05, 0); // EAST
            case 5, 6, 7 -> pos.add(0.43, -0.05, 0.43); // SOUTH EAST p p
            case 8 -> pos.add(0, -0.05, 0.628); // SOUTH
            case 9, 10, 11 -> pos.add(-0.43, -0.05, 0.43); // SOUTH WEST n p
            case 12 -> pos.add(-0.628, -0.05, 0); // WEST
            case 13, 14, 15 -> pos.add(-0.43, -0.05, -0.43); // NORTH WEST n n
            default -> pos;
        };
    }

    @Override
    public Vec3d seatTranslations() {
        return new Vec3d(0.5, 1, 0.5);
    }

    @Override
    public double portalHeight() {
        return 2.28d;
    }

    @Override
    public double portalWidth() {
        return 1.15d;
    }
}
