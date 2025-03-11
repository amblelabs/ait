package dev.amble.ait.data.schema.door.impl;

import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.client.models.doors.TardimDoorModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.door.DoorSchema;

public class TardimDoorVariant extends DoorSchema {
    public static final Identifier REFERENCE = AITMod.id("door/tardim");

    public TardimDoorVariant() {
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
            case NORTH -> pos.add(0, 0.175f, -0.499f);
            case SOUTH -> pos.add(0, 0, 0.499f);
            case WEST -> pos.add(-0.499f, 0, 0);
            case EAST -> pos.add(0.499f, 0, 0);
        };
    }

    @Override
    public DoorModel model() {
        return new TardimDoorModel(TardimDoorModel.getTexturedModelData().createModel());
    }
}
