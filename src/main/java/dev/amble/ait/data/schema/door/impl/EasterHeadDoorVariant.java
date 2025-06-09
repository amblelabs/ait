package dev.amble.ait.data.schema.door.impl;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.door.DoorSchema;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class EasterHeadDoorVariant extends DoorSchema {

    public static final Identifier REFERENCE = AITMod.id("door/easter_head");

    public EasterHeadDoorVariant() {
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
