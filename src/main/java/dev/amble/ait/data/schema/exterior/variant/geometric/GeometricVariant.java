package dev.amble.ait.data.schema.exterior.variant.geometric;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.GeometricDoorVariant;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.category.GeometricCategory;
import dev.amble.ait.registry.impl.door.DoorRegistry;
import org.jetbrains.annotations.Nullable;

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
    public DoorSchema door() {
        return DoorRegistry.getInstance().get(GeometricDoorVariant.REFERENCE);
    }

    @Override
    public boolean hasPortals() {
        return true;
    }

    @Override
    public @Nullable Vec3d getPortalPosition() {
        return new Vec3d(0, -0.07, -0.025);
    }

    @Override
    public Vec3d seatTranslations() {
        return new Vec3d(0.5, 1, 0.5);
    }

    @Override
    public double portalHeight() {
        return 2.38d;
    }
}
