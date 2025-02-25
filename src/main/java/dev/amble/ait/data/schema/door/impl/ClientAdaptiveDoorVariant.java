package dev.amble.ait.data.schema.door.impl;

import dev.amble.ait.client.models.doors.CapsuleDoorModel;
import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.data.schema.door.ClientDoorSchema;

public class ClientAdaptiveDoorVariant extends ClientDoorSchema {
    public ClientAdaptiveDoorVariant() {
        super(AdaptiveDoorVariant.REFERENCE);
    }

    @Override
    public DoorModel model() {
        return new CapsuleDoorModel(CapsuleDoorModel.getTexturedModelData().createModel());
    }
}
