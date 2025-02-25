package dev.amble.ait.data.schema.door.impl;

import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.client.models.doors.PresentDoorModel;
import dev.amble.ait.data.schema.door.ClientDoorSchema;

public class ClientPresentDoorVariant extends ClientDoorSchema {
    public ClientPresentDoorVariant() {
        super(PresentDoorVariant.REFERENCE);
    }

    @Override
    public DoorModel model() {
        return new PresentDoorModel(PresentDoorModel.getTexturedModelData().createModel());
    }
}
