package dev.amble.ait.data.schema.exterior.variant.jake;

import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
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
    public DoorSchema door() {
        return DoorRegistry.getInstance().get(JakeDoorVariant.REFERENCE);
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
