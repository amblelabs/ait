package dev.amble.ait.data.schema.door.impl;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.door.DoorSchema;
import org.jetbrains.annotations.Nullable;

public class CoralGrowthDoorVariant extends DoorSchema {

    public static final Identifier REFERENCE = AITMod.id("door/coral_growth");

    public CoralGrowthDoorVariant() {
        super(REFERENCE);
    }

    @Override
    public boolean isDouble() {
        return true;
    }

    @Override
    public SoundEvent openSound() {
        return SoundEvents.BLOCK_BEEHIVE_SHEAR;
    }

    @Override
    public SoundEvent closeSound() {
        return SoundEvents.BLOCK_BEEHIVE_SHEAR;
    }

    @Override
    public @Nullable Vec3d getPortalPosition() {
        return new Vec3d(0, 0, 0.1);
    }
}
