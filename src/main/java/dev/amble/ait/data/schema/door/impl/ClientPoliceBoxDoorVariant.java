package dev.amble.ait.data.schema.door.impl;

import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.client.models.doors.PoliceBoxDoorModel;
import dev.amble.ait.data.schema.door.ClientDoorSchema;

public class ClientPoliceBoxDoorVariant extends ClientDoorSchema {
    public ClientPoliceBoxDoorVariant() {
        super(PoliceBoxDoorVariant.REFERENCE);
    }

    @Override
    public DoorModel model() {
        return new PoliceBoxDoorModel(PoliceBoxDoorModel.getTexturedModelData().createModel());
    }
}
