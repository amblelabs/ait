package dev.amble.ait.data.schema.exterior.variant.adaptive;

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.AdaptiveDoorVariant;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.category.AdaptiveCategory;
import dev.amble.ait.registry.impl.door.DoorRegistry;

public class AdaptiveVariant extends ExteriorVariantSchema {

    public AdaptiveVariant() {
        super(AdaptiveCategory.REFERENCE, AITMod.id("exterior/adaptive"));
    }

    @Override
    public Vec3d seatTranslations() {
        return new Vec3d(0.5, 1, 0.5);
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.getInstance().get(AdaptiveDoorVariant.REFERENCE);
    }

    @Override
    public boolean hasPortals() {
        return true;
    }

    @Override
    public @Nullable Vec3d getPortalPosition() {
        return new Vec3d(0, 0.125, -0.5);
    }

    @Override
    public double portalWidth() {
        return 0.75d;
    }
}
