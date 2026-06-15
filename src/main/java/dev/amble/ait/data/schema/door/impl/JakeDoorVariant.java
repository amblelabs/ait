package dev.amble.ait.data.schema.door.impl;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.door.DoorSchema;

public class JakeDoorVariant extends DoorSchema {
    public static final Identifier REFERENCE = AITMod.id("door/jake");

    public JakeDoorVariant() {
        super(REFERENCE);
    }

    @Override
    public boolean isDouble() {
        return false;
    }

    @Override
    public SoundEvent openSound() {
        return SoundEvents.BLOCK_GRINDSTONE_USE;
    }

    @Override
    public SoundEvent closeSound() {
        return SoundEvents.BLOCK_GRINDSTONE_USE;
    }

}
