package dev.amble.ait.data.schema.exterior.variant.tardim;

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.TardimDoorVariant;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.category.TardimCategory;
import dev.amble.ait.registry.impl.door.DoorRegistry;

// a useful class for creating tardim variants as they all have the same filepath you know
public abstract class TardimVariant extends ExteriorVariantSchema {
    protected static final String TEXTURE_PATH = "textures/blockentities/exteriors/tardim/tardim_";

    protected TardimVariant(String name) {
        super(TardimCategory.REFERENCE, AITMod.id("exterior/tardim/" + name),
                new Loyalty(Loyalty.Type.OWNER));
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.getInstance().get(TardimDoorVariant.REFERENCE);
    }

    @Override
    public boolean hasPortals() {
        return true;
    }

    @Override
    public @Nullable Vec3d getPortalPosition() {
        return new Vec3d(0, 0, -0.2899f);
    }

    @Override
    public Vec3d seatTranslations() {
        return new Vec3d(0.5, 1, 0.5);
    }
}
