package dev.amble.ait.data.schema.exterior.variant.easter_head;


import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.EasterHeadDoorVariant;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.category.EasterHeadCategory;
import dev.amble.ait.registry.impl.door.DoorRegistry;

// a useful class for creating easter_head variants as they all have the same filepath you know
public abstract class EasterHeadVariant extends ExteriorVariantSchema {
    protected static final String TEXTURE_PATH = "textures/blockentities/exteriors/easter_head/easter_head_";

    protected EasterHeadVariant(String name) {
        super(EasterHeadCategory.REFERENCE, AITMod.id("exterior/easter_head/" + name),
                new Loyalty(Loyalty.Type.COMPANION));
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.getInstance().get(EasterHeadDoorVariant.REFERENCE);
    }


    @Override
    public boolean hasPortals() {
        return false;
    }

    @Override
   public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
        return switch (direction) {
            case 0 -> pos.add(0, 0.27, -0.025); // NORTH
            case 1, 2, 3 -> pos.add(0.025, 0.27, -0.025); // NORTH EAST p n
            case 4 -> pos.add(0.5, 0.27, 0); // EAST
           case 5, 6, 7 -> pos.add(0.025, 0.27, 0.025); // SOUTH EAST p p
            case 8 -> pos.add(0, 0.27, 0.5); // SOUTH
            case 9, 10, 11 -> pos.add(-0.025, 0.27, 0.025); // SOUTH WEST n p
            case 12 -> pos.add(-0.5, 0.27, 0); // WEST
            case 13, 14, 15 -> pos.add(-0.025, 0.27, -0.025); // NORTH WEST n n
           default -> pos;
        };
    }

    @Override
    public Vec3d seatTranslations() {
        return new Vec3d(0.5, 1, 0.5);
    }

    @Override
    public double portalHeight() {
        return 0d;
    }

    @Override
    public double portalWidth() {
        return 0d;
    }
}
