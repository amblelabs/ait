package dev.amble.ait.data.schema.door.impl.exclusive;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.door.DoorSchema;

public class BlueBoxDoorVariant extends DoorSchema {
    public static final Identifier REFERENCE = AITMod.id("door/blue_box");

    public BlueBoxDoorVariant() {
        super(REFERENCE);
    }

    @Override
    public boolean isDouble() {
        return true;
    }

    @Override
    public Vec3d adjustPortalPos(Vec3d pos, Direction direction) {
        return switch (direction) {
            case DOWN, UP -> pos;
            case NORTH -> pos.add(0, 0.125f, -0.48f);
            case SOUTH -> pos.add(0, 0.125f, 0.48f);
            case WEST -> pos.add(-0.48f, 0.125f, 0);
            case EAST -> pos.add(0.48f, 0.125f, 0);
        };
    }
}
