package dev.amble.ait.data.schema.exterior.variant.dalek_mod;


import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.DalekModDoorVariant;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.category.DalekModCategory;
import dev.amble.ait.registry.impl.door.DoorRegistry;


public abstract class DalekModVariant extends ExteriorVariantSchema {
    protected static final String TEXTURE_PATH = "textures/blockentities/exteriors/dalek_mod/dalek_mod_";

    protected DalekModVariant(int number) {
        super(DalekModCategory.REFERENCE, AITMod.id("exterior/dalek_mod/" + number),
                new Loyalty(Loyalty.Type.OWNER));
    }

    @Override
    public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
        return switch (direction) {
            case 0 -> pos.add(0, -0.05, -0.59); // NORTH
            case 1, 2, 3 -> pos.add(0.43, -0.05, -0.45); // NORTH EAST p n
            case 4 -> pos.add(0.628, -0.05, 0); // EAST
            case 5, 6, 7 -> pos.add(0.43, -0.05, 0.45); // SOUTH EAST p p
            case 8 -> pos.add(0, -0.05, 0.628); // SOUTH
            case 9, 10, 11 -> pos.add(-0.43, -0.05, 0.45); // SOUTH WEST n p
            case 12 -> pos.add(-0.628, -0.05, 0); // WEST
            case 13, 14, 15 -> pos.add(-0.43, -0.05, -0.45); // NORTH WEST n n
            default -> pos;
        };
    }


    @Override
    public double portalHeight() {
        return 1.92d;
    }

    @Override
    public double portalWidth() {
        return 1.145d;
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.REGISTRY.get(DalekModDoorVariant.REFERENCE);
    }

    @Override
    public Vec3d seatTranslations() {
        return new Vec3d(0.5, 1, 0.5);
    }

    @Override
    public boolean hasPortals() {
        return true;
    }

}
