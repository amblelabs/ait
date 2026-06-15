package dev.amble.ait.data.schema.door.impl;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.door.DoorSchema;

public class TardimDoorVariant extends DoorSchema {
    public static final Identifier REFERENCE = AITMod.id("door/tardim");

    public TardimDoorVariant() {
        super(REFERENCE);
    }

    @Override
    public boolean isDouble() {
        return true;
    }


    @Override
    public Vec3d adjustPortalPos(Vec3d pos, Direction direction) {
        return pos.add(0, -0.08, -0.3);
    }
}
