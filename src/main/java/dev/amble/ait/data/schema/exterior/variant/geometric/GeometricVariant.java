package dev.amble.ait.data.schema.exterior.variant.geometric;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.animation.ExteriorAnimation;
import dev.amble.ait.core.tardis.animation.PulsatingAnimation;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.GeometricDoorVariant;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.category.GeometricCategory;
import dev.amble.ait.registry.impl.door.DoorRegistry;

// a useful class for creating tardim variants as they all have the same filepath you know
public abstract class GeometricVariant extends ExteriorVariantSchema {
    protected static final String TEXTURE_PATH = "textures/blockentities/exteriors/geometric/geometric_";

    protected GeometricVariant(String name, String modId) { // idk why i added the modid bit i dont use it later lol
        super(GeometricCategory.REFERENCE, new Identifier(modId, "exterior/geometric/" + name));
    }

    protected GeometricVariant(String name) {
        this(name, AITMod.MOD_ID);
    }

    @Override
    public ExteriorAnimation animation(ExteriorBlockEntity exterior) {
        return new PulsatingAnimation(exterior);
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.REGISTRY.get(GeometricDoorVariant.REFERENCE);
    }

    @Override
    public boolean hasPortals() {
        return true;
    }

    @Override
    public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
        return switch (direction) {
            case 0 -> pos.add(0, -0.07, -0.025); // NORTH
            case 1, 2, 3 -> pos.add(0.025, -0.07, -0.025); // NORTH EAST p n
            case 4 -> pos.add(0.5, -0.07, 0); // EAST
            case 5, 6, 7 -> pos.add(0.025, -0.07, 0.025); // SOUTH EAST p p
            case 8 -> pos.add(0, -0.07, 0.5); // SOUTH
            case 9, 10, 11 -> pos.add(-0.025, -0.07, 0.025); // SOUTH WEST n p
            case 12 -> pos.add(-0.5, -0.07, 0); // WEST
            case 13, 14, 15 -> pos.add(-0.025, -0.07, -0.025); // NORTH WEST n n
            default -> pos;
        };
    }

    @Override
    public Vec3d seatTranslations() {
        return new Vec3d(0.5, 1, 0.5);
    }

    @Override
    public double portalHeight() {
        return 2.52d;
    }
}
