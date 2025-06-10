package dev.amble.ait.data.schema.exterior.variant.jake;

import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.animation.ExteriorAnimation;
import dev.amble.ait.core.tardis.animation.PulsatingAnimation;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.JakeDoorVariant;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.category.JakeCategory;
import dev.amble.ait.registry.impl.door.DoorRegistry;

public abstract class JakeVariant extends ExteriorVariantSchema {
    protected static final String TEXTURE_PATH = "textures/blockentities/exteriors/jake/jake_";

    protected JakeVariant(String name) {
        super(JakeCategory.REFERENCE, AITMod.id("exterior/jake/" + name),
                new Loyalty(Loyalty.Type.OWNER));
    }

    @Override
    public ExteriorAnimation animation(ExteriorBlockEntity exterior) {
        return new PulsatingAnimation(exterior);
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.REGISTRY.get(JakeDoorVariant.REFERENCE);
    }

    @Override
    public boolean hasPortals() {
        return false;
    }

    @Override
    public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
        return switch (direction) {
            case 0 -> pos.add(0, 0, -0.499f); // NORTH
            case 1, 2, 3 -> pos.add(0.349f, 0, -0.349f); // NORTH EAST p n
            case 4 -> pos.add(0.499f, 0, 0); // EAST
            case 5, 6, 7 -> pos.add(0.349f, 0, 0.349f); // SOUTH EAST p p
            case 8 -> pos.add(0, 0, 0.499f); // SOUTH
            case 9, 10, 11 -> pos.add(-0.349f, 0, 0.349f); // SOUTH WEST n p
            case 12 -> pos.add(-0.499f, 0, 0); // WEST
            case 13, 14, 15 -> pos.add(-0.349f, 0, -0.349f); // NORTH WEST n n
            default -> pos;
        };
    }

    @Override
    public Vec3d seatTranslations() {
        return new Vec3d(0.5, 1, 0.5);
    }
}
