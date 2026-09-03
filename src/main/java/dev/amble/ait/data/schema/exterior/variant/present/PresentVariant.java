package dev.amble.ait.data.schema.exterior.variant.present;

import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.PresentDoorVariant;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.category.PresentCategory;
import dev.amble.ait.registry.impl.door.DoorRegistry;

public abstract class PresentVariant extends ExteriorVariantSchema {
    protected static final String TEXTURE_PATH = "textures/blockentities/exteriors/present/present_";

    protected PresentVariant(String name) {
        super(PresentCategory.REFERENCE, AITMod.id("exterior/present/" + name),
                new Loyalty(Loyalty.Type.NEUTRAL));
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.getInstance().get(PresentDoorVariant.REFERENCE);
    }

    @Override
    public boolean hasPortals() {
        return false;
    }

    @Override
    public Vec3d seatTranslations() {
        return new Vec3d(0.5, 1, 0.5);
    }
}
