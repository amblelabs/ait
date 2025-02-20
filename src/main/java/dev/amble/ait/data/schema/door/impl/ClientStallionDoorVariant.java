package dev.amble.ait.data.schema.door.impl;

import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.client.models.doors.StallionDoorModel;
import dev.amble.ait.data.schema.door.ClientDoorSchema;

public class ClientStallionDoorVariant extends ClientDoorSchema {
    public ClientStallionDoorVariant() {
        super(StallionDoorVariant.REFERENCE);
    }

    @Override
    public DoorModel model() {
        return new StallionDoorModel(StallionDoorModel.getTexturedModelData().createModel());
    }
}
