package dev.amble.ait.data.schema.door.impl;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.door.DoorSchema;

public class DalekModDoorVariant extends DoorSchema {
    public static final Identifier REFERENCE = AITMod.id("door/dalek_mod");

    public DalekModDoorVariant() {
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
            case NORTH -> pos.add(0, 0.04, -0.5);
            case SOUTH -> pos.add(0, 0.04, 0.5);
            case WEST -> pos.add(-0, 0.04, -0.5);
            case EAST -> pos.add(0.0, 0.04, -0.5);
        };
    }
}
