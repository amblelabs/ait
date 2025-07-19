package dev.amble.ait.data.schema.exterior.variant.exclusive.doom;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.animation.ExteriorAnimation;
import dev.amble.ait.core.tardis.animation.PulsatingAnimation;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.exclusive.DoomDoorVariant;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.category.ExclusiveCategory;
import dev.amble.ait.registry.impl.door.DoorRegistry;

public class DoomVariant extends ExteriorVariantSchema {
    public static final Identifier REFERENCE = AITMod.id("exterior/exclusive/doom");

    public DoomVariant() {
        super(ExclusiveCategory.REFERENCE, REFERENCE);
    }

    @Override
    public ExteriorAnimation animation(ExteriorBlockEntity exterior) {
        return new PulsatingAnimation(exterior);
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.REGISTRY.get(DoomDoorVariant.REFERENCE);
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
