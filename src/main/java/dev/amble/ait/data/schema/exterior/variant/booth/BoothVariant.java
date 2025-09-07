package dev.amble.ait.data.schema.exterior.variant.booth;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.BoothDoorVariant;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.category.BoothCategory;
import dev.amble.ait.registry.impl.door.DoorRegistry;

// a useful class for creating tardim variants as they all have the same filepath you know
public abstract class BoothVariant extends ExteriorVariantSchema {
    protected static final String TEXTURE_PATH = "textures/blockentities/exteriors/booth/booth_";

    protected BoothVariant(String name, String modId) { // idk why i added the modid bit i dont use it later lol
        super(BoothCategory.REFERENCE, new Identifier(modId, "exterior/booth/" + name),
                new Loyalty(Loyalty.Type.PILOT));
    }

    protected BoothVariant(String name) {
        this(name, AITMod.MOD_ID);
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
