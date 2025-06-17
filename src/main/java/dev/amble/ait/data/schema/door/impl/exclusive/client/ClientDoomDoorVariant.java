package dev.amble.ait.data.schema.door.impl.exclusive.client;

import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.client.models.doors.exclusive.DoomDoorModel;
import dev.amble.ait.data.schema.door.ClientDoorSchema;
import dev.amble.ait.data.schema.door.impl.exclusive.DoomDoorVariant;

public class ClientDoomDoorVariant extends ClientDoorSchema {
    public ClientDoomDoorVariant() {
        super(DoomDoorVariant.REFERENCE);
    }

    @Override
    public DoorModel model() {
        return new DoomDoorModel(DoomDoorModel.getTexturedModelData().createModel());
    }
}
