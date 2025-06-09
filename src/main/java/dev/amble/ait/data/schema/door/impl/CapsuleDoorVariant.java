package dev.amble.ait.data.schema.door.impl;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.door.DoorSchema;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class CapsuleDoorVariant extends DoorSchema {
    public static final Identifier REFERENCE = AITMod.id("door/capsule");

    public CapsuleDoorVariant() {
        super(REFERENCE);
    }

    @Override
    public boolean isDouble() {
        return true;
    }

    @Override
    public SoundEvent openSound() {
        return SoundEvents.BLOCK_IRON_DOOR_OPEN;
    }

    @Override
    public SoundEvent closeSound() {
        return SoundEvents.BLOCK_IRON_DOOR_CLOSE;
    }

    @Override
    public Vec3d adjustPortalPos(Vec3d pos, Direction direction) {
        return switch (direction) {
            case DOWN, UP -> pos;
            case NORTH -> pos.add(0, 0.04, -0.3);
            case SOUTH -> pos.add(0, 0.04, 0.3);
            case WEST -> pos.add(-0, 0.04, -0.3);
            case EAST -> pos.add(0.0, 0.04, -0.3);
        };

    }
}
