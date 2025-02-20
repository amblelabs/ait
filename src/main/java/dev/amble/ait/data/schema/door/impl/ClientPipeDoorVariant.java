package dev.amble.ait.data.schema.door.impl;

import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.client.models.doors.PipeDoorModel;
import dev.amble.ait.data.schema.door.ClientDoorSchema;

public class ClientPipeDoorVariant extends ClientDoorSchema {
    public ClientPipeDoorVariant() {
        super(PipeDoorVariant.REFERENCE);
    }

    @Override
    public DoorModel model() {
        return new PipeDoorModel(PipeDoorModel.getTexturedModelData().createModel());
    }
}
