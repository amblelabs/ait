package dev.amble.ait.data.schema.door.impl;

import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.client.models.doors.PoliceBoxCoralDoorModel;
import dev.amble.ait.data.schema.door.ClientDoorSchema;

public class ClientPoliceBoxCoralDoorVariant extends ClientDoorSchema {
    public ClientPoliceBoxCoralDoorVariant() {
        super(PoliceBoxCoralDoorVariant.REFERENCE);
    }

    @Override
    public DoorModel model() {
        return new PoliceBoxCoralDoorModel(PoliceBoxCoralDoorModel.getTexturedModelData().createModel());
    }
}
