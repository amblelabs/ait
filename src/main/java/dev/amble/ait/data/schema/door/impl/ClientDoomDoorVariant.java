package dev.amble.ait.data.schema.door.impl;

import dev.amble.ait.client.models.doors.DoomDoorModel;
import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.data.schema.door.ClientDoorSchema;

public class ClientDoomDoorVariant extends ClientDoorSchema {
    public ClientDoomDoorVariant() {
        super(DoomDoorVariant.REFERENCE);
    }

    @Override
    public DoorModel model() {
        return new DoomDoorModel(DoomDoorModel.getTexturedModelData().createModel());
    }
}
