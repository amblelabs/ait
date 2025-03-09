package dev.amble.ait.data.schema.door.impl;

import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.client.models.doors.RenegadeDoorModel;
import dev.amble.ait.data.schema.door.ClientDoorSchema;

public class ClientRenegadeDoorVariant extends ClientDoorSchema {
    public ClientRenegadeDoorVariant() {
        super(RenegadeDoorVariant.REFERENCE);
    }

    @Override
    public DoorModel model() {
        return new RenegadeDoorModel(RenegadeDoorModel.getTexturedModelData().createModel());
    }
}
