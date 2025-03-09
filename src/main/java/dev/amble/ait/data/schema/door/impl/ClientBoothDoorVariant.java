package dev.amble.ait.data.schema.door.impl;

import dev.amble.ait.client.models.doors.BoothDoorModel;
import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.data.schema.door.ClientDoorSchema;

public class ClientBoothDoorVariant extends ClientDoorSchema {
    public ClientBoothDoorVariant() {
        super(BoothDoorVariant.REFERENCE);
    }

    @Override
    public DoorModel model() {
        return new BoothDoorModel(BoothDoorModel.getTexturedModelData().createModel());
    }
}
