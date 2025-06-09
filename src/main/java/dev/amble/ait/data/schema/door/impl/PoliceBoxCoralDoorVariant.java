package dev.amble.ait.data.schema.door.impl;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.door.DoorSchema;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class PoliceBoxCoralDoorVariant extends DoorSchema {
    public static final Identifier REFERENCE = AITMod.id("door/police_box/coral");

    public PoliceBoxCoralDoorVariant() {
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
            case NORTH -> pos.add(0, 0.075, -0.4);
            case SOUTH -> pos.add(0, 0.075, 0.4);
            case WEST -> pos.add(-0.4, 0.075, 0);
            case EAST -> pos.add(0.4, 0.075, 0);
        };
    }
}
