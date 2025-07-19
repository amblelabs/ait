package dev.amble.ait.data.schema.door.impl;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.door.DoorSchema;

public class PipeDoorVariant extends DoorSchema {
    public static final Identifier REFERENCE = AITMod.id("door/pipe");

    public PipeDoorVariant() {
        super(REFERENCE);
    }

    @Override
    public boolean isDouble() {
        return false;
    }

    @Override
    public SoundEvent openSound() {
        return SoundEvents.BLOCK_NOTE_BLOCK_BIT.value();
    }

    @Override
    public SoundEvent closeSound() {
        return SoundEvents.BLOCK_NOTE_BLOCK_BIT.value();
    }

    @Override
    public Vec3d adjustPortalPos(Vec3d pos, Direction direction) {
        return switch (direction) {
            case DOWN, UP -> pos;
            case NORTH -> pos.add(0, 0.1, -0.4);
            case SOUTH -> pos.add(0, 0.1, 0.4);
            case WEST -> pos.add(-0.4, 0.1, 0);
            case EAST -> pos.add(0.4, 0.1, 0);
        };
    }
}
