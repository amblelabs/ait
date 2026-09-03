package dev.amble.ait.data.schema.door.impl;

import org.jetbrains.annotations.Nullable;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.door.DoorSchema;

public class AdaptiveDoorVariant extends DoorSchema {
    public static final Identifier REFERENCE = AITMod.id("door/adaptive");

    public AdaptiveDoorVariant() {
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
    public @Nullable Vec3d getPortalPosition() {
        return new Vec3d(0, 0.125, -0.45);
    }
}
