package dev.amble.ait.data.schema.door.impl;

import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.client.models.doors.PlinthDoorModel;
import dev.amble.ait.data.schema.door.ClientDoorSchema;

public class ClientPlinthDoorVariant extends ClientDoorSchema {
    public ClientPlinthDoorVariant() {
        super(PlinthDoorVariant.REFERENCE);
    }

    @Override
    public DoorModel model() {
        return new PlinthDoorModel(PlinthDoorModel.getTexturedModelData().createModel());
    }
}
