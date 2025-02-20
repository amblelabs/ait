package dev.amble.ait.data.schema.door.impl;

import dev.amble.ait.client.models.doors.DalekModDoorModel;
import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.data.schema.door.ClientDoorSchema;

public class ClientDalekModDoorVariant extends ClientDoorSchema {
    public ClientDalekModDoorVariant() {
        super(DalekModDoorVariant.REFERENCE);
    }

    @Override
    public DoorModel model() {
        return new DalekModDoorModel(DalekModDoorModel.getTexturedModelData().createModel());
    }
}
