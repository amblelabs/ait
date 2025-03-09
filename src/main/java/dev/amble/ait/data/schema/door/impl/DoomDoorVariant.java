package dev.amble.ait.data.schema.door.impl;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.data.schema.door.DoorSchema;

public class DoomDoorVariant extends DoorSchema {

    public static final Identifier REFERENCE = AITMod.id("door/doom");

    public DoomDoorVariant() {
        super(REFERENCE);
    }

    @Override
    public boolean isDouble() {
        return false;
    }

    @Override
    public SoundEvent openSound() {
        return AITSounds.DOOM_DOOR_OPEN;
    }

    @Override
    public SoundEvent closeSound() {
        return AITSounds.DOOM_DOOR_CLOSE;
    }
}
