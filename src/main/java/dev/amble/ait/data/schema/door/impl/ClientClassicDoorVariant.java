package dev.amble.ait.data.schema.door.impl;

import dev.amble.ait.client.models.doors.ClassicDoorModel;
import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.data.schema.door.ClientDoorSchema;

public class ClientClassicDoorVariant extends ClientDoorSchema {
    public ClientClassicDoorVariant() {
        super(ClassicDoorVariant.REFERENCE);
    }

    @Override
    public DoorModel model() {
        return new ClassicDoorModel(ClassicDoorModel.getTexturedModelData().createModel());
    }
}
