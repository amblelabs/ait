package dev.amble.ait.data.schema.exterior.variant.renegade;

import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.RenegadeDoorVariant;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.category.RenegadeCategory;
import dev.amble.ait.registry.impl.door.DoorRegistry;
import org.jetbrains.annotations.Nullable;

// a useful class for creating tardim variants as they all have the same filepath you know
public abstract class RenegadeVariant extends ExteriorVariantSchema {
    protected static final String TEXTURE_PATH = "textures/blockentities/exteriors/renegade/renegade_";
    protected RenegadeVariant(String name) {
        super(RenegadeCategory.REFERENCE, AITMod.id("exterior/renegade/" + name),
                new Loyalty(Loyalty.Type.PILOT));
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.getInstance().get(RenegadeDoorVariant.REFERENCE);
    }

    @Override
    public boolean hasPortals() {
        return true;
    }

    @Override
    public @Nullable Vec3d getPortalPosition() {
        return new Vec3d(0, 0.125, -0.4);
    }

    @Override
    public double portalHeight() {
        return 2.4d;
    }

    @Override
    public Vec3d seatTranslations() {
        return new Vec3d(0.5, 1, 0.5);
    }
}
