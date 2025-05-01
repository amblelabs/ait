package dev.amble.ait.data.schema.exterior.variant.classic;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.animation.ExteriorAnimation;
import dev.amble.ait.core.tardis.animation.PulsatingAnimation;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.ClassicDoorVariant;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.category.ClassicCategory;
import dev.amble.ait.registry.impl.door.DoorRegistry;

// a useful class for creating tardim variants as they all have the same filepath you know
public abstract class ClassicBoxVariant extends ExteriorVariantSchema {
    protected static final String TEXTURE_PATH = "textures/blockentities/exteriors/classic/classic_";

    protected ClassicBoxVariant(String name, String modId) {
        super(ClassicCategory.REFERENCE, new Identifier(modId, "exterior/classic/" + name),
                new Loyalty(Loyalty.Type.OWNER));
    }

    protected ClassicBoxVariant(String name) {
        this(name, AITMod.MOD_ID);
    }

    @Override
    public ExteriorAnimation animation(ExteriorBlockEntity exterior) {
        return new PulsatingAnimation(exterior);
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.REGISTRY.get(ClassicDoorVariant.REFERENCE);
    }

    @Override
    public boolean hasPortals() {
        return true;
    }

    @Override
    public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
        return switch (direction) {
            case 0 -> pos.add(0, -0.05, -0.599); // NORTH
            case 1, 2, 3 -> pos.add(0.45f, -0.05, -0.45f); // NORTH EAST p n
            case 4 -> pos.add(0.599, -0.05, 0); // EAST
            case 5, 6, 7 -> pos.add(0.45f, -0.05, 0.45f); // SOUTH EAST p p
            case 8 -> pos.add(0, -0.05, 0.599); // SOUTH
            case 9, 10, 11 -> pos.add(-0.45f, -0.05, 0.45f); // SOUTH WEST n p
            case 12 -> pos.add(-0.599, -0.05, 0); // WEST
            case 13, 14, 15 -> pos.add(-0.45f, -0.05, -0.45f); // NORTH WEST n n
            default -> pos;
        };
    }

    @Override
    public Vec3d seatTranslations() {
        return new Vec3d(0.5, 1, 0.5);
    }

    @Override
    public double portalHeight() {
        return 2.2d;
    }

    @Override
    public double portalWidth() {
        return 1.2d;
    }
}
