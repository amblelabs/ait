package dev.amble.ait.data.schema.exterior.variant.classic;

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.Vec3d;

import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.ClassicHudolinDoorVariant;
import dev.amble.ait.registry.impl.door.DoorRegistry;

public class ClassicBoxHudolinVariant extends ClassicBoxVariant {
    public ClassicBoxHudolinVariant() {
        super("hudolin");
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.getInstance().get(ClassicHudolinDoorVariant.REFERENCE);
    }

    @Override
    public @Nullable Vec3d getPortalPosition() {
        return new Vec3d(0, 0, -0.599);
    }
}
