package dev.amble.ait.data.schema.exterior.variant.stallion;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.animation.ExteriorAnimation;
import dev.amble.ait.core.tardis.animation.PulsatingAnimation;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.StallionDoorVariant;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.category.StallionCategory;
import dev.amble.ait.registry.impl.door.DoorRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;


public abstract class StallionVariant extends ExteriorVariantSchema {
    protected static final String TEXTURE_PATH = "textures/blockentities/exteriors/stallion/stallion_";

    protected StallionVariant(String name, String modId) {
        super(StallionCategory.REFERENCE, new Identifier(modId, "exterior/stallion/" + name),
                new Loyalty(Loyalty.Type.COMPANION));
    }

    protected StallionVariant(String name) {
        this(name, AITMod.MOD_ID);
    }

    @Override
    public ExteriorAnimation animation(ExteriorBlockEntity exterior) {
        return new PulsatingAnimation(exterior);
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.REGISTRY.get(StallionDoorVariant.REFERENCE);
    }

    @Override
    public boolean hasPortals() {
        return true;
    }

    @Override
    public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
        return switch (direction) {
            case 0 -> pos.add(0, -0.05, -0.5); // NORTH
            case 1, 2, 3 -> pos.add(0.38, -0.05, -0.38); // NORTH EAST p n
            case 4 -> pos.add(0.5, -0.05, 0); // EAST
            case 5, 6, 7 -> pos.add(0.38, -0.05, 0.38); // SOUTH EAST p p
            case 8 -> pos.add(0, -0.05, 0.5); // SOUTH
            case 9, 10, 11 -> pos.add(-0.38, -0.05, 0.38); // SOUTH WEST n p
            case 12 -> pos.add(-0.5, -0.05, 0); // WEST
            case 13, 14, 15 -> pos.add(-0.38, -0.05, -0.38); // NORTH WEST n n
            default -> pos;
        };
    }
    @Override
    public double portalHeight() {
        return 2.3d;
    }

    @Override
    public double portalWidth() {
        return 1d;
    }

    @Override
    public Vec3d seatTranslations() {
        return new Vec3d(0.5, 1, 0.5);
    }
}
