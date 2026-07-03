package dev.amble.ait.data.schema.exterior.variant.booth;

import org.jetbrains.annotations.Nullable;

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
    public @Nullable Vec3d getPortalPosition() {
        return new Vec3d(0, 0.125, -0.48f);
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
        return 0.875d;
    }

    @Override
    public double portalHeight() {
        return 2.125d;
    }
}
