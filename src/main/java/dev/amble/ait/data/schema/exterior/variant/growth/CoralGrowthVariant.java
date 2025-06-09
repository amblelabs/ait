package dev.amble.ait.data.schema.exterior.variant.growth;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.animation.ExteriorAnimation;
import dev.amble.ait.core.tardis.animation.PulsatingAnimation;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.CoralGrowthDoorVariant;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.category.GrowthCategory;
import dev.amble.ait.registry.impl.door.DoorRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class CoralGrowthVariant extends ExteriorVariantSchema {
    public static final Identifier REFERENCE = AITMod.id("exterior/coral_growth");

    public CoralGrowthVariant() {
        super(GrowthCategory.REFERENCE, REFERENCE);
    }

    @Override
    public ExteriorAnimation animation(ExteriorBlockEntity exterior) {
        return new PulsatingAnimation(exterior);
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.REGISTRY.get(CoralGrowthDoorVariant.REFERENCE);
    }

    @Override
    public boolean hasPortals() {
        return true;
    }

    @Override
    public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
        return switch (direction) {
            case 0 -> pos.add(0, 0.1, -0.5); // NORTH
            case 1, 2, 3 -> pos; // NORTH EAST
            case 4 -> pos.add(0.5, 0.1, 0); // EAST
            case 5, 6, 7 -> pos; // SOUTH EAST
            case 8 -> pos.add(0, 0.1, 0.5); // SOUTH
            case 9, 10, 11 -> pos; // SOUTH WEST
            case 12 -> pos.add(-0.5, 0.1, 0); // WEST
            case 13, 14, 15 -> pos; // NORTH WEST
            default -> pos;
        };
    }

    @Override
    public double portalHeight() {
        return 2.1d;
    }

    @Override
    public double portalWidth() {
        return 0.6d;
    }

    @Override
    public Vec3d seatTranslations() {
        return new Vec3d(0.5, 1, 0.5);
    }
}
