package dev.amble.ait.data.schema.door.impl;

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.door.DoorSchema;

public class ClassicDoorVariant extends DoorSchema {
    public static final Identifier REFERENCE = AITMod.id("door/classic");

    public ClassicDoorVariant() {
        super(REFERENCE);
    }

    @Override
    public boolean isDouble() {
        return true;
    }

    @Override
    public @Nullable Vec3d getPortalPosition() {
        return new Vec3d(0, 0, -0.47);
    }
}
