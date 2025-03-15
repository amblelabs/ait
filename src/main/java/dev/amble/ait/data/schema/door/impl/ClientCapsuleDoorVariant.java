package dev.amble.ait.data.schema.door.impl;

import dev.amble.ait.client.models.doors.CapsuleDoorModel;
import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.data.schema.door.ClientDoorSchema;

public class ClientCapsuleDoorVariant extends ClientDoorSchema {
    public ClientCapsuleDoorVariant() {
        super(CapsuleDoorVariant.REFERENCE);
    }

    @Override
    public DoorModel model() {
        return new CapsuleDoorModel(CapsuleDoorModel.getTexturedModelData().createModel());
    }
}
