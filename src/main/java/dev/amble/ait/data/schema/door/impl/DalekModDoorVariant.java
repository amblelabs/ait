package dev.amble.ait.data.schema.door.impl;

import net.minecraft.util.Identifier;

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
}
